package com.kingl.zxs.klapplication.ZXing.Camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/6/13.
 * 相机配置管理器
 */

public class CameraConfigurationManager {

    private static final String TAG=CameraConfigurationManager.class.getSimpleName();
    private static final Pattern COMMA_PATTERN=Pattern.compile(",");//正则表达式
    private final Context context;
    private int previewFormat;
    private String previewFormatString;

    private Point screenResolution;//屏幕分辨率
    private Point cameraResolution;//相机分辨率

    private static final int TEN_DESIRED_ZOOM = 27;//设置十级缩放级别

    public Point getCameraResolution() {
        return cameraResolution;
    }

    public Point getScreenResolution() {

        return screenResolution;
    }

    public String getPreviewFormatString() {

        return previewFormatString;
    }

    public int getPreviewFormat() {

        return previewFormat;
    }

    CameraConfigurationManager(Context context){
        this.context=context;
    }

    /*获取手机的相机和手机屏幕的一些基本参数*/
    void initFromCameraParameters(Camera camera){
        Camera.Parameters parameters=camera.getParameters();
        //获取相机参数
        previewFormat=parameters.getPreviewFormat();//查询支持的预览帧格式
        previewFormatString=parameters.get("preview-format");
        Log.d(TAG,"默认的预览格式："+previewFormat+"/"+previewFormatString);

        WindowManager manager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display =manager.getDefaultDisplay();//Display显示   获取默认
        //获取屏幕的宽高（像素值）设置到一个实体(point)里面
        screenResolution =new Point(display.getWidth(),display.getHeight());
        Log.d(TAG,"屏幕分辨率:"+screenResolution);

        Point screenResolutionForCamera=new Point();
        screenResolutionForCamera.x=screenResolution.x;
        screenResolutionForCamera.y=screenResolution.y;

        if(screenResolution.x<screenResolution.y){
            screenResolutionForCamera.x=screenResolution.y;
            screenResolutionForCamera.y=screenResolution.x;
        }

        //尽量获取相机最好的point
        cameraResolution=getCameraResolution(parameters,screenResolutionForCamera);
        Log.d(TAG,"相机分辨率："+cameraResolution);

    }

