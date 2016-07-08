package org.skynet.bgby.deviceprofile;

import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;

public interface DeviceProfileRepository {

	DeviceProfile getDeviceProfile(String profileID);

	Map<String, DeviceProfile> getAll();

	UpdateResult update(Map<String, DeviceProfile> data, boolean overWriteAll);

}
