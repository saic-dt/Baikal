/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.datatech.baikal.mongosync.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.data.Stat;
import org.bson.BsonTimestamp;
import org.datatech.baikal.mongosync.Config;
import org.datatech.baikal.mongosync.bean.TimestampBean;
import org.datatech.baikal.mongosync.util.HadoopUtils;
import org.datatech.baikal.mongosync.util.SpringUtil;
import org.datatech.baikal.mongosync.util.ZkHandler;
import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Provide functions to write MongoDB oplog to HDFS.
 */
@Component
public class Log2HdfsStream {

    private static final Logger logger = LoggerFactory.getLogger(Log2HdfsStream.class);
    private final String ts = "ts";
    private final String regex = "___";
    private final byte[] lineByte = "\n".getBytes();
    private FileSystem fs;
    private Map<String, ByteArrayOutputStream> dataBufferMap;
    private Map<String, LinkedBlockingQueue<Pair<byte[], Integer>>> queueMap;
    private Map<String, String> archiveDirMap;
    private String archiveDirFormat;
    private String incremental = "incremental/";
    private AtomicLong bufferSize = new AtomicLong(0);

    private ScheduledExecutorService scheduledThreadPool;

    private String cliKey;

    public Log2HdfsStream() {
    }

    /**
     * init Log2Hdfs modules  start a scheduledThreadPool to write hdfs
     *
     * @param archiveDirFormat archive dir's format String
     * @param cliKey           cliKey
     * @throws IOException     IOException
     */
    public Log2HdfsStream(final String archiveDirFormat, final String cliKey) throws IOException {
        Configuration conf = HadoopUtils.getConf();
        fs = FileSystem.get(conf);
        dataBufferMap = new ConcurrentHashMap<>(30);
        queueMap = new ConcurrentHashMap<>(100);
        archiveDirMap = new ConcurrentHashMap<>(30);
        this.archiveDirFormat = archiveDirFormat;
        this.cliKey = cliKey;
        exe();
    }

    public void set(Pair<byte[], BsonTimestamp> pair, final String[] formatData) {
        final String key = String.join(regex, formatData);
        LinkedBlockingQueue queue = queueMap.get(key);
        if (StringUtil.isNull(queue)) {
            queue = new LinkedBlockingQueue();
            archiveDirMap.put(key, String.format(archiveDirFormat, formatData));
        }
        queue.add(pair);
        queueMap.put(key, queue);
        //log.info(new String(pair.getLeft())+" size:"+queueMap.get(key).size());
    }

    /**
     * add oplog in hdfs
     */
    private void log2hdfs() {
        queueMap.forEach((key, value) -> {
            if (value.size() > 0) {
                write2Hdfs(key);
            }
        });

    }

    private void write2Hdfs(final String key) {
        final String archiveDir = archiveDirMap.get(key);
        final String incrementalDir = archiveDir + incremental;
        LinkedBlockingQueue queue = queueMap.get(key);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Integer timeT = 0;
        Integer inc = 0;
        for (int i = 0; i < Constants.BATCH_SAVE_HDFS_SIZE; i++) {
            Object obj = queue.poll();
            if (StringUtil.isNotEmpty(obj)) {
                Pair<byte[], BsonTimestamp> pair = (Pair<byte[], BsonTimestamp>) obj;
                try {
                    timeT = pair.getRight().getTime();
                    inc = pair.getRight().getInc();
                    outputStream.write(pair.getLeft());
                    outputStream.write(lineByte);
                } catch (IOException e) {
                    logger.error("Exception occurred.", e);
                }
            } else {
                break;
            }
        }
        if (outputStream.size() == 0) {
            return;
        }

        byte[] data = outputStream.toByteArray();

        bufferSize.addAndGet(-outputStream.size());
        outputStream.reset();
        outputStream = null;

        final long time = System.currentTimeMillis();
        Path incrementalPath = new Path(incrementalDir);
        Path archivePath = new Path(archiveDir);
        boolean createFlg = false;
        boolean flg = false;
        try {
            String maxTs = null;
            if (fs.exists(incrementalPath)) {
                // get maximum timestamp from incremental path
                maxTs = getMaxTs(incrementalPath);
            } else {
                // create incremental path
                fs.mkdirs(incrementalPath);
            }
            // create incremental file when maxTs is null, the timestamp should be larger than archive path
            if (maxTs == null) {
                createFlg = true;
                maxTs = getMaxTs(archivePath);
            }

            if (maxTs == null) {
                // create incremental file when no archive file exists
                flg = createFile(incrementalDir, time, data);
            } else if (createFlg) {
                long mts = Long.valueOf(maxTs);
                long t = time > mts ? time + 1 : mts;
                flg = createFile(incrementalDir, t, data);
            } else {
                flg = appendFile(incrementalDir, maxTs, data, time);
            }
            if (!flg) {
                reTry(data, key);
            }
            data = null;

            // record written timestamp to ZK
            writeZkTime(key, timeT, inc);
        } catch (IOException e) {
            logger.error("Exception occurred.", e);
            reTry(data, key);
        }

    }

