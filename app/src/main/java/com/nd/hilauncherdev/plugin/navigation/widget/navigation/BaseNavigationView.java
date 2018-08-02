package com.nd.hilauncherdev.plugin.navigation.widget.navigation;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationFavoriteSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationSiteView;

import java.util.List;

/**
 * 零屏基础类 主要包含需要转发的方法
 * 部分方法有反射，不可混淆
 * Created by linliangbin on 2017/4/7 11:29.
 */

public abstract class BaseNavigationView extends RelativeLayout {

    protected BaseNavigationSearchView navigationView2;

    public BaseNavigationView(Context context) {
        super(context);
    }

    public BaseNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void refreshPaintAndView() {
        if (navigationView2 != null)
            navigationView2.refreshPaintAndView();
    }


    public void refreshPaintAndViewNoRefreshCard() {
        if (navigationView2 != null)
            navigationView2.refreshPaintAndViewNoRefreshCard();
    }

    public void initCardV() {
        if (navigationView2 != null)
            navigationView2.initCardV();
    }


    /**
     * 0屏是否已经加载完成
     *
     * @return
     */
    public boolean isLoaded() {
        if (navigationView2 != null)
            return navigationView2.isLoaded();
        return false;
    }

    public void onShow() {
        if (navigationView2 != null)
            navigationView2.onShow();
    }

    public void showVoiceRecognitionResult(List<String> results) {
        if (navigationView2 != null)
            navigationView2.showVoiceRecognitionResult(results);
    }

    public void startVoiceRecognition() {
        if (navigationView2 != null)
            navigationView2.startVoiceRecognition();
    }


    /**
     * <br>
     * Description: 处理回退键事件 <br>
     * Author:caizp <br>
     * Date:2012-10-14下午05:54:05
     */
    public void onBackKeyDown() {
        if (navigationView2 != null)
            navigationView2.onBackKeyDown();
    }


    public void refreshFavoriteSiteView() {
        if (navigationView2 != null)
            navigationView2.refreshFavoriteSiteView();
    }

    public void showWebSites() {
        if (navigationView2 != null)
            navigationView2.showWebSites();
    }

    public void hideWebSites() {
        if (navigationView2 != null)
            navigationView2.hideWebSites();
    }

    /**
     * 启动跑马灯
     */
    public void startMarquee() {

    }

    /**
     * 关闭跑马灯
     */
    public void stopMarquee() {

    }

    public Handler getHandler() {
        if (navigationView2 != null)
            return navigationView2.getHandler();
        return null;
    }


    public NavigationFavoriteSiteView getFavoriteSiteView() {
        if (navigationView2 != null)
            return navigationView2.getFavoriteSiteView();
        return null;
    }


    public void showNavigationLayout() {
        if (navigationView2 != null)
            navigationView2.showNavigationLayout();
    }

    public void hideNavigationLayout() {
        if (navigationView2 != null)
            navigationView2.hideNavigationLayout();
    }


    public NavigationSiteView getSiteView() {
        if (navigationView2 != null)
            return navigationView2.getSiteView();
        return null;
    }


    public void refresh() {

    }

    public void setCUID(String CUID) {
        if (navigationView2 != null)
            navigationView2.setCUID(CUID);
        Global.CUID = CUID;
    }


    public void hideVideoView() {

    }

    public void updateAndRefreshSiteDetail() {
        if (navigationView2 != null)
            navigationView2.updateAndRefreshSiteDetail();
    }

    public void setHotWordView(List<Object> list) {
        if (navigationView2 != null)
            navigationView2.setHotWordView(list);
    }


    public void onNetworkAvaiable() {
        if (navigationView2 != null)
            navigationView2.onNetworkAvaiable();
    }

    public void upgradePlugin(final String url, final int ver, final boolean isWifiAutoDownload) {
        if (navigationView2 != null)
            navigationView2.upgradePlugin(url, ver, isWifiAutoDownload);
    }

    public void setThemeChoose(int position) {
        if (navigationView2 != null)
            navigationView2.setThemeChoose(position);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (navigationView2 != null)
            return navigationView2.onInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }


    /**
     * @desc 滑动或返回键进入零屏时的回调
     * @author linliangbin
     * @time 2017/4/7 13:48
     */
    public abstract void onShowingNavigationView();


    /**
     * @desc 滑动或返回键离开零屏时的回调
     * @author linliangbin
     * @time 2017/4/7 13:50
     */
    public abstract void onLeavingNavigation();

}
