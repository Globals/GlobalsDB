package com.intersys.gds;

public class ElementType {

    public  static  final   int     TYPE_STRING                 = 0;
    public  static  final   int     TYPE_INTEGER                = 1;
    public  static  final   int     TYPE_LONG                   = 2;
    public  static  final   int     TYPE_DOUBLE                 = 3;
    public  static  final   int     TYPE_INTEGER_WRAPPER        = 4;
    public  static  final   int     TYPE_LONG_WRAPPER           = 5;
    public  static  final   int     TYPE_DOUBLE_WRAPPER         = 6;
    public  static  final   int     TYPE_BYTE_ARRAY             = 7;
    public  static  final   int     TYPE_LONG_BYTE_ARRAY        = 8;
    public  static  final   int     TYPE_EMBEDDED               = 9;
    public  static  final   int     TYPE_REFERENCE              = 10;
    public  static  final   int     TYPE_STRING_ARRAY           = 11;
    public  static  final   int     TYPE_INTEGER_ARRAY          = 12;
    public  static  final   int     TYPE_LONG_ARRAY             = 13;
    public  static  final   int     TYPE_DOUBLE_ARRAY           = 14;
    public  static  final   int     TYPE_INTEGER_WRAPPER_ARRAY  = 15;
    public  static  final   int     TYPE_LONG_WRAPPER_ARRAY     = 16;
    public  static  final   int     TYPE_DOUBLE_WRAPPER_ARRAY   = 17;
    public  static  final   int     TYPE_REFERENCE_ARRAY        = 18;
    public  static  final   int     TYPE_EMBEDDED_ARRAY         = 19;
    public  static  final   int     TYPE_BACK_REFERENCE         = 20;

    String                      name;
    int                         type;
    DocumentType                nestedType;
    String                      reference;
    String                      referenceKey;

    ElementType(String n, int t) {
        name = n;
        type = t;
    }

    ElementType(String n, int t, DocumentType subtype) {
        name = n;
        type = t;
        nestedType = subtype;
    }

    ElementType(String n, int t, String ref, String key) {
        name = n;
        type = t;
        reference = ref;
        referenceKey = key;
    }

    ElementType(String n, Class clazz) {
        name = n;
        if (clazz == Integer.class) {
            type = TYPE_INTEGER_WRAPPER;
        } else if (clazz == int.class) {
            type = TYPE_INTEGER;
        } else if (clazz == Long.class) {
            type = TYPE_LONG_WRAPPER;
        } else if (clazz == long.class) {
            type = TYPE_LONG;
        } else if (clazz == Double.class) {
            type = TYPE_DOUBLE_WRAPPER;
        } else if (clazz == double.class) {
            type = TYPE_DOUBLE;
        } else if (clazz == String.class) {
            type = TYPE_STRING;
        }
    }

    boolean isBasicType() {
        return ((type == TYPE_STRING) ||
                (type == TYPE_LONG) ||
                (type == TYPE_DOUBLE) ||
                (type == TYPE_INTEGER) ||
                (type == TYPE_LONG_WRAPPER) ||
                (type == TYPE_DOUBLE_WRAPPER) ||
                (type == TYPE_INTEGER_WRAPPER) ||
                (type == TYPE_BYTE_ARRAY));
    }

    public static Object cast(String value, int type) {
        if ((type == TYPE_INTEGER) || (type == TYPE_INTEGER_WRAPPER)) {
            return Integer.parseInt(value);
        }
        if ((type == TYPE_LONG) || (type == TYPE_LONG_WRAPPER)) {
            return Long.parseLong(value);
        }
        if ((type == TYPE_DOUBLE) || (type == TYPE_DOUBLE_WRAPPER)) {
            return Double.parseDouble(value);
        }
        return value;
    }

}
