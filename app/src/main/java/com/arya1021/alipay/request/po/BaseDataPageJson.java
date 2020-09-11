package com.arya1021.alipay.request.po;

import java.util.List;

/**
 * 返回JSON分页结果基础类
 * @author xin.chen
 *
 */
public class BaseDataPageJson<T> extends BaseResp{
	
    private long page;
    private long size;
    private long total;
    private long count;

	/**
	 * 返回结果
	 */
	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public BaseDataPageJson() {
		super();
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public BaseDataPageJson(long total, List<T> data) {
		super();
		this.total = total;
		this.data = data;
	}
	public BaseDataPageJson(int code, String message) {
		super();
		super.setCode(code);
		super.setMsg(message);
	}
}
