package org.skynet.bgby.restserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.RestRequestCodec;
import org.skynet.bgby.protocol.HttpResponseImpl;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class RestService extends NanoHTTPD {

	private static final String TAG = RestService.class.getName();
	protected RestRequestCodec protocolHandler;
	protected List<IRestRequestHandler> restCommandHandlers;

	public RestService() {
		this(DriverUtils.DEFAULT_REST_PORT);
	}

	public RestService(int port) {
		this(null, port);
	}

	public RestService(String hostname, int port) {
		super(hostname, port);
		DriverUtils.log(Level.FINE, TAG,
				"Rest service created at " + (hostname == null ? "port " : (hostname + ":")) + port);
		restCommandHandlers = new ArrayList<IRestRequestHandler>();
	}

	public RestRequestCodec getProtocolHandler() {
		return protocolHandler;
	}

	public void registerCommandHandler(IRestRequestHandler restCmdHandler) {
		if (restCommandHandlers.contains(restCmdHandler)) {
			return;
		}
		restCommandHandlers.add(restCmdHandler);
	}

	@Override
	public Response serve(IHTTPSession session) {
		IRestRequest request = getProtocolHandler().getRequest(session);
		if (request == null) {
			return responseInvalidRestRequest(session);
		}
		IHttpResponse response = new HttpResponseImpl();

		Iterator<IRestRequestHandler> it = restCommandHandlers.iterator();
		boolean handled = false;
		while (it.hasNext()) {
			IRestRequestHandler handler = it.next();
			try {
				if (handler.handleCommand(request, response)) {
					handled = true;
					break;
				}
			} catch (Throwable t) {
				String msg = DriverUtils.dumpExceptionToString(t);
				return newFixedLengthResponse(Status.EXPECTATION_FAILED, MIME_PLAINTEXT, msg);
			}
		}

		if (!handled && response.getDataLength() < 1) {
			String msg = "Request " + request.getCommand() + " has no handler found.";
			DriverUtils.log(Level.INFO, TAG, msg);
			response.setStatus(Status.INTERNAL_ERROR);
			response.setMimeType(MIME_PLAINTEXT);
			response.setAsString(msg);
		}
		IStatus status = response.getStatus();
		String mimeType = response.getMimeType();
		InputStream data = response.getInputStream();
		long totalBytes = response.getDataLength();
		return newFixedLengthResponse(status, mimeType, data, totalBytes);
	}

	private Response responseInvalidRestRequest(IHTTPSession session) {
		String rstMessage = "bad request: " + session.getUri();
		if (session.getQueryParameterString() != null) {
			rstMessage += "?" + session.getQueryParameterString();
		}
		return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, rstMessage);
	}

	public void setRestProtocolHandler(RestRequestCodec protocolHandler) {
		this.protocolHandler = protocolHandler;
	}

}
