package org.skynet.bgby.command.management;

import java.util.List;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.error.ErrorCode;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.layout.LayoutUtils;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestCommandHandler;

/**
 * Request "/getUILayout/<UI controller ID>" will get the corresponding
 * 
 * @author Clariones
 *
 */
public class CmdGetLayout extends BaseManageCmd implements IRestCommandHandler {
	public static final String CMD = "getUILayout";
	public static final int ERR_CONTROLLER_NOT_FOUND = ErrorCode.MNGCMD_GET_LAYOUT_START_CODE + 1;
	
	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		IRestResponse result = new RestResponseImpl();
		result.setRequest(DriverUtils.getRequestFullUri(restRequest));
		LayoutManager lm = getLayoutManager();
		String controllerID = restRequest.getTarget();
		List<LayoutData> layout = getLayoutManager().getControllerLayout(controllerID);
		if (layout == null) {
			result.setErrorCode(ERR_CONTROLLER_NOT_FOUND);
			result.setResult("Cannot found controller " + controllerID);
		} else {
			String layoutJson = LayoutUtils.toJson(layout);
//			result.setData(layoutJson);
			result.setData(layout);
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
