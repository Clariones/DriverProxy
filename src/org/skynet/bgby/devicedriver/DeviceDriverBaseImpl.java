package org.skynet.bgby.devicedriver;

import java.util.Map;

public abstract class DeviceDriverBaseImpl implements DeviceDriver {
	protected String ID;

	@Override
	public void setID(String id) {
		ID = id;
	}
	@Override
	public String getID() {
		return ID;
	}


	@Override
	public boolean canDriverDevice(String deviceID, String profile, Map<String, Object> identity) {
		return false;
	}

	@Override
	public void onStart() {

	}

}
