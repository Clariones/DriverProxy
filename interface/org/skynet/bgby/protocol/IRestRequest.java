package org.skynet.bgby.protocol;

import java.util.Map;

public interface IRestRequest {
	
	String getRequestUri();
	
	String getRequestParameterString();
	
	void setParams(Map<String, String> params);

	Map<String, String> getParams();

	void setTarget(String target);

	String getTarget();

	void setCommand(String command);

	String getCommand();

	void setRequestParameterString(String requestParameterString);

	void setRequestUri(String requestUri);

}
