package com.arya1021.alipay.ws;

import java.io.Serializable;
import java.util.List;

public class Heartbeat implements Serializable {

    private static final long serialVersionUID = -8845376909035360387L;

    private String deviceInfo;
    private String imei;
    private List<ChannelAccount> list;

    public Heartbeat() {
    }

    public class ChannelAccount{

        private String channelType;
        private String channelAccount;

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

        @Override
        public String toString() {
            return "ChannelAccount{" +
                    "channelType='" + channelType + '\'' +
                    ", channelAccount='" + channelAccount + '\'' +
                    '}';
        }
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public List<ChannelAccount> getList() {
        return list;
    }

    public void setList(List<ChannelAccount> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "deviceInfo='" + deviceInfo + '\'' +
                ", imei='" + imei + '\'' +
                ", list=" + list +
                '}';
    }
}
