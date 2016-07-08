package org.skynet.bgby.devicedriver.honeywell;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.devicedriver.DeviceDriver;
import org.skynet.bgby.devicedriver.DeviceDriverBaseImpl;
import org.skynet.bgby.devicedriver.DeviceDriverException;
import org.skynet.bgby.devicedriver.honeywell.wrapper.HGWDriverWrapper;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;

/**
 * Lifecycle:<br/>
 * 1. create by Class.newInstance() at
 * {@link org.skynet.bgby.devicedriver.DriverManagerImpl.createDriver}<br/>
 * 2. set its configuration. (the class name was defined in configuration file)
 * <br/>
 * 3. call its {@link initStatus}() at
 * {@link org.skynet.bgby.devicedriver.DriverManagerImpl.initDriverStatus}<br/>
 * 4. call its {@link onStart}()<br/>
 * Now its ready to handle command.<br/>
 * Rest command enter point is
 * {@link org.skynet.bgby.driverproxy.DriverProxyService.handleDeviceCommand}
 * <br/>
 * When command arrived, will call its {@link canDriverDevice}() to check if it
 * can handle the command.<br/>
 * If it is the one, then call its {@link onCommand}()<br/>
 * 
 * @author Clariones
 *
 */
public class Hgw2000 extends DeviceDriverBaseImpl {

	interface HgwCmdHandler {
		IRestResponse handleCmd(DeviceDriver standardDriver, HGW2000Controller driver, DeviceStatus status,
				Map<String, String> params);
	}

	enum Profile {
		// standard: NormalFloorHeating
		FLOOR_HEATING("Honeywell.HGW2000.UFH"),
		// standard: TBD
		HBUS_CURTAIN("Honeywell.HGW2000.Curtain"),
		// standard: TBD
		HBUS_HBUS_CURTAIN("Honeywell.HGW2000.HBus_Curtain"),
		// standard: SimpleDimer, SimpleLight
		HBUS_LIGHT_M1("Honeywell.HGW2000.Maia_I_HBus_Light"), HBUS_LIGHT_M2("Honeywell.HGW2000.Maia_II_HBus_Light"),
		// standard: NormalHVAC
		HVAC("Honeywell.HGW2000.485HVAC"),
		// standard: SimpleLight
		LIGHT_M1("Honeywell.HGW2000.Maia_I_Light"), LIGHT_M2("Honeywell.HGW2000.Maia_II_Light");

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
	public static final String IDENTIFIER_LOOP = "loop";
	public static final String IDENTIFIER_IPADDRESS = "ipAddress";
	public static final String TAG = Hgw2000.class.getName();

	public static IRestResponse newErrorResult(int errCode, String title, Object detail) {
		RestResponseImpl response = new RestResponseImpl();
		response.setData(detail);
		response.setErrorCode(errCode);
		response.setResult(title);
		return response;
	}

	protected Hgw2000DriverConfig driverConfig;

	protected Map<String, HGW2000Controller> drivers;

	@Override
	public boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile,
			DeviceConfigData devCfg) {
		// so far, the profile ID is the only factor
		return Helper.SUPPORTED_PROFILES.contains(profile.getID());
	}

	public Hgw2000DriverConfig getConfig() {
		return driverConfig;
	}

	protected HGW2000Controller getDriver(DeviceConfigData config) throws DeviceDriverException {
		String idfyIP = (String) config.getIdentity().get(IDENTIFIER_IPADDRESS);
		if (idfyIP == null) {
			throw new DeviceDriverException("Some HGW2000 device configuration has no ipAddress");
		}
		if (drivers == null) {
			synchronized (this) {
				if (drivers == null) {
					drivers = new ConcurrentHashMap<>();
				}
			}
		}
		HGW2000Controller driver = drivers.get(idfyIP);
		if (driver == null) {
			driver = new HGW2000Controller();
			Configuration cfg = createControllerConfig(idfyIP, config);
			driver.setConfiguration(cfg);
			drivers.put(idfyIP, driver);
		}
		return driver;
	}

	protected Configuration createControllerConfig(String ipAddress, DeviceConfigData config) {
		String userName = (String) config.getExtParam("userName");
		String password = (String) config.getExtParam("password");
		int port = DriverUtils.getAsInt(config.getExtParam("port"), -2);
		if (userName == null) {
			userName = driverConfig.getDefaultUserName();
		}
		if (password == null) {
			password = driverConfig.getDefaultPassword();
		}
		if (port == -2) {
			port = driverConfig.getDefaultPort();
		}
		Configuration data = new Configuration();
		data.setHostIPAddress(ipAddress);
		data.setUsername(userName);
		data.setPassword(password);
		data.setPort(port);
		return data;
	}

	@Override
	public IRestResponse onCommand(ExecutionContext executionContext) throws DeviceDriverException {
		HGW2000Controller driver = getDriver(executionContext.getConfig());
		HGWDriverWrapper wrapper = Helper.getCommandWrapper(executionContext.getProfile().getID(),
				executionContext.getCommand());
		if (wrapper == null) {
			Map<String, Object> errDetail = new HashMap<>();
			errDetail.put("command", executionContext.getCommand());
			errDetail.put("device_profile", executionContext.getProfile().getID());
			errDetail.put("device_ID", executionContext.getDevice().getID());
			return newErrorResult(ERR_WRONG_CMD_FOR_DEVICE_TYPE,
					"This device cannot support Command " + executionContext.getCommand(), errDetail);
		}
		return wrapper.execute(driver, executionContext);
	}

	@Override
	public void setConfig(Object cfgObject) {
		driverConfig = (Hgw2000DriverConfig) cfgObject;
	}

	@Override
	public boolean isNeedPollingDevice(DeviceProfile profile) {
		Profile devProfile = Profile.byName(profile.getID());
		if (devProfile == null) {
			throw new RuntimeException("Unknown profile for HGW2000: " + profile.getID());
		}
		switch (devProfile) {
		case FLOOR_HEATING:
		case HVAC:
			return true;
		default:
			return false;
		}
	}

	@Override
	public DeviceStatus pollingDevice(DeviceConfigData device) {
		try {
			HGW2000Controller hgwCtrller = getDriver(device);
			Profile devProfile = Profile.byName(device.getProfile());
			String command = DeviceStandardBaseImpl.CMD_GET_ALL;
			HGWDriverWrapper wrapper = Helper.getCommandWrapper(device.getProfile(), command);
			if (wrapper == null) {
				throw new Exception("Why I cannot found wrapper for " + device.getProfile());
			}
			ExecutionContext executionContext = new ExecutionContext();
			executionContext.setCommand(command);
			executionContext.setConfig(device);
			DeviceStatus devieStatus = new DeviceStatus();
			devieStatus.setID(device.getID());
			devieStatus.setProfile(device.getProfile());
			executionContext.setDevice(devieStatus);
			executionContext.setProfile(getDeviceProfileManager().getProfile(device.getProfile()));
			IRestResponse response = wrapper.execute(hgwCtrller, executionContext);
			// System.out.println(response);
			if (response.getErrorCode() != 0) {
				DriverUtils.log(Level.WARNING, TAG, "Error when query " + device.getID() + ": ["
						+ response.getErrorCode() + "]" + response.getResult());
				return null;
			}
			return executionContext.getDevice();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public long getDevicePollingPeriod() {
		return driverConfig.getDevicePollingTime();
	}

}