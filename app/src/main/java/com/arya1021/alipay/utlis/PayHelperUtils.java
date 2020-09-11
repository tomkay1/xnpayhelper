package com.arya1021.alipay.utlis;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import com.arya1021.alipay.Main2Activity;

import java.util.List;
import java.util.Map;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;


public class PayHelperUtils {

    public static String getVerName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            sendmsg(context, "getVerName异常" + e.getMessage());
        }
        return "";
    }

    //判断应用程序是否运行中
    public static boolean isAppRunning(Context context, String packageName) {
        @SuppressLint("WrongConstant")
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        if (list.iterator().hasNext()) {
            ActivityManager.RunningTaskInfo info = list.iterator().next();
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    //判断应用程序是否在后台运行
    public static boolean isBackgroundRunning(Context context, String processName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

        if (activityManager == null) return false;
        // get running application processes
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
            if (process.processName.startsWith(processName)) {
                boolean isBackground = process.importance != IMPORTANCE_FOREGROUND && process.importance != IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
                if (isBackground || isLockedState) return true;
                else return false;
            }
        }
        return false;
    }

    /*** 判断应用是否在运行* @param context* @return*/
    public static boolean isRun(Context context,String MY_PKG_NAME) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                Log.i("ActivityService isRun()", info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                break;
            }
        }
        Log.i("ActivityService isRun()", "com.ad 程序  ...isAppRunning......" + isAppRunning);
        return isAppRunning;
    }

    public static void sendAppMsg(String money, String mark, String type, Context context) {
        Intent broadCastIntent = new Intent();
        if (type.equals("alipay")) {
            broadCastIntent.setAction(SealAppContext.ALIPAYSTART_ACTION);
        } else if (type.equals("wechat")) {
            Log.d("wechat","准备生成微信二维码");
            broadCastIntent.setAction(SealAppContext.WECHATSTART_ACTION);
        }
        broadCastIntent.putExtra("mark", mark);
        broadCastIntent.putExtra("money", money);
        context.sendBroadcast(broadCastIntent);
    }

    public static void sendLoginId(Map<String,String> userInfo, String loginId, String userId, String type, Context context) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(SealAppContext.LOGINIDRECEIVED_ACTION);
        broadCastIntent.putExtra("type", type);
        broadCastIntent.putExtra("loginid", loginId);
        broadCastIntent.putExtra("userId", userId);
        if(userInfo!= null && userInfo.keySet().size() > 0){
            for(Map.Entry<String,String> entry : userInfo.entrySet()){
                broadCastIntent.putExtra(entry.getKey(), entry.getValue());
            }
        }
        context.sendBroadcast(broadCastIntent);
    }
    public static void sendLoginId(String loginId, String userId, String type, Context context) {
        sendLoginId(null,loginId,userId,type,context);
    }

    public static void sendmsg(Context context, String msg) {
        Intent broadCastIntent = new Intent();
        broadCastIntent.putExtra("msg", msg);
        broadCastIntent.setAction(SealAppContext.MSGRECEIVED_ACTION);
        context.sendBroadcast(broadCastIntent);
    }

    public static void startAPP(Context context) {
        try {
            Intent intent = new Intent(context, Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        } catch (Exception e) {
        }
    }

    public static void startAPP(Context context, String appPackageName) {
        try {
            /*Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            context.startActivity(intent);
            return;*/

            Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
            if (intent != null) {
                intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }

        } catch (Exception e) {
            sendmsg(context, "startAPP异常" + e.getMessage());
        }
    }

    public static Intent getAppOpenIntentByPackageName(Context context,String packageName){
        //Activity完整名
        String mainAct = null;
        //根据包名寻找
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    public static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName,
                        Context.CONTEXT_IGNORE_SECURITY
                                | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }

    public static boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }
}
