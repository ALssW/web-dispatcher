package com.alva.dispatcher.exception;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class RequestTypeException  extends CasterException{

	public RequestTypeException(String message) {
		super(message);
	}

	public RequestTypeException(String message, Object... params) {
		super(message, params);
	}
}
