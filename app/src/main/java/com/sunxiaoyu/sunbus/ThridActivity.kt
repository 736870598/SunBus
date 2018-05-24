package com.sunxiaoyu.sunbus

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.sunxiaoyu.sunbus.core.Subscribe
import com.sunxiaoyu.sunbus.core.SunEventBus

/**
 * --
 *
 * Created by sunxy on 2018/4/26 0026.
 */
class ThridActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SunEventBus.getDefault().register(this)
    }

    @Subscribe("99","2323")
    fun post(str: String){
        Log.v("sunxy", "ThridActivity 99 收到事件：" + str)
    }

    fun sendClick(view : View){
        SunEventBus.getDefault().post("99", "-----------------")
    }

}