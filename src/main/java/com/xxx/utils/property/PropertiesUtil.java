package com.xxx.utils.property;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
	public static PropertiesUtil getInstance(){
		return propertiesUtil;
	}
	
	public static Object get(String key) {
		return config.getProperty(key);
	}
	
	public static String getString(String key) {
		return config.getString(key);
	}
	
	public static String getString(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}
	
	public static int getInt(String key) {
		return config.getInt(key);
	}
	
	public static int getInt(String key, int defaultValue) {
		return config.getInt(key, defaultValue);
	}
	
	public static List<?> getList(String key) {
		return config.getList(key);
	}
	
	public static List<?> getList(String key, List<?> defaultValue) {
		return config.getList(key, defaultValue);
	}
	
	public static boolean getBoolean(String key) {
		return config.getBoolean(key);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		return config.getBoolean(key, defaultValue);
	}
	
	public static void set(String key, Object value) {
		try {
        	config.setProperty(key, value);
        	config.save();
        } catch (Exception e) {
        	logger.error("Can't read properties file :{}.", P_FILE_URL.getFile());
        	logger.error(e.getMessage());
        }
	}
	
	public static void set(String propertiesFilePath, String key, Object value) {
        try {
        	PropertiesConfiguration config = new PropertiesConfiguration(propertiesFilePath);
        	config.setProperty(key, value);
        	config.save();
        } catch (Exception e) {
        	logger.error("Can't read properties file :{}.", propertiesFilePath);
        	logger.error(e.getMessage());
        }
	}
	
	public static Object get(String propertiesFilePath, String key) {
		Object obj = "";
        try {
        	PropertiesConfiguration config = new PropertiesConfiguration(propertiesFilePath);
        	obj = config.getProperty(key);
        } catch (Exception e) {
        	logger.error("Can't read properties file :{}.", propertiesFilePath);
        	logger.error(e.getMessage());
        }
		
		return obj;
	}
	
	public static String replaceAllContext(String content, String propertiesFilePath) {
		content = replaceContext(content, propertiesFilePath);
		content = replaceContext(content, P_FILE_URL.getFile());
		return content;
	}
	
	public static String replaceContext(String content) {
		return replaceContext(content, P_FILE_URL.getFile());
	}
	
	@SuppressWarnings("unchecked")
	public static String replaceContext(String content, String propertiesFilePath) {
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(propertiesFilePath);
			Iterator<String> keys = config.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				String value = "";
				Object obj = config.getProperty(key);
				if (obj instanceof ArrayList) {
					for (String str : (ArrayList<String>) obj) {
						value += "\"" + str + "\",";
					}
					value = value.substring(0, value.length()-1);
				} else {
					value = (String) obj;
					if (cassandraListKeynames.contains(key)) {
						value = "\"" + value + "\"";
					}
				}
				String localCaontextKey = "${" + key + "}";
				content = content.replace(localCaontextKey, value);
			}
		} catch (ConfigurationException e) {
			logger.error("Can't read properties file :{}.", propertiesFilePath);
			logger.error(e.getMessage());
		}
		return content;
	}
	
	private PropertiesUtil() {
		P_FILE_URL = getClass().getClassLoader().getResource("application.properties");
		try {
			if (P_FILE_URL != null) {
				config = new PropertiesConfiguration(P_FILE_URL);
			} else {
				config = new PropertiesConfiguration("./src/main/resources/application.properties");
			}
		} catch (Exception e) {
			logger.error("Can't read properties file :{}.", P_FILE_URL.getFile());
			logger.error(e.getMessage());
		}
		cassandraListKeynames = new ArrayList<String>();
		cassandraListKeynames.add("cassandra.cardid");
		cassandraListKeynames.add("cassandra.billsummaryid");
	}
	
	private static PropertiesUtil propertiesUtil = new PropertiesUtil();
	private static URL P_FILE_URL;
	private static PropertiesConfiguration config;
	private static List<String> cassandraListKeynames;
	private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
}
