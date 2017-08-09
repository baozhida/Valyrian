package com.xxx.utils.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.model.utils.db.CrawlDBCfg;
import com.xxx.model.utils.db.MysqlConcCfg;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.file.FileUtil;
import java.sql.PreparedStatement;

public class MysqlUtil {
    private Connection connection = null;
    
    public MysqlUtil(MysqlConcCfg cfg) {
    	try {
    		init(cfg.getUrl(), cfg.getUsername(), cfg.getPasswd());
		} catch (Exception e) {
			logger.error("Build mysql connection failed.");
        	e.printStackTrace();
		}
    }
    
    public MysqlUtil(String url, String username, String password) throws ClassNotFoundException, SQLException {
    	Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(url, username, password);
    }
    
    public void init(String url, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            logger.error("Build mysql jdbc connection failed.");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
            logger.info("Close mysql jdbc connection success");
        } catch (Exception e) {
            logger.error("Close mysql jdbc connection failed");
            e.printStackTrace();
        }
    }
    
    public boolean excute(String sql) {
        boolean flag = false;
        Statement statement = null;
        try {
            logger.info(sql);
            statement = connection.createStatement();
            flag = statement.execute(sql);
        } catch (Exception e) {
        	logger.error("Excute sql failed.");
        	e.printStackTrace();
        }
        finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                	logger.error("Close mysql statment failed");
                }
            }
        }
        return flag;
    }
    
    public List<Map<String, Object>> query(String keyname, String tableName, Map<String, Object> conditions) {
    	String sql = this.convertCrawlerDBCfgToQuerySql(keyname, tableName, conditions);
    	return query(sql);
    }
    
    public boolean update(String keyname, String tableName, Map<String, Object> setConditions, Map<String, Object> whereConditions) {
    	String sql = this.convertCrawlerDBCfgToUpdateSql(keyname, tableName, setConditions, whereConditions);
    	return excute(sql);
    }
	
    public List<Map<String, Object>> query(String sql) {
        Statement statement = null;
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        try {
            logger.info("Excute mysql sql: {}.", sql);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            mapList = resultSetToList(resultSet);
        } catch (Exception e) {
        	logger.error("Excute sql failed.");
        	e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                	logger.error("Close mysql statment failed");
                }
            }
        }
        return mapList;
    }
    
    public List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return Collections.emptyList();
        } else {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
            	Map<String, Object> rowData = new HashMap<String, Object>(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                	String key = metaData.getColumnName(i);
                	Object value = rs.getObject(i);
                    rowData.put(key, value);
                }
                list.add(rowData);
            }
            return list;
        }
    }
    
    public void crawlDataToFile(CrawlDBCfg cfg) {
    	List<Map<String, Object>> listAll =  this.query(cfg.getKeyspace(), cfg.getTableName(), cfg.getConditions());
    	
        String jsonString = "";
        for (int i = 0; i < listAll.size(); i++) {
            String str = ConvertUtil.toJson(listAll.get(i));
            if (i == listAll.size()) {
            	jsonString += str;
            } else {
            	jsonString += str + System.getProperty("line.separator");
            }
        }
        
        String filePath = cfg.getPath() + cfg.getTableName() + ".txt";
        FileUtil.createFileWithString(jsonString.trim(), filePath, "UTF-8", cfg.getPathType());
    }
    
    public String convertCrawlerDBCfgToQuerySql(String keyname, String tableName, Map<String, Object> conditions) {
    	String sql = "";
    	
    	StringBuilder sBuilder = new StringBuilder("select * from ");
    	sBuilder.append(keyname + "." + tableName + " ");
    	sBuilder.append("where ");
    	for (Map.Entry<String, Object> entry : conditions.entrySet()) {
    		if (entry.getValue() instanceof Object[]) {
    			Object[] valueArray = (Object[]) entry.getValue();
    			String str = "(";
    			for (Object obj : valueArray) {
    				str += "'" + obj + "',";
    			}
    			str = str.substring(0, str.length()-1) + ")";
    			sBuilder.append(entry.getKey() + " in " + str);
    		} else {
    			sBuilder.append(entry.getKey() + " = '" + entry.getValue() + "'");
    		}
    		sBuilder.append(" and ");
    	}
    	sql = sBuilder.substring(0, sBuilder.length() - 5);
    	sql += ";";
    	
    	return sql;
    }
    
    public String convertCrawlerDBCfgToUpdateSql(String keyname, String tableName, Map<String, Object> setConditions, Map<String, Object> whereconditions) {
    	String sql = "";
    	
    	StringBuilder sBuilder = new StringBuilder("update ");
    	sBuilder.append(keyname + "." + tableName + " ");
    	
    	sBuilder.append("set ");
    	for (Map.Entry<String, Object> entry : setConditions.entrySet()) {
    		sBuilder.append(entry.getKey() + " = '" + entry.getValue() + "'");
    		sBuilder.append(", ");
    	}
    	sBuilder.delete(sBuilder.length()-2, sBuilder.length()-1);
    	
    	sBuilder.append("where ");
    	for (Map.Entry<String, Object> entry : whereconditions.entrySet()) {
    		if (entry.getValue() instanceof Object[]) {
    			Object[] valueArray = (Object[]) entry.getValue();
    			String str = "(";
    			for (Object obj : valueArray) {
    				str += "'" + obj + "',";
    			}
    			str = str.substring(0, str.length()-1) + ")";
    			sBuilder.append(entry.getKey() + " in " + str);
    		} else {
    			sBuilder.append(entry.getKey() + " = '" + entry.getValue() + "'");
    		}
    		sBuilder.append(" and ");
    	}
    	sql = sBuilder.substring(0, sBuilder.length() - 5);
    	sql += ";";
    	
    	return sql;
    }
    
    public boolean insert(String tableName, Map<String, Object> map) {
    	if (map.size() > 0) {
    		String sql = convertMapToSql(tableName, map);
        	return excute(sql);
    	} else {
    		return true;
    	}
    }
    
    public long insert(String tableName, Map<String, Object> map, String incrementName) {
    	long autoid = 0;
    	String sql = convertMapToSql(tableName, map);
    	PreparedStatement statement = null;
    	if (!StringUtils.isEmpty(sql)) {
        	try {
        		logger.info(sql);
    			statement = connection.prepareStatement(sql, new String[] {incrementName});
    			int affectedRows = statement.executeUpdate();
    	        if (affectedRows == 0) {
    	            throw new SQLException("Creating user failed, no rows affected.");
    	        }

    	        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
    	            if (generatedKeys.next()) {
    	            	autoid = generatedKeys.getLong(1);
    	            }
    	            else {
    	                throw new SQLException("Creating user failed, no ID obtained.");
    	            }
    	        }
    		} catch (Exception e) {
            	logger.error("Excute sql failed.");
            	e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                    	statement.close();
                    } catch (Exception e) {
                    	logger.error("Close mysql statment failed");
                    }
                }
            }
    	}
    	
    	return autoid;
    }
    
    public static String convertMapToJsonStr(Map<String, Object> map) {
    	if (map.size() > 0) {
    		StringBuffer sb = new StringBuffer();
    		Set<String> keys = map.keySet();
    		List<String> pairs = new Vector<String>();
    		
    		keys.forEach(key -> {
    			String pair = null;
    			Object value = map.get(key);
    			if (value instanceof Boolean) {
    				if (value.equals(Boolean.FALSE)) {
    					value = 0;
    				} else {
    					value = 1;
    				}
    				pair = "\"" + key + "\": " + value + "";
    			} else {
    				pair = "\"" + key + "\": \"" + value + "\"";
    			} 
    			pairs.add(pair);
    		});
    		
    		sb.append("{");
    		pairs.forEach(pair -> {
    			sb.append(pair).append(",");
    		});
    		String jsonString = sb.substring(0, sb.length()-1);
    		
    		return jsonString + "}";
    	} else {
    		return null;
    	}
    }
    
    public static Map<String, Object> convertJsonStrToMap(String str) {
    	if (str.length() > 0) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		String[] pairArray = str.split(",");
    		for (int i = 0; i < pairArray.length; i++) {
    			String[] keyAndValue = new String[2];
    			
    			if (i == 0) {
    				keyAndValue = pairArray[i].trim().substring(1, pairArray[i].trim().length()).split(":");
    			} else if (i == (pairArray.length - 1)) {
    				keyAndValue = pairArray[i].trim().substring(0, pairArray[i].trim().length() - 1).split(":");
    			} else {
    				keyAndValue = pairArray[i].trim().split(":");
    			}
    			
    			String key = keyAndValue[0].trim().substring(1, keyAndValue[0].trim().length()-1);
    			String valueStr = keyAndValue[1].trim();
    			Object value = null;
				if (valueStr.startsWith("\"")) {
					value = valueStr.substring(1, valueStr.length()-1);
				} else if (valueStr.length() == 1) {
					try {
						value = Integer.valueOf(valueStr);
					} catch (Exception e) {
						logger.warn("Cant't convert string '{}' to integer.", valueStr);
					}
				} else {
					value = valueStr;
				}
				
    			map.put(key, value);
    		}
    		return map;
    	} else {
    		return null;
    	}
    }
    
    public static String convertMapToSql(String tableName, Map<String, Object> map) {
    	String sql = null;
    	if (map.size() > 0) {
        	
        	String keysStr = "";
        	String valuesStr = "";
        	Set<String> keys = map.keySet();
        	
    		for (String key : keys) {
    			Object value = map.get(key);
    			if (null != value) {
    				keysStr += key + ", ";
    				if (value instanceof String) {
    					valuesStr += "'" + value + "', ";
    				} else {
    					valuesStr += value + ", ";
    				}
    			}
    		}
    		sql = "INSERT INTO " + tableName + " (" + keysStr.substring(0, keysStr.length()-2) + ") VALUES (" + valuesStr.substring(0, valuesStr.length()-2) + ");";
    	}
    	
    	return sql;
    }
	
	private static final Logger logger = LoggerFactory.getLogger(MysqlUtil.class);
}
