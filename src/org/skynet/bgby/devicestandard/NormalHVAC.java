package org.skynet.bgby.devicestandard;

public class NormalHVAC extends DeviceStandardBaseImpl {

	public static final String CMD_GET_FAM_MODE = "getFanMode";
	public static final String CMD_GET_ROOM_HUMIDITY = "getRoomHumidity";
	public static final String CMD_GET_ROOM_TEMPERATURE = "getRoomTemperature";
	public static final String CMD_GET_RUNNING_MODE = "getRunningMode";
	public static final String CMD_GET_TEMPERATURE_SETTING = "getTemperatureSetting";
	public static final String CMD_SET_FAN_MODE = "setFanMode";
	public static final String CMD_SET_RUNNING_MODE = "setRunningMode";
	public static final String CMD_SET_TEMPERATURE = "setTemperature";

	public static final String ID = "HVAC.Normal";

	public static final String TERM_DEVICE_REPORT = "deviceReport";
	public static final String TERM_FAN_MODE = "fanMode";
	public static final String TERM_FAN_MODES = "validFanModes";
	public static final String TERM_HAS_HUMIDITY = "humidityFunction";
	public static final String TERM_ROOM_HUMIDITY = "roomHumidity";
	public static final String TERM_ROOM_TEMPERATURE = "roomTemperature";
	public static final String TERM_ROOM_TERPERATURE_RANGE = "roomTemperatureRange";
	public static final String TERM_RUNNING_MODE = "runningMode";
	public static final String TERM_RUNNING_MODES = "validRunningModes";
	public static final String TERM_SET_TEMPERATURE = "temperatureSetting";
	public static final String TERM_SET_TEMPERATURE_RANGE = "temperatureSettingRange";
	public static final String TERM_WING_DIRECTION = "wingDirection";
	public static final String TERM_WING_DIRECTIONS = "validwingDirections";
	public static final String TERM_HAS_WING_DIRECTION = "hasWingDirection";

	public static final int ERR_HVAC_START_CODE = 76000;
	public static final int ERR_CONNECT_TO_GATEWAY = ERR_HVAC_START_CODE + 1;
	public static final int ERR_SET_TEMP_OUT_OF_RANGE = ERR_HVAC_START_CODE + 2;
	public static final int ERR_INVALID_FAN_MODE = ERR_HVAC_START_CODE + 3;
	public static final int ERR_INVALID_RUNNING_MODE = ERR_HVAC_START_CODE + 4;

	public NormalHVAC() {
		id = ID;
		TERM(TERM_DEVICE_REPORT);
		TERM(TERM_DISPLAY_NAME);
		TERM(TERM_FAN_MODE);
		TERM(TERM_FAN_MODES);
		TERM(TERM_HAS_HUMIDITY);
		TERM(TERM_ROOM_HUMIDITY);
		TERM(TERM_ROOM_TEMPERATURE);
		TERM(TERM_ROOM_TERPERATURE_RANGE);
		TERM(TERM_RUNNING_MODE);
		TERM(TERM_RUNNING_MODES);
		TERM(TERM_SET_TEMPERATURE);
		TERM(TERM_SET_TEMPERATURE_RANGE);
		TERM(TERM_WING_DIRECTION);
		TERM(TERM_WING_DIRECTIONS);
		TERM(TERM_HAS_WING_DIRECTION);

		CMD(CMD_GET_FAM_MODE);
		CMD(CMD_GET_ROOM_HUMIDITY);
		CMD(CMD_GET_ROOM_TEMPERATURE);
		CMD(CMD_GET_RUNNING_MODE);
		CMD(CMD_GET_TEMPERATURE_SETTING);
		CMD(CMD_SET_FAN_MODE);
		CMD(CMD_SET_RUNNING_MODE);
		CMD(CMD_SET_TEMPERATURE);
		CMD(CMD_SET_ALL);
		CMD(CMD_GET_ALL);
	}
}
