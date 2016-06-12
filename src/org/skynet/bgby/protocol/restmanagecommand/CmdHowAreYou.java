package org.skynet.bgby.protocol.restmanagecommand;

import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestCommandHandler;

/**
 * Request http://ip:port/howAreYou/anything will get response "I'm fine. And you ?"
 * @author Clariones
 *
 */
public class CmdHowAreYou implements IRestCommandHandler{
	public static final String CMD = "howAreYou";
	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		IRestResponse result = new RestResponseImpl();
		result.setData("I'm fine. And you?");
		return result;
	}

}
