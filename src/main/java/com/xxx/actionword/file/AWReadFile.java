package com.xxx.actionword.file;

import java.util.List;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.utils.file.FileUtil;

public class AWReadFile extends ActionWord{
	private String filePath;
	private int typeOfReturn;//1:String 2:List<String>
	private int type;//1:绝对路径 2:resource path 3:project path
	private String contextKeyName;

	@Override
	public boolean compareExpectAndActual() {
		return true;
	}

	@Override
	public void assignContext() {
		if (typeOfReturn == 1) {
			String content  = FileUtil.toString(filePath, type);
			contextMap.put(contextKeyName, content);
		} else if (typeOfReturn == 2) {
			List<String> lines = FileUtil.readLines(filePath, type);
			contextMap.put(contextKeyName, lines);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}

	public int getTypeOfReturn() {
		return typeOfReturn;
	}

	public void setTypeOfReturn(int typeOfReturn) {
		this.typeOfReturn = typeOfReturn;
	}
}
