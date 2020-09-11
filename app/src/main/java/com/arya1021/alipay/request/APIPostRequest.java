package com.arya1021.alipay.request;

import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.arya1021.alipay.request.po.BaseDataJson;

import java.io.UnsupportedEncodingException;

/**
 * @ Created by Dlg
 * @ <p>TiTle:  FastJsonRequest</p>
 * @ <p>Description: 和服务器HTTP请求的返回统一构造类
 * @ 统一设置好超时和重试次数，自动添加token和返回序列化好的BaseMsg</p>
 * @ date:  2018/9/30
 */
public class APIPostRequest extends JsonRequest<BaseDataJson> {

    public APIPostRequest(String url, String requestBody,Response.Listener<BaseDataJson> listener, @Nullable Response.ErrorListener errorListener) {
        super(Method.POST, url, requestBody, listener, errorListener);
        Log.i("arik", "APIPostRequest: " + url);
        setRetryPolicy(new DefaultRetryPolicy(20000, 0, 0));
    }

    @Override
    protected Response<BaseDataJson> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.i("arik", "post返回值: " + jsonString);
            return Response.success(
                    JSON.parseObject(jsonString, BaseDataJson.class), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
         return Response.error(new ParseError(je));
        }
    }



}