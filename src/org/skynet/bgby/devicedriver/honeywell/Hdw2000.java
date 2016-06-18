package org.skynet.bgby.devicedriver.honeywell;

import java.util.Map;

import org.skynet.bgby.devicedriver.DeviceDriver;
import org.skynet.bgby.devicestatus.DeviceStatus;

public class Hdw2000 implements DeviceDriver{
	protected Hdw2000Config config;
	protected String ID;
	
	@Override
	public void setConfig(Object cfgObject) {
		config = (Hdw2000Config) cfgObject;
	}

	public Hdw2000Config getConfig() {
		return config;
	}

	public void setConfig(Hdw2000Config config) {
		this.config = config;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	@Override
	public void initStatus(String profile, Map<String, String> identity, Map<String, Object> status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canDriverDevice(String deviceID, String profile, Map<String, String> identity) {
		// TODO Auto-generated method stub
		return false;
	}


}
