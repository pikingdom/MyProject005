package com.nd.hilauncherdev.plugin.navigation.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.NavigationLauncherInterface;
import com.nd.hilauncherdev.plugin.navigation.NavigationPreferences;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.kit.advert.AdControllerWrapper;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.NavigationHelper;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;
import com.nd.hilauncherdev.plugin.navigation.widget.ScrollViewWithAnalytics.RefreshCallBack;
import com.nd.hilauncherdev.plugin.navigation.widget.ad.NavigationAdHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.ad.PopupAdLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.ReaderPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.browser.WebPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.combine.CombineNavigationView;
import com.nd.hilauncherdev.plugin.navigation.widget.dz.ReaderAndTaobaoPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.fenghuang.FenghuangPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationView;
import com.nd.hilauncherdev.plugin.navigation.widget.netease.NeteaseNewsPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.openpage.PageCountSetter;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoData;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoExtPageView;

import java.util.Arrays;
import java.util.List;

/**
 * <br>
 * Description: 第0屏布局View <br>
 * Author:chenzhihong_9101910 <br>
 * Date:2015-03-25
 */
public class NavigationView extends BaseNavigationView implements RefreshCallBack, PageListenerImp, NavigationLauncherInterface, ZeroViewPagerTab.TabLisener {

    /**
     * 正常展示资讯屏
     * 零屏底部不显示新闻
     */
    public static final int TYPE_NAVIGATION_NORMAL = 0;
    /**
     * 不展示资讯屏
     * 零屏底部显示新闻
     */
    public static final int TYPE_NAVIGATION_SHOW_NEWS_BELOW_NAVI = 1;
    /**
     * 目前支持五个屏
     */
    public static final int MAX_PAGE_COUNT = 100;
    public static final int MSG_JUMP_TO_SOHU_GUIDE_ACTIVITY = 1002;
    /**
     * 当前渠道号
     */
    public static String CHANNEL_ID = "";
    public Activity activity;
    /**
     * 这个方法会被桌面反射调用
     */

