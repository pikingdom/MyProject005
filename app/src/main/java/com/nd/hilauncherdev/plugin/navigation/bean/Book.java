package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Book {

	public String name = "";
	public int type;
	public int num;
	public String detailUrl = "";
	public String icon = "";
	public String url = "";
	public String desc = "";
	public String bookId = "";
	public String author = "";
	public String abstractStr = "";
	public String recommend = "";
	public boolean isRecommend = true;
	public ArrayList<String> chapters = new ArrayList<>();


	public Book(){

	}

	public Book(JSONObject jo) {
		name = jo.optString("name");
		type = jo.optInt("type");
		num = jo.optInt("num");
		detailUrl = jo.optString("detailUrl");
		icon = jo.optString("icon");
		url = jo.optString("url");
		author = jo.optString("author");
		abstractStr = jo.optString("abstract");
		bookId = jo.optString("bookId");
		recommend = jo.optString("recommend");
		if (TextUtils.isEmpty(bookId)) {
			int ind = url.indexOf("bkid=");
			int end = url.indexOf("&", ind);
			if (end == -1) {
				end = url.length();
			}

			bookId = url.substring(ind + "bkid=".length(), end);
		}

		try {
			if (jo.has("chapters")) {
				chapters.clear();
				JSONArray ja = jo.optJSONArray("chapters");
				for (int i = 0; i < ja.length(); i++) {
					chapters.add(ja.optString(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("name", name);
			jo.put("type", type);
			jo.put("num", num);
			jo.put("detailUrl", detailUrl);
			jo.put("icon", icon);
			jo.put("url", url);
			jo.put("bookId", bookId);
			jo.put("author", author);
			jo.put("abstractStr", abstractStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}


	public boolean isAvailable() {
		return !TextUtils.isEmpty(bookId) && !TextUtils.isEmpty(icon) && !TextUtils.isEmpty(name);
	}
}
