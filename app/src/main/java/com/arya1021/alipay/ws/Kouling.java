package com.arya1021.alipay.ws;

import java.io.Serializable;

public class Kouling implements Serializable {

    private static final long serialVersionUID = 252785585549310750L;

    private String tradeNo;

    private String kouling;

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getKouling() {
        return kouling;
    }

    public void setKouling(String kouling) {
        this.kouling = kouling;
    }
}
