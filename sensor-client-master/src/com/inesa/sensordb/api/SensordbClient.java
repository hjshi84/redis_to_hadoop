package com.inesa.sensordb.api;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.cesl.sensordb.client.Connection;
import org.cesl.sensordb.client.ResultSet;
import org.cesl.sensordb.exception.DBException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pc on 15/5/12.
 */
public class SensordbClient implements ClientInterface {
    //    private String IP_SENSORDB;
//    private int PORT_SENSORDB;
    public Connection conn;
    private List<String> table_list;
    public static Logger logger = Logger.getLogger(SensordbClient.class);

    SensordbClient(Connection conn) {
//        this.IP_SENSORDB = IP_SENSORDB;
//        this.int = PORT_SENSORDB;
        this.conn = conn;

    }

    SensordbClient(String IP_SENSORDB, int PORT_SENSORDB) {
//        this.IP_SENSORDB = IP_SENSORDB;
//        this.int = PORT_SENSORDB;
        this.conn = new Connection(IP_SENSORDB, PORT_SENSORDB);

    }

    public void connect() throws DBException {
        this.conn.connect();
    }

    public void close() throws DBException {
        this.conn.close();
    }
//    @Override
//    public void close() throws Exception{
//        System.out.println("close()...");
//    }
//
//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//    }

    @Override
    public int create_table(String table_name) {
        try {
            this.conn.connect();
            this.conn.createTable(table_name);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
//            return 0;
        } finally {
            System.out.println("finally create_table:[" + table_name + "]...");
            refresh_table_list();
            conn.close();
        }
        return 1;
    }

    @Override
    public List<String> tables() {
        refresh_table_list();
        return this.table_list;
    }

    private void refresh_table_list() {
        List<String> table_list = new ArrayList<String>();
        try {
            this.conn.connect();
            this.table_list = conn.listTableNames();
//            System.out.println("table_list: " + table_list);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
//            conn.dropTable(table_test_name);
//            System.out.println("dropTable: " + table_test_name);
            System.out.println("finally refresh_table_list...");
            conn.close();
        }
//        return table_list;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public int delete_table(String table_name) {
        try {
            this.conn.connect();
            this.conn.dropTable(table_name);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("finally delete_table: [" + table_name + "] ...");
            refresh_table_list();
            conn.close();
        }
        return 1;
    }

    @Override
    public int delete_tables() {
        return 0;
    }

