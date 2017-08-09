package com.xxx.utils.convert;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

@SuppressWarnings("unchecked")
public class ConvertUtil {
    public static void mapToBean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
        	logger.error("Convert map to bean failed. Map is {}.", map);
            e.printStackTrace();
        }
    }
    
	public static List<Object> jsonToList(Object jsonStr) {
        List<Object> mapList = new ArrayList<Object>();
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONArray.fromObject(jsonStr);
        } catch (Exception e) {
            mapList.add(toMap(jsonStr));
            return mapList;
        }
        
        Iterator<JSONObject> it = jsonArray.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof JSONObject) {
                JSONObject jsonbject = (JSONObject) obj;
                mapList.add(toMap(jsonbject));
            } else {
                mapList.add(obj);
            }
        }
        
        return mapList;
    }
	
	public static Map<String, Object> jsonToMap(Object jsonStr) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (jsonStr == null || StringUtils.isEmpty(jsonStr.toString())) {
            return map;
        }
        
        JSONObject json = JSONObject.fromObject(jsonStr);
        for (Object key : json.keySet()) {
            Object value = json.get(key);
            if (value instanceof JSONArray) {
                List<Object> list = new ArrayList<Object>();
                Iterator<JSONObject> it = ((JSONArray) value).iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof JSONObject) {
                        JSONObject json2 = (JSONObject) obj;
                        list.add(jsonToMap(json2.toString()));
                    } else {
                        list.add(obj);
                    }
                }
                map.put(key.toString(), list);
            } else {
                map.put(key.toString(), value);
            }
        }
        return map;
    }

	public static Map<String, Object> toMap(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Map) return (Map<String, Object>) obj;
        
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
        	logger.error("Convert bean to map failed. Bean is {}.", toJson(obj));
            e.printStackTrace();
        }

        return map;
    }

    public static String toJson(Object obj) {
        String jsonStr = null;
        try {
            jsonStr = new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
        	logger.error("Convert bean to json failed.");
            e.printStackTrace();
        }
        
        return jsonStr;
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> clazz) {
        T t = null;
        try {
            t = new ObjectMapper().readValue(jsonStr, clazz);
        } catch (Exception e) {
        	logger.error("Convert json to bean failed.");
            e.printStackTrace();
        }

        return t;
    }

    public static List<Object> getValueByKeyNames(Object object, List<String> keyNames) {
        List<Object> values = new ArrayList<Object>();
        for (String keyName : keyNames) {
            Object value = getValueByKeyName(object, keyName);
            values.add(value);
        }
        
        return values;
    }

    public static Object getValueByKeyName(Object object, String keyName) {
        Object result = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (keyName.equals(key)) {
                    Method getter = property.getReadMethod();
                    result = getter.invoke(object);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static Map<String, Object> UDTValueToMap(UDTValue value) {
    	Map<String, Object> map = new HashMap<String, Object>();
		int i = 0;
		UserType userType = value.getType();
		Iterator<UserType.Field> fields = userType.iterator();
		while (fields.hasNext()) {
			UserType.Field field = fields.next();
			String name = field.getName();
			Object valueOfName = value.getObject(i);
			map.put(name, valueOfName);
			i++;
		}
		
		return map;
    }
    
    public static Map<String, Object> jsonObjectToMap(JSONObject object) {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            
            if(value instanceof JSONArray) {
                value = jsonObjectToList((JSONArray) value);
            } else if(value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            }
            
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> jsonObjectToList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);
            
            if(value instanceof JSONArray) {
                value = jsonObjectToList((JSONArray) value);
            } else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            
            list.add(value);
        }
        return list;
    }

    public static String StringBsae64Encoder(String str) throws UnsupportedEncodingException {
        BASE64Encoder base64encode = new BASE64Encoder();
        String base64 = base64encode.encode(str.getBytes("UTF-8"));
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(base64);
        base64 = m.replaceAll("");
        return base64;
    }
    
    private static Logger logger = LoggerFactory.getLogger(ConvertUtil.class);
}