package com.kingl.zxs.klapplication.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonSyntaxException;
import com.kingl.zxs.klapplication.ActivityCollector.BaseActivity;
import com.kingl.zxs.klapplication.Config.Consts;
import com.kingl.zxs.klapplication.Net.NetworkStateService;
import com.kingl.zxs.klapplication.Net.okgo.ApsResponse;
import com.kingl.zxs.klapplication.Net.okgo.JsonCallBack;
import com.kingl.zxs.klapplication.R;
import com.kingl.zxs.klapplication.SharedPreferences.SharedPreferencesBean;
import com.kingl.zxs.klapplication.WebService.AppService;
import com.kingl.zxs.klapplication.model.baseinfo;
import com.kingl.zxs.klapplication.model.userinfo;
import com.kingl.zxs.klapplication.utils.IntentUtil;
import com.kingl.zxs.klapplication.utils.ToastUtil;
import com.lzy.okgo.exception.OkGoException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class LoginNewActivity extends BaseActivity implements View.OnClickListener{

    private LinearLayout organizationLayout;
    private TextView organizationEditTxt;
    private ImageView downOrganizationImg;
    private LinearLayout usernameLayout;
    private EditText usernameEditTxt;
    private ImageView downUsernameImg;
    private LinearLayout passwordLayout;
    private EditText passwordEditTxt;
    private Button loginBtn;

    private List<baseinfo> organList;
    private int selectServerNum;
    private PopupWindow mserverPopupWindow;
    private LinearLayout systemSetLayout;

    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        this.mSharedPreference = getSharedPreferences(SharedPreferencesBean.SHAREDPREFERENCE_NAME, 0);
        this.initView();
        this.init();
    }

    private void initView(){
        this.organizationLayout=(LinearLayout)findViewById(R.id.input_layout_organization);
        this.organizationEditTxt=(TextView)findViewById(R.id.et_organization);
        this.downOrganizationImg=(ImageView)findViewById(R.id.downImgView_organization);
        this.usernameLayout=(LinearLayout)findViewById(R.id.input_layout_name);
        this.usernameEditTxt=(EditText)findViewById(R.id.et_username);
        this.downUsernameImg=(ImageView)findViewById(R.id.downImgView_username);
        this.passwordLayout=(LinearLayout)findViewById(R.id.input_layout_psw);
        this.passwordEditTxt=(EditText)findViewById(R.id.et_password);
        this.loginBtn=(Button)findViewById(R.id.loginBtn);
        this.systemSetLayout=(LinearLayout)findViewById(R.id.system_set_layout);
        this.loginBtn.setOnClickListener(this);
        this.organizationLayout.setOnClickListener(this);
        this.systemSetLayout.setOnClickListener(this);
    }

    private void init() {
        organList=new ArrayList<>();
        String serverurl=mSharedPreference.getString(SharedPreferencesBean.SERVER_URL,"");
        if(serverurl.equals("")){
            ToastUtil.shortToast("请设置服务器地址！");
            return;
        }
        Consts.API_SERVICE_HOST =  serverurl+"/KLApi/appServer";
        this.organizationEditTxt.setText(mSharedPreference.getString(SharedPreferencesBean.ORGANIZATION,""));
        this.usernameEditTxt.setText(this.mSharedPreference.getString(SharedPreferencesBean.USERNAME, ""));

        if (!NetworkStateService.isNetworkAvailable(this)) {
            ToastUtil.shortToast("网络连接不可用，请检查！");
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.input_layout_organization:
                if(organList==null || organList.size()==0){
                    int i =setOrganization();
                    if(i==1){
                        ToastUtil.shortToast("获取组织机构失败，请检查服务器地址！");
                        break;
                    }
                }
                initOrganPopupWindow();
                break;
            case R.id.system_set_layout:
                Intent intent1 = new Intent(this,ServerUrlActivity.class);
                startActivityForResult(intent1, 100);
                break;
            case R.id.loginBtn:
                if(!NetworkStateService.isNetworkAvailable(this)){
                    ToastUtil.shortToast("网络连接不可用，请检查！");
                    return;
                }
                this.username=this.usernameEditTxt.getText().toString().trim();
                this.password=this.passwordEditTxt.getText().toString().trim();
                if(TextUtils.isEmpty(username)){
                    ToastUtil.shortToast("用户名不能为空！");
                    return;
                }
                showLoading(this,"登录中");
//                showLoadingCircle(this);
                AppService.getInstance().loginAsync(LoginNewActivity.this, username, password, clientid, new JsonCallBack<ApsResponse<userinfo>>() {
                    @Override
                    public void onSuccess(ApsResponse<userinfo> userinfoApsResponse, Call call, Response response) {
                        if(userinfoApsResponse.code==ApsResponse.RESPONSE_ERROR){
                            ToastUtil.shortToast(userinfoApsResponse.msg);
                            stopLoading();
                        }else {
                            if(userinfoApsResponse.data!=null)
                                setUserInfo(userinfoApsResponse.data);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.shortToast("登录成功！");
                                    stopLoading();
                                }
                            });
                            IntentUtil.newIntent(LoginNewActivity.this,MainActivity.class);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        stopLoading();
                        if(e!=null){
                            Log.e("登录", "onError: "+e.getMessage() );
                            e.printStackTrace();
                            ToastUtil.shortToast(e.getMessage());
                        }
                        if(e instanceof OkGoException){
                            ToastUtil.shortToast("抱歉，发生了未知错误！");
                        }else  if( e instanceof SocketTimeoutException){
                            ToastUtil.shortToast("你的手机网络太慢！");
                        }else  if( e instanceof ConnectException){
                            ToastUtil.shortToast("服务器地址不正确，请重新设置！");
                        }
                    }
                });
                break;
        }

    }

    private void setUserInfo(userinfo data) {
        AppService.getInstance().setmCurrentUser(data);
        mSharedPreference.edit().putString(SharedPreferencesBean.TOKEN,data.token).commit();
        mSharedPreference.edit().putString(SharedPreferencesBean.USERNAME,data.username).commit();
        mSharedPreference.edit().putString(SharedPreferencesBean.PASSWORD,data.userpwd).commit();
        mSharedPreference.edit().putString(SharedPreferencesBean.USERID,data.userid).commit();
    }

    private int setOrganization() {
        int i=0;
        try{
            AppService.getInstance().getServerlistAsync(new JsonCallBack<ApsResponse<List<baseinfo>>>() {
                @Override
                public void onSuccess(ApsResponse<List<baseinfo>> listApsResponse, Call call, Response response) {
                    if(listApsResponse.code==ApsResponse.RESPONSE_ERROR){
                        ToastUtil.shortToast(listApsResponse.msg);
                    }else {
                        organList.addAll(listApsResponse.data);
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    if(e!=null){
                        Log.e("db","onError:"+e.getMessage());
                        e.printStackTrace();
                        ToastUtil.shortToast(e.getMessage());
                    }
                    if(e instanceof OkGoException){
                        ToastUtil.shortToast("抱歉，发生了未知错误！");
                    }else if(e instanceof SocketTimeoutException){
                        ToastUtil.shortToast("你的手机网络太慢");
                    }else if(e instanceof ConnectException){
                        ToastUtil.shortToast("服务器地址不正确，请重新设置！");
                    }else if (e instanceof JsonSyntaxException){
                        ToastUtil.shortToast("认证码失效，请重新登录");
                    }
                }
            });
        }catch (Exception e){
            i=1;
        }
        return i;
    }

    @SuppressWarnings("unchecked")
    public void initOrganPopupWindow() {
        if (organList != null && organList.size() > 0) {
            String[] arrayOfString = new String[this.organList.size()];
            for(int i=0;i<this.organList.size();i++){
                arrayOfString[i] = organList.get(i).basename;
            }
            View localView = LayoutInflater.from(this).inflate(R.layout.popupwindow_list, null);
            ListView localListView = (ListView) localView.findViewById(R.id.popuplist);
            ArrayAdapter localArrayAdapter = new ArrayAdapter(this, R.layout.popupwindow_list_textview, arrayOfString);
            localListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
                    LoginNewActivity.this.organizationEditTxt.setText(LoginNewActivity.this.organList.get(paramInt).basename);
                    LoginNewActivity.this.selectServerNum = paramInt;
                    String url = organList.get(paramInt).baseid;
                    if ((paramInt > -1) && !url.equals("")) {
                        mSharedPreference.edit().putString(SharedPreferencesBean.ORGANIZATION, organList.get(paramInt).basename).commit();
                    }
                    LoginNewActivity.this.mserverPopupWindow.dismiss();

                }
            });
            localListView.setAdapter(localArrayAdapter);
            this.mserverPopupWindow = new PopupWindow(localView, this.organizationEditTxt.getWidth(), -2);
            this.mserverPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            this.mserverPopupWindow.setFocusable(true);
            this.mserverPopupWindow.setOutsideTouchable(true);
            this.mserverPopupWindow.update();
            this.mserverPopupWindow.showAsDropDown(this.organizationEditTxt);

        } else {
            ToastUtil.shortToast("请等待");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100 && resultCode==101){
            init();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
