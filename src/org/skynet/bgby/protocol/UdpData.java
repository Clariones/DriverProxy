package org.skynet.bgby.protocol;

import java.net.SocketAddress;

public class UdpData {
	protected SocketAddress socketAddress;
	protected byte[] data;
	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public void setFrom(SocketAddress socketAddress) {
		setSocketAddress(socketAddress);
	}
	

}
