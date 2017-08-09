package com.xxx.model.utils.db;

public class MysqlConcCfg {
	private String url;
	
	private String username;
	
	private String passwd;
	
	public MysqlConcCfg() {
		
	}
	
	public MysqlConcCfg(String url, String username, String passwd) {
		this.url = url;
		this.username = username;
		this.passwd = passwd;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
