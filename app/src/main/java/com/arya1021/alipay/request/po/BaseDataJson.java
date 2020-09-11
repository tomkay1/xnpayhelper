package com.arya1021.alipay.request.po;

/**
 * 返回JSON结果基础类
 * @author xin.chen
 *
 */
public class BaseDataJson<T> extends BaseResp{

	/**
	 * 返回结果
	 */
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public BaseDataJson(T data) {
		super();
		setData(data);
	}

	public BaseDataJson(){
		super();
	}

	public BaseDataJson(int code, String message) {
		super();
		setCode(code);
		setMsg(message);
	}

}
