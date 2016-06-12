package org.skynet.bgby.listeningserver;

import org.skynet.bgby.listeningserver.MessageService.UdpMessageHandlingContext;

public interface IUdpMessageHandler {

	void handleMessage(UdpMessageHandlingContext context);

}
