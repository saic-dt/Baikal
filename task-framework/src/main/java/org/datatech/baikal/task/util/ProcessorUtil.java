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

package org.datatech.baikal.task.util;

import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.datatech.baikal.common.Bytes;
import org.datatech.baikal.common.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing methods for task processors.
 */
public class ProcessorUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProcessorUtil.class);

    public static int getSeqFromPath(String path) {
        return Integer.parseInt(path.substring(Configuration.PATH_SEQ_PREFIX.length()));
    }

    /**
     * Generate a unique base64 encoded string with length of 8 bytes, it is used
     * as rowkey prefix.
     *
     * @param seqNode seqNode
     * @return a unique base64 encoded string with length of 8 bytes
     */
    public static String shortenTableName(String seqNode) {
        int seq = getSeqFromPath(seqNode);
        Random rand = new Random();
        int n = rand.nextInt(65536);
        logger.debug("the random number is [{}]", n);
        byte[] a = new byte[6];
        byte[] b = Bytes.toBytes(seq);
        byte[] c = Bytes.toBytes(n);
        System.arraycopy(c, 2, a, 0, 2);
        System.arraycopy(b, 0, a, 2, 4);
        return Base64.encodeBase64URLSafeString(a);
    }
}
