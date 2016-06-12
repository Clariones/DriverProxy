package org.skynet.bgby.restserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;

public interface IRestClient {
	IHttpResponse synchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request)
			throws IOException;

	void asynchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request,
			IRestClientCallback callback);
}
