package com.arya1021.alipay.ws;

public enum ChannelType {

    ALIPAY("alipay","支付宝"),
    WECHAT("wechat","微信"),
    UNIONPAY("unionpay","云闪付");

    private String code;
    private String desc;

    ChannelType(String code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
