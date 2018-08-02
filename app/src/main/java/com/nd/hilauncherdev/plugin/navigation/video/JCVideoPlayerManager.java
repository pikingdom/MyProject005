package com.nd.hilauncherdev.plugin.navigation.video;

import android.text.TextUtils;

/**
 * Put JCVideoPlayer into layout
 * From a JCVideoPlayer to another JCVideoPlayer
 * Created by Nathen on 16/7/26.
 */
public class JCVideoPlayerManager {

    private static JCVideoPlayer FIRST_FLOOR_JCVD;
    private static JCVideoPlayer SECOND_FLOOR_JCVD;
    private static String FIRST_FLOOR_URL = "";
    private static String SECOND_FLOOR_URL = "";
    public static int playingUrlListIndex = -1;

    public static void setFirstFloor(JCVideoPlayer jcVideoPlayer) {
        FIRST_FLOOR_JCVD = jcVideoPlayer;
        if(jcVideoPlayer != null){
            FIRST_FLOOR_URL = jcVideoPlayer.url;
            playingUrlListIndex = jcVideoPlayer.index;
        }
    }

    public static void setSecondFloor(JCVideoPlayer jcVideoPlayer) {
        SECOND_FLOOR_JCVD = jcVideoPlayer;
        if(jcVideoPlayer != null){
            SECOND_FLOOR_URL = jcVideoPlayer.url;
        }
    }

    /**
     * @desc 是否为当前播放地址相同
     * @author linliangbin
     * @time 2017/6/12 21:22
     */
    public static boolean isCurrentPlayInFirstFloor(String url){
        if(TextUtils.isEmpty(FIRST_FLOOR_URL)){
            return false;
        }
        if(FIRST_FLOOR_URL.equals(url)){
            return true;
        }
        return false;
    }


    public static boolean isCurrentPlayInSecondFloor(String url){

        if(TextUtils.isEmpty(SECOND_FLOOR_URL)){
            return false;
        }
        if(SECOND_FLOOR_URL.equals(url)){
            return true;
        }
        return false;
    }



    public static JCVideoPlayer getFirstFloor() {
        return FIRST_FLOOR_JCVD;
    }

    public static JCVideoPlayer getSecondFloor() {
        return SECOND_FLOOR_JCVD;
    }

    public static JCVideoPlayer getCurrentJcvd() {
        if (getSecondFloor() != null) {
            return getSecondFloor();
        }
        return getFirstFloor();
    }

    public static void completeAll() {
        if (SECOND_FLOOR_JCVD != null) {
            SECOND_FLOOR_JCVD.onCompletion();
            SECOND_FLOOR_JCVD = null;
        }
        if (FIRST_FLOOR_JCVD != null) {
            FIRST_FLOOR_JCVD.onCompletion();
            FIRST_FLOOR_JCVD = null;
        }
    }
}
