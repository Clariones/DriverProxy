package org.skynet.bgby.devicedriver;

public class DeviceDriverException extends Exception {

	private static final long serialVersionUID = 3411097451157831451L;

	public DeviceDriverException() {
	}

	public DeviceDriverException(String message) {
		super(message);
	}

	public DeviceDriverException(Throwable cause) {
		super(cause);
	}

	public DeviceDriverException(String message, Throwable cause) {
		super(message, cause);
	}

	public DeviceDriverException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
