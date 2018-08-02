package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * 网址卡片<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class UrlBean {

	public int id;
	public String name;
	public String url;
	public boolean isRed;
	
	public UrlBean(JSONObject jsonObject) {
		id = jsonObject.optInt("id");
		name = jsonObject.optString("name", "");
		url = jsonObject.optString("url", "");
		isRed = jsonObject.optBoolean("isRed", false);
	}
	
	
}
