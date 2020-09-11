package com.arya1021.alipay.ws;

public enum OrderStatus {

    WAIT_QRCODE(0,"待生成支付url"),
    WAIT_PAY(1,"待支付"),
    PAY_SUCC(2,"支付成功"),
    TIMEOUT(3,"订单超时");

    private int code;
    private String desc;

    OrderStatus(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

}
