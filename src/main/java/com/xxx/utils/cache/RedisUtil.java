package com.xxx.utils.cache;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPoolConfig;

/*
 * Author 杜亮亮
 * 2016.8.31
 */
public class RedisUtil {
	private JedisCommands jedisCommands;
	
	private JedisPoolConfig jedisPoolConfig;
	
	private Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
	
	private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);
	
	@SuppressWarnings("unchecked")
	public RedisUtil(Object address) {
		try {
			if (null != address) {
				if (address instanceof String) {
					init((String) address);
	            } else if (address instanceof List) {
	            	init((List<String>) address);
	            }
			}
		} catch (Exception e) {
			logger.error("Build redis connection failed.");
			e.printStackTrace();
		}
	}
	
	public void init(List<String> addresses) {//集群
		if (addresses.size() > 0) {
        	for (int i = 0; i < addresses.size(); i++) {
        		HostAndPort hostAndPort = new HostAndPort(addresses.get(i).split(":")[0], Integer.parseInt(addresses.get(i).split(":")[1]));
        		hostAndPorts.add(hostAndPort);
			}
        	jedisPoolConfig = new JedisPoolConfig();
        	jedisPoolConfig.setMaxTotal(10);
        	jedisPoolConfig.setMaxIdle(2);
        	jedisCommands = new JedisCluster(hostAndPorts, 5000, 100, jedisPoolConfig);
        }
	}
	
	public void init(String address) {//单机
		jedisCommands = new Jedis(address.split(":")[0], Integer.parseInt(address.split(":")[1]));
	}
	
	public void close() {
		if (jedisCommands != null) {
			if (jedisCommands instanceof JedisCluster) {
				try {
					((JedisCluster) jedisCommands).close();
				} catch (IOException e) {
					logger.error("Close redis connection failed.");
				}
			} else {
				((Jedis) jedisCommands).close();
			}
		}
	}
	
	public Long sadd(final String key, final String... member) {
		return jedisCommands.sadd(key, member);
	}
	
	public String spop(final String key) {
		return jedisCommands.spop(key);
	}
	
	public Long lpush(final String key, final String... string) {
		return jedisCommands.lpush(key, string);
	}
	
	public String lpop(final String key) {
		return jedisCommands.lpop(key);
	}
	
	public String set(final String key, final String value) {
		return jedisCommands.set(key, value);
	}
	
	public String get(final String key) {
		return jedisCommands.get(key);
	}
	
	public Long del(final String key) {
		return jedisCommands.del(key);
	}
	
	public Long hset(final String key, final String field, final String value) {
		return jedisCommands.hset(key, field, value);
	}
	
	public String hget(final String key, final String field) {
		return jedisCommands.hget(key, field);
	}
}
