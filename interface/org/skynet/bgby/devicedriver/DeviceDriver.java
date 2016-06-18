package org.skynet.bgby.devicedriver;

import java.util.Map;

public interface DeviceDriver {

	void setConfig(Object cfgObject);

	void setID(String id);

	void initStatus(String profile, Map<String, String> identity, Map<String, Object> status);

	boolean canDriverDevice(String deviceID, String profile, Map<String, String> identity);

}
