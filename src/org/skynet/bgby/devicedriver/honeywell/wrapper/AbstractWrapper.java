package org.skynet.bgby.devicedriver.honeywell.wrapper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.skynet.bgby.devicedriver.honeywell.ExecutionResult;
import org.skynet.bgby.devicedriver.honeywell.HGW2000Controller;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;

public abstract class AbstractWrapper implements HGWDriverWrapper {

	protected static final String FIELD_TOKEN = "token";
	protected static final String FIELD_CMD = "cmd";
	protected static final String FIELD_DEVICE = "device";
	protected static final String FIELD_AREA = "area";
	protected static final String FIELD_LOOP = "loop";
	protected static final String FIELD_ACTION = "action";
	protected static final String FIELD_ON_OFF = "on/off";
	protected static final String FIELD_DIMMER = "dimmer";
	protected static final String FIELD_ERR = "err";
	protected static final String FIELD_ID = "id";
	protected static final String FIELD_MODE = "mode";
	protected static final String FIELD_FAN = "fan";
	protected static final String FIELD_TEMP_SET = "temp_set";
	protected static final String FIELD_TEMP_CUR = "temp_cur";
	protected static final String FIELD_WING_DIRECTION = "dir";

	private static final Map<String, Integer> errorCodes = new HashMap<>();

	static {
		errorCodes.put("0", 0);
		errorCodes.put("1", Hgw2000.ERR_DEVICE_ACCESS_FAIL);
		errorCodes.put("2", Hgw2000.ERR_DEVICE_STATUES_UNKNOWN);
		errorCodes.put("103", Hgw2000.ERR_NEED_AUTHENTICATION);
		errorCodes.put("104", Hgw2000.ERR_USER_AUTHENTICATION_FAIL);
		errorCodes.put("105", Hgw2000.ERR_WRONG_DATA_FORMAT);
		errorCodes.put("128", Hgw2000.ERR_SEND_COMMAND_FAIL);
		errorCodes.put("129", Hgw2000.ERR_WRONG_DEVICE_RETURN_VALUE);
		errorCodes.put("130", Hgw2000.ERR_CMD_TIME_OUT);
		errorCodes.put("131", Hgw2000.ERR_CMD_PARSING);
		errorCodes.put("132", Hgw2000.ERR_DEVICE_FAILURE);
		errorCodes.put("133", Hgw2000.ERR_BUS_FAILURE);
		errorCodes.put("134", Hgw2000.ERR_DEVICE_OFFLINE);
	}

	@Override
	public IRestResponse execute(HGW2000Controller driver, ExecutionContext executionContext) {
		DriverUtils.log(Level.FINE, Hgw2000.TAG, "execute command {0}{1} by {2}", new Object[] {
				executionContext.getCommand(), executionContext.getCmdParams(), getClass().getSimpleName() });
		Object apiArgs = createArgsFromStatus(executionContext);
		IRestResponse checkParamsResult = updateAndCheckParams(executionContext, apiArgs);
		if (checkParamsResult != null) {
			return checkParamsResult;
		}
		try {
			ExecutionResult result = invokeDriver(driver, apiArgs);
			IRestResponse response = convertResultToResponse(executionContext, result);
			updateStatus(executionContext, response);
			return response;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, Hgw2000.TAG, msg);
			return Hgw2000.newErrorResult(NormalHVAC.ERR_CONNECT_TO_GATEWAY,
					"Cannot connect to Honeywell Gateway of " + driver.viewCurrentConfiguration().getHostIPAddress(),
					msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, Hgw2000.TAG, msg);
			return Hgw2000.newErrorResult(NormalHVAC.ERR_IO_EXCEPTION, "Internal exception", msg);
		}
	}


	protected abstract ExecutionResult invokeDriver(HGW2000Controller driver, Object apiArgs) throws IOException;

	protected abstract void updateStatus(ExecutionContext executionContext, IRestResponse response);

	protected abstract IRestResponse convertResultToResponse(ExecutionContext executionContext, ExecutionResult result);

	protected abstract IRestResponse updateAndCheckParams(ExecutionContext executionContext, Object apiArgs);

	protected abstract Object createArgsFromStatus(ExecutionContext executionContext);

	protected static Pattern PTN_SPLITER_1 = Pattern.compile("\\$((cfg)|(ack)|(req)|(res))\\s*,");

	public Map<String, String> parseResult(String[] cmdFormat, String string) {
		if (string == null || cmdFormat == null || cmdFormat.length < 2) {
			return null;
		}
		Map<String, String> result = new HashMap<>();
		Matcher m = PTN_SPLITER_1.matcher(string);
		if (!m.find()) {
			return null;
		}
		String foundedCmd = m.group();
		String cmdName = m.group(1);
		int pos = string.indexOf(foundedCmd);
		int sIdx = 1;
		if (pos == 0) {
			result.put(cmdFormat[0], cmdName);
		} else {
			result.put(cmdFormat[0], string.substring(0, pos));
			result.put(cmdFormat[1], cmdName);
			sIdx = 2;
		}
		String[] left = string.trim().substring(pos + foundedCmd.length()).split(",");
		if (sIdx + left.length != cmdFormat.length) {
			return null;
		}
		for (int i = 0; i < left.length; i++) {
			result.put(cmdFormat[i + sIdx], left[i]);
		}

		return result;
	}

	protected IRestResponse newWrongStatusResult() {
		return Hgw2000.newErrorResult(DeviceStandardBaseImpl.ERR_WRONG_STATUS,
				"Cannot parse existed Hbus light device status", "Please delete the existed status and try again");
	}

	protected IRestResponse newWrongResponseResult(ExecutionResult result) {
		return Hgw2000.newErrorResult(Hgw2000.ERR_UNRECOGNIZED_RESPONSE, "Cannot recognize response",
				result.getReceivedResponse());
	}

	protected String getKeyByIntValue(Map<String, Object> map, int number) {
		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> ent = it.next();
			int val = DriverUtils.getAsInt(ent.getValue(), -2);
			if (val == number) {
				return ent.getKey();
			}
		}
		return null;
	}

	protected int toApiErrorCode(String strErr) {
		Integer code = errorCodes.get(strErr);
		if (code == null) {
			return Hgw2000.ERR_WRONG_RESPONSE_ERROR;
		}
		return code;
	}

	protected void updateStatus(DeviceStatus deviceStatus, Map<String, Object> resultData, String... terms) {
		if (terms == null || terms.length == 0) {
			return;
		}
		for (String term : terms) {
			Object value = resultData.get(term);
			if (value == null) {
				continue;
			}
			deviceStatus.setStatus(term, value);
		}
	}
}
