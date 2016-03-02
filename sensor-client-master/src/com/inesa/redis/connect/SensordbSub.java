package com.inesa.redis.connect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.err;

/**
 * Created by shihj on 5/11/15.
 */
public class SensordbSub implements SensordbSubThread.ICallback{
    public static RedisConnectPool mypool;

    private Lock lockBuf1=new ReentrantLock();
    public final static ArrayList<String> readbuf1=new ArrayList<String>(100);

    static int max=10;
    static String addr="10.200.46.245";
    static int port=7000;

    private RedisClusterSub clusterSub;
    private RedisSub sub;
    private static LinkedList<SensordbSubThread> sdbst;
    private static volatile boolean message_comming=false;

    public SensordbSub(String redisAddr, int redisPort){
        max=1;
        addr=redisAddr;
        port=redisPort;
        mypool=new RedisConnectPool(1,addr,port);
        if (mypool.createPool()==0) {
            err.print("CreatePool failed, try normal Redis\n");
            sub=new RedisSub(redisAddr,redisPort);
            clusterSub=null;
        }
        else {
            clusterSub = mypool.getRedisCLusterSub();
            sub=null;
        }
    }

    public SensordbSub(int maxlink,String redisAddr, int redisPort){
        max=maxlink;
        addr=redisAddr;
        port=redisPort;
        mypool=new RedisConnectPool(max,addr,port);
        if (mypool.createPool()==0) {
            err.print("CreatePool failed, try normal Redis\n");
            sub=new RedisSub(redisAddr,redisPort);
            clusterSub=null;
        }
        else {
            clusterSub = mypool.getRedisCLusterSub();
            sub=null;
        }
    }

    public boolean listen(){
        if(sub==null&&clusterSub==null) {
            if (mypool.createPool()==0) {
                err.print("CreatePool failed, try normal Redis\n");
                sub=new RedisSub(addr,port);
                clusterSub=null;
            }
            else {
                clusterSub = mypool.getRedisCLusterSub();
                sub=null;
            }
            if (sub==null&&clusterSub==null)
                return false;
        }
        if(sdbst==null){
            sdbst=new LinkedList<SensordbSubThread>();
            SensordbSubThread temp1;
            if (sub==null){
                temp1=new SensordbSubThread(clusterSub,this);
                temp1.start();
            }
            else if(clusterSub==null) {
                temp1 = new SensordbSubThread(sub,this);
                temp1.start();
            }
            else
                temp1=null;
            sdbst.add(temp1);

        }

        while(!message_comming)
        {

        }

        return true;
    }

    public ArrayList<String> getRead(){
        ArrayList<String> retVal=new ArrayList<String>();
        lockBuf1.lock();
        retVal.addAll(readbuf1);
        readbuf1.clear();
        lockBuf1.unlock();

        message_comming = false;
        return retVal;
    }

    public int destroy(){
        if(sdbst!=null&&sdbst.size()>0)
        {
            for(SensordbSubThread sst :sdbst){
                sst.kill();
            }
            sdbst.clear();
        }
        sdbst=null;
        while(!lockBuf1.tryLock()){
            readbuf1.clear();
            lockBuf1.unlock();
        }
        return 1;
    }

    public void messageCome(String message,String channel) {

        lockBuf1.lock();
        readbuf1.add(message);
        lockBuf1.unlock();
        message_comming=true;
    }

}
