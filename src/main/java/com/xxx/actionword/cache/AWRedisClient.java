package com.xxx.actionword.cache;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.utils.cache.RedisUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AWRedisClient extends ActionWord{
	private Object address;
	
	private int type;//1:集群 2:单机
	
	private String command;//命令名称，比如add,set,hset
	
	private String key;
	
	private String[] params;
	
	private Object expectedValue;
	
	private String contextKeyName;
	
	@JsonIgnore
	private Object value;

	@Override
	public boolean compareExpectAndActual() {
		boolean result = false;
		if (null != address) {
			RedisUtil redisUtil = new RedisUtil(address);
			
			try {
				switch (command) {
				case "sadd":
					value = redisUtil.sadd(key, params);
					break;
				case "spop":
					value = redisUtil.spop(key);
					break;
				case "lpush":
					value = redisUtil.lpush(key, params);
					break;
				case "lpop":
					value = redisUtil.lpop(key);
					break;
				case "set":
					value = redisUtil.set(key, params[0]);
					break;
				case "get":
					value = redisUtil.get(key);
					break;
				case "del":
					value = redisUtil.del(key);
					break;
				case "hset":
					value = redisUtil.hset(key, params[0], params[1]);
					break;
				case "hget":
					value = redisUtil.hget(key, params[0]);
					break;
				default:
					logger.error("Do not surpport this command {}", command);
					break;
				}
			} catch (Exception e) {
				logger.error("Excute redis command failed.");
			} finally {
				redisUtil.close();
			}
			
			if (value instanceof Long) {
				result = true;
			} else if (value instanceof String) {
				if (null != expectedValue) {
					result = expectedValue.equals(value);
				} else {
					result = true;
					logger.warn("Expected value is null. No need checkout.");
				}
			}
		} else {
			logger.error("Address can not be empty");
		}
		
		return result;
	}

	@Override
	public void assignContext() {
		setContext(contextKeyName, value);
	}

	public Object getAddress() {
		return address;
	}

	public void setAddress(Object address) {
		this.address = address;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(Object expectedValue) {
		this.expectedValue = expectedValue;
	}
}
