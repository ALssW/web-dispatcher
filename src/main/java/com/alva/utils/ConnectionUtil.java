package com.alva.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */
public class ConnectionUtil {
	private static final Logger<SqlUtil> logger = new Logger<>(SqlUtil.class);
	private static final DataSource DATA_SOURCE;

	static {
		try {
			//初始化Druid连接池 - 全局初始化一次
			DATA_SOURCE = DruidDataSourceFactory.createDataSource(PropertiesUtil.getProperties("datasource.properties"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = DATA_SOURCE.getConnection();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		logger.debug("get connection...");
		return connection;
	}

}
