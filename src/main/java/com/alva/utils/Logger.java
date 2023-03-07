package com.alva.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */
public class Logger<T> {

	private static int level;

	private static final String LEVEL_DEBUG = "debug";
	private static final String LEVEL_INFO  = "info";

	static {
		Properties properties = PropertiesUtil.getProperties("logger.properties");

		String level = properties.getProperty("level").toLowerCase(Locale.ROOT);
		if (LEVEL_DEBUG.equals(level)) {
			Logger.level = 1;
		} else if (LEVEL_INFO.equals(level)) {
			Logger.level = 0;
		}
	}


	private final String           clazzName;
	private final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public Logger(Class<T> clazz) {
		clazzName = " [" + Thread.currentThread().getName() + "] <" + clazz.getName() + ">";
	}

	private void log(String level, String msg) {
		System.out.println(f.format(new Date()) + clazzName + " [" + level + "] : " + msg);
	}


	public void info(String msg) {
		log("info", msg);
	}

	public void info(String msg, Object... params) {
		info(String.format(msg, params));
	}

	public void debug(String msg) {
		if (Logger.level >= 1) {
			log("debug", msg);
		}
	}

	public void debug(String msg, Object... params) {
		debug(String.format(msg, params));
	}

}
