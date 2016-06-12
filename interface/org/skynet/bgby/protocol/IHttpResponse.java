package org.skynet.bgby.protocol;

import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD.Response.IStatus;

public interface IHttpResponse {

	void setAsString(String strMsg);

	String getAsString();
	
	void setStatus(IStatus status);

	void setMimeType(String mimeType);

	void setInputStream(InputStream inputStream);

	IStatus getStatus();

	String getMimeType();

	InputStream getInputStream();

	long getDataLength();

}
