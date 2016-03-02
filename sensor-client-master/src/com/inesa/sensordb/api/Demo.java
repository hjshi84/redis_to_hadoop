package com.inesa.sensordb.api;

import com.inesa.redis.connect.RedisConnectPool;
import com.inesa.redis.connect.SensordbSub;
import org.apache.log4j.Logger;
import org.cesl.sensordb.client.Connection;
import org.cesl.sensordb.exception.DBException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 15-7-17.
 */
public class Demo {

    public static String redis_ip;
    public static int redis_port;
    public static Logger logger = Logger.getLogger(Main.class);


    Demo(String ip, int port) {
        this.redis_ip = ip;
        this.redis_port = port;
    }

    public void put_from_redis(Connection conn, String tablename)
            throws InterruptedException, DBException {
        List<String> list_in = new ArrayList<>();
        SensordbClient sensordb = new SensordbClient(conn);
        int cnt = 0;
        long receive_cnt = 0;

        try {
            conn.connect();
//            sensordb.connect();
            sensordb.start_conn_manager(sensordb.conn);
            SensordbSub myssb = new SensordbSub(this.redis_ip, this.redis_port);
            while (myssb.listen()) {
//                Thread.sleep(500);
                list_in = myssb.getRead();
                if (list_in.size() > 0) {
                    long starttimewhole = System.currentTimeMillis();

                    sensordb.long_put(tablename, list_in);

//                    for (String str_in : list_in){
//                        System.out.println("redis str_in: " + str_in);
//                        //     put_sensordb(str_in);
//                        SensordbItem item = new SensordbItem(str_in);
//                        conn.put(NEW_TABLE_PREFIX + "2", item.sensorID,
//                                item.timestamp,
//                                item.x, item.y, item.z, item.values);
//                        ++receive_cnt;
//                    }
                    long endtimewhole = System.currentTimeMillis();
                    logger.info("from redis 1 list: " + list_in.size()
                            + " item in 1 conn: "
                            + (endtimewhole - starttimewhole) + " ms");
                    list_in.clear();
//                ++cnt;
//                if(cnt>=10)
//                    break;
                }
                logger.info("from redis all:" + receive_cnt);
            }
//        } catch (DBException e) {
//            e.printStackTrace();
//            System.exit(-1);
        } finally {
            System.out.println("put_from_redis finally");
            conn.close();
//            sensordb.close();
        }
    }


    public void put_from_redis_foreverconn(Connection conn,
                                                  String tablename)
            throws InterruptedException, DBException {

        List<String> list_in = new ArrayList<>();
        SensordbClient sensordb = new SensordbClient(conn);
        long receive_cnt = 0;

        try {
            conn.connect();
            sensordb.start_conn_manager(sensordb.conn);
            SensordbSub myssb = new SensordbSub(this.redis_ip, this.redis_port);
            while (myssb.listen()) {
//                Thread.sleep(500);
                list_in = myssb.getRead();
                if (list_in.size() > 0) {
                    long starttimewhole = System.currentTimeMillis();

                    for (String str_in : list_in) {
                        System.out.println("redis str_in: " + str_in);
                        //     put_sensordb(str_in);
                        SensordbItem item = new SensordbItem(str_in);
                        conn.put(tablename, item.sensorID,
                                item.timestamp,
                                item.x, item.y, item.z, item.values);
                        ++receive_cnt;
                    }

                    long endtimewhole = System.currentTimeMillis();
                    logger.info("from redis 1 list: " + list_in.size()
                            + " item in 1 conn: "
                            + (endtimewhole - starttimewhole) + " ms");
                    list_in.clear();
//                ++cnt;
//                if(cnt>=10)
//                    break;
                }
                logger.info("from redis all:" + receive_cnt);
            }
        } finally {
            System.out.println("put_from_redis_foreverconn finally");
            conn.close();
        }
    }

}
