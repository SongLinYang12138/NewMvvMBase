package com.bondex.photo.test;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * date: 2020/4/28
 *
 * @Author: ysl
 * description:
 */
public class TestThread {

    private ThreadPoolExecutor executor;
    public BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
    public ThreadFactory factory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {

            System.out.println("factory " + r.toString());
            Thread thread = new Thread(r, "new-thread ");

            return thread;
        }
    };
    public RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        }
    };

    public TestThread() {

        executor = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, workQueue, factory,handler);
    }

    int myTag = 1;

    public void testThread() {

        DecodeRunable decodeRunable = new DecodeRunable();
        decodeRunable.setTag(myTag);
        ++myTag;

        if(executor.getPoolSize() < executor.getMaximumPoolSize()){
            executor.execute(decodeRunable);
        }else if(workQueue.size() < 5){
            workQueue.add(decodeRunable);
        }

    }

    private class DecodeRunable implements Runnable {
        private int tag;

        public void setTag(int tag) {
            this.tag = tag;
        }


        @Override
        public void run() {
            System.out.println("thread  " + tag);
        }
    }

}
