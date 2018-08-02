package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.FunnyBean;
import com.nd.hilauncherdev.plugin.navigation.bean.News;
import com.nd.hilauncherdev.plugin.navigation.loader.FunnyLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.ZeroNewsLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.UriParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linliangbin on 2016/4/29.
 */
public class FunnyListAdapter extends AbsListViewAdapter<FunnyBean,FunnyListAdapter.ViewHolder>{


    public FunnyListAdapter(Context context) {
        super(context);
    }

    public FunnyListAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
        //趣发现卡片，不需要展示头部，不需要移除第一个item
        removeFirstForBanner = false;
    }

    /**
     * 下一次请求的页码
     */
    private int mNextPindx = 1;

    Handler mHandle = new Handler();


    protected void setViewContent(ViewHolder holder, News bean, int position) {

    }


    @Override
    protected void setOnScrollIdleView(final ViewHolder holder, FunnyBean bean, int position) {
        holder.funny_title.setText(bean.Title);
        holder.funny_desc.setText(bean.Summary);
        asyncImageLoader.showDrawable(bean.ImgUrl, holder.funny_img,
                ImageView.ScaleType.CENTER_CROP, CommonLauncherControl.getVph());
    }

    @Override
    protected void setOnScrollViewContent(ViewHolder holder, FunnyBean bean, int position) {

        holder.funny_title.setText(bean.Title);
        holder.funny_desc.setText(bean.Summary);
        Drawable drawable = ImageLoader.getInstance().loadDrawableIfExistInMemory(bean.ImgUrl);

        if(drawable != null){
            holder.funny_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.funny_img.setImageDrawable(drawable);
        }else {
            try {
                holder.funny_img.setScaleType(ImageView.ScaleType.FIT_XY);
            }catch (Throwable t){
        
            }
            holder.funny_img.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
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
        mNextPindx = 1;
        doRequest("");
    }

    @Override
    protected void onDataItemClick(View view, int position) {

        FunnyBean funnyBean =getItem(position);
        FunnyCardViewHelper.startFunnyDetail(mContext, funnyBean);
        PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "zxlb");
        PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_DETAIL, funnyBean.TopicId+"");
    }

    @Override
    protected void setViewContent(ViewHolder holder, FunnyBean bean, int position) {

    }


    @Override
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.navi_funny_card_item_in_list, null);
        return v;
    }

    @Override
    protected ViewHolder initHolder(View view) {

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.funny_img = (ImageView) view.findViewById(R.id.funny_small_icon);
        viewHolder.funny_title = (TextView) view.findViewById(R.id.funny_small_title);
        viewHolder.funny_desc = (TextView) view.findViewById(R.id.funny_small_desc);
        return viewHolder;

    }


    @Override
    protected void doRequest(String url) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {

                try {
                    final int lastPi = mNextPindx;
                    final ArrayList<FunnyBean> beans = requestList();
                    if(beans != null && beans.size() > 0)
                        mNextPindx ++;

                    mHandle.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean isLast = false;
                            if(beans != null && beans.size() == 0 && lastPi != 1){
                                isLast = true;
                            }
                            if(beans == null){
                                FunnyListAdapter.this.appendData(new ArrayList<FunnyBean>(), false,0);
                            }else{
                                FunnyListAdapter.this.appendData(beans, isLast,0);
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

    protected void setBannerContent(List<FunnyBean> list) {

    }




    /**
     * 获取列表数据
     * @return
     */
    private ArrayList<FunnyBean> requestList(){

        ArrayList<FunnyBean> beans = null;

        try {
            String resString = FunnyLoader.requestData(mContext, 0,0,mNextPindx, FunnyLoader.REQUEST_TYPE_MIX);
            if (TextUtils.isEmpty(resString))
                return beans;
            JSONObject resJo = new JSONObject(resString);
            JSONArray jabig = resJo.getJSONArray("MixTopicList");
            beans = new ArrayList<FunnyBean>();
            for (int i = 0; i < jabig.length(); i++) {
                JSONObject jsonObject = (JSONObject) jabig.get(i);
                FunnyBean funnyBean = new FunnyBean();
                funnyBean.initDataFromJson(jsonObject);
                beans.add(funnyBean);
            }
            return beans;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beans;

    }
    public class ViewHolder {
        //趣发现item 图片
        public ImageView funny_img;
        //趣发现item 标题
        public TextView funny_title;
        //趣发现item 描述
        public TextView funny_desc;
    }

}
