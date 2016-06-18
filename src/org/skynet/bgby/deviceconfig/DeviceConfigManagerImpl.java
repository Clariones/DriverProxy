package org.skynet.bgby.deviceconfig;

import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;

public class DeviceConfigManagerImpl implements DeviceConfigManager {
	protected DPModuleStatusReporter startingReporter;
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
