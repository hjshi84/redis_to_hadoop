package com.inesa.sensordb.api.test;

import com.inesa.sensordb.api.SensordbItem;
import org.apache.log4j.Logger;
import org.cesl.sensordb.client.Connection;
import org.cesl.sensordb.client.ResultSet;
import org.cesl.sensordb.core.Item;
import org.cesl.sensordb.exception.DBException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by pc on 15-5-14.
 */
public class TestCase {

    int response_time_limit;
    int row_available_time_limit;
    //1 for lowest, 4 for test for test 3 levels
    int test_pressure;
    int item_num;
    int wait_duration;

    public static String IP_SENSORDB = "122.144.166.103";
    public static int PORT_SENSORDB = 6677;
    public static String NEW_TABLE_PREFIX = "ava_test_table_";
    public Date date_now = new Date();
    public long start_ltime = date_now.getTime();
    public String table_name = NEW_TABLE_PREFIX + start_ltime;

    public static Logger logger = Logger.getLogger(TestCase.class);

    public TestCase(){
        response_time_limit = 50;
        test_pressure = 1;
        item_num = 10;
        wait_duration = 3000;
    }

    TestCase(int wait_duration, float pressure){
        item_num = (int)(100 * pressure);
        wait_duration = 3000 / wait_duration;
    }

    TestCase(int resp_time, int pressure){
        response_time_limit = resp_time;
        test_pressure = pressure;
    }

    public void run_available_test() throws InterruptedException {
        response_time_limit = 50;
        test_pressure = 1;
        item_num = 10;
        wait_duration = 1000;
        TestResult result = new TestResult();

        test_list_tables();
        Thread.sleep(wait_duration);

        test_create_table();
        Thread.sleep(wait_duration);

//        test_list_tables();
//        Thread.sleep(1000);

        test_put();
        Thread.sleep(wait_duration);

        test_batchPut();
        Thread.sleep(wait_duration * 2);

        test_scan();
        Thread.sleep(wait_duration);

        test_fetch();
        Thread.sleep(wait_duration);

        test_drop_table();
        Thread.sleep(wait_duration);

        result = test_list_tables();
        for (String tbl : result.tables){
            if (tbl == table_name){
                System.err.println("test table did not drop successfully!");
                Thread.sleep(wait_duration*3);
                clean(0);
            }
        }
    }

    public void fetch_performance_test(int num, int round) throws InterruptedException {
//        test_list_tables();
//        Thread.sleep(wait_duration);

        response_time_limit = 50;
        test_pressure = 1;
        item_num = num;
        wait_duration = 3000;
        Map<String, Long> test_result = new HashMap<>();
        TestResult result = new TestResult();


        test_create_table();
        Thread.sleep(wait_duration);

//        test_list_tables();
//        Thread.sleep(1000);

        test_put();
        Thread.sleep(wait_duration);

        test_batchPut();
        Thread.sleep(wait_duration * 2);

//        test_scan();
//        Thread.sleep(wait_duration);

        long fetchtime = 0;
        for (int i=0;i<round;++i){
            result = test_fetch();
            fetchtime += result.time_spent;
            Thread.sleep(wait_duration);
        }

        test_result.put("fetchtime", fetchtime / round);
        test_result.put("num_item", result.num_items);

        test_drop_table();
        Thread.sleep(wait_duration);

        logger.info("test_result: " + test_result);
//        test_list_tables();
    }

    public void batchPut_performance_test(int num, int round) throws InterruptedException {
//        test_list_tables();
//        Thread.sleep(wait_duration);

        response_time_limit = 50;
        test_pressure = 1;
        item_num = num;
        wait_duration = 3000;
        Map<String, Long> test_result = new HashMap<>();

        test_create_table();
        Thread.sleep(wait_duration);

//        test_list_tables();
//        Thread.sleep(1000);

//        test_put();
//        Thread.sleep(wait_duration);

        long fetchtime = 0;
        for (int i=0;i<round;++i){
            fetchtime += test_batchPut();
            Thread.sleep(wait_duration*2);
        }
        test_result.put("batchPuttime", fetchtime / round);



//        test_scan();
//        Thread.sleep(wait_duration);

//        test_fetch();
//        Thread.sleep(wait_duration);


        test_drop_table();
        Thread.sleep(wait_duration);

        logger.info("test_result: " + test_result);
//        test_list_tables();
    }


