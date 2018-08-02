package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.SubscribeSiteDetailAct;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeSiteBean;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linliangbin on 16-7-12.
 */
public class MySubscribeSiteAdapter extends AbsListViewAdapter<SubscribeSiteBean,MySubscribeSiteAdapter.ViewHolder> {
    
    private Handler handler = new Handler();
    
    public MySubscribeSiteAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
        removeFirstForBanner = false;
        SHOW_LIST_END_FOOTER_LIMIT = (ScreenUtil.getCurrentScreenHeight(context) - ScreenUtil.dip2px(context,40) -ScreenUtil.dip2px(context,42))/ScreenUtil.dip2px(context,92) + 1;
    }
    
    
    @Override  
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.navi_subscribe_site_item, null);
        return v;
    }
    
    @Override
    protected ViewHolder initHolder(View view) {
        
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.subscribe_img = (ImageView) view.findViewById(R.id.subscribe_small_icon);
        viewHolder.subscribe_name = (TextView) view.findViewById(R.id.subscribe_site_name);
        viewHolder.subscribe_update_time = (TextView) view.findViewById(R.id.subscribe_site_update_time);
        viewHolder.subscribe_desc = (TextView) view.findViewById(R.id.subscribe_site_desc);
        return viewHolder;
    }
    
    
    
    
    @Override
    protected void doAddRequest() {
        doRequest("");
    }
    
    @Override
    protected void doRefreshRequest() {
        doRequest("");
    }
    
    
    @Override
    protected void doRequest(String url) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<SubscribeSiteBean> beans = SubscribeLoader.loadSubscribSiteById_6015(mContext, SubscribeHelper.getAddedSubscribeSite(mContext), 1);
            
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (beans != null && beans.size() > 0) {
                                MySubscribeSiteAdapter.this.appendData(beans, true, 0);
                            } else {
                                MySubscribeSiteAdapter.this.appendData(new ArrayList<SubscribeSiteBean>(), true, 0);
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
    protected void setOnScrollIdleView(final ViewHolder viewHolder, SubscribeSiteBean subscribeSiteBean, int position) {
        
    }
    
    
    @Override
    protected void setOnScrollViewContent(ViewHolder holder, SubscribeSiteBean bean, int position) {
        
        
    }
    
    @Override
    protected void setViewContent(ViewHolder viewHolder, SubscribeSiteBean bean, int position) {
        SubscribeSiteBean subscribeSiteBean = (SubscribeSiteBean) getItem(position);
        if(subscribeSiteBean != null){
        
            viewHolder.subscribe_name.setText(subscribeSiteBean.siteName);
            viewHolder.subscribe_desc.setText(subscribeSiteBean.summary);
            if(!TextUtils.isEmpty(subscribeSiteBean.updateTime)){
                viewHolder.subscribe_update_time.setText(DateUtil.parseDateTimeStringEx(subscribeSiteBean.updateTime));
            }
        }
        ImageLoader.getInstance().displayImage(bean.siteIcon, viewHolder.subscribe_img, new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(360)).build());
    
    }
    
    
    @Override
    protected void onDataItemClick(View view, int position) {
    
        try {

            SubscribeSiteBean subscribeSiteBean = (SubscribeSiteBean) getItem(position);
            Intent intent = new Intent();
            intent.setClassName(mContext, "com.nd.hilauncherdev.plugin.navigation.activity.SubscribeSiteDetailAct");
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_ICON, subscribeSiteBean.siteIcon);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_NAME, subscribeSiteBean.siteName);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_CATEGORY, subscribeSiteBean.cateName);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_COUNT, subscribeSiteBean.subCount);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_DESC, subscribeSiteBean.summary);
            intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_SITE_ID,subscribeSiteBean.siteID);
        
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    @Override
    protected void addBannerView() {
        
    }
    
    protected void setBannerContent(List<SubscribeSiteBean> list) {
        
    }
    
    
    
    public class ViewHolder {
        //订阅号图标
        public ImageView subscribe_img;
        //订阅号名字
        public TextView subscribe_name;
        //订阅号更新时间
        public TextView subscribe_update_time;
        //订阅号描述
        public TextView subscribe_desc;
    }
    
}
