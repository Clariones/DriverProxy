package org.skynet.bgby.driverproxy;

import java.net.SocketAddress;

import org.skynet.bgby.listeningserver.DirectBroadcastMessageService;
import org.skynet.bgby.protocol.UdpData;
import org.skynet.bgby.protocol.UdpMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DriverProxyMulticastListener extends DirectBroadcastMessageService {
	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public void sendMessage(UdpMessage message, SocketAddress address){
		UdpData data = this.getCodec().code(message);
		data.setSocketAddress(address);
		this.sendUdpMessage(data);
	}
}
