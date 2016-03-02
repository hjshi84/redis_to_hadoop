package com.inesa.redis.connect;

public class Main {
    static String addr="10.200.46.245";
    static int port=7000;

    public static void main(String[] args) {
	// write your code her

    int num=0;
    SensordbSub myssb=new SensordbSub(addr,port);
    while(myssb.listen()){
        myssb.getRead();
        //System.err.print(myssb.getRead()+"\n");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
    myssb.destroy();

    }

}
