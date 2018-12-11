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

package org.datatech.baikal.web.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.datatech.baikal.web.entity.bo.SourceJdbcBO;
import org.datatech.baikal.util.JsonUtil;
import org.datatech.baikal.web.utils.StringUtil;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.UpdateResult;

public class MongoDb {
    private static MongoCollection<Document> collection;
    private static MongoClientOptions.Builder options;

    static {
        options = MongoClientOptions.builder();

    }

    public static Set<String> getTableName(SourceJdbcBO sourceJdbcBO) {
        MongoClientURI puri = new MongoClientURI(
                getMongoUri(sourceJdbcBO.getJDBC_URL(), sourceJdbcBO.getUSER(), sourceJdbcBO.getPASSWORD()), options);
        MongoClient client = new MongoClient(puri);
        MongoDatabase db = client.getDatabase(sourceJdbcBO.getSCHEMA_NAME());
        MongoIterable<String> list = db.listCollectionNames();
        MongoCursor<String> tResultMongoCursor = list.iterator();
        Set<String> set = new HashSet<>();
        while (tResultMongoCursor.hasNext()) {
            set.add(tResultMongoCursor.next());
        }
        client.close();
        return set;
    }

    public static void main(String[] args) {
        System.out.println(getTableColumn("test", "mongodb://172.18.0.2:27017", "test"));
    }

    public static Map<String, String> getTableColumn(String databaseName, String uri, String collectionName) {
        MongoClientURI puri = new MongoClientURI(uri);
        MongoClient client = new MongoClient(puri);
        MongoDatabase db = client.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        Document document = collection.find().first();
        Map<String, String> map = new HashedMap();
        if(StringUtil.isNotEmpty(document)) {
            String jsonString = document.toJson();
            map = JsonUtil.getMap4Json(jsonString);
        }
        return map;
    }

    public static long getDocumentCount(String databaseName, String uri, String collectionName) {
        MongoClientURI puri = new MongoClientURI(uri);
        MongoClient client = new MongoClient(puri);
        MongoDatabase db = client.getDatabase(databaseName);
        MongoCollection<Document> collection = db.getCollection(collectionName);
        return collection.count();
    }

    public static Integer getTableCount(String databaseName, String uri) {
        MongoClientURI puri = new MongoClientURI(uri);
        MongoClient client = new MongoClient(puri);
        MongoDatabase db = client.getDatabase(databaseName);
        MongoIterable<String> list = db.listCollectionNames();
        MongoCursor<String> tResultMongoCursor = list.iterator();
        int count = 0;
        while (tResultMongoCursor.hasNext()) {
            tResultMongoCursor.next();
            count++;
        }
        client.close();
        return count;
    }

    public static String getMongoUri(String url, String user, String password) {
        if (url.contains(user) && url.contains(password)) {
            return url;
        } else {
            if (StringUtil.isNotEmpty(user) && StringUtil.isNotEmpty(password)) {
                String[] arr = url.split("//");
                url = arr[0] + "//" + user + ":" + password + "@" + arr[1];
            }
            return url;
        }
    }

    /**
     * 链接数据库
     *
     * @param databaseName   数据库名称
     * @param collectionName 集合名称
     * @param hostName       主机名
     * @param port           端口号
     */
    public static void connect(String databaseName, String collectionName, String hostName, int port) {
        @SuppressWarnings("resource")
        MongoClient client = new MongoClient(hostName, port);
        MongoDatabase db = client.getDatabase(databaseName);
        db.listCollectionNames();
        collection = db.getCollection(collectionName);
        collection.find().first();
        System.out.println(collection);
    }

    /**
     * 插入一个文档
     *
     * @param document 文档
     */
    public static void insert(Document document) {
        collection.insertOne(document);
    }

    /**
     * 查询所有文档
     *
     * @return 所有文档集合
     */
    public static List<Document> findAll() {
        List<Document> results = new ArrayList<Document>();
        FindIterable<Document> iterables = collection.find();
        MongoCursor<Document> cursor = iterables.iterator();
        while (cursor.hasNext()) {
            results.add(cursor.next());
        }

        return results;
    }

    /**
     * 根据条件查询
     *
     * @param filter 查询条件 //注意Bson的几个实现类，BasicDBObject, BsonDocument,
     *               BsonDocumentWrapper, CommandResult, Document, RawBsonDocument
     * @return 返回集合列表
     */
    public static List<Document> findBy(Bson filter) {
        List<Document> results = new ArrayList<Document>();
        FindIterable<Document> iterables = collection.find(filter);
        MongoCursor<Document> cursor = iterables.iterator();
        while (cursor.hasNext()) {
            results.add(cursor.next());
        }

        return results;
    }

    /**
     * 更新查询到的第一个
     *
     * @param filter 查询条件
     * @param update 更新文档
     * @return 更新结果
     */
    public static UpdateResult updateOne(Bson filter, Bson update) {
        UpdateResult result = collection.updateOne(filter, update);

        return result;
    }

    /**
     * 更新查询到的所有的文档
     *
     * @param filter 查询条件
     * @param update 更新文档
     * @return 更新结果
     */
    public static UpdateResult updateMany(Bson filter, Bson update) {
        UpdateResult result = collection.updateMany(filter, update);

        return result;
    }

    /**
     * 更新一个文档, 结果是replacement是新文档，老文档完全被替换
     *
     * @param filter      查询条件
     * @param replacement 跟新文档
     */
    public static void replace(Bson filter, Document replacement) {
        collection.replaceOne(filter, replacement);
    }

    /**
     * 根据条件删除一个文档
     *
     * @param filter 查询条件
     */
    public static void deleteOne(Bson filter) {
        collection.deleteOne(filter);
    }

    /**
     * 根据条件删除多个文档
     *
     * @param filter 查询条件
     */
    public static void deleteMany(Bson filter) {
        collection.deleteMany(filter);
    }
}
