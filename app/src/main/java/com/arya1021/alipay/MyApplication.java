package com.arya1021.alipay;

import android.app.Application;
import android.content.Context;

import com.arya1021.alipay.request.ServiceMain;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.robv.android.xposed.XposedBridge;

/**
 * @author shihy QQ:1336208558
 */
public class MyApplication extends Application {


    public static Context app;

    //记录崩溃信息
    final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setDebug(true);
        Logger.setTag("--NoHttp--收款助手--");

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5c4123e5");

        // 如果你需要自定义配置：
        NoHttp.initialize(this, new NoHttp.Config()
                .setNetworkExecutor(new OkHttpNetworkExecutor())
        );
        app = this;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                //获取崩溃时的UNIX时间戳
                long timeMillis = System.currentTimeMillis();
                //将时间戳转换成人类能看懂的格式，建立一个String拼接器
                StringBuilder stringBuilder = new StringBuilder(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeMillis)));
                stringBuilder.append(":\n");
                //获取错误信息
                stringBuilder.append(throwable.getMessage());
                stringBuilder.append("\n");
                //获取堆栈信息
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                stringBuilder.append(sw.toString());

                //这就是完整的错误信息了，你可以拿来上传服务器，或者做成本地文件保存等等等等
                String errorLog = stringBuilder.toString();
                ServiceMain.sendMsg(app,"app崩溃信息>>>>"+errorLog);
                XposedBridge.log("CrashLog>>>>>"+errorLog);

                //最后如何处理这个崩溃，这里使用默认的处理方式让APP停止运行
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

    }



}