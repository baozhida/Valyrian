package com.xxx.model.utils.http;

import com.xxx.model.actionword.http.MapComparedConfig;

public class HttpMessageInfo {
	private HttpMockServerContextRequest request;
	
	private HttpMockServerContextResponse response;
	
	private MapComparedConfig config;
	
	private String contextKeyName;

	public HttpMockServerContextRequest getRequest() {
		return request;
	}

	public void setRequest(HttpMockServerContextRequest request) {
		this.request = request;
	}

	public HttpMockServerContextResponse getResponse() {
		return response;
	}

	public void setResponse(HttpMockServerContextResponse response) {
		this.response = response;
	}

	public MapComparedConfig getConfig() {
		return config;
	}

	public void setConfig(MapComparedConfig config) {
		this.config = config;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}
}
