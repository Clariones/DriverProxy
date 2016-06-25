package org.skynet.bgby.driverproxy;

import java.util.Map;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.devicestatus.DeviceStatus;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;

public class ExecutionContext {

	protected String command;
	protected Map<String, String> cmdParams;
	protected DeviceStatus device;
	protected DeviceProfile profile;
	protected DeviceConfigData config;
	protected IRestRequest request;
	protected IRestResponse response;
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Map<String, String> getCmdParams() {
		return cmdParams;
	}
	public void setCmdParams(Map<String, String> cmdParams) {
		this.cmdParams = cmdParams;
	}
	public DeviceStatus getDevice() {
		return device;
	}
	public void setDevice(DeviceStatus device) {
		this.device = device;
	}
	public DeviceProfile getProfile() {
		return profile;
	}
	public void setProfile(DeviceProfile profile) {
		this.profile = profile;
	}
	public DeviceConfigData getConfig() {
		return config;
	}
	public void setConfig(DeviceConfigData config) {
		this.config = config;
	}
	public IRestRequest getRequest() {
		return request;
	}
	public void setRequest(IRestRequest request) {
		this.request = request;
	}
	public IRestResponse getResponse() {
		return response;
	}
	public void setResponse(IRestResponse response) {
		this.response = response;
	}
	
	
}
