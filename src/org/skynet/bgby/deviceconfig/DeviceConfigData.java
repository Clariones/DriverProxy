package org.skynet.bgby.deviceconfig;

import java.util.Map;

public class DeviceConfigData {
	protected String ID;
	protected String profile;
	protected Map<String, Object> identity;
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
	public Map<String, Object> getIdentity() {
		return identity;
	}
	public void setIdentity(Map<String, Object> identity) {
		this.identity = identity;
	}
}
