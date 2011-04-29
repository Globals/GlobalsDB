package com.intersys.gds.test;

import java.lang.reflect.Field;

import com.intersys.gds.Document;
import com.intersys.gds.Connection;
import com.intersys.gds.DocumentMap;

public class TwentyStrings {

    public static void main(String[] args) throws Exception {
        int count = 100000;
        xep.samples.TwentyStrings[] data = new xep.samples.TwentyStrings[count];
        for (int i=0;i<count;i++) {
            data[i] = new xep.samples.TwentyStrings("twenty string test " + i);
        }
        Connection connection = new Connection();
        connection.connect();
        DocumentMap sMap = connection.getDocumentMap("TwentyStrings");
        sMap.clear();
        Document s = new Document();
        Field field1 = data[0].getClass().getDeclaredField("s1");
        Field field2 = data[0].getClass().getDeclaredField("s2");
        Field field3 = data[0].getClass().getDeclaredField("s3");
        Field field4 = data[0].getClass().getDeclaredField("s4");
        Field field5 = data[0].getClass().getDeclaredField("s5");
        Field field6 = data[0].getClass().getDeclaredField("s6");
        Field field7 = data[0].getClass().getDeclaredField("s7");
        Field field8 = data[0].getClass().getDeclaredField("s8");
        Field field9 = data[0].getClass().getDeclaredField("s9");
        Field field10 = data[0].getClass().getDeclaredField("s10");
        Field field11 = data[0].getClass().getDeclaredField("s11");
        Field field12 = data[0].getClass().getDeclaredField("s12");
        Field field13 = data[0].getClass().getDeclaredField("s13");
        Field field14 = data[0].getClass().getDeclaredField("s14");
        Field field15 = data[0].getClass().getDeclaredField("s15");
        Field field16 = data[0].getClass().getDeclaredField("s16");
        Field field17 = data[0].getClass().getDeclaredField("s17");
        Field field18 = data[0].getClass().getDeclaredField("s18");
        Field field19 = data[0].getClass().getDeclaredField("s19");
        Field field20 = data[0].getClass().getDeclaredField("s20");
        for (int i=0;i<count;i++) {
            s.put("s1",(String) field1.get(data[i]));
            s.put("s2",(String) field2.get(data[i]));
            s.put("s3",(String) field3.get(data[i]));
            s.put("s4",(String) field4.get(data[i]));
            s.put("s5",(String) field5.get(data[i]));
            s.put("s6",(String) field6.get(data[i]));
            s.put("s7",(String) field7.get(data[i]));
            s.put("s8",(String) field8.get(data[i]));
            s.put("s9",(String) field9.get(data[i]));
            s.put("s10",(String) field10.get(data[i]));
            s.put("s11",(String) field11.get(data[i]));
            s.put("s12",(String) field12.get(data[i]));
            s.put("s13",(String) field13.get(data[i]));
            s.put("s14",(String) field14.get(data[i]));
            s.put("s15",(String) field15.get(data[i]));
            s.put("s16",(String) field16.get(data[i]));
            s.put("s17",(String) field17.get(data[i]));
            s.put("s18",(String) field18.get(data[i]));
            s.put("s19",(String) field19.get(data[i]));
            s.put("s20",(String) field20.get(data[i]));
            // Set ^Schema("TwentyStrings")=$LB(20,"s1",0,"s2",0,"s3",0,"s4",0,"s5",0,"s6",0,"s7",0,"s8",0,"s9",0,"s10",0,"s11",0,"s12",0,"s13",0,"s14",0,"s15",0,"s16",0,"s17",0,"s18",0,"s19",0,"s20",0)
        }
        sMap.clear();
        long start = Test.getTime();
        for (int i=0;i<count;i++) {
            sMap.store(i+"",s);
        }
        Test.reportStore(count,start);
        for (int i=0;i<count;i++) {
            s.put("s1",data[i].s1);
            s.put("s2",data[i].s2);
            s.put("s3",data[i].s3);
            s.put("s4",data[i].s4);
            s.put("s5",data[i].s5);
            s.put("s6",data[i].s6);
            s.put("s7",data[i].s7);
            s.put("s8",data[i].s8);
            s.put("s9",data[i].s9);
            s.put("s10",data[i].s10);
            s.put("s11",data[i].s11);
            s.put("s12",data[i].s12);
            s.put("s13",data[i].s13);
            s.put("s14",data[i].s14);
            s.put("s15",data[i].s15);
            s.put("s16",data[i].s16);
            s.put("s17",data[i].s17);
            s.put("s18",data[i].s18);
            s.put("s19",data[i].s19);
            s.put("s20",data[i].s20);
        }
        start = Test.getTime();
        for (int i=0;i<count;i++) {
            sMap.store(i+"",s);
        }
        Test.reportStore(count,start);
        connection.close();
    }

}