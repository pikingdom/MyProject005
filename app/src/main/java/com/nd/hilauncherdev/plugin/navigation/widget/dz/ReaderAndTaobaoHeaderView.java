package com.nd.hilauncherdev.plugin.navigation.widget.dz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPagerTab;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshSuperManHeader;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshSuperManWhiteHeader;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nd.hilauncherdev.plugin.navigation.widget.search.HeaderSearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.search.hotword.ComplexHotwordGenerator;
import com.nd.hilauncherdev.plugin.navigation.widget.site.FavoriteSiteLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.third_party.HeaderViewPager;
import com.nd.hilauncherdev.plugin.navigation.widget.upgrade.UpgradeLayout;

import java.util.List;

/**
 * 阅读购物屏-头部组件
 * Created by linliangbin on 2017/9/15 17:06.
 */

public class ReaderAndTaobaoHeaderView extends LinearLayout implements HeaderViewPager.OnScrollListener, PageActionInterface, UpgradeLayout.CancelCallback {

    static int SEARCH_LAYOUT_TOP_MARGIN = 0;
    HeaderSearchLayout headerSearchLayout;
    FavoriteSiteLayout favoriteSiteLayout;
    MyPhoneViewPagerTab myPhoneViewPagerTab;
    UpgradeLayout upgradeLayout;
    private boolean isUpgradeLayoutAdded = false;
    public ReaderAndTaobaoHeaderView(Context context) {
        super(context);
        init();
    }


    public ReaderAndTaobaoHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public static float getScrollRateByDistance(int scrollY) {

        float rate = 1 - scrollY / (float) SEARCH_LAYOUT_TOP_MARGIN;
        if (rate >= 1.0f) {
            rate = 1.0f;
        }
        if (rate <= 0.0f) {
            rate = 0.0f;
        }
        return rate;

    }

    public void loadFavoriteSites() {
        if (favoriteSiteLayout != null) {
            favoriteSiteLayout.loadSites();
        }
    }

    private void init() {


        SEARCH_LAYOUT_TOP_MARGIN = (getContext().getResources().getDimensionPixelSize(R.dimen.header_layout_top_margin)
                + getContext().getResources().getDimensionPixelSize(R.dimen.search_layout_horoscope_height));

        setOrientation(VERTICAL);
        headerSearchLayout = new HeaderSearchLayout(getContext());
        headerSearchLayout.updateLeftRightMargin(HeaderSearchLayout.SCROLL_RATE_START);
        addView(headerSearchLayout);

        PullToRefreshSuperManHeader superManHeader = new PullToRefreshSuperManWhiteHeader(getContext());
        superManHeader.setId(R.id.reader_and_taobao_viewpager_pull_refresh_header);
        addView(superManHeader);

        favoriteSiteLayout = new FavoriteSiteLayout(getContext(), false);
        favoriteSiteLayout.setAddIconMask(true);
        addView(favoriteSiteLayout);

        upgradeLayout = new UpgradeLayout(getContext());
        upgradeLayout.setCancelCallback(this);
        upgradeLayout.setVisibility(GONE);
        addView(upgradeLayout);

        myPhoneViewPagerTab = new MyPhoneViewPagerTab(getContext());
        myPhoneViewPagerTab.setId(R.id.reader_and_taobao_viewpager_tab);
        addView(myPhoneViewPagerTab, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(getContext(), 45)));
    }

    public void setSearchLayoutGenerator(ComplexHotwordGenerator generator) {
        if (headerSearchLayout != null) {
            headerSearchLayout.setHotwordGenerator(generator);
        }
    }


    @Override
    public void onScroll(int currentY, int maxY) {
        float computeRate = currentY / ScreenUtil.dip2px(getContext(), 30);
        headerSearchLayout.updateLeftRightMargin(computeRate);
    }

    @Override
    public void onFlingEnd(long endTime) {

    }

    public void updateSearchVisiblity(int visibility) {
        if (headerSearchLayout != null) {
            headerSearchLayout.setVisibility(visibility);
            updateHotwordSwitch(VISIBLE == visibility);
        }
    }

    public void updateHotwordSwitch(boolean enable) {
        if (enable) {
            headerSearchLayout.onStartHotwordSwitch();
        } else {
            headerSearchLayout.onStopHotwordSwitch();
        }
    }

    public void setHotword(List<HotwordItemInfo> list) {

        if (headerSearchLayout != null) {
            headerSearchLayout.setHotword(list);
        }
    }

    public void updateSearchMargin(float rate) {
        if (headerSearchLayout != null) {
            headerSearchLayout.updateLeftRightMargin(rate);
        }
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
        Log.i("llbeing", "onActionRefreshSync");
        if (NavigationLoader.getInstance().updateIconAndSiteData(getContext())) {
            Global.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("llbeing", "onActionRefreshSync:loadSites");
                    favoriteSiteLayout.loadSites();
                }
            });
        }
    }

    @Override
    public void doDailyUpdate() {

    }

    public void removeUpgradeLayout() {
        if (upgradeLayout != null) {
            upgradeLayout.setVisibility(GONE);
            isUpgradeLayoutAdded = false;
        }
    }

    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {

        if (upgradeLayout != null) {
            if (!isUpgradeLayoutAdded) {
                upgradeLayout.setVisibility(VISIBLE);
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
