package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class ControlAirCondition extends AbstractWrapper {

	protected Hgw2000DriverConfig config;

	protected static final String[] cmdFormat = { FIELD_TOKEN, FIELD_CMD, FIELD_DEVICE, FIELD_ID, FIELD_ON_OFF,
			FIELD_MODE, FIELD_FAN, FIELD_WING_DIRECTION, FIELD_TEMP_SET, FIELD_ERR };

	@Override
	protected void updateStatus(String command, DeviceStatus deviceStatus, Map<String, String> params,
			IRestResponse response) {
		if (response.getErrorCode() != 0) {
			DriverUtils.log(Level.FINE, Hgw2000.TAG, "Response not success, do not update status");
			return;
		}
		updateStatus(deviceStatus, (Map<String, Object>) response.getData(), 
				NormalHVAC.TERM_FAN_MODE,
				NormalHVAC.TERM_SET_TEMPERATURE, 
				NormalHVAC.TERM_RUNNING_MODE);
	}

	protected void updateRunningMode(Map<String, Object> responseData, Map<String, String> data) {
		String onOff = data.get(FIELD_ON_OFF);
		String mode = data.get(FIELD_MODE);
		int modeNum = Integer.valueOf(onOff) * 1000 + Integer.valueOf(mode);
		String modeName = getKeyByIntValue(config.getRunningModes(), modeNum);
		if (modeName != null) {
			responseData.put(NormalHVAC.TERM_RUNNING_MODE, modeName);
		}
	}

	protected void updateFanMode(Map<String, Object> responseData, Map<String, String> data) {
		String fanMode = data.get(FIELD_FAN);
		String fanName = getKeyByIntValue(config.getFanModes(), Integer.parseInt(fanMode));
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
	protected IRestResponse convertResultToResponse(String command, DeviceStatus status, Map<String, String> params,
			ExecutionResult result) {

		Map<String, String> data = super.parseResult(cmdFormat, result.getReceivedResponse());
		if (data == null) {
			return newWrongResponseResult(result);
		}
		RestResponseImpl response = new RestResponseImpl();
		Map<String, Object> responseData = new HashMap<>();
		response.setErrorCode(toApiErrorCode(data.get(FIELD_ERR)));

		updateInt(responseData, data, NormalHVAC.TERM_SET_TEMPERATURE, FIELD_TEMP_SET);
		updateFanMode(responseData, data);
		updateRunningMode(responseData, data);

		response.setData(responseData);
		return response;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		AirConditionArgs arg = (AirConditionArgs) apiArgs;
		return driver.controlAirCondition(arg.id, arg.onOrOff, arg.mode, arg.fan, arg.windDirection, arg.tempToSet);
	}

	@Override
	protected IRestResponse updateAndCheckParams(String command, Object apiArgs, Map<String, String> params) {
		if (!(apiArgs instanceof AirConditionArgs)) {
			return newWrongStatusResult();
		}
		AirConditionArgs arg = (AirConditionArgs) apiArgs;
		return arg.updateByRequiredParams(params);
	}

	@Override
	protected Object createArgsFromStatus(Hgw2000DriverConfig config, DeviceStatus status) {
		AirConditionArgs arg = new AirConditionArgs();
		this.config = config;
		arg.config = config;
		arg.id = DriverUtils.getAsInt(status.getIdentify().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(arg.id != -1);
		if (status.getStatus() == null) {
			arg.onOrOff = 0;
			return arg;
		}
		Object statusValue = status.getStatus().get(NormalHVAC.TERM_RUNNING_MODE);
		if (statusValue instanceof String) {
			Integer modeNum = config.getRunningModes().get(statusValue);
			if (modeNum != null) {
				arg.onOrOff = modeNum < 1000 ? 0 : 1;
				arg.mode = modeNum % 1000;
			} else {
				throw new RuntimeException("HGW 2000 running modes misconfigured: " + statusValue);
			}
		}
		statusValue = status.getStatus().get(NormalHVAC.TERM_FAN_MODE);
		if (statusValue instanceof String) {
			Integer modeNum = config.getFanModes().get(statusValue);
			if (modeNum != null) {
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
		Hgw2000DriverConfig config;
		int fan = 0;
		int id;
		int mode = 0;
		int onOrOff = 1;
		int tempToSet;
		int windDirection = 0;

		public IRestResponse updateByRequiredParams(Map<String, String> params) {
			Object statusValue = params.get(NormalHVAC.TERM_FAN_MODE);
			if (statusValue instanceof String) {
				Integer modeNum = config.getFanModes().get(statusValue);
				if (modeNum != null) {
					fan = modeNum;
				} else {
					return Hgw2000.newErrorResult(NormalHVAC.ERR_INVALID_FAN_MODE, "Invalid fan mode",
							(String) statusValue);
				}
			}

			statusValue = params.get(NormalHVAC.TERM_RUNNING_MODE);
			if (statusValue instanceof String) {
				Integer modeNum = config.getRunningModes().get(statusValue);
				if (modeNum != null) {
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
