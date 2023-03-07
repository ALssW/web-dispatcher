package com.alva.utils;

import com.alva.annotaion.TableAlias;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */
public class EntityUtil {

	private static final Logger<EntityUtil> logger = new Logger<>(EntityUtil.class);

	public static <T> void build(HttpServletRequest req, T instance) {
		try {
			build(req, instance, instance.getClass());
			logger.debug("建造实体 [%s] ", instance);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static <T> T create(HttpServletRequest req, Class<T> clazz) {
		T instance = null;
		try {
			instance = clazz.newInstance();
			logger.debug("创建实体 [%s] ", instance);
			build(req, instance, instance.getClass());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return instance;
	}

	private static void build(HttpServletRequest req, Object instance, Class<?> clazz) throws IllegalAccessException {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			if (field.getAnnotation(TableAlias.class) != null) {
				name = field.getAnnotation(TableAlias.class).value();
			}
			String parameter = req.getParameter(name);
			if (parameter == null || "".equals(parameter)) {
				continue;
			}
			if (field.getType() == Integer.class) {
				field.set(instance, Integer.parseInt(parameter));
			} else if (field.getType() == Double.class) {
				field.set(instance, Double.parseDouble(parameter));
			} else if(field.getType() == Long.class){
				field.set(instance, Long.parseLong(parameter));
			}else{
				field.set(instance, parameter);
			}
		}
		logger.debug(clazz.getSimpleName() + " 实体创建完成");
	}

}
