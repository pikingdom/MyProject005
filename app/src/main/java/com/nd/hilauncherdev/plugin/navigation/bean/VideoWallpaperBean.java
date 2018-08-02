package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * 视频壁纸
 * Created by linliangbin on 2017/5/10 10:29.
 */

public class VideoWallpaperBean {


    public int videoId = 0;

    public String thumbUrl = "";

    public String videoUrl = "";

    public VideoWallpaperBean(JSONObject dataJo) {

        videoId = dataJo.optInt("VideoId");
        videoUrl = dataJo.optString("VideoUrl");
        thumbUrl = dataJo.optString("IconUrl");

    }
}
