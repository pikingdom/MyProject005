package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

public class TravelBean {

	public static final int TYPE_MENU = 1;
	public static final int TYPE_PIC = 2;
	public static final int TYPE_TEXT = 3;

	public int id;
	public int type;
	public String title = "";
	public String cover = "";
	public String url = "";

	public TravelBean(JSONObject jo) {
		id = jo.optInt("id");
		type = jo.optInt("type");
		title = jo.optString("title");
		cover = jo.optString("cover");
		url = jo.optString("url");
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("id", id);
			jo.put("type", type);
			jo.put("title", title);
			jo.put("cover", cover);
			jo.put("url", url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jo;
	}
}
