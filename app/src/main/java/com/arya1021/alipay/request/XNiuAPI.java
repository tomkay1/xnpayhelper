package com.arya1021.alipay.request;


import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.arya1021.alipay.MyApplication;
import com.arya1021.alipay.request.po.BaseDataJson;
import com.arya1021.alipay.request.po.Configer;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * @ Created by Dlg
 * @ <p>TiTle:  ApiBll</p>
 * @ <p>Description: 和服务端交互的业务类</p>
 * @ date:  2018/9/21
 */
public class XNiuAPI {
    private RequestQueue mQueue;
    private static String httpUrl ;

    private BaseDataCallback baseDataCallback;
    private Context mContext;

    public void setBaseDataCallback(BaseDataCallback baseDataCallback) {
        this.baseDataCallback = baseDataCallback;
    }

    public XNiuAPI(Context context) {
        mQueue = Volley.newRequestQueue(MyApplication.app);
        mContext = context;

        httpUrl = Configer.getInstance().getUrl(mContext) + "/aapi/v1/";
    }

    /**
     * 登录并绑定
     */
    public void loginAndBind(String loginName,String loginPwd) throws Exception {
        if(loginName == null || loginPwd == null){
            return;
        }
        Log.d("arik", "loginAndBind: 登录并绑定");
        // 加密
        Map<String,String> map = new HashMap<>();
        map.put("loginName",loginName);
        map.put("loginPwd",loginPwd);
        Gson gson = new Gson();

        mQueue.add(new APIPostRequest(httpUrl + "loginAndBind",gson.toJson(map),
                resultListener, errorListener));


    }

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //打印错误信息
            Log.e("arik", error.getMessage(), error);
        }
    };
    private final Response.Listener resultListener = new Response.Listener<BaseDataJson>() {


        @Override
        public void onResponse(BaseDataJson response) {
            if (response == null) {
                return;

            }
            if(baseDataCallback  != null){
                baseDataCallback.callback(response);
            }

        }
    };


}
