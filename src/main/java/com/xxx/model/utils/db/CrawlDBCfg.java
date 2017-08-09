package com.xxx.model.utils.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CrawlDBCfg {
	private int type;//1:mysql 2:cassandra
	
	private String keyspace;
	
	private String tableName;
	
	private Set<String> valueIsUUIDKeyList = new HashSet<String>();
	
	private Map<String, Object> conditions = new HashMap<String, Object>();
	
	private String path;
	
	private int pathType;//1:绝对路径 2:resource path 3:project path
	
	public CrawlDBCfg() {
		
	}
	
	public CrawlDBCfg(int type, String keyspace, String tableName, Set<String> valueIsUUIDKeyList, Map<String, Object> conditions, String path) {
		this.type =  type;
		this.keyspace = keyspace;
		this.tableName = tableName;
		this.valueIsUUIDKeyList = valueIsUUIDKeyList;
		this.conditions = conditions;
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Set<String> getValueIsUUIDKeyList() {
		return valueIsUUIDKeyList;
	}

	public void setValueIsUUIDKeyList(Set<String> valueIsUUIDKeyList) {
		this.valueIsUUIDKeyList = valueIsUUIDKeyList;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getKeyspace() {
		return keyspace;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getConditions() {
		return conditions;
	}

	public void setConditions(Map<String, Object> conditions) {
		this.conditions = conditions;
	}

	public int getPathType() {
		return pathType;
	}

	public void setPathType(int pathType) {
		this.pathType = pathType;
	}
}
