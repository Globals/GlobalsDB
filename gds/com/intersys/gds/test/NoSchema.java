
package com.intersys.gds.test;

import java.util.ArrayList;

import com.intersys.gds.noschema.NoSchemaConnection;
import com.intersys.gds.noschema.NoSchemaDocument;
import com.intersys.gds.noschema.NoSchemaMap;

/** <CODE>NoSchema.java</CODE> is the main GDS test program for the no
 * schema version. The no schema version uses Globals API to build a
 * sample Document Store database without a schema. All key values are
 * stored together with the corresponding values, making it possible to
 * store heterogenous documents as part of a single DocumentMap. The 
 * test exercises the basic functionality such as connecting, storing
 * and running some basic queries using the simple GDS query language.
 * <CODE>NoSchema.java</CODE> extends <CODE>Test.java</CODE> which has a
 * number of utility methods shared by all DocumentStore test programs.
 *
 *
 * <CODE>This is the best place to get started with no schema version of GDS.</CODE>
 *
 */
public class NoSchema extends BasicTest {

    private NoSchemaMap                 customers;
    private NoSchemaMap                 accounts;
    private NoSchemaConnection          connection;
    private ArrayList<NoSchemaDocument> customersData;
    private ArrayList<NoSchemaDocument> accountsData;

   /** Main entry point.
    *
    * @param args program arguments
    */
    public static void main(String[] args) throws Exception {
        NoSchema test = new NoSchema();
        test.init(args);
        test.generateSampleData();
        test.createMaps();
        test.deleteData();
        test.store();
        test.load();
        test.close();
    }

   /** Test and profile store data.
    */
    void store() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            accounts.store((i * 10) + "",accountsData.get(i));
        }
        for (int i=0;i<documentCount;i++) {
            customers.store("" + i,customersData.get(i));
        }
        reportStore(documentCount*2,start);
    }

   /** Test and profile load data.
    */
    void load() {
        long start = getTime();
        for (int i=0;i<documentCount;i++) {
            NoSchemaDocument customer = customers.load(""+i,customersData.get(0));
            if (i == 2) {
                System.out.println(customer.toJSON("\r\n"));
            }
        }
        reportQuery(documentCount,start);
    }

   /** Deletes all Customers and Accounts data stored in ^Customers
    * and ^Accounts globals
    */
    void deleteData() {
        customers.clear();
        accounts.clear();
    }

   /** Creates Accounts and Customers NoSchemaMaps
    */
    private void createMaps() {
        customers = connection.getNoSchemaMap("Customers");
        accounts = connection.getNoSchemaMap("Accounts");
    }

   /** Generates sample data
    */
   void generateSampleData() {
        accountsData = generateAccounts(documentCount);
        customersData = generateCustomers(documentCount,accountsData);
    }

   /** Generates Addresses Documents.
    *
    * @param count number of addresses to generate
    *
    * @return ArrayList<NoSchemaDocument> an array list of addresses
    *
    */
    static ArrayList<NoSchemaDocument> generateAddresses(int count) {
        ArrayList<NoSchemaDocument> addresses = new ArrayList<NoSchemaDocument>();
        for (int i=0;i<count;i++) {
            NoSchemaDocument address = new NoSchemaDocument();
            address.put("street","One Memorial Drive");
            if (i == 0) {
                address.put("city","Cambridge");
                address.put("state","MA");
                address.put("zip",randomZip());
            }
            if (i % 2 == 0) {
                address.put("city","Cambridge");
                address.put("state","MA");
            } else {
                address.put("zip",randomZip());
            }
            if ((i % 4 == 0) || (i % 3 == 0)) {
                address.put("country","USA");
            }
            addresses.add(address);
        }
        return addresses;
    }

   /** Generates Accounts Documents.
    *
    * @param count number of accounts to generate
    *
    * @return ArrayList<NoSchemaDocument> an array list of accounts
    *
    */
    static ArrayList<NoSchemaDocument> generateAccounts(int count) {
        ArrayList<NoSchemaDocument> accounts = new ArrayList<NoSchemaDocument>();
        for (int i=0;i<count;i++) {
            NoSchemaDocument account = new NoSchemaDocument();
            account.put("number",getUniqueAccountNumber());
            account.put("type","SAV");
            account.put("balance",randomFloat());
            if (i % 7 == 0) {
                account.put("opened",randomDate());
            }
            account.put("owner",i);
            NoSchemaDocument[] transactions = null;

            if ((i % 2 == 0) || (i % 3 == 0) || (i == 0)) {
                transactions = new NoSchemaDocument[2];
                transactions[0]= new NoSchemaDocument();
                transactions[1]= new NoSchemaDocument();
                transactions[0].put("type","WITH");
                transactions[0].put("timestamp",randomTimestamp());
                transactions[0].put("amount",randomInt(0,10000));

                transactions[1].put("type","DEPO");
                transactions[1].put("timestamp",randomTimestamp());
                transactions[1].put("amount",randomInt(0,1000));
            }
            if (i % 5 == 0) {
                transactions = new NoSchemaDocument[1];
                transactions[0]= new NoSchemaDocument();
                transactions[0].put("type","WIRE");
                transactions[0].put("timestamp",randomTimestamp());
                transactions[0].put("amount",randomInt(0,10000));
            }
            account.put("transactions", transactions);
            accounts.add(account);
        }
        return accounts;
    }

   /** Generates Customers Documents.
    *
    * @param count number of customers to generate
    * @param accounts accounts
    *
    * @return ArrayList<NoSchemaDocument> an array list of customers
    *
    */
    static ArrayList<NoSchemaDocument> generateCustomers(int count, ArrayList<NoSchemaDocument> accounts) {
        ArrayList<NoSchemaDocument> customers = new ArrayList<NoSchemaDocument>();
        ArrayList<NoSchemaDocument> addressesData = generateAddresses(count);
        for (int i=0;i<count;i++) {
            NoSchemaDocument customer = new NoSchemaDocument();
            customer.put("name",randomName());
            customer.put("ssn",randomSSN());
            customer.put("dob",randomDate());
            customer.put("age",randomInt(18,99));
            if ((i == 0) || (i % 2 == 0)) {
                customer.put("home phone",randomPhone());
            }
            if ((i == 0) || (i % 3 == 0)) {
                customer.put("cell phone",randomPhone());
            }
            customer.put("address",addressesData.get(i));
            customer.put("account",accounts.get(i));
            customers.add(customer);
        }
        return customers;
    }

   /** Performs initialization by reading program parameters,
    * connecting, generating sample Accounts and Customers data,
    * as well as creating the corresponding NoSchemaDocument Maps.
    */
    void init(String[] args) {
        checkParameters(args);
        connection = new NoSchemaConnection();
        connection.connect();
    }

   /** Closes the connection.
    */
    void close() {
        connection.close();
    }

}
