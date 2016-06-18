package org.skynet.bgby.driverproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.skynet.bgby.command.management.BaseManageCmd;
import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.devicedriver.DriverManager;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.restserver.RestService;

import fi.iki.elonen.NanoHTTPD;

public class DriverProxyService {
	protected static final String TAG = "DriverProxyService";
	protected DriverProxyConfiguration config;
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceConfigManager deviceConfigManager;
	protected DriverProxyRestClient restClient;
	protected DriverManager driverManager;
	protected DeviceStatusManager deviceStatusManager;

	public DeviceStatusManager getDeviceStatusManager() {
		return deviceStatusManager;
	}

	public void setDeviceStatusManager(DeviceStatusManager deviceStatusManager) {
		this.deviceStatusManager = deviceStatusManager;
	}

	public DriverProxyConfiguration getConfig() {
		return config;
	}

	public void setConfig(DriverProxyConfiguration config) {
		this.config = config;
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

	public DPStatusReporter getReporter() {
		return reporter;
	}

	public void setReporter(DPStatusReporter reporter) {
		this.reporter = reporter;
	}

	protected LayoutManager layoutManager;
	protected DPStatusReporter reporter;
	protected RestService restService;
	protected DriverProxyMulticastListener multicastHandler;
	protected CmdRestMngHandler restMngCmdHandler;
	protected int steps;

	public void start() {
		DriverUtils.log(Level.INFO, TAG, "Starting...");
		this.steps = 1;
		// create Http Server, Client, and Multicast handler
		restClient = new DriverProxyRestClient();
		restClient.setConnectionTimeout(config.getConnectionTimeout());
		restClient.setEncoding("UTF-8");
		restClient.setReadTimeout(config.getReadTimeout());
		reportStartStatus(steps++, "REST Client initialed");

		InetSocketAddress serverAddress = new InetSocketAddress(config.getRestServicePort());
		RestService service = new RestService();
		RestRequestCodec restProtocolHandler = new RestRequestCodec();
		service.setRestProtocolHandler(restProtocolHandler);
		this.restService = service;
		reportStartStatus(steps++, "REST Service initialed");

		initRestCommandHandlers();
		reportStartStatus(steps++, "REST command handlers initialed");

		multicastHandler = new DriverProxyMulticastListener();
		multicastHandler.setListeningAddress(config.getMulticastAddress());
		multicastHandler.setListeningPort(config.getMulticastPort());
		multicastHandler.setDamon(false);
		reportStartStatus(steps++, "Multicast message handler initialed");

		// start all these services
		try {
			restService.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			reportStartStatus(steps++, "REST service started");
		} catch (IOException e) {
			e.printStackTrace();
			String stack = DriverUtils.dumpExceptionToString(e);
			reportError(steps++, "Start REST service fail.", stack);
			stop();
			return;
		}

		try {
			multicastHandler.start();
			reportStartStatus(steps++, "Multicast message handler started");
		} catch (ListeningServerException e) {
			e.printStackTrace();
			String stack = DriverUtils.dumpExceptionToString(e);
			reportError(steps++, "Start multicast message handler fail.", stack);
			stop();
			return;
		}

		// call all these components start() method
		try {
			ModuleStartingReporter mRept = new ModuleStartingReporter();
			
			deviceProfileManager.setStartingReporter(mRept);
			deviceProfileManager.start();			
			reportStartStatus(steps++, "Device profile manager started");
			
			deviceStatusManager.setStartingReporter(mRept);
			deviceStatusManager.start();
			reportStartStatus(steps++, "Driver status manager started");
			
			deviceConfigManager.setStartingReporter(mRept);
			deviceConfigManager.start();
			reportStartStatus(steps++, "Device config manager started");
			
			driverManager.setStartingReporter(mRept);
			driverManager.setDeviceStatusManager(deviceStatusManager);
			driverManager.setDeviceProfileManager(deviceProfileManager);
			driverManager.setDeviceConfigManager(deviceConfigManager);
			driverManager.start();
			reportStartStatus(steps++, "Driver manager started");
			
			layoutManager.setStartingReporter(mRept);
			layoutManager.start();
			reportStartStatus(steps++, "Layout manager started");
			
		} catch (DPModuleException e) {
			e.printStackTrace();
			String stack = DriverUtils.dumpExceptionToString(e);
			reportError(steps++, "Start modules fail.", stack);
			stop();
			return;
		}

	}

	private void stop() {
		if (this.multicastHandler.isAlive()){
			multicastHandler.stop();
		}
		if (this.restService.isAlive()){
			restService.stop();
		}
	}

	protected void reportStartStatus(int i, String string) {
		reportStartStatus(i, string, null);
	}

	protected void reportError(int i, String strTitle, String strMessage) {
		if (getReporter() != null) {
			getReporter().reportError(i, strTitle, strMessage);
		} else {
			DriverUtils.log(Level.SEVERE, TAG, strTitle + "\r\n" + strMessage);
		}
	}

	protected void initRestCommandHandlers() {
		initManagementCommandHandlers();
		initDeviceCommandHandlers();
	}

	/**
	 * 有以下几个管理命令需要被处理： 1. 取控制屏的Layout 2. 取控制屏的Layout中的所有设备的Profile
	 */
	protected void initManagementCommandHandlers() {
		this.restMngCmdHandler = new CmdRestMngHandler();
		registerMngCmdHandler(new CmdGetLayout());
		registerMngCmdHandler(new CmdGetProfileByDevice());
		this.restService.registerCommandHandler(restMngCmdHandler);
	}

	protected void registerMngCmdHandler(BaseManageCmd cmdHandler) {
		String command = cmdHandler.getCommand();
		cmdHandler.setDeviceConfigManager(getDeviceConfigManager());
		cmdHandler.setDeviceProfileManager(getDeviceProfileManager());
		cmdHandler.setLayoutManager(getLayoutManager());
		restMngCmdHandler.addHandler(command, cmdHandler);
	}

	protected void initDeviceCommandHandlers() {
		// TODO Auto-generated method stub

	}

	protected void reportStartStatus(int i, String strMsg, String detail) {
		if (getReporter() != null) {
			getReporter().reportStatus(i, strMsg, detail);
		} else {
			DriverUtils.log(Level.INFO, TAG, strMsg);
		}
	}

	public DriverManager getDriverManager() {
		return driverManager;
	}

	public void setDriverManager(DriverManager driverManager) {
		this.driverManager = driverManager;
	}

	class ModuleStartingReporter implements DPModuleStatusReporter{

		@Override
		public void reportStatus(int id, String title, String detail) {
			DriverProxyService.this.reportStartStatus(steps, id + "." + title, detail);
		}

		@Override
		public void reportError(int id, String title, String detail) {
			DriverProxyService.this.reportError(steps, id + "." + title, detail);
		}
		
	}
}
