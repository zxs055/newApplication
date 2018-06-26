package com.kingl.zxs.klapplication.Net.okgo;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/11.
 */

public class SimpleResponse implements Serializable {
    public int code;
    public String msg;

    public ApsResponse toResponse() {
        ApsResponse apsResponse = new ApsResponse();
        apsResponse.code = code;
        apsResponse.msg = msg;
        return apsResponse;
    }
}
