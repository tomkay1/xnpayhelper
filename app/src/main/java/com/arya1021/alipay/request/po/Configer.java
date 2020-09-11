package com.arya1021.alipay.request.po;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.arya1021.alipay.utlis.SpUtils;


/**
 * @ Created by Dlg
 * @ <p>TiTle:  Configer</p>
 * @ <p>Description: 用户的首页的配置Bean，单例模式类</p>
 * @ date:  2018/9/21
 */
public class Configer {

    private Configer(){}

    private String url = "http://aryapayapi.au.ngrok.io/";

    private String webSocketUrl = "";

    private boolean payVoiceSwitch;

    /**
     * 长度为8位，和服务端要设置为一样
     */
    private String token = "12345678";

    /**
     * 手机序列号
     */
    private String sn = android.os.Build.SERIAL;

    private String user_wechat = "wxid_"+(int) ((Math.random() * 9 + 1) * 1000);

    private String user_alipay = "2088****";

    private String user_unionpay = "姓名";

    /**
     * 商户uid
     */
    private String uid;

    /**
     * 商户用户名
     */
    private String loginName;

    /**
     * 当前登录的支付宝账号
     */
    private volatile String currentAlipay = null;
    /**
     * 当前登录的微信账号
     */
    private volatile String currentWechat = null;

    private volatile String currentUnionpay = null;


    public String getCurrentAlipay(Context context) {
        return SpUtils.getString(context,"currentAlipay");
    }

    public void setCurrentAlipay(Context context,String currentAlipay) {

        this.currentAlipay = currentAlipay;
        SpUtils.saveString(context,"currentAlipay",currentAlipay);
    }

    public String getCurrentUnionpay(Context context) {
        return SpUtils.getString(context,"currentUnionpay");
    }

    public void setCurrentUnionpay(Context context,String currentUnionpay) {
        this.currentUnionpay = currentUnionpay;
        SpUtils.saveString(context,"currentUnionpay",currentUnionpay);
    }

    public String getCurrentWechat(Context context) {
        return SpUtils.getString(context,"currentWechat");
    }

    public void setCurrentWechat(Context context,String currentWechat) {
        this.currentWechat = currentWechat;
        SpUtils.saveString(context,"currentWechat", currentWechat);
    }

    public static Configer getInstance() {
        return InnerSigleton.instance;
    }
    private static class InnerSigleton{
        private static Configer instance = new Configer();

    }

    public boolean isPayVoiceSwitch(Context context) {
        return SpUtils.getBoolean(context,"payVoiceSwitch");
    }

    public void setPayVoiceSwitch(Context context,boolean payVoiceSwitch) {
        this.payVoiceSwitch = payVoiceSwitch;
        SpUtils.saveBoolean(context,"payVoiceSwitch", payVoiceSwitch);
    }


    public String getUid(Context context) {

        return SpUtils.getString(context,"uid");
    }

    public void setUid(Context context,String uid) {
        this.uid = uid;
        SpUtils.saveString(context,"uid",uid);
    }

    public String getUrl(Context context) {

        String sUrl = SpUtils.getString(context,"gatewayUrl");
        return sUrl == null ? "http://127.0.0.1/" : sUrl;

    }

    public void setUrl(String url) {
        this.url = url;

    }

    public String getWebSocketUrl(Context context) {
        String sUrl = SpUtils.getString(context,"websocketUrl");
        return sUrl == null ? "ws://127.0.0.1/" : sUrl;
    }

    public void setWebSocketUrl(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
    }



    public String getToken(Context context) {
        return SpUtils.getString(context,"token");
    }

    public void setToken(Context context,String token) {
        this.token = token;
        SpUtils.saveString(context,"token",token);
    }

    public String getSN() {
        return sn == null ? "" : sn;
    }

    public String getUserWechat() {
        return user_wechat == null ? "" : user_wechat;
    }

    public void setUserWechat(String user_wechat) {
        this.user_wechat = user_wechat;
    }

    public String getUserAlipay() {
        return user_alipay == null ? "" : user_alipay;
    }

    public void setUser_unionpay(String user_alipay) {
        this.user_unionpay = user_unionpay;
    }

    public String getUser_unionpay() {
        return user_unionpay == null ? "" : user_unionpay;
    }

    public String getLoginName(Context context) {
        return SpUtils.getString(context,"loginName");
    }

    public void setLoginName(Context context,String loginName) {
        this.loginName = loginName;
        SpUtils.saveString(context,"loginName",loginName);
    }

    public void setUserAlipay(String user_alipay) {
        this.user_alipay = user_alipay;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

