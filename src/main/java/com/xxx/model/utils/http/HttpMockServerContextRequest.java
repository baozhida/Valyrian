package com.xxx.model.utils.http;

import java.util.Map;

public class HttpMockServerContextRequest {
	private Object requestBody;
	
	private Map<String, Object> headers;
	
	private Map<String, Object> urlParams;

	public Object getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public Map<String, Object> getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(Map<String, Object> urlParams) {
		this.urlParams = urlParams;
	}
}
