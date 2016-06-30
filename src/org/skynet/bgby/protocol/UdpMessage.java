package org.skynet.bgby.protocol;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UdpMessage {
	public static final String FIELD_COMMAND = "X_CMD";
	public static final String FIELD_FROM_APP = "X_APP";
	public static final String FIELD_DEVICE = "X_DEV";
	public static final String FIELD_TO = "X_TO";
	public static final String FIELD_DEVICE_STATUES_PREFIX = "DEV_STATUES_";
	
	public static final String CMD_UPDATE_CONFIG = "update_configration";
	public static final String CMD_DEVICE_ONLINE = "device_online";
	public static final String CMD_DEVICE_STATUS_REPORT = "device_report";
	
	public static final String APP_PC_TOOL = "pc_tool";
	public static final String APP_DRIVER_PROXY = "driver";
	public static final String APP_TOUCH_PAD = "touch_pad";
	
	public static final String VALUE_ALL = "all";
	
	protected static final List<String> sAllRecievers = new ArrayList<String>();
	static {
		sAllRecievers.add(VALUE_ALL);
	}
	
	protected String command;
	protected String fromApp;
	protected String fromDevice;
	protected List<String> receivers;
	protected Map<String, String> params;
	protected SocketAddress fromAddress;
	
	public SocketAddress getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(SocketAddress fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getFromApp() {
		return fromApp;
	}
	public void setFromApp(String fromApp) {
		this.fromApp = fromApp;
	}
	public String getFromDevice() {
		return fromDevice;
	}
	public void setFromDevice(String fromDevice) {
		this.fromDevice = fromDevice;
	}
	public List<String> getReceivers() {
		return receivers;
	}
	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	
}
