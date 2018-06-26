package com.kingl.zxs.klapplication.WebService;

import android.content.Context;

import com.kingl.zxs.klapplication.Config.Consts;
import com.kingl.zxs.klapplication.Net.okgo.ApsResponse;
import com.kingl.zxs.klapplication.Net.okgo.JsonCallBack;
import com.kingl.zxs.klapplication.model.baseinfo;
import com.kingl.zxs.klapplication.model.userinfo;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/6/11.
 */

public class AppService {
    private static AppService instance;
    private userinfo mCurrentUser;
    private AppService(){

    }
    public static AppService getInstance(){
        if(instance==null){
            instance =new AppService();
        }
        return instance;
    }

    public userinfo getmCurrentUser() {
        return mCurrentUser;
    }

    public void setmCurrentUser(userinfo mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }


    public static String getDeviceId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    //region  用户登录
    /**
     * 异步获取服务器列表
     * @param callback
     */
    public void getServerlistAsync(JsonCallBack<ApsResponse<List<baseinfo>>> callback){
        String url = Consts.API_SERVICE_HOST+"/Login/GetOrganiseList";
        OkGo.get(url).execute(callback);
    }

    /**
     * 异步用户登录
     * @param username  用户名
     * @param password  用户密码
     * @param clientid    客户端ID
     * @param callback  回调
     */
    public void loginAsync(Context context,String username,String password,String clientid,JsonCallBack<ApsResponse<userinfo>> callback){
        String url = Consts.API_SERVICE_HOST + "/Login/CheckLogin";
        HashMap<String,String> postParams = new HashMap<>();
        postParams.put("usercode",username);
        postParams.put("userpwd",password);
        postParams.put("clienttag",clientid);
        OkGo.post(url).tag(context).params(postParams).execute(callback);
    }
    //endregion


}
