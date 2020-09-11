package com.arya1021.alipay.xposed.kouling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class GetC implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        XposedBridge.log("GetC:" + method.getName());
        return null;
    }
}
