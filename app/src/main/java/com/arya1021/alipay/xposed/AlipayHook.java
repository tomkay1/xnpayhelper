package com.arya1021.alipay.xposed;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arya1021.alipay.request.PayUtils;
import com.arya1021.alipay.utlis.PayHelperUtils;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.utlis.StringUtils;
import com.arya1021.alipay.ws.Channel;
import com.arya1021.alipay.xposed.HookUtils.Tools;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class AlipayHook {

    public static ClassLoader mClassLoader = null;

    public void hook(final ClassLoader classLoader, final Context context) {
        securityCheckHook(classLoader);

        mClassLoader = classLoader;


        try {

            XposedHelpers.findAndHookMethod(
                    "com.alipay.android.phone.messageboxstatic.biz.dao.ServiceDao", classLoader,
                    "insertMessageInfo",
                    findClass(
                            "com.alipay.android.phone.messageboxstatic.biz.db.ServiceInfo",
                            classLoader), String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {


                                String str = PayUtils.getMidText((String) XposedHelpers.callMethod(param.args[0],
                                        "toString", new Object[0]), "extraInfo='", "'").replace("\\", "");
                                Log.d("arik", "支付宝收到支付订单1111111>>>>>>>>" + str);

                                String billType = Channel.ALIPAY_QRCODE_FIX.getCode();
                                String money = "";
                                String mark = "";
                                String billNo = String.valueOf(System.currentTimeMillis());
                                JSONObject jsonObject = null;
                                JSONObject bizMonitorJO = null;


                                // value双引号处理 避免json解析异常
                                str = str.replace("\"bizMonitor\":\"","\"bizMonitor\":");
                                str = str.replace("}\",\"cardLink\"","},\"cardLink\"");


                                // 转卡到账
                                if ((str.contains("你的银行卡")) || (str.contains("通过支付宝"))) {
                                    //str = PayUtils.getAlipayCookieStr(appClassLoader);

                                    money = PayUtils.getMidText(str, "转账", "元已到账");
                                    mark = PayUtils.getMidText(str, "{\"assistMsg1\":\"", "通过支付宝");
                                    Log.d("arik", "支付宝收到支付订单======" + money + " mark==" + mark);

                                    billType = Channel.ALIPAY_NEW_ZK.getCode();

                                }
                                // 店员通
                                else if (str.contains("店员通")){
                                    billType = Channel.ALIPAY_DY.getCode();

                                    money = StringUtils.getTextCenter(str, "mainAmount\":\"", "\",\"mainTitle");

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


                                    jsonObject = JSON.parseObject(str);
                                    String assistMsg1 = jsonObject.getString("assistMsg1");

                                    // 第几笔收款
                                    String ss = assistMsg1.substring(0,assistMsg1.indexOf("收款")+1);
                                    // 订单号格式：yyyyMMdd+'今日第x笔收'+money
                                    billNo = sdf.format(new Date()) + ss + money;

                                }

                                else {

                                    jsonObject = JSON.parseObject(str);
                                    bizMonitorJO = JSON.parseObject(jsonObject.getString("bizMonitor"));

                                    String content = jsonObject.getString("content");

                                    if( !content.contains("收款金额￥") ){
                                        Log.d("arik","统计的支付消息，忽略..");
                                        return;
                                    }
                                    money = content.replace("收款金额￥","");
                                }



                                Intent broadCastIntent = new Intent();
                                //TODO 交易单号处理
                                //broadCastIntent.putExtra("bill_no", bizMonitorJO.getString("msgId"));
                                broadCastIntent.putExtra("bill_no", billNo);
                                broadCastIntent.putExtra("bill_money", money);
                                broadCastIntent.putExtra("bill_mark", "");
                                long bill_time = System.currentTimeMillis();//个人收款暂时以当前时间为准

                                broadCastIntent.putExtra("bill_time", bill_time);
                                broadCastIntent.putExtra("bill_type", billType);


                                broadCastIntent.setAction(SealAppContext.BILLRECEIVED_ACTION);
                                context.sendBroadcast(broadCastIntent);

                            } catch (Exception i) {
                                i.printStackTrace();
                                Log.d("arik" ,"支付宝收到支付订单出错"+i.getMessage());
                            }
                        }
                    }
            );


            Class<?> insertTradeMessageInfo = findClass("com.alipay.android.phone.messageboxstatic.biz.dao.TradeDao", classLoader);
            XposedBridge.hookAllMethods(insertTradeMessageInfo, "insertMessageInfo", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Log.d("arik","支付宝收到支付订单22222222222>>>>>>>>>>>");
                        Object object = param.args[0];
                        String MessageInfo = (String) XposedHelpers.callMethod(object, "toString");
                        Log.d("arik",MessageInfo);
                        String content = PayUtils.getMidText(MessageInfo, "content='", "'");

                        String zzbzStr = PayUtils.getMidText(MessageInfo, "收款理由：\"},", ",{\"");
                        Log.d("arik",zzbzStr);
                        if (content.contains("二维码收款") || content.contains("收到一笔转账")
                                || content.contains("成功收款")) {
                            JSONObject jsonObject = JSON.parseObject(content);
                            Log.d("arik",jsonObject.toJSONString());
                            String money = jsonObject.getString("content");
                            String mark = jsonObject.getString("assistMsg2");

                            String assistMsg1 = jsonObject.getString("assistMsg1");

                            money = money.replace("￥", "").replace(" ", "");
                            String tradeNo = PayUtils.getMidText(MessageInfo, "tradeNO=", "&");
                            if (TextUtils.isEmpty(mark)) {
                                return;
                            }
                            Log.d("arik","收到支付宝支付订单：" + tradeNo + "|" + money + "|" + mark);


                            Intent broadCastIntent = new Intent();
                            broadCastIntent.putExtra("bill_no", tradeNo);
                            broadCastIntent.putExtra("bill_money", money);
                            broadCastIntent.putExtra("bill_mark", mark);
                            long bill_time = System.currentTimeMillis();//个人收款暂时以当前时间为准

                            broadCastIntent.putExtra("bill_time", bill_time);
                            broadCastIntent.putExtra("bill_type", "alipay_zz");
                            //TODO 转账备注
                            Log.d("arik","===="+(assistMsg1.equals("收到一笔转账") && (!"".equals(mark) && mark!= null)));
                            if(assistMsg1.equals("收到一笔转账")){
                                broadCastIntent.putExtra("bill_type", "alipay_zz");
                            }
                            broadCastIntent.setAction(SealAppContext.BILLRECEIVED_ACTION);
                            context.sendBroadcast(broadCastIntent);

                        }
                    } catch (Exception e) {
                        Log.d("alipay","支付宝订单获取错误：" + e.getMessage());
                    }
                    super.beforeHookedMethod(param);
                }
            });

            final Class clazzSyncMessage = XposedHelpers.findClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage", classLoader);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.alipay.mobile.socialchatsdk.chat.data.ChatDataSyncCallback",
                    classLoader), "onReceiveMessage", clazzSyncMessage,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object object = param.args[0];
                            org.json.JSONArray msgArr = new org.json.JSONArray(clazzSyncMessage.getField("msgData").get(object).toString());
                            for (int i = 0; i < msgArr.length(); i++) {
                                org.json.JSONObject msg1 = msgArr.getJSONObject(i);
                                Log.d("arik","alipay message>>>"+msg1.toString());
                                String pl = msg1.getString("pl");
                                Object wireIns = XposedHelpers.newInstance(XposedHelpers.findClass("com.squareup.wire.Wire", classLoader), new ArrayList<Class>());
                                Object decode_pl = XposedHelpers.callMethod(wireIns, "parseFrom", Base64.decode(pl, 0),
                                        XposedHelpers.findClass("com.alipay.mobilechat.core.model.message.MessagePayloadModel", classLoader));
                                String decode_pl_str = JSON.toJSONString(decode_pl);
                                final com.alibaba.fastjson.JSONObject decode_pl_json = JSON.parseObject(decode_pl_str);
                                String biz_type = decode_pl_json.getString("biz_type");
                                Log.d("arik","alipay>>>>decode_pl_json===="+decode_pl_json);
                                if (biz_type.equals("CHAT")) {
                                    //TODO 聊天信息
                                } else if (biz_type.equals("GIFTSHARE")) {
                                    // 休眠个1s，防止风控
                                    Thread.sleep(1000);

                                    String link = decode_pl_json.getString("link") + "#";
                                    final String socialCardCMsgId = decode_pl_json.getString("client_msg_id");
                                    final String prevBiz = "chat";
                                    final String sign = StringUtils.getTextCenter(link, "sign=", "#");
                                    final String crowdNo = StringUtils.getTextCenter(link, "crowdNo=", "&");
                                    final String socialCardToUserId = decode_pl_json.getString("to_u_id");
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Class ServiceUtil = XposedHelpers.findClass("com.alipay.mobile.beehive.util.ServiceUtil", classLoader);
                                            Class RpcService = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", classLoader);
                                            Class giftCrowdReceive = XposedHelpers.findClass("com.alipay.giftprod.biz.crowd.gw.GiftCrowdReceiveService", classLoader);
                                            final Object RpcProxy = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(ServiceUtil,
                                                    "getServiceByInterface", RpcService), "getRpcProxy", giftCrowdReceive);
                                            Object requstObj = XposedHelpers.newInstance(XposedHelpers.findClass(
                                                    "com.alipay.giftprod.biz.crowd.gw.request.GiftCrowdReceiveReq", classLoader));
                                            XposedHelpers.setObjectField(requstObj, "crowdNo", crowdNo);
                                            XposedHelpers.setObjectField(requstObj, "prevBiz", prevBiz);
                                            XposedHelpers.setObjectField(requstObj, "receiverUserType", "1");
                                            XposedHelpers.setObjectField(requstObj, "sign", sign);
                                            XposedHelpers.setObjectField(requstObj, "clientMsgID", socialCardCMsgId);
                                            XposedHelpers.setObjectField(requstObj, "receiverId", socialCardToUserId);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("canLocalMessage", "Y");
                                            map.put("feedId", "");
                                            map.put("receiverUserType", "1");
                                            XposedHelpers.setObjectField(requstObj, "extInfo", map);
                                            Object giftCrowdDetailResult = XposedHelpers.callMethod(RpcProxy, "receiveCrowd", requstObj);
                                            Object _giftCrowdInfo = getFieldValue(giftCrowdDetailResult, "giftCrowdInfo");
                                            String money = (String) getFieldValue(_giftCrowdInfo, "amount");
                                            Object receiveMoneyInfo = getFieldValue(giftCrowdDetailResult, "giftCrowdFlowInfo");
                                            String moneyReceived = (String) getFieldValue(receiveMoneyInfo, "receiveAmount");

                                            String prodCode = (String) getFieldValue(_giftCrowdInfo, "prodCode");
                                            String remark = (String) getFieldValue(_giftCrowdInfo, "remark");
                                            String crowdNo = (String) getFieldValue(_giftCrowdInfo, "crowdNo");

                                            if (prodCode.equals("CROWD_COMMON_CASH")) {
                                                Intent broadCastIntent = new Intent();
                                                broadCastIntent.putExtra("bill_no", crowdNo);
                                                broadCastIntent.putExtra("bill_money", moneyReceived);
                                                broadCastIntent.putExtra("bill_mark", remark);
                                                long bill_time = System.currentTimeMillis();//个人收款暂时以当前时间为准

                                                broadCastIntent.putExtra("bill_time", bill_time);
                                                broadCastIntent.putExtra("bill_type", Channel.ALIPAY_HB.getCode());

                                                broadCastIntent.setAction(SealAppContext.BILLRECEIVED_ACTION);
                                                context.sendBroadcast(broadCastIntent);

                                                //删除好友
                                                //delete(decode_pl_json, classLoader);
                                                delectContact(decode_pl_json,classLoader);
                                            }
                                        }
                                    }
                                    ).start();
                                }
                            }
                        }
                    });
            XposedHelpers.findAndHookMethod("com.alipay.mobile.quinox.LauncherActivity", classLoader, "onResume", new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                    hookAlipayUserInfo(classLoader, context);
                }
            });

        } catch (Error e) {
            PayHelperUtils.sendmsg(context, "异常1" + e.getMessage());
        } catch (Exception e) {
            PayHelperUtils.sendmsg(context, "异常2" + e.getMessage());
        }
    }


    private void hookAlipayUserInfo(ClassLoader appClassLoader, Context context) {
        Object callMethod = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.framework.AlipayApplication", appClassLoader),
                "getInstance"), "getMicroApplicationContext");
        Object callMethod2 = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.alipay.mobile.common.helper.UserInfoHelper", appClassLoader),
                "getInstance"), "getUserInfo", callMethod);
        if (callMethod2 == null) return;

        String logonId = (String) XposedHelpers.callMethod(callMethod2, "getLogonId");
        String userId = (String) XposedHelpers.callMethod(callMethod2, "getUserId");
        String nickname = (String) XposedHelpers.callMethod(callMethod2, "getNick");
        String realName = (String) XposedHelpers.callMethod(callMethod2, "getRealName");
        String userAvatar = (String) XposedHelpers.callMethod(callMethod2, "getUserAvatar");

        /*Class userInfoClzz = (Class) XposedHelpers.callMethod(callMethod2, "getClass");
        for(Method method : userInfoClzz.getDeclaredMethods()){
            Log.d("arik","method===="+method.getName());
            *//*if(method.getName().startsWith("get")){
                String value = (String) XposedHelpers.callMethod(callMethod2, method.getName());
                Log.d("arik","收款助手>>alipay userinfo>>>>"+method.getName()+"==="+value);
            }*//*
        }*/
        Map<String,String> infoMap = new HashMap<>();
        infoMap.put("nickname",nickname);
        infoMap.put("realName",realName);
        infoMap.put("userAvatar",userAvatar);


        PayHelperUtils.sendLoginId(infoMap,logonId, userId, "alipay", context);
    }


    private void securityCheckHook(ClassLoader classLoader) {
        try {
            Class<?> securityCheckClazz = XposedHelpers.findClass("com.alipay.mobile.base.security.CI", classLoader);
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", String.class, String.class, String.class, new XC_MethodHook() {
                protected void afterHookedMethod(MethodHookParam p1) throws Throwable {
                    Object object = p1.getResult();
                    XposedHelpers.setBooleanField(object, "a", false);
                    p1.setResult("object");
                    super.afterHookedMethod(p1);
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", Class.class, String.class, String.class, new XC_MethodReplacement() {
                protected Object replaceHookedMethod(MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                    return Byte.valueOf("1");
                }
            });
            ;
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", ClassLoader.class, String.class, new XC_MethodReplacement() {
                protected Object replaceHookedMethod(MethodHookParam paramAnonymousMethodHookParam) throws Throwable {
                    return Byte.valueOf("1");
                }
            });
            XposedHelpers.findAndHookMethod(securityCheckClazz, "a", new XC_MethodReplacement() {
                protected Object replaceHookedMethod(MethodHookParam paramAnonymousMethodHookParam) {
                    return Boolean.valueOf(false);
                }
            });
            return;
        } catch (Exception e) {
            Log.d("arik",e.getMessage());
        }
    }

    public static void sendMsg(String str, String str2) {
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(
                "com.alipay.mobile.socialchatsdk.chat.sender.MessageFactory", mClassLoader),
                "createTextMsg", str, "1", str2, null, null, Boolean.valueOf(false));
    }


    public static Object getFieldValue(Object obj, String FieldName) {
        Object obj2 = null;
        if (obj != null) {
            Field field = XposedHelpers.findFieldIfExists(obj.getClass(), FieldName);
            if (field != null) {
                try {
                    obj2 = field.get(obj);
                } catch (Exception e) {
                }
            }
        }
        return obj2;
    }

    private void delete(com.alibaba.fastjson.JSONObject decode_pl_json, ClassLoader classLoader) {
        String from_login_id = decode_pl_json.getString("from_login_id");
        String from_u_id = decode_pl_json.getString("from_u_id");

        sendMsg(from_u_id,"自动充值成功！");
        Class ServiceUtil = XposedHelpers.findClass("com.alipay.mobile.beehive.util.ServiceUtil", classLoader);
        Class RpcService = XposedHelpers.findClass("com.alipay.mobile.framework.service.common.RpcService", classLoader);
        Class alipayRelationManageService = XposedHelpers.findClass(
                "com.alipay.giftprod.biz.crowd.gw.GiftCrowdReceiveService",
                classLoader);
        final Object RpcProxy = XposedHelpers.callMethod(XposedHelpers.callStaticMethod(ServiceUtil,
                "getServiceByInterface", RpcService),
                "getRpcProxy", alipayRelationManageService);
        Object handleRelationReq = XposedHelpers.newInstance(XposedHelpers.findClass
                ("com.alipay.giftprod.biz.crowd.gw.request.GiftCrowdReceiveReq", classLoader));
        XposedHelpers.setObjectField(handleRelationReq, "targetUserId", from_u_id);
        XposedHelpers.setObjectField(handleRelationReq, "alipayAccount", from_login_id);
        XposedHelpers.setObjectField(handleRelationReq, "bizType", "2");
        Object re = XposedHelpers.callMethod(RpcProxy, "handleRelation", handleRelationReq);
        Log.e("========", "删除好友结果" + JSON.toJSONString(re));
        Log.d("arik", "删除好友结果" + JSON.toJSONString(re));
    }
    public static void  delectContact(com.alibaba.fastjson.JSONObject decode_pl_json,ClassLoader mClassLoader){
    /*    HandleRelationReq handleRelationReq = new HandleRelationReq();
        handleRelationReq.targetUserId = profileSettingActivity.k.userId;
        handleRelationReq.bizType = "2";
        handleRelationReq.alipayAccount = profileSettingActivity.k.account;
        BaseResult handleRelation = profileSettingActivity.u.handleRelation(handleRelationReq);*/

        String from_login_id = decode_pl_json.getString("from_login_id");
        String from_u_id = decode_pl_json.getString("from_u_id");

        /*String userid = AlipayTools.getUserId(mClassLoader);
        Object contactAccount= AlipayTools.getContactAccount(mClassLoader,userid);
        String userId =XposedHelpers.getObjectField(contactAccount,"userId")+"";
        String account =XposedHelpers.getObjectField(contactAccount,"account")+"";*/

        Object handleRelationReq =XposedHelpers.newInstance(XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.req.HandleRelationReq",mClassLoader));
        XposedHelpers.setObjectField(handleRelationReq,"targetUserId",from_u_id);
        XposedHelpers.setObjectField(handleRelationReq,"alipayAccount",from_login_id);
        XposedHelpers.setObjectField(handleRelationReq,"bizType","2");
        Object service =  Tools.getRpcService( Tools.getAlipayApplication(mClassLoader));
        XposedBridge.log("service "+service);
        Object alipayRelationManageService= XposedHelpers.callMethod(service,"getRpcProxy",XposedHelpers.findClass("com.alipay.mobilerelation.biz.shared.rpc.AlipayRelationManageService",mClassLoader));
        Object re=XposedHelpers.callMethod(alipayRelationManageService,"handleRelation",handleRelationReq);
        XposedBridge.log("删除好友结果"+JSON.toJSONString(re));

        Log.d("arik","删除好友结果"+JSON.toJSONString(re));
    }
}