    public void clean(int all){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            conn.connect();
            if(all==0)
                conn.dropTables("^"+table_name+".*");
            else if(all==1)
                conn.dropTables("^"+NEW_TABLE_PREFIX+".*");
        } catch (DBException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }


    TestResult test_list_tables(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        List<String> table_list = new ArrayList<String>();
        long time_spent = 0;
        TestResult result = new TestResult();
        try {
            long starttimewhole = System.currentTimeMillis();
            conn.connect();
            table_list = conn.listTableNames();
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_list_tables: " + table_list.size() +
                    " tables in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
            result.time_spent = endtimewhole - starttimewhole;
            result.tables = table_list;
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
        return result;
    }

    void test_create_table(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            long starttimewhole = System.currentTimeMillis();
            conn.connect();
            conn.createTable(table_name);
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_create_table: " + table_name +
                    " in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    void test_drop_table(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        try {
            long starttimewhole = System.currentTimeMillis();
            conn.connect();
            conn.dropTable(table_name);
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_drop_table: " + table_name +
                    " in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
    }


    long test_put(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        List<String> table_list = new ArrayList<String>();
        Utils utils = new Utils();
        long time_spent = 0;
        try {
            List<SensordbItem> items = utils.get_sensordb_items(item_num);
            long starttimewhole = System.currentTimeMillis();
            conn.connect();
            for (SensordbItem item : items){
                conn.put(table_name, item.sensorID,
                        item.timestamp,
                        item.x, item.y, item.z, item.values);
            }
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_put: " + items.size() +
                    " items in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
            time_spent = endtimewhole - starttimewhole;
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
        return time_spent;
    }

    public long test_batchPut() {
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        Utils utils = new Utils();
        long time_spent = 0;

        try {
            conn.connect();
//            long starttime = System.currentTimeMillis();
//            System.out.println("start time: " + starttime);
                long starttimewhole = System.currentTimeMillis();

                List<Item> items = utils.get_batch_items(item_num * 99);
//                System.out.println("items: " + items);
                conn.batchPut(table_name, items);
//                System.out.println("batchPut status: " + status);

                long endtimewhole = System.currentTimeMillis();
                logger.info("batchPut: " + items.size() +
                        " items in 1 conn: "
                        + (endtimewhole - starttimewhole) + " ms");
                time_spent = endtimewhole - starttimewhole;

//            pro_time[i] = System.currentTimeMillis() - starttime;
//            System.out.println("pro_time time: " + pro_time);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return time_spent;

    }

    long test_scan(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String starttime = sdf.format(start_ltime-30000000);
        String endtime = sdf.format(start_ltime+300000);
//        System.out.println("st: "+starttime+" end:"+endtime);
        long time_spent = 0;
        try {
            long starttimewhole = System.currentTimeMillis();
            conn.connect();
            ResultSet result_set = conn.get(table_name,
                    starttime, endtime);
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_scan: " + result_set.getSize() +
                    " items in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
            time_spent = endtimewhole - starttimewhole;
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
        return time_spent;
    }

    TestResult test_fetch(){
        Connection conn = new Connection(IP_SENSORDB, PORT_SENSORDB);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String starttime = sdf.format(start_ltime-30000000);
        String endtime = sdf.format(start_ltime+600000);
        TestResult result = new TestResult();
//        System.out.println("st: "+starttime+" end:"+endtime);
        long time_spent = 0;
        try {
            conn.connect();
            ResultSet result_set = conn.get(table_name,
                    starttime, endtime);
            long starttimewhole = System.currentTimeMillis();
            while (result_set.next()) {}
            long endtimewhole = System.currentTimeMillis();
            logger.info("test_fetch: " + result_set.getSize() +
                    " items in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");
            result.time_spent = endtimewhole - starttimewhole;
            result.num_items = result_set.getSize();
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            conn.close();
        }
        return result;
    }

    class TestResult{
        long time_spent;
        List<String> tables = new ArrayList<>();
        long num_items;
    }
}
