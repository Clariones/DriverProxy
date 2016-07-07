package org.skynet.bgby.driverproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.skynet.bgby.command.management.BaseManageCmd;
import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.command.management.CmdGetProxyData;
import org.skynet.bgby.command.management.CmdSetProxyData;
import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.devicedriver.DeviceDriver;
import org.skynet.bgby.devicedriver.DriverManager;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestandard.DeviceStandard;
import org.skynet.bgby.devicestandard.NormalFloorHeating;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestandard.SimpleDimmer;
import org.skynet.bgby.devicestandard.SimpleLight;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.listeningserver.DirectBroadcastMessageService.UdpMessageHandlingContext;
import org.skynet.bgby.listeningserver.IUdpMessageHandler;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.protocol.UdpMessage;
import org.skynet.bgby.protocol.UdpMessageCodec;
import org.skynet.bgby.restserver.IRestRequestHandler;
import org.skynet.bgby.restserver.RestService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class DriverProxyService {
	class DeviceDriverCommandHandler implements IRestRequestHandler {

		@Override
		public boolean handleCommand(IRestRequest restRequest, IHttpResponse restResponse) {
			if (canHandleRequest(restRequest)) {
				handleDeviceCommand(restRequest, restResponse);
				return true;
			}
			return false;
		}

	}

	class ModuleStartingReporter implements DPModuleStatusReporter {

		@Override
		public void reportError(int id, String title, String detail) {
			DriverProxyService.this.reportError(steps, id + "." + title, detail);
		}

		@Override
		public void reportStatus(int id, String title, String detail) {
			DriverProxyService.this.reportStartStatus(steps, id + "." + title, detail);
		}
	}

	class MulticastMsgHandler implements IUdpMessageHandler{

		@Override
		public void handleMessage(UdpMessageHandlingContext context) {
			context.setServed(true); // All messages for driver proxy be served here.
			UdpMessage responseUdpMessage = handleUdpMessage(context.getInputMessage());
			context.setResponseMessage(responseUdpMessage);
		}
		
	}

	public static final Map<String, DeviceStandard> deviceStandards = new HashMap<>();

	protected static final String TAG = "DriverProxyService";

	private static final long HEART_BEAT_DEAD_TIME = 35*1000;
	static {
		deviceStandards.put(NormalHVAC.ID, new NormalHVAC());
		deviceStandards.put(SimpleLight.ID, new SimpleLight());
		deviceStandards.put(SimpleDimmer.ID, new SimpleDimmer());
		deviceStandards.put(NormalFloorHeating.ID, new NormalFloorHeating());
	}
	protected DriverProxyConfiguration config;
	protected DeviceDriverCommandHandler deviceCommandHandler;
	protected DeviceConfigManager deviceConfigManager;
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceStatusManager deviceStatusManager;
	protected Map<String, AppOnLineInfo> appInfos;
	
	protected DriverManager driverManager;

	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

	protected LayoutManager layoutManager;

	protected DriverProxyMulticastListener multicastHandler;

	protected DPStatusReporter reporter;

	protected DriverProxyRestClient restClient;

	protected CmdRestMngHandler restMngCmdHandler;

	protected RestService restService;
	protected boolean started = false;

	protected int steps;

	public boolean canHandleRequest(IRestRequest restRequest) {
		if (!started) {
			return false;
		}
		String devId = restRequest.getTarget();
		String command = restRequest.getCommand();
		DeviceConfigData devCfg = getDeviceConfigManager().getDeviceConfigData(devId);
		if (devCfg == null) {
			return false;
		}
		DeviceProfile devProfile = getDeviceProfileManager().getProfile(devCfg.getProfile());
		if (devProfile == null) {
			return false;
		}
		DeviceStandard standard = deviceStandards.get(devProfile.getStandard());
		if (standard == null) {
			return false;
		}
//		return standard.isSupportCommand(command);
		return true;
	}

	private UdpMessage createDeviceStatusMsgData(DeviceStatus device) {
		UdpMessage message = new UdpMessage();
		message.setCommand(UdpMessage.CMD_DEVICE_STATUS_REPORT);
		message.setFromApp(this.getConfig().getAppId());
		message.setFromDevice(device.getID());
		Map<String, String> params = new HashMap<>();
		Iterator<Entry<String, Object>> it = device.getStatus().entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Object> ent = it.next();
			String key = UdpMessage.FIELD_DEVICE_STATUES_PREFIX+ent.getKey();
			String value = String.valueOf(ent.getValue());
			params.put(key, value);
		}
		message.setParams(params);
		return message;
	}

	public DriverProxyConfiguration getConfig() {
		return config;
	}

	public DeviceConfigManager getDeviceConfigManager() {
		return deviceConfigManager;
	}

	public DeviceProfileManager getDeviceProfileManager() {
		return deviceProfileManager;
	}

	public DeviceStatusManager getDeviceStatusManager() {
		return deviceStatusManager;
	}

	public DriverManager getDriverManager() {
		return driverManager;
	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public DPStatusReporter getReporter() {
		return reporter;
	}

	public void handleDeviceCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		if (!started) {
			return;
		}
		String command = restRequest.getCommand();
		String devId = restRequest.getTarget();
		try {
			// first, prepare the device status, profile and config
			DeviceConfigData devCfg = getDeviceConfigManager().getDeviceConfigData(devId);
			assert(devCfg != null);
			DeviceStatus deviceStatus = getDeviceStatusManager().getDevice(devId);
			if (deviceStatus == null) {
				deviceStatus = new DeviceStatus();
				deviceStatus.setID(devId);
			}
			deviceStatus.setProfile(devCfg.getProfile());
			// second, find correct dirver for this device
			DeviceDriver driver = getDriverManager().lookupDriverForDevice(devId, deviceStatus, devCfg);
			if (driver == null){
				throw new Exception("Cannot find any driver for " + devId);
			}
			DeviceProfile profile = getDeviceProfileManager().getProfile(devCfg.getProfile());
			// then we can handle this command by that driver
			ExecutionContext ctx = new ExecutionContext();
			ctx.setCommand(command);
			ctx.setCmdParams(restRequest.getParams());
			ctx.setDevice(deviceStatus);
			ctx.setProfile(profile);
			ctx.setConfig(devCfg);
			
			IRestResponse response = driver.onCommand(ctx);
			// and return result finally
			if (response.getRequest() == null){
				response.setRequest(DriverUtils.getRequestFullUri(restRequest));
			}
			getDeviceStatusManager().updateDevice(deviceStatus);
			restResponse.setAsString(gson.toJson(response));
			restResponse.setMimeType(DriverUtils.MIME_TYPE_JSON);
		} catch (DPModuleException e) {
			// Should not be possible of 'cannot found driver'. That must be an
			// deployment mistake.
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			restResponse.setStatus(Status.INTERNAL_ERROR);
			restResponse.setAsString(msg);
			restResponse.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			restResponse.setStatus(Status.INTERNAL_ERROR);
			restResponse.setAsString(msg);
			restResponse.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
			return;
		}

	}

	public UdpMessage handleUdpMessage(UdpMessage inputMessage) {
		if (getConfig().getAppId().equals(inputMessage.getFromApp())){
			DriverUtils.log(Level.FINE, TAG, "Receive self message, ignore");
			return null;
		}
		String command = inputMessage.getCommand();
		switch (command){
		case UdpMessage.CMD_HEART_BEAT:
			updateAppOnlineInfo(inputMessage);
			return null;
		case UdpMessage.CMD_DEVICE_STATUS_REPORT:
			updateDeviceStatusReport(inputMessage);
			return null;
		}
		return null;
	}

	private void updateDeviceStatusReport(UdpMessage inputMessage) {
		String appId = inputMessage.getFromDevice();
		DeviceStatus device = getDeviceStatusManager().getDevice(appId);
		if (device == null){
			return;
		}
		Iterator<Entry<String, String>> it = inputMessage.getParams().entrySet().iterator();
		Map<String, String> params = new HashMap<>();
		while(it.hasNext()){
			Entry<String, String> ent = it.next();
			String key = ent.getKey();
			if (key.startsWith(UdpMessage.FIELD_DEVICE_STATUES_PREFIX)){
				String statusKey = key.substring(UdpMessage.FIELD_DEVICE_STATUES_PREFIX.length());
				params.put(statusKey, ent.getValue());
			}
		}
//		device.getStatus()
	}

	private void updateAppOnlineInfo(UdpMessage inputMessage) {
		String appId = inputMessage.getFromDevice();
		String appType = inputMessage.getFromApp();
		SocketAddress address = inputMessage.getFromAddress();
		
		AppOnLineInfo info = appInfos.get(appId);
		if (info == null){
			info = new AppOnLineInfo();
			info.setID(appId);
			appInfos.put(appId, info);
		}
		info.setAppType(appType);
		info.setLastActiveTime(System.currentTimeMillis());
		info.setUdpAddress(address);
	}

	protected void initDeviceCommandHandlers() {
		this.deviceCommandHandler = new DeviceDriverCommandHandler();
		this.restService.registerCommandHandler(deviceCommandHandler);
	}

	/**
	 * 有以下几个管理命令需要被处理： 1. 取控制屏的Layout 2. 取控制屏的Layout中的所有设备的Profile
	 */
	protected void initManagementCommandHandlers() {
		restMngCmdHandler = new CmdRestMngHandler();
		registerMngCmdHandler(new CmdGetLayout());
		registerMngCmdHandler(new CmdGetProfileByDevice());
		registerMngCmdHandler(new CmdGetProxyData());
		registerMngCmdHandler(new CmdSetProxyData());
		restService.registerCommandHandler(restMngCmdHandler);
	}

	protected void initRestCommandHandlers() {
		initManagementCommandHandlers();
		initDeviceCommandHandlers();
	}

	public void multicastDeviceStatus(DeviceStatus deviceStatus) {
		UdpMessage data = createDeviceStatusMsgData(deviceStatus);
		Iterator<Entry<String, AppOnLineInfo>> it = appInfos.entrySet().iterator();
		long cutTs = System.currentTimeMillis();
		while(it.hasNext()){
			Entry<String, AppOnLineInfo> ent = it.next();
			AppOnLineInfo info = ent.getValue();
			if (cutTs - info.getLastActiveTime() > HEART_BEAT_DEAD_TIME){
				it.remove();
				continue;
			}
			
			this.multicastHandler.sendMessage(data, info.getUdpAddress());
		}
		this.multicastHandler.sendMessage(data, new InetSocketAddress(getConfig().getMulticastAddress(), getConfig().getMulticastPort()));
	}

	protected void registerMngCmdHandler(BaseManageCmd cmdHandler) {
		String command = cmdHandler.getCommand();
		cmdHandler.setDeviceConfigManager(getDeviceConfigManager());
		cmdHandler.setDeviceProfileManager(getDeviceProfileManager());
		cmdHandler.setLayoutManager(getLayoutManager());
		cmdHandler.setDeviceStatusManager(getDeviceStatusManager());
		restMngCmdHandler.addHandler(command, cmdHandler);
	}

	protected void reportError(int i, String strTitle, String strMessage) {
		if (getReporter() != null) {
			getReporter().reportError(i, strTitle, strMessage);
		} else {
			DriverUtils.log(Level.SEVERE, TAG, strTitle + "\r\n" + strMessage);
		}
	}

	protected void reportStartStatus(int i, String string) {
		reportStartStatus(i, string, null);
	}

	protected void reportStartStatus(int i, String strMsg, String detail) {
		if (getReporter() != null) {
			getReporter().reportStatus(i, strMsg, detail);
		} else {
			DriverUtils.log(Level.INFO, TAG, strMsg);
		}
	}

	public void setConfig(DriverProxyConfiguration config) {
		this.config = config;
	}

	public void setDeviceConfigManager(DeviceConfigManager deviceConfigManager) {
		this.deviceConfigManager = deviceConfigManager;
	}

	public void setDeviceProfileManager(DeviceProfileManager deviceProfileManager) {
		this.deviceProfileManager = deviceProfileManager;
	}

	public void setDeviceStatusManager(DeviceStatusManager deviceStatusManager) {
		this.deviceStatusManager = deviceStatusManager;
	}

	public void setDriverManager(DriverManager driverManager) {
		this.driverManager = driverManager;
	}

	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	public void setReporter(DPStatusReporter reporter) {
		this.reporter = reporter;
	}
	
	public void start() {
		DriverUtils.log(Level.INFO, TAG, "Starting...");
		appInfos = new HashMap<>();
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
		multicastHandler.setCodec(new UdpMessageCodec());
		multicastHandler.setDamon(false);
		multicastHandler.registerHandler(new MulticastMsgHandler());
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
			deviceProfileManager.setDriverProxy(this);
			deviceProfileManager.start();
			reportStartStatus(steps++, "Device profile manager started");

			deviceStatusManager.setStartingReporter(mRept);
			deviceStatusManager.setDriverProxy(this);
			deviceStatusManager.start();
			reportStartStatus(steps++, "Driver status manager started");

			deviceConfigManager.setStartingReporter(mRept);
			deviceConfigManager.setDriverProxy(this);
			deviceConfigManager.start();
			reportStartStatus(steps++, "Device config manager started");

			driverManager.setStartingReporter(mRept);
			driverManager.setDeviceStatusManager(deviceStatusManager);
			driverManager.setDeviceProfileManager(deviceProfileManager);
			driverManager.setDeviceConfigManager(deviceConfigManager);
			driverManager.setDriverProxy(this);
			driverManager.start();
			reportStartStatus(steps++, "Driver manager started");

			layoutManager.setStartingReporter(mRept);
			layoutManager.setDriverProxy(this);
			layoutManager.start();
			reportStartStatus(steps++, "Layout manager started");

			started = true;
		} catch (DPModuleException e) {
			e.printStackTrace();
			String stack = DriverUtils.dumpExceptionToString(e);
			reportError(steps++, "Start modules fail.", stack);
			stop();
			return;
		}

	}

	protected void stop() {
		started = false;
		layoutManager.stop();
		driverManager.stop();
		deviceConfigManager.stop();
		deviceStatusManager.stop();
		deviceProfileManager.stop();
		if (this.multicastHandler.isAlive()) {
			multicastHandler.stop();
		}
		if (this.restService.isAlive()) {
			restService.stop();
		}
	}
}
