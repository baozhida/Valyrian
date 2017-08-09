package com.xxx.actionword.basic;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.actionword.basic.config.AWAndClassPath;
import com.xxx.model.actionword.basic.ManualTestcase;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.db.mysql.MysqlUtil;
import com.xxx.utils.replace.ReplaceUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AutoTestcase {
	protected ManualTestcase manualTestcase;
	protected List<Map<String, Object>> preSteps = new ArrayList<Map<String, Object>>();
	protected List<List<Map<String, Object>>> stepSets = new ArrayList<List<Map<String, Object>>>();
	protected List<Map<String, Object>> afterSteps = new ArrayList<Map<String, Object>>();
	@JsonIgnore
	protected Map<String, Object> contextMap = new HashMap<String, Object>();
	
	public boolean excute() {
		if (getManualTestcase().isActive()) {
			UUID taskid = UUID.randomUUID();
			Thread.currentThread().setName(taskid.toString());
			initContextMap();
			boolean result = false;
			try {
				boolean resultOfPreSteps = excutePreSteps();
				if (resultOfPreSteps) {
					result = excuteStepSets(taskid);
				}
			} finally {
				excuteAfterSteps();
				clear();
			}
			
			return result;
		} else {
			logger.warn("Testcase[{}] is unactivated.", manualTestcase.getId());
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void initContextMap() {
		URL propertiesFileURL = getClass().getClassLoader().getResource("application.properties");
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(propertiesFileURL);
			Iterator<String> keys = config.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				Object value = config.getProperty(key);
				contextMap.put(key, value);
			}
		} catch (ConfigurationException e) {
			logger.error("Can not find property file :{}.", propertiesFileURL.toString());
			e.printStackTrace();
		}
	}
	
	public boolean excutePreSteps() {
		logger.info("Start to excute pre steps.");
		return excuteSteps(preSteps);
	}
	
	public boolean excuteStepSets(UUID taskid) {
		logger.info("Start to excute steps.");
		int sumOfstepSets = stepSets.size();
		if (sumOfstepSets > 0) {
			List<Boolean> results = new ArrayList<Boolean>();
			ExecutorService executor = Executors.newFixedThreadPool(sumOfstepSets);
			
			stepSets.forEach(steps -> {
				executor.submit(() -> {
					String name = Thread.currentThread().getName();
					Thread.currentThread().setName(taskid.toString() + "-" + name);
					boolean result = excuteSteps(steps);
					results.add(result);
				});
			});
			
			try {
				executor.shutdown();
				executor.awaitTermination(2, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				logger.error("Shutdown thread failed.");
				e.printStackTrace();
			}
			
			boolean finalResult = true;
			for (boolean result : results) {
				if (!result) {
					finalResult = false;
					break;
				}
			}
			
			return finalResult;
		} else {
			logger.warn("Step sets is empty.");
			return true;
		}
	}
	
	public void excuteAfterSteps() {
		logger.info("Start to excute after steps.");
		excuteSteps(afterSteps);
	}
	
	public void clear() {
		Set<Map.Entry<String, Object>> entries = contextMap.entrySet();
		entries.forEach(entry -> {
			Object value = entry.getValue();
			if (value instanceof MysqlUtil) {
				((MysqlUtil) value).close();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private boolean excuteSteps(List<Map<String, Object>> steps) {
		boolean result = false;
		if ( null == steps || steps.size() == 0) {
			result = true;
		} else {
			for (Map<String, Object> stepMap : steps) {
				try {
					String className = (String) stepMap.get("className");
					className = getClassPath(className);
					Class<?> c = Class.forName(className);
					Constructor<?> cons = c.getConstructor();
					ActionWord aw = (ActionWord) cons.newInstance();					
					stepMap = (Map<String, Object>) ReplaceUtil.replaceAll(stepMap, contextMap);
					String jsonStr = ConvertUtil.toJson(stepMap);
					aw = ConvertUtil.jsonToBean(jsonStr, aw.getClass());
					aw.contextMap = contextMap;
					
					if (aw.isActive()) {
						try {
							result = aw.compareExpectAndActual();
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Excute action word {} failed.", aw.getClassName());
						}	
						
						if (result) {
							aw.assignContext();
						} else {
							break;//当前aw执行失败，则无须执行剩下的aw
						}
					} else {
						result = true;
					}
				} catch (Exception e) {
					logger.error("Excute auto testcase steps failed.");
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	private String getClassPath(String name) {
		String classPath = AWAndClassPath.getClassPathByName(name);
		if (StringUtils.isEmpty(classPath)) {
			return name;
		} else {
			return classPath;
		}
	}
	
	public List<Map<String, Object>> getPreSteps() {
		return preSteps;
	}

	public void setPreSteps(List<Map<String, Object>> preSteps) {
		this.preSteps = preSteps;
	}

	public List<List<Map<String, Object>>> getStepSets() {
		return stepSets;
	}

	public void setStepSets(List<List<Map<String, Object>>> stepSets) {
		this.stepSets = stepSets;
	}

	public List<Map<String, Object>> getAfterSteps() {
		return afterSteps;
	}

	public void setAfterSteps(List<Map<String, Object>> afterSteps) {
		this.afterSteps = afterSteps;
	}

	public ManualTestcase getManualTestcase() {
		if (null == manualTestcase) {
			manualTestcase = new ManualTestcase();
		}
		return manualTestcase;
	}

	public void setManualTestcase(ManualTestcase manualTestcase) {
		this.manualTestcase = manualTestcase;
	}

	protected static Logger logger = LoggerFactory.getLogger(AutoTestcase.class); 
}
