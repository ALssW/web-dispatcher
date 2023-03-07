package com.alva.dispatcher;

import com.alva.dispatcher.exception.DispatcherException;
import com.alva.utils.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class ServletHandler {


	private final String path;
	private final Object servlet;

	private final Map<String, RequestHandler> requestHandlerMap;

	private final Logger<ServletHandler> logger = new Logger<>(ServletHandler.class);

	public ServletHandler(String path, Object servlet) {
		this.path = path;
		this.servlet = servlet;
		this.requestHandlerMap = new HashMap<>(servlet.getClass().getDeclaredMethods().length);
	}

	public String getPath() {
		return path;
	}

	public Object getServlet() {
		return servlet;
	}

	public void putRequestHandler(String path, RequestHandler requestHandler) {
		requestHandler.getHandler().setAccessible(true);
		requestHandlerMap.put(path, requestHandler);
	}

	public RequestHandler getRequestHandler(String requestUri) {
		return requestHandlerMap.get(requestUri);
	}

	public void handleServlet(String requestPath, HttpServletRequest req, HttpServletResponse resp)
			throws DispatcherException, IOException, InvocationTargetException, IllegalAccessException {
		RequestHandler requestHandler = getRequestHandler(requestPath);
		if (requestHandler == null) {
			logger.info("未找到资源 [%s] 所需的请求处理器", req.getRequestURI());
			throw new DispatcherException("未找到资源 [%s] 所需的请求处理器", req.getRequestURI());
		}

		requestHandler.handle(req, resp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{ServletPath='");
		sb.append(path).append("' RequestHandlers=");
		for (String handlerKey : requestHandlerMap.keySet()) {
			sb.append(getRequestHandler(handlerKey)).append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.append("}").toString();
	}
}
