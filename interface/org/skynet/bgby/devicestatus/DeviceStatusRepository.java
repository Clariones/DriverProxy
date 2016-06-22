package org.skynet.bgby.devicestatus;

import java.io.IOException;

public interface DeviceStatusRepository {

	DeviceStatus getDeviceStatus(String devId);

	void updateDeviceStatus(DeviceStatus deviceStatus) throws IOException;

}
