package com.xxx.actionword.file;

import java.util.ArrayList;
import java.util.List;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.model.utils.basic.Results;
import com.xxx.model.utils.file.ComparedFileInfo;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.file.FileUtil;

public class AWCompareFiles extends ActionWord{
	private int type;//1:从文件中读取配置 2:直接使用comparedFileInfos
	private List<ComparedFileInfo> comparedFileInfos;
	private String configFilePath;//resource file path
	private int filePathType;//1:绝对路径 2:resource path 3:project path

	@Override
	public boolean compareExpectAndActual() {
		init();
		List<Results> results = compareFile();
		boolean result = checkResultOfCompareFiles(results);
		return result;
	}

	@Override
	public void assignContext() {
		logger.info("Nothing need to put in context map.");
	}
	
	public void init() {
		if (type == 1) {
			String content = FileUtil.toString(configFilePath, filePathType);
			content = (String) replace(content);
			ComparedFileInfo[] comparedFileInfoArray = ConvertUtil.jsonToBean(content, ComparedFileInfo[].class);
			if (comparedFileInfoArray.length > 0) {
				comparedFileInfos = new ArrayList<ComparedFileInfo>();
				for (ComparedFileInfo info : comparedFileInfoArray) {
					comparedFileInfos.add(info);
				}
			}
 		} else {
 			logger.info("configFilePath is empty");
 		}
	}
	
	public List<Results> compareFile() {
        List<Results> resultsList = new ArrayList<Results>();
        if (null != comparedFileInfos && !comparedFileInfos.isEmpty()) {
            for (ComparedFileInfo fileCommonInfo : comparedFileInfos) {
            	Results results = FileUtil.compareFileWithoutModel(fileCommonInfo);
                resultsList.add(results);
            }
        }

        return resultsList;
    }

    public boolean checkResultOfCompareFiles(List<Results> resultsList) {
        boolean finaleResult = true;

        if (null == resultsList || !resultsList.isEmpty()) {
            for (Results results : resultsList) {
            	int size = results.getResultItems().size();
            	if (size > 0) {
                    if (results.isResult()) {
                        finaleResult = true;
                    } else {
                        finaleResult = false;
                        break;
                    }
            	}
            }
        }

        return finaleResult;
    }

	public List<ComparedFileInfo> getComparedFileInfos() {
		return comparedFileInfos;
	}

	public void setComparedFileInfos(List<ComparedFileInfo> comparedFileInfos) {
		this.comparedFileInfos = comparedFileInfos;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public int getFilePathType() {
		return filePathType;
	}

	public void setFilePathType(int filePathType) {
		this.filePathType = filePathType;
	}
}
