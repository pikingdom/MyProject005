package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.MySubscribeAct;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeArticleBean;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeSiteBean;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.SubscribeHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by linliangbin on 16-7-11.
 */
public class SubscribeCardViewHelper extends CardInnerViewHelperBase implements View.OnClickListener {
    
    
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    /**
     * 卡片布局总布局类
     */
    private View baseCardView = null;
    
    public static final int recommendIds[] = {R.id.navi_subscribe_recommend_item1,
            R.id.navi_subscribe_recommend_item2,
            R.id.navi_subscribe_recommend_item3};
    
    public static final int articleIds[] = {R.id.navi_card_subscribe_article_1,
            R.id.navi_card_subscribe_article_2,
            R.id.navi_card_subscribe_article_3
    };
    
    /**
     * 展示的推荐订阅号数
     * 展示的文章数
     */
    public static final int SHOW_COUNT = 3;
    
    
    public SubscribeCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
        super(navigationView, card, context);
    }
    
    @Override
    public void showDataS(NavigationView2 navigationView, Card card, Message msg) {
        if(msg.what == CardViewHelper.MSG_LOAD_SUBSCRIBE_SITE_SUC){
            showRecommendSite(card,(ArrayList < SubscribeSiteBean >) msg.obj);    
        }else if(msg.what == CardViewHelper.MSG_LOAD_SUBSCRIBE_ARTICLE_SUC){
            showArticle(card,(ArrayList<SubscribeArticleBean>) msg.obj);
        }
    }
    
    private void showRecommendSite(Card card,ArrayList<SubscribeSiteBean> subscribeSiteBeans){
        if(baseCardView != null){
            TextView nextText = ((TextView) baseCardView.findViewById(R.id.navi_card_base_next));
            if(nextText != null){
                nextText.setVisibility(View.GONE);
            }
        }
        View view = CardViewFactory.getInstance().getView(card);
        if(view == null)
            return;
        view.findViewById(R.id.navi_subscribe_card_recommend_layout_id).setVisibility(View.VISIBLE);
        view.findViewById(R.id.navi_subscribe_card_article_layout_id).setVisibility(View.GONE);
        view.findViewById(R.id.navi_subscribe_card_recommend_layout_id).setOnClickListener(this);
        if(subscribeSiteBeans != null && subscribeSiteBeans.size() >= SHOW_COUNT){
            for(int i=0;i< SHOW_COUNT;i++){
                SubscribeSiteBean siteBean = subscribeSiteBeans.get(i);
                View recommendItem = view.findViewById(recommendIds[i]);
                ((TextView)recommendItem.findViewById(R.id.navi_subscribe_card_recommnd_name)).setText(siteBean.siteName);
                ImageLoader.getInstance().displayImage(siteBean.siteIcon, ((ImageView) recommendItem.findViewById(R.id.navi_subscribe_card_recommnd_icon))
                            , new DisplayImageOptions.Builder().showImageOnLoading(mContext.getResources().getDrawable(R.drawable.fill_oval)).displayer(new RoundedBitmapDisplayer(360)).build());
                
            }
        }
    }
    
    private void showArticle(Card card,ArrayList<SubscribeArticleBean> articleBeans){
        if(baseCardView != null){
            TextView nextText = ((TextView) baseCardView.findViewById(R.id.navi_card_base_next));
            if(nextText != null){
                nextText.setVisibility(View.VISIBLE);
            }
        }
        
        View view = CardViewFactory.getInstance().getView(card);
        if(view == null)
            return;
        view.findViewById(R.id.navi_subscribe_card_recommend_layout_id).setVisibility(View.GONE);
        view.findViewById(R.id.navi_subscribe_card_article_layout_id).setVisibility(View.VISIBLE);
    
        
        if(articleBeans != null && articleBeans.size() >= SHOW_COUNT){
            for(int i =0 ;i<SHOW_COUNT;i++){
                final SubscribeArticleBean subscribeArticleBean = articleBeans.get(i);
                View articleItem = view.findViewById(articleIds[i]);
                ((TextView)articleItem.findViewById(R.id.navi_subscribe_card_article_layout_title)).setText(subscribeArticleBean.title);
                ((TextView)articleItem.findViewById(R.id.navi_subscribe_card_article_layout_from)).setText(subscribeArticleBean.from);
                asyncImageLoader.showDrawable(subscribeArticleBean.img, ((ImageView) articleItem.findViewById(R.id.navi_subscribe_card_article_layout_icon)),
                        ImageView.ScaleType.CENTER_CROP, CommonLauncherControl.getLoadingBgTranscunt());
                articleItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            LauncherCaller.openUrl(mContext, "", subscribeArticleBean.url,0,
                                    CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_CARD_CLICK,
                                    CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
                            PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_FIRST_SCREEN, "zxdj");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    
            }
        }
    }
    
    @Override
    public void initCardView(View cardView, Card card) {
        baseCardView = cardView;
        
        ((TextView)cardView.findViewById(R.id.navi_card_base_more)).setText(R.string.subscribe_card_more);
        (cardView.findViewById(R.id.navi_card_base_more)).setOnClickListener(this);
        cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(this);
        if(SubscribeHelper.getAddedSubscribeSiteCount(mContext) == 0){
            cardView.findViewById(R.id.navi_subscribe_card_recommend_layout_id).setVisibility(View.VISIBLE);
            cardView.findViewById(R.id.navi_subscribe_card_article_layout_id).setVisibility(View.GONE);
        }else{
            cardView.findViewById(R.id.navi_subscribe_card_recommend_layout_id).setVisibility(View.GONE);
            cardView.findViewById(R.id.navi_subscribe_card_article_layout_id).setVisibility(View.VISIBLE);
        }
        
    }
    
    @Override
    public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
        View objCardBaseV;
        if(com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil.getApiLevel() >= 14){
            objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_subscribe_card_layout, null);
        }else{
            objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_subscribe_card_layout_for_api_8, null);
        }
        cardInnerTotalV.addView(objCardBaseV);
        setCardView(cardInnerTotalV);
    }
    
    @Override
    public String getCardDisName(Card card) {
        return "wdy";
    }

    @Override
    public int getCardIcon(Card card) {
        return R.drawable.navi_card_wcy_ic;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.navi_card_base_more:
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_FIRST_SCREEN, "gdfx");
                startMySubscribeActivity();
                break;
            case R.id.navi_card_base_next:
                loadData(navigationView.handler, card, true);
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_FIRST_SCREEN, "hyp");
                break;
            case R.id.navi_subscribe_card_recommend_layout_id:
                startMySubscribeActivity();
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_FIRST_SCREEN, "dytj");
                break;

        }
    }
    
    private void startMySubscribeActivity(){
        try {
            Intent intent = new Intent(mContext, MySubscribeAct.class);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }
}
