package com.xxx.actionword.db.mysql;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.utils.db.mysql.MysqlUtil;

public class AWMysqlClose extends ActionWord{
	private String contextKeyName;

	@Override
	public boolean compareExpectAndActual() {
		MysqlUtil mysqlUtil = (MysqlUtil) getContext(contextKeyName);
		mysqlUtil.close();
		removeContext(contextKeyName, mysqlUtil);
		return true;
	}

	@Override
	public void assignContext() {
		
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}
}
