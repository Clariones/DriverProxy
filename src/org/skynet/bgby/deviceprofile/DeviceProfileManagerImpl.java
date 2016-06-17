package org.skynet.bgby.deviceprofile;

public class DeviceProfileManagerImpl implements DeviceProfileManager {
	protected DeviceProfileRepository repository;
	
	public DeviceProfileRepository getRepository() {
		return repository;
	}

	public void setRepository(DeviceProfileRepository repository) {
		this.repository = repository;
	}

	@Override
	public DeviceProfile getProfile(String profileID) {
		return repository.getDeviceProfile(profileID);
	}

}
