package com.alva.dispatcher;

import com.alva.annotaion.Dispatcher;
import com.alva.dispatcher.entity.Response;
import com.alva.dispatcher.exception.DispatcherException;
import com.alva.utils.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
@WebServlet(value = "/*", loadOnStartup = Integer.MAX_VALUE)
@Dispatcher(false)
@MultipartConfig
public class DispatcherServlet extends HttpServlet {

	private final Logger<DispatcherServlet> logger = new Logger<>(DispatcherServlet.class);

	private Map<String, ServletHandler> servletHandlerMap;

	private final Pattern URI_PATTERN = Pattern.compile("(^/\\w*)");
	private       String  contextPath;
	private       String  contextPathTo;

	/**
	 * 初始化 Dispatcher Servlet
	 * 获取需要被转发的 Servlet，封装为 ServletHandler，其下的请求方法则封装为 RequestHandler
	 * 之后所有需要的请求资源路径都将由 DispatcherServlet 通过给定的资源路径转发给指定的 ServletHandler
	 * 并由其下指定的 RequestHandler 处理请求与需要的参数
	 *
	 * @param config 通过 ServletConfig 获取 ServletContext 接着再通过 ServletRegistrations 获取所有 Servlet 的注册信息集合
	 */
	@Override
	public void init(ServletConfig config) {
		contextPath = config.getServletContext().getContextPath();
		contextPathTo = contextPath + "/";

		// 通过 ServletConfig 获取 ServletContext 接着再获取 ServletRegistrations
		// 在 ServletRegistrations 中则存有所有被 Tomcat 注册发现的 Servlet
		Map<String, ? extends ServletRegistration> registrationMap = config.getServletContext().getServletRegistrations();
		logger.debug("初始化 DispatcherServlet...");
		servletHandlerMap = new HashMap<>(registrationMap.size());

		// 遍历 RegistrationMap 排除不必要的 Servlet
		for (String servletName : registrationMap.keySet()) {
			if ("default".equals(servletName) || "jsp".equals(servletName)) {
				continue;
			}
			// 指定需要被转发的 Servlet 在 Dispatcher 中的资源路径
			specifyServletDispatcher(registrationMap.get(servletName));
		}

		logger.debug("初始化 DispatcherServlet [成功]");
		logger.info("\n\n-------------------- 以下资源将由 [%s] 转发处理 -------------------- ",
				this.getClass().getName());
		for (ServletHandler servletHandler : servletHandlerMap.values()) {
			System.out.println(servletHandler);
		}
		System.out.println();
	}

	/**
	 * 指定需要被 @Dispatcher 标记的 Servlet 在 Dispatcher 中的资源路径
	 * 存入 servletHandlerMap 中用于资源定位获取指定的 ServletHandler 处理请求
	 *
	 * @param registration 指定 Servlet 在 Tomcat 中的注册信息
	 */
	private void specifyServletDispatcher(ServletRegistration registration) {
		try {
			Class<?>   dispatcherServlet = Class.forName(registration.getClassName());
			Dispatcher dispatcherAnnotation;
			if (!dispatcherServlet.isAnnotationPresent(Dispatcher.class)) {
				return;
			}

			dispatcherAnnotation = dispatcherServlet.getAnnotation(Dispatcher.class);
			if (!dispatcherAnnotation.value()) {
				return;
			}
			for (String servletPath : registration.getMappings()) {
				ServletHandler servletHandler = new ServletHandler(servletPath, dispatcherServlet.newInstance());
				servletHandlerMap.put(servletPath, servletHandler);
				disassembleServlet(servletHandler);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.info("初始化 DispatcherServlet [失败]");
			e.printStackTrace();
		}
	}

	/**
	 * 拆解需要被转发的 Servlet，将其下被 @RequestHandler 标记的方法封装为 RequestHandler 并存入 ServletHandler
	 *
	 * @param servletHandler ServletHandler
	 */
	private void disassembleServlet(ServletHandler servletHandler) {
		Method[] requestHandlers = servletHandler.getServlet().getClass().getDeclaredMethods();

		for (Method handlerMethod : requestHandlers) {
			com.alva.annotaion.RequestHandler requestHandlerAnnotation;
			if (!handlerMethod.isAnnotationPresent(com.alva.annotaion.RequestHandler.class)) {
				continue;
			}
			requestHandlerAnnotation = handlerMethod.getAnnotation(com.alva.annotaion.RequestHandler.class);
			String requestPath = requestHandlerAnnotation.value();

			RequestHandler requestHandler = new RequestHandler(requestPath, servletHandler, handlerMethod);
			servletHandler.putRequestHandler(requestPath, requestHandler);
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String requestUri = req.getRequestURI();
		String nonePath   = "/";
		if (contextPathTo.equals(requestUri) || nonePath.equals(requestUri)) {
			return;
		}
		Matcher uriMatcher = URI_PATTERN.matcher(requestUri);

		String         servletUri;
		ServletHandler servletHandler;
		if (!uriMatcher.find() || (servletUri = uriMatcher.group()).isEmpty() ||
				(servletHandler = servletHandlerMap.get(servletUri)) == null) {
			logger.info("无法获取该资源路径 [%s]", requestUri);
			resp.sendError(404, "无法获取该资源路径 [" + requestUri + "]");
			return;
		}

		try {
			servletHandler.handleServlet(requestUri.replace(servletUri, ""), req, resp);
		} catch (IllegalAccessException | InvocationTargetException | DispatcherException e) {
			e.printStackTrace();
			logger.info("资源获取异常 [%s]", e);
			logger.info("[%s]", e.getMessage());
			Response.responseJson(resp, Response.fail(500, "资源获取异常 [" + e.getMessage() + "]", null));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("发生未知异常 [%s]", e);
			logger.info("[%s]", e.getMessage());
			Response.responseJson(resp, Response.fail(500, "发生未知异常 [" + e.getMessage() + "]", null));

		}

	}
}
