package com.arya1021.alipay.utlis;


public class SealAppContext {

    public static String MYPACKAGE = "com.arya1021.alipay";

    public static String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
    public static String WECHAT_PACKAGE = "com.tencent.mm";
    public static String UNIONPAY_PACKAGE = "com.unionpay";
    public static String MSGRECEIVED_ACTION = "received.msg.1";//日志
    public static String LOGINIDRECEIVED_ACTION = "received.loginid.1";//登录账号
    public static String BILLRECEIVED_ACTION = "received.bill.1";//收款订单
    public static String TRADENORECEIVED_SHI_ACTION = "received.alipay.tradeno.1";//支付宝商户成功
    public static String ALIPAYSTART_ACTION = "received.alipay.start.1";//启动支付宝
    public static String WECHATSTART_ACTION = "received.wechat.start.1";//启动微信
    public static String QRCODERECEIVED_ACTION = "received.qrcode.1";//二维码
    public static String ALARM_ACTION = "received.alarm.1";//定时任务

    public static String VOICE_ACTION = "received.action.voice";//语音播报
}