package com.arya1021.alipay.utlis;

import android.util.Log;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * 金额转换工具
 */
public final class MoneyUtil {

    private MoneyUtil(){}

    public static String fenToYuan(String amount){
        NumberFormat format = NumberFormat.getInstance();
        try{
            Number number = format.parse(amount);
            double temp = number.doubleValue() / 100.0;
            format.setGroupingUsed(false);
            // 设置返回的小数部分所允许的最大位数
            format.setMaximumFractionDigits(2);
            amount = format.format(temp);

        } catch (ParseException e){
            Log.d("arik",e.getMessage());
        }

        return amount;
    }
    public static Long yuanToFen(String amount){
        NumberFormat format = NumberFormat.getInstance();
        try{
            Number number = format.parse(amount);
            double temp = number.doubleValue() * 100.0;
            format.setGroupingUsed(false);
            // 设置返回数的小数部分所允许的最大位数
            format.setMaximumFractionDigits(0);
            amount = format.format(temp);
        } catch (ParseException e){
            Log.d("arik",e.getMessage());
        }
        return Long.valueOf(amount);
    }

    public static void main(String[] args) {
        System.out.println(fenToYuan("10"));
    }

}
