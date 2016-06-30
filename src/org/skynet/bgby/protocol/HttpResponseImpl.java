package org.skynet.bgby.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.skynet.bgby.driverutils.DriverUtils;

import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpResponseImpl implements IHttpResponse {
	protected static final InputStream EMPRTY_INPUT_STREAM = new ByteArrayInputStream(new byte[0]);
	protected static final String TYPE_BINARY = "binary";
	protected static final String TYPE_STRING = "string";

	protected String dataType = TYPE_STRING;

	protected InputStream inputStream;

	protected String mimeType = DriverUtils.MIME_TYPE_JSON;

	protected IStatus status = Status.OK;
	protected String strData;
	protected void closeInputStream() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}
	}
	@Override
	public long getDataLength() {
		if (dataType.equals(TYPE_STRING)) {
			if (strData == null) {
				return 0;
			}
			closeInputStream();
			return strData.getBytes().length;
		}

		if (dataType.equals(TYPE_BINARY)) {
			if (inputStream == null) {
				return 0;
			}
			try {
				return inputStream.available();
			} catch (IOException e) {
			}
		}
		return 0;
	}

	@Override
	public InputStream getInputStream() {
		if (dataType.equals(TYPE_STRING)) {
			if (strData == null) {
				strData = "";
			}
			closeInputStream();
			return new ByteArrayInputStream(strData.getBytes());
		}

		if (dataType.equals(TYPE_BINARY)) {
			if (inputStream == null) {
				return EMPRTY_INPUT_STREAM;
			}
			return inputStream;
		}
		return EMPRTY_INPUT_STREAM;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public IStatus getStatus() {
		return status;
	}

	@Override
	public void setInputStream(InputStream inputStream) {
		dataType = TYPE_BINARY;
		this.inputStream = inputStream;
	}

	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public void setStatus(IStatus status) {
		this.status = status;
	}

	@Override
	public void setAsString(String strMsg) {
		dataType = TYPE_STRING;
		strData = strMsg;
	}
	
	@Override
	public String getAsString() {
		InputStream is = getInputStream();
		byte[] buf = new byte[(int) getDataLength()];
		try {
			is.read(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return new String(buf, Charset.forName("UTF-8"));
	}

}
