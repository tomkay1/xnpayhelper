package com.arya1021.alipay.request.po;


/**
 * api返回基类
 * @author xin.chen
 *
 */
public class BaseResp {

	/**
	 * 返回码,0表示接口调用正常，非0为异常
	 */
	private int code = 0;

	/**
	 *提示信息
	 */
	private String msg= "";

	private Long timestamp = System.currentTimeMillis();
	
	private String version = "1.0";
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String message) {
		this.msg = message;
	}
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public BaseResp() {
		super();
	}

	public BaseResp(int code, String message) {
		super();
		this.code = code;
		this.msg = message;
	}
    public static BaseResp buildFailed(int code,String msg){
    	return new BaseResp(code, msg);
    }
}
