package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.bean.VideoBean;
import com.nd.hilauncherdev.plugin.navigation.net.NetOpi;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by linliangbin on 2017/5/11 11:51.
 */

public class VideoDataLoader {


    private static final String NAVI_FILE_VIDEO = "navi_wallpaper_text.txt";
    private static final String NAVI_PATH_VIDEO = NaviCardLoader.NAV_DIR + NAVI_FILE_VIDEO;
    private static final int PAGE_SIZE = 20;
    private Context context;
    private VideoDataCallback callback;

    int bannerAdIndex = 5;
    int bannerAdPace = 6;

    int videoAdIndex = 3;
    int videoAdPace = 4;

    /**
     * 视频ITEM ID map,用于视频列表去重
     */
    private HashMap<String, String> idsMap = new HashMap<String, String>();

    private static VideoDataLoader instance;


    public static synchronized VideoDataLoader getInstance(Context context){
        if(instance == null){
            instance = new VideoDataLoader(context);
        }
        return instance;
    }

    private void resetIndex(){
        bannerAdIndex = 5;
        bannerAdPace = 6;

        videoAdIndex = 3;
        videoAdPace = 4;

        if(idsMap != null){
            idsMap.clear();
        }
    }

    private LinkedList<AdvertSDKManager.AdvertInfo> bannerAd = new LinkedList<AdvertSDKManager.AdvertInfo>();
    private LinkedList<AdvertSDKManager.AdvertInfo> videoAd = new LinkedList<AdvertSDKManager.AdvertInfo>();

    private VideoDataLoader(Context context) {
        this.context = context;
    }

    public void setCallback(VideoDataCallback callback) {
        this.callback = callback;
    }


    public AdvertSDKManager.AdvertInfo popVideoAd() {
        if (videoAd != null && videoAd.size() > 0) {
            AdvertSDKManager.AdvertInfo advertInfo = videoAd.poll();
            videoAd.add(advertInfo);
            return advertInfo;
        }
        return null;
    }

    public AdvertSDKManager.AdvertInfo popBannerAd() {
        if (bannerAd != null && bannerAd.size() > 0) {
            AdvertSDKManager.AdvertInfo advertInfo = bannerAd.poll();
            bannerAd.add(advertInfo);
            return advertInfo;
        }
        return null;
    }

    private void minusIndex(){
        videoAdIndex--;
        bannerAdIndex--;
    }
    /**
     * @desc 将广告按照一定的位置先后插入视频列表中
     * @author linliangbin
     * @time 2017/6/13 10:52
     */
    private int counter = 1;
    private void handleVideoAd(VideoBean currentVideo,ArrayList<VideoBean> resultList){
        AdvertSDKManager.AdvertInfo videoAd = popVideoAd();
        if (videoAd != null) {
            videoAdPace++;
            videoAdIndex = videoAdPace;
            currentVideo.advertInfo = videoAd;
            currentVideo.adType = VideoBean.AD_TYPE_VIDEO;
            resultList.add(currentVideo);
            minusIndex();
        }else{
            resultList.add(currentVideo);
            minusIndex();
        }
    }

    private boolean handleBannerAd(ArrayList<VideoBean> resultList){

        VideoBean bannerAdVideo = new VideoBean();
        bannerAdVideo.adType = VideoBean.AD_TYPE_BANNER;
        AdvertSDKManager.AdvertInfo bannerAd = popBannerAd();
        if (bannerAd != null) {
            bannerAdPace++;
            bannerAdIndex = bannerAdPace;
            bannerAdVideo.advertInfo = bannerAd;
            counter++;
            resultList.add(bannerAdVideo);
            minusIndex();
            return true;
        }
        return false;
    }


    private ArrayList<VideoBean> assembleVideoList(List<VideoBean> arrayList) {
        ArrayList<VideoBean> resultList = new ArrayList<VideoBean>();
        if(arrayList == null || arrayList.size() ==0){
            return null;
        }
        for (VideoBean videoBean : arrayList) {
            if (videoAdIndex <= 0) {
                handleVideoAd(videoBean,resultList);
            }else if (bannerAdIndex <= 0) {
                if(handleBannerAd(resultList)){
                    if(videoAdIndex <= 0){
                        handleVideoAd(videoBean,resultList);
                    }
                }else{
                    counter++;
                    resultList.add(videoBean);
                    minusIndex();
                }
            }else{
                counter++;
                resultList.add(videoBean);
                minusIndex();
            }
        }
        return resultList;

    }

