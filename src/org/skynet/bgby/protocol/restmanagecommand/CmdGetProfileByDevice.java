package org.skynet.bgby.protocol.restmanagecommand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.skynet.bgby.device.DeviceConfigData;
import org.skynet.bgby.device.DeviceProfile;
import org.skynet.bgby.driverproxy.DeviceManager;
import org.skynet.bgby.driverproxy.DeviceProfileManager;
import org.skynet.bgby.driverproxy.LayoutConfigManager;
import org.skynet.bgby.layout.ILayout;
import org.skynet.bgby.layout.ILayoutGroup;
import org.skynet.bgby.layout.LayoutUtils;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestCommandHandler;

public class CmdGetProfileByDevice implements IRestCommandHandler{
	public static final String CMD = "getDeviceProfiles";
	private static final int ERROR_CODE_BASE = 10000;
	protected DeviceProfileManager profileManager;
	protected LayoutConfigManager layoutManager;
	protected DeviceManager deviceManager;
	
	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	public DeviceProfileManager getProfileManager() {
		return profileManager;
	}

	public void setProfileManager(DeviceProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public LayoutConfigManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(LayoutConfigManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	@Override
	public IRestResponse handleCommand(IRestRequest restRequest) {
		String controllerID = restRequest.getTarget();
		List<ILayout> tgtLayout = getLayoutManager().getControllerLayout(controllerID);
		if (tgtLayout == null || tgtLayout.isEmpty()){
			return newErrorResponse(1, "Layout not existed for device " + controllerID);
		}
		Set<String> deviceIds = new HashSet<>();
		// Step1, find out all devices with their ID
		findAllDeviceIds(tgtLayout, deviceIds);
		if (deviceIds.isEmpty()){
			return newErrorResponse(2, "Didnt found any device from layout for " + controllerID);
		}
		// step2, list all their profile names
		Map<String, String> deviceProfileNames = new HashMap<>();
		Map<String, DeviceProfile> profiles = new HashMap<>();
		for(String devId: deviceIds){
			DeviceConfigData cfgData = getDeviceManager().getDeviceConfigData(devId);
			String profileID = cfgData.getProfile();
			deviceProfileNames.put(devId, profileID);
			if (profiles.containsKey(profileID)){
				continue;
			}
			profiles.put(profileID, getProfileManager().getProfile(profileID));
		}
		
		DeviceProfilesRestResult result = new DeviceProfilesRestResult();
		result.setDevices(deviceProfileNames);
		result.setProfiles(profiles);
		RestResponseImpl response = new RestResponseImpl();
		response.setData(LayoutUtils.gson.toJson(result));
		return response;
	}

	private IRestResponse newErrorResponse(int i, String string) {
		RestResponseImpl response = new RestResponseImpl();
		response.setErrorCode(ERROR_CODE_BASE + i);
		response.setResult(string);
		return response;
	}

	private void findAllDeviceIds(List<ILayout> tgtLayout, Set<String> deviceIds) {
		for(ILayout layout: tgtLayout){
			findDevicesInOneLayout(layout, deviceIds);
		}
	}

	private void findDevicesInOneLayout(ILayout layout, Set<String> deviceIds) {
		Map<String, Object> params = layout.getParams();
		if (params != null){
			String theId = (String) params.get(LayoutUtils.PARAM_DEVICE_ID);
			if (theId != null){
				deviceIds.add(theId);
			}
		}
		
		if (!(layout instanceof ILayoutGroup)){
			return;
		}
		ILayoutGroup group = (ILayoutGroup) layout;
		List<ILayout> children = group.getLayoutContent();
		if (children == null){
			return;
		}
		for(ILayout child : children){
			findDevicesInOneLayout(child, deviceIds);
		}
	}

	public static class DeviceProfilesRestResult {
		protected Map<String, DeviceProfile> profiles;
		protected Map<String, String> devices;
		public Map<String, DeviceProfile> getProfiles() {
			return profiles;
		}
		public void setProfiles(Map<String, DeviceProfile> profiles) {
			this.profiles = profiles;
		}
		public Map<String, String> getDevices() {
			return devices;
		}
		public void setDevices(Map<String, String> devices) {
			this.devices = devices;
		}
	}
}
