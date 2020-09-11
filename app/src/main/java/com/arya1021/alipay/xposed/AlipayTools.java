package com.arya1021.alipay.xposed;

import android.content.Context;
import android.util.Log;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class AlipayTools {
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
    public static String getLoginId(ClassLoader classLoader) {

        try {

            Class AlipayApplication = classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication");
            Object AlipayApplicationInstance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");
            Object ApplicationContext = XposedHelpers.callMethod(AlipayApplicationInstance, "getMicroApplicationContext");


            Object AuthService = XposedHelpers.callMethod(ApplicationContext, "getExtServiceByInterface", "com.alipay.mobile.framework.service.ext.security.AuthService");
            Object UserInfo = XposedHelpers.callMethod(AuthService, "getUserInfo");
            String LogonId = (String) XposedHelpers.callMethod(UserInfo, "getLogonId");
            String UserId = (String) XposedHelpers.callMethod(UserInfo, "getUserId");
            XposedBridge.log("LogonId=" + LogonId);
            XposedBridge.log("UserId=" + UserId);
            return LogonId;
        } catch (Exception e) {
        }
        return null;

    }
    public static String getUserId(ClassLoader classLoader) {

        try {

            Class AlipayApplication = classLoader.loadClass("com.alipay.mobile.framework.AlipayApplication");
            Object AlipayApplicationInstance = XposedHelpers.callStaticMethod(AlipayApplication, "getInstance");
            Object ApplicationContext = XposedHelpers.callMethod(AlipayApplicationInstance, "getMicroApplicationContext");


            Object AuthService = XposedHelpers.callMethod(ApplicationContext, "getExtServiceByInterface", "com.alipay.mobile.framework.service.ext.security.AuthService");
            Object UserInfo = XposedHelpers.callMethod(AuthService, "getUserInfo");
            String LogonId = (String) XposedHelpers.callMethod(UserInfo, "getUserId");

            XposedBridge.log("UserId=" + LogonId);
            return LogonId;
        } catch (Exception e) {
        }
        return null;

    }
    public static Object receiveCrowd(ClassLoader classLoader, Object GiftCrowdReceiveReq) {
        try {


            Class GiftCrowdReceiveService = classLoader.loadClass(
                    "com.alipay.giftprod.biz.crowd.gw.GiftCrowdReceiveService");
            Object app = getAlipayApplication(classLoader);
            Object GiftCrowdReceiveServiceObj = getRpcProxy(app, GiftCrowdReceiveService);
            Object re = XposedHelpers.callMethod(GiftCrowdReceiveServiceObj, "receiveCrowd", GiftCrowdReceiveReq);
            return re;
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

    public static String getCookieStr(ClassLoader appClassLoader) {
        String cookieStr = "";
        // 获得cookieStr
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(
                "com.alipay.mobile.common.transportext.biz.appevent.AmnetUserInfo", appClassLoader), "getSessionid");
        Context context = (Context) XposedHelpers.callStaticMethod(XposedHelpers.findClass(
                "com.alipay.mobile.common.transportext.biz.shared.ExtTransportEnv", appClassLoader), "getAppContext");
        if (context != null) {
            Object readSettingServerUrl = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("com.alipay.mobile.common.helper.ReadSettingServerUrl", appClassLoader),
                    "getInstance");
            if (readSettingServerUrl != null) {
                // String gWFURL = (String)
                // XposedHelpers.callMethod(readSettingServerUrl, "getGWFURL",
                // context);
                String gWFURL = ".alipay.com";
                cookieStr = (String) XposedHelpers.callStaticMethod(XposedHelpers
                                .findClass("com.alipay.mobile.common.transport.http.GwCookieCacheHelper", appClassLoader),
                        "getCookie", gWFURL);
            } else {
                Log.d("arik","异常readSettingServerUrl为空");
            }
        } else {
            Log.d("arik","异常context为空");

        }
        return cookieStr;
    }
}
