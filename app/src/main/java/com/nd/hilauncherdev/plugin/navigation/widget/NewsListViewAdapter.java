package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dian91.ad.AdvertSDKManager;
import com.felink.sdk.common.DigestUtil;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.News;
import com.nd.hilauncherdev.plugin.navigation.bean.UCClientEvent;
import com.nd.hilauncherdev.plugin.navigation.loader.ZeroNewsLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.HttpCommon;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.MD5FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.UriParser;
import com.nostra13.universalimageloader.ex.ImageCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouheqiang_dian91 on 2016/3/3.
 */
public class NewsListViewAdapter extends AbsListViewAdapter<News, NewsListViewAdapter.ViewHolder> {
    //被选中的当前广告
    public static AdvertSDKManager.AdvertInfo advertInfo;
    //所有广告列表
    public List<AdvertSDKManager.AdvertInfo>  advertInfos;
    public NewsListViewAdapter(Context context) {
        super(context);
    }

    public NewsListViewAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
        if (listView instanceof PullToRefreshListView) {
            ((PullToRefreshListView) listView).setPullRateListent(listen);
        }
        News_Title_Width=ScreenUtil.getScreenWidth(context)-ScreenUtil.dip2px(context, 16 + 16 + 10+102);
    }
    
    @Override
    public void initLoadingView() {
        if(LauncherBranchController.isNavigationForCustomLauncher()){
            mLoadView = new NewsListLoadingView(mContext);
        }else{
            super.initLoadingView();
        }
    }
    
    private AsyncImageLoader mAsyncImageLoader = new AsyncImageLoader();
    /**
     * 下一次请求的时间 戳
     */
    private long mNextNonce = -1;
    /**
     * 下一次请求的页码
     */
    private int mNextPindx = -1;
    /**
     * uc最后一条recoid
     */
    private String mNewUcRecoId = "";
    /**
     * uc最后一条时间
     */
    private long mNewUcftime = -1;

    /**
     * 缓存下一次请求的时间 戳
     * <p/>
     * 当下拉刷新时，需要保存这个，当下拉刷新失败时
     */
    private long mCacheNextNonce = -1;
    /**
     * 缓存下一次请求的页码
     */
    private int mCacheNextPindx = -1;

    /**
     * 缓存当前请求批次
     */
    private String mCacheUcRecoId = "";

    /**
     * 缓存当前最新的一条新闻时间
     */
    private long mCacheUcftime = -1;
    /**
     * 本地记录的已曝光过的UC报光地址
     */
    private List<String> mUcShownUrl = new ArrayList<String>();

    Handler mHandle = new Handler();


    private ImageView mBannerImage;
    private TextView mBannerTitle;

    /**
     * 新闻请求完成+1
     * 广告请求完成+1
     * 只有这个Falg为2时，才会去显示列表
     */

    private int loadDataFinishFlag = 0;
    /**
     * 是否是刷新请求
     */
    private boolean isRefresh = false;

    /**
     * 是否要请求广告，这个变量由外部控制
     * 避免每次都刷新广告
     * */
    private boolean isNeedRequestAD =false;
    /**
     * 是否需要等待广告数据
     * */
    private boolean isNeedWaitAD =false;
    /**
     * 新闻的临时变量
     */
    public News.NewsList mTempNewsList;

