package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * Created by linliangbin_dian91 on 2015/10/20.
 */
public class VPHAdBean {
    public String imgUrl = "";
    public String clickUrl = "";
    public int zone_id = 0;

    public VPHAdBean(JSONObject dataJo) {
        imgUrl = dataJo.optString("imgFullPath");
        clickUrl = dataJo.optString("url");
        zone_id = dataJo.optInt("Zone_id");
    }

    @Override
    public String toString() {
        return "VPHAdBean{" +
                "clickUrl='" + clickUrl + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", zone_id=" + zone_id +
                '}';
    }
}
