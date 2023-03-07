package com.alva.dispatcher.entity;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ALsW
 */
public class Response<T> {
	public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
	public static final String CONTENT_TYPE_HTML = "text/html;charset=UTF-8";
	public static final String CONTENT_TYPE_IMAGE_ = "text/html;charset=UTF-8";
	public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";

	public static final Integer CODE_OK   = 200;
	public static final Integer CODE_FAIL = 300;


	private Integer code;
	private String  msg;
	private T       data;

	private Response() {
	}

	private Response(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> Response<T> ok(String msg) {
		return ok(msg, null);
	}

	public static <T> Response<T> ok(T data) {
		return ok(null, data);
	}

	public static <T> Response<T> ok(String msg, T data) {
		return new Response<>(CODE_OK, msg, data);
	}

	public static <T> Response<T> fail(String msg) {
		return fail(msg, null);
	}

	public static <T> Response<T> fail(String msg, T data) {
		return fail(CODE_FAIL, msg, data);
	}

	public static <T> Response<T> fail(Integer code, String msg, T data) {
		return new Response<>(code, msg, data);
	}

	public static <T> void response(HttpServletResponse resp, Response<T> response) throws IOException {
		responseObj(resp, response);
	}

	public static void responseObj(HttpServletResponse resp, Object response) throws IOException {
		if (CONTENT_TYPE_JSON.equals(resp.getContentType())) {
			responseJson(resp, response);
		} else {
			response(resp, response);
		}
	}

	public static void responseJson(HttpServletResponse resp, Object json) throws IOException {
		response(resp, JSON.toJSONString(json));
	}

	public static void response(HttpServletResponse resp, Object obj) throws IOException {
		if (!resp.isCommitted()) {
			PrintWriter writer = resp.getWriter();
			writer.print(obj);
			writer.close();
		}
	}
}
