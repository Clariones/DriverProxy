package org.skynet.bgby.restserver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.restserver.IRestRequestHandler;

public abstract class RestCmdDispatcher {
	protected Map<String, IRestCommandHandler> handlers;

	public abstract void initHandlers();
	
	protected void addHandler(String cmd, IRestCommandHandler handler) {
		if (handlers == null) {
			handlers = new HashMap<String, IRestCommandHandler>();
		}
		handlers.put(cmd, handler);
	}

	protected IRestResponse dispatchCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		IRestCommandHandler handler = handlers.get(restRequest.getCommand());
		if (handler == null) {
			return null;
		}
		DriverUtils.log(Level.FINE, this.getClass().getName(), "Dispatch {0} to {1}",
				new String[] { restRequest.getCommand(), handler.getClass().getSimpleName() });
		return handler.handleCommand(restRequest);
	}
}
