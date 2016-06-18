package org.skynet.bgby.deviceconfig;

import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceConfigManager extends DPManagedModule{

	DeviceConfigData getDeviceConfigData(String devId);

}
