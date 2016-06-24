package org.skynet.bgby.restserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.HttpResponseImpl;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public abstract class RestClientBaseImpl implements IRestClient {
	public class Context implements IRestClientContext {
		protected String contentRoot;
		protected IRestRequest request;
		protected IHttpResponse response;
		protected InetSocketAddress serverAddress;

		@Override
		public String getContentRoot() {
			return contentRoot;
		}

		@Override
		public IRestRequest getRequest() {
			return request;
		}

		@Override
		public IHttpResponse getResponse() {
			return response;
		}

		@Override
		public InetSocketAddress getServerAddress() {
			return serverAddress;
		}

		@Override
		public void setContentRoot(String contentRoot) {
			this.contentRoot = contentRoot;
		}

		@Override
		public void setRequest(IRestRequest request) {
			this.request = request;
		}

		@Override
		public void setResponse(IHttpResponse response) {
			this.response = response;
		}

		@Override
		public void setServerAddress(InetSocketAddress serverAddress) {
			this.serverAddress = serverAddress;
		}

	}

	protected static final int DEFAULT_CONN_TIMEOUT = 30*1000;
	protected static final int DEFAULT_READ_TIMEOUT = 30*1000;
	protected static final String TAG = RestClientBaseImpl.class.getName();
	protected int connectionTimeout = DEFAULT_CONN_TIMEOUT;
	protected String encoding = "UTF-8";
	protected int readTimeout = DEFAULT_READ_TIMEOUT;
	protected ExecutorService threadPool;

	@Override
	public void asynchRequest(final InetSocketAddress serverAddress, final String contentRoot,
			final IRestRequest request, final IRestClientCallback callback) {
		getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				Context ctx = new Context();
				ctx.setContentRoot(contentRoot);
				ctx.setRequest(request);
				ctx.setServerAddress(serverAddress);
				IHttpResponse response = null;
				try {
					response = synchRequest(serverAddress, contentRoot, request);
					ctx.setResponse(response);
				} catch (ConnectException e) {
					e.printStackTrace();
					String msg = DriverUtils.dumpExceptionToString(e);
					response = new HttpResponseImpl();
					response.setStatus(Status.CLIENT_CONNECTION_REFUSED);
					response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
					response.setAsString(msg);
					ctx.setResponse(response);
				} catch (IOException e) {
					e.printStackTrace();
					String msg = DriverUtils.dumpExceptionToString(e);
					response = new HttpResponseImpl();
					response.setStatus(Status.CLIENT_INTERNAL_ERROR);
					response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
					response.setAsString(msg);
					ctx.setResponse(response);
				}
				callback.onRestResponse(ctx, response);
			}

		});

	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public String getEncoding() {
		return encoding;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	protected ExecutorService getThreadPool() {
		if (threadPool == null) {
			threadPool = Executors.newFixedThreadPool(10);
		}
		return threadPool;
	}


	protected String makeUrl(InetSocketAddress serverAddress, String contentRoot, IRestRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(serverAddress.getHostName());
		if (serverAddress.getPort() != 80) {
			sb.append(':').append(serverAddress.getPort());
		}
		if (contentRoot != null && !contentRoot.trim().isEmpty()) {
			contentRoot = contentRoot.replaceAll("^(\\/)?(.*?)(\\/)?$", "$2");
			sb.append('/').append(urlEncode(contentRoot));
		}
		sb.append('/').append(urlEncode(request.getCommand())).append('/').append(request.getTarget());
		Map<String, String> params = request.getParams();
		if (params == null || params.isEmpty()) {
			return sb.toString();
		}
		TreeMap<String, String> sortedParams = new TreeMap<String, String>(params);

		Iterator<Entry<String, String>> it = sortedParams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> kv = it.next();
			sb.append('/').append(urlEncode(kv.getKey())).append('/').append(kv.getValue());
		}
		return sb.toString();
	}

	protected void readFromResponse(HttpURLConnection conn, HttpResponseImpl response, InputStream es) {
		int respCode = 500;
		byte[] buf = new byte[1024];
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		try {
			respCode = conn.getResponseCode();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		IStatus status = Status.lookup(respCode);
		if (status == null) {
			status = Status.INTERNAL_ERROR;
		}
		response.setStatus(status);

		int ret = 0;
		// read the response body
		try {
			while ((ret = es.read(buf)) > 0) {
				bout.write(buf, 0, ret);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			assert(false); // should not be here
		}
		response.setInputStream(new ByteArrayInputStream(bout.toByteArray()));
		response.setMimeType(conn.getHeaderField("Content-Type"));
		// close the errorstream
		try {
			es.close();
			bout.close();
		} catch (IOException e1) {
		}
	}

	protected IHttpResponse sendRequestAndWaitResponse(HttpURLConnection conn) {
		HttpResponseImpl response = new HttpResponseImpl();

		try {
			InputStream inputStream = conn.getInputStream();
			readFromResponse(conn, response, inputStream);
		} catch (ConnectException e) {
			response.setStatus(Status.CLIENT_CONNECTION_REFUSED);
			response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
			String msg = DriverUtils.dumpExceptionToString(e);
			response.setAsString(msg);
		} catch (SocketTimeoutException e){
			response.setStatus(Status.CLIENT_REQUEST_TIMEOUT);
			response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
			String msg = DriverUtils.dumpExceptionToString(e);
			response.setAsString(msg);
		} catch (IOException e) {
			InputStream es = conn.getErrorStream();
			if (es != null){
				readFromResponse(conn, response, es);
			}else{
				e.printStackTrace();
				response.setStatus(Status.CLIENT_INTERNAL_ERROR);
				response.setMimeType(NanoHTTPD.MIME_PLAINTEXT);
				String msg = DriverUtils.dumpExceptionToString(e);
				response.setAsString(msg);
			}
		}
		return response;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	@Override
	public IHttpResponse synchRequest(InetSocketAddress serverAddress, String contentRoot, IRestRequest request)
			throws IOException {
		if (request == null) {
			DriverUtils.log(Level.SEVERE, TAG, "No request for restful http client");
			throw new IOException("Cannot got request data");
		}
		String url = makeUrl(serverAddress, contentRoot, request);
		URL requestUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
		conn.setDoInput(true); // ����������������������
		conn.setDoOutput(false); // ��������������������ϴ�
		conn.setUseCaches(false); // ��ʹ�û���
		conn.setRequestMethod("GET"); // ʹ��get����
		conn.setConnectTimeout(getConnectionTimeout());
		conn.setReadTimeout(getReadTimeout());
		IHttpResponse response = sendRequestAndWaitResponse(conn);
		return response;
	}

	protected String urlEncode(String contentRoot) {
		// TODO Auto-generated method stub
		try {
			return URLEncoder.encode(contentRoot, getEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace(); // should not be here
			assert(false);
			return contentRoot;
		}
	}
}