    private void writeZkTime(String key, Integer timeT, Integer inc) {
        try {
            // the key format is datalake___local___test___CAECDBFDAEFCFC___0aaaaaaaa
            String node = key.substring(0, key.lastIndexOf(Config.UNDERLINE_3));
            String path = Config.PATH_SYNC + "/" + node;
            TimestampBean timestampBean = new TimestampBean();
            timestampBean.setOrdinal(inc);
            timestampBean.setTime_t(timeT);
            ZkHandler zkHandler = SpringUtil.getBean(ZkHandler.class);
            Stat stat = zkHandler.getClient().checkExists().forPath(path);
            if (stat != null) {
                zkHandler.getClient().setData().forPath(path,
                        JsonUtil.getJsonString4JavaPOJO(timestampBean).getBytes());
            }
        } catch (Exception e1) {
            logger.error("Exception occurred.", e1);
        }
    }

    private synchronized void reTry(byte[] redata, String key) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(redata.length);
            ByteArrayOutputStream newdata = dataBufferMap.get(key);
            output.write(redata);
            output.write(newdata.toByteArray());
            dataBufferMap.put(key, output);
            bufferSize.addAndGet(redata.length + newdata.size());
            newdata = null;
            redata = null;
        } catch (IOException e) {
            logger.error("Exception occurred.", e);
        }
    }

    private boolean appendFile(final String incrementalDir, final String maxTs, byte[] data, long ltime)
            throws IOException {
        Path path = new Path(incrementalDir + ts + maxTs);
        final long rsize = fs.getContentSummary(path).getLength();
        if (rsize + data.length < Constants.HADOOP_BLOCK_SIZE) {
            FSDataOutputStream out = null;
            try {
                out = fs.append(path);
                out.write(data);
                return true;
            } catch (Exception e) {
                logger.error("Exception occurred.", e);
                return false;
            } finally {
                closeFSDataOutputStream(out);
            }
        } else {
            long rtime = Long.valueOf(maxTs);
            long times = rtime >= ltime ? rtime + 1 : ltime;
            return createFile(incrementalDir, times, data);
        }
    }

    private boolean createFile(final String incrementalDir, long maxTs, byte[] data) throws IOException {
        FSDataOutputStream out = null;
        try {
            out = fs.create(new Path(incrementalDir + ts + maxTs));
            out.write(data);
            return true;
        } catch (Exception e) {
            logger.error("Exception occurred.", e);
            return false;
        } finally {
            closeFSDataOutputStream(out);
        }

    }

    private void closeFSDataOutputStream(FSDataOutputStream out) throws IOException {
        if (out != null) {
            out.flush();
            out.close();
        }
    }

    private String getMaxTs(Path path) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(path);
        String maxTs = null;
        int len = fileStatuses.length;
        if (len == 0) {
            return maxTs;
        }
        for (int i = len - 1; len > -1; --len) {
            FileStatus status = fileStatuses[i];
            final String fileName = status.getPath().getName();
            if (Pattens.tsPattern.matcher(fileName).matches()) {
                maxTs = Pattens.numberPattern.matcher(fileName).replaceAll("");
                break;
            }
        }
        return maxTs;
    }

    private void exe() {
        scheduledThreadPool = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPool.scheduleAtFixedRate(() -> log2hdfs(), 1, 1, TimeUnit.SECONDS);
    }

    public void close() {
        scheduledThreadPool.shutdown();
        clear();
    }

    private void clear() {
        if (dataBufferMap != null) {
            dataBufferMap.clear();
            archiveDirMap.clear();
        }
        dataBufferMap = null;
        archiveDirMap = null;
    }

}
