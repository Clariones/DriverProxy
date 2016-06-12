package org.skynet.bgby.protocol;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;

public class UdpMessageCodec {
	private static final String LINE_SEPARATOR = "\r\n";
	protected static final Charset utf8 = Charset.forName("UTF-8");
	private static final String TAG = UdpMessageCodec.class.getName();
	
	public UdpMessage decode(UdpData udpData) {
		byte[] data = udpData.getData();
		if (data == null || data.length == 0){
			return null;
		}
		String strData = new String(data,utf8);
		if (strData == null || strData.isEmpty()){
			return null;
		}
		String[] lines = strData.split("[\r\n]+");
		Map<String, String> inputs = new HashMap<String, String>();
		for(String line: lines){
			if (line.isEmpty()){
				continue;
			}
			int pos = line.indexOf('=');
			if (pos <= 0){
				continue;
			}
			String key = line.substring(0, pos).trim();
			String value = line.substring(pos+1);
			String oldValue = inputs.put(key, value);
			if (oldValue != null){
				DriverUtils.log(Level.WARNING, TAG, "{0}={1} was overwrite with {2}", 
						new String[]{key, oldValue, value});
			}
		}
		
		UdpMessage rstMessage = new UdpMessage();
		rstMessage.setFromAddress(udpData.getSocketAddress());
		rstMessage.setCommand(pickByKey(UdpMessage.FIELD_COMMAND, inputs));
		rstMessage.setFromApp(pickByKey(UdpMessage.FIELD_FROM_APP, inputs));
		rstMessage.setFromDevice(pickByKey(UdpMessage.FIELD_DEVICE, inputs));
		String toList = pickByKey(UdpMessage.FIELD_TO, inputs);
		rstMessage.setParams(inputs);
		if (toList==null || toList.isEmpty() || toList.trim().equals(UdpMessage.VALUE_ALL)){
			rstMessage.setReceivers(null);
		}else{
			String[] recievers = toList.split(",");
			List<String> recList = new ArrayList<String>();
			for(String rev: recievers){
				String name = rev.trim();
				if (name.isEmpty()){
					continue;
				}
				if (name.equals(UdpMessage.VALUE_ALL)){
					continue;
				}
				recList.add(name);
			}
			rstMessage.setReceivers(recList);
		}
		return rstMessage;
	}

	private String pickByKey(String fieldName, Map<String, String> inputs) {
		return inputs.remove(fieldName);
	}

	public UdpData code(UdpMessage responseMessage) {
		if (responseMessage == null || responseMessage.getClass() == null){
			DriverUtils.log(Level.SEVERE, TAG, "Message is empty or without command code");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		appendField(sb, UdpMessage.FIELD_COMMAND, responseMessage.getCommand());
		appendField(sb, UdpMessage.FIELD_FROM_APP, responseMessage.getCommand());
		appendField(sb, UdpMessage.FIELD_DEVICE, responseMessage.getCommand());
		appendToList(sb, responseMessage);
		appendParams(sb, responseMessage);
		return null;
	}

	private void appendParams(StringBuilder sb, UdpMessage responseMessage) {
		Map<String, String> params = responseMessage.getParams();
		if (params == null || params.isEmpty()){
			return;
		}
		
		Iterator<Entry<String, String>> it = params.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String> kv = it.next();
			sb.append(kv.getKey()).append('=').append(kv.getValue()).append(LINE_SEPARATOR);
		}
	}

	private void appendToList(StringBuilder sb, UdpMessage responseMessage) {
		List<String> list = responseMessage.getReceivers();
		if (list == null || list.isEmpty()){
			return;
		}
		Set<String> recivers = new HashSet<String>(list);
		recivers.remove(UdpMessage.VALUE_ALL);
		if (recivers.isEmpty()){
			return;
		}
		sb.append(UdpMessage.FIELD_TO).append('=');
		boolean isFirst = true;
		for (String rev : recivers){
			if (isFirst){
				isFirst = false;
			}else{
				sb.append(',');
			}
			sb.append(rev);
		}
		sb.append(LINE_SEPARATOR);
	}

	private void appendField(StringBuilder sb, String fieldName, String fieldValue) {
		if (fieldValue == null || fieldValue.isEmpty()){
			return;
		}
		sb.append(fieldName).append('=').append(fieldValue).append(LINE_SEPARATOR); // Note: I don't trim the value
		return;
	}

	
	
}
