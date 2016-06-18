package org.skynet.bgby.deviceprofile;

import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;

public class DeviceProfileManagerImpl implements DeviceProfileManager {
	protected DeviceProfileRepository repository;
	protected DPModuleStatusReporter startingReporter;
	
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

	@Override
	public void start() throws DPModuleException{
		// so far nothing to do when start
	}

	@Override
	public void stop() {
		// so far nothing to do when stop
		
	}

	@Override
	public void setStartingReporter(DPModuleStatusReporter reporter) {
		startingReporter = reporter;
	}

}
