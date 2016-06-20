package org.skynet.bgby.devicedriver.honeywell;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.skynet.bgby.devicedriver.DeviceDriverBaseImpl;
import org.skynet.bgby.devicedriver.DeviceDriverException;
import org.skynet.bgby.devicedriver.honeywell.Helper.AirConditionArgs;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class Hgw2000 extends DeviceDriverBaseImpl {
	interface HgwCmdHandler {
		IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
				Map<String, String> params);
	}

	enum Profile {
		DIMMER("Honeywell HDW 2000 Dimmer"), FLOOR_HEATING("Honeywell HDW 2000 Floor Heating"), HVAC(
				"Honeywell HDW 2000 HVAC"), SIMPLE_LIGHT("Honeywell Switch Light");

		public static Profile byName(String name) {
			for (Profile p : values()) {
				if (p.name.equals(name)) {
					return p;
				}
			}
			return null;
		}

		String name;

		Profile(String pName) {
			name = pName;
		}
	}

	public static final String IDENTIFIER_ID = "id";
	public static final String IDENTIFIER_IPADDRESS = "ipAddress";

	private static final String TAG = Hgw2000.class.getName();

	protected Hgw2000DriverConfig config;

	protected Map<String, HGW2000Controller> drivers;

	@Override
	public boolean canDriverDevice(String deviceID, String profile, Map<String, Object> identity) {
		return Helper.SUPPORTED_PROFILES.contains(profile);
	}

	protected IRestResponse handleCmdSetFanMode(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		return handleCmdHvacSetAll(driver, status, params);
	}

	protected IRestResponse handleCmdSetRunningMode(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		return handleCmdHvacSetAll(driver, status, params);
	}

	public Hgw2000DriverConfig getConfig() {
		return config;
	}

	private HGW2000Controller getDriver(Map<String, Object> identify) throws DeviceDriverException {
		String ip = (String) identify.get(IDENTIFIER_IPADDRESS);
		if (ip == null) {
			throw new DeviceDriverException("Some HGW2000 device configuration has no ipAddress");
		}
		if (drivers == null) {
			synchronized (this) {
				if (drivers == null) {
					drivers = new ConcurrentHashMap<>();
				}
			}
		}
		HGW2000Controller driver = drivers.get(ip);
		if (driver == null) {
			driver = new HGW2000Controller();
			Configuration cfg = config.getConfigByIp(ip);
			driver.setConfiguration(cfg);
			drivers.put(ip, driver);
		}
		return driver;
	}

	protected IRestResponse handleCmdDimmerGetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdDimmerSetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdFloorHeatingGetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdFloorHeatingSetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdGetLight(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdHvacGetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdHvacSetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		AirConditionArgs args = Helper.getAirConditionArgs(config, status);
		IRestResponse errResponse = args.updateByRequiredParams(params);
		if (errResponse != null) {
			return errResponse;
		}

		try {
			ExecutionResult result = driver.controlAirCondition(args.id, args.onOrOff, args.mode, args.fan,
					args.windDirection, args.tempToSet);
			return newExecutionResult(result);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			return newErrorResult(NormalHVAC.ERR_CONNECT_TO_GATEWAY,
					"Cannot connect to Honeywell Gateway of " + driver.viewCurrentConfiguration().getHostIPAddress(),
					msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			return newErrorResult(NormalHVAC.ERR_IO_EXCEPTION, "Internal exception", msg);
		}
	}

	protected IRestResponse handleCmdLightGetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	protected IRestResponse handleCmdLightSetAll(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		String strOnOff = params.get(SimpleLight.TERM_LIGHT_STATUES);
		if (strOnOff == null){
			return newErrorResult(SimpleLight.ERR_MISS_LIGHT_STATUES, "Missing light state", "Must provide state parameter");
		}
		boolean bOnOff = DriverUtils.getAsBoolean(strOnOff, false);
		int lightId = DriverUtils.getAsInt(status.getIdentify().get(IDENTIFIER_ID), 0);
		assert(lightId > 0);
		int dimmer = 255;
		try {
			ExecutionResult result = driver.controlLight(lightId, 4, bOnOff?1:0, dimmer);
			return newExecutionResult(result);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			return newErrorResult(NormalHVAC.ERR_CONNECT_TO_GATEWAY,
					"Cannot connect to Honeywell Gateway of " + driver.viewCurrentConfiguration().getHostIPAddress(),
					msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			return newErrorResult(NormalHVAC.ERR_IO_EXCEPTION, "Internal exception", msg);
		}
	}

	protected IRestResponse handleCmdSetLight(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		return handleCmdLightSetAll(driver, status, params);
	}

	protected IRestResponse handleCmdSetTemperature(HGW2000Controller driver, DeviceStatus status,
			Map<String, String> params) {
		return handleCmdHvacSetAll(driver, status, params);
	}

	@Override
	public void initStatus(String profile, Map<String, Object> identity, Map<String, Object> status) {
		// TODO Auto-generated method stub

	}

	protected static IRestResponse newErrorResult(int errCode, String title, String detail) {
		RestResponseImpl response = new RestResponseImpl();
		response.setData(detail);
		response.setErrorCode(errCode);
		response.setResult(title);
		return response;
	}

	private IRestResponse newExecutionResult(ExecutionResult result) {
		RestResponseImpl response = new RestResponseImpl();
		response.setData(result);
		return response; // TODO will change later
	}

	@Override
	public IRestResponse onCommand(String command, DeviceStatus deviceStatus, Map<String, String> params)
			throws DeviceDriverException {
		assert(config != null);
		assert(deviceStatus != null);
		assert(deviceStatus.getIdentify() != null);
		HGW2000Controller driver = getDriver(deviceStatus.getIdentify());
		HgwCmdHandler handler = Helper.cmdHandlers.get(command);
		if (handler == null) {
			return newErrorResult(100, "Unsupport Command " + command, "<none>");
		}
		return handler.handleCmd(this, driver, deviceStatus, params);
	}

	@Override
	public void setConfig(Object cfgObject) {
		config = (Hgw2000DriverConfig) cfgObject;
	}
}