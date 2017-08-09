package com.xxx.utils.basic;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.model.utils.basic.Result;
import com.xxx.model.utils.basic.Results;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.db.mysql.MysqlUtil;
import com.xxx.utils.file.FileUtil;

import net.sf.json.JSONNull;

public class AssertUtil {
    public static Results assertObject(Object actual, Object expect, Results results) {
    	return assertObject(actual, expect, null, null, null, 2, results);
    }
    
    @SuppressWarnings("unchecked")
    public static Results assertObject(Object actual, Object expect, List<String> primaryKeys, Set<String> keys, Map<String, List<String>> keyNameAndPrimarykeys, int type, Results results) {
    	if (expect == null) {
    		logger.debug("Expected object is null. Set compred result true.");
            results.setResult(true);
            return results;
    	} else {
    		if (expect instanceof Map) {
        		return assertMap((Map<String, Object>) actual, (Map<String, Object>) expect, keys, keyNameAndPrimarykeys, type, results);
        	} else if (expect instanceof List) {
        		return assertList((List<Object>) actual, (List<Object>) expect, primaryKeys, keys, keyNameAndPrimarykeys, type, results);
        	} else if (expect instanceof String) {
        		String expectedString = (String) expect;
        		if (expectedString.startsWith("[") && expectedString.endsWith("]")) {
        			return results;
        		} else {
        			Map<String, Object> mapActual  = MysqlUtil.convertJsonStrToMap((String) actual);
        			Map<String, Object> mapExpected = MysqlUtil.convertJsonStrToMap(expectedString);
        			return assertMap(mapActual, mapExpected, keys, keyNameAndPrimarykeys, type, results);
        		}
        	} else {
        		Map<String, Object> mapActual = ConvertUtil.toMap(actual);
        		Map<String, Object> mapExpected = ConvertUtil.toMap(expect);
        		return assertMap(mapActual, mapExpected, keys, keyNameAndPrimarykeys, type, results);
        	}
    	}
    }
    
    public static Results assertMap(Map<String, Object> actual, Map<String, Object> expect, Results results) {
        return assertMap(actual, expect, null, null, 2, results);
    }
    
    /*
     * type
     * 1: assigned keys: 只对比set中的key
     * 2: ignored keys: 不对比set中的key
     */
    @SuppressWarnings("unchecked")
	public static Results assertMap(Map<String, Object> actual, Map<String, Object> expect, Set<String> keys, Map<String, List<String>> keyNameAndPrimarykeys, int type, Results results) {
    	if (expect == null || expect.size() == 0) {
            logger.debug("Expected map is null or size is 0. Set compred result true.");
            results.setResult(true);
            return results;
        }
    	
    	Iterator<Map.Entry<String, Object>> iterator = expect.entrySet().iterator();
        while (iterator.hasNext()) {
        	Map.Entry<String, Object> next = iterator.next();
        	String specifiedKey = next.getKey().toString();
        	boolean isNeedCheck = false;
        	if (null != keys) isNeedCheck = keys.contains(specifiedKey);
        	if (type != 1) isNeedCheck = !isNeedCheck;
            if (isNeedCheck) {	
            	Result result = new Result();
            	if (next.getValue().equals(JSONNull.getInstance())) next.setValue(null);
            	if (null == next.getValue() || instanceOfBasicType(next.getValue())) {
            		if (null != next.getValue() && null != actual.get(next.getKey())) {
            			Object actualValue = actual.get(next.getKey());
        				Object expectedValue = next.getValue();
        				if (actualValue instanceof Number && expectedValue instanceof Number) {//Int/Long/Float 都统一转化为Double来对比
        					if (actualValue instanceof Integer) actualValue = ((Integer)actualValue).doubleValue();
        					if (expectedValue instanceof Integer) expectedValue = ((Integer)expectedValue).doubleValue();
        					
        					if (actualValue instanceof Long) actualValue = ((Long)actualValue).doubleValue();
        					if (expectedValue instanceof Long) expectedValue = ((Long)expectedValue).doubleValue();
        				}
        				boolean resultInside = expectedValue.equals(actualValue);
                        result.setResult(resultInside);
                    } else if (null == next.getValue()) {
                    	result.setResult(true);
                    } else {
                    	result.setResult(false);
                    }
                } else if (next.getValue() instanceof List) {
                	List<?> list = (List<?>) next.getValue();
               	 	if (list.size() == 0) {
               	 		result.setResult(true);
               	 	} else {
               	 		List<Object> expectedList = ConvertUtil.jsonToList(next.getValue());
               	 		List<Object> actualList = ConvertUtil.jsonToList(actual.get(next.getKey()));
               	 		List<String> primaryKeys = null;
               	 		if (null != keyNameAndPrimarykeys && keyNameAndPrimarykeys.size() > 0) primaryKeys = keyNameAndPrimarykeys.get(next.getKey());
               	 		result.setResult(assertList(actualList, expectedList, primaryKeys, keys, keyNameAndPrimarykeys, type, results).isResult());
               	 	}
                } else {
                	Map<String, Object> map = (Map<String, Object>) next.getValue();
               	 	if (map == null || map.size() == 0) {
               	 		result.setResult(true);
               	 	} else {
               	 		result.setResult(assertMap(ConvertUtil.toMap(actual.get(next.getKey())), ConvertUtil.toMap(next.getValue()), keys, keyNameAndPrimarykeys, type, results).isResult()); 
               	 	}
                }
            	result.setActual(actual.get(next.getKey()));
                result.setExpect(next.getValue());
                result.setKey(next.getKey());
                results.getResultItems().add(result);
                results.setResultByItems();
                
                if (!result.isResult()) {
                	logger.error(result.toString());
                }
            }
        }
        
        return results;
    }

