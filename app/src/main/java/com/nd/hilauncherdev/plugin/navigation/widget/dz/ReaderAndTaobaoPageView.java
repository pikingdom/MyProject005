package com.nd.hilauncherdev.plugin.navigation.widget.dz;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviWordLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPager;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPagerInner;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPagerTab;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshAbsHeader;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshView;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.ReaderPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nd.hilauncherdev.plugin.navigation.widget.openpage.PageCountSetter;
import com.nd.hilauncherdev.plugin.navigation.widget.search.HeaderSearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.search.PinnedSearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.search.hotword.ComplexHotwordGenerator;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoExtPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.third_party.HeaderScrollHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.third_party.HeaderViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 定制版购物订阅合并零屏
 * Created by linliangbin on 2017/9/12 18:55.
 */

public class ReaderAndTaobaoPageView extends BaseNavigationSearchView
        implements PageActionInterface, HeaderViewPager.OnScrollListener,
        MyPhoneViewPager.InternalTouchHandler, PullToRefreshView.P2RListViewStateListener, PinnedSearchLayout.SeachActionListener, MyPhoneViewPager.OnPageSelectedListenner {

    HeaderViewPager headerViewPager;
    MyPhoneViewPagerInner viewPager;
    MyPhoneViewPagerTab viewPagerTab;

    ReaderAndTaobaoHeaderView headerLayout;

    ReaderPageView readerPageView;
    TaoBaoExtPageView taoBaoExtPageView;
    PinnedSearchLayout pinnedSearchLayout;
    ComplexHotwordGenerator hotwrodGenerator;
    private int lastSelectedPage = -1;
    /**
     * 当前页页码
     */
    private int pageIndex;
    private float currentRate = HeaderSearchLayout.SCROLL_RATE_START;
    private String[] titleResIds = null;
    private String[] titleResReaderLeft = {"小说阅读", "淘你喜欢"};
    private String[] titleResTaobaoLeft = {"淘你喜欢", "小说阅读"};

    public ReaderAndTaobaoPageView(Context context) {
        super(context);
        init();
    }


    public ReaderAndTaobaoPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onLauncherStart() {
        super.onLauncherStart();
        if (NavigationLoader.getInstance().isRequestCounterIn()) {
            ThreadUtil.executeMore(new Runnable() {
                @Override
                public void run() {
                    updateFullScreenAdData();
                    if (NavigationLoader.getInstance().updateIconAndSiteData(context)) {
                        Global.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (headerLayout != null) {
                                    headerLayout.loadFavoriteSites();
                                }
                                if (readerPageView != null){
                                    readerPageView.onActionRefresh();
                                }
                                if (taoBaoExtPageView != null){
                                    taoBaoExtPageView.onActionRefresh();
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public void setHotWordView(List<Object> list) {
        super.setHotWordView(list);

        ArrayList<HotwordItemInfo> itemList = NaviWordLoader.convertHotwordList(list);
        if (headerLayout != null) {
            headerLayout.setHotword(itemList);
        }
        if (pinnedSearchLayout != null) {
            pinnedSearchLayout.setHotword(itemList);
        }
    }

    public void init() {

        hotwrodGenerator = new ComplexHotwordGenerator(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, ScreenUtil.getStatusBarHeight(getContext()), 0, 0);
        addView(LayoutInflater.from(getContext()).inflate(R.layout.layout_reader_and_taobao_page, null), layoutParams);
        headerViewPager = (HeaderViewPager) findViewById(R.id.scrollableLayout);
        headerViewPager.setOnScrollListener(this);
        headerViewPager.setHeaderView((PullToRefreshAbsHeader) findViewById(R.id.reader_and_taobao_viewpager_pull_refresh_header));
        headerViewPager.setListener(this);

        headerLayout = (ReaderAndTaobaoHeaderView) findViewById(R.id.header_layout);
        headerLayout.setSearchLayoutGenerator(hotwrodGenerator);

        pinnedSearchLayout = (PinnedSearchLayout) findViewById(R.id.layout_reader_taobao_pinned_search);
        pinnedSearchLayout.setHotwordGenerator(hotwrodGenerator);
        pinnedSearchLayout.setListener(this);

        viewPager = (MyPhoneViewPagerInner) findViewById(R.id.viewPager);
        viewPager.setOnPageSelectedListenner(this);
        viewPager.setNeedAlphaChange(false);
        viewPagerTab = (MyPhoneViewPagerTab) findViewById(R.id.reader_and_taobao_viewpager_tab);

        initViewPager();

    }


    private void initViewPager() {

        readerPageView = new ReaderPageView(getContext());
        readerPageView.disableSearch();
        readerPageView.disablePullRefresh();
//        headerViewPager.setCurrentScrollableContainer(readerPageView);

        taoBaoExtPageView = new TaoBaoExtPageView(getContext(), false);
        taoBaoExtPageView.disablePull();

        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            if (LauncherBranchController.isXiangshu(getContext()) || LauncherBranchController.isFanYue(getContext())) {
                viewPager.addView(taoBaoExtPageView);
                viewPager.addView(readerPageView);
                titleResIds = titleResTaobaoLeft;
                headerViewPager.setCurrentScrollableContainer(taoBaoExtPageView);
            } else {
                viewPager.addView(readerPageView);
                viewPager.addView(taoBaoExtPageView);
                titleResIds = titleResReaderLeft;
                headerViewPager.setCurrentScrollableContainer(readerPageView);
            }
        }

        viewPagerTab.addTitle(titleResIds);
        viewPagerTab.setViewpager(viewPager);
        viewPagerTab.setUsedAsSecondTitle();
        viewPager.setTab(viewPagerTab);

        viewPager.setInitTab(0);
        viewPagerTab.setInitTab(0);

    }


    @Override
    public void onEnterPage() {
        Log.i("llbeing", "ReaderTaobao：onEnterPage");
        lastSelectedPage = getCurrentPageIndex();
        if (hotwrodGenerator != null) {
            if (!hotwrodGenerator.isServerHotwordAvailable()) {
                try {
                    NavigationKeepForReflect.reloadeHotwordData();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            hotwrodGenerator.reset();
        }
        onUpdateScrollRate(currentRate);
        super.onShowingNavigationView(false);
        View view = viewPager.getChildAt(viewPager.getCurrentScreen());
        if (view instanceof PageActionInterface) {
            ((PageActionInterface) view).onEnterPage();
        }

    }

    @Override
    public void onLeavePage() {
        pinnedSearchLayout.onStopHotwordSwitch();
        headerLayout.updateHotwordSwitch(false);
        super.onLeavingNavigation();
        View view = viewPager.getChildAt(viewPager.getCurrentScreen());
        if (view instanceof PageActionInterface) {
            ((PageActionInterface) view).onLeavePage();
        }
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

    }

    @Override
    public void doDailyUpdate() {
        super.doDailyUpdate();

    }

    public void setHotWordView2() {

        ArrayList<HotwordItemInfo> itemList = new ArrayList<HotwordItemInfo>();
        HotwordItemInfo hotwordItemInfo = new HotwordItemInfo();
        hotwordItemInfo.name = "测试热词1";
        itemList.add(hotwordItemInfo);

        HotwordItemInfo hotwordItemInfo2 = new HotwordItemInfo();
        hotwordItemInfo2.name = "测试热词2";
        itemList.add(hotwordItemInfo2);

        HotwordItemInfo hotwordItemInfo3 = new HotwordItemInfo();
        hotwordItemInfo3.name = "测试热词3";
        itemList.add(hotwordItemInfo3);

        HotwordItemInfo hotwordItemInfo4 = new HotwordItemInfo();
        hotwordItemInfo4.name = "测试热词4";
        itemList.add(hotwordItemInfo4);

        if (headerLayout != null) {
            headerLayout.setHotword(itemList);
        }
        if (pinnedSearchLayout != null) {
            pinnedSearchLayout.setHotword(itemList);
        }
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        float computeRate = headerLayout.getScrollRateByDistance(currentY);
        if (Math.abs(currentRate - computeRate) <= 0.001) {
            return;
        }
        currentRate = computeRate;
        onUpdateScrollRate(currentRate);
        headerLayout.updateSearchMargin(currentRate);
    }

    @Override
    public void onFlingEnd(final long endTime) {
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("llbeing", "onFlingEnd");
                if (hotwrodGenerator != null) {
                    hotwrodGenerator.resetOneStepCounter();
                }
            }
        }, endTime);
    }

    private void onUpdateScrollRate(float currentRate) {

        Log.i("llbeing", "onUpdateScrollRate:" + currentRate);
        if (currentRate == HeaderSearchLayout.SCROLL_RATE_END) {
            pinnedSearchLayout.setVisibility(VISIBLE);
            pinnedSearchLayout.onStartHotwordSwitch();
            headerLayout.updateSearchVisiblity(View.INVISIBLE);
        } else {
            pinnedSearchLayout.setVisibility(INVISIBLE);
            pinnedSearchLayout.onStopHotwordSwitch();
            headerLayout.updateSearchVisiblity(View.VISIBLE);
        }
    }

    @Override
    public void handleResume() {
        if (readerPageView != null) {
            readerPageView.handleResume();
        }
    }

    @Override
    public void onUpdate(int selectedPage, boolean finish) {
        headerViewPager.setCurrentScrollableContainer(
                (HeaderScrollHelper.ScrollableContainer) viewPager.getChildAt(viewPager.getCurrentScreen()));

        if (lastSelectedPage != selectedPage && finish) {
            View enterView = viewPager.getChildAt(selectedPage);
            View leaveView = viewPager.getChildAt(lastSelectedPage);
            if (leaveView instanceof PageActionInterface) {
                ((PageActionInterface) leaveView).onLeavePage();
            }
            if (enterView instanceof PageActionInterface) {
                ((PageActionInterface) enterView).onEnterPage();
            }

            lastSelectedPage = selectedPage;
        }


    }

    /**
     * @desc 是否在touch viewpager 布局
     * @author linliangbin
     * @time 2017/10/12 19:38
     */
    private boolean isTouchViewPager(MotionEvent event) {

        int[] location = new int[2];
        viewPager.getLocationOnScreen(location);
        Rect rect = new Rect();
        rect.left = location[0];
        rect.top = location[1];
        rect.right = ScreenUtil.getCurrentScreenWidth(getContext());
        rect.bottom = ScreenUtil.getCurrentScreenHeight(getContext());
        boolean result = rect.contains((int) event.getRawX(), (int) event.getRawY());
        return result;
    }

    @Override
    public boolean innerHandleTouch(MotionEvent event) {
        if (!isTouchViewPager(event)) {
            return false;
        }
        if (viewPager != null) {
            return viewPager.handleInnerTouch(event);
        }
        return false;
    }

    @Override
    public int getCurrentPageIndex() {
        return viewPager.getCurrentScreen();
    }

    @Override
    public int getPageCount() {
        return viewPager.getChildCount();
    }

    private void doPullRefresh() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {

                headerLayout.onActionRefreshSync();
                for (int i = 0; viewPager != null && i < viewPager.getChildCount(); i++) {
                    View view = viewPager.getChildAt(i);
                    if (view instanceof PageActionInterface) {
                        ((PageActionInterface) view).onActionRefreshSync();
                    }
                }
                updateFullScreenAdData();


                Global.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        headerViewPager.setRecover();
                    }
                });
            }
        });
    }

    @Override
    public void onStateChanged(int state) {
        if (state == PullToRefreshView.P2RListViewStateListener.STATE_LOADING) {
            doPullRefresh();
        }
    }

    @Override
    public void clickRefresh() {

        int currentScreen = viewPager.getCurrentScreen();
        View view = viewPager.getChildAt(currentScreen);
        if (view instanceof PageActionInterface) {
            ((PageActionInterface) view).onActionRefresh();
        }

    }

    @Override
    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {
        super.upgradePlugin(url, ver, isWifiAutoDownload);
        if (headerLayout != null) {
            headerLayout.upgradePlugin(url, ver, isWifiAutoDownload);
        }
    }
}
