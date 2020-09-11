package com.arya1021.alipay.ws;

import java.io.Serializable;

public class BaseMsg<T> implements Serializable {


    private static final long serialVersionUID = 5977712267193935542L;

    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息发送方用户id
     */
    private String fromUserId = "0";
    /**
     * 消息接收方用户id
     */
    private String toUserId = "0";

    /**
     * 消息内容
     */
    private T content;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
