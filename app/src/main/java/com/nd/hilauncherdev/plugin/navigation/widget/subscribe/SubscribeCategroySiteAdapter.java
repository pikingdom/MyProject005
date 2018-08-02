package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.SubscribeSiteDetailAct;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeSiteBean;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.SubscribeLoadingView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linliangbin on 16-7-12.
 */
public class SubscribeCategroySiteAdapter extends AbsListViewAdapter<SubscribeSiteBean,SubscribeCategroySiteAdapter.ViewHolder>  {
    

    private HashMap<String,SoftReference<ArrayList<SubscribeSiteBean>>> siteBeansMap = new HashMap<String,SoftReference<ArrayList<SubscribeSiteBean>>>();
    
    /**
     * 下一次请求的页码
     */
    private int mNextPindx = 0;
    
    /**
     * 当前cateId
     */
    private String currentCateId;
    
    
    private boolean isLocalCate = false;
    
    Handler mHandle = new Handler();
    
    Drawable loadingDrawable;
    
    /**
     * 采用时间戳
     * 避免由于请求网络数据缓慢导致界面刷新错误
     */
    private long currentSN = 0;
    
    public void setCallback(mainSubInterface callback) {
        this.callback = callback;
    }
    
    mainSubInterface callback;
    
    public SubscribeCategroySiteAdapter(Context context, ListView listView, String url,String cateId) {
        super(context, listView, url);
        removeFirstForBanner = false;
        currentCateId = cateId;
        loadingDrawable = context.getResources().getDrawable(R.drawable.fill_oval);
        SHOW_LIST_END_FOOTER_LIMIT = (ScreenUtil.getCurrentScreenHeight(context) - ScreenUtil.dip2px(context,40))/ScreenUtil.dip2px(context,70)+1;
    }
    
    /**
     * 更新选中的列表信息
     * @param selectedCateId 选中的分类ID 或 本地的订阅号列表
     * @param isLocal 是否是本地订阅号列表
     */
    public void updateCate(String selectedCateId,boolean isLocal){
        isLocalCate = isLocal;
        
        if(siteBeansMap.containsKey(selectedCateId) && !isLocal){
            SoftReference<ArrayList<SubscribeSiteBean>> siteBeansRefer = siteBeansMap.get(selectedCateId);
            ArrayList<SubscribeSiteBean> siteBeans;
            
            if(siteBeansRefer != null && (siteBeans=siteBeansRefer.get()) != null ){
                reset();
                currentCateId = selectedCateId;
                appendData(siteBeans,isLocal,0);
            }else{
                requestUnCacheData(selectedCateId);
            }
        }else{
            requestUnCacheData(selectedCateId);
        }
    }
    
    
    private void requestUnCacheData(String selectedCateId){
        currentSN = System.currentTimeMillis();
        siteBeansMap.put(currentCateId+"", new SoftReference<ArrayList<SubscribeSiteBean>>((ArrayList<SubscribeSiteBean>) mBeanList));
        mBeanList = new ArrayList<SubscribeSiteBean>();
        removeFootView();
        currentCateId = selectedCateId;
        refreshRequest();
    }
    
    @Override
    public void initLoadingView() {
        mLoadView = new SubscribeLoadingView(mContext);
    }
    
    @Override
    public void doNetErrorRefreshCallback() {
        super.doNetErrorRefreshCallback();
        if(callback != null){
            callback.updateMainList();
        }
    }
    
    @Override
    protected void setOnScrollIdleView(ViewHolder holder, SubscribeSiteBean bean, int position) {
    }
    
    @Override
    protected void setViewContent(ViewHolder holder, final SubscribeSiteBean bean, int position) {
        holder.titleText.setText(bean.siteName);
        holder.countText.setText(bean.subCount + "人订阅");
        
        
        if(SubscribeHelper.hasSubscribeThisSite(mContext,bean.siteID)){
            holder.addText.setText("取消订阅");
            holder.addText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.subscribe_card_has_sub_btn_sel));
        }else{
            holder.addText.setText("订阅");
            holder.addText.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.subscribe_card_sub_btn_sel));
        }
        
        holder.addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SubscribeHelper.hasSubscribeThisSite(mContext, bean.siteID)) {
                    SubscribeHelper.removeSubscribeSite(mContext, bean.siteID);
                } else {
                    SubscribeHelper.addSubscribeSite(mContext, bean.siteID);
                    PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN, "tj");
                }
                notifyDataSetChanged();
            }
        });
        ImageLoader.getInstance().displayImage(bean.siteIcon, holder.icon, new DisplayImageOptions.Builder().showImageOnLoading(loadingDrawable).displayer(new RoundedBitmapDisplayer(360)).build());
    }
    
    
    @Override
    protected void setOnScrollViewContent(ViewHolder holder, SubscribeSiteBean bean, int position) {

    }
    
    
    @Override
    protected View createItem() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.subscribe_cate_sub_list_item,null);
        return view;
    }
    
    @Override
    protected ViewHolder initHolder(View view) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.icon = (ImageView) view.findViewById(R.id.subscribe_site_icon_img);
        viewHolder.addText = (TextView) view.findViewById(R.id.subscribe_site_add_btn);
        viewHolder.titleText = (TextView) view.findViewById(R.id.subscribe_site_title_text);
        viewHolder.countText = (TextView) view.findViewById(R.id.subscribe_site_count_text);
        return viewHolder;
    }
    
    @Override
    protected void onDataItemClick(View view, int position) {
        try {

            SubscribeSiteBean subscribeSiteBean = getItem(position);
            Intent intent = new Intent(mContext,SubscribeSiteDetailAct.class);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_ICON, subscribeSiteBean.siteIcon);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_NAME, subscribeSiteBean.siteName);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_CATEGORY, subscribeSiteBean.cateName);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_COUNT, subscribeSiteBean.subCount);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_DESC, subscribeSiteBean.summary);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_SITE_ID, subscribeSiteBean.siteID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
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
    protected void doRequest(String url) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
        
                try {
                    final int lastPi = mNextPindx;
                    final ArrayList<SubscribeSiteBean> beans;
                    final long tempSN = currentSN;
            
                    if (isLocalCate) {
                        beans = SubscribeLoader.loadSubscribSiteById_6015(mContext, currentCateId, 1);
                    } else {
                        beans = SubscribeLoader.loadSubscribSiteByCate_6013(mContext, mNextPindx, currentCateId);
                    }
            
                    if (beans != null && beans.size() > 0)
                        mNextPindx++;
                    mHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            if (tempSN >= currentSN) {
                                boolean isLast = false;
                                if (beans != null && beans.size() > 0 && beans.size() < 10 && lastPi != 1) {
                                    isLast = true;
                                }
                                //我的订阅只添加一组数据，避免重复添加
                                if (isLocalCate && mBeanList.size() > 0)
                                    return;
        
                                if (isLocalCate)
                                    isLast = true;
        
                                if (beans == null) {
                                    SubscribeCategroySiteAdapter.this.appendData(new ArrayList<SubscribeSiteBean>(), false, 0);
                                } else {
                                    SubscribeCategroySiteAdapter.this.appendData(beans, isLast, 0);
                                }
                                if (beans == null || beans.size() == 0 && isLocalCate) {
                                    changeRequestStatus(STATUS_NO_DATA);
                                }
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
    
    protected void setBannerContent(List<SubscribeSiteBean> list) {
        
    }
    
    
    class ViewHolder{
        ImageView icon;
        TextView addText;
        TextView titleText;
        TextView countText;
    }
    
}
