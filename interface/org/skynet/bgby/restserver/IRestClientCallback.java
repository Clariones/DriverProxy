package org.skynet.bgby.restserver;

import org.skynet.bgby.protocol.IHttpResponse;

public interface IRestClientCallback {
	void onRestResponse(IRestClientContext context, IHttpResponse response);
}
