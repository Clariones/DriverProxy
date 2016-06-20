package org.skynet.bgby.devicestandard;

import java.util.HashSet;
import java.util.Set;

public abstract class DeviceStandardBaseImpl implements DeviceStandard {
	public void setId(String id) {
		this.id = id;
	}

	public static final String TERM_DISPLAY_NAME = "displayName";
	public static final String CMD_GET_ALL = "getAll";
	public static final String CMD_SET_ALL = "setAll";
	
	public static final int ERR_DEVICE_COMMON_START_CODE = 75000;
	public static final int ERR_UNSUPPORTED_COMMAND = ERR_DEVICE_COMMON_START_CODE + 1;
	public static final int ERR_IO_EXCEPTION = ERR_DEVICE_COMMON_START_CODE + 2;
	public static final int ERR_WRONG_PROFILE = ERR_DEVICE_COMMON_START_CODE + 3;
	public static final int ERR_MISS_PROFILE = ERR_DEVICE_COMMON_START_CODE + 4;
	
	protected Set<String> terms;
	protected Set<String> commands;
	protected String id;
	
	
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
