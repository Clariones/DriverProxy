package org.skynet.bgby.deviceconfig;

import java.io.File;

import org.skynet.bgby.driverutils.SimpleFileRepository;

public class DeviceConfigManagerPCImpl extends DeviceConfigManagerImpl {

	public DeviceConfigManagerPCImpl(){
		repository = new DeviceConfigFileRepository();
	}

	protected File baseFolder;

	public File getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
		if (this.repository == null){
			repository = new DeviceConfigFileRepository();
		}
		SimpleFileRepository<DeviceConfigData> repo = (SimpleFileRepository<DeviceConfigData>) repository;
		repo.setBaseFolder(baseFolder);
	}

}
