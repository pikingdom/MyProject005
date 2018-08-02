package com.nd.hilauncherdev.plugin.navigation.bean;

import java.util.ArrayList;

/**
 * Created by linliangbin on 2017/5/10 11:10.
 */

public class WallpaperResultBean {

    public ArrayList<WallpaperBean> wallpaperList = new ArrayList<WallpaperBean>();
    public ArrayList<VideoWallpaperBean> videoWallpaperList = new ArrayList<VideoWallpaperBean>();
    public ArrayList<TextBean> textList = new ArrayList<TextBean>();


    public void addTextBean(TextBean textBean) {

        if (textList == null) {
            textList = new ArrayList<TextBean>();
        }
        textList.add(textBean);
    }


    public void addVideoBean(VideoWallpaperBean videoWallpaperBean) {

        if (videoWallpaperList == null) {
            videoWallpaperList = new ArrayList<VideoWallpaperBean>();
        }
        videoWallpaperList.add(videoWallpaperBean);
    }


    public void addWallpaperBean(WallpaperBean wallpaperBean) {

        if (wallpaperList == null) {
            wallpaperList = new ArrayList<WallpaperBean>();
        }
        wallpaperList.add(wallpaperBean);
    }

    public boolean isTextListAvailable() {
        return textList != null && textList.size() > 0;
    }

    public boolean isWallpaperListAvailable() {
        return wallpaperList != null && wallpaperList.size() > 0;
    }

    public boolean isVideopaperListAvailable() {
        return videoWallpaperList != null && videoWallpaperList.size() > 0;
    }


}
