package org.skynet.bgby.listeningserver;

import org.skynet.bgby.listeningserver.DirectBroadcastMessageService.UdpMessageHandlingContext;

public interface IUdpMessageHandler {

	void handleMessage(UdpMessageHandlingContext context);

}
