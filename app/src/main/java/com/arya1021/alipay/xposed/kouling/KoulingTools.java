package com.arya1021.alipay.xposed.kouling;

import android.content.Context;
import android.net.Uri;

import com.arya1021.alipay.xposed.AlipayTools;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class KoulingTools {

    public static Object getAlipayApplication(ClassLoader classLoader) {
        try {
            Class AlipayApplication = classLoader.loadClass("com.alipay.mobile.framework.LauncherApplicationAgent");

            Object AlipayApplicationInstance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");

            Object ApplicationContext = XposedHelpers.callMethod(AlipayApplicationInstance, "getMicroApplicationContext");
            XposedBridge.log("ApplicationContext cn:" + ApplicationContext.getClass().getName());
            return ApplicationContext;

        } catch (Exception e) {
        }
        return null;
    }


    public static Object getRpcService(Object AlipayApplication) {
        try {

            // Application application=(Application) XposedHelpers.callMethod(AlipayApplication,"getApplicationContext");

            ClassLoader classLoader = AlipayApplication.getClass().getClassLoader();
            Class RpcService = classLoader.loadClass("com.alipay.mobile.framework.service.common.RpcService");
            return XposedHelpers.callMethod(AlipayApplication, "findServiceByInterface", RpcService.getName());
        } catch (Exception e) {
        }
        return null;
    }

    public static Object getRpcProxy(Object AlipayApplication, Class face) {
        try {

            Object RpcService = getRpcService(AlipayApplication);
            return XposedHelpers.callMethod(RpcService, "getRpcProxy", face);
        } catch (Exception e) {
        }
        return null;
    }





    public static void openCmdHB(Context context, String tradeNo,String cmd) {

        try {


            ClassLoader classLoader = context.getClassLoader();


            Class d = classLoader.loadClass("com.alipay.mobile.redenvelope.proguard.u.b");




            Object dObj = XposedHelpers.newInstance(d,context, "EnvelopeHome");

            Object da = XposedHelpers.getObjectField(dObj, "a");
            String db = (String) XposedHelpers.getObjectField(dObj, "b");

            HashMap v3_4 = new HashMap();
            String user = AlipayTools.getLoginId(classLoader);
            String appkey = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            ((Map) v3_4).put("user", user);
            ((Map) v3_4).put("appkey", appkey);
            ((Map) v3_4).put("pageName", "EnvelopeHome");
            ((Map) v3_4).put("refPageName", "");
            ((Map) v3_4).put("pbswitch", "v3");
            XposedHelpers.callMethod(da, "onPage", context, v3_4, false );


            String oneid_ua_data = (String) XposedHelpers.callMethod(dObj, "a");

            Class ShortCodeEnterRequest = classLoader.loadClass("com.alipay.giftprod.biz.front.models.ShortCodeEnterRequest");
            Object ShortCodeEnterRequestObj = XposedHelpers.newInstance(ShortCodeEnterRequest);

            HashMap hashMap = new HashMap();
            XposedBridge.log("oneid_ua_data:" + oneid_ua_data);
            hashMap.put("oneid_ua_data", oneid_ua_data);


            XposedHelpers.setObjectField(ShortCodeEnterRequestObj, "extInfo", hashMap);
            XposedHelpers.setObjectField(ShortCodeEnterRequestObj, "shortCode", cmd);

            Class ShortCodeEnterService = classLoader.loadClass("com.alipay.giftprod.biz.front.rpc.ShortCodeEnterService");
            Object ShortCodeEnterServiceObj = getRpcProxy(getAlipayApplication(classLoader), ShortCodeEnterService);


            Object ShortCodeEnterRsqObj = XposedHelpers.callMethod(ShortCodeEnterServiceObj, "shortCodeEnter", ShortCodeEnterRequestObj);
            XposedBridge.log("openCmdHB:" + new Gson().toJson(ShortCodeEnterRsqObj));
            boolean success = XposedHelpers.getBooleanField(ShortCodeEnterRsqObj, "success");
            String resultCode = (String) XposedHelpers.getObjectField(ShortCodeEnterRsqObj, "resultCode");
            String resultDesc = (String) XposedHelpers.getObjectField(ShortCodeEnterRsqObj, "resultDesc");
            String resultView = (String) XposedHelpers.getObjectField(ShortCodeEnterRsqObj, "resultView");
            String prodCode = (String) XposedHelpers.getObjectField(ShortCodeEnterRsqObj, "prodCode");
            String url = (String) XposedHelpers.getObjectField(ShortCodeEnterRsqObj, "url");


            if (!success) {

                XposedBridge.log("openCmdHB 口令红包打开失败 resultDesc:" + resultDesc);
                XposedBridge.log("openCmdHB 口令红包打开失败 resultView:" + resultView);
                XposedBridge.log("openCmdHB 口令红包打开失败 resultCode:" + resultCode);

                if(resultView.startsWith("已经错"))
                {
                    String dd=resultView.substring(resultView.indexOf("已经错"),4).replace("已经错","");
                    XposedBridge.log("openCmdHB 口令红包打开失败 已经错次数:"+dd + resultCode);
                }

                return;
            }

            Uri uri = Uri.parse(url);
            String bizType = uri.getQueryParameter("bizType");
            String crowdNo = uri.getQueryParameter("crowdNo");
            String sign = uri.getQueryParameter("sign");
            String prevBiz = uri.getQueryParameter("prevBiz");
            String universalDetail = uri.getQueryParameter("universalDetail");
            String target = uri.getQueryParameter("target");

            XposedBridge.log("openCmdHB  bizType:" + bizType);
            XposedBridge.log("openCmdHB  crowdNo:" + crowdNo);
            XposedBridge.log("openCmdHB  sign:" + sign);
            XposedBridge.log("openCmdHB  universalDetail:" + universalDetail);
            XposedBridge.log("openCmdHB  target:" + target);


            new ReceiveCrowdTask(classLoader, crowdNo, null,
                    sign, prevBiz, null, null, null,
                    null, null,context,cmd,tradeNo)
                    .execute();

        } catch (Exception e) {
        }
    }



}
