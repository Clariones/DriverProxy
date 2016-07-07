package org.skynet.bgby.devicestandard;

import java.util.HashSet;
import java.util.Set;

import org.skynet.bgby.error.ErrorCode;

public abstract class DeviceStandardBaseImpl implements DeviceStandard {
	public void setId(String id) {
		this.id = id;
	}

	public static final String TERM_DISPLAY_NAME = "displayName";
	public static final String TERM_CAN_QUERY = "canQueryStatus";
	public static final String TERM_STATE_ON = "on";
	public static final String TERM_STATE_OFF = "off";
	public static final String CMD_GET_ALL = "getAll";
	public static final String CMD_SET_ALL = "setAll";
	
	public static final int ERR_UNSUPPORTED_COMMAND = ErrorCode.DEVICE_COMMON_START_CODE + 1;
	public static final int ERR_IO_EXCEPTION = ErrorCode.DEVICE_COMMON_START_CODE + 2;
	public static final int ERR_WRONG_PROFILE = ErrorCode.DEVICE_COMMON_START_CODE + 3;
	public static final int ERR_MISS_PROFILE = ErrorCode.DEVICE_COMMON_START_CODE + 4;
	public static final int ERR_WRONG_STATUS = ErrorCode.DEVICE_COMMON_START_CODE + 5;
	
	protected Set<String> terms;
	protected Set<String> commands;
	protected String id;
	
	public DeviceStandardBaseImpl(){
		TERM(TERM_CAN_QUERY);
		TERM(TERM_DISPLAY_NAME);
		
		CMD(CMD_GET_ALL);
		CMD(CMD_SET_ALL);
	}
	
	@Override
	public Set<String> getTerms() {
		return terms;
	}

	@Override
	public Set<String> getCommands() {
		return commands;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isValidTerm(String term){
		assert(terms != null);
		return terms.contains(term);
	}
	
	@Override
	public boolean isSupportCommand(String command){
		assert(commands != null);
		return commands.contains(command);
	}
	
	protected void TERM(String term){
		if (terms == null){
			terms = new HashSet<>();
		}
		terms.add(term);
	}
	
	protected void CMD(String command){
		if (commands == null){
			commands = new HashSet<>();
		}
		commands.add(command);
	}
}
