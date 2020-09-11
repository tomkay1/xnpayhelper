package com.arya1021.alipay.request.po;

import java.io.Serializable;

public class UserInfoResp implements Serializable {


    private static final long serialVersionUID = -4307668139031107384L;

    /**
     * id
     */
    private String uid;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 接口调用令牌
     */
    private String token;

    /**
     * 商户费率。单位万分之x。默认100即1%
     */
    private String userRate;

    /**
     * 登录用户名
     */
    private String loginName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 最近登录时间
     */
    private String lastLogin;

    /**
     * 登录ip
     */
    private String lastLoginIp;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRate() {
        return userRate;
    }

    public void setUserRate(String userRate) {
        this.userRate = userRate;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @Override
    public String toString() {
        return "UserInfoResp{" +
                "uid='" + uid + '\'' +
                ", createTime='" + createTime + '\'' +
                ", token='" + token + '\'' +
                ", userRate=" + userRate +
                ", loginName='" + loginName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                '}';
    }
}
