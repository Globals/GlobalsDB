package com.intersys.gds.query;

import com.intersys.gds.Util;

/** <CODE>Between</CODE> query takes three arguments, a String key
 * which identifies the key value pair to query, and two numeric
 * arguments. The two numeric arguments are passed in as instances
 * of java.lang.Numeric arguments, and have to be one of the following
 * java.lang.* types (Integer, Long or Double).
 * If the arguments are not of those types, or if the underlying data
 * is not numeric an exception is thrown.
 *
 */

public class Between extends Query {

    Number  from;
    Number  to;

   /** <CODE>Between</CODE> query constructor. Takes key and two values as parameters.
    *
    */
    public Between(String k, Number f, Number t) {
        key = k;
        from = f;
        to = t;
        Util.validateNumericTypes(from,to);
    }

    public boolean apply(Object candidate) {
        if (from instanceof Integer) {
            return ((Integer)candidate > (Integer)from) && ((Integer)candidate < (Integer)to);
        }
        if (from instanceof Long) {
            return ((Long)candidate > (Long)from) && ((Long)candidate < (Long)to);
        }
        return ((Double)candidate > (Double)from) && ((Double)candidate < (Double)to);
    }

}
