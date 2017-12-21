package com.yiqi.choose.factory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by moumou on 16/11/30.
 */
public class ThreadPollFactory {
    static ExecutorService mNormalExecutorSerVice;
    static ExecutorService mDownLoadExecutorService;
    static ExecutorService mSingleExecutorService;

    public static ExecutorService getNormalPool(){
        if(mNormalExecutorSerVice==null){
            synchronized (ThreadPollFactory.class){
                if(mNormalExecutorSerVice==null){

                    mNormalExecutorSerVice= Executors.newFixedThreadPool(5);
                }
            }
        }
        return mNormalExecutorSerVice;
    }


    public static void canclePool(){
        Callable<String> s = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "true";
            }
        };

        Future<String> f = mNormalExecutorSerVice.submit( s);

        f.cancel(true);
    }
    public static ExecutorService getDownPool(){
        if(mDownLoadExecutorService==null){
            synchronized (ThreadPollFactory.class){
                if(mDownLoadExecutorService==null){
                    mDownLoadExecutorService= Executors.newFixedThreadPool(3);
                }
            }
        }
        return mDownLoadExecutorService;
    }
    public static ExecutorService getSinglePool(){
        if(mSingleExecutorService==null){
            synchronized (ThreadPollFactory.class){
                if(mSingleExecutorService==null){
                    mSingleExecutorService= Executors.newSingleThreadExecutor();
                }
            }
        }
        return mSingleExecutorService;
    }
}
