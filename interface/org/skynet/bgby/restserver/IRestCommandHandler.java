package org.skynet.bgby.restserver;

import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;

public interface IRestCommandHandler {
	public IRestResponse handleCommand(IRestRequest restRequest);

	public String getUsage();
}
