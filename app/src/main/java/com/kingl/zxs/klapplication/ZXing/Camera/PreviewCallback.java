package com.kingl.zxs.klapplication.ZXing.Camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Administrator on 2018/6/14.
 * 相机预览回调接口
 * 回调接口用于交付的副本显示预览帧。
 */

public class PreviewCallback implements Camera.PreviewCallback{
    private static final String TAG=PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager configurationManager;
    private final boolean useOneShotPreviewCallback;
    private Handler previewHandler;
    private int previewMessage;

    PreviewCallback(CameraConfigurationManager configManager,boolean isUseOneShotPreviewCallback){
        this.configurationManager=configManager;
        this.useOneShotPreviewCallback=isUseOneShotPreviewCallback;
    }

    void setHandler(Handler previewHandler,int previewMessage){
        this.previewHandler=previewHandler;
        this.previewMessage=previewMessage;
    }

    //一旦使用void setPreviewCallback (Camera.PreviewCallback cb) 注册预览回调接口，
    // onPreviewFrame()方法会一直被调用，直到camera preview销毁
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution=configurationManager.getCameraResolution();
        if(!useOneShotPreviewCallback){
            camera.setPreviewCallback(null);
        }
        if(previewHandler!=null){
//            是从消息池中拿来一个msg 不需要另开辟空间new
            Message message=previewHandler.obtainMessage(previewMessage,cameraResolution.x,cameraResolution.y,data);
            message.sendToTarget();//message 从handler 类获取然后发送，性能好
            previewHandler=null;
        }else {
            Log.d(TAG,"有预览回调,但没有处理程序");
        }

    }
}
