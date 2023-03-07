package com.alva.dispatcher.exception;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class DispatcherException extends Exception {
	public DispatcherException() {
		super();
	}

	public DispatcherException(String message) {
		super(message);
	}

	public DispatcherException(String message, Object... params) {
		super(String.format(message, params));
	}

	public DispatcherException(String message, Throwable cause) {
		super(message, cause);
	}

	public DispatcherException(Throwable cause) {
		super(cause);
	}

	protected DispatcherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
