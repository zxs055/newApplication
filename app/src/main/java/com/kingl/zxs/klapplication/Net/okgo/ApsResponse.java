package com.kingl.zxs.klapplication.Net.okgo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/11.
 */

public class ApsResponse<T> implements Serializable {
    public int code;
    public String msg;
    public T data;

    /**
     * 请求编码返回正确
     */
    public final static int RESPONSE_OK = 0;


    /**
     * 请求编码返回错误
     */
    public final static int RESPONSE_ERROR = -1;
}
