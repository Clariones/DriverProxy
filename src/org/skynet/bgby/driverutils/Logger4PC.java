package org.skynet.bgby.driverutils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logger4PC implements ILogger {
	protected Map<String, Logger> loggers = new HashMap<String, Logger>();
	
	protected Logger logger(String tag){
		Logger rst = loggers.get(tag);
		if (rst != null){
			return rst;
		}
		
		rst = Logger.getLogger(tag);
		loggers.put(tag, rst);
		return rst;
	}

	@Override
	public void log(Level level, String tag, String msg) {
		logger(tag).log(level, msg);
	}

	@Override
	public void log(Level level, String tag, String msg, Object param1) {
		logger(tag).log(level, msg, param1);
	}

	@Override
	public void log(Level level, String tag, String msg, Object[] params) {
		logger(tag).log(level, msg, params);
	}

	@Override
	public void log(Level level, String tag, String msg, Throwable thrown) {
		logger(tag).log(level, msg, thrown);
	}
}
