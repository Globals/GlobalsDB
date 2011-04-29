package com.intersys.gds.test;

/** <CODE>Test.java</CODE> is a base Test class that can serve as
 * a base for testing any Document Store NoSQL database. It declares
 * a number of abstract methods that its subclasses need to implement
 * in order to be able to connect, disconnect and perform data store,
 * load or queries. It also provides a number of utility methods to
 * help generate some sample data etc.
  *
  */

public abstract class Test extends BasicTest {

   /** startsWith query. Finds all elements whose values
    * corresponding to the given <CODE>key</CODE> start with
    * <CODE>pattern</CODE>
    *
    * @param key key
    * @param pattern starts with pattern
    */
   abstract void startsWith(String key, String pattern);

   /** between query. Finds all elements whose values
    * corresponding to the given <CODE>key</CODE> are between
    * <CODE>from</CODE> and <CODE>to</CODE>
    *
    * @param key key
    * @param from from value
    * @param to to value
    */
    abstract void between(String key, int from, int to);

   /** Runs a sample And query by combining two conditions. 
    */
    abstract void and(String key1, String pattern, String key2, int from, int to);

   /** Runs a sample Or query by combining two conditions.
    */
    abstract void or(String key1, int i1, String key2, int i2);

   /** Runs a sample Equals query.
    *
    * @param key key
    * @param value value to match
    */
    abstract void equals(String key, Object value);

   /** Perform PatternMatch query.
    *
    * @param key key
    * @param pattern pattern
    */
    abstract void patternMatch(String key, String pattern);

    /** Runs a simple test
     *
     * @param args program arguments
     * @throws Exception
     */
    void runTest(String[] args) throws Exception {
        generateSampleData();
        init(args);
        deleteData();
        store();
        load();
        startsWith("name","B");
        between("age",20,40);
        and("name","C","age",20,40);
        equals("age",21);
        or("age",60,"age",90);
        patternMatch("name","[A-F][A-E][A-D][A-D][A-D]");
        close();
    }

}
