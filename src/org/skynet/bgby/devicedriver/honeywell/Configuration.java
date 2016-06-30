package org.skynet.bgby.devicedriver.honeywell;

public class Configuration {

	protected String username="admin";
	protected String password="123456";
	protected String hostIPAddress="192.168.0.40";
	protected int port=10099;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHostIPAddress() {
		return hostIPAddress;
	}
	public void setHostIPAddress(String hostIPAddress) {
		this.hostIPAddress = hostIPAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	
	
	
	
	
	
}
