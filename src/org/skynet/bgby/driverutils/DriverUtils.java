package org.skynet.bgby.driverutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.skynet.bgby.protocol.IRestRequest;

public class DriverUtils {
	public static final int DEFAULT_REST_PORT = 8981;

	public static final String MIME_TYPE_JSON = "application/json; charset=utf-8";
	
	private static ILogger logger;

	public static ILogger getLogger() {
		return logger;
	}

	public static void setLogger(ILogger plogger) {
		logger = plogger;
	}

	public static void log(Level level, String tag, String msg) {
		logger.log(level, tag, msg);
	}

	public static void log(Level level, String tag, String msg, Object param1) {
		logger.log(level, tag, msg, param1);
	}

	public static void log(Level level, String tag, String msg, Object[] params) {
		logger.log(level, tag, msg, params);
	}

	public static void log(Level level, String tag, String msg, Throwable thrown) {
		logger.log(level, tag, msg, thrown);
	}

	public static String dumpExceptionToString(Throwable t) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
		t.printStackTrace(new PrintStream(bout));
		String msg = bout.toString();
		try {
			bout.close();
		} catch (IOException e) {
		}
		return msg;
	}

	private DriverUtils() {}

	public static String getRequestFullUri(IRestRequest restRequest) {
		String uri = restRequest.getRequestUri();
		String params =restRequest.getRequestParameterString();
		if (params == null || params.isEmpty()){
			return uri;
		}
		return uri+"?"+params;
	}

}
