package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class ControlHbusLight extends AbstractWrapper {
	static String[] cmdFormat = { FIELD_TOKEN, FIELD_CMD, FIELD_DEVICE, FIELD_AREA, FIELD_LOOP, FIELD_ACTION,
			FIELD_ON_OFF, FIELD_DIMMER, FIELD_ERR };

	static class HBusLightArgs {
		int loop = -1;
		int area = -1;
		int onOrOff = 0;
		int dimmer = 0;
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

		String val = data.get(FIELD_ON_OFF);
		if (val != null) {
			if (val.equals("1")) {
				responseData.put(SimpleDimmer.TERM_LIGHT_STATUES, SimpleDimmer.TERM_LIGHT_ON);
			} else {
				responseData.put(SimpleDimmer.TERM_LIGHT_STATUES, SimpleDimmer.TERM_LIGHT_OFF);
			}
		}
		val = data.get(FIELD_DIMMER);
		if (val != null) {
			responseData.put(SimpleDimmer.TERM_DIMMER_LEVEL, DriverUtils.getAsInt(val, 0));
		}
		response.setData(responseData);

		return response;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		HBusLightArgs arg = (HBusLightArgs) apiArgs;
		// always use action=4, single device control here.
		return driver.controlHBusLight(arg.area, arg.loop, 4, arg.onOrOff, arg.dimmer);
	}

	@Override
	protected IRestResponse updateAndCheckParams(String command, Object apiArgs, Map<String, String> params) {
		if (!(apiArgs instanceof HBusLightArgs)) {
			return newWrongStatusResult();
		}
		HBusLightArgs arg = (HBusLightArgs) apiArgs;
		String state = params.get(SimpleDimmer.TERM_LIGHT_STATUES);
		String level = params.get(SimpleDimmer.TERM_DIMMER_LEVEL);
		if (state == null && level == null) {
			return Hgw2000.newErrorResult(SimpleDimmer.ERR_MISS_LIGHT_STATUES,
					"setLight must provide " + SimpleDimmer.TERM_LIGHT_STATUES + " and/or "
							+ SimpleDimmer.TERM_DIMMER_LEVEL,
					String.format("Now %s=%s, %s=%s", SimpleDimmer.TERM_LIGHT_STATUES, state,
							SimpleDimmer.TERM_DIMMER_LEVEL, level));
		}
		if (level != null) {
			int iLevel = DriverUtils.getAsInt(level, -1);
			if (iLevel < 0 || iLevel > 100) {
				return Hgw2000.newErrorResult(SimpleDimmer.ERR_DIMMER_OUT_OF_RANGE, "Dimmer level out of range", level);
			}
			arg.dimmer = iLevel;
		}
		if (state != null) {
			arg.onOrOff = DriverUtils.getAsBoolean(state, false) ? 1 : 0;
			if (level == null) {
				arg.dimmer = arg.onOrOff == 1 ? 100 : 0;
			}
		} else {
			// if state not set, but level set, then means turn on and set to
			// level
			if (arg.dimmer > 0) {
				arg.onOrOff = 1;
			} else {
				arg.onOrOff = 0;
			}
		}
		return null;
	}

	@Override
	protected Object createArgsFromStatus(Hgw2000DriverConfig config, DeviceStatus status) {
		HBusLightArgs args = new HBusLightArgs();
		args.loop = DriverUtils.getAsInt(status.getIdentify().get(Hgw2000.IDENTIFIER_ID), -1);
		args.area = DriverUtils.getAsInt(status.getIdentify().get(Hgw2000.IDENTIFIER_AREA), -1);
		if (args.loop <= 0 || args.area <= 0) {
			throw new RuntimeException(status.getID() + " profile loop and area not correct:" + status.getIdentify());
		}
		if (status.getStatus() == null) {
			return args;
		}
		Object val = status.getStatus().get(SimpleLight.TERM_LIGHT_STATUES);
		if (val != null) {
			boolean isOn = DriverUtils.getAsBoolean(val, false);
			args.onOrOff = isOn ? 1 : 0;
		}
		val = status.getStatus().get(SimpleDimmer.TERM_DIMMER_LEVEL);
		if (val != null) {
			args.dimmer = DriverUtils.getAsInt(val, args.dimmer);
		}
		return args;
	}

	@Override
	protected void updateStatus(String command, DeviceStatus deviceStatus, Map<String, String> params,
			IRestResponse response) {
		if (response.getErrorCode() != 0) {
			DriverUtils.log(Level.FINE, Hgw2000.TAG, "Response not success, do not update status");
			return;
		}
		updateStatus(deviceStatus, (Map<String, Object>) response.getData(), SimpleDimmer.TERM_DIMMER_LEVEL,
				SimpleDimmer.TERM_LIGHT_STATUES);
	}

}
