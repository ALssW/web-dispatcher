package com.alva.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */
public class PropertiesUtil {

	public static Properties getProperties(String name) {
		Properties properties = new Properties();
		try {
			properties.load(PropertiesUtil.class.getClassLoader()
					.getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static Properties getApplicationProperties() {
		return getProperties("application.properties");
	}

	public static String getProperty(String key) {
		return getApplicationProperties().getProperty(key);
	}

}
