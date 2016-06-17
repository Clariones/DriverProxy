package org.skynet.bgby.deviceconfig;

import java.io.File;

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
		DeviceConfigFileRepository repo = (DeviceConfigFileRepository) repository;
		repo.setBaseFolder(baseFolder);
	}

}
