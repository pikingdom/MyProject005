package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * 壁纸Bean
 * Created by linliangbin on 2017/5/10 10:15.
 */

public class WallpaperBean {

    public String picUrl = "";
    public int picId = 0;

    public WallpaperBean(JSONObject dataJo) {
        picUrl = dataJo.optString("PicUrl");
        picId = dataJo.optInt("PicId");
    }

}
