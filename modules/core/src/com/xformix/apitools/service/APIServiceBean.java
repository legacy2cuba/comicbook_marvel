package com.xformix.apitools.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xformix.apitools.JSONPath;

public class APIServiceBean {

    private static Logger logger = Logger.getLogger(APIServiceBean.class);
    
    private static final Pattern arrayIndexPattern = Pattern.compile("([^\\]]+)\\[([0-9]+)\\]");

    protected static String readAllFromURL(String url) throws IOException {
	InputStream is = new URL(url).openStream();
	try {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
		sb.append((char) cp);
	    }
	    return sb.toString();
	} finally {
	    is.close();
	}
    }

    protected void fillObject(Object toFill, JSONObject json) {
	Method[] methods = toFill.getClass().getDeclaredMethods();
	Field[] fields = toFill.getClass().getDeclaredFields();
	for (Field field : fields) {
	    Method method = null;
	    for (Method m : methods) {
		if (m.getName().toLowerCase().equals("set" + field.getName().toLowerCase())) {
		    method = m;
		}
	    }
	    JSONPath[] annons = field.getDeclaredAnnotationsByType(JSONPath.class);
	    String jsonField = field.getName();
	    Object val = null;
	    if (annons != null && annons.length > 0) {
		if (annons[0].path() != null) {
		    jsonField = annons[0].path();
		}
	    }
	    try {
		val = getObject(json, jsonField);
		if(Date.class.isAssignableFrom(field.getType())) {
		    val = getDate(val);
		} else if(Integer.class.isAssignableFrom(field.getType())) {
		    val = getInteger(val);
		} else if(Double.class.isAssignableFrom(field.getType())) {
		    val = getDouble(val);
		} else if(String[].class.isAssignableFrom(field.getType())) {
		    val = getStringArray(val);
		}
	    } catch (Exception e) {
		logger.info("Unable to load field " + jsonField + ", " + e);
	    }
	    try {
		if(val != null) {
		    if (method != null) {
			method.invoke(toFill, val);
		    } else {
			field.setAccessible(true);
			field.set(toFill, val);
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }
    
    private String[] getStringArray(Object val) {
	if(val instanceof Object[]) {
	    Object[] objArray = (Object[]) val;
	    String[] strArray = new String[objArray.length];
	    for (int i = 0; i < objArray.length; i++) {
		strArray[i] = (String) objArray[i];
	    }
	    return strArray;
	}
	return null;
    }

    protected <T extends Object> List<T> getResults(String type, String selectStr, Class<T> returnClass) {
	return getResults(type, selectStr, -1, returnClass);
    }
    
    protected String getBaseURL(String type, Integer id) {
	return "";
    }
	
    protected <T extends Object> List<T> getResults(String type, String selectStr, Integer id, Class<T> returnClass) {
	List<T> returnSet = null;
	try {
	    String url = getBaseURL(type, id);
	    url += "&"+selectStr;
	    logger.debug("getResults, selectStr="+selectStr+", URL="+url);
	    JSONObject json = new JSONObject(readAllFromURL(url));

	    JSONArray jarray = json.getJSONObject("data").getJSONArray("results");
	    if(jarray != null) {
		logger.debug("getResults, returned " + jarray.length() + " results");
		returnSet = new ArrayList<>();
		for (int j = 0; j < jarray.length(); j++) {
		    T toFill = returnClass.newInstance();
		    fillObject(toFill, jarray.getJSONObject(j));
		    returnSet.add(toFill);
		}
	    } else {
		logger.debug("getResults, returned null");
	    }
	} catch (Exception e) {
	    logger.error("Error in MarvelServiceBean.getResults, "+e);
	}
	return returnSet;
    }
    
    public Date getDateObject(JSONObject jobj, String path) {
	Object dateObj = getObject(jobj, path);
	if (dateObj != null) {
	    return getDate(dateObj);
	}
	return null;
    }
    
    private Date getDate(Object dateObj) {
	try {
	    if(dateObj == null)
		return null;
	    return new SimpleDateFormat("yyyy-MM-dd").parse(dateObj.toString());
	} catch (ParseException e) {
	}
	return null;
    }

    public Integer getIntegerObject(JSONObject jobj, String path) {
	Object obj = getObject(jobj, path);
	return getInteger(obj);
    }
    
    public Double getDoubleObject(JSONObject jobj, String path) {
	Object obj = getObject(jobj, path);
	return getDouble(obj);
    }
    
    public Object[] getArrayObject(JSONObject jobj, String path) {
	Object obj = getObject(jobj, path);
	if(obj instanceof Object[])
	    return (Object[]) obj;
	if(obj != null)
	    logger.error("Expected an array object but got "+obj+" for path "+path);
	return null;
    }
    
    public String getStringObject(JSONObject jobj, String path) {
	Object obj = getObject(jobj, path);
	if(obj == null)
	    return null;
	return obj.toString();
    }

    private static Integer getInteger(Object obj) {
	if(obj instanceof Integer) {
	    return (Integer)obj;
	}
	if(obj instanceof Double) {
	    return ((Double)obj).intValue();
	}
	if(obj instanceof Float) {
	    return ((Float)obj).intValue();
	}
	if(obj instanceof Long) {
	    return ((Long)obj).intValue();
	}
	if(obj instanceof String) {
	    try {
	    return Integer.parseInt(((String)obj).trim());
	    } catch(Exception e) {}
	}
	return null;
    }
    
    private static Double getDouble(Object obj) {
	if(obj instanceof Double) {
	    return (Double)obj;
	}
	if(obj instanceof Integer) {
	    return ((Integer)obj).doubleValue();
	}
	if(obj instanceof Float) {
	    return ((Float)obj).doubleValue();
	}
	if(obj instanceof Long) {
	    return ((Long)obj).doubleValue();
	}
	if(obj instanceof String) {
	    try {
	    return Double.parseDouble(((String)obj).trim());
	    } catch(Exception e) {}
	}
	return null;
    }
    
    private Object getObject(JSONObject jobj, String path) {
	try {
	    if(path.startsWith("{") && path.endsWith("}")) {
		return path.substring(1, path.length()-1);
	    }
	    if(path.contains("|")) {
		String result = "";
		String[] superParts = path.split("\\|");
		for (String superPart : superParts) {
		    result += getObject(jobj, superPart);
		}
		return result;
	    }
	    String[] parts = path.split("/");
	    for (int i=0;i<parts.length;i++) {
		String part = parts[i].trim();
		if(part.equals(""))
		    continue;
		Matcher m = arrayIndexPattern.matcher(part);
		if (m.find()) {
		    part = m.group(1);
		    JSONArray array = jobj.getJSONArray(part);
		    int index = Integer.parseInt(m.group(2));
		    if (array != null && array.length() > index) {
			Object obj = array.get(index);
			if (obj instanceof JSONObject) {
			    jobj = (JSONObject) obj;
			} else {
			    return obj;
			}
		    }
		} else {
		    Object obj = jobj.get(part);
		    logger.debug("getObject - got "+obj+" for "+part);
		    if (obj instanceof JSONObject) {
			jobj = (JSONObject) obj;
		    } else if(obj instanceof JSONArray) {
			JSONArray jarray = (JSONArray)obj;
			Object[] returnArray = new Object[jarray.length()];
			String restOfPath = "";
			for(int j=i+1;j<parts.length;j++) {
			    restOfPath += "/"+parts[j];
			}
			for (int subIndex=0;subIndex<jarray.length();subIndex++) {
			    JSONObject jobj2 = jarray.getJSONObject(subIndex);
			    returnArray[subIndex] = getObject(jobj2, restOfPath);
			}
			return returnArray;
		    } else {
			return obj;
		    }
		}
	    }
	    return jobj;
	} catch (Exception e) {
	    logger.debug("Unable to find object with path " + path + " in JSON object " + jobj + ", error is: " + e);
	    return null;
	}
    }

    protected String getHash(String str) {
	try {
	    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	    messageDigest.update(str.getBytes());
	    return DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase();
	} catch (Exception e) {
	    logger.error("Error in getHash", e);
	}
	return null;
    }
    
    protected String encode(String str) {
	try {
	    return URLEncoder.encode(str, "UTF-8");
	} catch(Exception e) {
	    logger.error("Error encoding "+str, e);
	    return str;
	}
    }

}