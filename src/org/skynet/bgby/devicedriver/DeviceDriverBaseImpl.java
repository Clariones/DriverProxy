package org.skynet.bgby.devicedriver;

import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestatus.DeviceStatus;

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
	public boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile,
			DeviceConfigData devCfg) {
		return false;
	}
	@Override
	public void onStart() {

	}

}
