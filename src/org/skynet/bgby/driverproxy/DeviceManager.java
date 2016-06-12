package org.skynet.bgby.driverproxy;

import org.skynet.bgby.device.DeviceConfigData;

public class DeviceManager {

	public DeviceConfigData getDeviceConfigData(String devId) {
		DeviceConfigData demoData = new DeviceConfigData();
		demoData.setProfile("Honeywell HDW 2000 HVAC");
		return demoData;
	}

}
