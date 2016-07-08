package org.skynet.bgby.deviceprofile;

import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;
import org.skynet.bgby.driverproxy.DPManagedModule;

public interface DeviceProfileManager extends DPManagedModule{

	DeviceProfile getProfile(String profileID);

	Map<String, DeviceProfile> listAllProfiles();

	UpdateResult update(Map<String, DeviceProfile> data, boolean overWriteAll);


}
