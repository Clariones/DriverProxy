package org.skynet.bgby.driverproxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.command.management.BaseManageCmd;
import org.skynet.bgby.command.management.CmdGetLayout;
import org.skynet.bgby.command.management.CmdGetProfileByDevice;
import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.devicedriver.DeviceDriver;
import org.skynet.bgby.devicedriver.DeviceDriverException;
import org.skynet.bgby.devicedriver.DriverManager;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.devicestandard.DeviceStandard;
import org.skynet.bgby.devicestandard.NormalHVAC;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.listeningserver.ListeningServerException;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.restserver.IRestRequestHandler;
import org.skynet.bgby.restserver.RestService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class DriverProxyService {
	protected static final Map<String, DeviceStandard> deviceStandards = new HashMap<>();

	static {
		deviceStandards.put(NormalHVAC.ID, new NormalHVAC());
	}

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

	protected static final String TAG = "DriverProxyService";
	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
	protected DriverProxyConfiguration config;
	protected DeviceConfigManager deviceConfigManager;
	protected DeviceProfileManager deviceProfileManager;
	protected DeviceStatusManager deviceStatusManager;
	protected DriverManager driverManager;

	protected LayoutManager layoutManager;

	protected DriverProxyMulticastListener multicastHandler;

	protected DPStatusReporter reporter;

	protected DriverProxyRestClient restClient;

	protected CmdRestMngHandler restMngCmdHandler;

	protected RestService restService;

	protected boolean started = false;

	protected int steps;
	private DeviceDriverCommandHandler deviceCommandHandler;

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
		return standard.isSupportCommand(command);
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
		DeviceConfigData devCfg = getDeviceConfigManager().getDeviceConfigData(devId);
		assert(devCfg != null);
		try {
			DeviceDriver driver = getDriverManager().lookupDriverForDevice(devId, devCfg.getProfile(),
					devCfg.getIdentity());
			DeviceStatus deviceStatus = getDeviceStatusManager().getDevice(devId);
			if (deviceStatus == null) {
				deviceStatus = new DeviceStatus();
				deviceStatus.setID(devId);
				deviceStatus.setProfile(devCfg.getProfile());
				deviceStatus.setIdentify(devCfg.getIdentity());
			}
			IRestResponse response = driver.onCommand(command, deviceStatus, restRequest.getParams());
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
		} catch (DeviceDriverException e) {
			e.printStackTrace();
			String msg = DriverUtils.dumpExceptionToString(e);
			DriverUtils.log(Level.SEVERE, TAG, msg);
			restResponse.setStatus(Status.INTERNAL_ERROR);
			restResponse.setAsString(msg);
			restResponse.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
			return;
		}

	}

	protected void initDeviceCommandHandlers() {
		this.deviceCommandHandler = new DeviceDriverCommandHandler();
		this.restService.registerCommandHandler(deviceCommandHandler);
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

	protected void initRestCommandHandlers() {
		initManagementCommandHandlers();
		initDeviceCommandHandlers();
	}

	protected void registerMngCmdHandler(BaseManageCmd cmdHandler) {
		String command = cmdHandler.getCommand();
		cmdHandler.setDeviceConfigManager(getDeviceConfigManager());
		cmdHandler.setDeviceProfileManager(getDeviceProfileManager());
		cmdHandler.setLayoutManager(getLayoutManager());
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
