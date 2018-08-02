package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

public class Joke {

	public String content = "";

	public Joke(JSONObject dataJo) {
		content = dataJo.optString("Content");
	}
}
