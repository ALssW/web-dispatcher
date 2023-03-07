package com.alva.dispatcher.caster;

import com.alva.utils.EntityUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public class EntityCaster implements BaseCaster {
	@Override
	public Object cast(HttpServletRequest request, Parameter parameter) {
		return EntityUtil.create(request, parameter.getType());
	}
}
