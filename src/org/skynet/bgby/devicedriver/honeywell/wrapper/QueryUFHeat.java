package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicestandard.NormalFloorHeating;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class QueryUFHeat extends ControlUFHeat {

	@Override
	protected ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException {
		return driver.queryUFHeat((int) apiArgs);
	}

	@Override
	protected IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs) {
		if (apiArgs == null) {
			return newWrongStatusResult();
		}
		return null;
	}

	@Override
	protected Object createArgsFromStatus(ExecutionContext ctx) {
		int id = DriverUtils.getAsInt(ctx.getConfig().getIdentity().get(Hgw2000.IDENTIFIER_ID), -1);
		assert(id != -1);
		return id;
	}

	@Override
	protected void updateStatus(ExecutionContext executionContext, IRestResponse response) {
		if (response.getErrorCode() != 0) {
			DriverUtils.log(Level.FINE, Hgw2000.TAG, "Response not success, do not update status");
			return;
		}
		updateStatus(executionContext.getDevice(), 
				(Map<String, Object>) response.getData(), 
				NormalFloorHeating.TERM_SET_TEMPERATURE, NormalFloorHeating.TERM_ROOM_TEMPERATURE, NormalFloorHeating.TERM_STATE);
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
		updateInt(responseData, data, NormalFloorHeating.TERM_ROOM_TEMPERATURE, FIELD_TEMP_CUR);
		updateState(responseData, data, executionContext);

		response.setData(responseData);
		return response;
	}
	
	

}