    @Override
    public int long_put(String table_name, List<String> json_str_list) {
        int status = 0;
        this.used.set(1);

        if (connected.intValue() == 0) {
            try {
                this.conn.connect();
                this.connected.set(1);
    //            this.used.set(1);
                connected_num.addAndGet(1);
                logger.info("long put connect(), set used to 1, " +
                        "connected_num-closed_num: " + connected_num + "-"
                        + closed_num);
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
//        this.conn.connect();

        for (String str_in : json_str_list){
            System.out.println("redis str_in: " + str_in);
            //     put_sensordb(str_in);
            SensordbItem item = new SensordbItem(str_in);
            conn.put(table_name, item.sensorID,
                    item.timestamp,
                    item.x, item.y, item.z, item.values);
//            ++receive_cnt;
        }
        this.used.set(0);
        logger.info("long put set used to 0");
        return status;
    }


    public int long_put_items(String table_name, List<SensordbItem> item_list)
            throws DBException {
//        Map<String, byte[]> values_map = new HashMap<String, byte[]>();
//        JsonConvertor jsonconv = new JsonConvertor();
        int status = 0;
//        SensordbItem item = new SensordbItem(json_value);

//        this.used.set(1);
//
//        if (connected.intValue() == 0) {
//            this.conn.connect();
//            this.connected.set(1);
////            this.used.set(1);
//            connected_num.addAndGet(1);
//            logger.info("long put connect(), set used to 1, " +
//                    "connected_num-closed_num: "+connected_num+"-"+closed_num);
//        }

//        this.conn.connect();

        for (SensordbItem item : item_list){
//            System.out.println("redis str_in: " + str_in);
            //     put_sensordb(str_in);
//            SensordbItem item = new SensordbItem(str_in);
            conn.put(table_name, item.sensorID,
                    item.timestamp,
                    item.x, item.y, item.z, item.values);
//            ++receive_cnt;
        }
//        this.used.set(0);
//        logger.info("long put set used to 0");
        return status;
    }


    @Override
    public int put_record(String table_name, String json_value) {
        int status = 0;
        SensordbItem item = new SensordbItem(json_value);

        try {
            this.conn.connect();
            status = conn.put(table_name, item.sensorID, item.timestamp,
                    item.x, item.y, item.z, item.values);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
//            conn.dropTable(table_test_name);
//            System.out.println("dropTable: " + table_test_name);
            conn.close();
        }
        return status;
    }


    //TODO
    public int put_record_safe(String table_name, String json_value) {
        int status = 0;
        SensordbItem item = new SensordbItem(json_value);
        int put_status = put_record(table_name, json_value);

        try {
            this.conn.connect();
//            status = conn.get(table_name, item.sensorID, item.timestamp,
//                    item.x, item.y, item.z, item.values);
        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
//            conn.dropTable(table_test_name);
//            System.out.println("dropTable: " + table_test_name);
            conn.close();
        }
        return status;
    }

    @Override
    public int put_records(String table_name) {
        return 0;
    }

    @Override
    public int delete_record() {
        return 0;
    }

    @Override
    public int delete_records() {
        return 0;
    }




    @Override
    public List<String> get_json_record(String table_name, String sensorID,
                                  String starttime, String endtime,
                                  List<String> valuekeys) {
        String jsonstr = " ";
        List<String> json_list = new ArrayList<>();
        Gson gson = new Gson();
//        SensordbItem item = new SensordbItem();
        ResultItem item = new ResultItem();
//        ResultSet result_set = new ResultSet();
        try {
            this.conn.connect();
            ResultSet result_set = this.conn.get(table_name,
                    sensorID.getBytes(), starttime, endtime);
            System.out.println("result_set: " + result_set + " size:"
                    + result_set.getSize() + " errorcode:" +
                    result_set.getErrCode());

            while (result_set.next()) {
                result_set.getString("id");
                item.id = result_set.getString("id");
                item.ts = result_set.getLong("ts");
                item.x = result_set.getDouble("x");
                item.y = result_set.getDouble("y");
                item.z = result_set.getDouble("z");
//                String tst = result_set.getString("word_separators");
                for (int i=0; i<valuekeys.size(); ++i){
                    item.values.put(valuekeys.get(i),
                            result_set.getString(valuekeys.get(i)));
                }
                System.out.println("item - sensorID:" + item.id + " timestamp:"
                        + item.ts/*+
                        " tst:"+tst+" x:"+item.x+" y:"+item.y+" z:"+item.z*/
                        +" values: "+item.values);
                jsonstr = gson.toJson(item);
                json_list.add(jsonstr);
            }

        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("get_json_record finally");
            conn.close();
        }

        return json_list;
    }


    @Override
    public List<String> get_json_record(String table_name,
                                        String starttime, String endtime,
                                        List<String> valuekeys) {
        String jsonstr = " ";
        List<String> json_list = new ArrayList<>();
        Gson gson = new Gson();
//        SensordbItem item = new SensordbItem();
        ResultItem item = new ResultItem();
//        ResultSet result_set = new ResultSet();
        try {
            this.conn.connect();
            ResultSet result_set = this.conn.get(table_name,
                    starttime, endtime);
            System.out.println("result_set: " + result_set + " size:"
                    + result_set.getSize() + " errorcode:" +
                    result_set.getErrCode());

            long starttimewhole = System.currentTimeMillis();
            while (result_set.next()) {


                item.id = result_set.getString("id");
                item.ts = result_set.getLong("ts");
                item.x = result_set.getDouble("x");
                item.y = result_set.getDouble("y");
                item.z = result_set.getDouble("z");
                for (int i=0; i<valuekeys.size(); ++i){
                    item.values.put(valuekeys.get(i),
                            result_set.getString(valuekeys.get(i)));

                }
                if (item.id=="sensor_redis_1"){
                System.out.println("item - sensorID: " + item.id + "  timestamp: "
                        + item.ts/*+
                        " tst:"+tst+" x:"+item.x+" y:"+item.y+" z:"+item.z*/
                        + "  name: " + result_set.getString("name") + "  state: " + result_set.getString("state"));}
//                jsonstr = gson.toJson(item);
//                json_list.add(jsonstr);
            }
            long endtimewhole = System.currentTimeMillis();
            logger.info("fetch " + result_set.getSize() + " items in 1 conn: "
                    + (endtimewhole - starttimewhole) + " ms");

        } catch (DBException e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            System.out.println("get_json_record finally");
            conn.close();
        }

        return json_list;
    }


    @Override
    public String get_records() {
        return null;
    }


    private AtomicInteger connected = new AtomicInteger(0);
    private AtomicInteger used = new AtomicInteger(0);
    private AtomicInteger connected_num = new AtomicInteger(0);
    private AtomicInteger closed_num = new AtomicInteger(0);
    private InputStream inStream;
    private OutputStream outStream;
    private Process process;
    private boolean abortCondition = false;
    private int watchDogTSleepTime = 10000; //3 sek
    private int no_use_timer = 10;
    public void start_conn_manager(final Connection conn) {
        Runnable r = new Runnable() {
            int timer = 0;
            boolean no_use = true;
            @Override
            public void run() {
                while(!abortCondition){
                    try {
                        Thread.sleep(watchDogTSleepTime);
                        if (connected.intValue() == 0) {
                            conn.connect();
                            connected.set(1);
                            connected_num.addAndGet(1);
                            logger.info("conn_manager connect(), " + "connected_num" +
                                    "-closed_num: " + connected_num + "-" + closed_num);
                            timer = 0;
                        }
                        ++timer;

                        if (used.intValue() == 1) {
                            used.set(0);
                            if (no_use == true)
                                no_use = false;
                            Thread.sleep(watchDogTSleepTime);
//                            ++timer;
//                            logger.info("timer: "+timer);

                            if (used.intValue() == 0 & connected.intValue() == 1) {
                                connected.set(0);
                                conn.close();
                                closed_num.addAndGet(1);
                                logger.info("conn_manager close(), " +
                                        "connected_num-closed_num: " + connected_num
                                        + "-" + closed_num);
                                timer = 0;
                            }
                        }

                        if (no_use == true & timer > no_use_timer) {
                            if (used.intValue() == 0 & connected.intValue() == 1) {
                                connected.set(0);
                                conn.close();
                                closed_num.addAndGet(1);
                                logger.info("conn_manager close(), " + no_use_timer*watchDogTSleepTime +
                                        "ms no action from start, connected_num-closed_num: " + connected_num
                                        + "-" + closed_num);
                            }
                            timer = 0;
                        }
//                        Thread.sleep(watchDogTSleepTime);
//                        if (timer>5) {
//                            if (used.intValue() == 0 & connected.intValue() == 1) {
//                                connected.set(0);
//                                conn.close();
//                                closed_num.addAndGet(1);
//                                logger.info("watchdog close(), timer > 10 " +
//                                        "connected_num-closed_num: " + connected_num
//                                        + "-" + closed_num);
//                                timer = 0;
//                            }
//
//                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (DBException e) {
                        e.printStackTrace();
                    }

                    //if you want, you might try to kill the process
//                    process.destroy();
                }
            }
        };

        Thread watchDog = new Thread(r);
        watchDog.start();
        //watchDog.setDaemon(true); //maybe
    }
}
