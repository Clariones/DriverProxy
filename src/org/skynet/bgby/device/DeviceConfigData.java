package org.skynet.bgby.device;

import java.util.Map;

public class DeviceConfigData {
	protected String ID;
	protected String profile;
	protected Map<String, String> identity;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public Map<String, String> getIdentity() {
		return identity;
	}
	public void setIdentity(Map<String, String> identity) {
		this.identity = identity;
	}
}
