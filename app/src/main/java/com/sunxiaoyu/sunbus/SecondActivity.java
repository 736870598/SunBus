package com.sunxiaoyu.sunbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sunxiaoyu.sunbus.core.Subscribe;
import com.sunxiaoyu.sunbus.core.SunBus;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SunBus.getDefault().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SunBus.getDefault().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SunBus.getDefault().onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SunBus.getDefault().unRegister(this);
    }

    public void startActivity2(View view){
        SunBus.getDefault().postWait("99", "SecondActivity发的事件");
        startActivity(new Intent(this, ThridActivity.class));
    }

    public void sendClick(View view){
        SunBus.getDefault().postWait("123456", "SecondActivity发的事件");
    }

    @Subscribe("1234")
    private void post(String str){
        Log.v("sunxy", "SecondActivity收到事件 1234, str =" + str);
    }

}
