package org.skynet.bgby.deviceconfig;

import java.util.List;
import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyService;

public class DeviceConfigManagerImpl implements DeviceConfigManager {
	protected DriverProxyService proxy;
	protected DeviceConfigRepository repository;
	protected DPModuleStatusReporter startingReporter;
	
	@Override
	public DeviceConfigData getDeviceConfigData(String devId) {
		return repository.getConfigData(devId);
	}

	public DeviceConfigRepository getRepository() {
		return repository;
	}

	@Override
	public void setDriverProxy(DriverProxyService proxy) {
		this.proxy = proxy;
	}

	public void setRepository(DeviceConfigRepository repository) {
		this.repository = repository;
	}

	@Override
	public void setStartingReporter(DPModuleStatusReporter reporter) {
		startingReporter = reporter;
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
	public Map<String, DeviceConfigData> listAllDevices() {
		return repository.getAll();
	}

	@Override
	public UpdateResult update(Map<String, DeviceConfigData> data, boolean overWriteAll) {
		return repository.update(data, overWriteAll);
	}

	
}
