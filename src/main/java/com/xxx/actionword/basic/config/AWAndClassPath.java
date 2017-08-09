package com.xxx.actionword.basic.config;

public enum AWAndClassPath {
	//cache
	AWREDISCLIENT("com.xxx.actionword.cache.AWRedisClient", "AWRedisClient"),
	//common
	AWCONTEXTMANAGER("com.xxx.actionword.common.AWContextManager", "AWContextManager"),
	AWSLEEP("com.xxx.actionword.common.AWSleep", "AWSleep"),
	//db.cassandra
	AWCASSANDRACLOSE("com.xxx.actionword.db.cassandra.AWCassandraClose", "AWCassandraClose"),
	AWCASSANDRACRAWLDATATOFILE("com.xxx.actionword.db.cassandra.AWCassandraCrawlDataToFile", "AWCassandraCrawlDataToFile"),
	AWCASSANDRAEXCUTESQL("com.xxx.actionword.db.cassandra.AWCassandraExcuteSql", "AWCassandraExcuteSql"),
	AWCASSANDRAINIT("com.xxx.actionword.db.cassandra.AWCassandraInit", "AWCassandraInit"),
	//db.mysql
	AWMYSQLCLOSE("com.xxx.actionword.db.mysql.AWMysqlClose", "AWMysqlClose"),
	AWMYSQLCRAWLDATATOFILE("com.xxx.actionword.db.mysql.AWMysqlCrawlDataToFile", "AWMysqlCrawlDataToFile"),
	AWMYSQLEXCUTESQL("com.xxx.actionword.db.mysql.AWMysqlExcuteSql", "AWMysqlExcuteSql"),
	AWMYSQLINIT("com.xxx.actionword.db.mysql.AWMysqlInit", "AWMysqlInit"),
	//db
	AWFILTERSQLLINES("com.xxx.actionword.db.AWFilterSqlLine", "AWFilterSqlLine"),
	//file
	AWCOMPAREFILES("com.xxx.actionword.file.AWCompareFiles", "AWCompareFiles"),
	AWREADFILE("com.xxx.actionword.file.AWReadFile", "AWReadFile"),
	//http.client
	AWHTTPCLIENT("com.xxx.actionword.http.client.AWHttpClient", "AWHttpClient"),
	//http.server
	AWHTTPMOCKSERVERINIT("com.xxx.actionword.http.mockserver.AWHttpMockServerInit", "AWHttpMockServerInit"),
	AWHTTPMOCKSERVERCREATECONTEXT("com.xxx.actionword.http.mockserver.AWHttpMockServerCreateContext", "AWHttpMockServerCreateContext"),
	AWHTTPMOCKSERVERSTOP("com.xxx.actionword.http.mockserver.AWHttpMockServerStop", "AWHttpMockServerStop")
	;
	
	private String actionWordName;
	private String classPath;
	
	private AWAndClassPath(String classPath, String actionWordName) {
		this.actionWordName = actionWordName;
		this.classPath = classPath;
	}
	
	public static String getClassPathByName(String name) {
		for (AWAndClassPath aw : AWAndClassPath.values()) {
			if (aw.getActionWordName().equals(name)) {
				return aw.classPath;
			}
		}
		
		return null;
	}

	public String getActionWordName() {
		return actionWordName;
	}

	public void setActionWordName(String actionWordName) {
		this.actionWordName = actionWordName;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}
}
