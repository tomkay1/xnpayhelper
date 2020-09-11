package com.arya1021.alipay.request.po;


import java.io.Serializable;

/**
 * api请求通用参数
 * @author xin.chen
 *
 */
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BaseReq implements Serializable {


    private static final long serialVersionUID = -817530164344568826L;
    /**
     * 注册的商户uid
     */
    private String uid;

    /**
     * 签名。计算规则参考接口文档说明
     */
    private String sign;

    /**
     * 当前时间戳（单位毫秒）
     */
    private String timestamp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BaseReq() {
    }

    public BaseReq( String uid,  String sign,  String timestamp) {
        this.uid = uid;
        this.sign = sign;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "BaseReq{" +
                "uid='" + uid + '\'' +
                ", sign='" + sign + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
