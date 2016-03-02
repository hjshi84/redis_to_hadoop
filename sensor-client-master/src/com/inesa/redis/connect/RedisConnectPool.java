package com.inesa.redis.connect;

import java.util.ArrayList;


/**
 * Created by shihj on 5/11/15.
 */
public class RedisConnectPool {
    private static String Addr;
    private static int Port;
    private static int maxConnectLink;
    private static ArrayList<RedisClusterSub> usedLink;
    private static ArrayList<RedisClusterSub> freeLink;
    private static int actualCapacity;

    public RedisConnectPool(int maxConnect,String Redis_Cluster_Addr,int Redis_Cluster_Port){
        maxConnectLink=maxConnect;
        usedLink=new ArrayList<RedisClusterSub>();
        freeLink=new ArrayList<RedisClusterSub>();
        Addr=Redis_Cluster_Addr;
        Port=Redis_Cluster_Port;

    }

    public int createPool(){
        if (usedLink.size()>0||freeLink.size()>0)
            return 1;
        try {
            actualCapacity=0;
            for (int i = 0; i < maxConnectLink; i++) {
                RedisClusterSub tempSub = new RedisClusterSub(Addr, Port);
                if (tempSub.redisClusterConnect()==1) {
                    freeLink.add(tempSub);
                    actualCapacity++;
                }
                else {
                    System.err.print("Connect to Redis Cluster Error, Please Check!\n");
                    return 0;
                }
            }
            return 1;
        }catch (Exception e)
        {

            return 0;
        }
    }

    public RedisClusterSub getRedisCLusterSub(){
        if(freeLink.isEmpty()){
            return null;
        }
        else{
            RedisClusterSub res=freeLink.get(0);
            usedLink.add(res);
            freeLink.remove(0);
            return res;
        }
    }

    public int returnCLusterSub(RedisClusterSub retval){
        int usedIndex;
        if ((usedIndex=usedLink.indexOf(retval))!=-1){
            freeLink.add(retval);
            usedLink.remove(usedIndex);
            return 1;
        }else{
            return 0;
        }
    }

    public int dumpPool(int maxLink,String Redis_Cluster_Addr,int Redis_Cluster_Port){
        try {
            if ((freeLink == null) || (usedLink == null)) {
                freeLink = new ArrayList<RedisClusterSub>();
                usedLink = new ArrayList<RedisClusterSub>();
            }
            if (freeLink.isEmpty() && usedLink.isEmpty()) {
                maxConnectLink = maxLink;
            }
            for (RedisClusterSub rcb : usedLink) {
                rcb.destroy();
            }
            for (RedisClusterSub rcb : freeLink) {
                rcb.destroy();
            }
            usedLink.clear();
            freeLink.clear();
            maxConnectLink = maxLink;
            Addr = Redis_Cluster_Addr;
            Port = Redis_Cluster_Port;
            createPool();
            return 1;
        }catch (Exception e){
            System.err.print(e.toString());
            return 0;
        }
    }
}
