package com.kingl.zxs.klapplication.utils;

import android.content.Context;
import android.widget.Toast;

import com.kingl.zxs.klapplication.application.App;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/6/7.
 */

public class ToastUtil {
    private static Toast toast;

    public static void shortToast(Context context,String str){
        // 使用弱引用，防止内存泄漏
        WeakReference<Context> contextWeakReference=new WeakReference<Context>(context);//把context设为弱引用
        if(toast==null){
            toast = Toast.makeText(contextWeakReference.get(),str,Toast.LENGTH_SHORT);
        }else {
            toast.setText(str);
        }
        toast.show();
    }

    public static void longToast(Context context,String str){
        // 使用弱引用，防止内存泄漏
        WeakReference<Context> contextWeakReference=new WeakReference<Context>(context);//把context设为弱引用
        if(toast==null){
            toast = Toast.makeText(contextWeakReference.get(),str,Toast.LENGTH_LONG);
        }else {
            toast.setText(str);
        }
        toast.show();
    }

    public static void shortToast(String desc){
        if (toast == null){
            toast = Toast.makeText(App.getAppContext(),desc,Toast.LENGTH_SHORT);
        }else {
            toast.setText(desc);
        }
        toast.show();
    }

    public static void longToast(String desc){
        // 使用弱引用，防止内存泄漏
        if (toast == null){
            toast = Toast.makeText(App.getAppContext(),desc,Toast.LENGTH_LONG);
        }else {
            toast.setText(desc);
        }
        toast.show();
    }
}
