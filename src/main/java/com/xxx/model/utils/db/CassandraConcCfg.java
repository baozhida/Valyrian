package com.xxx.model.utils.db;

public class CassandraConcCfg {
	private Object address;
	
	private String username;
	
	private String passwd;
	
	private int type;//0:单机 1:集群
	
	public CassandraConcCfg() {
		
	}
	
	public CassandraConcCfg(Object address, String username, String passwd, int type) {
		this.address = address;
		this.username = username;
		this.passwd = passwd;
		this.type = type;
	}

	public Object getAddress() {
		return address;
	}

	public void setAddress(Object address) {
		this.address = address;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
