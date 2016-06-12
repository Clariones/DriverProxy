package org.skynet.bgby.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class RestRequestCodec {

	private static final String TAG = RestRequestCodec.class.getName();

	public IRestRequest getRequest(IHTTPSession session) {
		String uri = session.getUri();
		
		DriverUtils.log(Level.FINE, TAG, "Recieve URI " + session.getRemoteIpAddress());
		if (uri.startsWith("/")){
			uri = uri.substring(1);
		}
		String[] segments = uri.split("/");
		if (segments.length < 2){
			DriverUtils.log(Level.FINE, TAG, "This is not valid rest request");
			return null;
		}
		
		IRestRequest req = new RestRequestImpl();
		req.setRequestUri(uri);
		req.setRequestParameterString(session.getQueryParameterString());
		req.setCommand(segments[0]);
		req.setTarget(segments[1]);
		Map<String, String> params = new HashMap<String, String>();
		if (segments.length > 2){
			for(int i = 2; i< segments.length; i+=2){
				String key = segments[i];
				String value = "";
				if ((i+1) < segments.length){
					value = segments[i+1];
				}
				params.put(key, value);
			}
		}
		
		Map<String, String> reqParams = session.getParms();
		if (reqParams != null && !reqParams.isEmpty()){
			params.putAll(reqParams);
		}
		req.setParams(params);
		return req;
	}

}
