package com.xxx.actionword.db.mysql;

import java.util.List;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.utils.db.mysql.MysqlUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AWMysqlExcuteSql extends ActionWord{
	private String contextKeyName;
	
	private List<String> sqlList;
	
	@JsonIgnore
	private MysqlUtil mysqlUtil;

	@Override
	public boolean compareExpectAndActual() {
		mysqlUtil = (MysqlUtil) getContext(contextKeyName);
		if (null != mysqlUtil) {
			sqlList.forEach(sql -> {
				mysqlUtil.excute(sql);
			});
		} else {
			logger.error("Excute sql failed. Please init mysql connection first.");
			return false;
		}
		
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

	public List<String> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<String> sqlList) {
		this.sqlList = sqlList;
	}
}
