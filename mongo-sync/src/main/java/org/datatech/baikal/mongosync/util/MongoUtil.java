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

package org.datatech.baikal.mongosync.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;
import org.bson.BsonBinary;
import org.bson.BsonJavaScript;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.datatech.baikal.mongosync.bean.TimestampBean;
import org.datatech.baikal.mongosync.common.Constants;
import org.datatech.baikal.mongosync.common.Enums;
import org.datatech.baikal.mongosync.common.Executor;
import org.datatech.baikal.mongosync.common.Filter;
import org.datatech.baikal.mongosync.common.Log2HdfsStream;
import org.datatech.baikal.mongosync.common.PrefixGet;
import org.datatech.baikal.mongosync.common.ReflectionEntity;
import org.datatech.baikal.mongosync.json.Json;
import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

/**
 * Utility class providing methods to
 */
public class MongoUtil implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(MongoUtil.class);
    private final String collectionName = Constants.OPLOG_COLLECTION_NAME;
    private final String dataBaseName = "local";
    private final String regex = "___";
    private final String hdfsArchiveDirFormat = "/datalake/%s/%s/%s/%s/%s/";
    private final SimpleDateFormat db2DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private MongoClient client;
    private String clikey;
    private BsonTimestamp timestamp;

    /**
     * connect mongo with uri
     *
     * @param uri    mongo uri
     * @param clikey cli tenant___instance
     */
    public MongoUtil(String uri, final String clikey) {
        final CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientURI clientURI = new MongoClientURI(uri, MongoClientOptions.builder().codecRegistry(codecRegistry));
        client = new MongoClient(clientURI);
        this.clikey = clikey;
        timestamp = getBsonTimestamp(clikey);
    }

    /**
     * Get timestamp for sync
     * @param clikey tenant___instance
     * @return BsonTimestamp BsonTimestamp close to current timestamp
     */
    private BsonTimestamp getBsonTimestamp(String clikey) {
        Map<String, Map<String, TimestampBean>> minTimeMap = Filter.getMinTimeMap();
        BsonTimestamp bsonTimestamp = null;
        if (StringUtil.isNull(minTimeMap.get(clikey))) {
            bsonTimestamp = new BsonTimestamp((int) (System.currentTimeMillis() / 1000), 1);
        } else {
            long minTime = System.currentTimeMillis() / 100;
            for (Map.Entry<String, TimestampBean> entry : minTimeMap.get(clikey).entrySet()) {
                if (minTime > entry.getValue().getTime_t()) {
                    minTime = entry.getValue().getTime_t();
                }
            }
            bsonTimestamp = new BsonTimestamp((int) minTime, 1);
        }

        return bsonTimestamp;
    }

    private Stream<Document> tailOpLog(final BsonTimestamp timestamp) {
        logger.debug("dataBaseName:" + dataBaseName + " collectionName:" + collectionName + " timestamp:"
                + timestamp.getTime());
        MongoCollection<Document> collection = client.getDatabase(dataBaseName).getCollection(collectionName);
        FindIterable<Document> logs = collection
                .find(Filters.and(Filters.gt("ts", timestamp),
                        Filters.in("op", Enums.OpType.INSERT.value(), Enums.OpType.UPDATE.value(),
                                Enums.OpType.DELETE.value())))
                .cursorType(CursorType.TailableAwait).noCursorTimeout(true);
        MongoCursor<Document> mongoCursor = logs.iterator();
        logger.info("create " + clikey + " stream");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(mongoCursor, Spliterator.ORDERED), false);
    }

    private void tail(final BsonTimestamp timestamp, Log2HdfsStream log2Hdfs) {
        Stream<Document> steam = tailOpLog(timestamp);
        logger.info("tail " + clikey + " stream ing...");
        steam.forEach(src -> {
            logger.info("src:" + src.toString());
            // ns is schema.table
            final String ns = src.getString("ns");
            final Integer timeT = ((BsonTimestamp) src.get("ts")).getTime();
            final Integer inc = ((BsonTimestamp) src.get("ts")).getInc();
            if (Filter.isFilter(clikey, ns) && ((timeT.equals(Filter.getMinTimeMap().get(clikey).get(ns).getTime_t())
                    && Filter.getMinTimeMap().get(clikey).get(ns).getOrdinal() < inc)
                    || (Filter.getMinTimeMap().get(clikey).get(ns).getTime_t() < timeT))) {
                BsonTimestamp bsonTimestamp = ((BsonTimestamp) src.get("ts"));

                String dataf = getSampleJson(src, ns);
                byte[] bytes = Base64.getEncoder().encode(dataf.getBytes(Charset.forName("iso-8859-1")));

                // clikey is tenant___instance, key is tenant___instance___schema___table
                final String key = clikey + regex + ns.replaceFirst("\\.", regex);
                String[] formatData = PrefixGet.getPrefix4Cache(key);
                log2Hdfs.set(Pair.of(bytes, bsonTimestamp), formatData);
            }
        });
    }

    /**
     * close the mongo client
     */
    public void close() {
        client.close();
    }

    /**
     * get object from Document object
     *
     * @param key Documen 's key use point division
     * @param ob  Documen object
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object getObject(String key, Object ob) throws InvocationTargetException, IllegalAccessException {
        String[] keys = key.split("\\.");
        Object rtob = null;
        for (int i = 0; i < keys.length; ++i) {
            if (i == 0) {
                rtob = ReflectionEntity.DOCUMENT_GET_METHOD.invoke(ob, keys[i]);
            } else if (rtob != null) {
                rtob = ReflectionEntity.DOCUMENT_GET_METHOD.invoke(rtob, keys[i]);
            } else {
                break;
            }
        }
        return rtob;
    }

    @Override
    public void run() {
        // store thread to map to manage it later
        Executor.FIXED_THREAD_POOL_THREADS.put(clikey, Thread.currentThread());
        Log2HdfsStream log2Hdfs = null;
        try {
            logger.info("start tail mongodb " + clikey);
            log2Hdfs = new Log2HdfsStream(hdfsArchiveDirFormat, clikey);
            logger.info("create oplog to mongo " + clikey + " model start!");
            tail(timestamp, log2Hdfs);
        } catch (IOException e) {
            logger.error("Exception occurred.", e);
        } finally {
            logger.info("out !");
            log2Hdfs.close();
            close();
            // remote thread from map
            Executor.FIXED_THREAD_POOL_THREADS.remove(clikey);
        }
    }

    private String getSampleJson(Document src, final String ns) {
        String op = src.getString("op");
        Map<String, Object> dt = new HashMap<>(src.size());
        dt.put("o", src.get("op"));
        try {
            if (Enums.OpType.UPDATE.value().equals(op)) {
                Document doc = (Document) src.get("o2");
                ObjectId objectId = doc.getObjectId("_id");
                dt.put("before", encryption(doc, ns));
                dt.put("after", encryption(updataValueExtract(src.get("o"), ns, objectId), ns));
            } else if (Enums.OpType.DELETE.value().equals(op)) {
                dt.put("before", encryption(src.get("o"), ns));
            } else {
                dt.put("after", encryption(src.get("o"), ns));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("Exception occurred.", e);
        }
        op = null;
        return Json.jsonUtil.object2JsonString(dt);
    }

    private Object updataValueExtract(Object o, String ns, ObjectId objectId)
            throws InvocationTargetException, IllegalAccessException {
        Object ob = ReflectionEntity.DOCUMENT_GET_METHOD.invoke(o, "$set");
        if (ob == null) {
            ob = o;
        } else {
            Document bsonData = (Document) ob;
            for (String key : bsonData.keySet()) {
                if (key.indexOf(".") <= 0) {
                    return ob;
                }
                String k = key.substring(0, key.indexOf("."));
                BasicDBObject query = new BasicDBObject();
                query.put("_id", objectId);
                String[] arr = ns.split("\\.");
                FindIterable iterable = client.getDatabase(arr[0]).getCollection(arr[1]).find(query);
                MongoCursor<Document> mongoCursor = iterable.iterator();
                while (mongoCursor.hasNext()) {
                    Document value = mongoCursor.next();
                    ob = new Document(k, value.get(k));
                }
                mongoCursor.close();
                arr = null;
                k = null;
                break;
            }
        }
        return ob;
    }

    private Object encryption(Object data, String ns) {

        if (data instanceof Document) {
            Document bsonData = (Document) data;
            for (String k : bsonData.keySet()) {
                String sdt;
                Object tdt = bsonData.get(k);
                if (tdt == null) {
                    tdt = "null";
                }
                if (tdt instanceof BsonValue) {
                    if (((BsonValue) tdt).isString()) {
                        sdt = (String) tdt;
                    } else if (((BsonValue) tdt).isDocument()) {
                        sdt = Json.jsonUtil.object2JsonString(tdt);
                    } else if (((BsonValue) tdt).isDateTime()) {
                        Date date = (Date) tdt;
                        Timestamp timestamp = new Timestamp(date.getTime());
                        sdt = String.format("%s.%09d", db2DateFormat.format(timestamp), timestamp.getNanos());
                    } else if (((BsonValue) tdt).isBinary()) {
                        BsonBinary binary = ((BsonValue) tdt).asBinary();
                        sdt = "BinData(" + binary.getType() + ",\""
                                + Base64.getEncoder().encodeToString(binary.getData()) + "\")";
                    } else if (((BsonValue) tdt).isTimestamp()) {
                        BsonTimestamp bsonTimestamp = ((BsonValue) tdt).asTimestamp();
                        sdt = "Timestamp(" + bsonTimestamp.getTime() + "," + bsonTimestamp.getInc() + ")";
                    } else if (((BsonValue) tdt).isRegularExpression()) {
                        BsonRegularExpression bsonRegularExpression = ((BsonValue) tdt).asRegularExpression();
                        sdt = "/" + bsonRegularExpression.getPattern() + "/" + bsonRegularExpression.getOptions();
                    } else if (((BsonValue) tdt).isJavaScript()) {
                        BsonJavaScript bsonJavaScript = ((BsonValue) tdt).asJavaScript();
                        sdt = bsonJavaScript.getCode();
                    } else if ("UNDEFINED".equalsIgnoreCase(((BsonValue) tdt).getBsonType().name())) {
                        sdt = "undefined";
                    } else if (((BsonValue) tdt).isArray()) {
                        sdt = Json.jsonUtil.object2JsonString(tdt);
                    } else {
                        sdt = String.valueOf(tdt);
                    }
                } else if (tdt instanceof Document) {
                    sdt = Json.jsonUtil.object2JsonString(tdt);
                } else if (tdt instanceof Date) {
                    Date date = (Date) tdt;
                    Timestamp timestamp = new Timestamp(date.getTime());
                    sdt = String.format("%s.%09d", db2DateFormat.format(timestamp), timestamp.getNanos());
                } else if (tdt instanceof Code) {
                    sdt = "{\"code\":\"" + ((Code) tdt).getCode() + "\"}";
                } else if (tdt instanceof Binary) {
                    sdt = "BinData(" + ((Binary) tdt).getType() + ",\""
                            + Base64.getEncoder().encodeToString(((Binary) tdt).getData()) + "\")";
                } else if (tdt instanceof Arrays || tdt instanceof List) {
                    sdt = Json.jsonUtil.object2JsonString(tdt);
                } else if (tdt instanceof MaxKey || tdt instanceof MinKey) {
                    sdt = JsonUtil.jsonToPojo(bsonData.toJson(), JsonNode.class).get(k).toString();
                } else {
                    sdt = String.valueOf(tdt);
                }
                bsonData.put(k, sdt);
                tdt = null;
                sdt = null;
            }
            return bsonData;
        } else {
            return data;
        }
    }

}
