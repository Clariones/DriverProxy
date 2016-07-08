package org.skynet.bgby.deviceconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.skynet.bgby.driverutils.SimpleFileRepository;

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

	@Override
	protected String getDataKey(File dataFile, DeviceConfigData result) {
		return result.getID();
	}

	@Override
	protected void verifyData(DeviceConfigData data) throws IOException {
		if (data.getID() == null){
			throw new IOException("DeviceConfigData without ID");
		}
		if (data.getIdentity() == null || data.getIdentity().isEmpty()){
			throw new IOException("DeviceConfigData " + data.getID()+" without identify");
		}
		if (data.getProfile() == null){
			throw new IOException("DeviceConfigData " + data.getID()+" without profile");
		}
	}

	
}
