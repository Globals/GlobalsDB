package com.intersys.gds.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/** <CODE>BasicTest.java</CODE> is a base Test class that can serve as
 * a base for testing any Document Store NoSQL database. It declares
 * a number of abstract methods that its subclasses need to implement
 * in order to be able to connect, disconnect and perform data store,
 * load or queries. It also provides a number of utility methods to
 * help generate some sample data etc.
  *
  */

public abstract class BasicTest {

   private static int accountNumber    = 0;
           static int documentCount    = 50000;

   /** Test and profile store data.
    */
   abstract void store();

   /** Test and profile load data.
    */
   abstract void load();

   /** Deletes all data.
    */
    abstract void deleteData();

   /** Generates sample data.
    */
    abstract void generateSampleData() throws Exception;

   /** Performs initialization by reading program parameters,
    * connecting, generating sample Accounts and Customers data,
    * as well as creating the corresponding Document Maps.
    */
    abstract void init(String[] args) throws Exception;

   /** Performs any necessary cleanup.
    */
    abstract void close();

    /** Runs a simple test
     *
     * @param args program arguments
     * @throws Exception
     */
    //abstract void runTest(String[] args) throws Exception;

   /** Checks input parameters and reads them in if present.
    */
    public void checkParameters(String[] args) {
        if ((args.length == 1) && args[0].equalsIgnoreCase("-help")) {
            System.out.println("Parameters: \r\n  (1) total number of entries [optional, defaulfs to 10,000]");
        }
        if (args.length > 0) {
            documentCount = Integer.parseInt(args[0]);
        }
    }

   /** Generates Address Document.
    *
    * @param documentClass target document type (for example, for GSD
    *                      - Document, for MongoDB - BasicDBObject)
    *
    * @return HashMap<String,Object> address document
    *
    */
    static HashMap<String,Object> generateAddress(Class documentClass) throws Exception {
        HashMap<String,Object> address = (HashMap<String, Object>) documentClass.newInstance(); //HashMap<String,Object>();
        address.put("street","One Memorial Drive");
        address.put("city","Cambridge");
        address.put("state","MA");
        address.put("zip",randomZip());
        address.put("country","USA");
        return address;
    }

   /** Generates an Account Document.
    *
    * @param ownerId an id linking the account to Customer
    * @param documentClass target document type (for example, for GSD
    *                      - Document, for MongoDB - BasicDBObject)
    *
    * @return HashMap<String,Object> Account document
    *
    */
    static HashMap<String,Object> generateAccount(int ownerId, Class documentClass) throws Exception {
        HashMap<String,Object> account = (HashMap<String, Object>) documentClass.newInstance(); //new HashMap<String,Object>();
        account.put("number",getUniqueAccountNumber());
        if (ownerId % 2 == 0) {
            account.put("type","Checking");
        } else {
            account.put("type","Savings");
        }
        account.put("balance",randomFloat());
        account.put("opened",randomDate());
        account.put("owner",ownerId);
        Object transactions = generateTransactions(documentClass,ownerId);
        account.put("transactions",transactions);
        return account;
    }

   /** Generates Transaction Document array.
    *
    * @param documentClass target document type (for example, for GSD
    *                      - Document, for MongoDB - BasicDBObject)
    *
    * @param ownerId owner id
    *
    * @return HashMap[] an array of transaction documents
    *
    */
    static HashMap[] generateTransactions(Class documentClass, int ownerId) throws Exception {
        int count = ownerId % 4;
        // make sure the first owner has an account
        if (ownerId == 0) {
            count = 3;
        }
        if (count == 0) {
            return null;
        }
        HashMap[] transactions = (HashMap[]) Array.newInstance(documentClass,count);
        transactions[0]= (HashMap) documentClass.newInstance();
        transactions[0].put("type","WITH".getBytes());
        transactions[0].put("timestamp",randomTimestamp());
        transactions[0].put("amount",randomInt(0,5000));
        if (count == 1) {
            return transactions;
        }
        transactions[1]= (HashMap) documentClass.newInstance();
        transactions[1].put("type","DEPO".getBytes());
        transactions[1].put("timestamp",randomTimestamp());
        transactions[1].put("amount",randomInt(0,5000));
        if (count == 2) {
            return transactions;
        }
        transactions[2]= (HashMap) documentClass.newInstance();
        transactions[2].put("type","TRNS".getBytes());
        transactions[2].put("timestamp",randomTimestamp());
        transactions[2].put("amount",randomInt(0,5000));
        return transactions;
    }

