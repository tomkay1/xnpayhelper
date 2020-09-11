package com.arya1021.alipay.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arya1021.alipay.request.HookConstants;
import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.request.po.QrBean;
import com.arya1021.alipay.utlis.PayHelperUtils;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.utlis.Time;
import com.arya1021.alipay.ws.Channel;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BillReceived extends BroadcastReceiver {

    private Context mContext;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(final Context context, Intent intent) {
        mContext = context;
        try {
            if (intent.getAction().contentEquals(SealAppContext.BILLRECEIVED_ACTION)) {
                String bill_no = intent.getStringExtra("bill_no");
                String bill_money = intent.getStringExtra("bill_money");
                String bill_mark = intent.getStringExtra("bill_mark");
                String bill_mark_buy = intent.getStringExtra("bill_mark_buy");

                Date bill_time = new Date(intent.getLongExtra("bill_time", System.currentTimeMillis()));
                String bill_type = intent.getStringExtra("bill_type");

                String bill_paymsg = intent.getStringExtra("bill_paymsg");
                Log.d("arik","BILLRECEIVED_ACTION===bill_type="+bill_type+",bill_no="+bill_no);

                if (bill_type.equals("alipay_hb")) {
                    PayHelperUtils.startAPP(mContext);
                    sendBroadcast(mContext, "支付宝红包：" + bill_money + "金币");


                    QrBean qrBean = new QrBean();
                    qrBean.setOrder_id(bill_no);
                    qrBean.setMoney(bill_money);
                    qrBean.setMark_sell(bill_mark);
                    qrBean.setChannel(bill_type);
                    qrBean.setUid(Configer.getInstance().getUid(mContext));
                    qrBean.setChannelPayMsgResult(bill_paymsg);
                    qrBean.setBillTime(bill_time);
                    Intent broadCastIntent = new Intent()
                            .putExtra("data", qrBean.toString())
                            .setAction(HookConstants.RECEIVE_BILL_ALIPAYHB);
                    mContext.sendBroadcast(broadCastIntent);

                }else if(bill_type.equals("alipay_newzk")){

                    PayHelperUtils.startAPP(mContext);
                    sendBroadcast(mContext, "支付宝转卡：" + bill_money + "金币");

                    QrBean qrBean = new QrBean();
                    qrBean.setOrder_id(bill_no);
                    qrBean.setMoney(bill_money);
                    qrBean.setMark_sell("0");
                    qrBean.setChannel(bill_type);
                    qrBean.setUid(Configer.getInstance().getUid(mContext));
                    qrBean.setChannelPayMsgResult(bill_paymsg);
                    qrBean.setBillTime(bill_time);
                    Log.d("arik","支付宝银行卡收款qrbean=="+qrBean);
                    Intent broadCastIntent = new Intent()
                            .putExtra("data", qrBean.toString())
                            .setAction(HookConstants.RECEIVE_BILL_ALIPAYZK);
                    mContext.sendBroadcast(broadCastIntent);

                } else if(bill_type.equals("alipay_zz")
                        || bill_type.equals("alipay_qrcode_fix")
                        || bill_type.equals(Channel.ALIPAY_DY.getCode())){
                    PayHelperUtils.startAPP(mContext);
                    sendBroadcast(mContext, "支付宝扫码转账：" + bill_money + "金币");

                    QrBean qrBean = new QrBean();
                    qrBean.setOrder_id(bill_no);
                    qrBean.setMoney(bill_money);
                    qrBean.setMark_sell("0");
                    qrBean.setChannel(bill_type);
                    qrBean.setUid(Configer.getInstance().getUid(mContext));
                    qrBean.setChannelPayMsgResult(bill_paymsg);
                    qrBean.setBillTime(bill_time);
                    Log.d("arik","支付宝收款qrbean=="+qrBean);
                    Intent broadCastIntent = new Intent()
                            .putExtra("data", qrBean.toString())
                            .setAction(HookConstants.RECEIVE_BILL_ALIPAYZZ);
                    mContext.sendBroadcast(broadCastIntent);


                }else if(bill_type.equals("alipay_zz_bz")){

                    PayHelperUtils.startAPP(mContext);
                    sendBroadcast(mContext, "支付宝转账：" + bill_money + "金币");

                    QrBean qrBean = new QrBean();
                    qrBean.setOrder_id(bill_no);
                    qrBean.setMoney(bill_money);
                    qrBean.setMark_sell(bill_mark);
                    qrBean.setChannel(bill_type);
                    qrBean.setUid(Configer.getInstance().getUid(mContext));
                    qrBean.setChannelPayMsgResult(bill_paymsg);
                    qrBean.setBillTime(bill_time);

                    Log.d("arik","支付宝收款qrbean=="+qrBean);
                    Intent broadCastIntent = new Intent()
                            .putExtra("data", qrBean.toString())
                            .setAction(HookConstants.RECEIVE_BILL_ALIPAYZZ);
                    mContext.sendBroadcast(broadCastIntent);

                }else if(bill_type.equals("alipay_hb_kouling")){

                    PayHelperUtils.startAPP(mContext);
                    sendBroadcast(mContext, "支付宝口令红包：" + bill_money + "金币");

                    QrBean qrBean = new QrBean();
                    qrBean.setOrder_id(bill_no);
                    qrBean.setMoney(bill_money);
                    qrBean.setMark_sell(bill_mark);
                    qrBean.setChannel(bill_type);
                    qrBean.setUid(Configer.getInstance().getUid(mContext));
                    qrBean.setChannelPayMsgResult(bill_paymsg);
                    qrBean.setBillTime(bill_time);

                    Log.d("arik","支付宝口令红包收款qrbean=="+qrBean);
                    Intent broadCastIntent = new Intent()
                            .putExtra("data", qrBean.toString())
                            .setAction(HookConstants.RECEIVE_BILL_ALIPAYZZ);
                    mContext.sendBroadcast(broadCastIntent);

                }

                notifyapi(bill_type, bill_no, bill_money, bill_mark, bill_time);

            }

        } catch (Exception e) {
            PayHelperUtils.sendmsg(mContext, "BillReceived异常：" + e.getMessage());
            return;
        }

    }



    //提交收款信息
    public void notifyapi(String type, final String payChNo, String money, String mark, Date PaidAt) {
//        String account = SpUtils.getString(mContext, type);
//        new DBManager(mContext).updateOrder(type, money, mark, payChNo, PaidAt, SpUtils.getString(mContext, type));
//        String orderID = new DBManager(mContext).FindOrdersGetOrderID(type, money, mark);
        String orderID = "订单号是你们系统订单号";
//        String orderNO = new DBManager(mContext).FindOrdersGetOrderNO(type, money, mark);
//        String notifyUrl = new DBManager(mContext).FindOrdersGetNotifyUrl(type, money, mark);
//        if (TextUtils.isEmpty(orderID)) {//这边主要判断收款是否是订单收款
//            PayHelperUtils.sendmsg(mContext, "非商户" + (type.equals("alipay") ? "支付宝" : "微信") + "收款：" + "\n金额：￥" + money + "\n备注（订单号）：" + mark + "\n收款时间：" + Time.getStrTime(PaidAt) + "\n支付单号：" + payChNo);
//            return;
//        }
        Channel channel = Channel.getChannel(type);
        String channelType = "";
        switch (channel.getType().getCode()){
            case "alipay":
                channelType = "支付宝";
                break;
            case "wechat":
                channelType = "微信";
                break;
            case "unionpay":
                channelType = "云闪付";
                break;
            default:
                break;
        }
        PayHelperUtils.sendmsg(mContext, channelType + "订单已支付\n订单：" + orderID + "    金额：￥" + money + "\n备注（订单号）：" + mark + "\n收款时间：" + Time.getStrTime(PaidAt) + "\n支付单号：" + payChNo);

        //这边就是去发送回调的地方了

    }
    //播放语音
    public void sendBroadcast(final Context context, final String VoiceContent) {
        // 检查语音播放开关
        if(!Configer.getInstance().isPayVoiceSwitch(context)){
            return;
        }
        // 单个线程，排队播报
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                Intent broadCastIntent = new Intent();
                broadCastIntent.putExtra("VoiceContent", VoiceContent);
                broadCastIntent.setAction(SealAppContext.VOICE_ACTION);
                context.sendBroadcast(broadCastIntent);

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                }
            }
        });

    }
}