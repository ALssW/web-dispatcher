package com.alva.dispatcher.exception;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public class RequestParameterNofFoundException extends CasterException {

	public RequestParameterNofFoundException(String message) {
		super(message);
	}

	public RequestParameterNofFoundException(String message, Object... params) {
		super(message, params);
	}
}
