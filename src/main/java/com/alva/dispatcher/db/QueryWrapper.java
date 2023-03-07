package com.alva.dispatcher.db;

import com.alva.dispatcher.exception.SqlBuildException;
import com.alva.utils.SqlUtil;

import java.util.List;
import java.util.Map;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-14
 */
public class QueryWrapper<T> extends BaseWrapper<T> {

	private final String SELECT = "SELECT ";
	private final String AND    = " AND ";
	private final String LIMIT    = " LIMIT ";
	private final String GROUP_BY          = " GROUP BY ";
	private final String ORDER_BY          = " ORDER BY ";
	private final String ORDER_BY_TYPE_ASC          = " ASC ";
	private final String ORDER_BY_TYPE_DESC          = " DESC ";

	public QueryWrapper(Class<T> targetClazz) throws SqlBuildException {
		super(targetClazz);
	}

	public QueryWrapper(Class<T> targetClazz, String table) throws SqlBuildException {
		super(targetClazz, table);
	}

	public QueryWrapper<T> check(String filedName, Object value) throws SqlBuildException {
		checkSql();
		select(filedName).from().eq(filedName, value).end();
		return this;
	}

	@Override
	public T executeResult() {
		return SqlUtil.executeQuery(this);
	}

	public List<Map<String, Object>> executeGroup() {
		return SqlUtil.executeQueryMap(this);
	}

	public List<T> executeList() {
		return SqlUtil.executeQueryList(this);
	}

	public QueryWrapper<T> selectAllById(Object id) throws SqlBuildException {
		selectAllBy(new String[]{getIdName()}, new Object[]{id});
		return this;
	}

	public QueryWrapper<T> selectAllBy(String[] filedNames, Object[] filedValue) throws SqlBuildException {
		select(fieldNameList.toArray(new String[0])).from();
		for (int i = 0; i < filedNames.length; i++) {
			if (i == 0) {
				eq(filedNames[i], filedValue[i]);
				continue;
			}
			and(filedNames[i], filedValue[i]);
		}
		return this;
	}

	public QueryWrapper<T> selectAll() throws SqlBuildException {
		select(fieldNameList.toArray(new String[0])).from();
		return this;
	}

	public QueryWrapper<T> select(String... filedName) throws SqlBuildException {
		checkSql();
		sql.append(SELECT);
		for (String filed : filedName) {
			sql.append(filed).append(SEPARATE);
		}
		deleteLastSeparate();
		return this;
	}

	private QueryWrapper<T> and(String filedName, Object o) {
		sql.append(AND).append(filedName).append(SPACE).append(EQ).append(SPACE).append(VALUE_PLACEHOLDER);
		valueList.add(o);
		return this;
	}

	public QueryWrapper<T> groupBy(String filedName) {
		sql.append(GROUP_BY).append('`').append(filedName).append('`');
		return this;
	}

	public QueryWrapper<T> orderByAsc(String orderBy) {
		return orderBy(orderBy, ORDER_BY_TYPE_ASC);
	}

	public QueryWrapper<T> orderByDesc(String orderBy) {
		return orderBy(orderBy, ORDER_BY_TYPE_DESC);
	}

	public QueryWrapper<T> orderBy(String orderBy, String type) {
		sql.append(ORDER_BY).append('`').append(orderBy).append('`').append(SPACE).append(type);
		return this;
	}
	public QueryWrapper<T> limit(int size) {
		limit(0, size);
		return this;
	}

	public QueryWrapper<T> limit(int from, int size) {
		sql.append(LIMIT).append(from).append(SEPARATE).append(size);
		return this;
	}
}
