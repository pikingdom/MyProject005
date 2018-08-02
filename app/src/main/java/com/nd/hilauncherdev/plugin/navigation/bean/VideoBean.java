package com.nd.hilauncherdev.plugin.navigation.bean;

import com.dian91.ad.AdvertSDKManager;

import org.json.JSONObject;

/**
 * Created by linliangbin on 2017/5/10 15:30.
 */

public class VideoBean {

    public int resId;
    public String title;
    public String publishDate;
    public String resTime;
    public long resSize;
    public String playCount;
    public String downloadUrl;
    public String detailUrl;
    public String thumbUrl;
    public int upCount;
    public int downCount;
    public int forwardCount;
    public int commentCount;
    public int mediaType;

    public static final int AD_TYPE_UNKNOWN = -1;
    public static final int AD_TYPE_VIDEO = 1;
    public static final int AD_TYPE_BANNER = 2;

    public int adType = AD_TYPE_UNKNOWN;
    public AdvertSDKManager.AdvertInfo advertInfo;

    public boolean isPlayOnthis = false;
    public boolean isAutoPlay = false;
    public boolean isStartTiny = false;

    public VideoBean(){

    }
    public VideoBean(JSONObject dataJo) {

        resId = dataJo.optInt("ResId");
        title = dataJo.optString("Title");
        publishDate = dataJo.optString("Date");
        resTime = dataJo.optString("ResTime");
        resSize = dataJo.optLong("ResSize");
        playCount = dataJo.optString("Counts");
        downloadUrl = dataJo.optString("MediaUrl");
        detailUrl = dataJo.optString("H5Url");
        thumbUrl = dataJo.optString("Thumbnail");
        upCount = dataJo.optInt("GiveUpCount");
        downCount = dataJo.optInt("GiveDownCount");
        forwardCount = dataJo.optInt("ForwardCount");
        commentCount = dataJo.optInt("CommnetCount");
        mediaType = dataJo.optInt("MediaType");

    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public String getPlayCount() {
        return playCount + "";
    }

    public String getVideoTimeLong() {

        try {
            return secToTime(Integer.parseInt(resTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00";

    }

    public String getCommentCount() {
        return commentCount + "";
    }


    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        return this.hashCode() == o.hashCode();
    }
}
