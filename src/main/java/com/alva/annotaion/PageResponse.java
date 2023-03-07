package com.alva.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageResponse {

	Class<?> value();
	String eqSql() default "";
	String[] eqBy() default {};
}
