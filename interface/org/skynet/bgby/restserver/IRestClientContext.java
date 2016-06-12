package org.skynet.bgby.restserver;

import java.net.InetSocketAddress;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;

public interface IRestClientContext {

	void setResponse(IHttpResponse response);

	IHttpResponse getResponse();

	void setRequest(IRestRequest request);

	IRestRequest getRequest();

	void setContentRoot(String contentRoot);

	String getContentRoot();

	void setServerAddress(InetSocketAddress serverAddress);

	InetSocketAddress getServerAddress();

}
