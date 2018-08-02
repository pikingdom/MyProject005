package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * Created by linliangbin_dian91 on 2015/10/21.
 */
public class GameBean {

    public static final int RECOMMEND_TYPE = 1;
    public static final int H5_TYPE = 2;
    public static final int NEWS_TYPE = 3;


    public int type;
    public String text;
    public String pkg;
    public String size;
    public String iconUrl;
    public String downloadUrl;
    public String gameName;
    public String ID = "";
    public String title;
    public long openTime;

    public GameBean(JSONObject dataJo, int t) {
        switch (t) {
            case RECOMMEND_TYPE:
                pkg = dataJo.optString("Pkg");
                size = dataJo.optString("Size");
                iconUrl = dataJo.optString("IconUrl");
                downloadUrl = dataJo.optString("DownloadUrl");
                gameName = dataJo.optString("GameName");
                break;
            case H5_TYPE:
                iconUrl = dataJo.optString("IconUrl");
                downloadUrl = dataJo.optString("GameUrl");
                gameName = dataJo.optString("GameName");
                ID = dataJo.optString("Id");
                openTime = dataJo.optLong("openTime");
                break;
            case NEWS_TYPE:
                downloadUrl = dataJo.optString("Url");
                ID = dataJo.optString("Id");
                title = dataJo.optString("Title");
                break;
        }
        type = t;
    }

    @Override
    public String toString() {
        return "{" +
                "iconUrl='" + iconUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", gameName='" + gameName + '\'' +
                ", ID='" + ID + '\'' +
                '}';
    }
}
