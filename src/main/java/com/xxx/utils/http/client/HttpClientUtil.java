package com.xxx.utils.http.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxx.utils.convert.ConvertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.misc.BASE64Decoder;

/*
 * Author 杜亮亮
 * 2016.3.21
 */
public class HttpClientUtil {
    public static HttpClientResponse sendRequest(HttpClientRequest request) {
		RequestType type = request.getType();//1:GET 2:POST 3:PUT 4:DELETE
    	String url = request.getUrl();
		boolean BSAE64 = request.getBase64();
    	Map<String, String> headers = request.getHeaders();
    	Object body = request.getRequestBody();
    	String bodyStr = null;
    	HttpClientResponse response = null;
    	
    	if (!StringUtils.isEmpty(url)) {
    		HttpClientUtil httpClientUtil = new HttpClientUtil();
    		CloseableHttpClient httpclient = HttpClientBuilder.create().build();//init connection
    		if (null != body) {
    			if (body instanceof Map || body instanceof List) {
    				bodyStr = ConvertUtil.toJson(body);//get body
    			} else if (body instanceof String) {
    				bodyStr = (String) body;
    			}
    		} else {
    			//logger.warn("Request body is null.");
    		}
    		
    		try {
            	switch (type) {
            		case GET:
            			HttpGet httpGet = new HttpGet(url);
            			response = httpClientUtil.sendHTTPRequest(httpclient, httpGet, headers, null, BSAE64);
            			break;
            		case POST:
            			HttpPost httpPost = new HttpPost(url);
            			response = httpClientUtil.sendHTTPRequest(httpclient, httpPost, headers, bodyStr, BSAE64);
            			break;
            		case PUT:
            			HttpPut httpPut = new HttpPut(url);
            			response = httpClientUtil.sendHTTPRequest(httpclient, httpPut, headers, bodyStr, BSAE64);
            			break;
            		case DELETE:
            			HttpDelete httpDelete = new HttpDelete(url);
            			response = httpClientUtil.sendHTTPRequest(httpclient, httpDelete, headers, null, BSAE64);
            			break;
            		default:
            			logger.error("HttpClientRequest.type must in (GTE, POST, PUT, DELETE).");
            			break;
            	}
			} catch (Exception e) {
				logger.error("Send http request failed.");
				e.printStackTrace();
			} finally {
				try {
		            httpclient.close();
		        } catch (IOException e) {
		        	logger.error("Close connction of http client failed.");
		            e.printStackTrace();
		        }
			}
    	} else {
    		logger.error("Url can not be empty: {}.", url);
    	}
    	
    	return response;
    }

    public HttpClientResponse sendHTTPRequest(CloseableHttpClient httpclient, HttpRequestBase httpRequestBase, Map<String, String> headers, String body, boolean BSAE64) {
        HttpClientResponse httpClientResponse = null;
        try {
        	formatHttpRequestBase(headers, httpRequestBase, body);//format httpRequestBase
            CloseableHttpResponse response = httpclient.execute(httpRequestBase);//send request
            httpClientResponse = formatReponse(response,BSAE64);//edit HttpClientResponse
            response.close();
        } catch (ClientProtocolException e) {
        	logger.error("HttpClientUtil do not support this protocol.");
            e.printStackTrace();
        } catch (IOException e) {
        	logger.error("HttpClientUtil send request failed.");
            e.printStackTrace();
        }
        return httpClientResponse;
    }
    
    public HttpClientResponse formatReponse(CloseableHttpResponse response, boolean BSAE64Encode) {
    	HttpClientResponse httpClientResponse = new HttpClientResponse();
    	httpClientResponse.setStateCode(response.getStatusLine().toString().split(" ")[1]);
        Header[] responseHeaders = response.getAllHeaders();
        HashMap<String, Object> responseHeadersMap = new HashMap<String, Object>();
        for (int i = 0; i < responseHeaders.length; i++) {
            Header header = responseHeaders[i];
            responseHeadersMap.put(header.getName(), header.getValue());
        }
        httpClientResponse.setHeaders(responseHeadersMap);

		BASE64Decoder base64Decode = new BASE64Decoder();

        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                if (entity.getContent() != null) {
                        String responseStr = IOUtils.toString((entity.getContent()));
                        if (BSAE64Encode) {
                        	responseStr =  new String(base64Decode.decodeBuffer(responseStr));
                        }
                        httpClientResponse.setResponseBody(responseStr);;
                        //logger.info("Response body is :{}", responseStr);
                } 
            } 
        } catch (Exception e) {
        	logger.error("Format CloseableHttpResponse to HttpClientResponse failed." + e);
        }
        
        return httpClientResponse;
    }
    
    public void formatHttpRequestBase(Map<String, String> headers, HttpRequestBase httpRequestBase, String body) {
    	String encodingOfRequestBody = "UTF-8";//request body 默认charset
    	
    	if (null != headers && !headers.isEmpty()) {
            Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = (Entry<String, String>) iter.next();
                httpRequestBase.setHeader(entry.getKey(), entry.getValue());
                if (entry.getKey().toLowerCase().equals("content-type")
                        && entry.getValue().split(";").length >= 2) {
                    encodingOfRequestBody = entry.getValue().split(";")[1].split("=")[1];
                }
            }
        }
    	
    	//继承HttpRequestBase的子类有：HttpDelete、HttpGet、HttpHead、HttpOptions、HttpTrace
        //继承HttpEntityEnclosingRequestBase的子类有：HttpPut、HttpPost、HttpPatch
        if (null != body && httpRequestBase instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) httpRequestBase).setEntity(new StringEntity(body, encodingOfRequestBody));
            //logger.info("Request body is {}.", body);
        }
    }

    public String urlEncode(String url) {
        String encodeString = "";
        try {
            encodeString = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeString;
    }
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
}
