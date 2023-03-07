package com.alva.dispatcher.db;

import com.alva.dispatcher.exception.SqlBuildException;

import java.util.List;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-16
 */
public interface IBaseWrapper<T> {

	int execute();

	T executeResult();

	IBaseWrapper<T> from();

	IBaseWrapper<T> from(String table);

	IBaseWrapper<T> eq(String filedName, Object value) throws SqlBuildException;

	String getIdName();

	void getIdValue(T target) throws SqlBuildException;

	List<Object> getValueList();

	Object[] getValues();

	void end();

	IBaseWrapper<T> appendSql(String appendSql,  Object... params);

	IBaseWrapper<T> and(String and);

	IBaseWrapper<T> lt(String lt);

	IBaseWrapper<T> gt(String gt);

	IBaseWrapper<T> lte(String lte);

	IBaseWrapper<T> gte(String gte);
}
