package com.xxx.utils.http.mockserver;

import com.xxx.model.utils.http.HttpMockServerContext;
import com.xxx.model.utils.http.HttpMockServerConfig;
import com.sun.net.httpserver.HttpServer;   

import java.io.IOException;   
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class HttpMockServerUtil {	
    private HttpServer server;
    
    private HttpMockServerConfig httpConfig;
    
    private int port;
    
    public HttpMockServerUtil() {
    	
    }
    
    public HttpMockServerUtil(HttpMockServerConfig httpConfig) {
    	this.setHttpConfig(httpConfig);
    }
    
    public void initServer(int port) {
    	InetSocketAddress addr = new InetSocketAddress(port);//监听端口
    	this.port = port;
    	try {
			server = HttpServer.create(addr, 0);//创建HttpServer
			logger.info("[HttpMockServer] Create a new httpMockServer with port [" + this.port + "].");
		} catch (IOException e) {
			logger.error("[HttpMockServer] Create a new httpMockServer failed. Please check the port [" + this.port + "] whethear in use.");
			e.printStackTrace();
		}
    }
    
    public void createContext(HttpMockServerContext httpContext) {
    	HttpHandle httpHandle = new HttpHandle(httpContext);
    	server.createContext(httpContext.getUrl(), httpHandle);//创建URL上下文
    	logger.info("[HttpMockServer] Create context of the httpMockServer with port [" + this.port + "] succesfully.");
    }
    
    public void start() {
    	server.setExecutor(Executors.newCachedThreadPool());//初始化连接池
        server.start(); //启动HttpServer
        logger.info("[HttpMockServer] Start the httpMockServer with port [" + this.port + "] succesfully.");
    }
    
    public void stop() {
    	server.stop(0);//停止HttpServer
    	logger.info("[HttpMockServer] Stop the httpMockServer with port [" + this.port + "] succesfully.");
    }

	public HttpServer getServer() {
		return server;
	}

	public void setServer(HttpServer server) {
		this.server = server;
	}

	public HttpMockServerConfig getHttpConfig() {
		return httpConfig;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHttpConfig(HttpMockServerConfig httpConfig) {
		this.httpConfig = httpConfig;
		this.initServer(httpConfig.getPort());
		for (HttpMockServerContext httpContext : httpConfig.getHttpContexts()) {
			this.createContext(httpContext);
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(HttpMockServerUtil.class);
} 
