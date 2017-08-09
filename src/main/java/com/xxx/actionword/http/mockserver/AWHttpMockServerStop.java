package com.xxx.actionword.http.mockserver;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.actionword.basic.config.GlobalVariables;
import com.sun.net.httpserver.HttpServer;

public class AWHttpMockServerStop extends ActionWord{
	private int port;

	@Override
	public boolean compareExpectAndActual() {
		HttpServer server = (HttpServer) getContext(GlobalVariables.MOCK_SERVER_CONTEXT_NAME + port);
		int port = server.getAddress().getPort();
		try {
			server.stop(0);//停止HttpServer
	    	logger.info("[HttpMockServer] Stop the httpMockServer with port {} succesfully.", port);
		} catch (Exception e) {
			logger.error("[HttpMockServer] Stop the httpMockServer with port {} failed.", port);
		}
		return true;
	}

	@Override
	public void assignContext() {
		
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
