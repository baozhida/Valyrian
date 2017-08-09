package com.xxx.actionword.db.mysql;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.utils.db.mysql.MysqlUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AWMysqlInit extends ActionWord{
	private String url;
	
	private String username;
	
	private String passwd;
	
	private String contextKeyName;
	
	@JsonIgnore
	private MysqlUtil mysqlUtil;

	@Override
	public boolean compareExpectAndActual() {
		try {
			mysqlUtil = new MysqlUtil(url, username, passwd);
		} catch (Exception e) {
			logger.error("Build mysql jdbc connection failed.");
			return false;
		}
		
		return true;
	}

	@Override
	public void assignContext() {
		setContext(contextKeyName, mysqlUtil);
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

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}
}
