package org.skynet.bgby.deviceprofile;

import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceProfileManager extends DPManagedModule{

	DeviceProfile getProfile(String profileID);


}
