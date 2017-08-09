package com.xxx.utils.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
 * Author: duliangliang
 * date: 2016-7-5
 */
public class HttpClientRequest {
	private RequestType type;//1:GET 2:POST 3:PUT 4:DELETE
	
	private String url;
	
	private Map<String, String> headers;
	
	private Map<String, String> query;
	
	private Map<String, String> path;
	
	private Object requestBody;

	private boolean Base64 = false;

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public String getUrl() {
		return formatUrl(this.url, this.query, this.path);
	}

	public void setUrl(String url) {
		if (!url.startsWith("http")){
			this.url = "http://"+url;
		}else {
			this.url = url;
		}

	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getQuery() {
		return query;
	}

	public void setQuery(Map<String, String> query) throws UnsupportedEncodingException {
			this.query = query;
	}

	public Map<String, String> getPath() {
		return path;
	}

	public void setPath(Map<String, String> path) {
		this.path = path;
	}

	public Object getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}

	public boolean getBase64() {
		return Base64;
	}

	public void setBase64(boolean isBase64) {
		this.Base64 = isBase64;
	}

	private String formatUrl(String url, Map<String, String> query, Map<String, String> path) {
		String result = "";
		
		if (null != path && !path.isEmpty()) {
			String patternString = "\\{(" + StringUtils.join(path.keySet(), "|") + ")\\}";//正则表达式，示例：/{userid}/Info，替换{userid}部分
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(url);
			StringBuffer sb = new StringBuffer();
		    while(matcher.find()) {
		        matcher.appendReplacement(sb, path.get(matcher.group(1)));
		    }
		    matcher.appendTail(sb);
		    result += sb.toString();
		} else {
			result = url;
		}
		
		if (null != query && !query.isEmpty()) {
			String params = "?";
			Iterator<Entry<String, String>> iter = query.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iter.next();
				if (entry.getKey().equals("")){
					params += entry.getValue() + "&";
				}else {
					params += entry.getKey() + "=" + entry.getValue() + "&";
				}
			}
			result += params.substring(0, params.length()-1);
		}
		//logger.info("Request url is {}", result);
		
		return result.toString();
	}
	
	private String urlEncode(String url) {
        String encodeString = "";
        try {
            encodeString = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeString;
    }
	//字符串base64加密
	public String base64Encoder(String str) {
		byte[] b = null;
		String result = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			result = new BASE64Encoder().encode(b);
		}
		return result;
	}

	// base64解密
	public String base64Decoder(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	private static final Logger logger = LoggerFactory.getLogger(HttpClientRequest.class);
}
