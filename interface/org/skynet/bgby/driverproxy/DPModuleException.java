package org.skynet.bgby.driverproxy;

public class DPModuleException extends Exception {

	protected static final long serialVersionUID = -4629305001333591911L;

	public DPModuleException() {
		super();
	}

	public DPModuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DPModuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DPModuleException(String message) {
		super(message);
	}

	public DPModuleException(Throwable cause) {
		super(cause);
	}

}
