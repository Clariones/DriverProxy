package org.skynet.bgby.restserver;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;

public interface IRestRequestHandler {
	/**
	 * 
	 * @param restRequest
	 * @param restResponse
	 * @return true: This command had been handled by me. Don't pass to any other handlers<p/>
	 *  false: Please pass this command to other handlers
	 */
	public boolean handleCommand(IRestRequest restRequest, IHttpResponse restResponse);
}
