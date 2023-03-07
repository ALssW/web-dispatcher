package com.alva.dispatcher.db;

import com.alva.dispatcher.exception.SqlBuildException;
import com.alva.utils.SqlUtil;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-15
 */
public class UpdateWrapper<T> extends BaseWrapper<T> {

	public final String METHOD_INSERT = "INSERT";
	public final String METHOD_UPDATE = "UPDATE";
	public final String METHOD_DELETE = "DELETE";

	private final String INSERT = METHOD_INSERT + " ";
	private final String INTO   = " INTO ";
	private final String VALUES = " VALUES";
	private final String UPDATE = METHOD_UPDATE + " ";
	private final String SET    = " SET ";

	public UpdateWrapper(Class<T> targetClazz) throws SqlBuildException {
		super(targetClazz);
	}

	public UpdateWrapper(Class<T> targetClazz, String table) throws SqlBuildException {
		super(targetClazz, table);
	}

	public UpdateWrapper<T> insert(T target) throws SqlBuildException {
		checkSql();
		setMethod(METHOD_INSERT);
		sql.append(INSERT).append(INTO).append(getTable()).append(getFieldNames());
		insertValues(target);
		return this;
	}

	private UpdateWrapper<T> insertValues(T target) throws SqlBuildException {
		sql.append(VALUES).append("(");
		try {
			for (Field field : fieldList) {
				sql.append(VALUE_PLACEHOLDER);
				sql.append(SEPARATE);
				valueList.add(field.get(target));
			}
			deleteLastSeparate();
		} catch (IllegalAccessException e) {
			throw new SqlBuildException("获取属性值异常");
		}
		sql.append(")");
		return this;
	}

	public UpdateWrapper<T> update(T target, String... filedNames) throws SqlBuildException {
		checkSql();
		setMethod(METHOD_UPDATE);
		sql.append(UPDATE).append(getTable()).append(SET);
		updateValues(target, filedNames);
		return this;
	}

	public UpdateWrapper<T> update(String[] filedNames, Object[] values) throws SqlBuildException {
		checkSql();
		setMethod(METHOD_UPDATE);
		sql.append(UPDATE).append(getTable()).append(SET);
		updateValues(filedNames, values);
		return this;
	}

	public UpdateWrapper<T> update(String filedSql, Object... values) throws SqlBuildException {
		checkSql();
		setMethod(METHOD_UPDATE);
		sql.append(UPDATE).append(getTable()).append(SET);
		sql.append(SPACE).append(filedSql).append(SPACE);
		valueList.addAll(Arrays.asList(values));
		return this;
	}

	private void updateValues(T target, String[] filedNames) throws SqlBuildException {
		try {
			for (String filedName : filedNames) {
				sql.append(filedName).append(EQ).append(VALUE_PLACEHOLDER).append(SEPARATE);
				valueList.add(fieldMap.get(filedName).get(target));
			}
			deleteLastSeparate();
		} catch (IllegalAccessException e) {
			throw new SqlBuildException("获取属性值异常");
		}
	}

	private void updateValues(String[] filedNames, Object[] values) {
		for (int i = 0; i < filedNames.length; i++) {
			sql.append(filedNames[i]).append(EQ).append(VALUE_PLACEHOLDER).append(SEPARATE);
			valueList.add(values[i]);
		}
		deleteLastSeparate();
	}


	private String getFieldNames() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (String field : fieldNameList) {
			sb.append(field);
			sb.append(", ");
		}
		sb.deleteCharAt(sb.lastIndexOf(SEPARATE));
		sb.deleteCharAt(sb.lastIndexOf(SPACE));
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int execute() {
		return SqlUtil.executeUpdate(this);
	}

	@Override
	public T executeResult() {
		return SqlUtil.executeUpdateResult(this);
	}
}
