package org.skynet.bgby.devicedriver.honeywell;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.skynet.bgby.devicedriver.DeviceDriverBaseImpl;
import org.skynet.bgby.devicedriver.DeviceDriverException;
import org.skynet.bgby.devicedriver.honeywell.wrapper.HGWDriverWrapper;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

public class Hgw2000 extends DeviceDriverBaseImpl {

	interface HgwCmdHandler {
		IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
				Map<String, String> params);
	}

	enum Profile {
		DIMMER("Honeywell HDW 2000 Dimmer"), FLOOR_HEATING("Honeywell HDW 2000 Floor Heating"), HBUS_CURTAIN(
				"Honeywell Curtain"), HBUS_HBUS_CURTAIN("Honeywell HBus Curtain"), HBUS_LIGHT("Honeywell HBus Light"), HVAC(
								"Honeywell HDW 2000 HVAC"), SIMPLE_LIGHT(
								"Honeywell Switch Light");

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
	public static final int ERR_HGW2000_START_CODE = 20000;
	public static final int ERR_BUS_FAILURE = ERR_HGW2000_START_CODE + 1;
	public static final int ERR_CMD_PARSING = ERR_HGW2000_START_CODE + 2;
	public static final int ERR_CMD_TIME_OUT = ERR_HGW2000_START_CODE + 3;
	public static final int ERR_DEVICE_ACCESS_FAIL = ERR_HGW2000_START_CODE + 4;
	public static final int ERR_DEVICE_FAILURE = ERR_HGW2000_START_CODE + 5;
	public static final int ERR_DEVICE_OFFLINE = ERR_HGW2000_START_CODE + 6;
	public static final int ERR_DEVICE_STATUES_UNKNOWN = ERR_HGW2000_START_CODE + 7;
	public static final int ERR_NEED_AUTHENTICATION = ERR_HGW2000_START_CODE + 8;
	public static final int ERR_SEND_COMMAND_FAIL = ERR_HGW2000_START_CODE + 9;
	public static final int ERR_UNRECOGNIZED_RESPONSE = ERR_HGW2000_START_CODE + 10;
	public static final int ERR_USER_AUTHENTICATION_FAIL = ERR_HGW2000_START_CODE + 11;
	public static final int ERR_WRONG_CMD_FOR_DEVICE_TYPE = ERR_HGW2000_START_CODE + 12;
	public static final int ERR_WRONG_DATA_FORMAT = ERR_HGW2000_START_CODE + 13;
	public static final int ERR_WRONG_DEVICE_RETURN_VALUE = ERR_HGW2000_START_CODE + 14;
	public static final int ERR_WRONG_RESPONSE_ERROR = ERR_HGW2000_START_CODE + 15;
	
	public static final String IDENTIFIER_AREA = "area";
	public static final String IDENTIFIER_ID = "id";
	public static final String IDENTIFIER_IPADDRESS = "ipAddress";
	public static final String TAG = Hgw2000.class.getName();

	public static IRestResponse newErrorResult(int errCode, String title, Object detail) {
		RestResponseImpl response = new RestResponseImpl();
		response.setData(detail);
		response.setErrorCode(errCode);
		response.setResult(title);
		return response;
	}

	protected Hgw2000DriverConfig config;

	protected Map<String, HGW2000Controller> drivers;

	@Override
	public boolean canDriverDevice(String deviceID, String profile, Map<String, Object> identity) {
		return Helper.SUPPORTED_PROFILES.contains(profile);
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

	@Override
	public void initStatus(String profile, Map<String, Object> identity, Map<String, Object> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public IRestResponse onCommand(String command, DeviceStatus deviceStatus, Map<String, String> params)
			throws DeviceDriverException {
		assert(config != null);
		assert(deviceStatus != null);
		assert(deviceStatus.getIdentify() != null);
		HGW2000Controller driver = getDriver(deviceStatus.getIdentify());
		HGWDriverWrapper wrapper = Helper.getCommandWrapper(deviceStatus.getProfile(), command);
		if (wrapper == null) {
			Map<String, Object> errDetail = new HashMap<>();
			errDetail.put("command", command);
			errDetail.put("device_profile", deviceStatus.getProfile());
			errDetail.put("device_ID", deviceStatus.getID());
			return newErrorResult(ERR_WRONG_CMD_FOR_DEVICE_TYPE, "This device cannot support Command " + command,
					errDetail);
		}
		return wrapper.execute(driver, config, command, deviceStatus, params);
	}

	@Override
	public void setConfig(Object cfgObject) {
		config = (Hgw2000DriverConfig) cfgObject;
	}

}