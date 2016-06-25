package org.skynet.bgby.devicedriver;

import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.protocol.IRestResponse;

public interface DeviceDriver {

	boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile, DeviceConfigData devCfg);

	String getID();

	void initStatus(String profile, Map<String, Object> identity, Map<String, Object> status);

	void onStart();

	void setConfig(Object cfgObject);

	void setID(String id);

	IRestResponse onCommand(ExecutionContext ctx) throws DeviceDriverException;;

}
