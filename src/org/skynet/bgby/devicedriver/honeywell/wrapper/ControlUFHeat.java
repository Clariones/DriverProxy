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
import org.skynet.bgby.devicestandard.NormalFloorHeating;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class ControlUFHeat extends AbstractWrapper {

	protected static final String[] cmdFormat = { FIELD_TOKEN, FIELD_CMD, FIELD_DEVICE, FIELD_ID, FIELD_ON_OFF,
			FIELD_TEMP_SET, FIELD_TEMP_CUR, FIELD_ERR };

	@SuppressWarnings("unchecked")
	@Override
	protected void updateStatus(ExecutionContext executionContext, IRestResponse response) {
		if (response.getErrorCode() != 0) {
			DriverUtils.log(Level.FINE, Hgw2000.TAG, "Response not success, do not update status");
			return;
		}
		updateStatus(executionContext.getDevice(), 
				(Map<String, Object>) response.getData(), 
				NormalFloorHeating.TERM_SET_TEMPERATURE, NormalFloorHeating.TERM_STATE);
	}

	protected void updateState(Map<String, Object> responseData, Map<String, String> data, ExecutionContext ctx) {
		String onOff = data.get(FIELD_ON_OFF);
		boolean bState = "1".equals(onOff);
		responseData.put(NormalFloorHeating.TERM_STATE, bState);
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

		updateInt(responseData, data, NormalFloorHeating.TERM_SET_TEMPERATURE, FIELD_TEMP_SET);
		updateState(responseData, data, executionContext);

		response.setData(responseData);
		return response;
	}

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		FloorHeatingArgs arg = (FloorHeatingArgs) apiArgs;
		return driver.controlUFHeat(arg.id, arg.onOrOff, arg.tempToSet);
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		if (!(apiArgs instanceof FloorHeatingArgs)) {
			return newWrongStatusResult();
		}
		FloorHeatingArgs arg = (FloorHeatingArgs) apiArgs;
		return arg.updateByRequiredParams(executionContext);
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext ctx) {
		FloorHeatingArgs arg = new FloorHeatingArgs();
		DeviceStatus status = ctx.getDevice();
		DeviceConfigData cfgData = ctx.getConfig();
		arg.id = DriverUtils.getAsInt(cfgData.getIdentity().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(arg.id != -1);
		if (status.getStatus() == null) {
			arg.onOrOff = 0;
			return arg;
		}
		Object statusValue = status.getStatus().get(NormalFloorHeating.TERM_STATE);
		if (statusValue != null) {
			boolean bOnOff = DriverUtils.getAsBoolean(statusValue, false);
			arg.onOrOff = bOnOff ? 1 : 0;
		}
		statusValue = status.getStatus().get(NormalFloorHeating.TERM_SET_TEMPERATURE);
		arg.tempToSet = DriverUtils.getAsInt(statusValue, 0);
		return arg;
	}

	static class FloorHeatingArgs {
		int id;
		int onOrOff = 1;
		int tempToSet;

		public IRestResponse updateByRequiredParams(ExecutionContext ctx) {
			Map<String, String> params = ctx.getCmdParams();
			Object statusValue = params.get(NormalFloorHeating.TERM_STATE);
			if (statusValue != null) {
				boolean bOnOff = DriverUtils.getAsBoolean(statusValue, false);
				onOrOff = bOnOff ? 1 : 0;
			}

			statusValue = params.get(NormalFloorHeating.TERM_SET_TEMPERATURE);
			if (statusValue instanceof String) {
				int newTemp = DriverUtils.getAsInt(statusValue, -1);
				if (newTemp <= 0) {
					return Hgw2000.newErrorResult(NormalFloorHeating.ERR_SET_TEMP_OUT_OF_RANGE, "Invalid temperature setting",
							(String) statusValue);
				} else {
					tempToSet = newTemp;
				}
			}
			return null;
		}
	}

}
