package com.xxx.utils.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.model.utils.basic.Result;
import com.xxx.model.utils.basic.Results;
import com.xxx.model.utils.file.ComparedFileInfo;
import com.xxx.utils.basic.AssertUtil;
import com.xxx.utils.convert.ConvertUtil;

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class FileUtil {
	public static void createFileWithString(String content, String filePath, int type) {
        createFileWithString(content, filePath, "UTF-8", type);
    }

    public static void createFileWithString(String content, String filePath, String charset, int type) {
        try {
        	switch (type) {//1:绝对路径 2:resource path 3:project path
        	case 2:
        		String pathOfResources = new FileUtil().getClass().getClassLoader().getResource("").getPath();
        		filePath = pathOfResources + filePath;
        		break;
        	case 3:
        		String pathOfProject = System.getProperty("user.dir");
        		filePath = pathOfProject + "/" + filePath;
        		break;
        	}
            PrintWriter writer = new PrintWriter(new File(filePath), charset);
            writer.println(content);
            writer.close();
        } catch (FileNotFoundException e) {
            logger.error("File is not found. File path is \"" + filePath + "\".");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    public static void appendFileWithString(String filePath, String content) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();
        } catch (FileNotFoundException e) {
            logger.error("File is not found. File path is \"" + filePath + "\".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String toString(String filePath, String encoding, int type) {
        String result = "";
        try {
        	switch (type) {//1:绝对路径 2:resource path 3:project path 4:integration
        	case 1:
        		byte[] encoded = Files.readAllBytes(Paths.get(filePath));
                result = new String(encoded, encoding);
                break;
        	case 2:
        		InputStream is = new FileUtil().getClass().getClassLoader().getResourceAsStream(filePath);
    			result = IOUtils.toString(is);
    			break;
        	case 3:
        		String pathOfProject = System.getProperty("user.dir");
        		filePath = pathOfProject + "/" + filePath;
        		byte[] encodedProject = Files.readAllBytes(Paths.get(filePath));
                result = new String(encodedProject, encoding);
                break;
        	case 4:
        		String resourcePath = getIntergrationRootPath();
        		byte[] encodedIntegration = Files.readAllBytes(Paths.get(resourcePath + filePath));
                result = new String(encodedIntegration, encoding);
        	}
            
        } catch (IOException e) {
        	logger.error("Read file failed. File path is {}.", filePath);
            e.printStackTrace();
        }
        return result;
    }

    public static String toString(String filePath, int type) {
        return toString(filePath, "UTF-8", type);
    }

    public static List<String> readLines(String filePath, int type) {
        return readLines(filePath, "UTF-8", type);
    }

    public static List<String> readLines(String filePath, String encoding, int type) {
        List<String> result = new ArrayList<String>();
        try {
        	switch (type) {//1:绝对路径 2:resource path 3:project path 4:integration
        	case 1:
//        		result = Files.readAllLines(Paths.get(filePath), Charset.forName(encoding));
        		break;
        	case 2:
        		String path = new FileUtil().getClass().getClassLoader().getResource("").getPath();
        		filePath = path + filePath;
            	break;
        	case 3:
        		String pathOfProject = System.getProperty("user.dir");
        		filePath = pathOfProject + "/" + filePath;
        		break;
        	case 4:
        		String resourcePath = getIntergrationRootPath();
        		filePath = resourcePath + filePath;
        	}
        	result = Files.readAllLines(Paths.get(filePath), Charset.forName(encoding));
        } catch (IOException e) {
        	logger.error("File not found. File path is {}.", filePath);
            e.printStackTrace();
        }
        return result;
    }
    
    public static Results compareFileWithoutModel(ComparedFileInfo fileCommonInfo) {
        Results results = new Results();
        int count = 0;
        
        List<String> expectedLines = FileUtil.readLines(fileCommonInfo.getExpectedFilePath(), "UTF-8", fileCommonInfo.getFilePathType());
        List<String> actualLines = FileUtil.readLines(fileCommonInfo.getActualFilePath(), "UTF-8", fileCommonInfo.getFilePathType());
        List<Map<String, Object>> mapsExpected = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> mapsActual = new ArrayList<Map<String, Object>>();
        
        for (String line : expectedLines) {
        	Map<String, Object> map = ConvertUtil.jsonToMap(line);
        	mapsExpected.add(map);
        }
        for (String line : actualLines) {
        	Map<String, Object> map = ConvertUtil.jsonToMap(line);
        	mapsActual.add(map);
        }
        
        logger.info("Sum of lines in expected:{}, actual:{}, expected file path is {}.", mapsExpected.size(), actualLines.size(), fileCommonInfo.getExpectedFilePath());

        if (expectedLines.size() == 1 && expectedLines.get(0).isEmpty()) {
        	logger.warn("Thers is no lines in expected file " + fileCommonInfo.getExpectedFilePath() + ". Set result true.");
        	count++;
        	Result result = new Result();
        	result.setResult(true);
            results.getResultItems().add(result);
            results.setResultByItems();
        } else {
            if (actualLines.size() == 1 && actualLines.get(0).isEmpty()) {
            	logger.error("Thers is no lines in actual file: {}.", fileCommonInfo.getActualFilePath());
            } else {
            	for (int i = 0; i < mapsExpected.size(); i++) {
                    int amountOfCompareTime = 0;
                    int sizeOfActualMap = mapsActual.size();
                    Result result = new Result();
                    Map<String, Object> expectedMap = mapsExpected.get(i);
                    Map<String, Object> actualMap = null;
                    List<Object> primaryValuesExpect = FileUtil.getValuesByKeysFromMap(expectedMap, fileCommonInfo.getPrimaryKeys());
                    for (int j = 0; j < mapsActual.size(); j++) {
                        actualMap = mapsActual.get(j);
                        List<Object> primaryValuesActual = FileUtil.getValuesByKeysFromMap(actualMap, fileCommonInfo.getPrimaryKeys());
                        if (AssertUtil.assertList(primaryValuesActual, primaryValuesExpect, null, new Results()).isResult()) {
                            Results resultsInside = new Results();
                            Set<String> keys = fileCommonInfo.getKeys();
                            Map<String, List<String>> keyNameAndPrimarykeys = fileCommonInfo.getKeyNameAndPrimarykeys();
                            int type = fileCommonInfo.getType();
                            boolean resultOfAssertMap = AssertUtil.assertMap(actualMap, expectedMap, keys, keyNameAndPrimarykeys, type, resultsInside).isResult();
                            if (resultOfAssertMap) {
                                result.setResult(true);
                                result.setKey("expectedFileLineNumber:"+i);
                                result.setExpect(expectedMap);
                                result.setActual(actualMap);
                                results.getResultItems().add(result);
                                results.setResultByItems();
                                mapsActual.remove(j);
                                count++;
                                break;
                            } else {
                            	logger.warn("Compare failed. The line number in expected:{}, actual:{}. " + resultsInside.toString(), i+1, j+1);
                            }
                        } else {
                            amountOfCompareTime++;
                        }
                    }

                    if (!result.isResult() && (amountOfCompareTime == sizeOfActualMap || sizeOfActualMap == 0)) {
                    	result.setResult(false);
                        result.setKey("expectedFileLineNumber:"+i);
                        result.setExpect(expectedMap);
                        result.setActual(actualMap);
                        results.getResultItems().add(result);
                        results.setResultByItems();
                        logger.error("This line didn't find the same in actual file. The line number of expect file [{}] is " + (i+1) + ".", fileCommonInfo.getExpectedFilePath());
                    }
                }
            }
        }

        if (count != expectedLines.size()) {
        	Result result = new Result();
        	result.setResult(false);
            result.setKey("sumOfSameLines");
            result.setExpect(expectedLines.size());
            result.setActual(count);
            results.getResultItems().add(result);
            results.setResultByItems();
            logger.error("Expected file path is " + fileCommonInfo.getExpectedFilePath() + ". Sum of line in expected file: "
            		+ expectedLines.size() + ". Sum of same line in actual file: " + count + ".");
        }

        return results;
    }
    
    public static List<Object> getValuesByKeysFromMap(Map<String, Object> map, List<String> keys) {
    	List<Object> values = new ArrayList<Object>();
    	if (keys.equals(null) || keys.size() == 0) {
    		logger.error("Method:[getValuesByKeysFromMap] param keys is null. Please checkout the compaired config.");
    	} else {
        	for (String key : keys) {
    			Object value = map.get(key);
    			values.add(value);
    		}
    	}
    	
    	return values;
    }
    
    public static String getIntergrationRootPath() {
    	String resourcePath = new FileUtil().getClass().getClassLoader().getResource("").getPath();
		if (resourcePath.contains("target") && resourcePath.contains(".jar")) {
			resourcePath = "../src/main/resources/";
		} else {
			resourcePath = "./src/main/resources/";
		}
		
		return resourcePath;
    }
    
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
}
