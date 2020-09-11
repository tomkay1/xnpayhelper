package com.arya1021.alipay.request.po;

import com.alibaba.fastjson.JSON;

import java.util.Date;


/**
 * @ Created by Dlg
 * @ <p>TiTle:  QrBean</p>
 * @ <p>Description: 这个最基本的二维码信息Bean类了</p>
 * @ date:  2018/9/21
 */
public class QrBean {

    //以后自己可以添加更多支付方式，没必要用枚举
    public static final String WECHAT = "wechat";
    public static final String ALIPAY = "alipay";
    public static final String UNIONPAY = "unionpay";
    public static final String ALIPAYBANK = "alipaybank";

    /**
     * 这个是服务器上此订单的id，这个暂时不加用处
     */
    private Integer id;

    /**
     * 渠道类型
     */
    private String channel;//wechat,alipay

    /**
     * 二维码的金额,单位为元 精确到后2为。例：9.89
     */
    private String money;

    /**
     * 此而二维码的链接(实为二维码内容)
     */
    private String url;

    /**
     * 二维码的收款方备注
     */
    private String mark_sell;

    /**
     * 二维码的付款方备注
     */
    private String mark_buy;

    /**
     * 订单id(支付通道的订单id)
     */
    private String order_id;

    /**
     * 商户uid
     */
    private String uid;

    /**
     * 支付通道支付结果
     */
    private String channelPayMsgResult;

    /**
     * 付款时间
     */
    private Date billTime;

    public String getChannelPayMsgResult() {
        return channelPayMsgResult;
    }

    public void setChannelPayMsgResult(String channelPayMsgResult) {
        this.channelPayMsgResult = channelPayMsgResult;
    }

    public Integer getId() {
        return id == null ? 0 : id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannel() {
        return channel == null ? "" : channel;
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

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMark_sell() {
        return mark_sell == null ? "" : mark_sell;
    }

    public void setMark_sell(String mark_sell) {
        this.mark_sell = mark_sell;
    }

    public String getMark_buy() {
        return mark_buy == null ? "" : mark_buy;
    }

    public void setMark_buy(String mark_buy) {
        this.mark_buy = mark_buy;
    }

    public String getOrder_id() {
        return order_id == null ? "" : order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public Date getBillTime() {
        return billTime;
    }

    public void setBillTime(Date billTime) {
        this.billTime = billTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
