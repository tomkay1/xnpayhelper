package com.arya1021.alipay.xposed;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.arya1021.alipay.ThreadPoolManager;
import com.arya1021.alipay.request.HookConstants;
import com.arya1021.alipay.utlis.PayHelperUtils;
import com.arya1021.alipay.utlis.SealAppContext;
import com.arya1021.alipay.xposed.kouling.KoulingTools;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    //是否已经HOOK过微信或者支付宝了
    private boolean ALIPAY_PACKAGE_ISHOOK = false;

    public static Context ALIHOOK_CONTEXT = null;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
        if ((lpparam.appInfo == null) || ((lpparam.appInfo.flags & 0x81) != 0)) {
            return;
        }
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;

        if (SealAppContext.ALIPAY_PACKAGE.equals(packageName)) {
            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam p1) throws Throwable {
                        super.afterHookedMethod(p1);
                        Context context = (Context) p1.args[0];
                        ALIHOOK_CONTEXT = context;
                        ClassLoader appClassLoader = context.getClassLoader();
                        if (SealAppContext.ALIPAY_PACKAGE.equals(processName)
                                && !ALIPAY_PACKAGE_ISHOOK) {
                            ALIPAY_PACKAGE_ISHOOK = true;


                            AlipayReceived alipayReceived = new AlipayReceived();
                            IntentFilter intentFilter = new IntentFilter();
                            intentFilter.addAction(HookConstants.ALIPAY_KOULING);
                            context.registerReceiver(alipayReceived, intentFilter);

                            new AlipayHook().hook(appClassLoader, context);
                            PayHelperUtils.sendmsg(context, "支付宝Hook成功，当前版本:" + PayHelperUtils.getVerName(context));
                        }
                    }
                });
            } catch (Throwable e) {
                Log.d("arik","--------Hook支付宝错误：" + e.getMessage());

                PayHelperUtils.sendmsg(ALIHOOK_CONTEXT, "Hook支付宝错误：" + e.getMessage());
            }
        }

    }

    class AlipayReceived extends BroadcastReceiver {

        public void onReceive(final Context context, Intent intent) {
            try {

                if (intent.getAction().contentEquals(HookConstants.ALIPAY_KOULING)) {


                    final String tradeNo = intent.getStringExtra("tradeNo");
                    final String kouling = intent.getStringExtra("kouling");

                    Log.d("arik","准备领取红包口令："+tradeNo+",口令="+kouling);


                    ThreadPoolManager.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            KoulingTools.openCmdHB(context,tradeNo,kouling);
                        }
                    });


                }
            } catch (Exception e) {
                XposedBridge.log("领取口令红包失败:" + e.getMessage());
            }
        }
    }

}