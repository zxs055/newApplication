package com.kingl.zxs.klapplication.SharedPreferences;

/**
 * Created by Administrator on 2018/6/6.
 */

public class SharedPreferencesBean {
    public static String SHAREDPREFERENCE_NAME;
    public static String SERVER_URL;
    public static String ORGANIZATION;
    public static String USERNAME;
    public static String PASSWORD;
    public static String TOKEN;
    public static String USERID;
    static {
        SHAREDPREFERENCE_NAME="mySharedPreference";
        SERVER_URL="http://192.168.0.234:8006";
        USERNAME="Admin";
        PASSWORD="";
        TOKEN="token";
        ORGANIZATION="安徽金缆";
        USERID="";
    }
}
