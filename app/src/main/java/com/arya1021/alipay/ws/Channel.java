package com.arya1021.alipay.ws;

public enum Channel {

    ALIPAY_HB(ChannelType.ALIPAY,"alipay_hb",true,"zfb红包收款"),
    ALIPAY_ZZ(ChannelType.ALIPAY,"alipay_zz",true,"zfb转账"),
    ALIPAY_ZZ_BZ(ChannelType.ALIPAY,"alipay_zz_bz",true,"zfb转账备注订单号"),
    ALIPAY_ZZ_NOBZ(ChannelType.ALIPAY,"alipay_zz_nobz",true,"zfb转帐不加备注靠金额锁定"),
    ALIPAY_JHYZZ_BZ(ChannelType.ALIPAY,"alipay_jhyzz_bz",true,"加好友zfb转帐备注订单号"),
    ALIPAY_JHYZZ_NOBZ(ChannelType.ALIPAY,"alipay_jhyzz_nobz",true,"加好友zfb转帐不加备注靠金额锁定"),
    ALIPAY_QRCODE_AUTO(ChannelType.ALIPAY,"alipay_qrcode_auto",true,"zfb自动二维码收款"),
    ALIPAY_QRCODE_FIX(ChannelType.ALIPAY,"alipay_qrcode_fix",false,"zfb固定二维码收款（随机立减，并发有限制）"),
    ALIPAY_QRCODE_HALF(ChannelType.ALIPAY,"alipay_qrcode_half",true,"zfb半自动二维码收款（付款人主动备注订单号）"),

    ALIPAY_NEW_ZZ(ChannelType.ALIPAY,"alipay_newzz",true,"新转账"),
    ALIPAY_HB_KOULING(ChannelType.ALIPAY,"alipay_hb_kouling",false,"口令红包"),
    ALIPAY_DY(ChannelType.ALIPAY,"alipay_dy",false,"店员通"),
    ALIPAY_NEW_ZK(ChannelType.ALIPAY,"alipay_newzk",true,"新转卡"),

    WECHAT_QRCODE_AUTO(ChannelType.WECHAT,"wechat_qrcode_auto",true,"wx自动二维码收款"),
    WECHAT_QRCODE_FIX(ChannelType.WECHAT,"wechat_qrcode_fix",false,"wx浮动金额二维码收款"),
    WECHAT_QRCODE_MANUAL(ChannelType.WECHAT,"wechat_qrcode_manual",true,"wx手动二维码收款"),

    UNIONPAY_QRCODE_AUTO(ChannelType.UNIONPAY,"unionpay_qrcode_auto",true,"ysf自动二维码收款（有备注不用输入金额）"),
    UNIONPAY_QRCODE_FIX(ChannelType.UNIONPAY,"unionpay_qrcode_fix",true,"ysf浮动金额二维码收款(没有备注不用输入金额)"),
    UNIONPAY_QRCODE_MANUAL(ChannelType.UNIONPAY,"unionpay_qrcode_manual",false,"ysf手动二维码收款（没有备注需要输入金额）");



    private ChannelType type;
    private String code;
    private boolean needGenQrcode;
    private String desc;

    Channel(ChannelType type,String code,boolean needGenQrcode, String desc){
        this.type = type;
        this.code = code;
        this.needGenQrcode = needGenQrcode;
        this.desc = desc;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
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
    public static Channel getChannel(String channel){
        for(Channel cnl : Channel.values()){
            if(cnl.getCode().equals(channel)){
                return cnl;
            }
        }
        return null;
    }
}
