package org.skynet.bgby.devicedriver;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverproxy.ExecutionContext;
import org.skynet.bgby.protocol.IRestResponse;

public interface DeviceDriver {



	boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile, DeviceConfigData devCfg);

	String getID();

	void initStatus(DeviceProfile profile, DeviceConfigData config, DeviceStatus device) throws DeviceDriverException;

	void onStart();

	void setConfig(Object cfgObject);

	void setID(String id);

	IRestResponse onCommand(ExecutionContext ctx) throws DeviceDriverException;
	
	void setDeviceStatusManager(DeviceStatusManager deviceStatusManager);

	DeviceStatusManager getDeviceStatusManager();

	void onStop();

	void setDeviceProfileManager(DeviceProfileManager deviceProfileManager);

	DeviceProfileManager getDeviceProfileManager();

}
