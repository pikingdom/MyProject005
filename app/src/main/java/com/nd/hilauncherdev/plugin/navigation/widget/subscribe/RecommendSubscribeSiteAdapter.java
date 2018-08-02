package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.MySubscribeAct;
import com.nd.hilauncherdev.plugin.navigation.activity.SubscribeSiteDetailAct;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeSiteBean;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import java.util.ArrayList;

/**
 * Created by linliangbin on 16-7-12.
 */
public class RecommendSubscribeSiteAdapter extends BaseAdapter {
    
    private Context context;
    private ArrayList<SubscribeSiteBean> subscribeSiteBeans = new ArrayList<SubscribeSiteBean>();
    private AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    private Handler handler = new Handler();
    private ListView mListView;
    
    public RecommendSubscribeSiteAdapter(Context context,ListView listView) {
        super();
        this.context = context;
        this.mListView = listView;
        this.mListView.setAdapter(this);
        requestList();
    }
    
    
    protected View createItem() {
        View v = View.inflate(context, R.layout.navi_subscribe_site_recommend_item, null);
        return v;
    }

    
    
    protected ViewHolder initHolder(View view) {
        
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.subscribe_img = (ImageView) view.findViewById(R.id.subscribe_recommend_site_icon);
        viewHolder.subscribe_name = (TextView) view.findViewById(R.id.subscribe_recommend_site_name);
        viewHolder.subscribe = (TextView) view.findViewById(R.id.subscribe_recommend_site_subscribe);
        viewHolder.subscribe_desc = (TextView) view.findViewById(R.id.subscribe_recommend_site_desc);
        viewHolder.linearLayout = (LinearLayout) view;
        return viewHolder;
    }
    
    
    
    /**
     * 获取列表数据
     *
     * @return
     */
    private void requestList() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<SubscribeSiteBean> templist = SubscribeLoader.loadSubscribSiteById_6015(context, SubscribeHelper.getAddedSubscribeSite(context), 2);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(subscribeSiteBeans == null){
                                subscribeSiteBeans =  new ArrayList<SubscribeSiteBean>();
                            }
                            if(templist != null){
                                subscribeSiteBeans.addAll(templist);
                                notifyDataSetChanged();
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
    public int getCount() {
        return subscribeSiteBeans != null ? subscribeSiteBeans.size() : 0 ;
    }
    
    @Override
    public Object getItem(int position) {
        return (subscribeSiteBeans != null && subscribeSiteBeans.size() > position) ? subscribeSiteBeans.get(position) : null;
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder viewHolder;
        
        if(convertView == null){
            convertView = createItem();
            viewHolder = initHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        final SubscribeSiteBean subscribeSiteBean = (SubscribeSiteBean) getItem(position);
        if(subscribeSiteBean != null){
            
            viewHolder.subscribe_name.setText(subscribeSiteBean.siteName);
            viewHolder.subscribe_desc.setText(subscribeSiteBean.summary);
            ImageLoader.getInstance().displayImage(subscribeSiteBean.siteIcon, viewHolder.subscribe_img, new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(360)).build());
            if(SubscribeHelper.hasSubscribeThisSite(context,subscribeSiteBean.siteID)){
                viewHolder.subscribe.setText("取消订阅");
                viewHolder.subscribe.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.subscribe_card_has_sub_btn_sel));
            }else{
                viewHolder.subscribe.setText("订阅");
                viewHolder.subscribe.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.subscribe_card_sub_btn_sel));
            }
            viewHolder.subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SubscribeHelper.hasSubscribeThisSite(context,subscribeSiteBean.siteID)) {
                        SubscribeHelper.addSubscribeSite(context, subscribeSiteBean.siteID);
                        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN, "tj");
                    }else{
                        SubscribeHelper.removeSubscribeSite(context,subscribeSiteBean.siteID);
                    }
                    notifyDataSetChanged();
                    ((MySubscribeAct) context).updateAddOrFinishLayout();
                }
            });
            
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(context,SubscribeSiteDetailAct.class);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_ICON, subscribeSiteBean.siteIcon);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_NAME, subscribeSiteBean.siteName);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_CATEGORY, subscribeSiteBean.cateName);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_COUNT, subscribeSiteBean.subCount);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_DESC, subscribeSiteBean.summary);
                        intent.putExtra(SubscribeSiteDetailAct.INTENT_FLAG_SITE_ID,subscribeSiteBean.siteID);
        
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        
        return convertView;
        
    }

    
    public class ViewHolder {
        //订阅号图标
        public ImageView subscribe_img;
        //订阅号名字
        public TextView subscribe_name;
        //添加订阅
        public TextView subscribe;
        //订阅号描述
        public TextView subscribe_desc;
        //订阅号布局
        public LinearLayout linearLayout;
    }
    
}
