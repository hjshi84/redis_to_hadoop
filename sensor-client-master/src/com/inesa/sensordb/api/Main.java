package com.inesa.sensordb.api;

import com.inesa.redis.connect.RedisConnectPool;
import org.apache.log4j.Logger;
import org.cesl.sensordb.client.Connection;
import org.cesl.sensordb.client.ResultSet;
import org.cesl.sensordb.core.Item;
import org.cesl.sensordb.exception.DBException;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by pc on 15-5-12.
 */
public class Main {
    public static String IP_SENSORDB = "122.144.166.103";
    public static int PORT_SENSORDB = 6677;
    public static String NEW_TABLE_PREFIX = "trivial_table_";

    static int MAX = 10;
    static String REDIS_IP = "10.200.46.245";
    static int REDIS_PORT = 7000;
    public static RedisConnectPool mypool;


    public static Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args)
            throws DBException, InterruptedException {


/********* used for daily testing ********************************************/
//        ConcurrentTest contest = new ConcurrentTest();
//        contest.concurrent_run(16);

//        TestCase test = new TestCase();
//        test.run_available_test();
//        test.fetch_performance_test(1000, 10);
//        test.batchPut_performance_test(10);
//        test.clean(1);

//        Connection conn = new Connection(IP_SENSORDB, REDIS_PORT_SENSORDB);
//        conn.connect();
//        conn.dropTables("^ava_test_table_.*");
//        conn.close();

        SensordbClient client = new SensordbClient(IP_SENSORDB, PORT_SENSORDB);
        List<String> tables = client.tables();
        System.out.println("tables: " + +tables.size() + tables);
        client.put_record("new_table_4", "{  \"sensorID\": \"sensor_redis_1\",  \"timestamp\": 1444357887154,  \"x\": 1.1,  \"y\": 2.2,  \"z\": 3.3" + ",  \"values\": {    \"abc\": 110,     \"idsss\": \"id123\"}}");
        List<String> keys=new ArrayList<String>();
        keys.add("abc");
        client.get_json_record("new_table_4", "2010-01-09 02:00:00", "2025-10-09 02:30:00", keys);
        //client.get_json_record("new_table_4", "sensor_redis_1", "2015-10-9 02:00:00", "2015-10-9 02:30:00", keys);

/*****************************************************************************/


/********* used for demo with redis ******************************************
 * Either put_from_redis() or put_from_redis_foreverconn() could be used
 * ***************************************************************************/
//        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);

//        Demo demo = new Demo(IP_SENSORDB, PORT_SENSORDB);

//        demo.put_from_redis(conn, "new_table_" + "4");

//        demo.put_from_redis_foreverconn(conn, "new_table_" + "4");

//        conn.connect();
//        ResultSet result_set = conn.get("new_table_" + "4",
//                "2015-05-15 01:00:00", "2015-05-19 23:00:00");
//        System.out.println("result_set: " + result_set + " size:"
//                + result_set.getSize() + " errorcode:" +
//                result_set.getErrCode());
//        conn.close();

/******************************************************************************/
    }


    public static List<Item> getItems(int num) {
//        Map<String, ByteBuffer> values = new HashMap();
        Date date = new Date();
        Item item = new Item();
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < num; ++i) {
            Map<String, ByteBuffer> values = new HashMap();
            values.put("key_test1" + String.valueOf(i),
                    ByteBuffer.wrap(("value_" + String.valueOf(i)).getBytes()));
            item.sensorID = ByteBuffer.wrap("testsendor".concat("_f_")
                    .concat(String.valueOf(i)).getBytes());
            item.sampledts = date.getTime();
            item.x = (item.sampledts%3+1)+i;
            item.y = (item.sampledts%3+2)+i;
            item.z = (item.sampledts%3+3)+i;
            item.values = values;
            items.add(item);

        }

        return items;
    }

    public static void test_conn_duration()
            throws DBException, InterruptedException {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);

        //        long starttimewhole = System.currentTimeMillis();
//        long duration = System.currentTimeMillis() - starttimewhole;;
//        conn.connect();
//        while(duration<1000000) {
//            Thread.sleep(10000);
//            ResultSet result_set2 = conn.get(NEW_TABLE_PREFIX + "1",
//                    "2015-05-15 01:00:00", "2015-05-15 23:00:00");
//            System.out.println("result_set: " + result_set2 + " size:"
//                    + result_set2.getSize() + " errorcode:" +
//                    result_set2.getErrCode());
//            duration = System.currentTimeMillis() - starttimewhole;
//            logger.info("conn continues: "
//                    + duration/1000 + " s");
//        }
//        conn.close();

        long starttime = System.currentTimeMillis();
        long duration2 = System.currentTimeMillis() - starttime;
        ;
        conn.connect();
        ResultSet result_set2 = conn.get(NEW_TABLE_PREFIX + "1",
                "2015-05-15 01:00:00", "2015-05-15 23:00:00");
        System.out.println("result_set2: " + result_set2 + " size:"
                + result_set2.getSize() + " errorcode:" +
                result_set2.getErrCode());
        while (duration2 < 1000000) {
            Thread.sleep(10000);
            duration2 = System.currentTimeMillis() - starttime;
            logger.info("conn without action continues: "
                    + duration2 / 1000 + " s");
        }
        ResultSet result_set3 = conn.get(NEW_TABLE_PREFIX + "1",
                "2015-05-15 01:00:00", "2015-05-15 23:00:00");
        System.out.println("result_set3: " + result_set3 + " size:"
                + result_set3.getSize() + " errorcode:" +
                result_set3.getErrCode());
        conn.close();

    }


    public static List<SensordbItem> get_sensordb_items(int num) {
//        Map<String, ByteBuffer> values = new HashMap();
        double[] spacexyz = {1.2, 2.3, 3.4};
        String sensorID = "sn";
        Date date = new Date();
        SensordbItem item = new SensordbItem();
        List<SensordbItem> items = new ArrayList<SensordbItem>();
        for (int i = 0; i < num; ++i) {
            Map<String, byte[]> values = new HashMap<String, byte[]>();
            values.put("key_test1" + String.valueOf(i),
                    ("value_" + String.valueOf(i)).getBytes());
            item.sensorID = (sensorID.concat("_e_").concat(String.valueOf(i))
                    .getBytes());
            item.timestamp = date.getTime();
            item.x = spacexyz[0] + i;
            item.y = spacexyz[1] + i;
            item.z = spacexyz[2] + i;
            item.values = values;
            items.add(item);
        }

        return items;
    }


    public void get_env(){
        final Properties p = System.getProperties();
        final Enumeration e = p.keys();
        while (e.hasMoreElements())
        {
            final String prt = (String) e.nextElement();
            final String prtvalue = System.getProperty(prt);
            System.out.println(prt + ":" + prtvalue);
        }
    }
}
