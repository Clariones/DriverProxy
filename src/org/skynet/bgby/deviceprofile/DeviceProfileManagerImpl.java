package org.skynet.bgby.deviceprofile;

import java.util.List;
import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyService;

public class DeviceProfileManagerImpl implements DeviceProfileManager {
	protected DeviceProfileRepository repository;
	protected DPModuleStatusReporter startingReporter;
	protected DriverProxyService proxy;
	
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

	@Override
	public void setDriverProxy(DriverProxyService proxy) {
		this.proxy = proxy;
	}

	@Override
	public Map<String, DeviceProfile> listAllProfiles() {
		return repository.getAll();
	}

	@Override
	public UpdateResult update(Map<String, DeviceProfile> data, boolean overWriteAll) {
		return repository.update(data, overWriteAll);
	}

	
}
