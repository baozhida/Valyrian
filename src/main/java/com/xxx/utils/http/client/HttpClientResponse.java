package com.xxx.utils.http.client;

import java.util.Map;

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class HttpClientResponse {
	private String stateCode;

	private Object responseBody;

	private Map<String, Object> headers;

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
}
