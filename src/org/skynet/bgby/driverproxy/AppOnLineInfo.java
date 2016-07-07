package org.skynet.bgby.driverproxy;

import java.net.SocketAddress;

public class AppOnLineInfo {
	protected String ID;
	protected long lastActiveTime;
	protected SocketAddress udpAddress;
	protected String appType;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public long getLastActiveTime() {
		return lastActiveTime;
	}
	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}
	public SocketAddress getUdpAddress() {
		return udpAddress;
	}
	public void setUdpAddress(SocketAddress udpAddress) {
		this.udpAddress = udpAddress;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	
	
}
