package org.skynet.bgby.deviceconfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.driverutils.SimpleFileRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeviceConfigFileRepository extends SimpleFileRepository<DeviceConfigData>
		implements DeviceConfigRepository {
	@Override
	protected DeviceConfigData loadFromFile(FileInputStream fIns) {
		return gson.fromJson(new InputStreamReader(fIns), DeviceConfigData.class);
	}

	@Override
	protected String convertToJsonStr(DeviceConfigData data) throws IOException {
		return gson.toJson(data);
	}

	@Override
	public DeviceConfigData getConfigData(String devId) {
		return getDataByID(devId);
	}

	@Override
	protected String getFilePostfix() {
		return ".config.json";
	}

	
}
