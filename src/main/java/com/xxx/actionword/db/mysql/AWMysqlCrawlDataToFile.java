package com.xxx.actionword.db.mysql;

import java.util.Arrays;
import java.util.List;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.model.utils.db.CrawlDBCfg;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.db.mysql.MysqlUtil;
import com.xxx.utils.file.FileUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AWMysqlCrawlDataToFile extends ActionWord{
	private int type;//1:从文件中读取配置 2:直接使用cfgList
	
	private List<CrawlDBCfg> cfgList;
	
	private String configFilePath;
	
	private String contextKeyName;
	
	private int filePathType;//1:绝对路径 2:resource path 3:project path
	
	@JsonIgnore
	private MysqlUtil mysqlUtil;

	@Override
	public boolean compareExpectAndActual() {
		mysqlUtil = (MysqlUtil) getContext(contextKeyName);
		init();
		if (null != mysqlUtil) {
			cfgList.forEach(cfg -> {
				mysqlUtil.crawlDataToFile(cfg);
			});
		} else {
			logger.error("Excute sql failed. Please init mysql connection first.");
			return false;
		}
		
		return true;
	}

	@Override
	public void assignContext() {
		logger.info("Nothing need to put in context map.");
	}
	
	public void init() {
		if (type == 1) {
			String content = FileUtil.toString(configFilePath, filePathType);
			content = (String) replace(content);
			CrawlDBCfg[] cfgArray = ConvertUtil.jsonToBean(content, CrawlDBCfg[].class);
			cfgList = Arrays.asList(cfgArray);
		} else {
 			logger.info("configFilePath is empty");
 		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<CrawlDBCfg> getCfgList() {
		return cfgList;
	}

	public void setCfgList(List<CrawlDBCfg> cfgList) {
		this.cfgList = cfgList;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}

	public int getFilePathType() {
		return filePathType;
	}

	public void setFilePathType(int filePathType) {
		this.filePathType = filePathType;
	}
}
