package com.arya1021.alipay.ws;

public final class MqTopic {

    private MqTopic(){}

    /**
     * 新订单消息
     */
    public static final String NEW_ORDER = "new.order";

    /**
     * 支付结果消息
     */
    public static final String PAY_RESULT = "pay.result";

    /**
     * 消息已确认
     */
    public static final String ACK = "ack";

    /**
     * 心跳包
     */
    public static final String HEARTBEAT = "heartbeat";

    /**
     * 客户端开/关收款服务
     */
    public static final String SWITCH_BY_CLIENT = "switch.client";
}
