package com.nd.hilauncherdev.plugin.navigation.widget.combine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayer;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.combine.adapter.VideoListAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.pull.WallpaperHeaderListView;
import com.nd.hilauncherdev.plugin.navigation.widget.search.HeaderSearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.search.PinnedSearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.site.FavoriteSiteLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.upgrade.UpgradeLayout;

import java.util.List;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * Created by linliangbin on 2017/4/28 15:10.
 */

public class VideoNavigationView extends RelativeLayout implements WallpaperHeaderListView.ListViewScroll, PinnedSearchLayout.SeachActionListener, UpgradeLayout.CancelCallback {


    public static final int MSG_ENABLE_SWITCH = 1000;
    public static final int MSG_DISABLE_SWITCH = 1001;
    ImageView listBgImage;
    WallpaperHeaderListView resourceListView;
    VideoListAdapter videoListAdapter;
    HeaderSearchLayout headerSearchLayout;
    PinnedSearchLayout pinnedTopSearchLayout;
    UpgradeLayout upgradeLayout;
    private float currentRate = HeaderSearchLayout.SCROLL_RATE_START;
    /**
     * 是否需要裁剪显示范围  下拉时不需要  从壁纸界面上拉时需要
     */
    private boolean needDispatchDraw = false;
    private Handler HotwordSwitchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DISABLE_SWITCH:

