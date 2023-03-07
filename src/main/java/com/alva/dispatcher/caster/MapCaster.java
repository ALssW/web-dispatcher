package com.alva.dispatcher.caster;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-17
 */
public class MapCaster implements BaseCaster {

	@Override
	public Object cast(HttpServletRequest request, Parameter parameter) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String>   returnMap    = new HashMap<>(parameterMap.size());
		parameterMap.forEach((key, value) -> returnMap.put(key, value[0]));
		return returnMap;
	}
}