//    /**
//     * 广告的临时变量
//     * */
//    public  List<News>  mTempADList;

    /**
     * 广告的临时变量
     */
    public List<News> mADList = new ArrayList<News>();

    /**
     * 默认广告推荐显示背景图
     */
    public int ad_bg_id = R.drawable.ad_text_bg;

    /**
     * 广告图片loading背景图
     */
    public int img_bg_id = R.drawable.banner_background;


    /**
     * 资讯图片loading 背景图
     */
    public int img_loading_bg_id = 0;

    /**
     * 默认的CV统计PAGE ID
     */
    public int CV_PAGE_ID = CvAnalysisConstant.OPEN_PAGE_NEWS_PAGE_ID;

    /**
     * 默认的CV统计新闻 POSITION ID
     */
    public int CV_POSITION_NEWS_ID = CvAnalysisConstant.OPEN_PAGE_SMALL_NEWS_POSTTION_ID;

    /**
     * 默认的CV统计广告 POSITION ID
     */
    public int CV_POSITION_AD_ID = CvAnalysisConstant.OPEN_PAGE_AD_POSITION_ID;

    /**
     * 广告插入的位置 每5条新闻一个广告
     */
    private static final int AD_POSITION = 6;

    public  int News_Title_Width=0;
    
    public HashMap<String,Integer> mAdMap =new HashMap<>();
    
    public boolean firstRequest=true;
    @Override
    public News getItem(int position) {
        int adcount = (position + 2) / AD_POSITION;
        if ((position + 2) % AD_POSITION == 0 && adcount > 0 && adcount <= mADList.size()) {
            AdvertSDKManager.AdvertInfo info =advertInfos.get(adcount - 1);
            AdvertSDKManager.submitShowEvent(mContext, mHandle, info);

            if (info.eventId != null) {
                if (!mAdMap.containsKey(info.eventId)) {
                    CvAnalysis.submitShowEvent(NavigationView2.activity, CV_PAGE_ID, CV_POSITION_AD_ID,
                            info.id, CvAnalysisConstant.RESTYPE_ADS);
                    mAdMap.put(info.eventId, info.id);
                    Log.e("zhou", "CV = eventid=" + info.eventId + " id=" + info.id);
                } else {
                    Log.e("zhou", "have send CV,so no send  eventid=" + info.eventId + " id=" + info.id);
                }
            }
            return mADList.get(adcount - 1);
        }
        adcount = adcount > mADList.size() ? mADList.size() : adcount;
        return mBeanList.get(position - adcount);
    }

    /**
     * @desc 获取标题栏字体大小
     * @author linliangbin
     * @time 2017/9/12 17:00
     */
    public int getNewsTitleTextSize(){
        return  0;
    }


    /**
     * @desc 获取标题描述字体大小
     * @author linliangbin
     * @time 2017/9/12 17:03
     */
    public int getNewsDescTextSize(){
        return  0;
    }


    /**
     * @desc 获取新闻来源字体大小
     * @author linliangbin
     * @time 2017/9/12 17:01
     */
    public int getPublicInfoTextSize(){
        return  0;
    }

    protected void setViewContent(ViewHolder holder, News bean, int position) {

    }
    /***
     * 需要重新请求广告
     * */
    public void setIsNeedRequestAD(boolean isNeed) {
        isNeedRequestAD = isNeed;
    }
    @Override
    protected void setOnScrollIdleView(ViewHolder holder, News bean, int position) {
        if (bean.type == News.TYPE_S_NEWS || bean.type == News.TYPE_AD) {
            if (bean.isUCNews()) {
                //uc新闻展示
                CvAnalysis.submitShowEvent(NavigationView2.activity, CV_PAGE_ID, CvAnalysisConstant.OPEN_PAGE_UC_NEWS_POSITION_ID,
                        CvAnalysisConstant.OPEN_PAGE_UC_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);
                if (validUcAdShowUrl(bean.showImpressionUrl)) {
                    //uc新闻广告展示
                    CvAnalysis.submitShowEvent(NavigationView2.activity, CV_PAGE_ID, CvAnalysisConstant.OPEN_PAGE_UC_AD_NEWS_POSITION_ID,
                            CvAnalysisConstant.OPEN_PAGE_UC_AD_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);
                    //上报UC广告展示地址
                    handleReportUcShow(bean.showImpressionUrl);
                }
            }
            
            holder.bannerAD.setVisibility(View.GONE);
            holder.zakerView.setVisibility(View.GONE);
            holder.wyView.setVisibility(View.VISIBLE);
            holder.wyTitle.setText(bean.title);
            holder.wyImg.setTag(bean.imageUrl);
            holder.wyDesc.setText(bean.desc);
            if(!TextUtils.isEmpty(bean.publicDate)){
                holder.wyPublicInfo.setText(bean.publicDate + " " + bean.publicAuthor);
            }else{
                holder.wyPublicInfo.setText(bean.publicAuthor);
            }

            int viewWidth = News_Title_Width;
            TextPaint textPaint = holder.wyTitle.getPaint();
            float textWidth = textPaint.measureText(bean.title);
            float lineCount = (textWidth / viewWidth);

            if (bean.type == News.TYPE_S_NEWS) {
                if (lineCount > 1) {
                    holder.wyDesc.setVisibility(View.GONE);
                } else {
                    holder.wyDesc.setMaxLines(2);
                    holder.wyDesc.setVisibility(View.VISIBLE);
                }
                holder.wyPublicInfo.setBackgroundColor(0x00ffffff);
            } else {
                holder.wyPublicInfo.setBackgroundResource(ad_bg_id);
                holder.wyDesc.setVisibility(View.VISIBLE);
                if (lineCount > 1) {
                    holder.wyDesc.setMaxLines(1);
                } else {
                    holder.wyDesc.setMaxLines(2);
                }
            }
            if (bean.imageUrl == null || bean.imageUrl.equals("")) {
                holder.wyImg.setImageResource(img_bg_id);
                return;
            }
            Drawable b = mAsyncImageLoader.loadDrawable(bean.imageUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                    ImageView img = (ImageView) mListView.findViewWithTag(imageUrl);
                    if (img != null && imageDrawable != null) {
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        img.setImageDrawable(imageDrawable);
                    }
                }
            });
            if (b == null) {
                try {
                    holder.wyImg.setScaleType(ImageView.ScaleType.FIT_XY);
                }catch (Throwable t){
        
                }
                if(img_loading_bg_id == 0)
                    holder.wyImg.setImageResource(CommonLauncherControl.getThemeNoFindSmall());
                else
                    holder.wyImg.setImageResource(img_loading_bg_id);
            } else {
                //holder.wyImg.setImageResource(R.drawable.theme_shop_v6_theme_no_find_small);
                holder.wyImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.wyImg.setImageDrawable(b);
            }


        } else if(bean.type == News.TYPE_L_NEWS  ) {
            holder.bannerAD.setVisibility(View.GONE);
            holder.wyView.setVisibility(View.GONE);
            holder.zakerView.setVisibility(View.VISIBLE);
            holder.zakerTitle.setText(bean.title);
            holder.zakerImg.setTag(bean.imageUrl);
            if (bean.imageUrl == null || bean.imageUrl.equals("")) {
                holder.zakerImg.setImageResource(img_bg_id);
                return;
            }
            Drawable b = mAsyncImageLoader.loadDrawable(bean.imageUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                    ImageView img = (ImageView) mListView.findViewWithTag(imageUrl);
                    if (img != null && imageDrawable != null) {
                        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        img.setImageDrawable(imageDrawable);
                    }
                }
            });
            if (b == null) {
                holder.zakerImg.setImageResource(img_bg_id);
            } else {
                holder.zakerImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.zakerImg.setImageDrawable(b);
            }

        }else
        {
            holder.wyView.setVisibility(View.GONE);
            holder.zakerView.setVisibility(View.GONE);
            holder.bannerAD.setVisibility(View.VISIBLE);
            holder.bannerADTitle.setText(bean.title);
            if (bean.desc == null || "".equals(bean.desc)) {
                holder.bannerADDesc.setVisibility(View.GONE);
            } else {
                holder.bannerADDesc.setVisibility(View.VISIBLE);
                holder.bannerADDesc.setText(bean.desc);
            }

            holder.bannerADImage.setTag(bean.imageUrl);
            if (bean.imageUrl == null || bean.imageUrl.equals("")) {
                holder.bannerADImage.setImageResource(img_bg_id);
                return;
            }
            Drawable b = mAsyncImageLoader.loadDrawable(bean.imageUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map extraParams) {
                    ImageView img = (ImageView) mListView.findViewWithTag(imageUrl);
                    if (img != null && imageDrawable != null) {
                        showBanner(imageDrawable, img);
                    }
                }
            });
            if (b == null) {
                holder.bannerADImage.setImageResource(img_bg_id);
                ViewGroup.LayoutParams lp = holder.bannerADImage.getLayoutParams();
                lp.height = ScreenUtil.dip2px(mContext,115);
            } else {
                showBanner(b,holder.bannerADImage);
            }
        }
    }

    @Override
    protected void setOnScrollViewContent(ViewHolder holder, News bean, int position) {
        if (bean.type == News.TYPE_S_NEWS || bean.type == News.TYPE_AD) {
            holder.zakerView.setVisibility(View.GONE);
            holder.bannerAD.setVisibility(View.GONE);
            holder.wyView.setVisibility(View.VISIBLE);
            holder.wyTitle.setText(bean.title);
            holder.wyDesc.setText(bean.desc);
            if(!TextUtils.isEmpty(bean.publicDate)){
                holder.wyPublicInfo.setText(bean.publicDate + " " + bean.publicAuthor);
            }else{
                holder.wyPublicInfo.setText(bean.publicAuthor);
            }
            int viewWidth = News_Title_Width;
            //Log.e("zhou","宽="+holder.wyTitle.getMeasuredWidth()+"V="+viewWidth);
            TextPaint textPaint = holder.wyTitle.getPaint();
            float textWidth = textPaint.measureText(bean.title);
            float lineCount = (textWidth / viewWidth);

            if (bean.type == News.TYPE_S_NEWS) {
                holder.wyPublicInfo.setBackgroundColor(0x00ffffff);
                if (lineCount > 1) {
                    holder.wyDesc.setVisibility(View.GONE);
                } else {
                    holder.wyDesc.setVisibility(View.VISIBLE);
                    holder.wyDesc.setMaxLines(2);
                }
            } else {
                holder.wyPublicInfo.setBackgroundResource(ad_bg_id);
                holder.wyDesc.setVisibility(View.VISIBLE);
                if (lineCount > 1) {
                    holder.wyDesc.setMaxLines(1);
                } else {
                    holder.wyDesc.setMaxLines(2);
                }
            }
            Drawable cache = mAsyncImageLoader.loadDrawableOnlyCache(bean.imageUrl);

            if (cache == null) {
                if(img_loading_bg_id == 0)
                    holder.wyImg.setImageResource(CommonLauncherControl.getThemeNoFindSmall());
                else
                    holder.wyImg.setImageResource(img_loading_bg_id);
                try {
                    holder.wyImg.setScaleType(ImageView.ScaleType.FIT_XY);
                }catch (Throwable t){
        
                }
            } else {
                holder.wyImg.setImageDrawable(cache);
                holder.wyImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }


        } else if(bean.type == News.TYPE_L_NEWS  ){
            //横幅形新闻
            holder.wyView.setVisibility(View.GONE);
            holder.bannerAD.setVisibility(View.GONE);
            holder.zakerView.setVisibility(View.VISIBLE);
            holder.zakerTitle.setText(bean.title);

            Drawable cache = mAsyncImageLoader.loadDrawableOnlyCache(bean.imageUrl);
            if (cache == null) {
                holder.zakerImg.setImageResource(img_bg_id);
                try {
                    holder.zakerImg.setScaleType(ImageView.ScaleType.FIT_XY);
                }catch (Throwable t){
        
                }
            } else {
                holder.zakerImg.setImageDrawable(cache);
                holder.zakerImg.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }
        }else {
            //横幅形广告
            holder.wyView.setVisibility(View.GONE);
            holder.bannerAD.setVisibility(View.VISIBLE);
            holder.zakerView.setVisibility(View.GONE);
            holder.bannerADTitle.setText(bean.title);
            holder.bannerADDesc.setText(bean.desc);
            Drawable cache = mAsyncImageLoader.loadDrawableOnlyCache(bean.imageUrl);
            if (cache == null) {
                holder.bannerADImage.setImageResource(img_bg_id);
                try {
                    holder.bannerADImage.setScaleType(ImageView.ScaleType.FIT_XY);
                }catch (Throwable t){
                    
                }

                ViewGroup.LayoutParams lp = holder.bannerADImage.getLayoutParams();
                lp.height = ScreenUtil.dip2px(mContext,115);
            } else {
                showBanner(cache, holder.bannerADImage);
            }
        }
    }

    private void showBanner(Drawable drawable,ImageView imageView){
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        int viewWidth = ScreenUtil.getCurrentScreenWidth(mContext);
        int viewHeight = (int) (drawableHeight / (float)drawableWidth * viewWidth);
        imageView.setImageDrawable(drawable);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width = viewWidth;
        lp.height = viewHeight;
        try {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }catch (Throwable t){
        
        }
    }
    @Override
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.news_item, null);
        return v;
    }

    @Override
    protected ViewHolder initHolder(View view) {

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.zakerImg = (ImageView) view.findViewById(R.id.imageViewZaker);
        viewHolder.zakerTitle = (TextView) view.findViewById(R.id.titleZaker);
        if(getNewsTitleTextSize() != 0){
            viewHolder.zakerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,getNewsTitleTextSize());
        }
        viewHolder.zakerView = view.findViewById(R.id.zakernews);

        viewHolder.wyView = view.findViewById(R.id.wyNews);
        viewHolder.wyImg = (ImageView) view.findViewById(R.id.imageViewWY);
        viewHolder.wyTitle = (TextView) view.findViewById(R.id.titleWY);
        if(getNewsTitleTextSize() != 0){
            viewHolder.wyTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,getNewsTitleTextSize());
        }
        viewHolder.wyDesc = (TextView) view.findViewById(R.id.newsdescWy);
        if(getNewsDescTextSize() != 0){
            viewHolder.wyDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP,getNewsDescTextSize());
        }
        viewHolder.wyPublicInfo = (TextView) view.findViewById(R.id.publicTime);
        if(getPublicInfoTextSize() != 0){
            viewHolder.wyPublicInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, getPublicInfoTextSize());
        }
        //viewHolder.wyTitle.getViewTreeObserver().addOnGlobalLayoutListener(new OnLayoutListen(viewHolder.wyTitle, viewHolder.wyDesc));

       viewHolder.bannerAD=view.findViewById(R.id.bannerAD);
        viewHolder.bannerADImage=(ImageView)view.findViewById(R.id.bannerADImage);
        viewHolder.bannerADTitle=(TextView)view.findViewById(R.id.bannerADTitle);
        viewHolder.bannerADDesc=(TextView)view.findViewById(R.id.bannerADDesc);
        viewHolder.adTitleRecommend = (TextView) view.findViewById(R.id.ad_recommend);
        return viewHolder;
    }

    public static class OnLayoutListen implements ViewTreeObserver.OnGlobalLayoutListener {
        private TextView mT1;
        private TextView mT2;

        public OnLayoutListen(TextView t1, TextView t2) {
            mT1 = t1;
            mT2 = t2;
        }

        public void onGlobalLayout() {
            if (mT1.getLineCount() > 1) {
                mT2.setVisibility(View.GONE);
            } else {
                mT2.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 设置下一次请求的时间戳和页码
     */
    public void setNextNonceAndPindex(long nextNonce, int nextPindex, String ucRecoId, long ucftime) {
        mNextNonce = nextNonce;
        mNextPindx = nextPindex;

        //2017.09.04 记录当前最新的UC头条批次ID + 新闻时间
        mNewUcRecoId = ucRecoId;
        mNewUcftime = ucftime;
    }

    /**
     * 保存时间戳
     */
    public void saveNextNonceAndPindex() {
        mCacheNextNonce = mNextNonce;
        mCacheNextPindx = mNextPindx;

        //2017.09.04 增加缓存UC头条：批次ID + 最新一条新闻时间
        mCacheUcRecoId = mNewUcRecoId;
        mCacheUcftime = mNewUcftime;

        //2017.09.04 清除本地已记录的UC头条曝光地址
        mUcShownUrl.clear();
    }

    /**
     * 还原时间戳
     */
    public void restoreNextNonceAndPindex() {
        mNextNonce = mCacheNextNonce;
        mNextPindx = mCacheNextPindx;

        //2017.09.04 重置UC头条批次ID + 最新一条新闻时间
        mNewUcftime = mCacheUcftime;
        mNewUcRecoId = mCacheUcRecoId;
    }

    @Override
    protected void doAddRequest() {
        isRefresh = false;
        doRequest("");
    }

    //下拉刷新时自动调用这个方法，请求数据可以在这个
    @Override
    public void doRefreshRequest() {
        //刷新的请求最新的新闻，所以不需要下一页和时间戳
        saveNextNonceAndPindex();
        setNextNonceAndPindex(-1, -1, "", -1);
        //重轩加载标志
        loadDataFinishFlag = 0;
        isRefresh = true;
        doRequest("");
    }

    @Override
    protected void doRequest(String url) {

        isNeedWaitAD=false;

        //只有是刷新请求时，才会请求广告
        if (isRefresh && !firstRequest) {
            if (isNeedRequestAD || mADList.isEmpty()) {
                isNeedRequestAD=false;
                isNeedWaitAD = true;
                ThreadUtil.executeMore(new Runnable() {
                    @Override
                    public void run() {
                        loadAD();
                    }
                });
            }
        }
        //Log.e("zhou", "清除数据" + mCurrentPage);
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                loadNews();
            }
        });


    }
    
    /**
     * 单纯的只刷新广告数据
     * */
     long latstRequestADTime=0;
     public void onlyRequestAD() {
         long currTime=System.currentTimeMillis();
         if (currTime - latstRequestADTime < 3600 * 1000) {
             Log.e("zhou","loadAD: time less than one hour");
             return;
         }
         Log.e("zhou","loadAD: onlyRequestAD");
         latstRequestADTime=currTime;
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<AdvertSDKManager.AdvertInfo> ads = AdvertSDKManager.getAdvertInfos(NavigationView2.activity, AnalyticsConstant.NEWS_PAGE_AD_POSITION);
                //Log.e("zhou", "广告数据=" + ads.size());
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ads != null) {
                            mAdMap.clear();
                            advertInfos = ads;
                            for (int i = 0; i < advertInfos.size(); i++) {
                                AdvertSDKManager.AdvertInfo info = advertInfos.get(i);
                                if (info.desc != null) {
                                    info.desc.trim();
                                    info.desc = "    " + info.desc;
                                }
                            }
                            mADList = ADToNews(ads);
                        }
                    }
                });

            }
        });
    }

    public void loadNews() {
        final News.NewsList list = ZeroNewsLoader.loadZakerAndWyNews(mContext, mNextNonce, mNextPindx, mNewUcRecoId, mNewUcftime);
        //返回的数据为空，并且当前列表也没有数据则尝试从SD从读缓存
        if ((list.mList.isEmpty()) && mBeanList.isEmpty()) {
            ZeroNewsLoader.readCacheNesFromSD(list);
            NewsListViewAdapter.this.setNextNonceAndPindex(list.mNextNonce, list.mNextPindx, list.ucRecoId, list.ucftime);
        }
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (STATUS_PULL_LOADING == mCurrentStatus && list.mList.isEmpty()) {
                    //下接刷新时，如果刷新失败，就还原是时间戳
                    restoreNextNonceAndPindex();
                }
                //有数据时才会去保存新的时间戳
                if (!list.mList.isEmpty()) {
                    NewsListViewAdapter.this.setNextNonceAndPindex(list.mNextNonce, list.mNextPindx, list.ucRecoId, list.ucftime);
                }
                mTempNewsList = list;
                //翻页请求不需要等待
                if (!isNeedWaitAD) {
                    //无论是否有数据，都要添加，为了让状态改变正常
                    if(isRefresh){
                        if((list != null && list.mList != null && list.mList.size() >= 2) || (mBeanList.size() == 0)){
                            NewsListViewAdapter.this.appendData(list.mList, list.isLast, 0);
                        }else{
                            if (STATUS_PULL_LOADING == mCurrentStatus) {
                                ((PullToRefreshListView) mListView).onLoadingComplete();
                                changeRequestStatus(STATUS_PENDDING);
                            }
                        }
                    }else{
                        NewsListViewAdapter.this.appendData(list.mList, list.isLast, 0);
                    }

                } else {
                    loadDataFinishFlag++;
                    if (loadDataFinishFlag == 2) {
                        if(isRefresh){
                            if((list != null && list.mList != null && list.mList.size() >= 2) || (mBeanList.size() == 0)){
                                NewsListViewAdapter.this.appendData(list.mList, list.isLast, 0);
                            }else{
                                if (STATUS_PULL_LOADING == mCurrentStatus) {
                                    ((PullToRefreshListView) mListView).onLoadingComplete();
                                    changeRequestStatus(STATUS_PENDDING);
                                }
                            }
                        }else{
                            NewsListViewAdapter.this.appendData(list.mList, list.isLast, 0);    
                        }

                    } else {
                        mTempNewsList = list;
                    }
                }
            }
        });
    }

    /**
     * 加载广告
     */
    public void loadAD() {
        final List<AdvertSDKManager.AdvertInfo> ads = AdvertSDKManager.getAdvertInfos(NavigationView2.activity, AnalyticsConstant.NEWS_PAGE_AD_POSITION);
        Log.e("zhou", "loadAD 广告数据=" + ads.size());
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                if (ads != null) {
                    mAdMap.clear();
                    loadDataFinishFlag++;
                    advertInfos = ads;
                    for (int i = 0; i < advertInfos.size(); i++) {
                        AdvertSDKManager.AdvertInfo info = advertInfos.get(i);
                        if (info.desc != null) {
                            info.desc.trim();
                            info.desc = "    " + info.desc;
                        }
                    }
                    mADList = ADToNews(ads);
                    if (loadDataFinishFlag == 2) {
                        if(isRefresh){
                            if((mTempNewsList != null && mTempNewsList.mList != null && mTempNewsList.mList.size() >= 2 || (mBeanList.size() == 0))){
                                NewsListViewAdapter.this.appendData(mTempNewsList.mList, mTempNewsList.isLast, 0);
                            }else{
                                if (STATUS_PULL_LOADING == mCurrentStatus) {
                                    ((PullToRefreshListView) mListView).onLoadingComplete();
                                    changeRequestStatus(STATUS_PENDDING);
                                }
                            }
                        }else{
                            NewsListViewAdapter.this.appendData(mTempNewsList.mList, mTempNewsList.isLast, 0);
                        }

                    }
                }
            }
        });
    }

    /**
     * 把广告的数据转成新闻的数据形式
     */
    private List<News> ADToNews(List<AdvertSDKManager.AdvertInfo> ads) {
        mADList.clear();
        for (int i = 0; i < ads.size(); i++) {
            News news = new News();
            AdvertSDKManager.AdvertInfo ad = ads.get(i);
            news.imageUrl = ad.picUrl;
            news.title = ad.name;
            news.desc = ad.desc;
            news.publicAuthor = "广告";
            news.publicDate = "";
            news.linkUrl = ad.actionIntent;
            float ratio=0;
            if (ad.height > 0) {
                ratio = ad.width / (float) ad.height;
            }
            if(ratio >= 2){
                news.type = News.TYPE_BANNER_AD;
            }else {
                news.type = News.TYPE_AD;
            }
            mADList.add(news);
        }
        return mADList;
    }

    public class ViewHolder {
        //zaker新闻图
        public ImageView zakerImg;
        //zaker新闻标师
        public TextView zakerTitle;

        //zaker新闻最顶层的view
        public View zakerView;

        //网易新闻图
        public ImageView wyImg;
        //网易新闻标师
        public TextView wyTitle;

        //网易新闻最顶层的view
        public View wyView;

        //网易新闻的描述
        public TextView wyDesc;

        //网易的发布时间和作者
        public TextView wyPublicInfo;

        public View bannerAD;
        public TextView bannerADTitle;
        public TextView bannerADDesc;
        public TextView adTitleRecommend;
        public ImageView bannerADImage;

    }

    @Override
    protected void onDataItemClick(View view, int position) {
        String url;

        News news;
        news=getItem(position);
        url = news.linkUrl;

        if (news.type == News.TYPE_S_NEWS) {
            CvAnalysis.submitClickEvent(NavigationView2.activity, CV_PAGE_ID, CV_POSITION_NEWS_ID,
                        CvAnalysisConstant.OPEN_PAGE_SMALL_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);

            if (news.isUCNews()) {
                //UC新闻点击CV统计
                CvAnalysis.submitClickEvent(NavigationView2.activity, CV_PAGE_ID, CvAnalysisConstant.OPEN_PAGE_UC_NEWS_POSITION_ID,
                        CvAnalysisConstant.OPEN_PAGE_UC_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);
                if (validUcAdShowUrl(news.showImpressionUrl)) {
                    //UC新闻广告点击CV统计
                    CvAnalysis.submitClickEvent(NavigationView2.activity, CV_PAGE_ID, CvAnalysisConstant.OPEN_PAGE_UC_AD_NEWS_POSITION_ID,
                            CvAnalysisConstant.OPEN_PAGE_UC_AD_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);
                }
            }
        } else if (news.type == News.TYPE_L_NEWS) {
            CvAnalysis.submitClickEvent(NavigationView2.activity, CvAnalysisConstant.OPEN_PAGE_NEWS_PAGE_ID, CvAnalysisConstant.OPEN_PAGE_BIG_NEWS_POSTTION_ID,
                    CvAnalysisConstant.OPEN_PAGE_BIG_NEWS_RES_ID, CvAnalysisConstant.RESTYPE_LINKS);
        }else if(news.type == News.TYPE_AD || news.type==News.TYPE_BANNER_AD) {
            //TODO: /提交广告打点
            try {
                int index = (position + 2) / AD_POSITION - 1;
                if (advertInfos != null && advertInfos.size() > index) {
                    advertInfo = advertInfos.get(index);
                }
                CvAnalysis.submitClickEvent(NavigationView2.activity, CV_PAGE_ID, CV_POSITION_AD_ID,
                        advertInfo.id, CvAnalysisConstant.RESTYPE_ADS, advertInfo.sourceId);
                AdvertSDKController.submitClickEvent(mContext, mHandle, advertInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(!url.startsWith("http") && !url.startsWith("https")){
            UriParser.handleUriEx(mContext, url);
            return;
        }
        try{
            if(news.type == News.TYPE_AD || news.type == News.TYPE_BANNER_AD || news.isUCNews()){
                /**
                 * 广告由于需要统计，走内置浏览器
                 * 新闻走QQ浏览器逻辑
                 */
                Intent intent = new Intent();
                intent.setClass(mContext, NewsDetailActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("need_download_analytic", true);
                intent.putExtra("need_loading",true);
                intent.putExtra("need_download_analytic", true);

                UCClientEvent event = UCClientEvent.format(news);
                JSONObject jsonEvent = event.toJsonObject();
                if (event != null && jsonEvent != null) {
                    intent.putExtra("need_report", true);
                    intent.putExtra("uc_event", jsonEvent.toString());
                }
                mContext.startActivity(intent);
            }else{
                LauncherCaller.openUrl(mContext, "", url);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void addBannerView() {
        if (mListView instanceof PullToRefreshListView) {
            PullToRefreshListView listView = (PullToRefreshListView) mListView;
            LinearLayout banner = listView.getBannerContainer();
            banner.setVisibility(View.VISIBLE);
            ViewGroup bannerContent = (ViewGroup) mInflater.inflate(R.layout.news_banner_head, null);
            mBannerImage = (ImageView) bannerContent.findViewById(R.id.bannerImage);
            mBannerTitle = (TextView) bannerContent.findViewById(R.id.bannerTitle);
            if(getNewsTitleTextSize() != 0){
                mBannerTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,getNewsTitleTextSize());
            }
            banner.addView(bannerContent);
            //顶部大图的点击态
            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mBannerData.isEmpty()) {
                        String url = mBannerData.get(0).linkUrl;
                        LauncherCaller.openUrl(mContext, "", url);
                        
                    }

                }
            });
        }
    }

    private PullToRefreshListView.OnPullRateListen listen = new PullToRefreshListView.OnPullRateListen() {
        @Override
        public void OnPullRate(float rate) {

            if (mBannerImage != null) {
                //Log.e("zhou", "放大图片" + rate);
                calculateMatrix(mBannerImage, mBannerImage.getDrawable(), rate);
                mBannerImage.setImageMatrix(bannerMatrix);
            }

        }
    };
    //baner图片要实现放大缩小效果
    private Matrix bannerMatrix = new Matrix();

    protected void setBannerContent(List<News> list) {
        if (list == null || list.isEmpty()) return;

        News oneNews = list.get(0);
        mBannerTitle.setText(oneNews.title);
        mBannerImage.setTag(oneNews.imageUrl);
        if (oneNews.imageUrl == null || oneNews.imageUrl.equals("")) {
            mBannerImage.setImageResource(img_bg_id);
            return;
        }
        Drawable b = mAsyncImageLoader.loadDrawable(oneNews.imageUrl, new ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                String url = (String) mBannerImage.getTag();
                if (url != null && imageDrawable!=null && url.equals(imageUrl)) {
                    calculateMatrix(mBannerImage, imageDrawable, 0);
                    mBannerImage.setImageMatrix(bannerMatrix);
                    mBannerImage.setImageDrawable(imageDrawable);
                    mBannerImage.invalidate();

                }
            }
        });
        if (b != null) {
            calculateMatrix(mBannerImage, b, 0);
            mBannerImage.setImageMatrix(bannerMatrix);
            mBannerImage.setImageDrawable(b);
        } else {
            mBannerImage.setImageResource(img_bg_id);
        }

    }

    /**
     * 计算矩阵
     */

    public void calculateMatrix(ImageView view, Drawable drawable, float extendScale) {
        bannerMatrix.reset();
        if (drawable == null) return;
        float scale;
        float dx = 0, dy = 0;

        int dwidth = drawable.getIntrinsicWidth();
        int dheight = drawable.getIntrinsicHeight();

        int vheight = view.getHeight();
        int vwidth = view.getWidth();
        if (vheight == 0) vheight = ScreenUtil.dip2px(mContext, 150);
        if (vwidth == 0) vwidth = ScreenUtil.getScreenWidth(mContext);
        if (dwidth * vheight > vwidth * dheight) {
            scale = (float) vheight / (float) dheight;
            dx = (vwidth - dwidth * scale) * 0.5f;
        } else {
            scale = (float) vwidth / (float) dwidth;
            dy = (vheight - dheight * scale) * 0.5f;
        }

        //额外扩展
        if (extendScale > 0) {
            extendScale = extendScale * 0.8f;
            scale = scale + extendScale;
            dx = (vwidth - dwidth * scale) * 0.5f;
            dy = (vheight - dheight * scale) * 0.5f;
        }
        bannerMatrix.setScale(scale, scale);
        bannerMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
    }

    /**
     * 处理上报UC头条展示URL
     * @param showImpressionUrl
     */
    private void handleReportUcShow(final String showImpressionUrl) {
        if (validUcAdShowUrl(showImpressionUrl)) {
            final String key = MD5FileUtil.getMD5String(showImpressionUrl);
            if (!mUcShownUrl.contains(key)) {
                //先置位
                mUcShownUrl.add(key);
                ThreadUtil.executeMore(new Runnable() {
                    @Override
                    public void run() {
                        HttpCommon common = new HttpCommon(showImpressionUrl);
                        final String response = common.getResponseAsStringGET(null);
                        Global.runInMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response == null) {
                                    mUcShownUrl.remove(key);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private boolean validUcAdShowUrl(String showImpressionUrl) {
        if (!TextUtils.isEmpty(showImpressionUrl) && !"null".equalsIgnoreCase(showImpressionUrl)) {
            return true;
        }
        return false;
    }
}
