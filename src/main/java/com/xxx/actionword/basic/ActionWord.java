package com.xxx.actionword.basic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.actionword.basic.intf.AssignContextIntf;
import com.xxx.actionword.basic.intf.CompareIntf;
import com.xxx.utils.replace.ReplaceUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ActionWord implements CompareIntf, AssignContextIntf{
	@JsonIgnore
	protected Map<String, Object> contextMap = new HashMap<String, Object>();
	protected String className;
	protected String remark;
	protected boolean active = true;
	protected static Logger logger = LoggerFactory.getLogger(ActionWord.class);
	
	protected final Object getContext(String key) {
		return contextMap.get(key);
	}
	
	protected final void setContext(String key, Object value) {
		try {
			if (!StringUtils.isEmpty(key) && null != value) {
				contextMap.put(key, value);
				logger.info("Set key:{} value:{} to context map successed.", key, value.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			logger.info("Set key:{} to context map failed.", key);
		}
	}
	
	protected final void removeContext(String key, Object value) {
		contextMap.remove(key, value);
	}
	
	protected final Object replace(Object object) {
		return ReplaceUtil.replaceAll(object, contextMap);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
