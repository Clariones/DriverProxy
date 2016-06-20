package org.skynet.bgby.devicedriver.honeywell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skynet.bgby.devicedriver.honeywell.Hgw2000.HgwCmdHandler;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000.Profile;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;

public class Helper {

	protected static final Map<String, HgwCmdHandler> cmdHandlers = new HashMap<>();
	public static final Set<String> SUPPORTED_PROFILES = new HashSet<>();

	static {
		SUPPORTED_PROFILES.add(Profile.DIMMER.name);
		SUPPORTED_PROFILES.add(Profile.FLOOR_HEATING.name);
		SUPPORTED_PROFILES.add(Profile.HVAC.name);
		SUPPORTED_PROFILES.add(Profile.SIMPLE_LIGHT.name);

		cmdHandlers.put(NormalHVAC.CMD_SET_TEMPERATURE, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				return standardDriver.handleCmdSetTemperature(driver, status, params);
			}
		});
		cmdHandlers.put(NormalHVAC.CMD_SET_RUNNING_MODE, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				return standardDriver.handleCmdSetRunningMode(driver, status, params);
			}
		});
		cmdHandlers.put(NormalHVAC.CMD_SET_FAN_MODE, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				return standardDriver.handleCmdSetFanMode(driver, status, params);
			}
		});
		cmdHandlers.put(SimpleLight.CMD_SET_LIGHT, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				return standardDriver.handleCmdSetLight(driver, status, params);
			}
		});
		cmdHandlers.put(SimpleLight.CMD_GET_LIGHT, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				return standardDriver.handleCmdGetLight(driver, status, params);
			}
		});
		cmdHandlers.put(DeviceStandardBaseImpl.CMD_SET_ALL, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				Profile p = Profile.byName(status.getProfile());
				if (p == null) {
					return standardDriver.newErrorResult(SimpleLight.ERR_WRONG_PROFILE,
							"Required device profile " + status.getProfile() + " not declared", status.getProfile());
				}

				switch (p) {
				case SIMPLE_LIGHT:
					return standardDriver.handleCmdLightSetAll(driver, status, params);
				case HVAC:
					return standardDriver.handleCmdHvacSetAll(driver, status, params);
				case FLOOR_HEATING:
					return standardDriver.handleCmdFloorHeatingSetAll(driver, status, params);
				case DIMMER:
					return standardDriver.handleCmdDimmerSetAll(driver, status, params);
				default:
					return standardDriver.newErrorResult(SimpleLight.ERR_MISS_PROFILE,
							"Required device profile " + status.getProfile() + " not handled yet", status.getProfile());
				}
			}
		});
		cmdHandlers.put(DeviceStandardBaseImpl.CMD_GET_ALL, new HgwCmdHandler() {
			public IRestResponse handleCmd(Hgw2000 standardDriver, HGW2000Controller driver, DeviceStatus status,
					Map<String, String> params) {
				Profile p = Profile.byName(status.getProfile());
				if (p == null) {
					return standardDriver.newErrorResult(SimpleLight.ERR_WRONG_PROFILE,
							"Required device profile " + status.getProfile() + " not declared", status.getProfile());
				}

				switch (p) {
				case SIMPLE_LIGHT:
					return standardDriver.handleCmdLightGetAll(driver, status, params);
				case HVAC:
					return standardDriver.handleCmdHvacGetAll(driver, status, params);
				case FLOOR_HEATING:
					return standardDriver.handleCmdFloorHeatingGetAll(driver, status, params);
				case DIMMER:
					return standardDriver.handleCmdDimmerGetAll(driver, status, params);
				default:
					return standardDriver.newErrorResult(SimpleLight.ERR_MISS_PROFILE,
							"Required device profile " + status.getProfile() + " not handled yet", status.getProfile());
				}
			}
		});
	}

	private Helper() {
	}

	public static AirConditionArgs getAirConditionArgs(Hgw2000DriverConfig config, DeviceStatus status) {
		AirConditionArgs arg = new AirConditionArgs();
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
				if (newTemp <= 0){
					return Hgw2000.newErrorResult(NormalHVAC.ERR_SET_TEMP_OUT_OF_RANGE, "Invalid temperature setting",
							(String) statusValue);
				}else{
					tempToSet = newTemp;
				}
			}
			return null;
		}
	}

}