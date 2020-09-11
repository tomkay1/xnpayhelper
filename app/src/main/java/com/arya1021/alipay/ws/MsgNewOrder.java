package com.arya1021.alipay.ws;

import java.io.Serializable;

public class MsgNewOrder implements Serializable {

    private static final long serialVersionUID = -5404617950142028964L;

    /**
     * 通道名称
     */
    private String channel;
    /**
     * 订单金额。单位：元。精确小数点后2位。例：1030.00
     */
    private String money;
    /**
     * 用户实际需要支付的金额。单位：元。精确小数点后2位。例：1021.16
     */
    private String realMoney;

    /**
     * 平台返回的订单号
     */
    private String tradeNo;

    /**
     * 收款账号
     */
    private String channelAccount;

    /**
     * 商户id
     */
    private String uid;

    /**
     * 支付二维码内容
     */
    private String qrcode;

    /**
     * 是否从APP端生成二维码。false则从服务端获取
     */
    private Boolean needAppGenQrCode = true;

    /**
     * 是否需要设置收款备注订单号
     */
    private Boolean needSetOrderMark = true;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(String realMoney) {
        this.realMoney = realMoney;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public Boolean getNeedAppGenQrCode() {
        return needAppGenQrCode;
    }

    public void setNeedAppGenQrCode(Boolean needAppGenQrCode) {
        this.needAppGenQrCode = needAppGenQrCode;
    }

    public String getChannelAccount() {
        return channelAccount;
    }

    public void setChannelAccount(String channelAccount) {
        this.channelAccount = channelAccount;
    }

    public Boolean getNeedSetOrderMark() {
        return needSetOrderMark;
    }

    public void setNeedSetOrderMark(Boolean needSetOrderMark) {
        this.needSetOrderMark = needSetOrderMark;
    }

    @Override
    public String toString() {
        return "MsgNewOrder{" +
                "channel='" + channel + '\'' +
                ", money='" + money + '\'' +
                ", realMoney='" + realMoney + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", channelAccount='" + channelAccount + '\'' +
                ", uid='" + uid + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", needAppGenQrCode=" + needAppGenQrCode +
                ", needSetOrderMark=" + needSetOrderMark +
                '}';
    }
}
