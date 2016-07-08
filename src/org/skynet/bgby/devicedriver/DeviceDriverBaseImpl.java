package org.skynet.bgby.devicedriver;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;

public abstract class DeviceDriverBaseImpl implements DeviceDriver {
	protected Set<DeviceConfigData> devicesNeedPolling;
	protected DeviceStatusManager deviceStatusManager;
	protected DeviceProfileManager deviceProfileManager;
	@Override
	public DeviceProfileManager getDeviceProfileManager() {
		return deviceProfileManager;
	}
	@Override
	public void setDeviceProfileManager(DeviceProfileManager deviceProfileManager) {
		this.deviceProfileManager = deviceProfileManager;
	}
	@Override
	public DeviceStatusManager getDeviceStatusManager() {
		return deviceStatusManager;
	}
	@Override
	public void setDeviceStatusManager(DeviceStatusManager deviceStatusManager) {
		this.deviceStatusManager = deviceStatusManager;
	}

	protected String ID;
	private Timer timer;
	protected void addPollingDevice(DeviceConfigData config) {
		if (devicesNeedPolling == null){
			devicesNeedPolling = new HashSet<>();
		}
		devicesNeedPolling.add(config);
	}
	@Override
	public abstract boolean canDriverDevice(String deviceID, DeviceStatus deviceStatus, DeviceProfile profile,
			DeviceConfigData devCfg);
	protected abstract long getDevicePollingPeriod();
	
	@Override
	public String getID() {
		return ID;
	}
	
	protected abstract boolean isNeedPollingDevice(DeviceProfile profile);
	
	@Override
	public void onStart() {
		if (devicesNeedPolling != null && !devicesNeedPolling.isEmpty()){
			timer = startPollingThread();
		}
	}
	@Override
	public void onStop() {
		if (timer != null){
			timer.cancel();
		}
	}
	protected abstract DeviceStatus pollingDevice(DeviceConfigData device);


	@Override
	public void setID(String id) {
		ID = id;
	}
	protected Timer startPollingThread() {
		long pollingPeriod = getDevicePollingPeriod();
		if (pollingPeriod <= 0) {
			return null;
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (devicesNeedPolling == null){
					return;
				}
				for(DeviceConfigData device: devicesNeedPolling){
					DeviceStatus status = pollingDevice(device);
					if (status != null){
						reportStatus(status);
					}
				}
			}
		}, 0, pollingPeriod);
		return timer;
	}
	
	protected void reportStatus(DeviceStatus device) {
		try {
			deviceStatusManager.updateDevice(device);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void initStatus(DeviceProfile profile, DeviceConfigData config, DeviceStatus device) throws DeviceDriverException {
		if (isNeedPollingDevice(profile)) {
			addPollingDevice(config);
		}
	}

}
