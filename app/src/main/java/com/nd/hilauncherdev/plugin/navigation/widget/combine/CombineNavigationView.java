package com.nd.hilauncherdev.plugin.navigation.widget.combine;

import android.content.Context;
import android.view.LayoutInflater;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.FullScreenVideoPlayerActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviWordLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayer;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationFavoriteSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源整合零屏
 * 包含壁纸+视频信息流
 * Created by linliangbin on 2017/4/21 14:41.
 */

public class CombineNavigationView extends BaseNavigationSearchView {

    WallpaperView wallpaperView;
    VideoNavigationView videoNavigationView;
    int marginTop = 0;

    public CombineNavigationView(Context context, int marginTop) {
        super(context);
        this.marginTop = marginTop;
        init();
    }


    public void init() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, marginTop, 0, 0);
        addView(LayoutInflater.from(getContext()).inflate(R.layout.navigation_combine_resource_layout, null), layoutParams);
        wallpaperView = (WallpaperView) findViewById(R.id.wallpaper_view);
        wallpaperView.setContainer(this);

        videoNavigationView = (VideoNavigationView) findViewById(R.id.video_navigation_view);

    }

    public NavigationFavoriteSiteView getFavoriteSiteView() {
        NavigationFavoriteSiteView favoriteSiteView = (NavigationFavoriteSiteView) findViewById(R.id.view_video_favorite_site);
        return favoriteSiteView;
    }


    public void onWallpaperUpdate(String imgPath) {
        if (videoNavigationView != null) {
            videoNavigationView.updateWallpaperBg(imgPath);
        }
    }

    public void onUpdatePullDistance(int distanceY) {

        if (videoNavigationView != null) {
            videoNavigationView.onUpdatePullDistance(distanceY);
        }
    }

    @Override
    public void updateAndRefreshSiteDetail() {
        super.updateAndRefreshSiteDetail();
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                if (NavigationLoader.getInstance().isRequestCounterIn()) {
                    updateFullScreenAdData();
                    if (NavigationLoader.getInstance().updateIconAndSiteData(context)) {
                        Global.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                getFavoriteSiteView().loadSites();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onLauncherStart() {
        super.onLauncherStart();
    }


    @Override
    public void setHotWordView(List<Object> list) {

        ArrayList<HotwordItemInfo> itemList = NaviWordLoader.convertHotwordListWithoutCheckSize(list);
//        HotwordItemInfo hotwordItemInfo = new HotwordItemInfo();
//        hotwordItemInfo.name = "测试热词1";
//        itemList.add(hotwordItemInfo);
//
//        HotwordItemInfo hotwordItemInfo2 = new HotwordItemInfo();
//        hotwordItemInfo2.name = "测试热词2";
//        itemList.add(hotwordItemInfo2);
//
//        HotwordItemInfo hotwordItemInfo3 = new HotwordItemInfo();
//        hotwordItemInfo3.name = "测试热词3";
//        itemList.add(hotwordItemInfo3);
//
//        HotwordItemInfo hotwordItemInfo4 = new HotwordItemInfo();
//        hotwordItemInfo4.name = "测试热词4";
//        itemList.add(hotwordItemInfo4);
        videoNavigationView.setHotWordView(itemList);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (!FullScreenVideoPlayerActivity.isStartFullScreen) {
            JCVideoPlayer.releaseAllVideos();
        }
    }

    @Override
    public void onShowingNavigationView(boolean isSohuAtRight) {
        super.onShowingNavigationView(isSohuAtRight);
        if (videoNavigationView != null) {
            videoNavigationView.onShowing();
        }
    }

    @Override
    public void onLeavingNavigation() {
        super.onLeavingNavigation();
        JCVideoPlayer.releaseAllVideos();
        if (videoNavigationView != null) {
            videoNavigationView.onLeaving();
        }
        JCVideoPlayer.cancelTimer();
    }

    @Override
    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {
        super.upgradePlugin(url, ver, isWifiAutoDownload);
//        Log.i("llbeing","upgradePlugin:" + url + "," + ver + ","+isWifiAutoDownload);
        if (videoNavigationView != null) {
            videoNavigationView.upgradePlugin(url, ver, isWifiAutoDownload);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.releaseAllVideos();
        JCVideoPlayer.cancelTimer();
        if (videoNavigationView != null) {
            videoNavigationView.onDestroy();
        }
    }
}
