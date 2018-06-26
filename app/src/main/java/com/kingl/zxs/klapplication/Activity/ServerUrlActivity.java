package com.kingl.zxs.klapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingl.zxs.klapplication.ActivityCollector.BaseActivity;
import com.kingl.zxs.klapplication.Config.Consts;
import com.kingl.zxs.klapplication.R;
import com.kingl.zxs.klapplication.SharedPreferences.SharedPreferencesBean;
import com.kingl.zxs.klapplication.utils.ToastUtil;

public class ServerUrlActivity extends BaseActivity implements View.OnClickListener{



    //region 公共
    private static final String TAG=ServerUrlActivity.class.getSimpleName();
//    private Button commitBtn;
//    private Button cancelBtn;
    private TextView titiltxt;
    private ImageButton backImgBtn;
    private ImageView scanImgBtn;
    //endregion

    private EditText serverEditTxt;
    private Button saveUrlbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_url);
        mSharedPreference=getSharedPreferences(SharedPreferencesBean.SHAREDPREFERENCE_NAME,0);
        initView();
        init();
    }

    private void initView() {

        this.serverEditTxt=(EditText)findViewById(R.id.serverEditTxt);
        this.saveUrlbtn=(Button)findViewById(R.id.set_server_url_btn);
        this.saveUrlbtn.setOnClickListener(this);

        //region公共
//        this.commitBtn = ((Button) findViewById(R.id.commitBtn));
//        this.commitBtn.setOnClickListener(this);
//        this.cancelBtn = ((Button) findViewById(R.id.cancelBtn));
//        this.cancelBtn.setOnClickListener(this);
        this.titiltxt=(TextView)findViewById(R.id.titletxt);
        this.titiltxt.setText("系统设置");
        this.backImgBtn=(ImageButton)findViewById(R.id.back);
        this.backImgBtn.setOnClickListener(this);
        this.scanImgBtn=(ImageView)findViewById(R.id.scanImgBtn);
        this.scanImgBtn.setOnClickListener(this);
        scanImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(ServerUrlActivity.this, CaptureActivity.class);
                startActivityForResult(it, SCANNIN_GREQUEST_CODE);
            }
        });
        //endregion

    }

    private void init() {
        String serverUrl=mSharedPreference.getString(SharedPreferencesBean.SERVER_URL,"");
        if(!serverUrl.equals("")){
            this.serverEditTxt.setText(serverUrl);
        }else {
            this.serverEditTxt.setText(Consts.API_SERVICE_HOST.replace("/KLApi/appServer",""));
            mSharedPreference.edit().putString(SharedPreferencesBean.SERVER_URL,this.serverEditTxt.getText().toString()).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default: break;
            case  R.id.back:
                this.finish();
                break;
            case R.id.set_server_url_btn:
                if (this.serverEditTxt.getText().toString().equals("")) {
                    ToastUtil.shortToast("请输入服务器地址!");
                    return;
                }
                mSharedPreference.edit().putString(SharedPreferencesBean.SERVER_URL, this.serverEditTxt.getText().toString()).commit();
                Consts.API_SERVICE_HOST=serverEditTxt.getText().toString()+"/KLApi/appServer";
                ToastUtil.shortToast("服务器地址设置成功!");
                Intent intent = new Intent();
                intent.putExtra("result", "OK");
                setResult(101, intent);
                this.finish();
                //region 强制下线
//                Intent intent=new Intent("com.kingl.zxs.app.START_LOGIN_IN");
//                sendBroadcast(intent);
                //endregion
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCANNIN_GREQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String result=data.getStringExtra("scan_result");
                Log.d(TAG,"扫码结果:"+result);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
