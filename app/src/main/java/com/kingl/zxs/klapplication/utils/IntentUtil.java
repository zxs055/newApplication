package com.kingl.zxs.klapplication.utils;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/25.
 */

public final class IntentUtil {

    public static void newIntent(Context from_act,Class<?> to_act){
        newIntent(from_act,to_act,null,0,false);
    }

    public static void newIntent(Context from_act, Class<?> to_act, HashMap<String, Object> hashMaps) {
        newIntent(from_act, to_act, hashMaps, 0, false);
    }

    /**
     * @param from_act  Context对象
     * @param to_act    目标页
     * @param hashMaps  携带参数
     */
    public static void newIntent(Context from_act, Class<?> to_act, HashMap<String, Object> hashMaps, int code, boolean isResult) {
        Intent intent = new Intent(from_act, to_act);
        if (null != hashMaps) {
            if (hashMaps.size() > 0) {
                for (Map.Entry<String, Object> entry : hashMaps.entrySet()) {
                    if (entry.getValue() instanceof Integer) {
                        intent.putExtra(entry.getKey(), (Integer) entry.getValue());
                    } else if (entry.getValue() instanceof Long) {
                        intent.putExtra(entry.getKey(), (Long) entry.getValue());
                    } else if (entry.getValue() instanceof Boolean) {
                        intent.putExtra(entry.getKey(), (Boolean) entry.getValue());
                    } else if(entry.getValue() instanceof String){
                        intent.putExtra(entry.getKey(), (String) entry.getValue());
                    }else if(entry.getValue() instanceof Serializable){
                        intent.putExtra(entry.getKey(), (Serializable)  entry.getValue());
                    }
                }
            }
        }
        from_act.startActivity(intent);

    }
}
