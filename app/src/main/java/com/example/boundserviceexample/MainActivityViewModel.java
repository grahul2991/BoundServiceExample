package com.example.boundserviceexample;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.IBinder;
import android.util.Log;

public class MainActivityViewModel extends ViewModel {

    private static final String TAG=MainActivityViewModel.class.getSimpleName();

    private MutableLiveData<Boolean> mIsProgressUpdating=new MutableLiveData<>();
    private MutableLiveData<MyService.MyBinder> myBinderMutableLiveData=new MutableLiveData<>();


    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
      Log.i(TAG,"On Service Connected");
      MyService.MyBinder myBinder= (MyService.MyBinder) service;
      myBinderMutableLiveData.postValue(myBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
myBinderMutableLiveData.postValue(null);
        }
    };

    public LiveData<Boolean> getIsProgressUpdating(){
        return mIsProgressUpdating;
    }

    public LiveData<MyService.MyBinder> getBinder(){
        return myBinderMutableLiveData;
    }

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public void setIsUpdating(Boolean isUpdating){
        mIsProgressUpdating.postValue(isUpdating);
    }
}
