package org.skynet.bgby.devicedriver.honeywell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.skynet.bgby.devicedriver.honeywell.Hgw2000.HgwCmdHandler;
import org.skynet.bgby.devicedriver.honeywell.Hgw2000.Profile;
import org.skynet.bgby.devicedriver.honeywell.wrapper.ControlAirCondition;
import org.skynet.bgby.devicedriver.honeywell.wrapper.ControlHbusLight;
import org.skynet.bgby.devicedriver.honeywell.wrapper.ControlLight;
import org.skynet.bgby.devicedriver.honeywell.wrapper.HGWDriverWrapper;
import org.skynet.bgby.devicedriver.honeywell.wrapper.QueryAirCondition;
import org.skynet.bgby.devicedriver.honeywell.wrapper.QueryHbusLight;
import org.skynet.bgby.devicedriver.honeywell.wrapper.QueryLight;
import org.skynet.bgby.devicestandard.DeviceStandardBaseImpl;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestResponse;

public class Helper {
	protected static class CmdProfileHandler {
		public CmdProfileHandler(String command2, HGWDriverWrapper wrapper2) {
			command = command2;
			wrapper = wrapper2;
		}
		String command;
		HGWDriverWrapper wrapper;
	}

	protected static final Map<String, HgwCmdHandler> cmdHandlers = new HashMap<>();
	public static final Set<String> SUPPORTED_PROFILES = new HashSet<>();
	public static final Map<String, Map<String, HGWDriverWrapper>> HANDLERS_BY_PRODILE = new HashMap<>();

	protected static void as_profile(Profile profile, CmdProfileHandler... handlers) {
		SUPPORTED_PROFILES.add(profile.name);
		if (handlers == null || handlers.length == 0){
			return;
		}
		Map<String, HGWDriverWrapper> proHandlers = HANDLERS_BY_PRODILE.get(profile.name);
		if (proHandlers == null){
			proHandlers = new HashMap<>();
			HANDLERS_BY_PRODILE.put(profile.name, proHandlers);
		}
		for(CmdProfileHandler handler: handlers){
			proHandlers.put(handler.command, handler.wrapper);
		}
	}

	static CmdProfileHandler handle(String command, HGWDriverWrapper wrapper) {
		return new CmdProfileHandler(command, wrapper);
	}

	static {
		as_profile(Profile.HBUS_LIGHT,
				handle(SimpleDimmer.CMD_SET_ALL, new ControlHbusLight()),
				handle(SimpleDimmer.CMD_GET_ALL, new QueryHbusLight()),
				handle(SimpleDimmer.CMD_SET_LIGHT, new ControlHbusLight()),
				handle(SimpleDimmer.CMD_GET_LIGHT, new QueryHbusLight()));
		as_profile(Profile.SIMPLE_LIGHT,
				handle(SimpleDimmer.CMD_SET_ALL, new ControlLight()),
				handle(SimpleDimmer.CMD_GET_ALL, new QueryLight()),
				handle(SimpleDimmer.CMD_SET_LIGHT, new ControlLight()),
				handle(SimpleDimmer.CMD_GET_LIGHT, new QueryLight()));
		as_profile(Profile.HVAC,
				handle(NormalHVAC.CMD_GET_FAM_MODE, new QueryAirCondition()),
				handle(NormalHVAC.CMD_GET_RUNNING_MODE, new QueryAirCondition()),
				handle(NormalHVAC.CMD_GET_TEMPERATURE_SETTING, new QueryAirCondition()),
				handle(NormalHVAC.CMD_GET_ROOM_TEMPERATURE, new QueryAirCondition()),
				handle(NormalHVAC.CMD_GET_ALL, new QueryAirCondition()),
				handle(NormalHVAC.CMD_SET_ALL, new ControlAirCondition()),
				handle(NormalHVAC.CMD_SET_FAN_MODE, new ControlAirCondition()),
				handle(NormalHVAC.CMD_SET_RUNNING_MODE, new ControlAirCondition()),
				handle(NormalHVAC.CMD_SET_TEMPERATURE, new ControlAirCondition()));
	}

	protected Helper() {
	}


	public static HGWDriverWrapper getCommandWrapper(String profile, String command) {
		Map<String, HGWDriverWrapper> handlers = HANDLERS_BY_PRODILE.get(profile);
		if (handlers == null){
			return null;
		}
		return handlers.get(command);
	}
}