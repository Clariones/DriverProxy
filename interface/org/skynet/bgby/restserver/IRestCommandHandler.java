package org.skynet.bgby.restserver;

import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.IRestRequest;

public interface IRestCommandHandler {
	public IRestResponse handleCommand(IRestRequest restRequest);

}
