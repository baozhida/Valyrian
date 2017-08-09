package com.xxx.actionword.http.mockserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.actionword.basic.config.GlobalVariables;
import com.xxx.model.utils.http.HttpMockServerContext;
import com.xxx.utils.http.mockserver.HttpHandle;
import com.sun.net.httpserver.HttpServer;

public class AWHttpMockServerCreateContext extends ActionWord{
	private List<HttpMockServerContext> contextList = new ArrayList<HttpMockServerContext>();
	
	private int port;

	@Override
	public boolean compareExpectAndActual() {
		if (contextList.size() == 0) {
			logger.error("ContextList can't be empty. Start http mock server failed.");
		} else {
			HttpServer server = (HttpServer) getContext(GlobalVariables.MOCK_SERVER_CONTEXT_NAME + port);
			int port = server.getAddress().getPort();
			for (HttpMockServerContext context : contextList) {
				HttpHandle httpHandle = new HttpHandle(context);
		    	server.createContext(context.getUrl(), httpHandle);//创建URL上下文
		    	logger.info("[HttpMockServer] Create context of the httpMockServer {} succesfully.", port);
			}
			
			try {
				server.setExecutor(Executors.newCachedThreadPool());//TODO 线程池大小配置化
		        server.start(); //启动HttpServer
		        logger.info("[HttpMockServer] Start the httpMockServer with port {} succesfully.", port);
			} catch (Exception e) {
				logger.error("[HttpMockServer] Start the httpMockServer with port {} failed.", port);
			}
		}
		
		return true;
	}

	@Override
	public void assignContext() {
		
	}

	public List<HttpMockServerContext> getContextList() {
		return contextList;
	}

	public void setContextList(List<HttpMockServerContext> contextList) {
		this.contextList = contextList;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
