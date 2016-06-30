package org.skynet.bgby.driverproxy;

public class DriverProxyConfiguration {
	protected String appId;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	protected int connectionTimeout;
	protected String multicastAddress;
	protected int multicastPort;
	protected int readTimeout;
	protected int restServicePort;
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public String getMulticastAddress() {
		return multicastAddress;
	}
	public int getMulticastPort() {
		return multicastPort;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public int getRestServicePort() {
		return restServicePort;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public void setMulticastAddress(String multicastAddress) {
		this.multicastAddress = multicastAddress;
	}
	public void setMulticastPort(int multicastPort) {
		this.multicastPort = multicastPort;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public void setRestServicePort(int restServicePort) {
		this.restServicePort = restServicePort;
	}
}
