package com.kingl.zxs.klapplication.ActivityCollector;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.kingl.zxs.klapplication.Activity.LoginNewActivity;
import com.kingl.zxs.klapplication.SharedPreferences.SharedPreferencesBean;
import com.kingl.zxs.klapplication.WebService.AppService;
import com.kingl.zxs.klapplication.view.Loading;

/**
 * Created by Administrator on 2018/6/6.
 */

public class BaseActivity extends AppCompatActivity {
    public SharedPreferences mSharedPreference;
    public static String clientid="";
    public StartLoginInBroadcastReceiver startLoginInBroadcastReceiver;
    protected final static int SCANNIN_GREQUEST_CODE = 101;

    private Dialog mDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState,persistentState);
        ActivityCollector.addActivity(this);//活动管理
        this.mSharedPreference=getSharedPreferences(SharedPreferencesBean.SHAREDPREFERENCE_NAME,0);
        this.clientid= AppService.getInstance().getDeviceId(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter =new IntentFilter();
        intentFilter.addAction("com.kingl.zxs.app.START_LOGIN_IN");
        startLoginInBroadcastReceiver=new StartLoginInBroadcastReceiver();
        registerReceiver(startLoginInBroadcastReceiver,intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(startLoginInBroadcastReceiver !=null){
            unregisterReceiver(startLoginInBroadcastReceiver);
            startLoginInBroadcastReceiver=null;
        }
    }

    class StartLoginInBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("警告");
            builder.setMessage("认证码失效，请重新登录");
            builder.setCancelable(false);
            builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    Intent intent=new Intent(context, LoginNewActivity.class);
                    context.startActivity(intent);
                }
            });
            builder.show();
        }
    }

    public boolean canUpdateUI(){
        return (!isFinishing());
    }


    public void showLoading(Context context){
        Loading loading=new Loading();
        mDialog=loading.showLoading(context,null,null,loading.LOGOSTYLE);
    }

    public void showLoading(Context context,String text){
        Loading loading = new Loading();
        mDialog = loading.showLoading(context,text,null,Loading.LOGOSTYLE);
    }

    public void showLoadingCircle(Context context){
        Loading loading=new Loading();
        mDialog=loading.showLoading(context,null,null,loading.CIRCLESTYLE);
    }

    public void stopLoading(){
        if (canUpdateUI()){
            Loading loading = new Loading();
            loading.dialogDismiss(mDialog);
        }
    }

}
