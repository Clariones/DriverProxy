package org.skynet.bgby.driverproxy;

import org.skynet.bgby.listeningserver.MessageService;
import org.skynet.bgby.protocol.UdpData;
import org.skynet.bgby.protocol.UdpMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DriverProxyMulticastListener extends MessageService {
	protected Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public void sendMessage(UdpMessage message){
		UdpData data = this.getCodec().code(message);
		this.sendToMulticastSocket(data);
	}
}
