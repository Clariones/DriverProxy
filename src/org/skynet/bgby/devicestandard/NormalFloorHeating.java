package org.skynet.bgby.devicestandard;

public class NormalFloorHeating extends DeviceStandardBaseImpl {

	public static final String CMD_GET_ROOM_TEMPERATURE = "getRoomTemperature";
	public static final String CMD_GET_STATE = "getState";
	public static final String CMD_GET_TEMPERATURE_SETTING = "getTemperatureSetting";
	public static final String CMD_SET_STATE = "setState";
	public static final String CMD_SET_TEMPERATURE = "setTemperature";

	public static final String ID = "FloorHeating.Normal";

	public static final String TERM_ROOM_TEMPERATURE = "roomTemperature";
	public static final String TERM_ROOM_TERPERATURE_RANGE = "roomTemperatureRange";
	public static final String TERM_STATE = "state";
	public static final String TERM_SET_TEMPERATURE = "temperatureSetting";
	public static final String TERM_SET_TEMPERATURE_RANGE = "temperatureSettingRange";


	public static final int ERR_CONNECT_TO_GATEWAY = ERR_FLOORHEATING_START_CODE + 1;
	public static final int ERR_SET_TEMP_OUT_OF_RANGE = ERR_FLOORHEATING_START_CODE + 2;

	public NormalFloorHeating() {
		super();
		id = ID;
		TERM(TERM_ROOM_TEMPERATURE);
		TERM(TERM_ROOM_TERPERATURE_RANGE);
		TERM(TERM_STATE);
		TERM(TERM_SET_TEMPERATURE);
		TERM(TERM_SET_TEMPERATURE_RANGE);

		CMD(CMD_GET_ROOM_TEMPERATURE);
		CMD(CMD_GET_STATE);
		CMD(CMD_GET_TEMPERATURE_SETTING);
		CMD(CMD_SET_STATE);
		CMD(CMD_SET_TEMPERATURE);
	}
}
