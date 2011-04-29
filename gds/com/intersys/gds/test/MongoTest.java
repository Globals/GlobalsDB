package com.intersys.gds.test;

import java.util.List;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** <CODE>MongoTest.java</CODE> is the MongoDB implementation of base
 * Test.java Document Store test. It uses the same data, and performs
 * identical tasks as its GDS counterparts, making it a useful tool
 * for a side-by-side comparison of MongoDB and GDS.
 *
 */
public class MongoTest extends Test {

    private List<DBObject>  customersData;
    private DBCollection    customersCollection;
    private Mongo           mongo;
    private DB              database;

    public static void main(String[] args) throws Exception {
        MongoTest test = new MongoTest();
        test.runTest(args);
    }

    void store() {
        long start = getTime();
        // individual inserts
        for (int i=0;i<customersData.size();i++) {
            customersCollection.insert(customersData.get(i));
        }
        // bulk insert
        // customersCollection.insert(customersData);
        reportStore(documentCount*2,start);
        System.out.println("Collection size: " + customersCollection.count());
    }

    void init(String[] args) throws Exception {
        checkParameters(args);
        mongo = new Mongo("localhost");
        database = mongo.getDB("test");
        customersCollection = database.getCollection("customers");
    }

    void generateSampleData() throws Exception {
        customersData = generateCustomers(BasicDBObject.class);
    }

    void deleteData() {
        customersCollection.drop();
    }

    void close() {
        mongo.close();
    }

    void load() {
        int i = 0;
        long start = getTime();
        DBCursor cur = database.getCollection("customers").find();
        while(cur.hasNext()) {
            cur.next();
            i++;
            if (i == 1) {
                System.out.println(cur.next());
            }
        }
        reportQuery(i*2,start);
    }

    void startsWith(String key, String pattern) {
        throw new UnsupportedOperationException("Not supported yet.");
        /*
        DBCollection customers = database.getCollection("customers");
        BasicDBObject query = new BasicDBObject();
        Pattern p = Pattern.compile(pattern+"*");
        QueryBuilder qb = new QueryBuilder();
        qb = qb.regex(p);
        query.put(key,qb.get());
        DBCursor cursor = customers.find(query);
        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
        */
    }

    void query(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void between(String key, int from, int to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void and(String key1, String pattern, String key2, int from, int to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void equals(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void or(String key1, int i1, String key2, int i2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void patternMatch(String key, String pattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
