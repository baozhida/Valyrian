package com.xxx.actionword.common;

import com.xxx.actionword.basic.ActionWord;

public class AWContextManager extends ActionWord {
	private String key;
	
	private Object value;

	@Override
	public boolean compareExpectAndActual() {
		return true;
	}

	@Override
	public void assignContext() {
		setContext(key, value);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
