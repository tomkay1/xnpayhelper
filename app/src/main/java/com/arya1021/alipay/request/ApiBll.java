package com.arya1021.alipay.request;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.arya1021.alipay.MyApplication;
import com.arya1021.alipay.request.po.Configer;
import com.arya1021.alipay.request.po.QrBean;
import com.arya1021.alipay.ws.Channel;
import com.arya1021.alipay.ws.ChannelType;
import com.arya1021.alipay.ws.MqTopic;
import com.arya1021.alipay.ws.MsgPayResult;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ Created by Dlg
 * @ <p>TiTle:  ApiBll</p>
 * @ <p>Description: 和服务端交互的业务类</p>
 * @ date:  2018/9/21
 */
public class ApiBll {
    private RequestQueue mQueue;

    public ApiBll() {
        mQueue = Volley.newRequestQueue(MyApplication.app);
    }

    /**
     * 检查是否需要发送新二维码
     */
    public void checkQR() {
//        if (!Configer.getInstance().getUrl().toLowerCase().startsWith("http")) {
//            return;//防止首次启动还没有配置，就一直去轮循
//        }
        Log.d("arik", "checkQR: 检查收款码任务");
        //增加判断，只有在启用状态才会轮循获取任务
        // 微信
//        if(ServiceMain.mIsWechatRunning)mQueue.add(new FastJsonRequest(httpUrl + "?command=ask&sn=" + Configer.getInstance().getSN() + "&user=" + Configer.getInstance().getUserWechat(), succ, null));

        // 支付宝
        /*mQueue.add(new FastJsonRequest(httpUrl + "?command=ask&sn=" +
                Configer.getInstance().getSN() + "&user=" + Configer.getInstance().getUserAlipay(),
                succ, null));
*/
        // 云闪付
//        if(ServiceMain.mIsUnionpayRunning)mQueue.add(new FastJsonRequest(httpUrl + "?command=ask&sn=" + Configer.getInstance().getSN() + "&user=" + Configer.getInstance().getUser_unionpay(), succ, null));
        //mQueue.start();
    }


//    ;


    /**
     * 发送服务器所需要的二维码字符串给服务器
     * 服务器如果有新订单，就会立马返回新的订单，手机端就不用再等下次轮循了
     *
     * @param url       当前二维码的字符串
     * @param mark_sell 收款方的备注
     */
    public void sendQR(String url, String mark_sell) throws UnsupportedEncodingException {


        /*StringBuilder stringBuilder = new StringBuilder(httpUrl)
                .append("?command=addqr")
                .append("&url=")
                .append(URLEncoder.encode(url, "utf-8"))
                .append("&mark_sell=")
                .append(URLEncoder.encode(mark_sell, "utf-8"))
                .append("&sn=")
                .append(URLEncoder.encode(Configer.getInstance().getSN(), "utf-8"));
        mQueue.add(new FastJsonRequest(stringBuilder.toString(), succ, null));
        //LogUtils.show(stringBuilder.toString());
        dealTaskList();*/
    }


    /**
     * 向服务器发送支付成功的消息
     * 如果因为一些原因，暂时没有通知成功，会保存任务，下次再尝试
     *
     * @param qrBean 订单详情信息
     */
    public void payQR(Context context,final QrBean qrBean) {

        Gson gson = new Gson();
        StringBuilder url = null;
        try {

            MsgPayResult msgPayResult = new MsgPayResult();
            String channelAccount = "";
            ChannelType channelType = Channel.getChannel(qrBean.getChannel()).getType();
            switch (channelType){
                case ALIPAY:
                    channelAccount = Configer.getInstance().getCurrentAlipay(context);
                    break;
                case WECHAT:
                    channelAccount = Configer.getInstance().getCurrentWechat(context);
                    break;
                case UNIONPAY:
                    channelAccount = Configer.getInstance().getCurrentUnionpay(context);
                    break;
                default:
                    break;
            }
            msgPayResult.setChannelAccount(channelAccount);
            msgPayResult.setOrderId(qrBean.getOrder_id());
            msgPayResult.setChannel(qrBean.getChannel());
            msgPayResult.setRealMoney(qrBean.getMoney());
            msgPayResult.setUid(qrBean.getUid());
            msgPayResult.setTradeNo(qrBean.getMark_sell());
            msgPayResult.setChannelPayMsgResult(qrBean.getChannelPayMsgResult());
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            msgPayResult.setBillTime(sdf.format(new Date()));
            if(qrBean.getBillTime() != null){
                msgPayResult.setBillTime(sdf.format(qrBean.getBillTime()));
            }

            Log.d("arik","send pay result:"+msgPayResult);
            com.arya1021.alipay.ws.BaseMsg baseMsg = new com.arya1021.alipay.ws.BaseMsg();
            baseMsg.setFromUserId(qrBean.getUid());
            baseMsg.setTopic(MqTopic.PAY_RESULT);
            baseMsg.setContent(msgPayResult);

            ServiceMain.sendMsg(context,gson.toJson(baseMsg));


            switch (channelType){
                case ALIPAY:
                    break;
                case WECHAT:
                    break;
                case UNIONPAY:

                    Intent broadCastIntent = new Intent();
                    broadCastIntent.setAction(HookConstants.UNIONPAY_ORDER_FINISH);
                    broadCastIntent.putExtra("bill_no", qrBean.getOrder_id());
                    context.sendBroadcast(broadCastIntent);

                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            Log.d("arik",e.getMessage());
        }
    }

}
