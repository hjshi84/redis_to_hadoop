package com.inesa.redis.connect;

import redis.clients.jedis.JedisPubSub;

import java.util.Calendar;

/**
 * Created by shihj on 5/11/15.
 */
public class SensordbSubThread extends Thread {

    public interface ICallback {
        void messageCome(String message, String channel);
    }

    private volatile boolean kills;
    private RedisConn myconn;
    private ICallback icallback;

    private Long caltime = 0l;
    private int num = 0;

    private class RedisConn {
        int type;
        RedisSub sub;//type=0
        RedisClusterSub clusterSub;//type=1;

        RedisConn(RedisSub _sub) {
            type = 0;
            sub = _sub;
        }

        RedisConn(RedisClusterSub _sub) {
            type = 1;
            clusterSub = _sub;
        }
    }

    public final static String connectChannel = "sensorDB";

    SensordbSubThread(RedisSub _sub, ICallback icalbak) {
        kills = false;
        myconn = new RedisConn(_sub);
        icallback = icalbak;
    }

    SensordbSubThread(RedisClusterSub mysub, ICallback icalbak) {
        kills = false;
        myconn = new RedisConn(mysub);
        icallback = icalbak;
    }

    public void kill() {
        kills = true;
    }

    public void run() {
        while (true) {
            try {
                if (myconn.type == 0) {
                    myconn.sub.jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            super.onMessage(channel, message);
                            num++;
                            if (caltime == 0l)
                                caltime = Calendar.getInstance().getTimeInMillis();
                            if (num > 10000) {
                                caltime = Calendar.getInstance().getTimeInMillis() - caltime;
                                System.err.print("Time eclipse is about(10000) :\n" + caltime + "\n");
                                caltime = 0l;
                                num = 0;
                            }

                            icallback.messageCome(message, channel);

                        }
                    }, connectChannel);
                } else {
                    myconn.clusterSub.jc.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            super.onMessage(channel, message);

                            num++;
                            if (caltime == 0l)
                                caltime = Calendar.getInstance().getTimeInMillis();
                            if (num > 10000) {
                                caltime = Calendar.getInstance().getTimeInMillis() - caltime;
                                System.err.print("Time eclipse is about(10000) :\n" + caltime + "\n");
                                caltime = 0l;
                                num = 0;
                            }

                            icallback.messageCome(message, channel);
                        }
                    }, connectChannel);
                }

            } catch (Exception e) {
                System.err.print(e.toString());
                break;
            }
            if (kills) {
                break;
            }
        }
    }
}