    public static Results assertList(List<Object> actual, List<Object> expect, List<String> primaryKeys, Results results) {
    	return assertList(actual, expect, primaryKeys, null, null, 2, results);
    }
    
    @SuppressWarnings("unchecked")
	public static Results assertList(List<Object> actual, List<Object> expect, List<String> primaryKeys, Set<String> keys, Map<String, List<String>> keyNameAndPrimarykeys, int type, Results results) {
    	if (expect == null || expect.size() == 0) {
            logger.info("Expected list is null or size is 0. Set compred result true.");
            results.setResult(true);
            return results;
        }
    	
    	if (actual.size() == 0) {
    		logger.error("Size of actual list is zero.");
    		results.setResult(false);
            return results;
    	}
    	
        for (int i = 0; i < expect.size(); i++) {
        	Result result = new Result();
            if (expect.get(i) instanceof Map) {
            	if (null == primaryKeys || primaryKeys.size() == 0) {
            		//没有设置主键，按照index对比
            		logger.warn("Primary keys is empty. Will compare list<object> by index.");
            		Map<String, Object> expectedMap = ConvertUtil.toMap(expect.get(i));
            		Map<String, Object> actualMap = ConvertUtil.toMap(actual.get(i));
            		Results resultsInside = new Results();
       			 	result.setResult(assertMap(actualMap, expectedMap, keys, keyNameAndPrimarykeys, type, resultsInside).isResult());
       			 	if (!result.isResult()) logger.error(resultsInside.toString());
            	} else {
                	Map<String, Object> expectedMap = ConvertUtil.toMap(expect.get(i));
                	List<Object> primaryValuesExpect = FileUtil.getValuesByKeysFromMap(expectedMap, primaryKeys);
                	for (int j = 0; j < actual.size(); j++) {
                		Map<String, Object> actualMap = ConvertUtil.toMap(actual.get(j));
                		List<Object> primaryValuesActual = FileUtil.getValuesByKeysFromMap(actualMap, primaryKeys);
                		 if (AssertUtil.assertList(primaryValuesActual, primaryValuesExpect, null, new Results()).isResult()) {
                			 Results resultsInside = new Results();
                			 result.setResult(assertMap(actualMap, expectedMap, keys, keyNameAndPrimarykeys, type, resultsInside).isResult());
                			 if (!result.isResult()) logger.error(resultsInside.toString());
                			 break;
                		 }
                	}
            	}
            } else if (expect.get(i) instanceof List) {
                result.setResult(assertList((List<Object>) actual.get(i), (List<Object>) expect.get(i), primaryKeys, results).isResult());
            } else {
                if (null != expect.get(i) && null != actual.get(i)) {
                    result.setResult(actual.get(i).equals(expect.get(i)));
                } else if (null == expect.get(i)){
                	result.setResult(true);
                } else {
                	result.setResult(false);
                }
            }

            result.setActual(actual.get(i));
            result.setExpect(expect.get(i));
            result.setKey("");
            results.getResultItems().add(result);
            results.setResultByItems();
            if (!result.isResult()) {
            	logger.error(result.toString());
            }
        }
        
        return results;
    }

    public static boolean instanceOfBasicType(Object object) {
    	boolean result = false;
    	
    	if (object instanceof Boolean
    			|| object instanceof Byte
    			|| object instanceof Character
    			|| object instanceof Double
    			|| object instanceof Float
    			|| object instanceof Integer
    			|| object instanceof Long
    			|| object instanceof Short
    			|| object instanceof String
    			|| object instanceof Date
    			|| object instanceof UUID) {
    		result = true;
    	}
    	
    	return result;
    }
    
    public static final Logger logger = LoggerFactory.getLogger(AssertUtil.class);
}
