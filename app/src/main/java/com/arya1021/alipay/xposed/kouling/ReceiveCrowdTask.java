package com.arya1021.alipay.xposed.kouling;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.xposed.HookUtils.Tools;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class ReceiveCrowdTask extends AsyncTask {
    ClassLoader classLoader;
    String crowdNo;
    String groupId;
    String sign;
    String prevBiz;
    String clientMsgID;
    String receiverUserType;
    String feedId;
    String channelName;
    String communityId;
    String receiverId;
    String id;
    Context context;
    String tradeNo;
    public ReceiveCrowdTask(ClassLoader classLoader,
                            String crowdNo, String groupId,
                            String sign, String prevBiz,
                            String clientMsgID, String receiverUserType,
                            String feedId, String channelName,
                            String communityId, Context context,String id,
                            String tradeNo) {

        this.classLoader = classLoader;
        this.crowdNo = crowdNo;
        this.groupId = groupId;
        this.sign = sign;
        this.prevBiz = prevBiz;
        this.clientMsgID = clientMsgID;
        this.receiverUserType = receiverUserType;
        this.feedId = feedId;
        this.channelName = channelName;
        this.communityId = communityId;
        this.context = context;
        this.id = id;
        receiverId = Tools.getUserId(classLoader);
        this.tradeNo = tradeNo;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            Thread.sleep(3000);
            /*
              alipays://platformapi/startapp?appId=88886666
                    &appClearTop=
                    &target=groupPre
                    &bizType=CROWD_COMMON_CASH
                    &crowdNo=201901090206302200000000180034829143
                    &universalDetail=true
                    &clientVersion=10.test.img1.test.img1-5
                    &schemeMode=portalInside
                    &prevBiz=chat
                    &sign=55dd871ee20888ad6de2f6053ed9fbd118ea79475b33885aa6c2d08ff730f8fdx
             */

           String mineId= Tools.getUserId(classLoader);
            Class GiftCrowdReceiveReq = classLoader.loadClass(
                    "com.alipay.giftprod.biz.crowd.gw.request.GiftCrowdReceiveReq");
            Object GiftCrowdReceiveReqObj = XposedHelpers.newInstance(GiftCrowdReceiveReq);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "crowdNo", crowdNo);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "groupId", groupId);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "receiverUserType", receiverUserType);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "prevBiz", prevBiz);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "sign", sign);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "clientMsgID", clientMsgID);
            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "receiverId", receiverId);

            HashMap extInfo = new HashMap();
            extInfo.put("feedId", "");
            extInfo.put("canLocalMessage", "Y");
            extInfo.put("channelName", channelName);
            extInfo.put("communityId", channelName);
            extInfo.put("receiverUserType", receiverUserType);


            XposedHelpers.setObjectField(GiftCrowdReceiveReqObj, "extInfo", extInfo);
            Object re = Tools.receiveCrowd(classLoader, GiftCrowdReceiveReqObj);

            if (re == null) {
                XposedBridge.log("领红包失败");
                return null;


            }

            Object giftCrowdFlowInfo = XposedHelpers.getObjectField(re, "giftCrowdFlowInfo");
            String receiveAmount = (String) XposedHelpers.getObjectField(giftCrowdFlowInfo, "receiveAmount");
            String crowdNo = (String) XposedHelpers.getObjectField(giftCrowdFlowInfo, "crowdNo");
            String  receiveDateDesc=(String) XposedHelpers.getObjectField(giftCrowdFlowInfo, "receiveDateDesc");
            Object receiver = XposedHelpers.getObjectField(giftCrowdFlowInfo, "receiver");
            String alipayAccount = (String) XposedHelpers.getObjectField(receiver, "alipayAccount");
            String userId = (String) XposedHelpers.getObjectField(receiver, "userId");
            String userName = (String) XposedHelpers.getObjectField(receiver, "userName");
            String imgUrl = (String) XposedHelpers.getObjectField(receiver, "imgUrl");
            Object giftCrowdInfo = XposedHelpers.getObjectField(re, "giftCrowdInfo");
            Object creator = XposedHelpers.getObjectField(giftCrowdInfo, "creator");
            String sendUserId = (String) XposedHelpers.getObjectField(creator, "userId");
            String remark = (String) XposedHelpers.getObjectField(giftCrowdInfo, "remark");
            XposedBridge.log("re:" + JSON.toJSONString(re));
            if(TextUtils.equals(userId,sendUserId)){
                return null;
            }
         /*   XposedBridge.log("xposed======userId:"+userId+"-----mineId:"+mineId+"----clientMsgID:"+clientMsgID+"-----receiverId:"+receiverId
            +"----clientMsgID:"+clientMsgID+"----:sendUserId"+sendUserId);*/
            Class fa = classLoader.loadClass("com.alipay.mobile.redenvelope.proguard.f.a");
            Object faObj = XposedHelpers.newInstance(fa);
            XposedHelpers.setObjectField(faObj, "a", crowdNo);
            XposedHelpers.setObjectField(faObj, "b", clientMsgID);
            XposedHelpers.setObjectField(faObj, "c", receiverUserType);
            XposedHelpers.setObjectField(faObj, "d", receiverId);
            XposedHelpers.setObjectField(faObj, "e", groupId);
            XposedHelpers.setObjectField(faObj, "f", prevBiz);
            XposedHelpers.setObjectField(faObj, "g", sign);

            Class b = classLoader.loadClass("com.alipay.android.phone.discovery.envelope.get.b");
            Class c = classLoader.loadClass("com.alipay.android.phone.discovery.envelope.get.c");

            Object ca = Proxy.newProxyInstance(classLoader, new Class[]{c}, new GetC());
            Object bObj = XposedHelpers.newInstance(b, new Class[]{c}, ca);
            XposedHelpers.callMethod(bObj, "a", faObj, false, true, re);


            // 领红包成功，发送支付结果消息

            Intent broadCastIntent = new Intent();
            broadCastIntent.putExtra("bill_no", crowdNo);
            broadCastIntent.putExtra("bill_money", receiveAmount);
            broadCastIntent.putExtra("bill_mark", tradeNo);
            long bill_time = System.currentTimeMillis();//个人收款暂时以当前时间为准

            broadCastIntent.putExtra("bill_time", bill_time);
            broadCastIntent.putExtra("bill_type", "alipay_hb_kouling");

            broadCastIntent.setAction(SealAppContext.BILLRECEIVED_ACTION);
            context.sendBroadcast(broadCastIntent);


        } catch (Exception e) {
           // Tool.printException(e);
        }
        return null;
    }
}
