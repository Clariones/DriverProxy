package org.skynet.bgby.devicedriver.honeywell;

public class Hgw2000DriverConfig {
	protected String defaultUserName;
	protected String defaultPassword;
	protected int defaultPort;
	
	public int getDefaultPort() {
		return defaultPort;
	}
	public void setDefaultPort(int defaultPort) {
		this.defaultPort = defaultPort;
	}
	public String getDefaultUserName() {
		return defaultUserName;
	}
	public void setDefaultUserName(String defaultUserName) {
		this.defaultUserName = defaultUserName;
	}
	public String getDefaultPassword() {
		return defaultPassword;
	}
	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}
}
