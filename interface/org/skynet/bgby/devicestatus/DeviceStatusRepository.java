package org.skynet.bgby.devicestatus;

import java.io.IOException;
import java.util.Map;

public interface DeviceStatusRepository {

	DeviceStatus getDeviceStatus(String devId);

	void updateDeviceStatus(DeviceStatus deviceStatus) throws IOException;

	Map<String, DeviceStatus> getAll();

	void removeDevice(String deviceId);

}
