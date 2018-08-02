package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaobaoBannerCard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 淘宝购物屏header卡片管理器<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/14<br/>
 */
public class TaobaoCardWrapper extends LinearLayout {

    private static final String TAG = "TaobaoCardWrapper";

    //一共五个卡片：
    // 1、淘一下搜索
    // 2、分类
    // 3、头条
    // 4、cps
    // 5、限时抢购
    // 6、banner视图
    // 7、单张广告视图
    private TaobaoBannerCard cardBanner;
    //private TaobaoCard cardSearch;
    private TaobaoCateCard cardCate;
    private TaobaoCard cardHeadLine;
    private TaobaoCPSCard cardCps;
    private TaobaoSplashSaleCard cardSplashSale;
    private TaobaoBannerCard cardSingleBanner;
    private View endCard;

    /**
     * 淘宝卡片顺序序列
     */
    private SparseArray<TaobaoCard> series = new SparseArray<TaobaoCard>();

    private LinearLayout.LayoutParams splitViewParams;

    private SharedPreferencesUtil spUtil;


    private final Map<CardType, TaobaoCard> cardMapping = new HashMap<CardType, TaobaoCard>();

    public TaobaoCardWrapper(Context context) {
        super(context);
        init();
    }

    public TaobaoCardWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        AsyncImageLoader.initImageLoaderConfig(getContext());
        spUtil = new SharedPreferencesUtil(getContext());
        initView();
        CardType.forceSeriesChanged();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        initCards();
        AbsListView.LayoutParams parentLP = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(parentLP);
        this.setOrientation(LinearLayout.VERTICAL);

        splitViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(getContext(), 8));
        //refreshView();
        assemble();
    }

    /**
     * 初始化卡片
     */
    private void initCards() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        //banner循环广告
        cardBanner = new TaobaoBannerCard(getContext(),this,CardType.MULTIBANNER);
        //淘一下
        //cardSearch = new TaobaoSearchCard(getContext(), this);
        //分类
        cardCate = new TaobaoCateCard(getContext(), this);
        //头条
        cardHeadLine = new TaobaoHeadLineCard(getContext(), this);
        //CPS
        cardCps = new TaobaoCPSCard(getContext(), this);
        //限时抢购
        cardSplashSale = new TaobaoSplashSaleCard(getContext(), this);
        //单张广告
        cardSingleBanner = new TaobaoBannerCard(getContext(),this,CardType.SINGLEBANNER);

        endCard = inflater.inflate(R.layout.taobao_viewtype_tipline, this, false);

        cardMapping.clear();
        cardMapping.put(cardBanner.getType(), cardBanner);
        cardMapping.put(cardCate.getType(), cardCate);
        cardMapping.put(cardHeadLine.getType(), cardHeadLine);
        cardMapping.put(cardCps.getType(), cardCps);
        cardMapping.put(cardSplashSale.getType(), cardSplashSale);
        cardMapping.put(cardSingleBanner.getType(), cardSingleBanner);
    }

    /**
     * <h5>组装卡片视图</h5>
     * 根据一定的顺序、显隐需求来进行卡片摆放顺序和是否显示
     */
    public void assemble() {
        //1、提取动态配置的卡片顺序和显隐值
        //initData();
        //2、清空当前内容
        boolean isChange = true;//CardType.isSeriesChanged(getContext());
        if (isChange) {
            int childrenCount = getChildCount();
            if (childrenCount > 0) removeAllViews();
            CardType.restoreSeriesChanged();
            CardType.getSeries(getContext());
        } else {
            CardType.getSeries(getContext());
            return;
        }

        //3、填充内容
        List<Integer> series = CardType.getSeries(getContext());
        Log.d(TAG, "current series : " + series);
        for (Integer sery : series) {
            CardType type = CardType.getCardTypeByFlag(sery);
            //搜索卡片不在此处展示
            if (type == CardType.SEARCH ) {
                continue;
            }
            TaobaoCard card = cardMapping.get(type);
            if (card != null && type != null && type.isVisible()) {
                Log.d(TAG, "" + card.getType());
                View cardView = card.getView();
                if (cardView != null&&isChange) {
                    addView(cardView);
                }
            } else {
                continue;
            }
            if (card.getSplitView() != null&&isChange){
                addView(card.getSplitView(),splitViewParams);
            }
            if (card != null&&type != null && type.isVisible()&&(card.getView()!=null&&card.getView().getVisibility() == View.VISIBLE))
                card.getSplitView().setVisibility(VISIBLE);
            else if(card != null&&type != null && !type.isVisible())
                card.getSplitView().setVisibility(GONE);
        }
        addView(endCard);
        requestLayout();
    }


    /**
     * 更新视图，包括数据的请求
     */
    public void refreshView() {
        //下拉刷新
        TaobaoData.getInstance().loadTaobaoCardSeries();

        if (cardBanner.getType().isVisible()) {
            cardBanner.update();
        }

        if (cardCate.getType().isVisible()) {
            cardCate.update();
        }

        if (cardCps.getType().isVisible()) {
            cardCps.update();
        }

        if (cardHeadLine.getType().isVisible()) {
            cardHeadLine.update();
        }

        if (cardSplashSale.getType().isVisible()) {
            cardSplashSale.update();
        }

        if (cardSingleBanner.getType().isVisible()) {
            cardSingleBanner.update();
        }

        assemble();
    }
    public void enterPageView(Boolean enter){
        if (cardBanner != null)
            cardBanner.enterPageView(enter);
        if (cardSingleBanner != null)
            cardSingleBanner.enterPageView(enter);
    }


    /**
     * 整体页面可见与否
     *
     * @param isVisible
     */
    public void notifyPageChanged(boolean isVisible) {
        Log.d(TAG, "notifyPageChanged() called with: " + "isVisible = [" + isVisible + "]");
        cardSplashSale.setPageVisible(isVisible);
        cardCps.setPageVisible(isVisible);
        cardCate.setPageVisible(isVisible);
        cardBanner.setPageVisible(isVisible);
        cardSingleBanner.setPageVisible(isVisible);
    }

    public void notifyScrollChanged(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
        if(firstVisibleItem <= 2){
            if(cardCps.getType().isVisible()){
                Rect cpsRect = new Rect();
                cardCps.getView().getLocalVisibleRect(cpsRect);
                cardCps.notifyVisibleRectChanged(cpsRect);
            }

            if(cardCate.getType().isVisible()){
                Rect cateRect = new Rect();
                cardCate.getView().getLocalVisibleRect(cateRect);
                cardCate.notifyVisibleRectChanged(cateRect);
            }
        }
    }
}
