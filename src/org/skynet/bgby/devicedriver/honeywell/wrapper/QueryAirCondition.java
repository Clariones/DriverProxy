package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000DriverConfig;
import org.skynet.bgby.devicedriver.honeywell.wrapper.ControlAirCondition.AirConditionArgs;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class QueryAirCondition extends ControlAirCondition  {

	protected static final String[] cmdFormat = {FIELD_TOKEN, FIELD_CMD,
			FIELD_DEVICE, FIELD_ID, FIELD_ON_OFF, FIELD_MODE, FIELD_FAN,
			FIELD_WING_DIRECTION, FIELD_TEMP_SET, FIELD_TEMP_CUR, FIELD_ERR};

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
				NormalHVAC.TERM_ROOM_TEMPERATURE,
				NormalHVAC.TERM_RUNNING_MODE);
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
		updateInt(responseData, data, NormalHVAC.TERM_ROOM_TEMPERATURE, FIELD_TEMP_CUR);
		updateFanMode(responseData, data);
		updateRunningMode(responseData, data);

		response.setData(responseData);
		return response;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		return driver.queryAirCondition((int) apiArgs);
	}

	@Override
	protected IRestResponse updateAndCheckParams(String command, Object apiArgs, Map<String, String> params) {
		if (apiArgs == null){
			return newWrongStatusResult();
		}
		return null;
	}

	@Override
	protected Object createArgsFromStatus(Hgw2000DriverConfig config, DeviceStatus status) {
		int id = DriverUtils.getAsInt(status.getIdentify().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(id != -1);
		return id;
	}

}
