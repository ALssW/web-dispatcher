package com.alva.dispatcher;

import com.alva.dispatcher.caster.BaseCaster;
import com.alva.dispatcher.exception.DispatcherException;
import com.alva.utils.Logger;
import com.alva.utils.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;
import java.util.Properties;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
public class ParameterCaster {
	private static final Logger<ParameterCaster> logger = new Logger<>(ParameterCaster.class);

	private static final Properties CASTER_PROPERTIES = PropertiesUtil.getProperties("parameter-caster.properties");

	public static Object cast(HttpServletRequest req, Parameter parameter) throws DispatcherException {
		Class<?>   parameterClazz = parameter.getType();
		Object     o              = CASTER_PROPERTIES.get(parameterClazz.getName());
		String     casterClass    = (String) o;
		Object     result;
		BaseCaster caster;
		try {
			if (casterClass == null) {
				logger.info("类型 [%s] 将使用实体转换器进行处理", parameterClazz.getName());
				caster = ((BaseCaster) Class.forName(CASTER_PROPERTIES.getProperty("entity")).newInstance());
				result = caster.cast(req, parameter);
				logger.info("类型 [%s] 转换成功", parameterClazz.getName());
				return result;
			}

			caster = (BaseCaster) Class.forName(casterClass).newInstance();
			result = caster.cast(req, parameter);
			logger.info("类型 [%s] 转换成功", parameterClazz.getName());
			return result;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			logger.info("类型 [%s] 转换失败", parameterClazz.getName());
			return null;
		}
	}

}