    public boolean isForceJumpToPage = false;
    ZeroViewPagerTab tab;
    MyPhoneViewPager myPhoneViewPager;
    //全屏广告界面
    PopupAdLayout popupAdLayout;
    //网易资讯屏
    NeteaseNewsPageView neteaseNewsPageView = null;
    //淘宝购物屏
    TaobaoPageView taobaoPageView;
    //唯品会购物屏
    OpenScreenView vipPageView;
    //搜狐新闻屏
    SohuPageView sohuPageView;
    //凤凰新闻屏
    FenghuangPageView fenghuangPageView;
    //掌阅阅读屏
    ReaderPageView readerPageView;
    //阅读购物屏
    ReaderAndTaobaoPageView readerAndTaobaoPageView;
    //淘宝扩展页
    TaoBaoExtPageView taoBaoExtPageView;
    //web页
    WebPageView webPageView;
    boolean mPageArray[];
    //上次初始化时的零屏类型
    int lastType = -1;
    int AdIDArray[] = new int[MAX_PAGE_COUNT];
    /**
     * 零屏是否在显示
     */
    boolean mIsOpenPageShow = true;
    /**
     * 当前各个屏的展示位置
     */
    // 零屏展示的位置
    int searchPageIndex = -1;
    // 购物屏展示的位置
    int TaoPageIndex = -1;
    // 新闻屏展示的位置
    int newsPageIndex = -1;
    // 唯品会屏展示的位置
    int vipIndex = -1;
    // 搜狐新闻屏展示的位置
    int sohuIndex = -1;
    // 凤凰新闻屏展示的位置
    int fenghuangIndex = -1;
    // 掌阅阅读屏展示的位置
    int ireaderIndex = -1;
    // 淘宝扩展页屏展示的位置
    int taoBaoExtIndex = -1;
    // 阅读购物屏展示的位置
    int readerTaobaoIndex = -1;
    // web屏展示的位置
    int webIndex = -1;
    private Context context;
    /**
     * 进入时置成true
     * 离开时置成false
     * 避免重复调用
     */
    private boolean isInShowing = false;
    private int lastSelectedPage = -1;
    private int defaultSelect = 2;
    private int screenW = 0;
    //广告是否已经初始化
    private boolean isInitAd = false;
    private boolean isShowingSohu = false;
    private boolean isShowingFenghuang = false;
    private boolean isShowingIreader = false;
    private boolean isShowingTaoBaoExt = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_JUMP_TO_SOHU_GUIDE_ACTIVITY:
                    try {
                        Intent intent = new Intent();
                        intent.setClassName(context, "com.nd.hilauncherdev.launcher.navigation.SohuShowTipsActivity");
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }

        }

    };
    public NavigationView(Context context) {
        super(context);
        this.context = context;
        Global.context = context;
        Global.CURRENT_VERSION_CODE = TelephoneUtil.getVersionCode(context);
        CommonLauncherControl.initLauncher(getContext().getPackageName());
        TaobaoData.getInstance().init(context);
        if (NavigationPreferences.getInstance(context).isNavigationFlagForNewInstall()) {
            NavigationHelper.handleNavigationForNewAllChannel(this.getContext());
            if (ReflectInvoke.getIsAnzhi()) {// 安智市场渠道新用户默认关闭导航屏 caizp 2015-04-14
                NavigationHelper.handleNavigationForNewAnzhi(this.getContext());
            }
            NavigationPreferences.getInstance(context).setNavigationFlagForNewInstall(false);
        }
    }

    @Override
    public void setHotWordView(List<Object> list) {
        super.setHotWordView(list);
        if (readerTaobaoIndex != -1 && readerAndTaobaoPageView != null) {
            readerAndTaobaoPageView.setHotWordView(list);
        }
    }

    /**
     * @param channelID 目前必须包含三个元素，0是搜索屏1是购物屏，2是资讯屏，后续有添加就加到3-n
     *                  桌面也要做相应处理
     *                  pageArray[0]：零屏是否展示（91桌面&点心）
     *                  pageArray[1]：购物屏是否展示（91桌面）
     *                  pageArray[2]：资讯屏是否展示 （91桌面）
     *                  pageArray[3]：唯品会屏是否展示 （点心）
     *                  pageArray[4]：搜狐新闻屏是否展示 （点心）
     */

    public void init(String channelID) {
        int pageCount = NavigationPreferences.getInstance(context).getOpenPageCount();
        boolean pageArray[] = NavigationPreferences.getInstance(context).getOpenPageDisplayArray();
        int pageShowSequence[] = NavigationPreferences.getInstance(context).getShowPageSequence();
        int mode = NavigationPreferences.getInstance(context).getOpenPageMode();
        init(channelID, pageCount, pageArray, pageShowSequence, mode);
    }

    public void init(String channelID, int pageCount, boolean pageArray[], int[] pageShowSequence, int type) {
        try {
            CHANNEL_ID = channelID;
            initEx(channelID, pageCount, pageArray, pageShowSequence, type);
            lastType = type;
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * @desc 增加全屏弹出广告布局
     * @author linliangbin
     * @time 2017/9/8 14:21
     */
    private void initPopupAdLayout() {
        popupAdLayout = new PopupAdLayout(getContext());
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.addRule(ALIGN_PARENT_TOP);
        popupAdLayout.setVisibility(GONE);
        addView(popupAdLayout, rlp);
    }

    public void initEx(String channelID, int pageCount, boolean pageArray[], int[] pageShowSequence, int type) {
        CHANNEL_ID = channelID;
        mPageArray = pageArray;
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        /**
         * 修改2.X 层次过多崩溃问题
         */
        if (myPhoneViewPager != null) myPhoneViewPager.removeAllViews();
        if (myPhoneViewPager == null) {
            myPhoneViewPager = new MyPhoneViewPager(this.getContext());
            myPhoneViewPager.setId(R.id.viewpage);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            rlp.addRule(ALIGN_PARENT_TOP);
            addView(myPhoneViewPager, rlp);

            myPhoneViewPager = (MyPhoneViewPager) this.findViewById(R.id.viewpage);
            myPhoneViewPager.setBackgroundColor(0x00000000);
            myPhoneViewPager.setOnPageSelectedListenner(new MyPhoneViewPager.OnPageSelectedListenner() {
                @Override
                public void onUpdate(int selectedPage, boolean isfinish) {
                    Log.i("llbeing", "onUpdate:" + selectedPage);
                    setPageIndex(NavigationView2.activity, selectedPage);
                    if (!isfinish) return;
                    if (!isForceJumpToPage) {
                        callCvAnalysis(selectedPage, false);
                    } else {
                        isForceJumpToPage = false;
                    }

                }
            });
            myPhoneViewPager.setNavigationView(this);
        }
        //初始化各个屏并添加
        initPage(pageArray, pageShowSequence, type);
        if (TaoPageIndex == -1 && taoBaoExtIndex == -1) {
            TaobaoData.getInstance().clearCallback();
        }
        isForceJumpToPage = true;
        myPhoneViewPager.setInitTab(PageCountSetter.getInstance().getPageCount() - 1);
        defaultSelect = PageCountSetter.getInstance().getPageCount() - 1;
        myPhoneViewPager.setToScreen(defaultSelect);


        if (tab != null) {
            this.removeView(tab);
            tab = null;
        }


        if (isNeedShowTab(pageArray, pageShowSequence)) {
            showTab();
        }


        if (!isInitAd) {
            AdvertSDKController.init(NavigationView2.activity, channelID);
            isInitAd = true;
        }

        initPopupAdLayout();
    }

    /**
     * 判断当前是否需要展示TAB
     * 当且仅当 展示购物-网易资讯-零屏时，需要展示
     *
     * @param pageArray
     * @param showSequence
     * @return
     */
    private boolean isNeedShowTab(boolean[] pageArray, int showSequence[]) {
        try {

            if (PageCountSetter.getInstance().getPageCount() == 3 &&
                    pageArray[NavigationPreferences.INDEX_PAGE_NAVIGATION]
                    && pageArray[NavigationPreferences.INDEX_PAGE_NEWS]
                    && pageArray[NavigationPreferences.INDEX_PAGE_TAOBAO]
                    && showSequence[0] == NavigationPreferences.INDEX_PAGE_TAOBAO
                    && showSequence[1] == NavigationPreferences.INDEX_PAGE_NEWS
                    && showSequence[2] == NavigationPreferences.INDEX_PAGE_NAVIGATION) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    private void showTab() {

        tab = new ZeroViewPagerTab(this.getContext());
        tab.setId(R.id.pagetab);
        RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(getContext(), 50));
        rlp2.addRule(ALIGN_PARENT_TOP);
        rlp2.setMargins(ScreenUtil.dip2px(getContext(), 50), ScreenUtil.dip2px(getContext(), 25), 0, 0);
        addView(tab, rlp2);
        tab = (ZeroViewPagerTab) this.findViewById(R.id.pagetab);
        tab.setVisibility(VISIBLE);
        setTabTitle(PageCountSetter.getInstance().getPageCount(), mPageArray);
        myPhoneViewPager.setTab(tab);
        tab.setViewpager(myPhoneViewPager);
        int marginTop = ScreenUtil.dip2px(context, 20);
        int h = tab.getRecommendHeight() + marginTop;
        RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
        tab.setTextTop(marginTop);
        tab.setLayoutParams(lparams);
        defaultSelect = PageCountSetter.getInstance().getPageCount();
        tab.scrollHighLight(defaultSelect * screenW);
    }

    private void resetPageIndex() {

        searchPageIndex = -1;
        newsPageIndex = -1;
        TaoPageIndex = -1;
        sohuIndex = -1;
        taoBaoExtIndex = -1;
        readerTaobaoIndex = -1;
    }

    /**
     * 初始化要展示的各个屏
     *
     * @param pageArray
     * @param pageShowSequence
     */
    private void initPage(boolean pageArray[], int pageShowSequence[], int type) {

        Log.i("llbeing", "initPage:pageArray:" + Arrays.toString(pageArray));
        Log.i("llbeing", "initPage:pageShowSequence:" + Arrays.toString(pageShowSequence));

        int index = 0;
        isShowingSohu = false;
        isShowingFenghuang = false;
        if (sohuPageView != null) {
            sohuPageView.removeFragment();
        }
        resetPageIndex();

        for (int i = 0; i < pageShowSequence.length; i++) {

            int currentPageIndex = pageShowSequence[i];
            //不展示
            if (currentPageIndex == -1)
                continue;
            //长度不匹配，不展示
            if (currentPageIndex >= pageArray.length)
                continue;
            //当前页面不显示
            if (!pageArray[currentPageIndex])
                continue;

            switch (currentPageIndex) {
                case NavigationPreferences.INDEX_PAGE_NAVIGATION:
                    if (addNaviPage(type)) {
                        searchPageIndex = index;
                        AdIDArray[index] = CvAnalysisConstant.NAVIGATION_SCREEN_INTO;
                        if (navigationView2 != null) {
                            navigationView2.CV_PAGE_ID = CvAnalysisConstant.NAVIGATION_SCREEN_INTO;
                        }
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_TAOBAO:
                    if (addTaobaoPage()) {
                        TaoPageIndex = index;
                        if (taobaoPageView != null) {
                            taobaoPageView.setPageIndex(TaoPageIndex);
                        }
                        AdIDArray[index] = CvAnalysisConstant.TAOBAO_SCREEN_PAGEID;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_NEWS:
                    if (addNewsPage()) {
                        newsPageIndex = index;
                        if (neteaseNewsPageView != null) {
                            neteaseNewsPageView.setPageIndex(newsPageIndex);
                        }
                        AdIDArray[index] = CvAnalysisConstant.OPEN_PAGE_NEWS_PAGE_ID;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_VIP:
                    if (addVipPage()) {
                        vipIndex = index;
                        AdIDArray[index] = CvAnalysisConstant.DIANXIN_VIP_PAGE_ID;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_SOHU:
                    if (addSohuPage()) {
                        sohuIndex = index;
                        if (CommonLauncherControl.DX_PKG) {
                            AdIDArray[index] = CvAnalysisConstant.DIANXIN_SOHU_PAGE_ID;
                        } else {
                            AdIDArray[index] = CvAnalysisConstant.SOHU_PAGE_ID;
                        }
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_FENGHUANG:
                    if (addFenghuangPage()) {
                        fenghuangIndex = index;
                        AdIDArray[index] = CvAnalysisConstant.DINGZHI_IFENG_PAGE_ID;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_IREADER:
                    if (addIreaderPage()) {
                        ireaderIndex = index;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_TAOBAO_EXT:
                    Log.i("llbeing", "addTaobaoExPage");
                    if (addTaoBaoExtPage()) {
                        taoBaoExtIndex = index;
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_DZSEARCH:
                    if (addIreaderAndTaobaoPage()) {
                        readerTaobaoIndex = index;
                        readerAndTaobaoPageView.setPageIndex(readerTaobaoIndex);
                        index++;
                    }
                    break;
                case NavigationPreferences.INDEX_PAGE_WEB:
                    if (addDZBrowserPage()) {
                        webIndex = index;
                        index++;
                    }
                    break;
            }
        }

        initSohuTheme();
        int pageIndex = index;
        if ((readerTaobaoIndex == 0 || readerTaobaoIndex == index - 1)
                && readerAndTaobaoPageView.getPageCount() > 1) {
            pageIndex = index + readerAndTaobaoPageView.getPageCount() - 1;
        }
        Log.i("llbeing", "initPage:" + index + "," + pageIndex);
        PageCountSetter.getInstance().setPageCount(NavigationView2.activity, index);
        PageCountSetter.getInstance().setAllPageCount(NavigationView2.activity, index);
        PageCountSetter.getInstance().setPageIndex(NavigationView2.activity, index - 1);


    }

    /**
     * 初始化零屏
     *
     * @return
     */
    private boolean addNaviPage(int type) {
        try {


            //当零屏类型改变或零屏变量为空时重新初始化
            if (lastType != type || navigationView2 == null) {
                int marginTop = ScreenUtil.getStatusBarHeight(getContext());
                if (CommonGlobal.isDianxinLauncher(getContext())) {
                    CommonLauncherControl.DX_PKG = true;
                    marginTop = 0;
                }
                if (CommonGlobal.isAndroidLauncher(getContext())) {
                    CommonLauncherControl.AZ_PKG = true;
                }
                if (!LauncherBranchController.isNavigationForCustomLauncher()
                        && TelephoneUtil.getApiLevel() >= 16
                        && !CardManager.getInstance().getIsDisableVideo(context)
                        && CommonGlobal.isDianxinLauncher(context)) {
                    navigationView2 = new CombineNavigationView(context, marginTop);
                } else {
                    if (type == 1) {
                        navigationView2 = new NavigationView2(context, marginTop, true);
                    } else {
                        navigationView2 = new NavigationView2(context, marginTop, false);
                    }
                }
            }
            if (navigationView2 != null) {
                navigationView2.setActivity(this.activity);
                navigationView2.setPageImp(this);
            }

            myPhoneViewPager.addView(navigationView2);
            if (Build.VERSION.SDK_INT >= 14) {
                navigationView2.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化淘宝购物屏
     *
     * @return
     */
    private boolean addTaobaoPage() {
        try {

            //第二屏淘宝屏
            if (taobaoPageView == null) {
                taobaoPageView = new TaobaoPageView(getContext());
            }
            myPhoneViewPager.addView(taobaoPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                taobaoPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化新闻屏
     *
     * @return
     */
    private boolean addNewsPage() {
        try {
            //第三屏的新闻屏
            if (neteaseNewsPageView == null) {
                neteaseNewsPageView = new NeteaseNewsPageView(getContext());
                neteaseNewsPageView.setTabLisener(this);
            }
            myPhoneViewPager.addView(neteaseNewsPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                neteaseNewsPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * @desc 更新零屏数据
     * 调用时机：每日任务，网络状态连接，USB插拔
     * @author linliangbin
     * @time 2017/9/8 13:11
     */
    @Override
    public void updateAndRefreshSiteDetail() {
        super.updateAndRefreshSiteDetail();
        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            NavigationAdHelper.loadNavigationAd(context);
        }
        AdControllerWrapper.getInstance().saveNetworkAd(context, AdvertSDKController.AD_POSITION_POPUP_AD);
    }

    @Override
    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {
        super.upgradePlugin(url, ver, isWifiAutoDownload);
        if (readerAndTaobaoPageView != null && readerTaobaoIndex != -1) {
            readerAndTaobaoPageView.upgradePlugin(url, ver, isWifiAutoDownload);
        }
    }

    /**
     * 初始化唯品会购物屏
     *
     * @return
     */
    private boolean addVipPage() {
        try {
            if (vipPageView == null) {
                vipPageView = new OpenScreenView(getContext());
            }
            myPhoneViewPager.addView(vipPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                vipPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化搜狐fragment
     * 桌面将零屏添加到activity后回调
     */
    public void initSohuFragment() {
        if (sohuPageView != null && isShowingSohu) {
            sohuPageView.initFragment();
        }
        if (fenghuangPageView != null && isShowingFenghuang) {
            fenghuangPageView.initFragment();
        }
    }

    /**
     * 初始化搜狐新闻屏
     *
     * @return
     */
    private boolean addSohuPage() {
        try {
            if (sohuPageView == null) {
                sohuPageView = new SohuPageView(context);
                sohuPageView.init();
            }
            myPhoneViewPager.addView(sohuPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                sohuPageView.setAlpha(1);
            }
            isShowingSohu = true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 初始化凤凰新闻SDK
     *
     * @return
     */
    private boolean addFenghuangPage() {
        try {
            if (fenghuangPageView == null) {
                fenghuangPageView = new FenghuangPageView(context);
                fenghuangPageView.init();
            }
            myPhoneViewPager.addView(fenghuangPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                fenghuangPageView.setAlpha(1);
            }
            isShowingFenghuang = true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化掌阅阅读屏
     *
     * @return
     */
    private boolean addIreaderPage() {
        try {
            if (readerPageView == null) {
                readerPageView = new ReaderPageView(context);
            }
            myPhoneViewPager.addView(readerPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                readerPageView.setAlpha(1);
            }
            isShowingIreader = true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始淘宝扩展页
     *
     * @return
     */
    private boolean addTaoBaoExtPage() {
        try {
            if (taoBaoExtPageView == null) {
                taoBaoExtPageView = new TaoBaoExtPageView(getContext());
            }
            myPhoneViewPager.addView(taoBaoExtPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                taoBaoExtPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化阅读购物屏
     *
     * @return
     */
    private boolean addIreaderAndTaobaoPage() {
        try {
            if (readerAndTaobaoPageView == null) {
                readerAndTaobaoPageView = new ReaderAndTaobaoPageView(context);
            }
            myPhoneViewPager.addView(readerAndTaobaoPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                readerAndTaobaoPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 初始化定制版web屏
     * @return
     */
    private boolean addDZBrowserPage() {
        try {
            if (webPageView == null) {
                webPageView = new WebPageView(context);
            }
            myPhoneViewPager.addView(webPageView);
            if (Build.VERSION.SDK_INT >= 14) {
                webPageView.setAlpha(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 更新搜狐背景主题
     */
    private void initSohuTheme() {

        if (sohuPageView != null) {
            if (isSohuAtRight()) {
                sohuPageView.setNeedTransparentBg(true);
            } else {
                sohuPageView.setNeedTransparentBg(false);
            }
        }
    }


    /**
     * 通过反射，通知桌面已经滚动在第一屏
     *
     * @param ctx 参数必须是桌面的Launcher
     */
    @Override
    public void setPageIndex(Context ctx, int index) {
        PageCountSetter.getInstance().setPageIndex(ctx, index);
    }

    /**
     * 通过反射，通知桌面当前在第几屏
     * 主要用于异常情况下初始化出问题的处理
     *
     * @param ctx 参数必须是桌面的Launcher
     */
    @Override
    public void setPageCount(Context ctx, int index) {
        PageCountSetter.getInstance().setAllPageCount(ctx, index);
    }

    @Override
    public void jumpToPage(int which) {
        if (myPhoneViewPager != null) {
            isForceJumpToPage = true;
            myPhoneViewPager.setToScreen(which);
            if (tab != null) {
                tab.scrollHighLight(which * screenW);
            }
        }
    }

    /**
     * 这个方法会被桌面反射调用
     */
    @Override
    public void scrollToPage(int which) {
        if (myPhoneViewPager != null) {
            myPhoneViewPager.snapToScreen(which);
            if (tab != null) {
                tab.scrollHighLight(which * screenW);
            }
        }
    }

    /**
     * 这个方法会被桌面反射调用，设置是否需要刷新广告
     **/
    public void setIsNeedRequest(boolean isNeedRequest) {
        if (neteaseNewsPageView != null) {
            neteaseNewsPageView.setIsNeedRequestAD(isNeedRequest);
        }
    }


    public void callCvAnalysis(int selectedPage, boolean isleave) {
        //零屏关闭时不再进行统计
        if (!mIsOpenPageShow) return;
        //最后离开零屏时，需要打结束的点
        if (isleave && lastSelectedPage >= 0 && lastSelectedPage < PageCountSetter.getInstance().getPageCount()) {
            int adId = AdIDArray[lastSelectedPage];
            if (lastSelectedPage == searchPageIndex && navigationView2 != null) {
                navigationView2.onLeavingNavigation();
            }
            if (lastSelectedPage == sohuIndex && sohuPageView != null) {
                sohuPageView.onLeaving();
                reportSohuSuccessLeave();
            }
            if (lastSelectedPage == fenghuangIndex && fenghuangPageView != null) {
                fenghuangPageView.onLeaving();
            }
            if (lastSelectedPage == TaoPageIndex && taobaoPageView != null) {
                taobaoPageView.onLeavePage();
            }
            if (lastSelectedPage == ireaderIndex && readerPageView != null) {
                readerPageView.onLeavePage();
            }
            if (lastSelectedPage == taoBaoExtIndex && taoBaoExtPageView != null) {
                taoBaoExtPageView.onLeavePage();
            }
            if (lastSelectedPage == readerTaobaoIndex && readerAndTaobaoPageView != null) {
                readerAndTaobaoPageView.onLeavePage();
            }
            if (lastSelectedPage == webIndex && webPageView != null) {
                webPageView.onLeavePage();
            }

            removeMsgForSohuGuide();
            if(adId != 0){
                CvAnalysis.submitPageEndEvent(NavigationView2.activity, adId);
            }
        }

        //同一个页轻微滑动时没有发生翻页，所以不需要再次调用
        if (selectedPage == lastSelectedPage) {
            return;
        }
        //退出打点
        if (selectedPage != lastSelectedPage && lastSelectedPage >= 0 && lastSelectedPage < PageCountSetter.getInstance().getPageCount()) {
            int adId = AdIDArray[lastSelectedPage];
            if (lastSelectedPage == searchPageIndex && navigationView2 != null) {
                navigationView2.onLeavingNavigation();
            }
            if (lastSelectedPage == sohuIndex && sohuPageView != null) {
                sohuPageView.onLeaving();
                reportSohuSuccessLeave();
            }
            if (lastSelectedPage == fenghuangIndex && fenghuangPageView != null) {
                fenghuangPageView.onLeaving();
            }
            if (lastSelectedPage == TaoPageIndex && taobaoPageView != null) {
                taobaoPageView.onLeavePage();
            }
            if (lastSelectedPage == ireaderIndex && readerPageView != null) {
                readerPageView.onLeavePage();
            }
            if (lastSelectedPage == taoBaoExtIndex && taoBaoExtPageView != null) {
                taoBaoExtPageView.onLeavePage();
            }
            if (lastSelectedPage == readerTaobaoIndex && readerAndTaobaoPageView != null) {
                readerAndTaobaoPageView.onLeavePage();
            }
            if (lastSelectedPage == webIndex && webPageView != null) {
                webPageView.onLeavePage();
            }
            removeMsgForSohuGuide();
            if(adId != 0){
                CvAnalysis.submitPageEndEvent(NavigationView2.activity, adId);
            }
        }

        //进入打点
        if (selectedPage >= 0 && selectedPage < PageCountSetter.getInstance().getPageCount()) {
            int currentadId = AdIDArray[selectedPage];
            if (selectedPage == searchPageIndex && navigationView2 != null) {
                navigationView2.onShowingNavigationView(isSohuAtRight());
                //展示提示界面
                showGuideForSohuPageIfNeeded();
                //请求广告数据
                if (navigationView2.isShowNewsBelowCard) {
                    navigationView2.onlyRequestAD();
                }
                if (navigationView2.isShowingPluginAd() && popupAdLayout != null) {
                    popupAdLayout.onLeavePage();
                }
            }
            if (selectedPage == sohuIndex && sohuPageView != null) {
                sohuPageView.onShowing();
                reportSohuSuccessEnter();
                CardManager.getInstance().setHasSnapToSohuNewsPage(context, true);
            }

            if (selectedPage == fenghuangIndex && fenghuangPageView != null) {
                fenghuangPageView.onShowing();
            }

            if (selectedPage == newsPageIndex && neteaseNewsPageView != null) {
                neteaseNewsPageView.callOnEnter();
            }

            if (selectedPage == TaoPageIndex && taobaoPageView != null) {
                taobaoPageView.onEnterPage();
            }

            if (selectedPage == ireaderIndex && readerPageView != null) {
                readerPageView.onEnterPage();
            }

            if (selectedPage == taoBaoExtIndex && taoBaoExtPageView != null) {
                taoBaoExtPageView.onEnterPage();
            }

            if (selectedPage == readerTaobaoIndex && readerAndTaobaoPageView != null) {
                readerAndTaobaoPageView.onEnterPage();
            }
            if (selectedPage == webIndex && webPageView != null) {
                webPageView.onEnterPage();
            }

            if(currentadId != 0){
                CvAnalysis.submitPageStartEvent(NavigationView2.activity, currentadId);
            }
        }


        lastSelectedPage = selectedPage;
    }

    /**
     * 在需要的情况下展示搜狐新闻提示
     */
    private void showGuideForSohuPageIfNeeded() {

        if (isNeedShowSohuShopTips()) {
            removeMsgForSohuGuide();
            handler.sendEmptyMessageDelayed(MSG_JUMP_TO_SOHU_GUIDE_ACTIVITY, 1000);
        }

    }

    /**
     * 离开时需要将MSG 移除
     */
    private void removeMsgForSohuGuide() {
        if (sohuPageView != null && sohuIndex != -1 && handler != null) {
            handler.removeMessages(MSG_JUMP_TO_SOHU_GUIDE_ACTIVITY);

        }
    }

    /**
     * 搜狐新闻屏是否在最右边
     *
     * @return
     */
    public boolean isSohuAtRight() {

        if (sohuIndex > searchPageIndex && sohuIndex > newsPageIndex && sohuIndex > TaoPageIndex) {
            return true;
        }

        return false;
    }

    /**
     * 是否需要展示搜狐新闻提示
     *
     * @return
     */
    private boolean isNeedShowSohuShopTips() {
        try {
            //已经播放动画完毕 则不再播放
            if (CardManager.getInstance().getHasFinishedPlaySohuGuideAnim(context))
                return false;
            if (sohuPageView == null || sohuIndex == -1) {
                return false;
            }

            if (!SohuPageView.isSohuInite()) {
                return false;
            }

            //搜狐新闻屏在最右边显示时，不需要显示
            if (isSohuAtRight())
                return false;

            //零屏在搜狐右边的时候显示
            if (searchPageIndex != sohuIndex + 1)
                return false;

            if (CardManager.getInstance().getHasSnapToSohuNewsPage(context))
                return false;
            else
                return true;


        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    private void reportSohuSuccessEnter() {
        try {
            if (SohuPageView.isSohuInite()) {
                PluginUtil.invokeSubmitEvent(activity, LauncherBranchController.getSohuAnaticsID(context), "hr");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private void reportSohuSuccessLeave() {
        try {
            if (SohuPageView.isSohuInite()) {
                PluginUtil.invokeSubmitEvent(activity, LauncherBranchController.getSohuAnaticsID(context), "hk");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public void setTabTitle(int count, boolean pageArray[]) {
        int hide = 0;
        String title[] = context.getResources().getStringArray(R.array.open_page_array);
        if (pageArray[0]) hide = 1;
        String t[] = new String[count];
        int index = 0;
        for (int i = 0; i < title.length; i++) {
            if (pageArray[i]) {
                t[index] = title[i];
                index++;
            }
        }
        tab.addTitle(t, hide);
    }


    @Override
    public void jumpToSohu() {
        if (myPhoneViewPager != null && sohuIndex != -1) {
            myPhoneViewPager.snapToScreen(sohuIndex);
        }
    }


    @Override
    public void handleNavigationWhenLauncherOnResume() {

        if (NavigationPreferences.getInstance(context).isOpenPageChange()) {
            init(CHANNEL_ID, NavigationPreferences.getInstance(context).getOpenPageCount(),
                    NavigationPreferences.getInstance(context).getOpenPageDisplayArray(),
                    NavigationPreferences.getInstance(context).getShowPageSequence(),
                    NavigationPreferences.getInstance(context).getOpenPageMode());
            initSohuFragment();
            jumpToPage(NavigationHelper.getNavigationOpenPageCount(context) - 1);
            NavigationPreferences.getInstance(context).setOpenPageChange(false);
        }
        if (navigationView2 != null) {
            navigationView2.handleResume();
        }
        if (isShowingIreader && readerPageView != null) {
            readerPageView.handleResume();
        }
        if (readerTaobaoIndex != -1 && readerAndTaobaoPageView != null) {
            readerAndTaobaoPageView.handleResume();
        }
        if (taoBaoExtIndex != -1 && taoBaoExtPageView != null){
            taoBaoExtPageView.onPageResume();
        }
        BookShelfLoader.mayNeedReloadRead = false;

    }

    @Override
    public void handleNavigationWhenLauncherOnPause() {
        if (navigationView2 != null) {
            navigationView2.onPause();
        }
    }


    public void onLauncherStart() {
        if (navigationView2 != null)
            navigationView2.onLauncherStart();
        if(readerAndTaobaoPageView != null && readerTaobaoIndex != -1){
            readerAndTaobaoPageView.onLauncherStart();
        }
        //更新新闻页
        if (neteaseNewsPageView != null && mPageArray[2]) {
            neteaseNewsPageView.forcedoRefreshRequest();
        }

    }


    public void setActivity(Activity activity) {
        this.activity = activity;
        NavigationView2.activity = activity;
        screenW = ScreenUtil.getScreenWidth(activity);
    }

    @Override
    public void doInBackground() {
        if (navigationView2 != null)
            navigationView2.doInBackground();
    }

    @Override
    public void complete() {
        if (navigationView2 != null)
            navigationView2.complete();
    }


    /**
     * Launcher destory 时调用
     * 解除广播
     */
    public void unregisterReceiver() {
        AdvertSDKController.unregisterReceiver(context);
        if (navigationView2 != null) {
            navigationView2.onDestroy();
        }
    }


    /**
     * @desc 定制版进入零屏时即进行曝光上报
     * @author linliangbin
     * @time 2017/4/11 19:47
     */
    private void onPostExposedAnaticsForCustom() {
        AdvertSDKManager.AdvertInfo advertInfo = NavigationAdHelper.getNowChangeAdvertInfo();
        if (advertInfo != null) {
            AdvertSDKManager.submitExposureEvent(getContext(), advertInfo);
        }
    }


    @Override
    public void onShowingNavigationView() {
        // navigationView2.onShowingNavigationView();
        //开始相应的CV统计
        //上次选中的是未知的，所以置1，防止多调了一次end事件
        mIsOpenPageShow = true;
        if (myPhoneViewPager != null) {
            myPhoneViewPager.snapToDestination();
        }
        //解决统计错误问题
        if (!isInShowing) {
            isInShowing = true;
            lastSelectedPage = -1;
            if (myPhoneViewPager != null && lastSelectedPage == -1) {
                lastSelectedPage = -1;
                int select = myPhoneViewPager.getCurrentScreen();
                callCvAnalysis(select, false);
            }
        }

        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            onPostExposedAnaticsForCustom();
        }


        int select = myPhoneViewPager.getCurrentScreen();
        if (!(select == searchPageIndex && navigationView2 != null && navigationView2.isShowingPluginAd())
                && popupAdLayout != null) {
            popupAdLayout.onEnterPage();
        }
    }


    @Override
    public void onLeavingNavigation() {
        setIsNeedRequest(true);
        if (isInShowing) {
            isInShowing = false;
            callCvAnalysis(lastSelectedPage, true);
        }
        mIsOpenPageShow = false;
        lastSelectedPage = -1;

        if (popupAdLayout != null) {
            popupAdLayout.onLeavePage();
        }

    }


    /**
     * @desc 特殊机型单独处理返回键进入哪个屏
     * @author linliangbin
     * @time 2017/3/17 10:57
     */
    private boolean handleBackKeyToNavigationForSpecialDevice() {
        /** 机型判断 */
        if (!ReflectInvoke.isBackKeyToSohu(NavigationView2.activity)) {
            return false;
        }

        if (sohuIndex != -1) {
            jumpToPage(sohuIndex);
            return true;
        }

        return false;
    }

    /**
     * @desc 获取给定屏幕的当前位置
     * @author linliangbin
     * @time 2017/11/6 14:51
     */
    private int getCurrentPageActualIndex(int pageIndex){
        int destPageIndex = -1;
        switch (pageIndex) {
            case NavigationPreferences.INDEX_PAGE_NAVIGATION:
                if (searchPageIndex != -1) {
                    destPageIndex = searchPageIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_TAOBAO:
                if (TaoPageIndex != -1) {
                    destPageIndex = TaoPageIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_NEWS:
                if (newsPageIndex != -1) {
                    destPageIndex = newsPageIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_VIP:
                if (vipIndex != -1) {
                    destPageIndex = vipIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_SOHU:
                if (sohuIndex != -1) {
                    destPageIndex = sohuIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_FENGHUANG:
                if (fenghuangIndex != -1) {
                    destPageIndex = fenghuangIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_IREADER:
                if (ireaderIndex != -1) {
                    destPageIndex = ireaderIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_TAOBAO_EXT:
                if (taoBaoExtIndex != -1) {
                    destPageIndex = taoBaoExtIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_DZSEARCH:
                if (readerTaobaoIndex != -1) {
                    destPageIndex = readerTaobaoIndex;
                }
                break;
            case NavigationPreferences.INDEX_PAGE_WEB:
                if (webIndex != -1) {
                    destPageIndex = webIndex;
                }
                break;
        }
        return destPageIndex;
    }

    @Override
    public void handleBackKeyToNavigation() {

        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            if (NavigationPreferences.getInstance(context).getOpenPageCount() >= 1) {
                if (LauncherBranchController.isChaoqian(context)
                        && (TelephoneUtil.getVersionName(context).equalsIgnoreCase("7.6.5")
                        || TelephoneUtil.getVersionName(context).equalsIgnoreCase("7.6.6"))
                        && NavigationPreferences.getInstance(context).isShowSearchPage()) {
                    jumpToPage(0);
                } else {
                    int destPageIndex = NavigationPreferences.getInstance(context).getOpenPageCount() - 1;
                    if (ConfigPreferences.getInstance(context).getNavigationModeBackKeyToPageIndex() != ConfigPreferences.NAVIGATION_PAGE_INDEX_UNKNOWN) {
                        destPageIndex = getCurrentPageActualIndex(ConfigPreferences.getInstance(context).getNavigationModeBackKeyToPageIndex());
                    }
                    if(destPageIndex == -1 || destPageIndex > NavigationPreferences.getInstance(context).getOpenPageCount() - 1){
                        destPageIndex = NavigationPreferences.getInstance(context).getOpenPageCount() - 1;
                    }
                    jumpToPage(destPageIndex);
                }
            }
        } else if (CommonGlobal.isAndroidLauncher(context)) {
            /** 安卓桌面返回键进入搜狐新闻屏 */
            if (sohuIndex != -1) {
                jumpToPage(sohuIndex);
            }
        } else if (CommonGlobal.isDianxinLauncher(context)) {
            /** 点心桌面返回键进入阅读屏 */
            if (ireaderIndex != -1) {
                jumpToPage(ireaderIndex);
            }
        } else {
            if (NavigationPreferences.getInstance(context).getOpenPageCount() > 1) {
                /**
                 * V7598 新用户零屏在最左
                 * 升级用户零屏在最右
                 *
                 * 其他版本都在最右
                 * 返回键一定进入零屏
                 */
                if (!handleBackKeyToNavigationForSpecialDevice()) {
                    if (NavigationPreferences.getInstance(context).isNavigationAtLeft(context)) {
                        jumpToPage(0);
                    } else {
                        jumpToPage(NavigationPreferences.getInstance(context).getOpenPageCount() - 1);
                    }
                }
            }
        }

    }


    @Override
    public void setBackgroundAlpha(int alpha, int index) {
        if (tab != null) {
            tab.setBackgroundAlpha(alpha, index);
        }
    }

    @Override
    public void invalidateTab() {
        if (tab != null) {
            tab.invalidate();
        }
    }
}
