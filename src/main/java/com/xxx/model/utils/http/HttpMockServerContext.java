package com.xxx.model.utils.http;

import java.util.ArrayList;
import java.util.List;

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class HttpMockServerContext {
	private List<HttpMessageInfo> httpMessageInfoList = new ArrayList<HttpMessageInfo>();
	
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if (url.startsWith("/")){
			this.url = url;
		} else {
			this.url = "/" + url;
		}
	}

	public List<HttpMessageInfo> getHttpMessageInfoList() {
		return httpMessageInfoList;
	}

	public void setHttpMessageInfoList(List<HttpMessageInfo> httpMessageInfoList) {
		this.httpMessageInfoList = httpMessageInfoList;
	}
}
