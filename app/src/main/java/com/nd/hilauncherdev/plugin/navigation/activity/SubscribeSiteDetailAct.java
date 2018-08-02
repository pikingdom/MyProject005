package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.MySubscribeArticleAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.MySubscribeSiteAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.SubscribeHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by linliangbin on 16-7-12.
 */
public class SubscribeSiteDetailAct extends BaseActivity implements View.OnClickListener {
    
    
    HeaderView headerView;
    PullToRefreshListView mListView;
    MySubscribeSiteAdapter mSiteAdapter;
    MySubscribeArticleAdapter mArticleAdapter;
    Context mContext;
    
    private View listHeaderView;
    String icon,name,cate,desc;
    int count,siteId;
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    private ImageView iconImage;
    private TextView nameText,detailText,descText;
    
    public static final String INTENT_FLAG_ICON = "icon";
    public static final String INTENT_FLAG_NAME = "name";
    public static final String INTENT_FLAG_CATEGORY = "cate";
    public static final String INTENT_FLAG_COUNT = "count";
    public static final String INTENT_FLAG_DESC = "desc";
    public static final String INTENT_FLAG_SITE_ID = "site_id";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subscribe_site_detail);
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN, "dyh");
        initData();
        initView();
    }
    
    private void initData(){
        
        Intent intent = getIntent();
        icon = intent.getStringExtra(INTENT_FLAG_ICON);
        name = intent.getStringExtra(INTENT_FLAG_NAME);
        cate = intent.getStringExtra(INTENT_FLAG_CATEGORY);
        count = intent.getIntExtra(INTENT_FLAG_COUNT,1000);
        desc = intent.getStringExtra(INTENT_FLAG_DESC);
        siteId = intent.getIntExtra(INTENT_FLAG_SITE_ID,0);
    }
    
    
    private void initView(){
    
        headerView = (HeaderView) this.findViewById(R.id.head_view);
        headerView.setTitle("");
        headerView.setGoBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
            
        mListView = (PullToRefreshListView) this.findViewById(R.id.listview);
        listHeaderView = LayoutInflater.from(mContext).inflate(R.layout.subscribe_site_detail_header,null);
        ((TextView)listHeaderView.findViewById(R.id.name)).setText(name);
        ((TextView)listHeaderView.findViewById(R.id.detail)).setText(cate + " | " + count + "人订阅");
        ((TextView)listHeaderView.findViewById(R.id.desc)).setText(desc);
        if(SubscribeHelper.hasSubscribeThisSite(mContext,siteId)){
            ((TextView)listHeaderView.findViewById(R.id.subscribe)).setText("取消订阅");
        }else{
            ((TextView)listHeaderView.findViewById(R.id.subscribe)).setText("订阅");
        }
        listHeaderView.findViewById(R.id.subscribe).setOnClickListener(this);
        ImageLoader.getInstance().displayImage(icon,(ImageView) listHeaderView.findViewById(R.id.icon), new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(360)).build());
        mListView.addHeaderView(listHeaderView);
        mListView.setPullEnable(false);
        mListView.disableBannerHeader();
        mArticleAdapter = new MySubscribeArticleAdapter(this, mListView, "");
        mArticleAdapter.setSiteIdList(siteId + "");
        mArticleAdapter.setShowReadCount(true);
        mArticleAdapter.refreshRequest();
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subscribe:
                if(SubscribeHelper.hasSubscribeThisSite(mContext,siteId)){
                    SubscribeHelper.removeSubscribeSite(mContext,siteId);
                    ((TextView)listHeaderView.findViewById(R.id.subscribe)).setText("订阅");
                }else{
                    SubscribeHelper.addSubscribeSite(mContext,siteId);
                    ((TextView)listHeaderView.findViewById(R.id.subscribe)).setText("取消订阅");
                    PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN, "tj");
                }
                break;
        }
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
