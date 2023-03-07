package com.alva.dispatcher.exception;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public class CasterException extends DispatcherException{
	public CasterException() {
		super();
	}

	public CasterException(String message) {
		super(message);
	}

	public CasterException(String message, Object... params) {
		super(String.format(message, params));
	}

	public CasterException(String message, Throwable cause) {
		super(message, cause);
	}

	public CasterException(Throwable cause) {
		super(cause);
	}

	protected CasterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
