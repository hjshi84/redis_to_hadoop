package com.inesa.redis.connect;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by shihj on 5/11/15.
 */
public class RedisSub {

    private JedisPool pool = null;
    public Jedis jedis = null;

    public RedisSub(String host,int port){
        try {
            pool = new JedisPool(new JedisPoolConfig(), host, port);
            jedis = pool.getResource();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                pool.destroy();
            }
        }
    }

}
