package org.skynet.bgby.protocol;

import java.util.Map;

import fi.iki.elonen.NanoHTTPD.Method;

public class RestRequestImpl implements IRestRequest {

	protected String command;
	protected String target;
	protected Map<String, String> params;
	protected String requestUri;
	protected String requestParameterString;
	protected Method method;
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	@Override
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	@Override
	public void setRequestParameterString(String requestParameterString) {
		this.requestParameterString = requestParameterString;
	}
	@Override
	public String getCommand() {
		return command;
	}
	@Override
	public void setCommand(String command) {
		this.command = command;
	}
	@Override
	public String getTarget() {
		return target;
	}
	@Override
	public void setTarget(String target) {
		this.target = target;
	}
	@Override
	public Map<String, String> getParams() {
		return params;
	}
	@Override
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	@Override
	public String getRequestUri() {
		return requestUri;
	}
	@Override
	public String getRequestParameterString() {
		return requestParameterString;
	}

}
