package org.skynet.bgby.driverproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.skynet.bgby.command.management.BaseManageCmd;
import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.restserver.RestService;

import fi.iki.elonen.NanoHTTPD;

public class DriverProxyService {
	private static final String TAG = "DriverProxyService";
	protected DriverProxyConfiguration config;
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceConfigManager deviceConfigManager;
	protected DriverProxyRestClient restClient;
	
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

	public IStatusReporter getReporter() {
		return reporter;
	}

	public void setReporter(IStatusReporter reporter) {
		this.reporter = reporter;
	}

	protected LayoutManager layoutManager;
	protected IStatusReporter reporter;
	private RestService restService;
	private DriverProxyMulticastListener multicastHandler;
	private CmdRestMngHandler restMngCmdHandler;
	
	public void start() {
		DriverUtils.log(Level.INFO, TAG, "Starting...");
		int steps = 1;
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
			return;
		}
		
		try {
			multicastHandler.start();
			reportStartStatus(steps++, "Multicast message handler started");
		} catch (ListeningServerException e) {
			e.printStackTrace();
			String stack = DriverUtils.dumpExceptionToString(e);
			reportError(steps++, "Start multicast message handler fail.", stack);
			return;
		}
	}

	private void reportError(int i, String strMsg, String stack) {
		System.err.println("Step " + i + ": " + strMsg);
		System.err.println(stack);
		System.err.println();
	}

	private void initRestCommandHandlers() {
		initManagementCommandHandlers();
		initDeviceCommandHandlers();
	}

	/**
	 * 有以下几个管理命令需要被处理：
	 * 1. 取控制屏的Layout
	 * 2. 取控制屏的Layout中的所有设备的Profile
	 */
	private void initManagementCommandHandlers() {
		this.restMngCmdHandler = new CmdRestMngHandler();
		registerMngCmdHandler(new CmdGetLayout());
		registerMngCmdHandler(new CmdGetProfileByDevice());
		this.restService.registerCommandHandler(restMngCmdHandler);
	}

	private void registerMngCmdHandler(BaseManageCmd cmdHandler) {
		String command = cmdHandler.getCommand();
		cmdHandler.setDeviceConfigManager(getDeviceConfigManager());
		cmdHandler.setDeviceProfileManager(getDeviceProfileManager());
		cmdHandler.setLayoutManager(getLayoutManager());
		restMngCmdHandler.addHandler(command, cmdHandler);
	}

	private void initDeviceCommandHandlers() {
		// TODO Auto-generated method stub
		
	}

	private void reportStartStatus(int i, String strMsg) {
		System.out.println("Step " + i + ": " + strMsg);
	}


}
