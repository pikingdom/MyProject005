package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by linliangbin on 2017/5/26 16:01.
 */

public class DailyFortune {

    public String luckColor;
    public String luckDesc;
    public String luckPos;
    public int luckScore;
    public String luckTag;
    public String luckTitle;

    public DailyFortune() {
    }


    public static DailyFortune jsonToDailyFortune(JSONObject json) {
        if (json == null) {
            return null;
        } else {
            JSONObject object = json.optJSONObject("LucyData");
            DailyFortune entity = new DailyFortune();
            entity.luckColor = object.optString("luckColor");
            entity.luckDesc = object.optString("luckDesc");
            entity.luckPos = object.optString("luckPos");
            entity.luckScore = object.optInt("luckScore");
            entity.luckTag = object.optString("luckTag");
            entity.luckTitle = object.optString("luckTitle");
            return entity;
        }
    }

    public boolean isAvailable() {

        if (!TextUtils.isEmpty(luckTitle) && luckScore != 0) {
            return true;
        }
        return false;
    }

}
