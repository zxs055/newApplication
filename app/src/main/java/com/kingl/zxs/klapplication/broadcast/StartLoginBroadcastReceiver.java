package com.kingl.zxs.klapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import com.kingl.zxs.klapplication.Activity.LoginNewActivity;
import com.kingl.zxs.klapplication.ActivityCollector.ActivityCollector;

/**
 * Created by Administrator on 2018/6/12.
 */

public class StartLoginBroadcastReceiver extends BroadcastReceiver{
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
