package com.nd.hilauncherdev.plugin.navigation.widget.navigation;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdPluginDownloadManager;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdViewManager;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.GlobalConst;
import com.nd.hilauncherdev.plugin.navigation.util.ActualTimeAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationFavoriteSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.PageListenerImp;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.NewsListGoTopControler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linliangbin on 2017/4/11 9:39.
 */

public class BaseNavigationSearchView extends RelativeLayout implements NewsListGoTopControler.PluginAdViewChecker,PageActionInterface {

    public static final int HANDLER_DOWNLOA_AD_FINISH = 106;
    private static final int START_DATE_INDEX = 0;
    private static final int END_DATE_INDEX = 1;
    public static ArrayList<String> playDuration = new ArrayList<String>();
    public static String CUID = "";
    //新闻列表的view
    public static boolean isShowNewsBelowCard = false;
    public static Activity activity;
    /**
     * 当前是否在显示零屏全屏小动画View
     */
    public static boolean isCurrentShowSmallAdView = false;
    public static int CV_PAGE_ID = -1;
    private static long lastShowingTime = 0;
    // 显示零屏和离开零屏的时间间隔在间隔以内，则不显示动画更新
    private static int DURATION_LIMIT_SKIPPED = 250;
    // 滑动到零屏时的delay 时间
    private static int DELAY_WHEN_SHOWING = 600;
    public View pluginAdView;
    public Context context;
    public boolean needReportingAdShow = false;
    public boolean isShowingAd = false;
    public PageListenerImp pageImp;
    public int HANDLER_ADD_PLUGING_VIEW = 100;
    public int HANDLER_REMOVE_PLUGING_VIEW = 101;
    public int HANDLER_START_PLAYING = 102;
    public int HANDLER_PRE_START_PLAYING = 103;
    public int HANDLER_PRE_STOP_PLAYING = 104;
    public int HANDLER_STOP_PLAYING = 105;
    /**
     * 全屏动画底部边距
     */
    public int adMarginBottom = 0;
    public Handler pluginAdHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_ADD_PLUGING_VIEW) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.bottomMargin = adMarginBottom;
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = true;
                    addView(pluginAdView, lp);
                    sendEmptyMessage(HANDLER_START_PLAYING);
                }
            } else if (msg.what == HANDLER_REMOVE_PLUGING_VIEW) {
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = false;
                    BaseNavigationSearchView.this.removeView(pluginAdView);
                    pluginAdView = null;
                }
            } else if (msg.what == HANDLER_START_PLAYING) {
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = true;
                    AdViewManager.startShowing(BaseNavigationSearchView.this.context, pluginAdView, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
                }
            } else if (msg.what == HANDLER_PRE_START_PLAYING) {
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = true;
                    AdViewManager.startShowingPre(BaseNavigationSearchView.this.context, pluginAdView, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
                }
            } else if (msg.what == HANDLER_PRE_STOP_PLAYING) {
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = false;
                    AdViewManager.stopShowingPre(context, pluginAdView, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
                }
            } else if (msg.what == HANDLER_STOP_PLAYING) {
                if (pluginAdView != null) {
                    isCurrentShowSmallAdView = false;
                    AdViewManager.stopShowing(context, pluginAdView, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
                }
            } else if (msg.what == HANDLER_DOWNLOA_AD_FINISH) {
                refreshAdView();
                reReadAdViewDuration();
            }
        }
    };
    private boolean isShowingPluginAd = false;
    public BaseNavigationSearchView(Context context) {
        super(context);
        this.context = context;
        reReadAdViewDuration();

    }

    public BaseNavigationSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        reReadAdViewDuration();
    }

    public boolean isShowingPluginAd() {
        return isShowingPluginAd;
    }

    public void refreshAdView() {

    }

    public void refreshPaintAndView() {

    }

    public void refreshPaintAndViewNoRefreshCard() {

    }

    public void initCardV() {

    }

    public boolean isLoaded() {
        return true;
    }

    public void onShow() {

    }

    public void showVoiceRecognitionResult(List<String> results) {

    }

    public void startVoiceRecognition() {

    }

    public void refreshFavoriteSiteView() {

    }

    public void showWebSites() {

    }

    public void hideWebSites() {

    }

    public void showNavigationLayout() {

    }

    public void hideNavigationLayout() {

    }

    public void setCUID(String CUID) {
        NavigationView2.CUID = CUID;
    }

    public void onNetworkAvaiable() {

    }

    public void upgradePlugin(final String url, final int ver, final boolean isWifiAutoDownload) {

    }


    public void setThemeChoose(int position) {

    }

    private void loadNavigationAd() {

    }

    public void updateAndRefreshSiteDetail() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                updateFullScreenAdData();
            }
        });
    }


    public void setHotWordView(List<Object> list) {

    }

    public NavigationFavoriteSiteView getFavoriteSiteView() {
        return null;
    }

    public NavigationSiteView getSiteView() {
        return null;
    }


    /**
     * @desc 桌面每日任务回调
     * @author linliangbin
     * @time 2017/4/11 20:00
     */
    public void onLauncherStart() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                updateFullScreenAdData();
            }
        });
    }


    public void doInBackground() {

    }

    public void complete() {

    }


    public void setPageImp(PageListenerImp pageImp) {
        this.pageImp = pageImp;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * <br>
     * Description: 处理回退键事件 <br>
     * Author:caizp <br>
     * Date:2012-10-14下午05:54:05
     */
    public void onBackKeyDown() {

    }

    /**
     * 请求广告
     */
    public void onlyRequestAD() {

    }

    /**
     * @desc 重新读取全屏广告播放时间段
     * @author linliangbin
     * @time 2017/4/11 9:58
     */
    public void reReadAdViewDuration() {
        playDuration = AdViewManager.getPlayDateDuration(context, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
    }

    /**
     * @desc 桌面onPause 时调用
     * @author linliangbin
     * @time 2017/6/5 19:53
     */
    public void onPause() {

    }

    /**
     * @desc 桌面滑动或是返回键进入显示零屏回调
     * @author linliangbin
     * @time 2017/4/11 9:48
     */
    public void onShowingNavigationView(boolean isSohuAtRight) {

        //当前是否在广告显示区间内，否，则跳过
        if (playDuration != null && playDuration.size() == 2 && !TextUtils.isEmpty(playDuration.get(START_DATE_INDEX))
                && !TextUtils.isEmpty(playDuration.get(END_DATE_INDEX))
                && AdViewManager.isBetween(playDuration.get(START_DATE_INDEX), playDuration.get(END_DATE_INDEX))) {
            isShowingPluginAd = true;
            needReportingAdShow = true;
            if (isShowingAd) {
                ActualTimeAnalysis.sendActualTimeAnalysis(context, ActualTimeAnalysis.TAOBAO_VALID_HITS, "0tls");
                needReportingAdShow = false;
            }
            if (pluginAdView != null) {
                pluginAdHandler.sendEmptyMessage(HANDLER_PRE_START_PLAYING);
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShowingTime <= DURATION_LIMIT_SKIPPED) {
                return;
            } else {
                lastShowingTime = currentTime;
                pluginAdHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View tempView;
                        tempView = AdViewManager.getAdView(context, GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);
                        if (tempView != null && pluginAdView == null) {
                            //在显示时间内，且未添加过
                            pluginAdView = tempView;
                            pluginAdHandler.sendEmptyMessage(HANDLER_ADD_PLUGING_VIEW);
                        } else if (tempView == null && pluginAdView != null) {
                            //已经添加过，并且已经不在显示时间内（或是找不到插件）了，移除
                            pluginAdHandler.sendEmptyMessage(HANDLER_REMOVE_PLUGING_VIEW);
                        } else if (tempView != null && pluginAdView != null) {
                            //已经添加过，并且还在显示时间内
                            pluginAdHandler.sendEmptyMessage(HANDLER_START_PLAYING);
                        }
                    }

                }, DELAY_WHEN_SHOWING);
            }

        } else {
            isShowingPluginAd = false;
            //已经不在播放区间内，移除VIEW
            pluginAdHandler.sendEmptyMessage(HANDLER_REMOVE_PLUGING_VIEW);
        }

    }

    /**
     * @desc 更新全屏广告数据
     * @author linliangbin
     * @time 2017/9/22 17:10
     */
    public void updateFullScreenAdData() {

        try {
            AdPluginDownloadManager.setCallback(new AdPluginDownloadManager.DownloadFinishCallback() {
                @Override
                public void onDownloadSuccess() {
                    if (pluginAdHandler != null)
                        pluginAdHandler.sendEmptyMessage(NavigationView2.HANDLER_DOWNLOA_AD_FINISH);
                }
            });
            AdPluginDownloadManager.downloadAdPlugin(context, URLs.getAdPluginUrl(context), GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @desc 离开零屏回到桌面回调
     * @author linliangbin
     * @time 2017/4/11 9:47
     */
    public void onLeavingNavigation() {

        if (pluginAdView != null) {
            pluginAdHandler.sendEmptyMessage(HANDLER_PRE_STOP_PLAYING);
        }
        pluginAdHandler.removeMessages(HANDLER_START_PLAYING);
        pluginAdHandler.removeMessages(HANDLER_PRE_START_PLAYING);
        lastShowingTime = System.currentTimeMillis();

        pluginAdHandler.sendEmptyMessage(HANDLER_STOP_PLAYING);
    }


    /**
     * @desc 零屏销毁时调用
     * @author linliangbin
     * @time 2017/6/14 14:25
     */
    public void onDestroy() {

    }


    /**
     * @desc resume 时的回调
     * @author linliangbin
     * @time 2017/8/8 19:06
     */
    public void handleResume() {

    }

    @Override
    public boolean isAvailablePluinAdView() {
        return pluginAdView != null;
    }

    @Override
    public void onEnterPage() {

    }

    @Override
    public void onLeavePage() {

    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onBackToPage() {

    }

    @Override
    public void onActionRefresh() {

    }

    @Override
    public void onActionRefreshSync() {
        updateFullScreenAdData();
    }

    @Override
    public void doDailyUpdate() {
        updateFullScreenAdData();
    }
}
