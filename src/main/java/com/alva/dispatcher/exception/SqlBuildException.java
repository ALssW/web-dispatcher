package com.alva.dispatcher.exception;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class SqlBuildException extends DispatcherException{

	public SqlBuildException(String message) {
		super(message);
	}

	public SqlBuildException(String message, Object... params) {
		super(message, params);
	}

	public SqlBuildException(String message, Throwable cause) {
		super(message, cause);
	}
}
