package com.xxx.actionword.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xxx.actionword.basic.ActionWord;

public class AWFilterSqlLine extends ActionWord{
	private List<String> sqlList;
	private String contextKeyName;

	@Override
	public boolean compareExpectAndActual() {
		return true;
	}

	@Override
	public void assignContext() {
		if (null != sqlList && sqlList.size() > 0) {
			List<String> sqls = new ArrayList<String>();
			sqlList.forEach(sql -> {
				if (!sql.startsWith("//") && !StringUtils.isEmpty(sql)) {
					sqls.add(sql);
				}
			});
			setContext(contextKeyName, sqls);
		} else {
			logger.error("sqlList is null or size = 0.");
		}
	}

	public List<String> getSqlList() {
		return sqlList;
	}

	public void setSqlList(List<String> sqlList) {
		this.sqlList = sqlList;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}
}
