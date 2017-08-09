package com.xxx.actionword.http.mockserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.actionword.basic.config.GlobalVariables;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.net.httpserver.HttpServer;

public class AWHttpMockServerInit extends ActionWord{
	private int port;
	
	@JsonIgnore
	private HttpServer server;

	@Override
	public boolean compareExpectAndActual() {
		InetSocketAddress addr = new InetSocketAddress(port);//监听端口
		try {
			server = HttpServer.create(addr, 0);//创建HttpServer
			logger.info("[HttpMockServer] Create a new httpMockServer with port [" + this.port + "].");
		} catch (IOException e) {
			logger.error("[HttpMockServer] Create a new httpMockServer failed. Please check the port [" + this.port + "] whethear in use.");
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void assignContext() {
		setContext(GlobalVariables.MOCK_SERVER_CONTEXT_NAME + port, server);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
