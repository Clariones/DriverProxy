package org.skynet.bgby.devicestatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.driverutils.SimpleFileRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeviceStatusFileRepository extends SimpleFileRepository<DeviceStatus> implements DeviceStatusRepository {

	@Override
	protected String convertToJsonStr(DeviceStatus data) throws IOException {
		return gson.toJson(data);
	}

	@Override
	protected String getFilePostfix() {
		return ".status.json";
	}

	@Override
	protected DeviceStatus loadFromFile(FileInputStream fIns) {
		return gson.fromJson(new InputStreamReader(fIns), DeviceStatus.class);
	}
	
	

}
