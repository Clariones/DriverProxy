package org.skynet.bgby.deviceprofile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.skynet.bgby.driverutils.SimpleFileRepository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeviceProfileFileRepository extends SimpleFileRepository<DeviceProfile> implements DeviceProfileRepository {
	@Override
	public DeviceProfile getDeviceProfile(String profileID) {
		return getDataByID(profileID);
	}

	@Override
	protected DeviceProfile loadFromFile(FileInputStream fIns) {
		return gson.fromJson(new InputStreamReader(fIns), DeviceProfile.class);
	}

	@Override
	protected String convertToJsonStr(DeviceProfile data) throws IOException {
		return gson.toJson(data);
	}

	@Override
	protected String getFilePostfix() {
		return ".profile.json";
	}


}
