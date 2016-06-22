package org.skynet.bgby.devicestatus;

import java.io.IOException;
import java.util.List;

import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;

public class DeviceStatusManagerImpl implements DeviceStatusManager {
	protected DPModuleStatusReporter startingReporter;
	protected DeviceStatusRepository repository;


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
	public List<DeviceStatus> listAllDevices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeviceStatus getDevice(String devId) {
		return repository.getDeviceStatus(devId);
	}

	@Override
	public void updateDevice(DeviceStatus deviceStatus) throws IOException {
		repository.updateDeviceStatus(deviceStatus);
	}

}
