package com.example.boundserviceexample;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //UI component
    ProgressBar progressBar;
    TextView textView;
    Button btn;

    //Vars
    MyService myService;
    MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.pb);
        btn=findViewById(R.id.btn);
        textView=findViewById(R.id.tv_percent);

        mainActivityViewModel= ViewModelProviders.of(this).get(MainActivityViewModel.class);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
toggleUpdate();
            }
        });

        mainActivityViewModel.getBinder().observe(this, new Observer<MyService.MyBinder>() {
            @Override
            public void onChanged(@Nullable MyService.MyBinder myBinder) {
                if(myBinder!=null){
                    myService=myBinder.getMyService();
                }else{
                    myService=null;
                }
            }
        });

        mainActivityViewModel.getIsProgressUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean aBoolean) {

                final Handler handler=new Handler();
                final Runnable runnable=new Runnable() {
                    @Override
                    public void run() {

                        if(aBoolean){
                            if(mainActivityViewModel.getBinder().getValue()!=null){
                                if(myService.getmProgress()==myService.getmMaxValue()){
                                    mainActivityViewModel.setIsUpdating(false);
                                }
                                progressBar.setProgress(myService.getmProgress());
                                progressBar.setMax(myService.getmMaxValue());
                                String progress=String.valueOf(100* myService.getmProgress()/myService.getmMaxValue()+"%");
                                textView.setText(progress);
                                handler.postDelayed(this,100);
                            }
                        }else{
handler.removeCallbacks(this);
                        }
                    }
                };

                if(aBoolean){
                    btn.setText("Pause");
                    handler.postDelayed(runnable,100);
                }else{

                    if(myService.getmProgress()>=myService.getmMaxValue()){
                        btn.setText("Restart");
                    }else{
                        btn.setText("Start");
                    }
                }
            }
        });
    }

    private void toggleUpdate() {
        if(myService!=null){
            if(myService.getmProgress()>=myService.getmMaxValue()){
                myService.resetTask();
                btn.setText("Start");
            }else{
                if(myService.getPaused()){
myService.unPausePretendLongRunningTask();
mainActivityViewModel.setIsUpdating(true);
                }else{
                    myService.pausePretendLongRunningTask();
                    mainActivityViewModel.setIsUpdating(false);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    private void startService(){
        Intent intent=new Intent(this,MyService.class);
        startService(intent);
bindService();
    }

    private void bindService(){
        Intent intent=new Intent(this,MyService.class);
bindService(intent,mainActivityViewModel.getServiceConnection(), BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mainActivityViewModel.getBinder()!=null){
            unbindService(mainActivityViewModel.getServiceConnection());
        }
    }
}
