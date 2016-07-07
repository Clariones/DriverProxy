package org.skynet.bgby.command.management;

import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.restserver.IRestCommandHandler;

public abstract class BaseManageCmd implements IRestCommandHandler {
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceConfigManager deviceConfigManager;
	protected LayoutManager layoutManager;
	protected DeviceStatusManager deviceStatusManager;
	
	public DeviceStatusManager getDeviceStatusManager() {
		return deviceStatusManager;
	}

	public void setDeviceStatusManager(DeviceStatusManager deviceStatusManager) {
		this.deviceStatusManager = deviceStatusManager;
	}

	public DeviceProfileManager getDeviceProfileManager() {
		return deviceProfileManager;
	}

	public void setDeviceProfileManager(DeviceProfileManager deviceProfileManager) {
		this.deviceProfileManager = deviceProfileManager;
	}

	public DeviceConfigManager getDeviceConfigManager() {
		return deviceConfigManager;
	}

	public void setDeviceConfigManager(DeviceConfigManager deviceConfigManager) {
		this.deviceConfigManager = deviceConfigManager;
	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	public abstract String getCommand();

	
}
