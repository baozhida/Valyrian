package com.xxx.actionword.basic.dataprovider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import com.xxx.actionword.basic.AutoTestcase;
import com.xxx.actionword.basic.config.GlobalVariables;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.file.FileUtil;
import com.xxx.utils.property.PropertiesUtil;

public class DataProviderStrategy {
	public static void runCases(String filePath) {
		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
		String resourcePath = FileUtil.getIntergrationRootPath();
		File resoureceFile = new File(resourcePath + filePath);
		
		excute(resoureceFile, resultMap, typeOfDirectoryList, directoryList, priority);
		Set<String> keySet = resultMap.keySet();
		for (String key : keySet) {
			boolean result = resultMap.get(key);
			Assert.assertTrue(result);
		}
	}
	
	@DataProvider(name = "singleThread")
	public static Object[][] singleThread() {
		Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
		String resourcePath = FileUtil.getIntergrationRootPath();
		if (isActive) {
			File resoureceFile = new File(resourcePath);
			File[] fileList = resoureceFile.listFiles();
			for (File file : fileList) {
				excute(file, resultMap, typeOfDirectoryList, directoryList, priority);
			}
		}
		
		Object[][] result = mapToObjectArray(resultMap);
		return result;
	}
	
	@DataProvider(name = "multiThread")
	protected Object[][] multiThread() {
		Map<String, Boolean> resultMap = new ConcurrentHashMap<String, Boolean>();
		
		Object[][] result = mapToObjectArray(resultMap);
		return result;
	}
	
	public static void excute(File file, Map<String, Boolean> resultMap, int typeOfDirectoryList, List<String> directoryList, int priority) {
		if (file.isFile()) {
			String fileName = file.getName();
			if (fileName.endsWith(".at.json")) {
				String jsonStr = FileUtil.toString(file.getPath(), 1);
				AutoTestcase testcase = ConvertUtil.jsonToBean(jsonStr, AutoTestcase.class);
				boolean isActiveInside = testcase.getManualTestcase().isActive();
				int priorityInside = testcase.getManualTestcase().getPriority();
				if (isActiveInside &&  priorityInside <= priority) {
					logger.info("Start to excute testcase: ", file.getName());
					boolean result = testcase.excute();
					resultMap.put(file.getPath(), result);
				} else {
					logger.warn("This case do not need to excute. Priority :{}. IsActive :{}.", isActiveInside, priorityInside);
				}
			}
		} else if (file.isDirectory()) {
			String fileName = file.getName();
			boolean isIgnore = directoryList.contains(fileName);
			if (typeOfDirectoryList > 0) isIgnore = !isIgnore;
			if (file.getName().equals("com")) isIgnore = true;
			if (isIgnore) {
				logger.warn("This directory do not need excute. Name is {}.", fileName);
			} else {
				File[] fileList = file.listFiles();
				for (File fileInside : fileList) {
					excute(fileInside, resultMap, typeOfDirectoryList, directoryList, priority);
				}
			}
		} else {
			logger.error("This case file does not exist: {}.", file.getPath());
		}
	}
	
	public static Object[][] mapToObjectArray(Map<String, Boolean> map) {
		if (null != map && map.size() > 0) {
			int i = 0;
			Object[][] result = new Object[map.size()][2];
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				result[i][0] = key; 
				result[i][1] = map.get(key); 
				i++;
			}
			return result;
		} else {
			return new Object[0][0];
		}
	}
	
	public static DataProviderStrategy getInstance() {
		return dataProviderStrategy;
	}
	
	@SuppressWarnings("unchecked")
	private DataProviderStrategy() {
		directoryList = (List<String>) PropertiesUtil.getList(GlobalVariables.STRATEGY_DIR_LIST, new ArrayList<String>());
		typeOfDirectoryList = PropertiesUtil.getInt(GlobalVariables.STRATEGY_TYPE_OF_DIR_LIST, 0);
		priority = PropertiesUtil.getInt(GlobalVariables.STRATEGY_PRIORITY, 3);
		isActive = PropertiesUtil.getBoolean(GlobalVariables.STRATEGY_ISACTIVE, true);
	}
	
	private static DataProviderStrategy dataProviderStrategy = new DataProviderStrategy();
	private static List<String> directoryList;
	private static int typeOfDirectoryList;//0:ignore 1:among
	private static int priority;//1:high 2:middle 3:low 4:abandon
	private static boolean isActive;//all testcases excution on/off
	private static final Logger logger = LoggerFactory.getLogger(DataProviderStrategy.class);
}
