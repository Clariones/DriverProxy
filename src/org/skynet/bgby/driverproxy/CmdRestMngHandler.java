package org.skynet.bgby.driverproxy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import org.skynet.bgby.command.management.BaseManageCmd;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.protocol.IHttpResponse;
import org.skynet.bgby.protocol.IRestRequest;
import org.skynet.bgby.protocol.IRestResponse;
import org.skynet.bgby.protocol.RestResponseImpl;
import org.skynet.bgby.restserver.IRestRequestHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.iki.elonen.NanoHTTPD.Response.Status;

public class CmdRestMngHandler implements IRestRequestHandler {
	private static final String TAG = "RestManagementCommandHandler";
	protected Map<String, BaseManageCmd> handlers;
	protected Gson gson;

	@Override
	public boolean handleCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		String strCmd = restRequest.getCommand();
		if (strCmd.equals("help")){
			handleHelpCommand(restRequest, restResponse);
			return false;
		}
		BaseManageCmd handler = handlers.get(strCmd);
		if (handler == null){
			return false;
		}
		IRestResponse result = handler.handleCommand(restRequest);
		restResponse.setMimeType(DriverUtils.MIME_TYPE_JSON);
		restResponse.setStatus(Status.OK);
		restResponse.setAsString(toJson(result));
		return true;
	}

	private String toJson(IRestResponse result) {
		Gson gson = getGson();
		return gson.toJson(result);
	}

	private Gson getGson() {
		if (gson != null){
			return gson;
		}
		gson = new GsonBuilder().setPrettyPrinting().create();
		return gson;
	}

	private void handleHelpCommand(IRestRequest restRequest, IHttpResponse restResponse) {
		StringBuilder sb = new StringBuilder();
		Iterator<BaseManageCmd> it = handlers.values().iterator();
		while(it.hasNext()){
			BaseManageCmd handler = it.next();
			sb.append(handler.getUsage()).append("\r\n");
		}
		RestResponseImpl response = new RestResponseImpl();
		response.setRequest(DriverUtils.getRequestFullUri(restRequest));
		response.setData(sb.toString());
		String mineHelpMsg = toJson(response);
		
		String oldMsg = restResponse.getAsString();
		if (oldMsg == null){
			restResponse.setAsString(mineHelpMsg);
		}else{
			restResponse.setAsString(oldMsg +"\r\n" + mineHelpMsg);
		}
	}

	public void addHandler(String command, BaseManageCmd cmdHandler) {
		if (handlers == null){
			handlers = new HashMap<>();
		}
		BaseManageCmd oldHandler = handlers.put(command, cmdHandler);
		if (oldHandler != null){
			DriverUtils.log(Level.WARNING, TAG, "Command {0} handler changed from {1} to {2}",
					new Object[]{command, oldHandler, cmdHandler});
		}
	}

}
