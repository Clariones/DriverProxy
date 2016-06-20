package org.skynet.bgby.devicedriver.honeywell;

import java.util.Map;

public class Hgw2000DriverConfig {
	protected Map<String, Configuration> authConfig;
	protected Map<String, Integer> runningModes;
	protected Map<String, Integer> fanModes;
	
	public Map<String, Integer> getFanModes() {
		return fanModes;
	}

	public void setFanModes(Map<String, Integer> fanModes) {
		this.fanModes = fanModes;
	}

	public Map<String, Integer> getRunningModes() {
		return runningModes;
	}

	public void setRunningModes(Map<String, Integer> runningModes) {
		this.runningModes = runningModes;
	}

	public Map<String, Configuration> getAuthConfig() {
		return authConfig;
	}

	public void setAuthConfig(Map<String, Configuration> authConfig) {
		this.authConfig = authConfig;
	}
	
	public Configuration getConfigByIp(String ip){
		if (authConfig == null){
			return null;
		}
		return authConfig.get(ip);
	}
}
