package org.skynet.bgby.devicestatus;

import java.io.IOException;
import java.util.Map;

import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceStatusManager extends DPManagedModule {

	Map<String, DeviceStatus> listAllDevices();

	DeviceStatus getDevice(String devId);

	void updateDevice(DeviceStatus deviceStatus) throws IOException;

	void removeDevice(String deviceId);

}
