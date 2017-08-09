package com.xxx.actionword.http.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.xxx.actionword.basic.ActionWord;
import com.xxx.model.actionword.http.MapComparedConfig;
import com.xxx.model.utils.basic.Results;
import com.xxx.utils.basic.AssertUtil;
import com.xxx.utils.convert.ConvertUtil;
import com.xxx.utils.http.client.HttpClientRequest;
import com.xxx.utils.http.client.HttpClientResponse;
import com.xxx.utils.http.client.HttpClientUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class AWHttpClient extends ActionWord{
	private HttpClientRequest request;
	
	private HttpClientResponse reponseExpected;
	
	private MapComparedConfig config;
	
	private String contextKeyName;
	
	@JsonIgnore
	private HttpClientResponse responseActual;

	@SuppressWarnings("unchecked")
	@Override
	public boolean compareExpectAndActual() {
		boolean result = true;
		responseActual = HttpClientUtil.sendRequest(request);
		if (null != reponseExpected) {
			String stateCodeExpect = reponseExpected.getStateCode();
			String stateCodeActual = responseActual.getStateCode();
			boolean resultOfCompareStateCode = stateCodeExpect.equals(stateCodeActual);//1、校验stateCode
			if (resultOfCompareStateCode) {
				boolean resultOfCompareHeaders = false;
				Map<String, Object> headersExpected = reponseExpected.getHeaders();
				Map<String, Object> headersActual = null;
				if (null != headersExpected && headersExpected.size() > 0) {
					headersActual = responseActual.getHeaders();
					resultOfCompareHeaders = AssertUtil.assertMap(headersActual, headersExpected, new Results()).isResult();//2、校验header
				} else {
					resultOfCompareHeaders = true;//不需要对比Headers，直接将对比结果设为true
				}
				
				if (resultOfCompareHeaders) {//3、校验body
					List<String> primaryKeys = null;
					int type = 0;
					Set<String> keys = null;
					Map<String, List<String>> keyNameAndPrimarykeys = null;
					if (null != config) {
						primaryKeys = config.getPrimaryKeys();
						type = config.getType();
						keys = config.getKeys();
						keyNameAndPrimarykeys = config.getKeyNameAndPrimarykeys();
					}
					String bodyActual = (String) responseActual.getResponseBody();
					Object bodyExpect = reponseExpected.getResponseBody();
					if (bodyExpect == null) {
						result = true;
						logger.warn("Expected response is null. Do not need compare.");
					} else {
						if (bodyExpect instanceof String) {
							result = bodyExpect.equals(bodyActual);
						} else {
							if (!StringUtils.isEmpty(bodyActual)) {
								if (bodyActual.startsWith("[") && bodyActual.endsWith("]") && (bodyExpect instanceof List)) {//list
									JSONArray jsonArray = JSONArray.fromObject(bodyActual);
									List<Object> listActaul = ConvertUtil.jsonObjectToList(jsonArray);
									result = AssertUtil.assertObject(listActaul, (List<Object>) bodyExpect, primaryKeys, keys, keyNameAndPrimarykeys, type, new Results()).isResult();
								} else if (bodyActual.startsWith("{") && bodyActual.endsWith("}") && (bodyExpect instanceof Map)) {//map
									JSONObject jsonObject = JSONObject.fromObject(bodyActual);
									Map<String, Object> mapActual = ConvertUtil.jsonObjectToMap(jsonObject);
									result = AssertUtil.assertObject(mapActual, (Map<String, Object>) bodyExpect, primaryKeys, keys, keyNameAndPrimarykeys, type, new Results()).isResult();
								} else {
									result = false;
									logger.error("It is not json String: {}", bodyActual);
								}
							} else {
								result = false;
								logger.error("Actual body is empty.");
							}
						}
					}
				} else {
					result = false;
					logger.error("Compare response headers failed. Expected: {}. Actual: {}.", headersExpected.toString(), headersActual.toString());
				}
			} else {
				result = false;
				logger.error("Compare state code failed. Expected: {}. Actual: {}.", stateCodeExpect, stateCodeActual);
			}
		} else {
			logger.info("Expected response is null. No need checkout response.");
		}
		
		return result;
	}

	@Override
	public void assignContext() {
		setContext(contextKeyName, responseActual);
	}
	
	public HttpClientRequest getRequest() {
		return request;
	}
	
	public void setRequest(HttpClientRequest request) {
		this.request = request;
	}

	public HttpClientResponse getReponseExpected() {
		return reponseExpected;
	}

	public void setReponseExpected(HttpClientResponse reponseExpected) {
		this.reponseExpected = reponseExpected;
	}

	public MapComparedConfig getConfig() {
		return config;
	}

	public void setConfig(MapComparedConfig config) {
		this.config = config;
	}

	public String getContextKeyName() {
		return contextKeyName;
	}

	public void setContextKeyName(String contextKeyName) {
		this.contextKeyName = contextKeyName;
	}
}
