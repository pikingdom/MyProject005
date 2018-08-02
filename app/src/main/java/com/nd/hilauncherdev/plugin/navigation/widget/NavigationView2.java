package com.nd.hilauncherdev.plugin.navigation.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardUpdateManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.SettingsPreference;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.bean.UrlBean;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviSiteLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviWordLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.NewsLoader;
import com.nd.hilauncherdev.plugin.navigation.share.SharedPopWindow;
import com.nd.hilauncherdev.plugin.navigation.util.ActivityResultHelper;
import com.nd.hilauncherdev.plugin.navigation.util.ActualTimeAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.ScrollViewWithAnalytics.RefreshCallBack;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;
import com.nd.hilauncherdev.plugin.navigation.widget.search.SearchLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.CardNoticeLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.NewsListGoTopControler;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.RefreshAsyncTask;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.RefreshLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.SohuNoticeLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.ThemeOperator;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.TransparentUpgradeLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.upgrade.UpgradeLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * <br>
 * Description: 第0屏布局View <br>
 * Author:chenzhihong_9101910 <br>
 * Date:2015-03-25
 */
public class NavigationView2 extends BaseNavigationSearchView implements RefreshCallBack, View.OnClickListener,
        UpgradeLayout.CancelCallback, SohuNoticeLayout.SohuNoticeListener,
        ThemeOperator.ThemeOperateListener {


    public static final long MINUTE = 1000 * 60L;

    // 是否需要刷新卡片标题栏处的广告推荐词，只有在零屏启动时&下拉刷新时 刷新
    public static boolean needRefreshCardTitleRecommendWord = true;


    public View naviViewMain;
    public NaviSiteCardBaseView naviSiteCardBaseView;
    public NaviWordCardBaseView naviWordCardBaseView;
    public View naviSiteCardView;
    public View naviWordCardView;
    public LinearLayout adBannerViewLayout;
    public SharedPopWindow sharePop;

    public CardViewHelper cardViewHelper;
    public ListViewWithNews mListView;
    public NewsLoader newsLoader;
    public AsyncImageLoader asyncImageLoader;
    public ArrayList<HotwordItemInfo> hotWordList = new ArrayList<>();
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            cardViewHelper.processMsg(msg);
        }
    };
    public NewsListViewAdapterWithoutBanner mAdapter;
    //ListView回到顶端的按钮
    View mGotoTopBt = null;
    /**
     * 主题相关工具类
     */
    ThemeOperator themeOperator;
    private NavigationFavoriteSiteView mFavoriteSiteView;
    private LinearLayout blankBottomView;
    private LinearLayout cardAllV;
    private SearchLayout searchLayout;
    private ScrollViewWithAnalytics mNavigationScrollLayout;
    private LayoutInflater inflater;
    /**
     * 卡片更新布局界面
     */
    private TransparentUpgradeLayout upgradeLayout;

    /**
     * 卡片刷新和添加引导布局界面
     */
    private RefreshLayout refreshLayout;

    /**
     * 搜狐新闻引导布局界面
     */
    private SohuNoticeLayout sohuNoticeLayout;
    /**
     * 卡片提示消息布局界面
     */
    private CardNoticeLayout cardNoticeLayout;

    /**
     * 新闻界面置顶按钮控制器
     */
    private NewsListGoTopControler goTopControler;
    private Animation enterAnimation;
    @SuppressWarnings("unused")
    private Animation expandAnim;
    private CardViewFactory cardViewFactory;


    private boolean isLoaded = false;
    private ArrayList<Card> cardS = new ArrayList<Card>();
    private int downloadState = -1;
    private AdViewHelper adBannerViewHelper;

    private int refreshSiteCounts = 0;


    public NavigationView2(Context context, int top, boolean isShowNewsBelowCard) {
        super(context);
        this.isShowNewsBelowCard = isShowNewsBelowCard;
        cardViewFactory = CardViewFactory.getInstance();
        cardViewFactory.navigationView = this;
        newsLoader = new NewsLoader();

        NavigationLoader.createBaseDir();
        SettingsPreference.initFontStyle(context);
        asyncImageLoader = new AsyncImageLoader();
        initView(top);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPopWindow.loadSharedImg(getContext());
            }
        }, 1500);
        themeOperator = new ThemeOperator(getContext());
        themeOperator.setListener(this);
        themeOperator.registerThemeReceiver();
    }


    protected void initView(int top) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, top, 0, 0);
        addView(View.inflate(context, R.layout.launcher_navigation, null), layoutParams);
