package com.parmissmarthome.parmis_smart_home.Internet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class RequestPackage {

	private String uri;
	private String method = "POST";
	private Map<String, String> params = new HashMap<>();
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void setParam(String key, String value) {
        params.put(key, value);
	}
	
	public String getEncodedParams() {
		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			String value = null;
			try {
				value = URLEncoder.encode(params.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(key + "=" + value);
		}
		System.out.println("the request is ::: " + sb.toString());
		return sb.toString();
	}
}