package org.skynet.bgby.protocol;

public class RestResponseImpl implements IRestResponse {
	protected String result = "success";
	protected int errorCode = 0;
	protected String request;
	protected Object data;
	@Override
	public String getResult() {
		return result;
	}
	@Override
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public int getErrorCode() {
		return errorCode;
	}
	@Override
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	@Override
	public String getRequest() {
		return request;
	}
	@Override
	public void setRequest(String request) {
		this.request = request;
	}
	@Override
	public Object getData() {
		return data;
	}
	@Override
	public void setData(Object data) {
		this.data = data;
	}
	
}
