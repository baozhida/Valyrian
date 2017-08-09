package com.xxx.utils.replace;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.utils.convert.ConvertUtil;

public class ReplaceUtil {
	public static Object replaceAll(Object obj, Map<String, Object> contextMap) {
		Object object = null;
		if(obj != null && null != contextMap && contextMap.size() > 0) {
			try {
				if (obj instanceof String) {
					String str = obj.toString();
					object = replaceString(str, contextMap);
				} else if (obj instanceof Map) {
					object = replaceMap(obj, contextMap);
				} else if (obj instanceof List) {
					object = replaceList(obj, contextMap);
				} else {
					object = obj;
				}
			} catch (Exception e) {
				logger.error("Replace context failed.");
				e.printStackTrace();
			}	
		} 
		
		return object;
	}
	
	public static Object replaceString(String templateStr, Map<String, Object> contextMap) {
		Object object = null;
		if(templateStr != null && null != contextMap && contextMap.size() > 0) {
		    Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");  
		    StringBuffer newValue = new StringBuffer(templateStr.length());  
		    Matcher matcher = pattern.matcher(templateStr);
		    while (matcher.find()) {  
		        String key = matcher.group(1);
		        Object value = contextMap.get(key);
		        if (value != null) {
		        	/*
		        	 * 目前只实现了String类型，其他的基础类型待支持
		        	 */
		        	if (value instanceof String) {
		        		String valueStr = (String) value;
		        		logger.info("Get context from contextMap. Key is {}. Value is {}.", key, valueStr);
		        		matcher.appendReplacement(newValue, (String) valueStr.replaceAll("\\\\", "\\\\\\\\")); //替换windows下的文件目录在java里用\\表示
		        	} else if (value instanceof Map) {
		        		object = replaceMap(templateStr, contextMap);
		        		return object;
		        	} else if (value instanceof List) {
		        		object = replaceList(templateStr, contextMap);
		        		return object;
		        	} 
		        }
		    }
		    matcher.appendTail(newValue);
    		object = newValue.toString();
		} 
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> replaceMap(Object templateMap, Map<String, Object> contextMap) {
		Map<String, Object> map = null;
		if (templateMap != null && null != contextMap && contextMap.size() > 0) {
			if (templateMap instanceof String) {
				Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");   
			    Matcher matcher = pattern.matcher((String) templateMap);
			    while (matcher.find()) {  
			        String key = matcher.group(1);
			        if (contextMap.get(key) != null) {
			        	map = (Map<String, Object>) contextMap.get(key);
			        	logger.info("Get context from contextMap. Key is {}. Value is {}.", key, map.toString());
			        }
			    }
			} else if (templateMap instanceof Map) {
				map = (Map<String, Object>) templateMap;
				Set<Entry<String, Object>> entries = map.entrySet();
				Iterator<Entry<String, Object>> iterator = entries.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					String key = entry.getKey();
					Object value = entry.getValue();
					Object newValue = replaceAll(value, contextMap);
					map.put(key, newValue);
				}
			} else if (templateMap instanceof List) {
				logger.error("Wrong type list.");
			} else {
				logger.error("Wrong type.");
			}
		} 
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> replaceList(Object templateList, Map<String, Object> contextMap) {
		List<Object> listObjects = null;
		if (templateList != null && null != contextMap && contextMap.size() > 0) {
			if (templateList instanceof String) {
				Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");   
			    Matcher matcher = pattern.matcher((String) templateList);
			    while (matcher.find()) {  
			        String key = matcher.group(1);
			        if (contextMap.get(key) != null) {
			        	listObjects = (List<Object>) contextMap.get(key);
			        	logger.info("Get context from contextMap. Key is {}. Value is {}.", key, ConvertUtil.toJson(listObjects));
			        }
			    }
			} else if (templateList instanceof List) {
				listObjects = (List<Object>) templateList;
				for (int i = 0; i < listObjects.size(); i++) {
					Object object = listObjects.get(i);
					Object value = replaceAll(object, contextMap);
					listObjects.set(i, value);
				}
			} else {
				logger.error("Wrong type.");
			}
		}
		
		return listObjects;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ReplaceUtil.class);
}
