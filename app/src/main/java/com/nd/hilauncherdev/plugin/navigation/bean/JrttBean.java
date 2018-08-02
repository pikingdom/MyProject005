package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 今日头条<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class JrttBean {
	public int id;
	public String title;
	public String groupId;
	public String toutiaoUrl;
	public String abstractContent;
	public String imgUrl;

	public JrttBean(JSONObject jo) {
		title = jo.optString("Title");
		groupId = jo.optString("GroupID");
		toutiaoUrl = jo.optString("ToutiaoUrl");
		id = jo.optInt("card_resid");
		try {
			if (jo.getJSONArray("ImageUrl").length() > 0) {
				imgUrl = jo.getJSONArray("ImageUrl").getJSONArray(0).get(0).toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		abstractContent = jo.optString("Abstract");
		if (TextUtils.isEmpty(abstractContent)) {
			abstractContent = title;
		}
	}

}
