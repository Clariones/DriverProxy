package pkg;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CommonData {
	protected String result;
	protected int errorCode;
	protected String request;
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	
	protected JsonElement data;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public JsonElement getData() {
		return data;
	}
	public void setData(JsonObject data) {
		this.data = data;
	}
	
	
}
