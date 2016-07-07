package org.skynet.bgby.deviceconfig;

import java.util.Map;

import org.skynet.bgby.command.management.UpdateResult;

public interface DeviceConfigRepository {

	DeviceConfigData getConfigData(String devId);

	Map<String, DeviceConfigData> getAll();

	int clearAll();

	UpdateResult update(Map<String, DeviceConfigData> data, boolean overWriteAll);


}
