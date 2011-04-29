package com.intersys.gds.query;

import com.intersys.gds.Util;

/** <CODE>GT</CODE> (Greater Than)query. Returns all Documents whose values
 * corresponding to the given key are greater than the specified numerical value.
 *
 */
public class LT extends Query {

    private Number      value;

   /** <CODE>LT</CODE> query constructor. Takes key and value as parameters.
    *
    */
    public LT(String k, Number v) {
        key = k;
        Util.validateNumericType(v);
        value = v;
    }

    public boolean apply(Object candidate) {
        if (value instanceof Integer) {
            return (Integer)candidate < (Integer)value;
        }
        if (value instanceof Long) {
            return (Long)candidate < (Long)value;
        }
        return (Double)candidate < (Double)value;
    }
}
