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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.datatech.baikal.task.common.SourceJdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Utility class providing methods to access MongoDB.
 */
@Service
public class MongoUtil {

    public final static Class DOCUMENT_CLASS = Document.class;
    public final static Class DOUBLE_CLASS = Double.class;
    public final static Class INTEGER_CLASS = Integer.class;
    public final static Class LONG_CLASS = Long.class;
    public final static String DICIMAL128_CLASS = "org.bson.types.Decimal128";
    private static final Logger logger = LoggerFactory.getLogger(MongoUtil.class);
    public static Method DOCUMENT_KEY_SET_METHOD;
    public static Method DOCUMENT_GET_METHOD;
    private static MongoClient client = null;
    private static MongoClientOptions.Builder options;
    private static Map<String, MongoClient> clientMap = new ConcurrentHashMap<>();

    static {
        try {
            DOCUMENT_KEY_SET_METHOD = DOCUMENT_CLASS.getMethod("keySet");
            DOCUMENT_GET_METHOD = DOCUMENT_CLASS.getMethod("get", Object.class);
        } catch (NoSuchMethodException e) {
            logger.error("Exception occurred.", e);
        }
    }

    static {
        options = MongoClientOptions.builder();

    }

    public static MongoClient getMongoClient(String uri) {
        if (client == null) {
            // set timeout to 10s
            MongoClientURI puri = new MongoClientURI(uri, options.serverSelectionTimeout(10000));
            client = new MongoClient(puri);
            clientMap.put(uri, client);
        }
        return client;
    }

    public static Pair<List<String>, List<String>> getTableColumn(String databaseName, String uri, String userName,
            String password, String collectionName) {
        String newUri = getMongoUri(uri, userName, password);
        logger.info("mongodb url [{}]", newUri);
        List<Pair<String, String>> columnList = new ArrayList<>();
        MongoClient client = getMongoClient(newUri);
        MongoDatabase db = client.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        Document document = collection.find().first();
        for (String k : document.keySet()) {
            Object tdt = document.get(k);
            if (tdt == null) {
                tdt = "null";
            }
            String hiveType;
            String columnName = k;
            Class cl = tdt.getClass();
            try {
                if (MongoUtil.DOUBLE_CLASS.equals(cl)) {
                    hiveType = "double";
                } else if (MongoUtil.INTEGER_CLASS.equals(cl)) {
                    hiveType = "int";
                } else if (MongoUtil.LONG_CLASS.equals(cl)) {
                    hiveType = "decimal(38,0)";
                } else if (MongoUtil.DICIMAL128_CLASS.equals(cl.getTypeName())) {
                    hiveType = "decimal(38,16)";
                } else {
                    hiveType = "string";
                }

            } catch (IllegalArgumentException ex) {
                hiveType = "__STRING__";
            }
            columnList.add((Pair<String, String>) Pair.of(columnName, hiveType));
            logger.info("column: {}, {},{},{}", columnName, hiveType);
        }
        List<String> listCt = new ArrayList<String>();
        List<String> listC = new ArrayList<String>();
        for (Pair<String, String> element : columnList) {
            listC.add(element.getLeft());
            listCt.add(String.format("`%s` %s", element.getLeft(), element.getRight()));
        }
        return Pair.of(listCt, listC);
    }

    public static List<Pair<String, String>> getColumnList(SourceJdbc sourceJdbc, String instanceName,
            String schemaName, String tableName) throws Exception {
        String uri = sourceJdbc.getJDBC_URL();
        String userName = sourceJdbc.getUSER();
        String password = sourceJdbc.getPASSWORD();
        String newUri = getMongoUri(uri, userName, password);
        List<Pair<String, String>> columnList = new ArrayList<>();
        MongoClient client = getMongoClient(newUri);
        MongoDatabase db = client.getDatabase(schemaName);
        MongoCollection<Document> collection = db.getCollection(tableName);
        Document document = collection.find().first();
        for (String k : document.keySet()) {
            Object tdt = document.get(k);
            if (tdt == null) {
                tdt = "null";
            }
            String hiveType;
            String columnName = k;
            Class cl = tdt.getClass();
            try {
                if (MongoUtil.DOUBLE_CLASS.equals(cl)) {
                    hiveType = "double";
                } else if (MongoUtil.INTEGER_CLASS.equals(cl)) {
                    hiveType = "int";
                } else if (MongoUtil.LONG_CLASS.equals(cl)) {
                    hiveType = "long";
                } else if (MongoUtil.DICIMAL128_CLASS.equals(cl.getTypeName())) {
                    hiveType = "double";
                } else {
                    hiveType = "string";
                }
            } catch (IllegalArgumentException ex) {
                hiveType = "__STRING__";
            }
            columnList.add((Pair<String, String>) Pair.of(columnName, hiveType));
            logger.info("column: {}, {},{},{}", columnName, hiveType);
        }
        return columnList;
    }

    /**
     * Get MongoDB connection url with username and password
     *
     * @param url URL
     * @param user user
     * @param password password
     * @return Mongo URL
     */
    public static String getMongoUri(String url, String user, String password) {
        if (url.contains(user) && url.contains(password)) {
            return url;
        } else {
            if (StringUtils.isNotEmpty(user) && StringUtils.isNotEmpty(password)) {
                String[] arr = url.split("//");
                try {
                    url = String.format("%s//%s:%s@%s", arr[0], URLEncoder.encode(user, StandardCharsets.UTF_8.name()),
                            URLEncoder.encode(password, StandardCharsets.UTF_8.name()), arr[1]);

                } catch (UnsupportedEncodingException e) {
                    logger.error("Exception occurred.", e);
                }
            }
            return url;
        }
    }

}
