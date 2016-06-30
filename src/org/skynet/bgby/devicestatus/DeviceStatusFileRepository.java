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

	@Override
	public DeviceStatus getDeviceStatus(String devId) {
		return getDataByID(devId);
	}

	@Override
	public void updateDeviceStatus(DeviceStatus data) throws IOException {
		DeviceStatus mineData = getDataByID(data.getID());
		if (mineData == data){
			saveData(data.getID(), data);
			return;
		}
		if (mineData == null){
			this.setData(data.getID(), data);
			return;
		}
		mineData.getStatus().putAll(data.getStatus());
		saveData(data.getID(), mineData);
	}


}
