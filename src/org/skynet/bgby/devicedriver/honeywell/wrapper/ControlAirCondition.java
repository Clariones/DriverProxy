package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class ControlAirCondition extends AbstractWrapper {

	protected Hgw2000DriverConfig config;

	protected static final String[] cmdFormat = { FIELD_TOKEN, FIELD_CMD, FIELD_DEVICE, FIELD_ID, FIELD_ON_OFF,
			FIELD_MODE, FIELD_FAN, FIELD_WING_DIRECTION, FIELD_TEMP_SET, FIELD_TEMP_CUR, FIELD_ERR };

	@SuppressWarnings("unchecked")
	@Override
	protected void updateStatus(ExecutionContext executionContext, IRestResponse response) {
		if (response.getErrorCode() != 0) {
			DriverUtils.log(Level.FINE, Hgw2000.TAG, "Response not success, do not update status");
			return;
		}
		updateStatus(executionContext.getDevice(), 
				(Map<String, Object>) response.getData(), NormalHVAC.TERM_FAN_MODE,
				NormalHVAC.TERM_SET_TEMPERATURE, NormalHVAC.TERM_ROOM_TEMPERATURE, NormalHVAC.TERM_RUNNING_MODE);
	}

	protected void updateRunningMode(Map<String, Object> responseData, Map<String, String> data, ExecutionContext ctx) {
		String onOff = data.get(FIELD_ON_OFF);
		String mode = data.get(FIELD_MODE);
		int modeNum = Integer.valueOf(onOff) * 1000 + Integer.valueOf(mode);
		String modeName = getKeyByIntValue((Map<String, Object>) ctx.getProfile().getExtParam("runningModes"), modeNum);
		if (modeName != null) {
			responseData.put(NormalHVAC.TERM_RUNNING_MODE, modeName);
		}
	}

	protected void updateFanMode(Map<String, Object> responseData, Map<String, String> data, ExecutionContext ctx) {
		String fanMode = data.get(FIELD_FAN);
		String fanName = getKeyByIntValue((Map<String, Object>) ctx.getProfile().getExtParam("fanModes"),
				Integer.parseInt(fanMode));
		if (fanName != null) {
			responseData.put(NormalHVAC.TERM_FAN_MODE, fanName);
		}
	}

	protected void updateInt(Map<String, Object> responseData, Map<String, String> data, String term, String field) {
		String val = data.get(field);
		if (val == null) {
			return;
		}
		responseData.put(term, DriverUtils.getAsInt(val, 16));
	}

	@Override
	protected IRestResponse convertResultToResponse(ExecutionContext executionContext, ExecutionResult result) {
		Map<String, String> data = super.parseResult(cmdFormat, result.getReceivedResponse());
		if (data == null) {
			return newWrongResponseResult(result);
		}
		RestResponseImpl response = new RestResponseImpl();
		Map<String, Object> responseData = new HashMap<>();
		response.setErrorCode(toApiErrorCode(data.get(FIELD_ERR)));
		response.setResult(codeToMessage(response.getErrorCode()));

		updateInt(responseData, data, NormalHVAC.TERM_SET_TEMPERATURE, FIELD_TEMP_SET);
		updateInt(responseData, data, NormalHVAC.TERM_ROOM_TEMPERATURE, FIELD_TEMP_CUR);
		updateFanMode(responseData, data, executionContext);
		updateRunningMode(responseData, data, executionContext);

		response.setData(responseData);
		return response;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		AirConditionArgs arg = (AirConditionArgs) apiArgs;
		return driver.controlAirCondition(arg.id, arg.onOrOff, arg.mode, arg.fan, arg.windDirection, arg.tempToSet);
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		if (!(apiArgs instanceof AirConditionArgs)) {
			return newWrongStatusResult();
		}
		AirConditionArgs arg = (AirConditionArgs) apiArgs;
		return arg.updateByRequiredParams(executionContext);
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext ctx) {
		AirConditionArgs arg = new AirConditionArgs();
		DeviceStatus status = ctx.getDevice();
		DeviceConfigData cfgData = ctx.getConfig();
		arg.id = DriverUtils.getAsInt(cfgData.getIdentity().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(arg.id != -1);
		if (status.getStatus() == null) {
			arg.onOrOff = 0;
			return arg;
		}
		Object statusValue = status.getStatus().get(NormalHVAC.TERM_RUNNING_MODE);
		if (statusValue instanceof String) {
			Object proRunMode = ((Map<String, Object>) (ctx.getProfile().getExtParam("runningModes"))).get(statusValue);
			if (proRunMode != null) {
				Integer modeNum = DriverUtils.getAsInt(proRunMode, 0);
				arg.onOrOff = modeNum < 1000 ? 0 : 1;
				arg.mode = modeNum % 1000;
			} else {
				throw new RuntimeException("HGW 2000 running modes misconfigured: " + statusValue);
			}
		}
		statusValue = status.getStatus().get(NormalHVAC.TERM_FAN_MODE);
		if (statusValue instanceof String) {
			Object proFanMode = ((Map<String, Object>) (ctx.getProfile().getExtParam("fanModes"))).get(statusValue);
			if (proFanMode != null) {
				Integer modeNum = DriverUtils.getAsInt(proFanMode, 0);
				arg.fan = modeNum;
			} else {
				throw new RuntimeException("HGW 2000 fan modes misconfigured: " + statusValue);
			}
		}
		statusValue = status.getStatus().get(NormalHVAC.TERM_SET_TEMPERATURE);
		arg.tempToSet = DriverUtils.getAsInt(statusValue, 0);
		return arg;
	}

	static class AirConditionArgs {
		int fan = 0;
		int id;
		int mode = 0;
		int onOrOff = 1;
		int tempToSet;
		int windDirection = 0;

		public IRestResponse updateByRequiredParams(ExecutionContext ctx) {
			Map<String, String> params = ctx.getCmdParams();
			Object statusValue = params.get(NormalHVAC.TERM_FAN_MODE);
			if (statusValue instanceof String) {
				Object profileFanModeNum = ((Map<String, Object>) ctx.getProfile().getExtParam("fanModes"))
						.get(statusValue);
				if (profileFanModeNum != null) {
					Integer modeNum = DriverUtils.getAsInt(profileFanModeNum, 0);
					fan = modeNum;
				} else {
					return Hgw2000.newErrorResult(NormalHVAC.ERR_INVALID_FAN_MODE, "Invalid fan mode",
							(String) statusValue);
				}
			}

			statusValue = params.get(NormalHVAC.TERM_RUNNING_MODE);
			if (statusValue instanceof String) {
				Object profileRunningModeNum = ((Map<String, Object>) ctx.getProfile().getExtParam("runningModes"))
						.get(statusValue);
				if (profileRunningModeNum != null) {
					Integer modeNum = DriverUtils.getAsInt(profileRunningModeNum, 0);
					onOrOff = modeNum < 1000 ? 0 : 1;
					mode = modeNum % 1000;
				} else {
					return Hgw2000.newErrorResult(NormalHVAC.ERR_INVALID_RUNNING_MODE, "Invalid running mode",
							(String) statusValue);
				}
			}

			statusValue = params.get(NormalHVAC.TERM_SET_TEMPERATURE);
			if (statusValue instanceof String) {
				int newTemp = DriverUtils.getAsInt(statusValue, -1);
				if (newTemp <= 0) {
					return Hgw2000.newErrorResult(NormalHVAC.ERR_SET_TEMP_OUT_OF_RANGE, "Invalid temperature setting",
							(String) statusValue);
				} else {
					tempToSet = newTemp;
				}
			}
			return null;
		}
	}

}
