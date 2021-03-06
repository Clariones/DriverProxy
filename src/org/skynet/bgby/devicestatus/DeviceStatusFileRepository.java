package org.skynet.bgby.devicestatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.skynet.bgby.driverutils.SimpleFileRepository;

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

	@Override
	protected String getDataKey(File dataFile, DeviceStatus result) {
		return result.getID();
	}

	@Override
	protected void verifyData(DeviceStatus data) throws IOException {
		if (data.getID() == null){
			throw new IOException("DeviceStatus without ID");
		}
		if (data.getProfile() == null){
			throw new IOException("DeviceStatus " + data.getID()+" without profile");
		}
	}

	@Override
	public void removeDevice(String deviceId) {
		this.deleteData(deviceId);
	}


}
