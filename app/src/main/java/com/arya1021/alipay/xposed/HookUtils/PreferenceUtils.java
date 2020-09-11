package com.arya1021.alipay.xposed.HookUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import de.robv.android.xposed.XSharedPreferences;

public final class PreferenceUtils {

    /**
     * 待处理云闪付订单
     */
    public final static String UP_ORDER_WAIT = "UP_ORDER_WAIT_";

    /**
     * 已处理的云闪付订单
     */
    public final static String UP_ORDER_OLD = "UP_ORDER_OLD_";

    private static XSharedPreferences intance = null;


    private PreferenceUtils(){}

    public static XSharedPreferences getIntance(){
        if (intance == null){
            intance = new XSharedPreferences("com.arya1021.alipay","config");
            intance.makeWorldReadable();
        }else {
            intance.reload();
        }
        return intance;
    }
    public static void putStr(Context context,String key,String value){
        SharedPreferences mSharedPreferences = context.getSharedPreferences("config",
                Activity.MODE_PRIVATE);

        SharedPreferences.Editor   mEditor  = mSharedPreferences.edit();
        mEditor.putString(key,value).commit();


        Log.d("arik","spppppp get===========key=="+key+",value====="+get(key));

    }

    public static String get(String key){

        return getIntance().getString(key,null);
    }

}