package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * 推荐词<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class WordBean {

	public int id;
	public String word;
	public String url;
	
	public WordBean(JSONObject jsonObject) {
		word = jsonObject.optString("Word", "");
		url = jsonObject.optString("Url", "");
		id = jsonObject.optInt("card_resid",0);
	}
	
	
}
