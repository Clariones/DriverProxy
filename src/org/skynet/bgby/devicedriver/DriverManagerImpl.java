package org.skynet.bgby.devicedriver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverproxy.DPModuleException;
import org.skynet.bgby.driverproxy.DPModuleStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyService;
import org.skynet.bgby.driverutils.DriverUtils;

import com.google.gson.Gson;

public abstract class DriverManagerImpl implements DriverManager {
	protected static final String TAG = DriverManagerImpl.class.getName();

	protected int curStartingStep = 1;

	protected DeviceConfigManager deviceConfigManager;
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceStatusManager deviceStatusManager;
	protected List<DeviceDriver> driverList;
	protected Map<String, DeviceDriver> driverMap;
	protected List<DriverRegisterInfo> driverRegisterInfos;
	protected Gson gson = new Gson();
	protected DriverProxyService proxy;
	protected DPModuleStatusReporter startingReporter;
	protected void addDriverRegisterInfo(DriverRegisterInfo info) {
		if (driverRegisterInfos == null) {
			driverRegisterInfos = new ArrayList<>();
		}
		driverRegisterInfos.add(info);
	}

	/**
	 * Create driver with their register info (which from file now)
	 * @param config
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	protected void createDriver(DriverRegisterInfo config)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		DeviceDriver driver = (DeviceDriver) Class.forName(config.getDriverClass()).newInstance();
		Class cfgClass = Class.forName(config.getConfigurationClass());
		Object cfgObject = gson.fromJson(config.getConfigurationData(), cfgClass);
		driver.setConfig(cfgObject);
		driver.setID(config.getID());
		holdDriver(config.getID(), driver);
	}

	protected void createDrivers() throws DriverModuleException {
		Iterator<DriverRegisterInfo> it = driverRegisterInfos.iterator();
		while (it.hasNext()) {
			DriverRegisterInfo info = it.next();
			try {
				createDriver(info);
			} catch (Exception e) {
				String msg = DriverUtils.dumpExceptionToString(e);
				startingReporter.reportError(this.curStartingStep(), "Failed to create driver", msg);
				throw new DriverModuleException("Failed to create driver", e);
			}
		}
	}

	protected int curStartingStep() {
		return curStartingStep++;
	}

	@Override
	public DeviceConfigManager getDeviceConfigManager() {
		return deviceConfigManager;
	}

	@Override
	public DeviceProfileManager getDeviceProfileManager() {
		return deviceProfileManager;
	}

	@Override
	public DeviceStatusManager getDeviceStatusManager() {
		return deviceStatusManager;
	}

	void holdDriver(String id, DeviceDriver driver) {
		if (driverList == null) {
			driverList = new ArrayList<>();
		}
		driverList.add(driver);
	}

	protected void initDriverStatus() throws DPModuleException {
		 Map<String, DeviceStatus> deviceStatus = deviceStatusManager.listAllDevices();
		if (deviceStatus == null || deviceStatus.isEmpty()){
			return;
		}
		for (DeviceStatus device : deviceStatus.values()) {
			DeviceConfigData config = getDeviceConfigManager().getDeviceConfigData(device.getID());
			DeviceDriver driver = lookupDriverForDevice(device.getID(), device, config);
			if (driver == null){
				String msg = String.format("Cannot found any driver for device %s(%s,%s)",
						device.getID(), device.getProfile(), config.getIdentity());
				DriverUtils.log(Level.SEVERE, TAG, msg);
				throw new DPModuleException(msg);
			}
			driver.initStatus(device.getProfile(), config.getIdentity(), device.getStatus());
		}
	}

	protected abstract void loadDriverRegisterInfos() throws DriverModuleException;

	@Override
	public DeviceDriver lookupDriverForDevice(String deviceID, DeviceStatus deviceStatus, DeviceConfigData devCfg)
			throws DPModuleException {
		// first, try to get cached driver
		if (driverMap != null && driverMap.containsKey(deviceID)) {
			return driverMap.get(deviceID);
		}
		
		// then go through all the loaded drivers, find the on which can drive this device
		Iterator<DeviceDriver> it = driverList.iterator();
		DeviceProfile profile = this.getDeviceProfileManager().getProfile(devCfg.getProfile());
		while(it.hasNext()){
			DeviceDriver driver = it.next();
			if (driver.canDriverDevice(deviceID, deviceStatus, profile, devCfg)){
				putIntoDriverMap(deviceID, driver);
				return driver;
			}
		}
		return null;
	}

	
	protected void putIntoDriverMap(String deviceID, DeviceDriver driver) {
		if (driverMap == null){
			synchronized (this){
				if (driverMap == null){
					driverMap = new ConcurrentHashMap<>();
				}
			}
		}
		driverMap.put(deviceID, driver);
	}

	@Override
	public void setDeviceConfigManager(DeviceConfigManager deviceConfigManager) {
		this.deviceConfigManager = deviceConfigManager;
	}

	@Override
	public void setDeviceProfileManager(DeviceProfileManager deviceProfileManager) {
		this.deviceProfileManager = deviceProfileManager;
	}

	@Override
	public void setDeviceStatusManager(DeviceStatusManager deviceStatusManager) {
		this.deviceStatusManager = deviceStatusManager;
	}

	@Override
	public void setDriverProxy(DriverProxyService proxy) {
		this.proxy = proxy;
		
	}

	@Override
	public void setStartingReporter(DPModuleStatusReporter reporter) {
		startingReporter = reporter;
	}

	@Override
	public void start() throws DPModuleException {
		loadDriverRegisterInfos();
		createDrivers();
		initDriverStatus();
		startDrivers();
	}

	protected void startDrivers() {
		Iterator<DeviceDriver> it = driverList.iterator();
		while(it.hasNext()){
			it.next().onStart();
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
}
