package org.skynet.bgby.deviceprofile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DeviceProfileManagerPCImpl extends DeviceProfileManagerImpl {
	
	public DeviceProfileManagerPCImpl(){
		repository = new DeviceProfileFileRepository();
	}

	protected File baseFolder;

	public File getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(File baseFolder) {
		this.baseFolder = baseFolder;
		if (this.repository == null){
			repository = new DeviceProfileFileRepository();
		}
		DeviceProfileFileRepository repo = (DeviceProfileFileRepository) repository;
		repo.setBaseFolder(baseFolder);
	}
	

}
