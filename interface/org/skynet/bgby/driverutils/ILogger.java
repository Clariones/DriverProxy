package org.skynet.bgby.driverutils;

import java.util.logging.Level;

public interface ILogger {

	void log(Level level, String tag, String msg);

	void log(Level level, String tag, String msg, Object param1);

	void log(Level level, String tag, String msg, Object[] params);

	void log(Level level, String tag, String msg, Throwable thrown);
	
}