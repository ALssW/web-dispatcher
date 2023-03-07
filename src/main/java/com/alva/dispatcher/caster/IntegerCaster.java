package com.alva.dispatcher.caster;

import com.alva.dispatcher.exception.DispatcherException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public class IntegerCaster implements BaseCaster {
	@Override
	public Object cast(HttpServletRequest request, Parameter parameter) throws DispatcherException {
		return Integer.valueOf(getRequestParam(request, parameter));
	}
}
