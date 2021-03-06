package org.skynet.bgby.restserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;

public interface IRestClient {
	IHttpResponse synchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request)
			throws IOException;

	void asynchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request,
			IRestClientCallback callback);

	IHttpResponse synchPost(InetSocketAddress serverAddress, String contentRoot, IRestRequest request,
			byte[] postContent) throws IOException;
}
