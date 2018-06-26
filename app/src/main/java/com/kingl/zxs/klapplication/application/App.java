package com.kingl.zxs.klapplication.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.kingl.zxs.klapplication.Activity.LoginNewActivity;
import com.kingl.zxs.klapplication.ActivityCollector.ActivityCollector;
import com.kingl.zxs.klapplication.SharedPreferences.SharedPreferencesBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.PersistentCookieStore;

import java.util.logging.Level;

/**
 * Created by Administrator on 2018/6/7.
 */

public class App extends Application implements Thread.UncaughtExceptionHandler{
//UncaughtExceptionHandler设置默认未捕获的异常处理handler，这个handler可以在任意线程被未处理的异常唤醒。
// 也就是说只要你的程序发生了没被处理的异常就会调用这个handler来处理异常。

    private static App app;
    private SharedPreferences mSharedPreference;

    public static App getInstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
        this.mSharedPreference=getSharedPreferences(SharedPreferencesBean.SHAREDPREFERENCE_NAME,0);

        //OKGo初始化
        OkGo.init(this);
        OkGo.getInstance().debug("okgo", Level.WARNING,true)
                // 打开该调试开关,打印级别
                // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                .setConnectTimeout(20000) //全局链接超时时间
                .setReadTimeOut(20000)    //全局读取超时时间
                .setWriteTimeOut(20000)   //全局写入超时时间
                .setCookieStore(new PersistentCookieStore());//cookie持久化存储

    }

    public SharedPreferences getSharedPreferences(){
        return this.mSharedPreference;
    }

    /**
     * 获取Application Context
     */
    public static Context getAppContext() {
        return app != null ? app.getApplicationContext() : null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);//启动MultiDex
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        ActivityCollector.finishAll();
        Intent intent=new Intent(this, LoginNewActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "程序崩溃！我们一直在尽力完善软件，欢迎您的反馈。", Toast.LENGTH_SHORT).show();
    }
}
