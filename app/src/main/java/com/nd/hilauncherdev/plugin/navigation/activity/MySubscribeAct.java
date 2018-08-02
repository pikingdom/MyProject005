package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPager;
import com.nd.hilauncherdev.plugin.navigation.widget.MyPhoneViewPagerTab;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.MySubscribeArticleAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.MySubscribeSiteAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.RecommendSubscribeSiteAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.SubscribeHelper;

/**
 * Created by linliangbin on 16-7-12.
 */
public class MySubscribeAct extends BaseActivity {
    
    HeaderView headerView;

    Context mContext;
    private String[] titleResIds = {"订阅号","文章"};
    
    /**
     * 我的订阅相关的
     */
    PullToRefreshListView mListView;
    MySubscribeSiteAdapter mSiteAdapter;
    MySubscribeArticleAdapter mArticleAdapter;
    MyPhoneViewPager myPhoneViewPager;
    MyPhoneViewPagerTab myPhoneViewPagerTab;
    
    /**
     * 推荐订阅相关的
     */
    PullToRefreshListView recommendListView;
    RecommendSubscribeSiteAdapter mRecommendSiteAdapter;
    /**
     * 当前是否在显示推荐数据
     * 不显示推荐数据时则显示已经添加的订阅号信息
     */
    private boolean isShowingRecommend = false;
    /**
     * 上一次显示的添加站点数
     */
    private String lastAddedSiteString = "||";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 仅当当前添加的订阅号数发生变化时，重新获取数据
         */
        if(!lastAddedSiteString.equals(SubscribeHelper.getAddedSubscribeSite(mContext))){
            initView();
            lastAddedSiteString = SubscribeHelper.getAddedSubscribeSite(mContext);
        }
    }
    
    public void initView(){
        if(SubscribeHelper.getAddedSubscribeSiteCount(mContext) == 0){
            setContentView(R.layout.navigation_card_my_subscribe_activity_recommend);
            isShowingRecommend = true;
        }else{
            setContentView(R.layout.navigation_card_my_subscribe_activity);
            isShowingRecommend = false;
        }
    
        headerView = (HeaderView) this.findViewById(R.id.head_view);
        headerView.setTitle("微订阅");
        if(SubscribeHelper.getAddedSubscribeSiteCount(mContext) > 0){
            headerView.setBackgroundRes(R.drawable.common_head_bg_without_line);
        }
        headerView.setGoBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        initMenuOption();
        if(isShowingRecommend){
            initRecommendSite();
            updateAddOrFinishLayout();
        }else {
            initMySite();
        }
        if(myPhoneViewPager != null){
            myPhoneViewPager.setNeedAlphaChange(false);
        }
        
    }
    
    /**
     * 更新添加或完成布局
     */
    public void updateAddOrFinishLayout(){
        if(SubscribeHelper.getAddedSubscribeSiteCount(this) > 0){
            try {
                findViewById(R.id.subscribe_btn_layout).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.recommend_subscribe_cancle_sel));
                ((TextView)findViewById(R.id.recommend_subscribe_text)).setText("完成");
                findViewById(R.id.add_icon).setVisibility(View.GONE);
    
                findViewById(R.id.add_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!lastAddedSiteString.equals(SubscribeHelper.getAddedSubscribeSite(mContext))){
                            initView();
                            lastAddedSiteString = SubscribeHelper.getAddedSubscribeSite(mContext);
                        }
                    }
                });
    
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try {
                findViewById(R.id.subscribe_btn_layout).setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.navi_card_add_btn));
                ((TextView)findViewById(R.id.recommend_subscribe_text)).setText("点此添加更多订阅号");
                findViewById(R.id.add_icon).setVisibility(View.VISIBLE);
                findViewById(R.id.add_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mContext, SubscribSiteCateAct.class);
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    private void initRecommendSite(){
        
        recommendListView = (PullToRefreshListView) findViewById(R.id.recommend_listview);
        recommendListView.setPullEnable(false);
        recommendListView.disableBannerHeader();
        mRecommendSiteAdapter = new RecommendSubscribeSiteAdapter(this, recommendListView);
    }
    
    private void initMySite(){
        myPhoneViewPagerTab = (MyPhoneViewPagerTab) findViewById(R.id.container_pagertab);
        myPhoneViewPager = (MyPhoneViewPager) findViewById(R.id.container_pager);
        initPager();
    }
    
    private void initMenuOption() {
    
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_my_subscribe_header, null);
        headerView.replaceMenu(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(mContext, SubscribSiteCateAct.class);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        
            }
        });
        headerView.setMenuVisibility(View.VISIBLE);
    }
    
    
    private void initPager(){
        
        myPhoneViewPagerTab.addTitle(titleResIds);
        myPhoneViewPagerTab.setViewpager(myPhoneViewPager);
        myPhoneViewPagerTab.setUsedAsSecondTitle();
        myPhoneViewPager.setTab(myPhoneViewPagerTab);
        myPhoneViewPager.addView(getMySubscribeAccount());
        myPhoneViewPager.addView(getMySubscribeArticle());
        myPhoneViewPagerTab.setInitTab(0);
        myPhoneViewPager.setInitTab(0);
        
    }
    
    
    private View getMySubscribeAccount(){
        View parent = LayoutInflater.from(mContext).inflate(R.layout.list_view_layout,null);
        mListView = (PullToRefreshListView) parent.findViewById(R.id.listview);
        mListView.setPullEnable(false);
        mListView.disableBannerHeader();
        mSiteAdapter = new MySubscribeSiteAdapter(this, mListView,"");
        mSiteAdapter.refreshRequest();
        return  parent;
    }
    
    private View getMySubscribeArticle(){
        View parent = LayoutInflater.from(mContext).inflate(R.layout.list_view_layout,null);
        mListView = (PullToRefreshListView) parent.findViewById(R.id.listview);
        mListView.setPullEnable(false);
        mListView.disableBannerHeader();
        mArticleAdapter = new MySubscribeArticleAdapter(this, mListView, "");
        mArticleAdapter.setSiteIdList(SubscribeHelper.getAddedSubscribeSite(mContext));
        mArticleAdapter.refreshRequest();
        return  parent;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCacheData();
    }
    
    /**
     * 释放缓存的文章id map
     */
    private void releaseCacheData(){
        if(mArticleAdapter != null){
            mArticleAdapter.releaseRefer();
        }
    }
}