//		 ((DropLayout) findViewById(R.id.dropView)).setRefreshCallBack(this);
        //新闻列表
        if (isShowNewsBelowCard) {
            ScrollViewWithAnalytics scrollViewWithAnalytics = (ScrollViewWithAnalytics) findViewById(R.id.navigationLayout);
            /**
             * 解决monkey 测试中出现的问题
             * java.lang.IllegalArgumentException: parameter must be a descendant of this view
             */
            clearChildFocus(scrollViewWithAnalytics);
            scrollViewWithAnalytics.clearFocus();
            scrollViewWithAnalytics.removeAllViews();
            this.removeView(scrollViewWithAnalytics);

            mListView = (ListViewWithNews) findViewById(R.id.navigationLayout_news);
            mListView.setId(R.id.navigationLayout);
            mListView.setPullEnable(true);
            mListView.disableBannerHeader();
            mAdapter = new NewsListViewAdapterWithoutBanner(context, mListView, "");
            mAdapter.refreshRequest();
            mAdapter.removeLoadingView();
            /**
             * listview 不包含EditText
             * 不需要获取焦点
             */
//			mListView.requestFocus();
            mGotoTopBt = findViewById(R.id.news_gototop);
            mGotoTopBt.setOnClickListener(this);
            setScrollListen(mAdapter, mGotoTopBt, context);

            mListView.setListener(new PullToRefreshListView.P2RListViewStateListener() {
                @Override
                public void onStateChanged(int state) {
                    switch (state) {
                        case PullToRefreshListView.P2RListViewStateListener.STATE_PULL: {

                        }
                        break;

                        case PullToRefreshListView.P2RListViewStateListener.STATE_LOADING: {
                            new RefreshAsyncTask(context, NavigationView2.this).execute();
                            //10s 超时则停止动画
                            if (mListView != null)
                                mListView.sendCancaleMsgDelay();
                        }
                        break;

                        case PullToRefreshListView.P2RListViewStateListener.STATE_RECOVER: {

                        }
                        break;

                        default:
                            break;
                    }
                }
            });
        } else {
            ListViewWithNews view = (ListViewWithNews) findViewById(R.id.navigationLayout_news);
//			listview调用removeAllViews 存在异常 取消调用
//			view.removeAllViews();
            /**
             * 解决monkey 测试中出现的问题
             * java.lang.IllegalArgumentException: parameter must be a descendant of this view
             */
            clearChildFocus(view);
            view.clearFocus();
            RelativeLayout listLayout = (RelativeLayout) findViewById(R.id.listview_layout);
            listLayout.removeAllViews();
            this.removeView(listLayout);

            mNavigationScrollLayout = (ScrollViewWithAnalytics) findViewById(R.id.navigationLayout);
            mNavigationScrollLayout.setVerticalScrollBarEnabled(false);
            mNavigationScrollLayout.setRefreshCallBack(this);
        }

        searchLayout = (SearchLayout) findViewById(R.id.view_search_layout);

        naviViewMain = findViewById(R.id.navi_view_main);
        naviViewMain.bringToFront();


        // --------------------------- 网址导航 -----------------------------//
        blankBottomView = (LinearLayout) findViewById(R.id.navigationBlankView);
        // 推荐站点
        mFavoriteSiteView = (NavigationFavoriteSiteView) findViewById(R.id.navigationFavoriteWebView);
        mFavoriteSiteView.init(handler);
        NavigationLoader.FAVORITE_ICON_COUNT = 8;
        mFavoriteSiteView.setupOnItemClickListener();
        if (!Global.isZh(context)) {
            mFavoriteSiteView.setVisibility(View.GONE);
        }


        cardAllV = (LinearLayout) findViewById(R.id.navigation_card_all);

        mFavoriteSiteView.loadSites();
        post(new Runnable() {
            @Override
            public void run() {
                isLoaded = true;
            }
        });
        initUpdateV();
        initRefreshLayout();
        initSohuNoticeLayout();
        initCardNoticeLayout();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cardViewHelper = new CardViewHelper(context, handler, this, inflater);
        initAnimation();
        initCardV();
        adBannerViewLayout = (LinearLayout) this.findViewById(R.id.navigation_ad_layout);
        if (adBannerViewHelper == null)
            adBannerViewHelper = new AdViewHelper();
        adBannerViewHelper.initCardView(context, adBannerViewLayout);
        if (!isShowNewsBelowCard && mNavigationScrollLayout != null) {
            mNavigationScrollLayout.setListener(new ScrollViewWithAnalytics.IOnScrollListener() {
                @Override
                public void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt) {
                    int adBannerHeight = mNavigationScrollLayout.getChildAt(0).getMeasuredHeight() -
                            adBannerViewLayout.getHeight() - ScreenUtil.dip2px(context, 20);
                    if (adBannerViewLayout.getVisibility() == VISIBLE && (
                            adBannerHeight <= mNavigationScrollLayout.getScrollY() + mNavigationScrollLayout.getHeight())) {
                        if (needReportingAdShow)
                            ActualTimeAnalysis.sendActualTimeAnalysis(context, ActualTimeAnalysis.TAOBAO_VALID_HITS, "0tls");
                        needReportingAdShow = false;
                        isShowingAd = true;
                    } else {
                        isShowingAd = false;
                    }
                }
            });
        }

    }

    public void setScrollListen(NewsListViewAdapter adapter, final View gotoTop, final Context context) {
        goTopControler = new NewsListGoTopControler(adapter, gotoTop, context);
        goTopControler.setAdViewChecker(this);
    }

    private void initUpdateV() {
        upgradeLayout = (TransparentUpgradeLayout) findViewById(R.id.view_upgrade_layout);
        upgradeLayout.setCancelCallback(this);
    }


    private void initRefreshLayout() {
        refreshLayout = (RefreshLayout) findViewById(R.id.view_refresh_layout);
        refreshLayout.setNavigationView2(this);
    }

    private void initSohuNoticeLayout() {
        sohuNoticeLayout = (SohuNoticeLayout) findViewById(R.id.view_sohu_notice_layout);
        sohuNoticeLayout.setListener(this);
    }

    private void initCardNoticeLayout() {
        cardNoticeLayout = (CardNoticeLayout) findViewById(R.id.view_card_notice_layout);

    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {

                case R.id.btnVoice:
                    startVoiceRecognition();
                    /** maolinnan_350804于14.1.14日添加的统计信息 */
                    PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.SEARCH_VOICE_SEARCH, "1");
                    break;
                case R.id.news_gototop:
                    mListView.setSelection(0);
                    break;

            }
        }
    }


    private void initAnimation() {
        enterAnimation = AnimationUtils.loadAnimation(context, R.anim.card_enter);
        expandAnim = AnimationUtils.loadAnimation(context, R.anim.expand);
    }

    @Override
    public void refreshPaintAndView() {
        SettingsPreference.initFontStyle(context);
        setTypeface(this);
        cardViewHelper.setTypeface();
    }

    @Override
    public void refreshPaintAndViewNoRefreshCard() {
        SettingsPreference.initFontStyle(context);
        setTypeface(this);
    }

    private void setTypeface(ViewGroup parentV) {
        for (int i = 0; i < parentV.getChildCount(); i++) {
            View v = parentV.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                PaintUtils2.assemblyTypeface(tv.getPaint());
                tv.invalidate();
            } else if (v instanceof ViewGroup) {
                setTypeface((ViewGroup) v);
            }
        }

        if (mAdapter != null) {
            mAdapter.updateFooterFont();
        }

    }

    @Override
    public void doInBackground() {
        RefreshAsyncTask.refreshTask(context, this);
    }

    @Override
    public void initCardV() {
        // naviVideoView.hideVideoView();
        /** 疑似旧版本升级时增加小说和图片卡片代码 移除*/
//        CardManager.getInstance().tryAddDefaultCard(context);
        CardUpdateManager.doUpdate(context);
        CardManager.getInstance().tryAddNewToDefaultCard(context);
        if (cardAllV.getChildCount() == 0 || CardManager.getInstance().getIsCardListChanged(context)) {
            HashSet<Integer> addedCardIdS = CardManager.getInstance().getAddedCardIdS(context);
            cardAllV.removeAllViews();
            if (isShowNewsBelowCard && mListView != null && mListView.getVisibility() == VISIBLE)
                mListView.setSelection(0);
            cardS = CardManager.getInstance().getCurrentCardS(context);
            cardS = CardManager.getDownCardS(cardS);
            View changedView = null;
            HashSet<Integer> implementCardIdM = CardManager.getImplementCardIdM();
            int cardViewSize = cardS.size();
            int currentCardCount = 0;
            for (Card card : cardS) {
                changedView = cardViewHelper.getCardView(card, false, false);
                if (changedView == null) {
                    continue;
                }

                if (!implementCardIdM.contains(new Integer(card.id))) {
                    continue;
                }

                if (currentCardCount == cardViewSize - 1) {
                    //最后一张卡片，padding距离需要调整
                    changedView.setPadding(changedView.getPaddingLeft(), changedView.getPaddingTop(), changedView.getPaddingRight(), ScreenUtil.dip2px(context, 5));
                }
                cardAllV.addView(changedView);
                dealCardAdded(changedView, card, addedCardIdS);
                currentCardCount++;
            }

            cardViewHelper.showCardRecommendWord();
            dealScroll(addedCardIdS);
            CardManager.getInstance().setAddedCardS(context, "");
            CardManager.getInstance().setIsCardListChanged(context, false);
            cardViewHelper.notifyNewCard(cardAllV);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshPaintAndViewNoRefreshCard();
            }
        }, 20);
    }

    private void dealScroll(final HashSet<Integer> addedCardIdS) {
        if (addedCardIdS.size() > 0) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    int scrollHeight = 0;
                    int childCardHeight = 0;
                    for (int i = 0; i < cardAllV.getChildCount(); i++) {
                        scrollHeight += cardAllV.getChildAt(i).getMeasuredHeight();
                    }
                    //减掉推荐添加卡片高度
                    ArrayList<Card> cardL = CardManager.getInstance().getNewCardL(context);
                    if (cardL != null && cardL.size() > 0) {
                        if (cardL.size() == 3 && cardAllV.getChildCount() >= 4) {
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 1).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 2).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 3).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 4).getMeasuredHeight();
                        }
                        if (cardL.size() == 2 && cardAllV.getChildCount() >= 3) {
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 1).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 2).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 3).getMeasuredHeight();
                        } else if (cardL.size() == 1 && cardAllV.getChildCount() >= 2) {
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 1).getMeasuredHeight();
                            scrollHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 2).getMeasuredHeight();
                        }
                        childCardHeight = scrollHeight;
                    } else {
                        childCardHeight = scrollHeight;
                        //扣除最后一张卡片的高度
                        childCardHeight -= cardAllV.getChildAt(cardAllV.getChildCount() - 1).getMeasuredHeight();
                    }
                    //扣除下拉刷新操作栏的高度
                    childCardHeight += ScreenUtil.dip2px(context, 40);
                    //扣除推荐网址的高度
                    if (mFavoriteSiteView != null && mFavoriteSiteView.getVisibility() == VISIBLE)
                        childCardHeight += mFavoriteSiteView.getMeasuredHeight();

                    if (!isShowNewsBelowCard && mNavigationScrollLayout != null)
                        mNavigationScrollLayout.smoothScrollTo(0, scrollHeight);
                    else if (mListView != null) {
                        mListView.smoothScrollBy(childCardHeight, 1000);
                    }
                }
            }, 200);
        }
    }

    private void dealCardAdded(View changedView, final Card card, HashSet<Integer> addedCardIdS) {
        View cardNewFlagV = changedView.findViewById(R.id.navi_card_new);
        if (addedCardIdS.contains(card.id) && changedView != null) {
            changedView.startAnimation(enterAnimation);
            if (cardNewFlagV != null) {
                cardNewFlagV.setVisibility(View.VISIBLE);
                final View cardNewFlagVFinal = cardNewFlagV;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardNewFlagVFinal.setVisibility(View.GONE);
                    }
                }, 20000);
            }

            if (card.type == CardManager.CARD_STAR_TYPE) {
                ThreadUtil.executeMore(new Runnable() {
                    @Override
                    public void run() {
                        CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataFromServer(context, card, true);
                    }
                });
            }
        } else {
            if (cardNewFlagV != null) {
                cardNewFlagV.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 0屏是否已经加载完成
     *
     * @return
     */
    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void onShow() {
        initCardV();
        if (refreshLayout != null) {
            refreshLayout.updateTime();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            themeOperator.unregisterThemeReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleResume() {

        if (BookShelfLoader.mayNeedReloadRead) {
            if (CardManager.getInstance().isInCurrentCard(getContext(), CardManager.CARD_BOOKSHELF_TYPE)) {
                ThreadUtil.executeMore(new Runnable() {
                    @Override
                    public void run() {
                        CardDataLoaderFactory.getInstance().getCardLoader(CardManager.CARD_BOOKSHELF_TYPE).
                                loadDataS(getContext(), BookShelfLoader.generateCard(), true, 3);
                    }
                });

            }
        }

    }

    @Override
    public void showVoiceRecognitionResult(List<String> results) {
        if (searchLayout != null) {
            searchLayout.showVoiceRecognitionResult(results);
        }
    }

    @Override
    public void startVoiceRecognition() {
        VoiceRecognitionWindow.startVoiceRecognition(context, activity, ActivityResultHelper.REQUEST_VOICE_RECOGNITION_NAVI);
    }

    @Override
    public void onBackKeyDown() {

        if (searchLayout != null) {
            searchLayout.onBackKeyDown();
        }

        if (sharePop != null && sharePop.isShowing()) {
            sharePop.dismiss();
        }
    }

    @Override
    public void refreshFavoriteSiteView() {
        mFavoriteSiteView.refreshSites();
    }

    @Override
    public void showWebSites() {
        findViewById(R.id.navigationView).setVisibility(View.VISIBLE);
        blankBottomView.setVisibility(View.VISIBLE);

        findViewById(R.id.navigationLayout).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWebSites() {
        findViewById(R.id.navigationView).setVisibility(View.GONE);
        blankBottomView.setVisibility(View.VISIBLE);
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public NavigationFavoriteSiteView getFavoriteSiteView() {
        return mFavoriteSiteView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN && !isBtnQRCodeOrVoiceTouched(ev)) {
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 是否二维码或语音按钮被touch
     */
    private boolean isBtnQRCodeOrVoiceTouched(MotionEvent ev) {
        if (searchLayout != null) {
            return searchLayout.isBtnQRCodeOrVoiceTouched(ev);
        } else {
            return false;
        }
    }

    @Override
    public void showNavigationLayout() {
        findViewById(R.id.navigationLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.search_view).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNavigationLayout() {
        findViewById(R.id.navigationLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.search_view).setVisibility(View.INVISIBLE);
    }

    @Override
    public NavigationSiteView getSiteView() {
        return null;
    }

    /**
     * 更新搜狐新闻提示卡片
     */
    public void updateNotcieForSohuNews() {

        if (!CardManager.getInstance().getHasSnapToSohuNewsPage(context) && CardManager.getInstance().getHasFinishedPlaySohuGuideAnim(context)) {
            showNoticeForSohuNews();
        } else {
            hideNoticeForSohuNews();
        }

    }

    /**
     * 显示搜狐资讯屏提示卡
     */
    public void showNoticeForSohuNews() {
        if (sohuNoticeLayout != null) {
            PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_ANIM_GUIDE, "kz");
            sohuNoticeLayout.setVisibility(VISIBLE);
        }

    }

    /**
     * 隐藏搜狐资讯屏提示卡片
     */
    public void hideNoticeForSohuNews() {
        if (sohuNoticeLayout != null) {
            sohuNoticeLayout.setVisibility(GONE);
        }
    }


    @Override
    public void updateAndRefreshSiteDetail() {
        // 每日任务时，重置标题栏推荐词显示位置 标题栏推荐词从头开始显示
        CardManager.getInstance().setRecommendWordIndex(context, CardManager.DEFAULT_WORD_INDEX);
        if (NavigationLoader.getInstance().isRequestCounterIn()) {
            ThreadUtil.executeMore(new Runnable() {
                @Override
                public void run() {
                    updateFullScreenAdData();
                    cardViewHelper.loadCardRecommendWordFromServer();
                    if (NavigationLoader.getInstance().updateIconAndSiteData(context)) {
                        final ArrayList<UrlBean> urlList = NaviSiteLoader.getSiteCardUrls(context);

                        Global.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (getFavoriteSiteView() != null) {
                                    getFavoriteSiteView().loadSites();
                                }
                                naviSiteCardBaseView.urlList = urlList;
                                naviSiteCardBaseView.showItemS();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void setHotWordView(List<Object> list) {

        ArrayList<HotwordItemInfo> itemList = NaviWordLoader.convertHotwordList(list);

        if (itemList.size() == 0) {
            return;
        }

        if (hotWordList == null || hotWordList.size() == 0 || !NaviWordLoader.isAllBuildInWord(itemList)) {
            hotWordList = itemList;
            if (naviWordCardBaseView != null) {
                naviWordCardBaseView.showItemS();
            }
        }
    }

    @Override
    public void refreshAdView() {
        adBannerViewHelper.initCardView(context, adBannerViewLayout);
    }

    @Override
    public void onLauncherStart() {
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    ThreadUtil.executeMore(new Runnable() {
                        @Override
                        public void run() {

                            NaviCardLoader.loadCardInfo(context);
                            NavigationView2.super.doDailyUpdate();
                            cardViewHelper.onLauncherStart();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (cardNoticeLayout != null) {
                                        if (cardNoticeLayout.update()) {
                                            CardManager.getInstance().setIsCardListChanged(context, true);
                                            initCardV();
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }, MINUTE * 2);
            submitAnalyseEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitAnalyseEvent() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
                for (Card card : cardS) {
                    if (card.id == CardManager.CARD_ID_HOT_WORD) {
                        PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SERVICE_CONDITION, "ssrd");
                    } else if (card.id == CardManager.CARD_ID_SITE) {
                        PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SERVICE_CONDITION, "wzdh");
                    } else {
                        String cardName = CardViewFactory.getInstance().getCardViewHelper(card).getCardDisName(card);
                        if (!TextUtils.isEmpty(cardName)) {
                            PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SERVICE_CONDITION, cardName);
                        }
                    }
                }
            }
        }, 5000);
    }

    @Override
    public void onNetworkAvaiable() {

    }


    @Override
    public void setThemeChoose(int position) {

        themeOperator.updateThemeChoose(position);
        pluginAdHandler.post(new Runnable() {
            @Override
            public void run() {
                initCardV();
            }
        });
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        mFavoriteSiteView.activity = activity;
    }


    @Override
    public void complete() {
        CardManager.getInstance().setRefreshTime(context, System.currentTimeMillis());
        if (naviWordCardBaseView != null)
            naviWordCardBaseView.showItemS();
        if (mListView != null) {
            ((PullToRefreshListView) mListView).onLoadingComplete();
        }

        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.updateTime();
        }
        //刷新完成时停止动画
        if (isShowNewsBelowCard && mListView != null) {
            mListView.stopRefreshingByView();
        }
        if (cardNoticeLayout != null) {
            if (cardNoticeLayout.update()) {
                CardManager.getInstance().setIsCardListChanged(context, true);
                initCardV();
            }
        }
    }

    @Override
    public void onlyRequestAD() {
        if (mAdapter != null) {
            mAdapter.firstRequest = false;
            mAdapter.onlyRequestAD();
        }
    }

    /**
     * @desc
     * @author linliangbin
     * @time 2017/8/2 17:19
     */
    private void updateRecommenSites() {
        if (LauncherBranchController.isNavigationForCustomLauncher()
                && LauncherBranchController.isChaoqian(context)
                && refreshSiteCounts < 3) {
            if (getFavoriteSiteView() != null) {
                getFavoriteSiteView().updateSiteIcon();
                refreshSiteCounts++;
            }
        }
    }

    /**
     * 桌面滑动或是返回键进入显示零屏回调
     */
    public void onShowingNavigationView(boolean isSohuAtRight) {
        super.onShowingNavigationView(isSohuAtRight);
        if (!isSohuAtRight) {
            updateNotcieForSohuNews();
        } else {
            hideNoticeForSohuNews();
        }

        updateRecommenSites();
        if (refreshLayout != null) {
            refreshLayout.updateTime();
        }

        //每次滑到零屏的时候进行曝光统计
        if (mFavoriteSiteView != null && mFavoriteSiteView.getVisibility() == VISIBLE) {
            mFavoriteSiteView.report618Anatics();
        }

    }

    @Override
    public void onLeavingNavigation() {
        super.onLeavingNavigation();
        needReportingAdShow = false;

        //离开零屏时停止刷新显示界面
        if (!isShowNewsBelowCard && mNavigationScrollLayout != null && mNavigationScrollLayout.isPlayAnim()) {
            mNavigationScrollLayout.stopRefreshingByView();
        }
        //离开零屏时停止动画
        if (isShowNewsBelowCard && mListView != null && mListView.getVisibility() == VISIBLE) {
            mListView.stopRefreshingByView();
        }
    }

    @Override
    public void upgradePlugin(String url, int ver, boolean isWifiAutoDownload) {
        super.upgradePlugin(url, ver, isWifiAutoDownload);
        if (upgradeLayout != null) {
            upgradeLayout.setVisibility(VISIBLE);
            upgradeLayout.upgradePlugin(url, ver, isWifiAutoDownload);
        }
    }

    @Override
    public void onCancleUpgrade() {
        upgradeLayout.setVisibility(GONE);
    }


    @Override
    public void onSohuNoticeEnd() {
        pageImp.jumpToSohu();
        hideNoticeForSohuNews();
        CardManager.getInstance().setHasSnapToSohuNewsPage(getContext(), true);
    }


    @Override
    public void onLauncherThemeApplied() {
        if (mFavoriteSiteView != null) {
            mFavoriteSiteView.loadSites();
        }
    }
}
