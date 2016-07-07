package org.skynet.bgby.command.management;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestandard.DeviceStandard;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverproxy.DriverProxyService;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.error.ErrorCode;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutManager;
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
public class CmdGetProxyData extends BaseManageCmd implements IRestCommandHandler {
	public static final String CMD = "getProxyData";
	public static final String DATE_TYPE_LAYOUT = "layout";
	public static final String DATE_TYPE_PROFILE = "profile";
	public static final String DATE_TYPE_DEVICE = "deviceStatus";
	public static final String DATE_TYPE_DEVICE_CFG = "device";
	public static final String DATE_TYPE_STANDARD = "standard";

	public static final int ERR_INVALID_DATA_TYPE = ErrorCode.MNGCMD_GET_DATA_START_CODE + 1;
	public static final int ERR_INVALID_STANDARD_ID = ErrorCode.MNGCMD_GET_DATA_START_CODE + 2;
	public static final int ERR_INVALID_DEVICE_ID = ErrorCode.MNGCMD_GET_DATA_START_CODE + 3;
	public static final int ERR_HAS_NO_ANY_DEVICE = ErrorCode.MNGCMD_GET_DATA_START_CODE + 4;
	public static final int ERR_INVALID_PROFILE_ID = ErrorCode.MNGCMD_GET_DATA_START_CODE + 5;
	public static final int ERR_HAS_NO_ANY_PROFILE = ErrorCode.MNGCMD_GET_DATA_START_CODE + 6;
	public static final int ERR_INVALID_CONTROLLER_ID = ErrorCode.MNGCMD_GET_DATA_START_CODE + 7;
	public static final int ERR_HAS_NO_ANY_LAYOUT = ErrorCode.MNGCMD_GET_DATA_START_CODE + 8;
	
	
	
	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		IRestResponse result = new RestResponseImpl();
		result.setRequest(DriverUtils.getRequestFullUri(restRequest));
		String target = restRequest.getTarget();
		switch (target) {
		case DATE_TYPE_LAYOUT:
			handleGetLayout(restRequest, result, getIdParam(restRequest));
			break;
		case DATE_TYPE_PROFILE:
			handleGetProfile(restRequest, result, getIdParam(restRequest));
			break;
		case DATE_TYPE_DEVICE:
			handleGetDevice(restRequest, result, getIdParam(restRequest));
			break;
		case DATE_TYPE_DEVICE_CFG:
			handleGetDeviceCfg(restRequest, result, getIdParam(restRequest));
			break;
		case DATE_TYPE_STANDARD:
			handleGetStandard(restRequest, result, getIdParam(restRequest));
			break;
		default:
			result.setErrorCode(ERR_INVALID_DATA_TYPE);
			result.setResult("Cannot understand data type: " + target);
			result.setData(getUsage());
		}

		
		return result;
	}

	private void handleGetDeviceCfg(IRestRequest restRequest, IRestResponse result, String id) {
		DeviceConfigManager mng = this.getDeviceConfigManager();
		if (id != null){
			DeviceConfigData data = mng.getDeviceConfigData(id);
			setResult(result, new RMap(id,data), ERR_INVALID_DEVICE_ID, "Unknown device: " + id);
			return;
		}
		Map<String, DeviceConfigData> list = mng.listAllDevices();
		setResult(result, list, ERR_HAS_NO_ANY_DEVICE, "Has no any device");
	}

	protected String getIdParam(IRestRequest restRequest) {
		if (restRequest.getParams() == null || restRequest.getParams().isEmpty()){
			return null;
		}
		return restRequest.getParams().get("id");
	}

	protected void handleGetStandard(IRestRequest restRequest, IRestResponse result, String id) {
		Map<String, DeviceStandard> standards = DriverProxyService.deviceStandards;
		if (id != null){
			DeviceStandard std = standards.get(id);
			setResult(result, new RMap(id,std), ERR_INVALID_STANDARD_ID, "Unknown standard: " + id);
			return;
		}
		setResult(result, standards, 0, null);
	}

	
	protected void handleGetDevice(IRestRequest restRequest, IRestResponse result, String id) {
		DeviceStatusManager dvcMng = this.getDeviceStatusManager();
		if (id != null){
			DeviceStatus dev = dvcMng.getDevice(id);
			setResult(result, new RMap(id,dev), ERR_INVALID_DEVICE_ID, "Unknown device: " + id);
			return;
		}
		Map<String, DeviceStatus> list = dvcMng.listAllDevices();
		setResult(result, list, ERR_HAS_NO_ANY_DEVICE, "Has no any device");
	}

	protected void handleGetProfile(IRestRequest restRequest, IRestResponse result, String id) {
		DeviceProfileManager proMng = this.getDeviceProfileManager();
		if (id != null){
			DeviceProfile profile = proMng.getProfile(id);
			setResult(result, new RMap(id,profile), ERR_INVALID_PROFILE_ID, "Unknown profile: " + id);
			return;
		}
		Map<String, DeviceProfile> list = proMng.listAllProfiles();
		setResult(result, list, ERR_HAS_NO_ANY_PROFILE, "Has no any profile");
	}

	protected void handleGetLayout(IRestRequest restRequest, IRestResponse result, String id) {
		LayoutManager mng = this.getLayoutManager();
		if (id != null){
			List<LayoutData> layout = mng.getControllerLayout(id);
			setResult(result, new RMap(id,layout), ERR_INVALID_CONTROLLER_ID, "Unknown controller: " + id);
			return;
		}
		Map<String, List<LayoutData>> data = mng.getAllLayout();
		setResult(result, data, ERR_HAS_NO_ANY_LAYOUT, "Has no any controller assigned layout");
	}

	@Override
	public String getCommand() {
		return CMD;
	}

	@Override
	public String getUsage() {
		return getCommand() + ": Get data from driver proxy.\r\n" + "Example:    http://<ip:port>/" + getCommand()
				+ "/<layout|standard|profile|device|deviceStatus>[/id/<device/profile ID>]";
	}
	
	private void setResult(IRestResponse result, Object data, int errorCode, String errorMsg) {
		if (data != null){
			result.setData(data);
		}else{
			result.setErrorCode(errorCode);
			result.setResult(errorMsg);
		}
	}

	private class RMap extends HashMap<String, Object>{
		public RMap(String id, Object data){
			put(id, data);
		}
	}
}
