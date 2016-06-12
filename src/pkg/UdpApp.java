package pkg;

import java.net.InetSocketAddress;
import java.util.Date;

import org.skynet.bgby.listeningserver.MulticastListenerService;
import org.skynet.bgby.protocol.UdpData;

public class UdpApp extends MulticastListenerService {

	@Override
	public UdpData serve(UdpData message) {
		System.out.println("Got message from " + message.getSocketAddress() + ": " + new String(message.getData()));
		UdpData response = new UdpData();
		response.setSocketAddress(new InetSocketAddress(getListeningAddress(), getListeningPort()));
		response.setData(("I hear you! @" + new Date()).getBytes());
		return response;
	}

}
