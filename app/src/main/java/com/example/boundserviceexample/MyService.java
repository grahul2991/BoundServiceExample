package com.example.boundserviceexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();

    private IBinder mBInder=new MyBinder();
    private Handler handler;
    private int mProgress,mMaxValue;
    private Boolean isPaused;

    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler();
        mProgress=0;
        isPaused=true;
        mMaxValue=5000;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBInder;
    }

    public class MyBinder extends Binder{
        MyService getMyService(){
            return MyService.this;
        }
    }

    public void startPretendLongRunningTask(){

        final  Runnable runnable=new Runnable() {
            @Override
            public void run() {

                if(mProgress>=mMaxValue||isPaused){
                    Log.i(TAG,"Run: removing Callback");
                    handler.removeCallbacks(this);
                    pausePretendLongRunningTask();
                }else{
                    Log.i(TAG,"Running: progress "+mProgress);
                    mProgress+=100;
                    handler.postDelayed(this,100);
                }

            }
        };
        handler.postDelayed(runnable,100);
    }

    public void pausePretendLongRunningTask() {
        isPaused=true;
    }

    public void unPausePretendLongRunningTask(){
        isPaused=false;
        startPretendLongRunningTask();
    }

    public int getmProgress() {
        return mProgress;
    }

    public int getmMaxValue() {
        return mMaxValue;
    }

    public void resetTask(){
        mProgress=0;
    }

    public Boolean getPaused() {
        return isPaused;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }
}
