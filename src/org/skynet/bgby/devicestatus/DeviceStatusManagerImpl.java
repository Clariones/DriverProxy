package org.skynet.bgby.devicestatus;

import java.io.IOException;
import java.util.Map;

import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyService;

public class DeviceStatusManagerImpl implements DeviceStatusManager {
	protected DPModuleStatusReporter startingReporter;
	protected DeviceStatusRepository repository;
	protected DriverProxyService proxy;


	@Override
	public void start() throws DPModuleException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStartingReporter(DPModuleStatusReporter reporter) {
		startingReporter = reporter;
	}

	@Override
	public Map<String, DeviceStatus> listAllDevices() {
		return repository.getAll();
	}

	@Override
	public DeviceStatus getDevice(String devId) {
		return repository.getDeviceStatus(devId);
	}

	@Override
	public void updateDevice(DeviceStatus deviceStatus) throws IOException {
		repository.updateDeviceStatus(deviceStatus);
		proxy.multicastDeviceStatus(deviceStatus);
	}

	@Override
	public void setDriverProxy(DriverProxyService proxy) {
		this.proxy=proxy;
	}

	@Override
	public void removeDevice(String deviceId) {
		repository.removeDevice(deviceId);
	}

}
