package com.intersys.gds.test;

import java.util.List;
import com.intersys.gds.Connection;
import com.intersys.gds.Document;
import com.intersys.gds.DocumentType;
import com.intersys.gds.DocumentMap;
import com.intersys.gds.ElementType;
import com.intersys.gds.query.And;
import com.intersys.gds.query.Between;
import com.intersys.gds.query.Equals;
import com.intersys.gds.query.GT;
import com.intersys.gds.query.LT;
import com.intersys.gds.query.Or;
import com.intersys.gds.query.PatternMatch;
import com.intersys.gds.query.Query;
import com.intersys.gds.query.StartsWith;

/** <CODE>GDSTest.java</CODE> is the main GDS test program for the full schema
 * version. The full schema version uses Globals API to build a sample
 * Document Store database with full schema in place. Schema is captured
 * in ^Schema global, and indexing info is tore in ^Index global. The test
 * exercises the basic functionality such as connecting, importing a schema,
 * storing and running some basic queries using the simple GDS query language.
 * <CODE>NoSchema.java</CODE> extends <CODE>Test.java</CODE> which has a
 * number of utility methods shared by all DocumentStore test programs.
 * <CODE>This is the best place to get started with full schema version GDS.</CODE>
 *
 */
public class GDSTest extends Test {

    private Connection          connection;
    private DocumentMap         customers;
    private List<Document>      customersData;

   /** Main entry point.
    *
    * @param args program arguments
    */
    public static void main(String[] args) throws Exception {
        GDSTest test = new GDSTest();
        test.runTest(args);
    }

   /** Test and profile storing of data.
    */
    void store() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            customers.store(Integer.toString(i),customersData.get(i));
        }
        reportStore(documentCount*2,start);
        customers.buildIndices();
    }

   /** Test and profile data load.
    */
    void load() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            Document customer = customers.load(Integer.toString(i));
            if (i == 2) {
                System.out.println(customer.toJSON("\r\n"));
            }
        }
        reportLoad(documentCount*2,start);
    }

   /** Sample startsWith query.
    *
    * @param key key
    * @param pattern pattern argument
    */
  public  void startsWith(String key, String pattern) {
        System.out.println("StartsWith query. Key = " + key + " startsWith " + pattern);
        Query query = new StartsWith(key,pattern);
        executeQuery(query);
    }

   /** Runs a sample And query by combining two conditions. 
    *
    * @param key1 key to be used in first sub-query
    * @param pattern argument to starts with match
    * @param key2 key to be used in second sub-query
    * @param from second query from parameter
    * @param to second query to parameter
    */
  public  void and(String key1, String pattern, String key2, int from, int to) {
        System.out.println("key1.startsWith(pattern) AND key2.between(from,to) query.");
        Query query = new And(new StartsWith(key1,pattern),new Between(key2,from,to));
        executeQuery(query);
    }

   /** Runs a sample Or query by combining two conditions.
    *
    * @param key1 key to be used in first sub-query
    * @param i1 LT query argument
    * @param key2 key to be used in second sub-query
    * @param i2 GT query argument
     */
  public   void or(String key1, int i1, String key2, int i2) {
        System.out.println("(key1 < ?) OR (key2 > ?) query.");
        Query query = new Or(new LT(key1,i1),new GT(key2,i2));
        executeQuery(query);
    }

   /** between query. Finds all elements whose values
    * corresponding to the given <CODE>key</CODE> are between
    * <CODE>from</CODE> and <CODE>to</CODE>
    *
    * @param key key
    * @param from from value
    * @param to to value
    */
  public  void between(String key, int from, int to) {
        System.out.println("Between query. Key = " + key +" between: " + from + " and " + to);
        Query query = new Between(key,from,to);
        query.limit(15);
        executeQuery(query);
    }

    /** Perform a simple Equals query.
    *
    * @param key key
    * @param value value
    */
  public  void equals(String key, Object value) {
        System.out.println("Equals query. Key = " + key + ", Value = " + value);
        Query query = new Equals(key,value);
        executeQuery(query);
    }

   /** Perform PatternMatch query.
    *
    * @param key key
    * @param pattern pattern
    */
  public  void patternMatch(String key, String pattern) {
        System.out.println("Pattern Match query. Matching: " + pattern);
        Query query = new PatternMatch(key,pattern);
        executeQuery(query);
    }

    void executeQuery(Query query) {
        long start = getTime();
        customers.executeQuery(query);
        int count = query.size();
        reportQuery(count,start);
        for (int i=0;i<count;i++) {
            Document document = query.next();
            //if (i < 3) {
            //    System.out.println(document.toJSON("\r\n "));
            //}
        }
    }

   /** Deletes all Customers and Accounts data stored in ^Customers
    * and ^Accounts globals
    */
    void deleteData() {
        customers.clear();
        connection.getDocumentMap("Accounts").clear();
     }

   /** Generates sample data
    */
    void generateSampleData() throws Exception {
        customersData = generateCustomers(Document.class);
    }

   /** Performs initialization by reading program parameters,
    * connecting, generating sample Accounts and Customers data,
    * as well as creating the corresponding Document Maps.
    */
    void init(String[] args) {
        checkParameters(args);
        connection = new Connection();
        connection.connect();
        deleteSchema();
        createSchema();
        customers = connection.getDocumentMap("Customers");
    }

   /** Closes the connection.
    */
    void close() {
        customers.close();
        connection.close();
    }

   /** Generates Customer and Account schema. The schema is stored
    * in ^Schema global. Corresponding global node entries are
    * ^Schema(Customers) and ^Schema(Accounts). Similarly, index
    * info is stored in ^Index global. Types are automatically
    * inferred in case of data types, arrays of data types and
    * embedded objects. The types can also be declared manually
    * by calling setDatatype or setEmbedded DocumentType methods.
    * For example:
    *
    *  customersType.setDatatype("name",ElementType.TYPE_STRING);
    *  customersType.setEmbedded("address",addressType,false);
    *
    * In case a reference is preferable to an embedded type, use
    * setReference to override. The below example embeds addresses
    * but sets Accounts to be a reference, which means Accounts
    * data will be stored in ^Accounts global, and Customer field
    * called 'account' will be a reference to that global.
    *
    * We also declare a number of unique, and non-unique indices.
    */
    private void createSchema() {
        // We will use the first customer data to infer types
        Document firstCustomer = customersData.get(0);

        // Customers schema
        DocumentType customersType = DocumentType.createDocumentType("Customers",firstCustomer);
        customersType.setReference("account",ElementType.TYPE_REFERENCE,"Accounts","number");

        // Customer indices
        customersType.addIndex("name",false);
        customersType.addIndex("ssn",true);
        customersType.addIndex("age",false);
        connection.saveDocumentType(customersType);

        // Accounts schema
        Document firstAccount = (Document)firstCustomer.get("account");
        DocumentType accountsType = DocumentType.createDocumentType("Accounts",firstAccount);
        accountsType.setReference("owner",ElementType.TYPE_BACK_REFERENCE,"Customers","TODO");

        // Accounts indices
        accountsType.addIndex("owner",true);
        accountsType.addIndex("number",true);
        connection.saveDocumentType(accountsType);
    }

   /** Deletes Customers and Accounts schema (by deleting
    * ^Schema(Customers), ^Schema(Accounts), ^Index(Customers)
    * and ^Index(Accounts) global nodes
    *
    */
    private void deleteSchema() {
        connection.deleteDocumentType("Customers");
        connection.deleteDocumentType("Accounts");
    }

}