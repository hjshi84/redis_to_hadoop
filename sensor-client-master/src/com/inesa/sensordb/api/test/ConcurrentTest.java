package com.inesa.sensordb.api.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by pc on 15-6-1.
 */
public class ConcurrentTest {

    public void concurrent_run(int num) {
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(num);
        List<Double> runningtime = new ArrayList<Double>();

        for (int i = 0; i < num; i++) {
            new Thread(new MyWorker(i, begin, end)).start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("start...");
        begin.countDown();
        long startTime = System.currentTimeMillis();

        try {
            end.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println(" finish");
            System.out.println("costs: " + (endTime - startTime));
        }

    }

    class MyWorker implements Runnable {
        final CountDownLatch begin;
        final CountDownLatch end;
        final int id;
        double[] time_multi_put;


        public MyWorker(final int id, final CountDownLatch begin,
                        final CountDownLatch end) {
            this.id = id;
            this.begin = begin;
            this.end = end;

        }

        @Override
        public void run() {
            double pro_time = 0;
            try {

                System.out.println(this.id + " ready");
                begin.await();
                // execute your logic
                Random rand1 = new Random();
                int random1 = rand1.nextInt(100);
                Thread.sleep(random1 * 10);
                System.out.println(this.id + " start");
                double starttime = System.currentTimeMillis();
                TestCase test = new TestCase();
                test.run_available_test();
                //                sample_values.put("key3", "value5".getBytes());
//                double starttime = System.currentTimeMillis();
//                for(int i=0;i<1000;++i)
//                    client.put_singledata(sample_values);
                pro_time = System.currentTimeMillis() - starttime;

//                Thread.sleep((long) (Math.random() * 1000));
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                System.out.println(this.id + " done");
//                timecsv[id] = pro_time;
                end.countDown();
            }
        }
    }
}
