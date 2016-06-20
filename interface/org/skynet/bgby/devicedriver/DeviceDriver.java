package org.skynet.bgby.devicedriver;

import java.util.Map;

import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.protocol.IRestResponse;

public interface DeviceDriver {

	boolean canDriverDevice(String deviceID, String profile, Map<String, Object> identity);

	String getID();

	void initStatus(String profile, Map<String, Object> identity, Map<String, Object> status);

	IRestResponse onCommand(String command, DeviceStatus deviceStatus, Map<String, String> map) throws DeviceDriverException;

	void onStart();

	void setConfig(Object cfgObject);

	void setID(String id);

}
