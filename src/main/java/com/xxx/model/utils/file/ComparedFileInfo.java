package com.xxx.model.utils.file;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComparedFileInfo {
	private String expectedFilePath;//预期结果数据文件路径
	
	private String actualFilePath;//实际结果数据文件路径
	
	private List<String> primaryKeys = new ArrayList<String>();//主键
	
	private Set<String> keys = new HashSet<String>();
	
	private Map<String, List<String>> keyNameAndPrimarykeys;//Map中类型为List<T>的keyname作为key，T的主键字段List<String>作为value
	
	private int type = 1;//1: assigned keys: 只对比set中的key  2: ignored keys: 不对比set中的key
	
	private int filePathType;//1:绝对路径 2:resource path 3:project path

	public Map<String, List<String>> getKeyNameAndPrimarykeys() {
		return keyNameAndPrimarykeys;
	}

	public void setKeyNameAndPrimarykeys(Map<String, List<String>> keyNameAndPrimarykeys) {
		this.keyNameAndPrimarykeys = keyNameAndPrimarykeys;
	}

	public String getExpectedFilePath() {
		return expectedFilePath;
	}

	public void setExpectedFilePath(String expectedFilePath) {
		this.expectedFilePath = expectedFilePath;
	}

	public String getActualFilePath() {
		return actualFilePath;
	}

	public void setActualFilePath(String actualFilePath) {
		this.actualFilePath = actualFilePath;
	}

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(List<String> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFilePathType() {
		return filePathType;
	}

	public void setFilePathType(int filePathType) {
		this.filePathType = filePathType;
	}
}
