package org.skynet.bgby.devicestatus;

import java.io.File;

public class DeviceStatusManagerPCImpl extends DeviceStatusManagerImpl {
	protected File baseFolder;

	public DeviceStatusManagerPCImpl() {
		// TODO Auto-generated constructor stub
	}

	public void setBaseFolder(File file) {
		baseFolder = file;
	}

}
