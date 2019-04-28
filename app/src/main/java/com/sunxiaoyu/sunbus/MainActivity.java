package com.sunxiaoyu.sunbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sunxiaoyu.sunbus.core.Subscribe;
import com.sunxiaoyu.sunbus.core.SunEventBus;
import com.sunxiaoyu.sunbus.core.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private Object[] objects = new Object[]{"----------------------"};
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SunEventBus.getDefault().register(this);

        textView = findViewById(R.id.textView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SunEventBus.getDefault().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SunEventBus.getDefault().onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SunEventBus.getDefault().unRegister(this);
    }

    public void startActivity2(View view){
        startActivity(new Intent(this, SecondActivity.class));
    }

    public void sendClick(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SunEventBus.getDefault().post("change", 123, "sdsd");
//                SunEventBus.getDefault().post("change", new Integer[]{1,2,3}, "sdsd");
//                SunEventBus.getDefault().post("change", "thread, changeBtnText");
            }
        }).start();
//        SunBus.getDefault().postWait("1234", "MainActivity发的事件");
    }

    public void sendClick1(View view){
        SunEventBus.getDefault().postWait("1234", objects);
    }

    public void removePost(View view){
        SunEventBus.getDefault().removePost("post", "123");
    }

    @Subscribe("123456")
    private void post(String str){
        Log.v("sunxy", "MainActivity收到事件 123456, str =" + str);
    }

    @Subscribe(value = "change", threadMode = ThreadMode.MainThread)
    private void changeBtnText(int arrays, String str){
        textView.setText(arrays + "---" + str);
    }

}
