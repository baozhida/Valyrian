package com.xxx.model.utils.http;

import java.util.HashMap;

public class HttpMockServerContextResponse {
	private int stateCode;
	
	private HashMap<String, String> headers = new HashMap<String, String>();
	
	private int contentTypeOfResponseBody;
	
	private Object responseBody;
	
	public HttpMockServerContextResponse() {
		
	}
	
	public HttpMockServerContextResponse(int stateCode, Object reponseBody) {
		this.stateCode = stateCode;
		this.responseBody = reponseBody;
	}

	public int getStateCode() {
		return stateCode;
	}

	public void setStateCode(int stateCode) {
		this.stateCode = stateCode;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public int getContentTypeOfResponseBody() {
		return contentTypeOfResponseBody;
	}

	public void setContentTypeOfResponseBody(int contentTypeOfResponseBody) {
		this.contentTypeOfResponseBody = contentTypeOfResponseBody;
	}

	public Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}
}
