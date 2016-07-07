package org.skynet.bgby.deviceconfig;

import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceConfigManager extends DPManagedModule{

	DeviceConfigData getDeviceConfigData(String devId);

	Map<String, DeviceConfigData> listAllDevices();

	UpdateResult update(Map<String, DeviceConfigData> data, boolean overWriteAll);

}
