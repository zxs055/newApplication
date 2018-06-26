package com.kingl.zxs.klapplication.ZXing.Camera;

import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Administrator on 2018/6/14.
 * 回调接口用于通知完成相机自动对焦。
 */

public class AutoFocusCallback implements Camera.AutoFocusCallback {
    private static final String TAG=AutoFocusCallback.class.getSimpleName();

    private static final long AUTOFOCUS_INTERVAL_MS = 1500L;

    private Handler autoFocusHandler;
    private int autoFocusMessage;

    void setHandler(Handler autoFocusHandler,int autoFocusMessage){
        this.autoFocusHandler=autoFocusHandler;
        this.autoFocusMessage=autoFocusMessage;
    }

    //相机的自动对焦完成时调用。
    // 如果相机不支持自动对焦和自动对焦,onAutoFocus将立即被称为成功的用一个假的值设置为true。
    // 自动对焦程序不锁自动曝光模式和自动白平衡后完成。
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if(autoFocusHandler!=null){
            Message message= autoFocusHandler.obtainMessage(autoFocusMessage,success);
//           过发送请求每一个焦点 模拟连续自动对焦通
            Log.d(TAG,"有自动对焦的回调;请求另一个");
            autoFocusHandler.sendMessageDelayed(message,AUTOFOCUS_INTERVAL_MS);
//            Enqueue a message into the message queue after all pending messages before (current time + delayMillis).
            autoFocusHandler=null;
        }else {
            Log.d(TAG,"有自动对焦回调,但没有处理程序");
        }
    }
}
