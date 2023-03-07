package com.alva.dispatcher;

import com.alibaba.fastjson2.JSON;
import com.alva.annotaion.PageResponse;
import com.alva.annotaion.Table;
import com.alva.dispatcher.entity.Page;
import com.alva.dispatcher.entity.Response;
import com.alva.dispatcher.exception.DispatcherException;
import com.alva.utils.Logger;
import com.alva.utils.SqlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class RequestHandler {

	private final String                 path;
	private final Method                 handler;
	private final ServletHandler         servletHandler;
	private final Logger<RequestHandler> logger = new Logger<>(RequestHandler.class);

	public RequestHandler(String path, ServletHandler servletHandler, Method handler) {
		this.path = path;
		this.servletHandler = servletHandler;
		this.handler = handler;
	}

	public ServletHandler getServletHandler() {
		return servletHandler;
	}

	public String getPath() {
		return path;
	}

	public Method getHandler() {
		return handler;
	}

	public void handle(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, DispatcherException, InvocationTargetException, IllegalAccessException {
		ServletHandler servletHandler = getServletHandler();
		Method         handler        = getHandler();
		logger.info("正在获取资源 [%s] 处理器 [%s-%s] 的响应",
				servletHandler.getPath(), getPath(), handler.getName());
		setContentType(resp);
		Object returnValue;

		if (handler.isAnnotationPresent(PageResponse.class)) {
			returnValue = page(req, handler);
		} else {
			Object[] parameters = prepareParameters(handler, req, resp);
			returnValue = handler.invoke(servletHandler.getServlet(), parameters);
		}

		Response.responseObj(resp, returnValue);
		logger.info("资源 [%s] 处理器  [%s-%s]] 的响应成功 ==>\n [%s]", servletHandler.getPath(), getPath(),
				handler.getName(), JSON.toJSONString(returnValue));
	}


	private Object[] prepareParameters(Method requestHandler, HttpServletRequest req, HttpServletResponse resp) throws DispatcherException {
		if (requestHandler.getParameterCount() == 0) {
			return null;
		}
		Parameter[]  handlerParameters = requestHandler.getParameters();
		List<Object> parameterList     = new ArrayList<>(requestHandler.getParameterCount());

		for (Parameter handlerParameter : handlerParameters) {
			Class<?> handlerParameterType = handlerParameter.getType();
			if (handlerParameterType == HttpServletRequest.class) {
				parameterList.add(req);
				continue;
			}

			if (handlerParameterType == HttpServletResponse.class) {
				parameterList.add(resp);
				continue;
			}
			parameterList.add(ParameterCaster.cast(req, handlerParameter));
		}
		return parameterList.toArray();
	}

	private Page<?> page(HttpServletRequest req, Method requestHandler) throws DispatcherException {
		PageResponse pageResponse = requestHandler.getAnnotation(PageResponse.class);
		Table        table;
		if ((table = pageResponse.value().getAnnotation(Table.class)) == null) {
			return null;
		}
		Class  pageEntityClass = pageResponse.value();
		String tableName       = "`" + table.value() + "`";

		Parameter pageParameter = requestHandler.getParameters()[0];
		if (pageParameter.getType() == Page.class) {
			Page<?> page = (Page<?>) ParameterCaster.cast(req, pageParameter);
			if (pageResponse.eqSql().isEmpty() && pageResponse.eqBy().length == 0) {
				return SqlUtil.executePage(tableName, page, pageEntityClass, null);
			}
			String[]     eqBys     = pageResponse.eqBy();
			List<String> eqParams = new ArrayList<>(eqBys.length);
			for (String by : eqBys) {
				String eqParma = req.getParameter(by);
				if (eqParma == null) {
					throw new DispatcherException("缺少分页所需的 eq 参数: [%s]", by);
				}
				eqParams.add(eqParma);
			}
			return SqlUtil.executePage(tableName, page, pageEntityClass, pageResponse.eqSql(), eqParams.toArray());

		}
		return null;
	}

	private void setContentType(HttpServletResponse resp) {
		resp.setContentType(handler.getAnnotation(
				com.alva.annotaion.RequestHandler.class).contentType());
	}

	@Override
	public String toString() {
		return "[" +
				"path='" + path + '\'' +
				", handler=" + getServletHandler().getServlet().getClass().getSimpleName() + "-" + handler.getName() +
				"]";
	}
}
