package com.kingl.zxs.klapplication.Activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.kingl.zxs.klapplication.ActivityCollector.BaseActivity;
import com.kingl.zxs.klapplication.R;
import com.kingl.zxs.klapplication.utils.ToastUtil;
import com.lzy.okgo.OkGo;

public class MainActivity extends BaseActivity {

    private static final String TAG=MainActivity.class.getSimpleName();
    private FrameLayout contentPagerlayout;

    // 保存用户按返回键的时间
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.initView();
        this.init();
    }

    private void initView() {
        this.contentPagerlayout=(FrameLayout)findViewById(R.id.contentPager);
    }

    private void init() {
        FragmentTransaction localFragmentTransaction = getSupportFragmentManager().beginTransaction();
        localFragmentTransaction.replace(R.id.contentPager, new WorkFragment());
        localFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.e(TAG, System.currentTimeMillis()+"" );
        Log.e(TAG, mExitTime +"");
        if((System.currentTimeMillis()-mExitTime)>2000){
            ToastUtil.shortToast("再按一次退出");
            mExitTime=System.currentTimeMillis();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//      Activity销毁时，取消网络请求
        OkGo.getInstance().cancelAll();
    }
}
