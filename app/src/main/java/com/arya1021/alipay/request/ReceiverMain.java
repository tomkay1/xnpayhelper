package com.arya1021.alipay.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.request.po.QrBean;
import com.arya1021.alipay.utlis.MoneyUtil;
import com.arya1021.alipay.ws.BaseMsg;
import com.arya1021.alipay.ws.MqTopic;
import com.arya1021.alipay.ws.MsgNewOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ReceiverMain extends BroadcastReceiver {
    private ApiBll mApiBll;
    public volatile static boolean mIsInit = false;
    private static String lastMsg = "";//防止重启接收广播，一定要用static
    private static long mLastSucc = 0;
    private static String cook = "";


    public ReceiverMain() {
        super();
        mIsInit = true;
        //LogUtils.show("Receiver创建成功！");
        mApiBll = new ApiBll();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String data = intent.getStringExtra("data");

            // 过滤重复广播
            if (lastMsg.contentEquals(intent.getAction() + data)) {
                return;
            }

            lastMsg = intent.getAction() + data;

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            switch (intent.getAction()) {

                case HookConstants.RECEIVE_QR_ALIPAY:

                    QrBean qrBean = JSON.parseObject(data, QrBean.class);
                    //mApiBll.sendQR(qrBean.getUrl(), qrBean.getMark_sell());
                    // 发送webscoket
                    BaseMsg msg  = new BaseMsg();
                    msg.setTopic(MqTopic.NEW_ORDER);
                    msg.setFromUserId(Configer.getInstance().getUid(context));
                    MsgNewOrder msgNewOrder = new MsgNewOrder();
                    msgNewOrder.setChannel(qrBean.getChannel());
                    msgNewOrder.setChannelAccount(Configer.getInstance().getCurrentAlipay(context));
                    msgNewOrder.setMoney(qrBean.getMoney());
                    msgNewOrder.setRealMoney(MoneyUtil.fenToYuan(qrBean.getMoney().toString()));
                    msgNewOrder.setTradeNo(qrBean.getMark_sell());
                    msgNewOrder.setUid(Configer.getInstance().getUid(context));
                    msgNewOrder.setQrcode(qrBean.getUrl());

                    msg.setContent(gson.toJson(msgNewOrder));

                    ServiceMain.sendMsg(context,gson.toJson(msg));

                    Log.d("arik", "发送了支付宝QR："+qrBean);
                    break;
                case HookConstants.RECEIVE_BILL_ALIPAYHB:
                    qrBean = JSON.parseObject(data, QrBean.class);
                    //LogUtils.show("支付宝收款成功2：" + qrBean.getOrder_id() + "|" + qrBean.getMark_sell() + "|" + qrBean.getMoney());
                    Log.d("arik","支付宝红包收款："+qrBean);
                    mApiBll.payQR(context,qrBean);
                    break;
                case HookConstants.RECEIVE_BILL_ALIPAYZZ:
                case HookConstants.RECEIVE_BILL_ALIPAYZK:
                    qrBean = JSON.parseObject(data, QrBean.class);
                    //LogUtils.show("支付宝收款成功2：" + qrBean.getOrder_id() + "|" + qrBean.getMark_sell() + "|" + qrBean.getMoney());
                    mApiBll.payQR(context,qrBean);
                    break;
                case HookConstants.RECEIVE_BILL_ALIPAY:
                    cook = data;
                    mLastSucc = System.currentTimeMillis();
                    //PayUtils.dealAlipayWebTrade(context, data);
                    qrBean = JSON.parseObject(data, QrBean.class);
                    Log.d("arik", "支付宝银行收款：" + qrBean.getMark_sell() + "|" + qrBean.getMoney());
                    mApiBll.payQR(context,qrBean);
                    break;
                default:
                    break;

            }


        } catch (Exception e) {
            //LogUtils.show(e.getMessage());
        }
    }

    public static String getCook() {
        return cook == null ? "" : cook;
    }

    public static void setCook(String cook) {
        ReceiverMain.cook = cook;
    }

    public static long getmLastSucc() {
        return mLastSucc;
    }

    public static void setmLastSucc(long mLastSucc) {
        ReceiverMain.mLastSucc = mLastSucc;
    }

}