   /** Generates Customers Documents.
    *
    * @param documentClass target document type (for example, for GSD
    *                      - Document, for MongoDB - BasicDBObject)
    *
    * @return ArrayList an array list of customers
    *
    */
    static ArrayList generateCustomers(Class documentClass) throws Exception {
        ArrayList<HashMap<String,Object>> customers = new ArrayList<HashMap<String,Object>>();
        for (int i=0;i<documentCount;i++) {
            HashMap<String,Object> customer = (HashMap<String, Object>) documentClass.newInstance();
            customer.put("name",randomName());
            customer.put("ssn",randomSSN());
            customer.put("dob",randomDate());
            customer.put("age",randomInt(18,99));
            customer.put("home phone",randomPhone());
            if (i % 3 == 0) {
                customer.put("cell phone",randomPhone());
                customer.put("services",new String[]{"ATM","Direct Deposit","Checking","Savings"});
            } else {
                if (i % 2 == 0) {
                    customer.put("services",new String[]{"ATM","Savings"});
                } else {
                    customer.put("services",new String[]{"CD","Investment"});
                }
            }
            customer.put("address",generateAddress(documentClass));
            customer.put("account",generateAccount(i,documentClass));
            customers.add(customer);
        }
        return customers;
    }

   /** Generates a random five letter name.
    *
    * @return String a five letter random name
    *
    */
    static String randomName() {
        Random random = new Random();
        char[] ba = new char[5];
        ba[0] =  (char) (random.nextInt(26) + 65);
        ba[1] = (char) (random.nextInt(26) + 65);
        ba[2] = (char) (random.nextInt(26) + 65);
        ba[3] = (char) (random.nextInt(26) + 65);
        ba[4] = (char) (random.nextInt(26) + 65);
        return new String(ba);
    }

   /** Generates a random date.
    *
    * @return String random date
    *
    */
    static String randomDate() {
        String month = random(1,12);
        if (month.length() == 1) {
            month = "0" + month;
        }
        String day = random(1,30);
        if (day.length() == 1) {
            day = "0" + day;
        }
        String year = random(1920,2011);
        return month+"-"+day+"-"+year;
    }

   /** Generates a random time.
    *
    * @return String random time
    *
    */
    static String randomTime() {
        String hours = random(0,23);
        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        String minutes = random(0,59);
        if (minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        String seconds = random(0,59);
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        return hours+":"+minutes+":"+seconds;
    }

   /** Generates a random phone number.
    *
    * @return String random phone number
    *
    */
    static String randomPhone() {
        return "("+random(100,999)+") "+random(100,999)+"-"+random(1000,9999);
    }

   /** Generates a random zip number.
    *
    * @return String random zip number
    *
    */
    static String randomZip() {
        String zip = random(1000,99999);
        if (zip.length() == 4) {
            return "0" + zip;
        }
        return zip;
    }

   /** Generates a random SSN.
    *
    * @return String random SSN
    *
    */
    static String randomSSN() {
        return random(100,999)+"-"+random(10,99)+"-"+random(1000,9999);
    }

   /** Generates a random double.
    *
    * @return double random double
    *
    */
    static double randomFloat() {
        return Double.parseDouble(random(0,10000)+"."+random(0,99));
    }

   /** Generates a random integer.
    *
    * @return double random integer
    *
    */
    static int randomInt(int from, int to) {
        Random random = new Random();
        int x = random.nextInt(to);
        if (x < from) {
            return randomInt(from,to);
        }
        return x;
    }

   /** Generates a random timestamp.
    *
    * @return String random timestamp
    *
    */
    static String randomTimestamp() {
        return (randomDate() + " " + randomTime());
    }

   /** Returns a random integer in the from/to range as a String.
    *
    * @param from from
    * @param to to
    *
    * @return String random integer as String
    */
    private static String random(int from, int to) {
        return Integer.toString(randomInt(from,to));
    }

   /** Returns current time.
    * @return long current time
    */
    public static long getTime() {
        return (new java.util.Date(System.currentTimeMillis())).getTime();
    }

   /** Reports store duration and rates.
    *
    */
    static void reportStore(long count, long start) {
        long duration = getTime() - start;
        System.out.println("\r\n  Number of entries stored = " + count);
        System.out.println("  Duration: "+ duration/1000.0 + " seconds");
        System.out.println("  Store rate: " + (int)(count * 1000.0/duration) + " objects/sec\r\n");
    }

   /** Reports query duration and rates.
    *
    */
    static void reportLoad(long count, long start) {
        long duration = getTime() - start;
        System.out.println("\r\n  Number of entries loaded = " + count);
        System.out.println("  Duration: "+ duration/1000.0 + " seconds");
        System.out.println("  Load rate: " + (int)(count * 1000.0/duration) + " objects/sec\r\n");
    }

   /** Reports query duration and rates.
    *
    */
    static void reportQuery(long count, long start) {
        long duration = getTime() - start;
        System.out.println("  Number of entries returned = " + count);
        System.out.println("  Duration: "+ duration/1000.0 + " seconds");
        System.out.println("  Query rate: " + (int)(count * 1000.0/duration) + " objects/sec\r\n");
    }

    static int getUniqueAccountNumber() {
        accountNumber++;
        return accountNumber;
    }

}

