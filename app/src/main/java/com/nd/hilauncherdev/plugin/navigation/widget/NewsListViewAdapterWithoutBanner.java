package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.News;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouheqiang_dian91 on 2016/3/3.
 */
public class NewsListViewAdapterWithoutBanner extends NewsListViewAdapter{

    boolean hasShowTitle = false;

    public NewsListViewAdapterWithoutBanner(Context context) {
        super(context);
    }

    public NewsListViewAdapterWithoutBanner(Context context, ListView listView, String url) {
        super(context, listView, url);
        News_Title_Width= ScreenUtil.getScreenWidth(context)-ScreenUtil.dip2px(context, 16 + 16 + 10*2 + 10 + 102);
        ad_bg_id = R.drawable.bg_ad;
        //零屏底部的新闻资讯使用半透明loading图片
        img_bg_id = CommonLauncherControl.getLoadingBgTranscunt();
        img_loading_bg_id = CommonLauncherControl.getLoadingBgTranscunt();
        CV_PAGE_ID = CvAnalysisConstant.NAVIGATION_SCREEN_INTO;
        CV_POSITION_AD_ID = CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_NEWS_CARD_AD;
        CV_POSITION_NEWS_ID = CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_NEWS_CARD_NEWS;
        isNoLoadingView = true;
        //一开始就显示标题
        if(!hasShowTitle){
            View view = mListView.findViewById(R.id.news_card_title_layout);
            if(view != null){
                view.setVisibility(View.VISIBLE);
                hasShowTitle = true;
            }
        }
    }

    @Override
    protected void setOnScrollIdleView(ViewHolder holder, News bean, int position) {
        super.setOnScrollIdleView(holder, bean, position);
    }

    @Override
    protected void setOnScrollViewContent(ViewHolder holder, News bean, int position) {
        super.setOnScrollViewContent(holder, bean, position);
    }

    @Override
    protected void addBannerView() {

    }
    protected void setBannerContent(List<News> list) {

    }


    @Override
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.news_item_trancunt, null);
        return v;
    }

    @Override
    public News getItem(int position) {
        News news = super.getItem(position);
        news.desc = news.desc.trim();
        return super.getItem(position);
    }

    @Override
    protected void addFooterView() {
        if (mFootView == null) {
            mFootView = new ListViewFooter(mInflater,R.layout.market_footer_wait_margin);
            mFootView.getFooterView().findViewById(R.id.market_footer_margin).setBackgroundResource(R.drawable.navi_card_content_bg);
            ((TextView)mFootView.getFooterView().findViewById(R.id.tv_market_refresh)).setTextColor(Color.parseColor("#ffffffff"));
            ((TextView)mFootView.getFooterView().findViewById(R.id.market_textView1)).setTextColor(Color.parseColor("#ffffffff"));
        }
        super.addFooterView();
    }

    public void removeLoadingView(){

        if(mLoadView != null && mListView != null){
            ((ViewGroup) mListView.getParent()).removeView(mLoadView);
            mListView.setEmptyView(null);
        }

    }

    @Override
    protected void setViewContent(ViewHolder holder, News bean, int position) {
        super.setViewContent(holder, bean, position);
        initFont(holder);
    }

    /**
     * 更新字体，
     * 解决应用字体后新闻内字体不更新的问题
     * @param holder
     */
    private void initFont(ViewHolder holder){
        PaintUtils2.assemblyTypeface(holder.zakerTitle.getPaint());
        PaintUtils2.assemblyTypeface(holder.wyTitle.getPaint());
        PaintUtils2.assemblyTypeface(holder.wyDesc.getPaint());
        PaintUtils2.assemblyTypeface(holder.wyPublicInfo.getPaint());
        PaintUtils2.assemblyTypeface(holder.bannerADTitle.getPaint());
        PaintUtils2.assemblyTypeface(holder.bannerADDesc.getPaint());
        PaintUtils2.assemblyTypeface(holder.adTitleRecommend.getPaint());

    }

    @Override
    public void appendData(List<News> list, boolean last, int index) {
        //去除zaker 新闻
        List<News> newsWithoutZaker = new ArrayList<News>();
        if(list != null){
            for(News news : list){
                if(news.type != News.TYPE_L_NEWS){
                    newsWithoutZaker.add(news);
                }
            }
        }

        super.appendData(newsWithoutZaker, last, index);


    }

    public void updateFooterFont(){
        if(mFootView != null)
            mFootView.initFont();
    }
}
