package org.skynet.bgby.devicestatus;

import java.io.File;

public class DeviceStatusManagerPCImpl extends DeviceStatusManagerImpl {
	protected File baseFolder;
	protected DeviceStatusRepository repository;

	public DeviceStatusManagerPCImpl() {
		repository = new DeviceStatusFileRepository(); 
	}

	public void setBaseFolder(File file) {
		baseFolder = file;
		if (repository == null){
			repository = new DeviceStatusFileRepository();
		}
		((DeviceStatusFileRepository)repository).setBaseFolder(file);
	}

}
