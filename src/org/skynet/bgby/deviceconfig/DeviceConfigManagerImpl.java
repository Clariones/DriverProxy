package org.skynet.bgby.deviceconfig;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.deviceprofile.DeviceProfileRepository;

public class DeviceConfigManagerImpl implements DeviceConfigManager {

	protected DeviceConfigRepository repository;
	
	public DeviceConfigRepository getRepository() {
		return repository;
	}

	public void setRepository(DeviceConfigRepository repository) {
		this.repository = repository;
	}

	@Override
	public DeviceConfigData getDeviceConfigData(String devId) {
		return repository.getConfigData(devId);
	}

}
