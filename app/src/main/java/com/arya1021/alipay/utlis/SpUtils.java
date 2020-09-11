package com.arya1021.alipay.utlis;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public final class SpUtils {

    /**
     * 待发送的websocket消息
     */
    public final static String WS_WAIT_SEND_MSG = "WS_MSG_WAIT_LIST";

    public final static String UP_NEWORDER_CHECK_RETRY = "UP_ORDERCHECK_RETRY_";

    /**
     * 待处理云闪付订单
     */
    public final static String UP_WAIT_ORDER = "UP_WAIT_ORDER_";
    public static final String UP_XTID = "UP_XTID_";
    public static final String UP_CachecityCd = "UP_CachecityCd_";
    public static final String UP_SID = "UP_SID_";
    public static final String UP_URID = "UP_URID_";
    public static final String UP_DfpSessionId = "UP_DfpSessionId_";
    public static final String UP_Cachegray = "UP_Cachegray_";


    public static final String UP_ACCOUNT = "UP_ACCOUNT";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean init) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getBoolean(key, init);
    }


    public static void saveString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, value).commit();
    }

    public static float getFloat(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getFloat(key, 0);
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    public static float getLong(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    public static void saveSet(Context context,String key, Set<String> values){
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        if (values == null){
            sp.edit().remove(key).commit();
        } else{
            sp.edit().putStringSet(key, values).commit();
        }

    }

    public static Set<String> getSet(Context context,String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getStringSet(key,null);
    }
}
