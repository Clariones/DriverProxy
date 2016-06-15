package org.skynet.bgby.command.management;

import java.util.List;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.layout.LayoutUtils;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestCommandHandler;
import org.skynet.bgby.restserver.RestResponseError;

/**
 * Request "/getUILayout/<UI controller ID>" will get the corresponding
 * 
 * @author Clariones
 *
 */
public class CmdGetLayout extends BaseManageCmd implements IRestCommandHandler {
	public static final String CMD = "getUILayout";

	protected LayoutManager layoutManager;

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		IRestResponse result = new RestResponseImpl();
		result.setRequest(DriverUtils.getRequestFullUri(restRequest));
		LayoutManager lm = getLayoutManager();
		String controllerID = restRequest.getTarget();
		List<ILayout> layout = getLayoutManager().getControllerLayout(controllerID);
		if (layout == null) {
			result.setErrorCode(RestResponseError.ERR_CONTROLLER_NOT_FOUND.ordinal());
			result.setResult(RestResponseError.ERR_CONTROLLER_NOT_FOUND.toString());
		} else {
			String layoutJson = LayoutUtils.toJson(layout);
			result.setData(layoutJson);
		}
		return result;
	}

	@Override
	public String getCommand() {
		return CMD;
	}

	@Override
	public String getUsage() {
		return getCommand() + ": Get the layout data for required controller.\r\n" + "Example:    http://<ip:port>/"
				+ getCommand() + "/<controller ID>";
	}

}
