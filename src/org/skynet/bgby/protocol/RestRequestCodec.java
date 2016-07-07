package org.skynet.bgby.protocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.ResponseException;

public class RestRequestCodec {

	protected static final String TAG = RestRequestCodec.class.getName();

	public IRestRequest getRequest(IHTTPSession session) {
		String uri = session.getUri();
		HashMap<String, String> body = new HashMap<String, String>();
		try {
			session.parseBody(body);
		} catch (IOException | ResponseException e) {
			e.printStackTrace();
			DriverUtils.log(Level.FINE, TAG, DriverUtils.dumpExceptionToString(e));
			return null;
		}

		DriverUtils.log(Level.FINE, TAG, "Recieve URI " + session.getRemoteIpAddress());
		if (uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		String[] segments = uri.split("/");
		if (segments.length < 2) {
			DriverUtils.log(Level.FINE, TAG, "This is not valid rest request");
			return null;
		}

		IRestRequest req = new RestRequestImpl();
		req.setMethod(session.getMethod());
		req.setRequestUri(uri);
		req.setRequestParameterString(session.getQueryParameterString());
		req.setCommand(segments[0]);
		req.setTarget(segments[1]);
		Map<String, String> params = new HashMap<String, String>();
		if (segments.length > 2) {
			for (int i = 2; i < segments.length; i += 2) {
				String key = segments[i];
				String value = "";
				if ((i + 1) < segments.length) {
					value = segments[i + 1];
				}
				params.put(key, value);
			}
		}

		Map<String, String> reqParams = session.getParms();
		if (reqParams != null && !reqParams.isEmpty()) {
			params.putAll(reqParams);
		}
		if (!body.isEmpty()) {
			for (String value : body.values()) {
				File file = new File(value);
				if (!(file.exists() && file.isFile() && file.canRead())) {
					continue;
				}
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				try {
					FileInputStream fin = new FileInputStream(file);
					byte[] buff = new byte[1024];
					while (fin.available() > 0) {
						int len = fin.read(buff);
						bout.write(buff, 0, len);
					}
					fin.close();
					bout.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.put("post_body", new String(bout.toByteArray(), Charset.forName("UTF-8")));
//				System.out.println(params.get("post_body"));
				DriverUtils.log(Level.FINE, TAG, "Post body length is " + bout.toByteArray().length);
				break;
			}
		}
		req.setParams(params);

		return req;
	}

}
