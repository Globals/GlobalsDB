package com.intersys.gds.test;

import java.util.Random;

import com.intersys.gds.Connection;
import com.intersys.gds.Document;
import com.intersys.gds.DocumentMap;
import com.intersys.gds.DocumentType;

 /** <CODE>Benchmark.java</CODE> is meant to be used as an example on
  * how to performance test XEP. It performs all basic XEP tasks, but
  * also keeps track of numbers.
  * It uses <CODE>xep.samples.BenchmarkSample</CODE> as the event class.
  *
  */

public class Benchmark extends BasicTest {

    private Connection                  connection;
    private DocumentMap                 benchmark;
    private Document[]                  data;

    Benchmark() {
        documentCount = 100000;
    }

   /** Main entry point.
    *
    * @param args program arguments
    */
    public static void main(String[] args) {
        Benchmark test = new Benchmark();
        test.runTest(args);
    }

    /** Runs a simple test
     *
     * @param args program arguments
     * @throws Exception
     */
    void runTest(String[] args) {
        init(args);
        deleteData();
        store();
        load();
        close();
    }

   /** Test and profile storing of data.
    */
    void store() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            benchmark.store(Integer.toString(i),data[i]);
        }
        reportStore(documentCount,start);
        benchmark.buildIndices();
    }

   /** Test and profile data load.
    */
    void load() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            Document current = benchmark.load(Integer.toString(i));
            int ind = (Integer) data[i].get("intOne");
            if (!(data[ind].get("longTwo").equals(current.get("longTwo")))) { // ||
                //(((Double[])(data[ind].get("doubleArrayOne"))[3]).equals((Double[])current.get("doubleArrayOne")))[3])) {
                throw new RuntimeException("Data does not match");
            }
            //if (i == 2) {
                //System.out.println(customer.toJSON("\r\n"));
            //}
        }
        reportLoad(documentCount,start);
    }

    void generateSampleData() {
        Random rnd = new Random(5283314247687391911L);
        data = new Document[100000];
        for (int i=0;i<documentCount;i++) {
            data[i] = new Document();
            data[i].put("shortOne",rnd.nextInt());
            data[i].put("intOne",i);
            data[i].put("longOne",12345678L);
            data[i].put("longTwo",rnd.nextLong());
            data[i].put("longThree",rnd.nextLong());
            data[i].put("longFour",i);
            data[i].put("longFive",rnd.nextInt());
            data[i].put("floatOne",(double)rnd.nextDouble());
            data[i].put("floatTwo",rnd.nextDouble());
            data[i].put("floatThree",rnd.nextDouble());
            data[i].put("floatFour",rnd.nextDouble());
            data[i].put("doubleOne",rnd.nextDouble());
            data[i].put("doubleTwo",rnd.nextDouble());
            data[i].put("doubleThree",rnd.nextDouble());

            // Sample arrays
            double[] doubleArrayOne = new double[10];
            for (int j = 0;j < 10;j++) {
                doubleArrayOne[j] = rnd.nextDouble();
            }
            data[i].put("doubleArrayOne",doubleArrayOne);

            Double[] doubleArrayTwo = new Double[10];
            for (int j = 0;j < 10;j++) {
                doubleArrayTwo[j]= rnd.nextDouble();
            }
            data[i].put("doubleArrayTwo",doubleArrayTwo);
        }
    }

    void deleteData() {
        benchmark.clear();
    }

   /** Performs initialization by reading program parameters,
    * connecting, generating sample Accounts and Customers data,
    * as well as creating the corresponding Document Maps.
    */
    void init(String[] args) {
        checkParameters(args);
        generateSampleData();
        connection = new Connection();
        connection.connect();
        deleteSchema();
        createSchema();
        benchmark = connection.getDocumentMap("Benchmark");
    }

   /** Closes the connection.
    */
    void close() {
        benchmark.close();
        connection.close();
    }

   /** Deletes Benchmark schema (by deleting ^Schema(Benchmark) global node)
    */
    private void deleteSchema() {
        connection.deleteDocumentType("Benchmark");
    }

    private void createSchema() {
        // since we fully intitialized data, we can use the flavor
        // of creatDocumentType which takes a Document and infers
        // the types
        DocumentType type = DocumentType.createDocumentType("Benchmark",data[0]);
        connection.saveDocumentType(type);
    }

}
