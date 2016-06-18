package org.skynet.bgby.devicestatus;

import java.util.List;

import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceStatusManager extends DPManagedModule {

	List<DeviceStatus> listAllDevices();

}
