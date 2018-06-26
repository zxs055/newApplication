package com.kingl.zxs.klapplication.Net;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/6/8.
 */

public class NetworkStateService extends Service {

    // 系统网络连接相关的操作管理类.
    private ConnectivityManager mManager;

    // 网络状态信息的实例
    private NetworkInfo mInfo;

    /**
     * 当前处于的网络
     * 0 ：null
     * 1 : 2G/3G/4G
     * 2 ：wifi
     */
    public static int networkStatus;

    /**
     * An action name
     */
    public static final String NETWORKSTATE = "com.text.android.network.state";

    /**
     * 自定义广播
     */
    private BroadcastReceiver mReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // action是行动的意思，也许是我水平问题无法理解为什么叫行动，我一直理解为标识（现在理解为意图）
            String action = intent.getAction(); //当前接受到的广播的标识(行动/意图)
            // 当当前接受到的广播的标识(意图)为网络状态的标识时做相应判断
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                // 获取网络连接管理器
                mManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                // 获取当前网络状态信息
                mInfo = mManager.getActiveNetworkInfo();
                if (mInfo != null && mInfo.isAvailable()) {

                    //当NetworkInfo不为空且是可用的情况下，获取当前网络的Type状态
                    //根据NetworkInfo.getTypeName()判断当前网络
                    String name = mInfo.getTypeName();

                    //更改NetworkStateService的静态变量，之后只要在Activity中进行判断就好了
                    if (name.equals("WIFI")) {
                        networkStatus = 2;
                    } else {
                        networkStatus = 1;
                    }

                } else {

                    // NetworkInfo为空或者是不可用的情况下
                    networkStatus = 0;

//                    UIUtil.showToast("当前网络不可用，请检查网络连接!");

                    EventBus.getDefault().post(new NetStateEvent());

                    Intent it = new Intent();
                    it.putExtra("networkStatus", networkStatus);
                    it.setAction(NETWORKSTATE);
                    //sendBroadcast(it); //发送无网络广播给注册了当前服务广播的Activity
                    /**
                     * 这里推荐使用本地广播的方式发送:
                     * LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                     */
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册网络状态的广播，绑定到mReceiver
        IntentFilter mFilter=new IntentFilter();//描叙intent属性
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);//自定义广播发送者，监听网络变化
        registerReceiver(mReceiver, mFilter);//注册mReceiver广播接收器接收值为mFilter的广播
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }
}
