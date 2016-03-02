package com.inesa.redis.connect;


import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.lang.System.err;

/**
 * Created by shihj on 5/11/15.
 */
public class RedisClusterSub {
    public JedisCluster jc;
    private Set<HostAndPort> jedisClusterNodes;

    RedisClusterSub(String Redis_Cluster_Addr,int Redis_Cluster_Port){
        try{
            jedisClusterNodes = new HashSet<HostAndPort>();
            HostAndPort hap=new HostAndPort(Redis_Cluster_Addr,Redis_Cluster_Port);
            jedisClusterNodes.add(hap);

        }catch (Exception e)
        {
            err.print(e.toString());
        }
    }

    public int redisClusterConnect(){
        int res=0;
        try{
            jc=new JedisCluster(jedisClusterNodes,500);
        }catch(Exception e)
        {
            err.print(e.toString());
            return res;
        }finally {
            try {
                //test for set/get in cluster
                jc.set("test_redis_cluster_A0FE", "Ok");
                String getval=jc.get("test_redis_cluster_A0FE");
                if ("Ok".equals(getval)) {
                    jc.del("test_redis_cluster_A0FE");
                    res=1;
                } else {
                    err.print("Something wrong happened when set/get the value in cluster\n");
                }
            }catch(Exception e)
            {
                err.print(e.toString());
            }
        }
        return res;
    }

    public void destroy(){
        try {
            jc.close();
            jedisClusterNodes.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
