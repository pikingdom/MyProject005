package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.News;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeArticleBean;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linliangbin on 16-7-12.
 */
public class MySubscribeArticleAdapter extends AbsListViewAdapter<SubscribeArticleBean,MySubscribeArticleAdapter.ViewHolder> {
    
    
    public void setSiteIdList(String siteIdList) {
        this.siteIdList = siteIdList;
    }
    
    private String siteIdList = "";
    
    public void setShowReadCount(boolean showReadCount) {
        this.showReadCount = showReadCount;
    }
    
    /**
     * 是否显示文章阅读数
     */
    private boolean showReadCount = false;
    
    /**
     * 当前文章列表IDmap,用于文章显示去重
     */
    private HashMap<String,String> idMap = new HashMap<String,String>();
    
    public MySubscribeArticleAdapter(Context context) {
        super(context);
    }
    
    public MySubscribeArticleAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
        //趣发现卡片，不需要展示头部，不需要移除第一个item
        removeFirstForBanner = false;
    }
    
    /**
     * 下一次请求的页码
     */
    private int mNextPindx = 0;
    
    Handler mHandle = new Handler();
    
    
    
    
    @Override
    protected void setOnScrollIdleView(final ViewHolder holder, SubscribeArticleBean bean, int position) {
        asyncImageLoader.showDrawable(bean.img, holder.subscribe_img,
                ImageView.ScaleType.CENTER_CROP, CommonLauncherControl.getVph());
    }
    
    @Override
    protected void setOnScrollViewContent(ViewHolder holder, SubscribeArticleBean bean, int position) {
        
        Drawable drawable = ImageLoader.getInstance().loadDrawableIfExistInMemory(bean.img);
        if(drawable != null){
            holder.subscribe_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.subscribe_img.setImageDrawable(drawable);
        }else {
            try {
                holder.subscribe_img.setScaleType(ImageView.ScaleType.FIT_XY);
            }catch (Throwable t){
                t.printStackTrace();
            }
            holder.subscribe_img.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
        }
    }
    
    
    
    @Override
    protected void doAddRequest() {
        doRequest("");
    }
    
    //下拉刷新时自动调用这个方法，请求数据可以在这个
    @Override
    protected void doRefreshRequest() {
        //刷新的请求最新的新闻，所以不需要下一页和时间戳
        mNextPindx = 0;
        doRequest("");
    }
    
    @Override
    protected void onDataItemClick(View view, int position) {
        
        try {
            LauncherCaller.openUrl(mContext,"",getItem(position).url,0,
                    CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_CARD_CLICK,
                    CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
            PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN, "wz");
        }catch (Exception e){
            e.printStackTrace();
        }

        
    }
    
    @Override
    protected void setViewContent(ViewHolder holder, SubscribeArticleBean bean, int position) {
        holder.subscribe_name.setText(bean.title);
        holder.subscribe_from.setText(bean.from);
        
        if(showReadCount){
            holder.subscribe_count_icon.setVisibility(View.VISIBLE);
            holder.subscribe_count.setVisibility(View.VISIBLE);
            holder.subscribe_from.setVisibility(View.GONE);
            holder.subscribe_count.setText(bean.readCount+"");
        }else{
            holder.subscribe_count_icon.setVisibility(View.INVISIBLE);
            holder.subscribe_count.setVisibility(View.INVISIBLE);
            holder.subscribe_from.setVisibility(View.VISIBLE);
        }
    }
    
    
    @Override
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.navi_subscribe_article_item, null);
        return v;
    }
    
    @Override
    protected ViewHolder initHolder(View view) {
        
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.subscribe_img = (ImageView) view.findViewById(R.id.subscribe_small_icon);
        viewHolder.subscribe_name = (TextView) view.findViewById(R.id.subscribe_article_name);
        viewHolder.subscribe_from = (TextView) view.findViewById(R.id.subscribe_article_from);
        viewHolder.subscribe_count = (TextView) view.findViewById(R.id.subscribe_article_readcount);
        viewHolder.subscribe_count_icon = (ImageView) view.findViewById(R.id.subscribe_article_readcount_icon);
        return viewHolder;
        
    }
    
    
    @Override
    protected void doRequest(String url) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
        
                try {
                    final int lastPi = mNextPindx;
                    final ArrayList<SubscribeArticleBean> beans = requestList();
                    if (beans != null && beans.size() > 0)
                        mNextPindx++;
                    mHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean isLast = false;
                            if (beans != null && beans.size() > 0 && beans.size() < 20  && lastPi != 0) {
                                //请求的数据不到请求的页码，则表示达到结束
                                isLast = true;
                            }
                            if (beans == null) {
                                MySubscribeArticleAdapter.this.appendData(new ArrayList<SubscribeArticleBean>(), false, 0);
                            } else {
                                MySubscribeArticleAdapter.this.appendData(beans, isLast, 0);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    @Override
    protected void addBannerView() {
        
    }
    
    protected void setBannerContent(List<SubscribeArticleBean> list) {
        
    }
    
    
    /**
     * 获取列表数据
     * 过滤重复数据
     * @return
     */
    private ArrayList<SubscribeArticleBean> requestList(){
        ArrayList<SubscribeArticleBean> resultBeans = new ArrayList<SubscribeArticleBean>();
        
        ArrayList<SubscribeArticleBean> beans = SubscribeLoader.loadSubscribArticleById_6014(mContext,siteIdList,mNextPindx);

        if(idMap == null){
            idMap = new HashMap<String,String>();
        }
        
        if(beans != null && idMap != null){
            for(int i = 0;i<beans.size();i++){
                SubscribeArticleBean subscribeArticleBean = beans.get(i);
                if(idMap.containsKey(subscribeArticleBean.articleId+"")){
                    continue;
                }else{
                    resultBeans.add(subscribeArticleBean);
                    idMap.put(subscribeArticleBean.articleId+"",subscribeArticleBean.articleId+"");
                }
            }
        }
        return resultBeans;
        
    }
    
    
    public void releaseRefer(){
        
        if(idMap != null){
            idMap.clear();
        }
    }
    public class ViewHolder {
        //文章图片
        public ImageView subscribe_img;
        //文章标题
        public TextView subscribe_name;
        //文章来源
        public TextView subscribe_from;
        //阅读人数
        public TextView subscribe_count;
        //人数icon
        public ImageView subscribe_count_icon;
        
    }
    
}
