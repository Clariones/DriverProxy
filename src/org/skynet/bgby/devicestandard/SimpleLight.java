package org.skynet.bgby.devicestandard;

import org.skynet.bgby.error.ErrorCode;

public class SimpleLight extends DeviceStandardBaseImpl {
	public static final String ID = "Light.Simple";
	
	public static final String TERM_LIGHT_STATUES = "state";
	public static final String TERM_LIGHT_ON = TERM_STATE_ON;
	public static final String TERM_LIGHT_OFF = TERM_STATE_OFF;
	public static final String TERM_CAN_TOGGLE = "canDoToggle";
	public static final String CMD_SET_LIGHT = "setLight";
	public static final String CMD_GET_LIGHT = "getLight";
	public static final String CMD_TOGGLE_LIGHT = "toggleLight";
	
	public static final int ERR_UNSUPPORTED_ACTION = ErrorCode.LIGHT_START_CODE + 1;
	public static final int ERR_MISS_LIGHT_STATUES = ErrorCode.LIGHT_START_CODE + 2;

	public SimpleLight() {
		super();
		id = ID;
		
		TERM(TERM_DISPLAY_NAME);
		TERM(TERM_LIGHT_STATUES);
		TERM(TERM_CAN_TOGGLE);
		
		CMD(CMD_SET_LIGHT);
		CMD(CMD_GET_LIGHT);
		CMD(CMD_TOGGLE_LIGHT);
	}

}
