package com.xxx.model.actionword.http;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapComparedConfig {
	private List<String> primaryKeys;
	
	private int type;//1: assigned keys: 只对比set中的key  非1: ignored keys: 不对比set中的key
	
	private Set<String> keys;
	
	private Map<String, List<String>> keyNameAndPrimarykeys;
	
	private int contentType;//1:JSON 2:STRING

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}

	public Map<String, List<String>> getKeyNameAndPrimarykeys() {
		return keyNameAndPrimarykeys;
	}

	public void setKeyNameAndPrimarykeys(Map<String, List<String>> keyNameAndPrimarykeys) {
		this.keyNameAndPrimarykeys = keyNameAndPrimarykeys;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
}