    public List<VideoBean> removeDuplicate(List<VideoBean> list) {

        if (list == null || list.size() == 0) {
            return list;
        }
        List<VideoBean> resultList = new ArrayList<VideoBean>();
        for (VideoBean videoBean : list) {
            if (!idsMap.containsKey(videoBean.resId + "")) {
                resultList.add(videoBean);
                idsMap.put(videoBean.resId + "", "");
            }
        }

        return resultList;
    }




    private void sendResult(final boolean isAppend, final int resultCode, final boolean isEnd, final ArrayList<VideoBean> videoBeen, final List<AdvertSDKManager.AdvertInfo> bannerAd, final List<AdvertSDKManager.AdvertInfo> videoAd) {

        Global.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                if(bannerAd != null && bannerAd.size() > 0){
                    VideoDataLoader.this.bannerAd.addAll(bannerAd);
                }
                if(videoAd != null && videoAd.size() > 0){
                    VideoDataLoader.this.videoAd.addAll(videoAd);
                }
                ArrayList<VideoBean> result = assembleVideoList(removeDuplicate(videoBeen));
                int newResultCode = resultCode;
                if (result == null || result.isEmpty()) {
                    newResultCode = VideoDataCallback.RESULT_LOAD_FAIL;
                }
                callback.onVideoListUpdate(result, null, null, newResultCode, isEnd, isAppend);
            }
        });
    }

    public void requestVideoAd(){

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<AdvertSDKManager.AdvertInfo> videoAdList;
                videoAdList = AdvertSDKController.getAdvertInfos(context, AdvertSDKController.AD_POSITION_VIDEO_AD);
                Global.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoAdList != null && videoAdList.size() > 0){
                            VideoDataLoader.this.videoAd.addAll(videoAdList);
                        }
                    }
                });
            }
        });
    }

    public void requestVideoPage(final int pageIndex, final boolean isAppend, final  boolean refreshAd) {

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                List<AdvertSDKManager.AdvertInfo> videoAdList = null;
                List<AdvertSDKManager.AdvertInfo> bannerAdList = null;

                if (refreshAd) {
                    resetIndex();
                    videoAdList = AdvertSDKController.getAdvertInfos(context, AdvertSDKController.AD_POSITION_VIDEO_AD);
                    bannerAdList = AdvertSDKController.getAdvertInfos(context, AdvertSDKController.AD_POSITION_VIDEO_BANNER);
                }

                String data = NetOpi.loadVideoList(context, pageIndex, PAGE_SIZE, NetOpi.MEDIA_TYPE_VIDEO, NetOpi.SORT_TYPE_RECOMMEND);
                try {
                    ArrayList<VideoBean> videos = new ArrayList<VideoBean>();
                    JSONObject jsonObject = null;
                    jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.optJSONArray("List");
                    boolean isDisableVideo = jsonObject.optBoolean("IsDisableVideo",false);
                    CardManager.getInstance().setIsDisableVideo(context,isDisableVideo);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject tempObject = (JSONObject) jsonArray.get(i);
                        VideoBean subscribeCateBean = new VideoBean(tempObject);
                        videos.add(subscribeCateBean);
                    }

                    if (videos != null && !videos.isEmpty()) {
                        sendResult(isAppend, VideoDataCallback.RESULT_LOAD_SUCCESS, videos.size() < PAGE_SIZE ? true : false, videos, bannerAdList, videoAdList);
                    } else {
                        sendResult(isAppend, VideoDataCallback.RESULT_LOAD_FAIL, false, videos, bannerAdList, videoAdList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResult(isAppend, VideoDataCallback.RESULT_LOAD_FAIL, false, null, null, null);
                }
            }
        });
    }

    public interface VideoDataCallback {
        public static final int RESULT_LOAD_SUCCESS = 1;
        public static final int RESULT_LOAD_FAIL = 2;

        /**
         * 成功获取到Video 的回调
         */
        public void onVideoListUpdate(ArrayList<VideoBean> videoList, List<AdvertSDKManager.AdvertInfo> bannerAd,
                                      List<AdvertSDKManager.AdvertInfo> videoAd, int resultCode, boolean isLast, boolean isAppend);

    }


}


