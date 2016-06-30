package org.skynet.bgby.listeningserver;

public class ListeningServerException extends Exception {

	/**
	 * 
	 */
	protected static final long serialVersionUID = -8147455868745651145L;

	public ListeningServerException() {
		super();
	}

	public ListeningServerException(String message) {
		super(message);
	}

	public ListeningServerException(Throwable cause) {
		super(cause);
	}

	public ListeningServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ListeningServerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
