package org.skynet.bgby.deviceconfig;

public class DeviceManager {

	public DeviceConfigData getDeviceConfigData(String devId) {
		DeviceConfigData demoData = new DeviceConfigData();
		demoData.setProfile("Honeywell HDW 2000 HVAC");
		return demoData;
	}

}
