package com.xxx.model.utils.http;

import java.util.ArrayList;
import java.util.List;

public class HttpMockServerConfig {
	private int port;
	
	private List<HttpMockServerContext> httpContexts = new ArrayList<HttpMockServerContext>();

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<HttpMockServerContext> getHttpContexts() {
		return httpContexts;
	}

	public void setHttpContexts(List<HttpMockServerContext> httpContexts) {
		this.httpContexts = httpContexts;
	}
}
