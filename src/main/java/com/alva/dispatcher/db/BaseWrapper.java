package com.alva.dispatcher.db;

import com.alva.annotaion.Id;
import com.alva.annotaion.Table;
import com.alva.annotaion.TableAlias;
import com.alva.dispatcher.exception.SqlBuildException;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-15
 */
abstract class BaseWrapper<T> implements IBaseWrapper<T> {
	String             table;
	Class<T>           targetClazz;
	Field              idField;
	List<Field>        fieldList     = new ArrayList<>();
	Map<String, Field> fieldMap      = new HashMap<>();
	List<String>       fieldNameList = new ArrayList<>();
	List<Object>       valueList     = new ArrayList<>();
	String             method;

	final String FROM              = " FROM ";
	final String WHERE             = " WHERE ";
	final String EQ                = " = ";
	final String LT                = " < ";
	final String GT                = " > ";
	final String LTE                = " <= ";
	final String GTE                = " >= ";
	final String AND                = " AND ";
	final String SEPARATE          = ", ";
	final String SPACE             = " ";
	final String VALUE_PLACEHOLDER = "?";

	final StringBuilder sql = new StringBuilder();

	public BaseWrapper(Class<T> targetClazz) throws SqlBuildException {
		setTargetClazz(targetClazz);
		setTable(null);
	}

	public BaseWrapper(Class<T> targetClazz, String table) throws SqlBuildException {
		setTargetClazz(targetClazz);
		setTable(table);
	}

	@Override
	public int execute() {
		return 0;
	}

	String getTable() throws SqlBuildException {
		if (this.table != null) {
			return this.table;
		}
		setTable(null);
		return this.table;
	}

	void setTable(String table) throws SqlBuildException {
		Table tableA = targetClazz.getAnnotation(Table.class);
		if (tableA == null) {
			throw new SqlBuildException(targetClazz + " 并未在指定 @Table 注解");
		}
		initFiled(tableA.hsaBase());
		this.table = table == null ? '`' + tableA.value() + '`' : '`' + table + '`';
	}

	public String getSql() {
		return sql.toString();
	}

	public Class<T> getTargetClazz() {
		return targetClazz;
	}

	void setTargetClazz(Class<T> targetClazz) {
		this.targetClazz = targetClazz;
	}

	List<Field> getFieldList() {
		return fieldList;
	}

	List<String> getFieldNameList() {
		return fieldNameList;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	protected void initFiled(boolean hasBase) {
		Field[] declaredFields = getTargetClazz().getDeclaredFields();
		setFiled(declaredFields);
		if (hasBase) {
			initSuperFiled();
		}
	}

	protected void initSuperFiled() {
		Class<? super T> superclass = getTargetClazz().getSuperclass();
		if (superclass != Object.class) {
			setFiled(superclass.getDeclaredFields());
		}
	}

	private void setFiled(Field... fields) {
		for (Field field : fields) {
			field.setAccessible(true);
			fieldList.add(field);
			fieldMap.put(field.getName(), field);

			String     fileName   = field.getName();
			TableAlias tableAlias = field.getAnnotation(TableAlias.class);
			if (tableAlias != null) {
				fileName = tableAlias.value();
			}
			fieldNameList.add('`' + fileName + '`');
		}
	}

	@Override
	public IBaseWrapper<T> from() {
		sql.append(FROM).append(table).append(SPACE);
		return this;
	}

	@Override
	public IBaseWrapper<T> from(String table) {
		sql.append(FROM).append(table);
		return this;
	}

	@Override
	public IBaseWrapper<T> eq(String filedName, Object value) throws SqlBuildException {
		if (sql.indexOf(WHERE) != -1) {
			throw new SqlBuildException("已存在编写的 where 语句, 无法再次编写");
		}
		sql.append(WHERE).
				append(filedName).append(EQ).append(VALUE_PLACEHOLDER)
				.append(SPACE);
		valueList.add(value);
		return this;
	}

	@Override
	public IBaseWrapper<T> and(String and) {
		sql.append(AND).append(and);
		return this;
	}

	@Override
	public IBaseWrapper<T> lt(String lt) {
		sql.append(LT).append(lt);
		return this;
	}

	@Override
	public IBaseWrapper<T> gt(String gt) {
		sql.append(GT).append(gt);
		return this;
	}

	@Override
	public IBaseWrapper<T> lte(String lte) {
		sql.append(LTE).append(lte);
		return this;
	}

	@Override
	public IBaseWrapper<T> gte(String gte) {
		sql.append(GTE).append(gte);
		return this;
	}

	@Override
	public IBaseWrapper<T> appendSql(String appendSql, Object... params) {
		sql.append(appendSql);
		valueList.addAll(Arrays.asList(params));
		return null;
	}

	@Override
	public String getIdName() {
		for (Field field : fieldList) {
			Id id = field.getAnnotation(Id.class);
			if (id == null) {
				continue;
			}

			idField = field;
			TableAlias tableAlias = field.getAnnotation(TableAlias.class);
			if (tableAlias != null) {
				return tableAlias.value();
			}
			return '`' + field.getName() + '`';
		}

		return "`id`";
	}

	@Override
	public void getIdValue(T target) throws SqlBuildException {
		try {
			valueList.add(idField.get(target));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new SqlBuildException("获取 ID 值异常");
		}
	}

	@Override
	public List<Object> getValueList() {
		return valueList;
	}

	@Override
	public Object[] getValues() {
		return valueList.toArray();
	}

	protected void deleteLastSeparate() {
		sql.deleteCharAt(sql.lastIndexOf(SEPARATE));
		sql.deleteCharAt(sql.lastIndexOf(SPACE));
	}

	private Object parseString(Object value) {
		if (value == null) {
			return "null";
		}

		if (value.getClass() != Integer.class) {
			return "'" + value + "'";
		}
		return value;
	}

	@Override
	public void end() {
		sql.append(";");
	}

	protected void checkSql() throws SqlBuildException {
		if (sql.length() != 0) {
			throw new SqlBuildException("已存在编写的 sql 语句, 无法重新编写");
		}
	}

	@Override
	public String toString() {
		return sql.toString();
	}

}
