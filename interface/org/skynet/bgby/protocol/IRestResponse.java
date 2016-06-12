package org.skynet.bgby.protocol;

public interface IRestResponse {

	void setData(Object data);

	Object getData();

	void setRequest(String string);

	String getRequest();

	void setErrorCode(int errorCode);

	int getErrorCode();

	void setResult(String result);

	String getResult();

}