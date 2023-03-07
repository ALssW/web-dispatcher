package com.alva.utils;

import com.alva.annotaion.Table;
import com.alva.annotaion.TableAlias;
import com.alva.dispatcher.db.QueryWrapper;
import com.alva.dispatcher.db.UpdateWrapper;
import com.alva.dispatcher.entity.Page;
import com.alva.dispatcher.exception.SqlBuildException;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-09
 */
public class SqlUtil {

	private static final Logger<SqlUtil>         logger           = new Logger<>(SqlUtil.class);
	private static final ThreadLocal<Connection> CONNECTION_LOCAL = new ThreadLocal<>();
	private static final ThreadLocal<Integer>    COUNT_LOCAL      = new ThreadLocal<>();

	public static <T> int executeUpdate(UpdateWrapper<T> wrapper) {
		logger.info("execute query sql [%s]", wrapper);
		return executeUpdate(wrapper.getSql(), wrapper.getValues());
	}

	public static <T> T executeUpdateResult(UpdateWrapper<T> wrapper) {
		logger.info("execute query sql [%s]", wrapper);
		int updateResult = executeUpdateResult(wrapper.getSql(), wrapper.getValues());
		T   queryResult;
		if (updateResult != 0) {
			try {
				QueryWrapper<T> queryWrapper = new QueryWrapper<>(wrapper.getTargetClazz())
						.selectAllById(updateResult);
				queryResult = executeQuery(queryWrapper);
				return queryResult;
			} catch (SqlBuildException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static <T> T executeQuery(QueryWrapper<T> wrapper) {
		logger.debug("execute query sql [%s]", wrapper);
		return executeQuery(wrapper.getSql(), wrapper.getTargetClazz(), wrapper.getValues());
	}

	public static <T> List<T> executeQueryList(QueryWrapper<T> wrapper) {
		logger.debug("execute query sql [%s]", wrapper);
		return executeQueryList(wrapper.getSql(), wrapper.getTargetClazz(), wrapper.getValues());
	}

	public static <T> List<Map<String, Object>> executeQueryMap(QueryWrapper<T> wrapper) {
		return executeQueryMap(wrapper.getSql());
	}

	public static int executeUpdate(String sql, Object... params) {
		Connection        conn      = getConnection();
		PreparedStatement statement = null;
		try {
			statement = getPreparedStatement(conn, sql, params);
			logger.debug("execute update sql [%s]", getSql(statement));
			return statement.executeUpdate();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			close(statement, conn);
		}
		return 0;
	}

	/**
	 * 执行 <code>PreparedStatement.executeUpdate()</code>
	 *
	 * @param sql    预编译 SQL
	 * @param params 预编译 SQL 的参数
	 * @return 返回更新的 ID
	 */
	public static int executeUpdateResult(String sql, Object... params) {
		Connection        conn      = getConnection();
		PreparedStatement statement = null;
		ResultSet         resultSet = null;
		try {
			statement = getPreparedStatementWithKey(conn, sql, params);
			logger.debug("execute update sql [%s]", getSql(statement));
			if (statement.executeUpdate() == 0) {
				return 0;
			}
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			close(resultSet, statement, conn);
		}
		return 0;
	}

	public static <T> Page<T> executePage(String table, Page<T> page, Class<T> clazz, String eqSql, Object... eqBy) {
		String       pageSql;
		String       countSql;
		List<T>      list;
		int          count;
		List<Object> objectList;

		if (eqSql != null && eqBy != null) {
			countSql = "select count(*) from " + table + " where " + eqSql;
			pageSql = "select * from " + table + " where " + eqSql + " limit ?, ?;";
			objectList = new ArrayList<>(eqBy.length + 2);
			objectList.addAll(Arrays.asList(eqBy));
		} else {
			countSql = "select count(*) from " + table;
			pageSql = "select * from " + table + " limit ?, ?;";
			objectList = new ArrayList<>(2);
		}

		count = executeCount(countSql, eqBy);
		if (count <= 0) {
			page.setMaxPage(0);
			return page;
		}

		int nowPage  = page.getNowPage();
		int pageSize = page.getPageSize();

		int pageCont = count / pageSize;
		if (count % pageSize != 0) {
			pageCont++;
		}
		page.setMaxPage(pageCont);

		objectList.add((nowPage - 1) * pageSize);
		objectList.add(pageSize);
		list = executeQueryList(pageSql, clazz, objectList.toArray());
		page.setList(list);
		return page;
	}

	public static Integer executeCount(String sql, Object... params) {
		Connection        conn      = getConnection();
		PreparedStatement statement = null;
		ResultSet         resultSet = null;
		try {
			statement = getPreparedStatement(conn, sql, params);
			resultSet = statement.executeQuery();
			logger.debug("execute count sql [%s]", getSql(statement));
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			close(resultSet, statement, conn);
		}
		return 0;
	}

	/**
	 * 执行 <code>PreparedStatement.executeQuery()</code>
	 *
	 * @param sql    预编译 SQL
	 * @param clazz  实体类 Class
	 * @param params 预编译 SQL 的参数
	 * @param <T>    实体类类型
	 * @return 实体类
	 */
	public static <T> T executeQuery(String sql, Class<T> clazz, Object... params) {
		List<T> result = executeQueryList(sql, clazz, params);
		return result.size() != 0 ? result.get(0) : null;
	}

	/**
	 * 执行 <code>PreparedStatement.executeQuery()</code> 并以 List 返回
	 * 获取所有数据
	 *
	 * @param table 查找的表
	 * @param clazz 实体类 Class
	 * @param <T>   实体类类型
	 * @return 实体类
	 */
	public static <T> List<T> executeQueryAll(String table, Class<T> clazz) {
		return executeQueryList("select * from `" + table + "`;", clazz);
	}

	/**
	 * 执行 <code>PreparedStatement.executeQuery()</code> 并以 List 返回
	 *
	 * @param sql    预编译 SQL
	 * @param clazz  实体类 Class
	 * @param params 预编译 SQL 的参数
	 * @param <T>    实体类类型
	 * @return 实体类
	 */
	public static <T> List<T> executeQueryList(String sql, Class<T> clazz, Object... params) {
		Connection        conn      = getConnection();
		PreparedStatement statement = null;
		ResultSet         resultSet = null;
		List<T>           list      = new ArrayList<>();
		try {
			statement = getPreparedStatement(conn, sql, params);
			resultSet = statement.executeQuery();
			logger.debug("execute query sql [%s]", getSql(statement));
			while (resultSet.next()) {
				list.add(analysisResultSet(clazz, resultSet));
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			close(resultSet, statement, conn);
		}
		return list;
	}

	private static <T> T analysisResultSet(Class<T> clazz, ResultSet resultSet) {
		T instance = null;
		try {
			instance = clazz.newInstance();
			List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
			Table   table  = clazz.getAnnotation(Table.class);
			if (table != null && table.hsaBase()) {
				fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
			}
			setInField(instance, resultSet, fields.toArray(new Field[0]));
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public static List<Map<String, Object>> executeQueryMap(String sql, Object... params) {
		Connection                conn       = getConnection();
		PreparedStatement         statement  = null;
		ResultSet                 resultSet  = null;
		List<Map<String, Object>> resultList = new ArrayList<>();
		try {
			statement = getPreparedStatement(conn, sql, params);
			resultSet = statement.executeQuery();
			logger.debug("execute query sql [%s]", getSql(statement));
			while (resultSet.next()) {
				resultList.add(analysisResultSet2Map(resultSet));
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			close(resultSet, statement, conn);
		}
		return resultList;
	}

	private static Map<String, Object> analysisResultSet2Map(ResultSet resultSet) throws SQLException {
		ResultSetMetaData   metaData  = resultSet.getMetaData();
		Map<String, Object> columnMap = new HashMap<>(metaData.getColumnCount());

		columnMap.put("row", resultSet.getRow());
		for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
			columnMap.put(metaData.getColumnLabel(i), resultSet.getObject(i));
		}
		return columnMap;
	}

	private static <T> void setInField(T instance, ResultSet resultSet, Field[] fields) throws IllegalAccessException {
		for (Field field : fields) {
			field.setAccessible(true);
			String     filedName  = field.getName();
			TableAlias tableAlias = field.getAnnotation(TableAlias.class);
			if (tableAlias != null) {
				filedName = tableAlias.value();
			}
			field.setAccessible(true);
			try {
				field.set(instance, resultSet.getObject(filedName));
			} catch (SQLException e) {
				field.set(instance, null);
			}
		}

	}

	private static PreparedStatement getPreparedStatement(Connection conn, String sql, Object... params) throws SQLException {
		return getPreparedStatement(conn, sql, Statement.NO_GENERATED_KEYS, params);
	}

	private static PreparedStatement getPreparedStatementWithKey(Connection conn, String sql, Object... params) throws SQLException {
		return getPreparedStatement(conn, sql, Statement.RETURN_GENERATED_KEYS, params);
	}

	private static PreparedStatement getPreparedStatement(Connection conn, String sql, int key, Object... params) throws SQLException {
		PreparedStatement statement = null;
		if (conn != null) {
			statement = conn.prepareStatement(sql, key);
			for (int i = 0; i < params.length; i++) {
				statement.setObject(i + 1, params[i]);
			}
		}
		return statement;
	}

	public static void begin() {
		Connection connection = CONNECTION_LOCAL.get();
		if (connection != null) {
			logger.info("已开启一个事务");
			Integer count = COUNT_LOCAL.get();
			COUNT_LOCAL.set(++count);
			return;
		}
		connection = ConnectionUtil.getConnection();
		try {
			connection.setAutoCommit(false);
			CONNECTION_LOCAL.set(connection);
			COUNT_LOCAL.set(1);

			logger.info("开启新的事务");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	public static void commit() {
		if (countToThis()) {
			logger.info("本次事务需要上抛");
			return;
		}

		Connection connection = getConnection();
		try {
			COUNT_LOCAL.remove();
			connection.commit();
			connection.close();
			logger.info("本次事务已提交");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		CONNECTION_LOCAL.remove();

	}


	public static void rollback() {
		if (countToThis()) {
			logger.info("本次回滚需要上抛");
			return;
		}
		Connection connection = getConnection();
		try {
			COUNT_LOCAL.remove();
			connection.rollback();
			connection.close();
			logger.info("本次事务已回滚");
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
		CONNECTION_LOCAL.remove();

	}

	private static Connection getConnection() {
		Connection conn = CONNECTION_LOCAL.get();
		if (conn != null) {
			return conn;
		}
		return ConnectionUtil.getConnection();
	}

	private static boolean countToThis() {
		Integer count = COUNT_LOCAL.get();
		if (count == 1) {
			return false;
		}

		COUNT_LOCAL.set(--count);
		return true;
	}

	public static void close(AutoCloseable... closes) {
		for (AutoCloseable close : closes) {
			if (close == null) {
				continue;
			}
			try {
				if (close instanceof Connection) {
					closeConnection((Connection) close);
					continue;
				}

				close.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeConnection(Connection conn) throws SQLException {
		if (conn.getAutoCommit()) {
			conn.close();
		}
	}

	private static String getSql(PreparedStatement statement) {
		String sqlInfo = statement.toString();
		return sqlInfo.substring(sqlInfo.indexOf(" ") + 1);
	}
}