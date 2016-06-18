package org.skynet.bgby.devicedriver;

import org.skynet.bgby.driverproxy.DPModuleException;

public class DriverModuleException extends DPModuleException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -66820367212789247L;

	public DriverModuleException() {
	}

	public DriverModuleException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DriverModuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DriverModuleException(String message) {
		super(message);
	}

	public DriverModuleException(Throwable cause) {
		super(cause);
	}

}
