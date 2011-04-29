package com.intersys.gds;

import java.io.StringWriter;

public class Util {

    public static boolean isPrimitiveWrapper(Class type) {
        return (
         // (type == Boolean.class) ||
         (type == Integer.class) ||
         (type == Long.class) ||
         // (type == Short.class) ||
         (type == Float.class) ||
         (type == Double.class)); // ||
         //(type == Byte.class) ||
         //(type == Character.class));
    }

    public static boolean isDatatype(Class type) {
        return (type.isPrimitive() || isPrimitiveWrapper(type) ||
                (type == String.class));
    }

    static String toJSON(Document document, String separator, boolean topLevel) {
        if (separator == null) {
            separator = "";
        }
        String json = separator + "{" + separator;
        boolean first = true;
        for (String key : document.keySet()) {
            if (!first) {
                json = json + "," + separator;
            }
            first = false;
            Object doc = document.get(key);
            if (doc instanceof Document[]) {
                Document[] docs = (Document[]) doc;
                String temp = "";
                for (int i=0;i<docs.length;i++) {
                     temp = temp + toJSON(docs[i],separator+ " ",false);
                }
                json = json + key + ":" + separator + "[" + temp + separator + "]";
            } else if (doc instanceof Document) {
                json = json + key + " : " + toJSON((Document)doc,separator+ " ",false);
            } else {
                Object value = document.get(key);
                if (value == null) {
                    json = json + key + " : null";
                } else if (value instanceof Object[]) {
                    json = json + key + jsonStringArray(toStringArray((Object[])value));
                } else if (value instanceof byte[]) {
                    json = json + key + " : " + new String((byte[])value);
                } else {
                    json = json + key + " : " + value;
                }
            }
        }
        return json + separator + "}";
    }

    static String toXML(Document document, String separator) {
        if (separator == null) {
            separator = "";
        }
        String xml = "";
        for (String key : document.keySet()) {
            Object doc = document.get(key);
            if (doc instanceof Document[]) {
                Document[] docs = (Document[]) doc;
                String temp = separator;
                for (int i=0;i<docs.length;i++) {
                    temp = temp + "<item>" + toXML(docs[i],separator+ " ") + separator + "</item>";
                }
                xml = xml + writeElement(key,temp) + separator;
            } else if (doc instanceof Document) {
                xml = xml + writeElement(key,toXML((Document)doc,separator+ " "));
            } else {
                Object value = document.get(key);
                if (value == null) {
                    xml = xml + writeElement(key,"null");
                } else if (value instanceof Object[]) {
                    xml = xml + writeElement(key,xmlStringArray(toStringArray((Object[])value),separator));
                } else {
                    xml = xml + writeElement(key,value.toString());
                }
            }
        }
        return xml + separator;
    }

    static String[] toStringArray(Object[] array) {
        if (array.length == 0) {
            return null;
        }
        if (array[0] instanceof String) {
            return (String[]) array;
        }
        String[] stringArray = new String[array.length];
        for (int i=0;i<array.length;i++) {
            stringArray[i] = array.toString();
        }
        return stringArray;
    }

    private static String jsonStringArray(String[] stringArray) {
        String temp = "";
        for (int i=0;i<stringArray.length;i++) {
            temp = temp + stringArray[i];
            if (i < stringArray.length-1) {
                temp = temp + ", ";
            }
        }
        return " : [ " + temp + " ]";
    }

    private static String xmlStringArray(String[] stringArray, String separator) {
        String temp = "";
        for (int i=0;i<stringArray.length;i++) {
            temp = temp + separator + "  <item>" + stringArray[i] + "</item>";
        }
        return temp + separator;
    }

    private static String xmlAsString(StringWriter writer) {
	writer.flush();
	return writer.toString();
    }

    private static String writeElement(String name, String data) {
	return "\r\n<" + name + ">" + data + "</" + name + ">";
    }

    public static void validateNumericType(Number number) {
        if (!(number instanceof Integer) && !(number instanceof Double)  && !(number instanceof Long)) {
            throw new RuntimeException("Unsupported Number type: " + number.getClass().toString());
        }
    }

    public static void validateNumericTypes(Number number1, Number number2) {
        if ((number1 instanceof Integer) && (number2 instanceof Integer)) {
            return;
        }
        if ((number1 instanceof Long) && (number2 instanceof Long)) {
            return;
        }
        if ((number1 instanceof Double) && (number2 instanceof Double)) {
            return;
        }
        throw new RuntimeException("Unsupported Number types: " + number1.getClass().toString() + " " + number2.getClass().toString());
    }

}
