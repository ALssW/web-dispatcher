package com.alva.annotaion;

import com.alva.dispatcher.entity.Response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ALsW
 * @version 1.0.0
 * @since 2023-02-11
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHandler {
	String value() default "/";
	String contentType() default Response.CONTENT_TYPE_JSON;
}
