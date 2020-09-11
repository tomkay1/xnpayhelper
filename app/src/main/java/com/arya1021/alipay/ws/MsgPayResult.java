package com.arya1021.alipay.ws;

import java.io.Serializable;

/**
 * 支付结果
 */
public class MsgPayResult implements Serializable {


    private static final long serialVersionUID = 7889934468449954933L;

    /**
     * 通道账号
     */
    private String channelAccount;
    /**
     * 订单号（支付通道的订单id）
     */
    private String orderId;
    /**
     * 通道名称
     */
    private String channel;
    /**
     * 用户实际支付的金额。单位：元。精确小数点后2位。例：1021.16
     */
    private String realMoney;

    /**
     * 平台返回的订单号
     */
    private String tradeNo;

    /**
     * 商户id
     */
    private String uid;

    /**
     * 通道支付结果消息
     */
    private String channelPayMsgResult;

    /**
     * 扩展json字段
     */
    private String extJson;

    /**
     * 付款时间
     */
    private String billTime;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public String getChannelPayMsgResult() {
        return channelPayMsgResult;
    }

    public void setChannelPayMsgResult(String channelPayMsgResult) {
        this.channelPayMsgResult = channelPayMsgResult;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getChannelAccount() {
        return channelAccount;
    }

    public void setChannelAccount(String channelAccount) {
        this.channelAccount = channelAccount;
    }

    public String getExtJson() {
        return extJson;
    }

    public void setExtJson(String extJson) {
        this.extJson = extJson;
    }

    public String getBillTime() {
        return billTime;
    }

    public void setBillTime(String billTime) {
        this.billTime = billTime;
    }

    @Override
    public String toString() {
        return "MsgPayResult{" +
                "channelAccount='" + channelAccount + '\'' +
                ", orderId='" + orderId + '\'' +
                ", channel='" + channel + '\'' +
                ", realMoney='" + realMoney + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", uid='" + uid + '\'' +
                ", channelPayMsgResult='" + channelPayMsgResult + '\'' +
                ", extJson='" + extJson + '\'' +
                ", billTime=" + billTime +
                '}';
    }
}
