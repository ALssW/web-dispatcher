package com.alva.dispatcher.caster;

import com.alva.annotaion.RequestParma;
import com.alva.dispatcher.exception.DispatcherException;
import com.alva.dispatcher.exception.RequestParameterNofFoundException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public interface BaseCaster {
	/**
	 * 将方法所需参数按所需类型转换, 从 request 中取出传入的请求参数, 并创建实例
	 * @param request 请求参数
	 * @param parameter 方法所需的参数
	 * @return 转换的实例
	 * @throws DispatcherException 转换异常
	 */
	Object cast(HttpServletRequest request, Parameter parameter) throws DispatcherException;

	/**
	 * 按参数注解 @RequestParam 获取请求参数
	 * @param request 请求
	 * @param parameter 参数
	 * @return 请求参数
	 * @throws DispatcherException 转换异常
	 */
	default String getRequestParam(HttpServletRequest request, Parameter parameter) throws DispatcherException {
		RequestParma requestParma = parameter.getAnnotation(RequestParma.class);
		if (requestParma == null) {
			throw new RequestParameterNofFoundException("请求中未包含该参数 [%s]", parameter.getType());
		}
		String requestParameter = request.getParameter(requestParma.value());
		if (requestParameter == null) {
			throw new RequestParameterNofFoundException("请求中未包含该参数 [%s]", parameter.getType());
		}
		return requestParameter;
	}
}
