package com.kingl.zxs.klapplication.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kingl.zxs.klapplication.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/6/19.
 */

public class Loading {
    public static final String TAG=Loading.class.getSimpleName();

    public static final String LOGOSTYLE = "logo";
    public static final String CIRCLESTYLE = "circle";

    private Dialog mDialog;
    private TextView mLoadingText;
    // 控制进度增长
    private boolean isLoadingShow = true;
    private ProgressBar mProgressBar;
    // 控制填满速度
    public static final int SLEEPTIME = 160;

    /**
     * 开启控制progressBar线程
     */
    private void startProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isLoadingShow){
                    try{
                        handler.sendEmptyMessage(0x123);
                        Thread.sleep(SLEEPTIME);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 对话框销毁
     */
    public void dialogDismiss(Dialog dialog){
        isLoadingShow = false;
        if ( dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public  interface OnReturnListener{
        void back();
    }
    /**
     * 显示网络加载对话框
     *
     * @param context   上下文对象
     * @param text      按返回之后等待框消失之前progressBar下面需要显示的文字提示（可以为空，为空显示默认文字）
     * @param listener 按返回之后回调监听函数
     * @param style     对话框风格
     * @return          返回一个自定义对话框
     */
    public Dialog showLoading(Context context,final String text,final OnReturnListener listener,String style){
        if(mDialog!=null&&mDialog.isShowing()){
            return mDialog;
        }
        //使用弱引用，防止内存泄漏
        WeakReference<Context> contextWeakReference =new WeakReference<Context>(context);//把context设为弱引用
        LayoutInflater inflater=LayoutInflater.from(contextWeakReference.get());////加载布局管理器
        mDialog=new Dialog(contextWeakReference.get(), R.style.loading_style);//初始化对话框
        mDialog.setCanceledOnTouchOutside(false);
        //设置点击对话框以外不可取消
        //dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
        //dialog.setCancelable(false);dialog弹出后会点击屏幕或物理返回键，dialog不消失
        mDialog.setCancelable(true);
        View loadingView=null;
        if(style.equals(LOGOSTYLE)){
            loadingView=inflater.inflate(R.layout.view_loading_logo,null,false);//将xml布局转换为view对象
            mProgressBar=(ProgressBar)loadingView.findViewById(R.id.view_loading);
            Log.d(TAG,"开始线程");
            startProgress();
        }else if(style.equals(CIRCLESTYLE)){
            loadingView = inflater.inflate(R.layout.view_loading_circle,null,false);
        }
        mLoadingText=(TextView) loadingView.findViewById(R.id.loading_text);
        setListener(listener,text);
        mDialog.setContentView(loadingView);
        mDialog.show();
        isLoadingShow=true;
        return mDialog;
    }
    /**
     * 设置Dialog的监听器
     * @param listener 按返回之后回调监听函数
     * @param text      按返回之后等待框消失前progressBar下面需要显示的文字
     */
    private void setListener(final OnReturnListener listener, final String text) {
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(!TextUtils.isEmpty(text)){
                        setDialogText(text);
                    }
                    if(listener !=null){
                        listener.back();
                    }
                }
                return false;
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isLoadingShow = false;
                mProgressBar = null;
                dialog = null;
                mLoadingText = null;
                // 清空handler队列，防止内存泄漏
                handler.removeCallbacksAndMessages(null);
            }
        });
    }

    /**
     * 设置文字内容
     * @param text  文字内容
     */
    private void setDialogText(String text) {
        if (mLoadingText != null){
            mLoadingText.setText(text);
        }
    }

//    标注忽略指定的警告
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x123:
                    if (mProgressBar != null){
                        Log.d(TAG,String.valueOf(mProgressBar.getProgress()));
                        if (mProgressBar.getProgress() >= 100){
                            mProgressBar.setProgress(0);
                        }
                        mProgressBar.incrementProgressBy(2);//方法增加或减少进度
                    }
                    break;
            }

        }
    };
}
