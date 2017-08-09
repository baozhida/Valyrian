package com.xxx.utils.http.mockserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.model.actionword.http.MapComparedConfig;
import com.xxx.model.utils.basic.Results;
import com.xxx.model.utils.http.HttpMessageInfo;
import com.xxx.model.utils.http.HttpMockServerContext;
import com.xxx.model.utils.http.HttpMockServerContextRequest;
import com.xxx.model.utils.http.HttpMockServerContextResponse;
import com.xxx.utils.basic.AssertUtil;
import com.xxx.utils.convert.ConvertUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class HttpHandle implements HttpHandler {
    private HttpMockServerContext httpContext;

    public HttpHandle(HttpMockServerContext httpContext) {
        this.setHttpContext(httpContext);
    }

    public void handle(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        this.printHttpMessage(exchange);
        if (requestMethod.equals("GET") || requestMethod.equals("DELETE") || requestMethod.equals("HEAD")
                || requestMethod.equals("OPTION") || requestMethod.equals("TRACE")) {
        	String query = urlDecode(exchange.getRequestURI().getQuery());
            sendResponseByactualRequestBody(exchange, urlDecode(query), 1);
        } else if (requestMethod.equals("POST") || requestMethod.equals("PUT") || requestMethod.equals("PATCH")) {
            String encoding = "ISO-8859-1";//默认body编码
            Headers reqHeaders = exchange.getRequestHeaders();
            String contentType = reqHeaders.getFirst("Content-Type");
            if (contentType != null && contentType.split(";").length >= 2) {
                encoding = contentType.split(";")[1].split("=")[1];//获取body的charset
            }
            sendResponseByactualRequestBody(exchange, convertStreamToString(exchange.getRequestBody(), encoding).trim(), 2);
        }
    }

    @SuppressWarnings("unchecked")
	private void sendResponseByactualRequestBody(HttpExchange exchange, String actualRequestBody, int typeOfMethod) {
        boolean isRequestMatched = false;//MockServer实际收到的request(query or body)是否符合预期结果
        boolean isHeadersMatched = false;//MockServer实际收到的headers是否符合预期结果
        int indexMatched = 0;
        
        if ( null == this.getHttpContext().getHttpMessageInfoList() || this.getHttpContext().getHttpMessageInfoList().size() == 0 ) {
        	logger.warn("[HttpMockServer] HttpMessageInfoList is empty.");
        	isRequestMatched = true;
        } else {
        	List<HttpMessageInfo> httpMessageInfoList = getHttpContext().getHttpMessageInfoList();
        	for (int i = 0; i < httpMessageInfoList.size(); i++) {
        		HttpMockServerContextRequest request = httpMessageInfoList.get(i).getRequest();
                Map<String, Object> expectRequestHeaders = request.getHeaders();
                convertMapKeyToLowercase(expectRequestHeaders);
                if (null != expectRequestHeaders && !expectRequestHeaders.isEmpty()) {
                    Map<String, Object> actualRequestHeaders = new HashMap<String, Object>();
                    Headers requestHeaders = exchange.getRequestHeaders();
                    Set<String> keySet = requestHeaders.keySet();
                    Iterator<String> iterOfRequestHeaders = keySet.iterator();
                    while (iterOfRequestHeaders.hasNext()) {
                        String key = iterOfRequestHeaders.next();
                        List<String> values = requestHeaders.get(key);
                        actualRequestHeaders.put(key.toLowerCase(), values.toString().subSequence(1, values.toString().length() - 1));
                    }
                    logger.info("[HttpMockServer] ActualRequestHeaders is : {}", ConvertUtil.toJson(actualRequestHeaders));
                    logger.info("[HttpMockServer] ExpectedRequestHeaders is : {}", ConvertUtil.toJson(expectRequestHeaders));
                    isHeadersMatched = AssertUtil.assertMap(actualRequestHeaders, expectRequestHeaders, new Results()).isResult();
                } else {
                	logger.info("[HttpMockServer] ExpectedRequestHeaders is null.");
                    isHeadersMatched = true;
                }

                if (isHeadersMatched) {
                	logger.info("[HttpMockServer] The result of checking actualRequestHeaders is passing.");
                	if (actualRequestBody.isEmpty() || (null == request.getRequestBody() && null == request.getUrlParams())) {//
                		if (typeOfMethod == 1) {
                			logger.info("[HttpMockServer] Expected&actual URI prams are both empty. Send the index [" + indexMatched + "] response of httpContexts.responses");
                		} else {
                			logger.info("[HttpMockServer] Expected&actual body are both empty. Send the index [" + indexMatched + "] response of httpContexts.responses");
                		}
                		isRequestMatched = true;
                		indexMatched = i;
                        break;
                	} 
                	
                	if (!actualRequestBody.isEmpty() && (null != request.getRequestBody() || null != request.getUrlParams())) {
                		if (typeOfMethod == 1) { //GET、DELETE、HEAD对比URL中的query参数
                            Map<String, Object> expectRequestMap = request.getUrlParams();
                            Map<String, Object> actualRequestMap = convertQueryToMap(actualRequestBody);
                            logger.info("[HttpMockServer] ActualRequestURIParams is : {}", ConvertUtil.toJson(actualRequestMap));
                            logger.info("[HttpMockServer] ExpectedRequestURIParams is : {}", ConvertUtil.toJson(expectRequestMap));
                            isRequestMatched = AssertUtil.assertMap(actualRequestMap, expectRequestMap, new Results()).isResult();
                            if (isRequestMatched) {
                            	logger.info("[HttpMockServer] The result of checking actualURIparams is passing.");
                            	logger.info("[HttpMockServer] Send the index [" + indexMatched + "] response of httpContexts.responses.");
                                indexMatched = i;
                                break;
                            }else {
                            	logger.error("[HttpMockServer] The result of checking actualRequestURIParams is not passing.");
                            }
                        } else if (typeOfMethod == 2) {//POST、PUT、PATCH对比body，body的格式为json
                        	MapComparedConfig config = httpMessageInfoList.get(i).getConfig();
                        	int contentType = 1;
        					List<String> primaryKeys = null;
        					int type = 0;
        					Set<String> keys = null;
        					Map<String, List<String>> keyNameAndPrimarykeys = null;
        					if (null != config) {
        						contentType = config.getContentType();
        						primaryKeys = config.getPrimaryKeys();
        						type = config.getType();
        						keys = config.getKeys();
        						keyNameAndPrimarykeys = config.getKeyNameAndPrimarykeys();
        					}
        					String bodyActual = actualRequestBody;
        					Object bodyExpect = request.getRequestBody();
        					logger.info("[HttpMockServer] ActualRequestBody is : \"{}\"", actualRequestBody);
                            logger.info("[HttpMockServer] ExpectedRequestBody is : {}", ConvertUtil.toJson(bodyExpect));
        					switch (contentType) {
    						case 2:
    							isRequestMatched = bodyExpect.equals(bodyActual);
    							break;
    						default://默认按照JSON做对比
    							if (!StringUtils.isEmpty(bodyActual)) {
    								if (bodyActual.startsWith("[") && bodyActual.endsWith("]")) {//list
    									JSONArray jsonArray = JSONArray.fromObject(bodyActual);
    									List<Object> listActaul = ConvertUtil.jsonObjectToList(jsonArray);
    									isRequestMatched = AssertUtil.assertObject(listActaul, (List<Object>) bodyExpect, primaryKeys, keys, keyNameAndPrimarykeys, type, new Results()).isResult();
    								} else if (bodyActual.startsWith("{") && bodyActual.endsWith("}")) {//map
    									JSONObject jsonObject = JSONObject.fromObject(bodyActual);
    									Map<String, Object> mapActual = ConvertUtil.jsonObjectToMap(jsonObject);
    									isRequestMatched = AssertUtil.assertObject(mapActual, (Map<String, Object>) bodyExpect, primaryKeys, keys, keyNameAndPrimarykeys, type, new Results()).isResult();
    								} else {
    									logger.error("It is not json String: {}", bodyActual);
    								}
    							}
    							break;
    						}
        					
                            if (isRequestMatched) {
                            	logger.info("[HttpMockServer] The result of checking actualRequestbody is passing.");
                            	logger.info("[HttpMockServer] Send the index [" + indexMatched + "] response of httpContexts.responses.");
                                indexMatched = i;
                                break;
                            } else {
                            	logger.error("[HttpMockServer] The result of checking actualRequestbody is not passing.");
                            }
                        }
                	} 
                } else {
            		logger.error("[HttpMockServer] The result of checking actualRequestHeaders is not passing.");
            	}
            }
        }
        
        if (isRequestMatched) {
            sendResponse(exchange, this.getHttpContext().getHttpMessageInfoList().get(indexMatched).getResponse());
        } else {
        	logger.error("[HttpMockServer] There is no matched expected request, so send the bad request. The response code is 400.");
            sendResponse(exchange, new HttpMockServerContextResponse(400, "BAD REQUEST"));//不满足上述所有条件，返回400
        }
    }

    @SuppressWarnings("finally")
	private void sendResponse(HttpExchange exchange, HttpMockServerContextResponse httpContextResponse) {
        Headers responseHeaders = exchange.getResponseHeaders();
        if (null != httpContextResponse.getHeaders() && !httpContextResponse.getHeaders().isEmpty()) {
            Iterator<Entry<String, String>> iter = httpContextResponse.getHeaders().entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iter.next();
                responseHeaders.set(entry.getKey(), entry.getValue());
            }
        }
        try {
        	int stateCode = httpContextResponse.getStateCode();
        	if (stateCode < 200) {
        		logger.error("Response state code must >= 200.");
        	} else {
        		exchange.sendResponseHeaders(httpContextResponse.getStateCode(), 0);//设置响应消息的返回码
                OutputStream responseBody = exchange.getResponseBody();//初始化响应消息的byte流
                int contentTypeOfResponseBody = httpContextResponse.getContentTypeOfResponseBody();
                Object bodyObject = httpContextResponse.getResponseBody();
                String body = "";
                switch (contentTypeOfResponseBody) {
                case 1:
                	body = (String) bodyObject;
                	break;
                default:
                	if (null != bodyObject) {
                		body = ConvertUtil.toJson(bodyObject);
                	} else {
                		body = null;
                	}
                }
            	
                if (null != body) {
                	responseBody.write(body.getBytes());
                	logger.info("[HttpMockServer] HttpMockServer will send response, and the content of body is " + body);
                }
                responseBody.flush();
                responseBody.close();//关闭流
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	Thread.currentThread().interrupt();//preserve the message
            return;
        }
    }

    private String convertStreamToString(InputStream in, String encoding) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(in, writer, encoding);
        } catch (IOException e) {
        	logger.error("[HttpMockServer] Convert actual request body stream to string failed.");
            e.printStackTrace();
        }
        String theString = writer.toString();
        return theString;
    }

    private Map<String, Object> convertQueryToMap(String query) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != query && !query.equals("")) {
            String[] keyAndValueSets = query.split("&");
            for (int i = 0; i < keyAndValueSets.length; i++) {
                map.put(keyAndValueSets[i].split("=")[0], keyAndValueSets[i].split("=")[1]);
            }
        }
        return map;
    }

    private String urlDecode(String url) {
        String result = "";
        try {
            if (StringUtils.isEmpty(url)) {
                return result;
            }
            result = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	logger.error("[HttpMockServer] Decode request url with UTF-8 encoding failed.");
            e.printStackTrace();
        }
        return result;
    }
    
    private void convertMapKeyToLowercase(Map<String, Object> map) {
    	if (null != map) {
    		List<String> keys = new ArrayList<String>();
            for (String key : map.keySet()) {
            	if (!key.equals(key.toLowerCase())) {
           		 	keys.add(key);
        		}
            }
            
            for (String key : keys) {
            	map.put(key.toLowerCase(), map.get(key));
            	map.remove(key);
            }
    	}
    }
    
    private void printHttpMessage(HttpExchange exchange) {
        logger.info("[HttpMockServer] HttpMockServer recieved a " + exchange.getRequestMethod() +" method http message.");
        logger.info("[HttpMockServer] Request URL: " + exchange.getRequestURI());
        Headers requestHeaders = exchange.getRequestHeaders();
        Set<String> keySet = requestHeaders.keySet();
        Iterator<String> iterOfRequestHeaders = keySet.iterator();
        while (iterOfRequestHeaders.hasNext()) {
            String key = iterOfRequestHeaders.next();
            List<String> values = requestHeaders.get(key);
            logger.debug("[HttpMockServer] Request Header: [" + key.toLowerCase() + ": " +values.toString().subSequence(1, values.toString().length() - 1) + "]");
        }
    }
    
    public HttpMockServerContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(HttpMockServerContext httpContext) {
		this.httpContext = httpContext;
	}

	private static final Logger logger = LoggerFactory.getLogger(HttpHandle.class);
}
