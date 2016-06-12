package org.skynet.bgby.restserver;

import java.util.Set;

import org.skynet.bgby.driverproxy.DeviceManager;
import org.skynet.bgby.driverproxy.DeviceProfileManager;
import org.skynet.bgby.driverproxy.LayoutConfigManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.protocol.restmanagecommand.CmdGetLayout;
import org.skynet.bgby.protocol.restmanagecommand.CmdGetProfileByDevice;
import org.skynet.bgby.protocol.restmanagecommand.CmdHowAreYou;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RestManageCommandHandler extends RestCmdDispatcher implements IRestRequestHandler, IRestCommandHandler {
	protected LayoutConfigManager layoutManager;

	public LayoutConfigManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(LayoutConfigManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	protected Gson gson;

	public RestManageCommandHandler() {
		super();
		gson = new GsonBuilder().setPrettyPrinting().create();
	}

	@Override
	public boolean handleCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		IRestResponse rst = dispatchCommand(restRequest, restResponse);
		if (rst == null) {
			return false;
		}
		if (rst.getRequest() == null) {
			rst.setRequest(DriverUtils.getRequestFullUri(restRequest));
		}
		String strRst = gson.toJson(rst);
		restResponse.setAsString(strRst);
		return true;
	}

	@Override
	public void initHandlers() {
		// TODO
		IRestCommandHandler handler = new CmdHowAreYou();
		addHandler(CmdHowAreYou.CMD, handler);
		CmdGetLayout hGetLayout = new CmdGetLayout();

		hGetLayout.setLayoutManager(getLayoutManager());
		addHandler(CmdGetLayout.CMD, hGetLayout);

		CmdGetProfileByDevice hGetProfile = new CmdGetProfileByDevice();
		hGetProfile.setDeviceManager(new DeviceManager());
		hGetProfile.setLayoutManager(layoutManager);
		hGetProfile.setProfileManager(new DeviceProfileManager());
		addHandler(CmdGetProfileByDevice.CMD, hGetProfile);
		addHandler("help", this);
	}

	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		String helpContent = restRequest.getTarget();
		IRestResponse result = new RestResponseImpl();
		if (helpContent.equalsIgnoreCase("commands")){
			Set<String> commands = this.handlers.keySet();
			result.setData(commands);
		}else{
			result.setErrorCode(RestResponseError.ERR_UNKNOW_HELP_TOPIC.ordinal());
			result.setResult(RestResponseError.ERR_UNKNOW_HELP_TOPIC.toString());
		}
		return result;
	}

}
