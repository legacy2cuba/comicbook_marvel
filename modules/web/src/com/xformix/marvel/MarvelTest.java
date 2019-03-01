package com.xformix.marvel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarvelTest {

    private static String publicKey = "e4caebb35277c645b6134b5db889e74b";
    private static String privateKey = "f8d3766b5d2b4b7d14e9e2c6792258e8c3fb3196";

    public static void main(String[] args) {
	String ts = "1";

	MessageDigest md;
	try {
	    md = MessageDigest.getInstance("MD5");
	    md.update((ts + privateKey + publicKey).getBytes());
	    byte[] digest = md.digest();
	    String hash = DatatypeConverter.printHexBinary(digest).toLowerCase();
	    
	    JSONObject json = readJsonFromUrl("http://gateway.marvel.com/v1/public/characters?ts="+ts+"&apikey="+publicKey+"&hash="+hash+"&limit=100&nameStartsWith=Sp");
//	    System.out.println(json.toString());
	    JSONObject data = json.getJSONObject("data");
	    System.out.println(data.get("results"));
	    JSONArray results = data.getJSONArray("results");
	    for(int i=0;i<results.length();i++) {
		JSONObject obj = results.getJSONObject(i);
		System.out.println(obj.get("name"));
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

	  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      return json;
	    } finally {
	      is.close();
	    }
	  }

}
