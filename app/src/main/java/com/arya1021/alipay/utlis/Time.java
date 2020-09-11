package com.arya1021.alipay.utlis;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author Administrator
 */
public class Time {
    private static long l;

    // 将字符串转为时间戳

    public static long getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();

            String str = String.valueOf(l);
            re_time = str.substring(0, str.length());

        } catch (java.text.ParseException e) {
            Log.d("arik",e.getMessage());
        }

        return l;
    }

    // 将时间戳转为字符串
    public static String getStrTime(long cc_time) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time));

        return re_StrTime;

    }
    public static String getStrTime(Date cc_time) {
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        re_StrTime = sdf.format(cc_time);

        return re_StrTime;

    }
}
