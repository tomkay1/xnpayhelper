package com.arya1021.alipay.request;

/**
 * @ Created by Dlg
 * @ <p>TiTle:  HookConstants</p>
 * @ <p>Description: Xposed的唯一Hook入口</p>
 * @ date:  2018/09/25
 */
public class HookConstants {

    //被申请要创建二维码的广播
    public static final String WECHAT_CREAT_QR = "com.wechat.qr.create";
    public static final String ALIPAY_CREAT_QR = "com.alipay.qr.create";
    public static final String ALIPAY_KOULING = "com.alipay.kouling";

    public static final String UNIONPAY_CREAT_QR = "com.unionpay.qr.create";

    public static final String UNIONPAY_ACCOUNT_CHANGE = "com.unionpay.account.change";

    public static final String UNIONPAY_LISTEN_CLOSE = "com.unionpay.listen.close";
    public static final String UNIONPAY_LISTEN_OPEN = "com.unionpay.listen.open";

    public static final String UNIONPAY_ORDER_FINISH = "com.unionpay.order.finish";

    //成功生成二维码的HOOK广播消息
    public static final String RECEIVE_QR_WECHAT = "com.wechat.qr.receive";
    public static final String RECEIVE_QR_ALIPAY = "com.alipay.qr.receive";
    public static final String RECEIVE_QR_UNIONPAY = "com.unionpay.qr.receive";

    //接收到新订单的HOOK广播消息
    public static final String RECEIVE_BILL_WECHAT = "com.wechat.bill.receive";
    public static final String RECEIVE_BILL_ALIPAY = "com.alipay.bill.receive";
    public static final String RECEIVE_BILL_ALIPAYHB = "com.alipay.bill.receive.hb";
    public static final String RECEIVE_BILL_ALIPAYZZ = "com.alipay.bill.receive.zz";
    public static final String RECEIVE_BILL_ALIPAYZK = "com.alipay.bill.receive.zk";
    public static final String RECEIVE_BILL_UNIONPAY = "com.unionpay.bill.receive";


    public static final String WECHAT_PACKAGE = "com.tencent.mm";
    public static final String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";
    public static final String UNIONPAY_PACKAGE = "com.unionpay";


    //是否已经HOOK过微信或者支付宝了
    private boolean WECHAT_PACKAGE_ISHOOK = false;
    private boolean ALIPAY_PACKAGE_ISHOOK = false;
    private boolean UNIONPAY_PACKAGE_ISHOOK = false;


    public static final String SPKEY_UP_QRCODE_RETRY = "UP_QRCODE_RETRY_";
    public static final String SPKEY_UP_OPENAPP = "UP_OPENAPP_";
    public static final String SPKEY_QRCODE_FAILCOUNT = "UP_QRCODE_FAILCOUNT_";

    public static final String SPKEY_QRCODE_FAILCOUNT_E = "UP_QRCODE_FAILCOUNT_E_";



}