    //设置自己想要的相机参数
    void setDesiredCameraParameters(Camera camera){
        Camera.Parameters parameters= camera.getParameters();
        Log.d(TAG,"设置预览尺寸："+cameraResolution);
        parameters.setPreviewSize(cameraResolution.x,cameraResolution.y);
        setFlash(parameters);
        setZoom(parameters);

        camera.setParameters(parameters);
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        }
    }

    protected void setDisplayOrientation(Camera camera, int angle) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod(
                    "setDisplayOrientation", new Class[] { int.class });
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, new Object[] { angle });
        } catch (Exception e1) {
        }
    }

    /*设置闪光灯，本来是不用设置的，应该是直接获取，但是根据三星建议说防止黑客攻击，最好设置一下*/
    private void setFlash(Camera.Parameters parameters) {
        //Build.MODEL: The end-user-visible name for the end product.（终端用户可见的设备型号）
        if(Build.MODEL.contains("Behold II")&&CameraManager.SDK_INT==3){
            parameters.set("flash-value", 1);
        }else {
            parameters.set("flash-value", 2);
        }
        parameters.set("flash-mode", "off");
    }

    /*根据相机的参数和手机屏幕参数来重新设置相机的*/
    private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolutionForCamera) {
        String previewSizeValueString = parameters.get("preview-size-values");//当前支持的拍照预览尺寸
        if(previewSizeValueString==null){
            previewSizeValueString=parameters.get("preview-size-values");
        }

        Point cameraResolution=null;
        if(previewSizeValueString !=null){
            Log.d(TAG,"当前支持拍照预览尺寸："+previewSizeValueString);
            cameraResolution=findBestPreviewSizeValue(previewSizeValueString,screenResolutionForCamera);
        }

        //没有找到最优的预览效果,那么至少也要确保相机分辨率是8的倍数(我也不知道为什么)，下面的操作就是防备屏幕不是8的倍数，如果
        //是8的倍数，就会在上面的寻找最优size的时候会找到的
        if(cameraResolution==null){
            cameraResolution=new Point((screenResolutionForCamera.x >> 3) << 3,
                    (screenResolutionForCamera.y >> 3) << 3);
        }
        return cameraResolution;
    }

    /**
     * 匹配最佳拍照尺寸
     *根据预览大小和手机屏幕大小来设一个比较完美的预览效果
    * @param previewSizeValueString
     * @param screenResolutionForCamera
     * @return
     */
    private static Point findBestPreviewSizeValue(String previewSizeValueString, Point screenResolutionForCamera) {
        int bestX=0;
        int bestY=0;
        int diff=Integer.MAX_VALUE;
        for(String previewSize : COMMA_PATTERN.split(previewSizeValueString)){
            previewSize=previewSize.trim();
            int dimPosition=previewSize.indexOf('x');
            if(dimPosition<0){
                Log.w(TAG,"错误的拍照尺寸:"+previewSize);
                continue;
            }

            int newX;
            int newY;
            try{
                newX=Integer.parseInt(previewSize.substring(0,dimPosition));
                newY=Integer.parseInt(previewSize.substring(dimPosition+1));
            }catch(NumberFormatException e){
                Log.w(TAG,"错误的拍照尺寸:"+previewSize);
                continue;
            }

            int newDiff=Math.abs(newX - screenResolutionForCamera.x)+Math.abs(newY-screenResolutionForCamera.y);
            if(newDiff==0){
                bestX=newX;
                bestY=newY;
                break;
            }else  if(newDiff<diff){
                bestX=newX;
                bestY=newY;
                diff=newDiff;
            }
        }
        if(bestX>0 && bestY>0){
            return new Point(bestX,bestY);
        }
        return  null;
    }

    /**
     * 设置变焦
     * @param parameters
     */
    private void setZoom(Camera.Parameters parameters){
        String zoomSupportedString = parameters.get("zoom-supported");//zoom-supported是否支持变焦
        if(zoomSupportedString !=null && !Boolean.parseBoolean(zoomSupportedString)){
            return;
        }

        int tenDesiredZoom=TEN_DESIRED_ZOOM;
        String maxZoomString =parameters.get("max-zoom");
        if(maxZoomString!=null){
            try{
                int tenMaxZoom=(int) (10.0* Double.parseDouble(maxZoomString));
                if(tenDesiredZoom>tenMaxZoom){
                    tenDesiredZoom=tenMaxZoom;
                }
            }catch(NumberFormatException e){
                Log.w(TAG, "[获取原本的缩放最大级别转换失败]错误的最大变焦 bad max-zoom:"+maxZoomString);
            }
        }

        String takingPictureZoomMaxString= parameters.get("taking-picture-zoom-max");
        if(takingPictureZoomMaxString != null){
            try{
                int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
                if(tenDesiredZoom>tenMaxZoom){
                    tenDesiredZoom=tenMaxZoom;
                }
            }catch (NumberFormatException e){
                Log.w(TAG,"[拍照缩放级别转换失败]bad taking-picture-zoom-max:"+takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(motZoomValuesString,
                    tenDesiredZoom);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString
                        .trim());
                int tenZoomStep = (int) (10.0 * motZoomStep);
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
                }
            } catch (NumberFormatException nfe) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", tenDesiredZoom);
        }


    }

    private static int findBestMotZoomValue(CharSequence stringValues,
                                            int tenDesiredZoom) {
        int tenBestValue = 0;
        for (String stringValue : COMMA_PATTERN.split(stringValues)) {
            stringValue = stringValue.trim();
            double value;
            try {
                value = Double.parseDouble(stringValue);
            } catch (NumberFormatException nfe) {
                return tenDesiredZoom;
            }
            int tenValue = (int) (10.0 * value);
            if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom
                    - tenBestValue)) {
                tenBestValue = tenValue;
            }
        }
        return tenBestValue;
    }
}
