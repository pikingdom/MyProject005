package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

public class PicBean {
	
	public String imgUrl = "";
	public String clickUrl = "";
	
	public PicBean(JSONObject dataJo) {
		imgUrl = dataJo.optString("IconUrl");
		clickUrl = dataJo.optString("DetailLink");
	}
	
}
