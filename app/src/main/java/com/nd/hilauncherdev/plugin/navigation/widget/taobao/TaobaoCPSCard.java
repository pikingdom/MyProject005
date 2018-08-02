package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoCps;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: 淘宝-CPS<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/15<br/>
 */
public class TaobaoCPSCard extends TaobaoCard implements View.OnClickListener {

    ViewHolder holderOne = new ViewHolder();
    ViewHolder holderTwo = new ViewHolder();
    ViewHolder holderThree = new ViewHolder();

    private Context mContext;

    private SharedPreferencesUtil spUtil;
    private AsyncImageLoader imageLoader;
    private SimpleDateFormat dateFormat;
    private Handler handler = new Handler();
    private List<TaobaoCps> mData = new ArrayList<TaobaoCps>();
    private boolean hasSubmited = false;//曝光上报

    public TaobaoCPSCard(Context context, ViewGroup parent) {
        super(parent);
        setType(CardType.CPS);

        mContext = context;
        spUtil = new SharedPreferencesUtil(context);
        imageLoader = new AsyncImageLoader();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_cps, parent, false);
        setContentView(contentView);

        View one = contentView.findViewById(R.id.layout_taobao_cps_one);
        holderOne.title = (TextView) contentView.findViewById(R.id.tv_taobao_cps_one_title);
        holderOne.subTitle = (TextView) contentView.findViewById(R.id.tv_taobao_cps_one_subtitle);
        holderOne.icon = (ImageView) contentView.findViewById(R.id.iv_taobao_cps_one_icon);

        View two = contentView.findViewById(R.id.layout_taobao_cps_two);
        holderTwo.title = (TextView) contentView.findViewById(R.id.tv_taobao_cps_two_title);
        holderTwo.subTitle = (TextView) contentView.findViewById(R.id.tv_taobao_cps_two_subtitle);
        holderTwo.icon = (ImageView) contentView.findViewById(R.id.iv_taobao_cps_two_icon);

        View three = contentView.findViewById(R.id.layout_taobao_cps_three);
        holderThree.title = (TextView) contentView.findViewById(R.id.tv_taobao_cps_three_title);
        holderThree.subTitle = (TextView) contentView.findViewById(R.id.tv_taobao_cps_three_subtitle);
        holderThree.icon = (ImageView) contentView.findViewById(R.id.iv_taobao_cps_three_icon);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        //默认隐藏的
        setCardVisible(false);
    }

    @Override
    public void onClick(View v) {
        String url = null;
        switch (v.getId()) {
            case R.id.layout_taobao_cps_one:
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_CPS_ONE, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                url = holderOne.url;
                break;
            case R.id.layout_taobao_cps_two:
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_CPS_TWO, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                url = holderTwo.url;
                break;
            case R.id.layout_taobao_cps_three:
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_CPS_THREE, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                url = holderThree.url;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }
        TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
        Intent intent = new Intent();
        intent.setClass(v.getContext(), NewsDetailActivity.class);
        intent.putExtra("url", url);
        SystemUtil.startActivitySafely(v.getContext(), intent);
    }

    private static class ViewHolder {
        TextView title;
        TextView subTitle;
        ImageView icon;
        int id;
        String url = "http://www.baidu.com/";
    }

    private void assemble(final TaobaoCps data, final ViewHolder holder) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                holder.title.setText("" + data.title);
                holder.subTitle.setText("" + data.subTitle);
                holder.id = data.id;
                holder.url = data.linkUrl;
                holder.icon.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
                Drawable drawable = imageLoader.loadDrawable(data.picUrl, new ImageCallback() {
                    @Override
                    public void imageLoaded(Drawable drawable, String s, Map map) {
                        if (drawable != null) {
                            holder.icon.setImageDrawable(drawable);
                        }
                    }
                });

                if (drawable != null) {
                    holder.icon.setImageDrawable(drawable);
                }
            }
        });
    }

    private void loadData() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    List<TaobaoCps> list = TaobaoLoader.getTaobaoCps(mContext);
                    if (list == null || (list != null && list.size()<=0)) {
                        mData.clear();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setCardVisible(false);
                            }
                        });
                        return;
                    }else {
                        hasSubmited = true;
                        filterCps(list);
                    }
                    if (mData.size() < 3) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setCardVisible(false);
                            }
                        });
                        return;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setCardVisible(true);
                        }
                    });
                    for (int i = 0, len = mData.size(); i < len; i++) {
                        switch (i) {
                            case 0:
                                assemble(mData.get(0), holderOne);
                                break;
                            case 1:
                                assemble(mData.get(1), holderTwo);
                                break;
                            case 2:
                                assemble(mData.get(2), holderThree);
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setCardVisible(false);
                        }
                    });
                }
            }
        });
    }

    private void handleSubmit(){
        if(getView().getVisibility() == View.VISIBLE && isPageVisible() && !hasSubmited){
            for (TaobaoCps item: mData) {
                if(item.callBackFlag == 1) {
                    AdvertSDKController.submitECShowURL(mContext, 2, item.adSourceId);
                    hasSubmited = true;
                    break;
                }
            }
        }
    }

    private void filterCps(List<TaobaoCps> list) {
        if (list == null || list.isEmpty()) return;
        mData.clear();
        for (TaobaoCps item : list) {
            if (item == null) return;
            long currentTime = System.currentTimeMillis();
            if (item.startTime <= 0 && item.endTime <= 0) {//起始时间、结束时间都没有配，那么OK
                mData.add(item);
            } else if (item.startTime > 0 && item.endTime > 0) {//起始时间、结束时间都有配，那么 当前时间 >= 起始时间 && 当前时间 <= 结束时间, 那么OK
                if (currentTime >= item.startTime && currentTime <= item.endTime) mData.add(item);
            } else if (item.startTime > 0 && currentTime >= item.startTime) {//只配了起始时间，当前时间 >= 起始时间，那么OK
                mData.add(item);
            } else if (item.endTime > 0 && currentTime <= item.endTime) {//只配了结束时间，当前时间 <= 结束时间，那么OK
                mData.add(item);
            }
        }
    }

    @Override
    public void setPageVisible(boolean visible) {
        super.setPageVisible(visible);
        if(!visible){
            hasSubmited = false;
        }else notifyVisibleRectChanged(visibleRect);
    }

    @Override
    public void notifyVisibleRectChanged(Rect visibleRect) {
        super.notifyVisibleRectChanged(visibleRect);
        if(visibleRect != null){
            if(visibleRect.top >= 0){
                handleSubmit();
            }
        }
    }

    @Override
    public void update() {
        loadData();
    }
}
