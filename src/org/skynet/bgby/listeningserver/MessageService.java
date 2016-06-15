package org.skynet.bgby.listeningserver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.UdpData;
import org.skynet.bgby.protocol.UdpMessage;
import org.skynet.bgby.protocol.UdpMessageCodec;

public class MessageService extends MulticastListenerService {
	protected UdpMessageCodec codec;
	protected List<IUdpMessageHandler> handlers;
	
	public void registerHandler(IUdpMessageHandler handler){
		if (handlers==null){
			handlers = new ArrayList<IUdpMessageHandler>();
		}
		if (handlers.contains(handler)){
			return;
		}
		handlers.add(handler);
	}

	public UdpMessageCodec getCodec() {
		return codec;
	}


	public void setCodec(UdpMessageCodec codec) {
		this.codec = codec;
	}


	@Override
	public UdpData serve(UdpData udpData) {
		if (codec == null){
			DriverUtils.log(Level.SEVERE, TAG, "UdpMessageCodec not initialed");
			return null;
		}
		if (handlers == null || handlers.isEmpty()){
			DriverUtils.log(Level.WARNING, TAG, "No any UDP message handlers registered");
			return null;
		}
		
		UdpMessage message = codec.decode(udpData);
		if (message == null){
			DriverUtils.log(Level.WARNING, TAG, "Invalid UDP data dropped");
			return null;
		}
		
		UdpMessageHandlingContext context = new UdpMessageHandlingContext();
		context.setInputMessage(message);
		Iterator<IUdpMessageHandler> it = handlers.iterator();
		while(it.hasNext()){
			IUdpMessageHandler handler = it.next();
			handler.handleMessage(context);
			if (!context.isServed()){
				continue;
			}
			if (context.getResponseMessage() != null){
				return codec.code(context.getResponseMessage());
			}
			break;
		}
		
		return null;
	}
	
	public static class UdpMessageHandlingContext {
		protected boolean served= false;
		protected UdpMessage inputMessage;
		protected UdpMessage responseMessage;
		public boolean isServed() {
			return served;
		}
		public void setServed(boolean served) {
			this.served = served;
		}
		public UdpMessage getInputMessage() {
			return inputMessage;
		}
		public void setInputMessage(UdpMessage inputMessage) {
			this.inputMessage = inputMessage;
		}
		public UdpMessage getResponseMessage() {
			return responseMessage;
		}
		public void setResponseMessage(UdpMessage responseMessage) {
			this.responseMessage = responseMessage;
		}
		
	}

}