                    endHotwordSwitch();
                    headerSearchLayout.setEnableSwitcher(false);
                    pinnedTopSearchLayout.setEnableSwitcher(false);
                    break;
                case MSG_ENABLE_SWITCH:
                    startHotwordSwitch();
                    headerSearchLayout.setEnableSwitcher(true);
                    pinnedTopSearchLayout.setEnableSwitcher(true);
                    break;
            }
        }
    };
    private boolean isUpgradeLayoutAdded = false;


    public VideoNavigationView(Context context) {
        super(context);
        init();
    }

    public VideoNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setNeedDispatchDraw(boolean needDispatchDraw) {
        this.needDispatchDraw = needDispatchDraw;
    }

    private void init() {


        LayoutInflater.from(getContext()).inflate(R.layout.layout_video_navigation_list, this);
        pinnedTopSearchLayout = (PinnedSearchLayout) findViewById(R.id.pinned_top_search_layout);
        pinnedTopSearchLayout.setListener(this);
        resourceListView = (WallpaperHeaderListView) findViewById(R.id.listview);

        headerSearchLayout = new HeaderSearchLayout(getContext());
        headerSearchLayout.updateLeftRightMargin(HeaderSearchLayout.SCROLL_RATE_START);
        resourceListView.addHeaderView(headerSearchLayout);

        FavoriteSiteLayout favoriteSiteLayout = new FavoriteSiteLayout(getContext());
        resourceListView.addHeaderView(favoriteSiteLayout);

        upgradeLayout = new UpgradeLayout(getContext());
        upgradeLayout.setCancelCallback(this);
        videoListAdapter = new VideoListAdapter(getContext(), resourceListView);
        resourceListView.init();
        resourceListView.setAdapter(videoListAdapter);
        resourceListView.setContainer(this);
        resourceListView.setListViewScroll(this);

        listBgImage = (ImageView) findViewById(R.id.iv_listview_bg);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (needDispatchDraw) {
            canvas.clipRect(new Rect(0, resourceListView.getDistanceY(), ScreenUtil.getCurrentScreenWidth(getContext()), ScreenUtil.getCurrentScreenHeight(getContext())));
        }
        super.dispatchDraw(canvas);
    }

    /**
     * @desc 壁纸界面上滑时更新当前滑动距离
     * @author linliangbin
     * @time 2017/5/2 14:13
     */
    public void onUpdatePullDistance(int distanceY) {

        if (this.getVisibility() != VISIBLE) {
            this.setVisibility(VISIBLE);
        }
        needDispatchDraw = true;
        if (resourceListView != null) {
            resourceListView.setDistanceY(ScreenUtil.getCurrentScreenHeight(getContext()) - distanceY);
            invalidate();
        }
    }

    private void startHotwordSwitch() {

        if (pinnedTopSearchLayout != null && pinnedTopSearchLayout.getVisibility() == VISIBLE) {
            pinnedTopSearchLayout.onStartHotwordSwitch();
        }
        if (headerSearchLayout != null && headerSearchLayout.getVisibility() == VISIBLE) {
            headerSearchLayout.onStartHotwordSwitch();
        }
    }

    private void endHotwordSwitch() {
        if (pinnedTopSearchLayout != null) {
            pinnedTopSearchLayout.onStopHotwordSwitch();
            pinnedTopSearchLayout.onEndAnimation();
        }
        if (headerSearchLayout != null) {
            headerSearchLayout.onStopHotwordSwitch();
        }
    }

    public void onShowing() {
        startHotwordSwitch();
    }

    public void onLeaving() {
        endHotwordSwitch();

        try {
            if (NavigationView2.activity != null) {
                NavigationView2.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        onLeaving();
    }

    public void updateSearchLayout(int scrollY) {
        float computeRate = headerSearchLayout.getScrollRateByDistance(scrollY);
        if (Math.abs(currentRate - computeRate) <= 0.001) {
            return;
        }
        currentRate = computeRate;
        if (currentRate == HeaderSearchLayout.SCROLL_RATE_END) {
            pinnedTopSearchLayout.setVisibility(VISIBLE);
            resourceListView.setBackgroundColor(Color.parseColor("#ffffff"));
            headerSearchLayout.setVisibility(INVISIBLE);
        } else {
            pinnedTopSearchLayout.setVisibility(INVISIBLE);
            resourceListView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.video_list_bg));
            headerSearchLayout.setVisibility(VISIBLE);
        }
        headerSearchLayout.updateLeftRightMargin(currentRate);

    }

    /**
     * @desc 更新高斯模糊图片背景
     * @author linliangbin
     * @time 2017/6/12 12:35
     * @deprecated
     */
    public void updateWallpaperBg(final String imgPath) {

//        ThreadUtil.executeMore(new Runnable() {
//            @Override
//            public void run() {
//
//                final Bitmap bitmap = BlurUtil.blutBitmap(imgPath);
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    Global.runInMainThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            listBgImage.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//            }
//        });

    }

    @Override
    public void onListViewScroll(int scrollY) {
        updateSearchLayout(scrollY);
    }

    @Override
    public void onScrollStateChanged(int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            HotwordSwitchHandler.removeMessages(MSG_ENABLE_SWITCH);
            HotwordSwitchHandler.removeMessages(MSG_DISABLE_SWITCH);
            HotwordSwitchHandler.sendEmptyMessageDelayed(MSG_ENABLE_SWITCH, 2000);
        } else {
            HotwordSwitchHandler.removeMessages(MSG_ENABLE_SWITCH);
            HotwordSwitchHandler.removeMessages(MSG_DISABLE_SWITCH);
            if (headerSearchLayout.enableSwitcher || pinnedTopSearchLayout.enableSwitcher) {
                HotwordSwitchHandler.sendEmptyMessage(MSG_DISABLE_SWITCH);
            }
        }
    }

    public void setHotWordView(List<HotwordItemInfo> list) {

        if (headerSearchLayout != null) {
            headerSearchLayout.setHotword(list);
        }
        if (pinnedTopSearchLayout != null) {
            pinnedTopSearchLayout.setHotword(list);
        }
    }

    @Override
    public void clickRefresh() {
        /** 当当前视频已经显示超过50页时，页码归零 */
        if (CardManager.getInstance().getVideoListPageIndex(getContext()) >= 50) {
            CardManager.getInstance().setVideoListPageIndex(getContext(), 1);
        }

        JCVideoPlayer.releaseAllVideos();
        if (videoListAdapter != null) {
            videoListAdapter.doRefresh();
        }
    }

    public float getCurrentRate() {
        return currentRate;
    }

    public void removeUpgradeLayout() {
        if (upgradeLayout != null && resourceListView != null) {
            resourceListView.removeHeaderView(upgradeLayout);
            isUpgradeLayoutAdded = false;
        }
    }

    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {

        if (upgradeLayout != null) {
            if (!isUpgradeLayoutAdded) {
                resourceListView.addHeaderView(upgradeLayout);
                isUpgradeLayoutAdded = true;
            }
            upgradeLayout.upgradePlugin(url, ver, isWifiAutoDownload);
        }
    }

    @Override
    public void onCancleUpgrade() {
        removeUpgradeLayout();
    }
}
