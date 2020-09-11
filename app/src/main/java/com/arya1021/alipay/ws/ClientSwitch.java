package com.arya1021.alipay.ws;

import java.io.Serializable;

public class ClientSwitch implements Serializable {

    private static final long serialVersionUID = 136321615284954609L;

    private String channelType;
    private String channelAccount;

    /**
     * 开关状态1开启，0关闭
     */
    private int status = 1;

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelAccount() {
        return channelAccount;
    }

    public void setChannelAccount(String channelAccount) {
        this.channelAccount = channelAccount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
