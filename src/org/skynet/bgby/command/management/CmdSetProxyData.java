package org.skynet.bgby.command.management;

import java.util.List;
import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.error.ErrorCode;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestCommandHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Request "/getUILayout/<UI controller ID>" will get the corresponding
 * 
 * @author Clariones
 *
 */
public class CmdSetProxyData extends BaseManageCmd implements IRestCommandHandler {
	public static final String CMD = "setProxyData";
	public static final String DATE_TYPE_LAYOUT = "layout";
	public static final String DATE_TYPE_PROFILE = "profile";
	public static final String DATE_TYPE_DEVICE_CFG = "device";

	public static final int ERR_INVALID_DATA_TYPE = ErrorCode.MNGCMD_SET_DATA_START_CODE + 1;
	private static final int ERR_INVALID_JSON_SYNTAX = ErrorCode.MNGCMD_SET_DATA_START_CODE + 2;
	private static final int ERR_EMPTY_JSON_CONTENT = ErrorCode.MNGCMD_SET_DATA_START_CODE + 3;
	
	
	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		IRestResponse result = new RestResponseImpl();
		result.setRequest(DriverUtils.getRequestFullUri(restRequest));
		String target = restRequest.getTarget();
		switch (target) {
		case DATE_TYPE_LAYOUT:
			handleSetLayout(restRequest, result, getOverwriteParam(restRequest));
			break;
		case DATE_TYPE_PROFILE:
			handleSetProfile(restRequest, result, getOverwriteParam(restRequest));
			break;
		case DATE_TYPE_DEVICE_CFG:
			handleSetDeviceCfg(restRequest, result, getOverwriteParam(restRequest));
			break;
		default:
			result.setErrorCode(ERR_INVALID_DATA_TYPE);
			result.setResult("Cannot set data which is type of: " + target);
			result.setData(getUsage());
		}

		
		return result;
	}

	protected boolean getOverwriteParam(IRestRequest restRequest) {
		if (restRequest.getParams() == null || restRequest.getParams().isEmpty()){
			return false;
		}
		String val = restRequest.getParams().get("overwrite");
		if ("all".equals(val)){
			return true;
		}else{
			return false;
		}
	}

	
	protected void handleSetDeviceCfg(IRestRequest restRequest, IRestResponse result, boolean overWriteAll) {
		Map<String, DeviceConfigData> data = null;
		try{
			data = loadJsonFromRequest(restRequest, new TypeToken<Map<String, DeviceConfigData>>(){});
		}catch (Exception e){
			result.setErrorCode(ERR_INVALID_JSON_SYNTAX);
			result.setResult("Posted DeviceCfg JSON has syntax error");
			result.setData(DriverUtils.dumpExceptionToString(e));
			return;
		}
		if (data == null || data.isEmpty()){
			result.setErrorCode(ERR_EMPTY_JSON_CONTENT);
			result.setResult("Posted DeviceCfg content has no any valid data");
			return;
		}
		UpdateResult opResult = getDeviceConfigManager().update(data, overWriteAll);
		result.setData(opResult);
		result.setErrorCode(opResult.getErrorCode());
		result.setResult(opResult.getErrorTitle());
	}

	private Map loadJsonFromRequest(IRestRequest restRequest,
			TypeToken typeToken) {
		Gson gson = new Gson();
		String datas = restRequest.getParams().get("post_body");
		if (datas == null || datas.isEmpty()){
			return null;
		}
		
		return gson.fromJson(datas, typeToken.getType());
	}

	protected void handleSetProfile(IRestRequest restRequest, IRestResponse result, boolean overWriteAll) {
		Map<String, DeviceProfile> data = null;
		try{
			data = loadJsonFromRequest(restRequest, new TypeToken<Map<String, DeviceProfile>>(){});
		}catch (Exception e){
			result.setErrorCode(ERR_INVALID_JSON_SYNTAX);
			result.setResult("Posted DeviceProfile JSON has syntax error");
			result.setData(DriverUtils.dumpExceptionToString(e));
			return;
		}
		if (data == null || data.isEmpty()){
			result.setErrorCode(ERR_EMPTY_JSON_CONTENT);
			result.setResult("Posted DeviceProfile content has no any valid data");
			return;
		}
		UpdateResult opResult = getDeviceProfileManager().update(data, overWriteAll);
		result.setData(opResult);
		result.setErrorCode(opResult.getErrorCode());
		result.setResult(opResult.getErrorTitle());
	}

	protected void handleSetLayout(IRestRequest restRequest, IRestResponse result, boolean overWriteAll) {
		Map<String, List<LayoutData>> data = null;
		try{
			data = loadJsonFromRequest(restRequest, new TypeToken<Map<String, List<LayoutData>>>(){});
		}catch (Exception e){
			result.setErrorCode(ERR_INVALID_JSON_SYNTAX);
			result.setResult("Post JSON has syntax error");
			result.setData(DriverUtils.dumpExceptionToString(e));
			return;
		}
		if (data == null || data.isEmpty()){
			result.setErrorCode(ERR_EMPTY_JSON_CONTENT);
			result.setResult("Post content has no any valid data");
			return;
		}
		UpdateResult opResult = getLayoutManager().update(data, overWriteAll);
		result.setData(opResult);
		result.setErrorCode(opResult.getErrorCode());
		result.setResult(opResult.getErrorTitle());
	}

	@Override
	public String getCommand() {
		return CMD;
	}

	@Override
	public String getUsage() {
		return getCommand() + ": Set data to driver proxy.\r\n" + "Example:    http://<ip:port>/" + getCommand()
				+ "/<layout|profile|device>[/overwrite/<all|existed>]";
	}
	
	private void setResult(IRestResponse result, Object data, int errorCode, String errorMsg) {
		if (data != null){
			result.setData(data);
		}else{
			result.setErrorCode(errorCode);
			result.setResult(errorMsg);
		}
	}


}
