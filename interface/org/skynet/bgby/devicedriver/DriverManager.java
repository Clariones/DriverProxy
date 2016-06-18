package org.skynet.bgby.devicedriver;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverproxy.DPManagedModule;
import org.skynet.bgby.driverproxy.DPModuleException;

public interface DriverManager extends DPManagedModule{

	DeviceConfigManager getDeviceConfigManager();

	DeviceProfileManager getDeviceProfileManager();

	DeviceStatusManager getDeviceStatusManager();


	void setDeviceConfigManager(DeviceConfigManager deviceConfigManager);

	void setDeviceProfileManager(DeviceProfileManager deviceProfileManager);

	void setDeviceStatusManager(DeviceStatusManager deviceStatusManager);

	DeviceDriver lookupDriverForDevice(String deviceID, String profile, DeviceConfigData config)
			throws DPModuleException;


}
