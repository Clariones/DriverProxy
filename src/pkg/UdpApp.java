package pkg;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;

import org.skynet.bgby.listeningserver.UdpListenerService;
import org.skynet.bgby.protocol.UdpData;

public class UdpApp extends UdpListenerService {

	@Override
	public UdpData serve(UdpData message) {
		System.out.println("Got message from " + message.getSocketAddress() + ": " + new String(message.getData()));
		UdpData response = new UdpData();
		response.setSocketAddress(new InetSocketAddress(getListeningAddress(), getListeningPort()));
		response.setData(("I hear you! @" + new Date()).getBytes());
		return response;
	}

	@Override
	protected void createUdpSocket() throws IOException {
		listeningSocket = new DatagramSocket(getListeningPort());
		listeningSocket.setBroadcast(true);
		sendingSocket = new DatagramSocket();
//		InetAddress group = InetAddress.getByName(getListeningAddress());
//		 MulticastSocket msr = null;
//		 msr = new MulticastSocket(getListeningPort());
//		 msr.setLoopbackMode(false);
//		 msr.joinGroup(group);
//		 listeningSocket = msr;
	}

}
