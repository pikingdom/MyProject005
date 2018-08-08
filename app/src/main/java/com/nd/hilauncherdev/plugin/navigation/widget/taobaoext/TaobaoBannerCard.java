package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.CardType;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoCard;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoData;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview.BannerView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview.HorizontalScrollViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview.MyHorizontalScrollView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview.OnBannerClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * description: banner循环卡片<br/>
 * author: yegaofei<br/>
 * date: 2017/9/8<br/>
 */
public class TaobaoBannerCard extends TaobaoCard {
    private final Context mContext;
    List<AdvertSDKManager.AdvertInfo> entities = new ArrayList<>();
    List<AdvertSDKManager.AdvertInfo> adShowEventEntities = new ArrayList<>();
    List<AdvertSDKManager.AdvertInfo> cacheEventEntities = new ArrayList<>();
    private BannerView mBannerView;
    private MyHorizontalScrollView mHorizontalScrollView;
    private HorizontalScrollViewAdapter mAdapter;
    private boolean isPageVisible = false;
    private Handler mHandler = new Handler();

    public TaobaoBannerCard(final Context context, ViewGroup parent, final CardType type) {
        super(parent);
        setType(type);
        mContext = context;
        OnBannerClickListener bannerListener = new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                if (position < entities.size() && position >= 0) {
                    AdvertSDKManager.AdvertInfo bannerItem = entities.get(position);
                    if (bannerItem instanceof AdvertSDKManager.AdvertInfo) {
                        startDetailActivity(context, bannerItem);
                    }
                }
            }

            @Override
            public void scrollToIndex(int position) {
                Log.d("banner scrollToIndex",""+position);
            }

            @Override
            public void showEntity(AdvertSDKManager.AdvertInfo entity) {
                if (entity != null) {
                    if (isPageVisible) {
                        if (!cacheEventEntities.contains(entity)) {
                            cacheEventEntities.add(entity);
                            submitEventProcess(entity);
                        }
                    }
                    else{
                        if (!adShowEventEntities.contains(entity)) {
                            adShowEventEntities.add(entity);
                        }
                    }
                }
            }
        };
        Boolean normalApi = (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD ? true : false);
        if (normalApi) {
            View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_banner, parent, false);
            setContentView(contentView);
            mBannerView = (BannerView) contentView.findViewById(R.id.banner_view);
            setSingleBannerHeight(mBannerView, type);
            mBannerView.setEntities(null);
            mBannerView.setOnBannerClickListener(bannerListener);
        } else {
            View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_scrollview, parent, false);
            setContentView(contentView);
            mHorizontalScrollView = (MyHorizontalScrollView) contentView.findViewById(R.id.id_horizontalScrollView);
            setSingleBannerHeight(mHorizontalScrollView, type);
            mHorizontalScrollView.setOnItemClickListener(bannerListener);
        }
        //默认隐藏的
        setCardVisible(false);
    }

    @Override
    public void setPageVisible(boolean visible) {
        super.setPageVisible(visible);
    }

    @Override
    public void notifyVisibleRectChanged(Rect visibleRect) {
        super.notifyVisibleRectChanged(visibleRect);
    }

    @Override
    public void update() {
        if (this.type == CardType.MULTIBANNER) {
            requestTopMultiBannerData(mContext);//banner 顶部循环广告
        } else if (type == CardType.SINGLEBANNER) {
            requestSingleBannerData(mContext);//单张广告
        }
        adShowEventEntities.clear();
        cacheEventEntities.clear();
    }

    private void setSingleBannerHeight(ViewGroup bannerView, CardType type) {
        ViewGroup.LayoutParams bannerParams = bannerView.getLayoutParams();
        int width = ScreenUtil.getScreenWidth(bannerView.getContext());
        if (type == CardType.SINGLEBANNER) {
            bannerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bannerParams.height = (int) (width * 200.f / 720.f);
            bannerView.setLayoutParams(bannerParams);
        } else if (type == CardType.MULTIBANNER) {
            bannerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bannerParams.height = (int) (width * 295.f / 720.f);
            bannerView.setLayoutParams(bannerParams);
        }
    }

    private void startDetailActivity(final Context context, AdvertSDKManager.AdvertInfo targetAd) {
        TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
//        AdvertSDKManager.submitClickEvent(mContext, mHandler, targetAd);
        CvAnalysis.submitClickEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_BANNER, targetAd.id, CvAnalysisConstant.RESTYPE_ADS);
        Intent intent = new Intent();
        intent.setClass(mContext, NewsDetailActivity.class);
        intent.putExtra("url", targetAd.actionIntent);
        intent.putExtra("need_download_analytic", true);
        SystemUtil.startActivitySafely(mContext, intent);
    }

    private void submitEventProcess(AdvertSDKManager.AdvertInfo entity){
//        AdvertSDKManager.submitShowEvent(mContext, mHandler, entity);
        //记录要展示的事件
        if (type == CardType.MULTIBANNER)
            CvAnalysis.submitShowEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_BANNER, entity.id, CvAnalysisConstant.RESTYPE_ADS);
        else
            CvAnalysis.submitShowEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_SINGLEBANNER, entity.id, CvAnalysisConstant.RESTYPE_ADS);
    }

    public void enterPageView(Boolean enter) {
        isPageVisible = enter;
        //广告cv统计未展示的时候进入页面统计
        if (isPageVisible && adShowEventEntities.size() > 0) {
            for (int ii = adShowEventEntities.size() - 1; ii >= 0; ii--) {
                AdvertSDKManager.AdvertInfo entity = adShowEventEntities.get(ii);
                submitEventProcess(entity);
                adShowEventEntities.remove(ii);
            }
        }else if(!isPageVisible){
            cacheEventEntities.clear();
        }
        //启动定时器
        startBannerTimer(enter);
    }

    public void startBannerTimer(boolean start){
        final int timerInterval = 3;
        if (this.type == CardType.MULTIBANNER) {
            if (mBannerView != null) {
                if (start&&entities!=null&&entities.size()>1){
                    TaobaoData.getInstance().startTopBannerTimer(new CountDownTimer(Integer.MAX_VALUE, timerInterval * 1000) {
                        @Override
                        public void onTick(long l) {
                            mBannerView.updateContent();
                        }
                        @Override
                        public void onFinish() {
                            mBannerView.setAutoScroll(timerInterval);
                        }
                    });
                }
                else
                    TaobaoData.getInstance().cancelTopBannerTimer();
            } else if (mHorizontalScrollView != null) {
                if (start&&mAdapter!=null&&mAdapter.getAdapterData()!=null&&mAdapter.getAdapterData().size()>1) {
                    mHorizontalScrollView.updateContent();
                    TaobaoData.getInstance().startTopBannerTimer(new CountDownTimer(Integer.MAX_VALUE, timerInterval * 1000) {
                        @Override
                        public void onTick(long l) {
                            mHorizontalScrollView.updateContent();
                        }
                        @Override
                        public void onFinish() {
                            mBannerView.setAutoScroll(timerInterval);
                        }
                    });
                }
                else
                    TaobaoData.getInstance().cancelTopBannerTimer();
            }
        }else if(this.type == CardType.SINGLEBANNER){
            if (mBannerView != null) {
                if (start&&entities!=null&&entities.size()>1) {
                    TaobaoData.getInstance().startBottomBannerTimer(new CountDownTimer(Integer.MAX_VALUE, timerInterval * 1000) {
                        @Override
                        public void onTick(long l) {
                            mBannerView.updateContent();
                        }
                        @Override
                        public void onFinish() {
                            mBannerView.setAutoScroll(timerInterval);
                        }
                    });
                }
                else
                    TaobaoData.getInstance().cancelBottomBannerTimer();
            } else if (mHorizontalScrollView != null) {
                if (start&&mAdapter!=null&&mAdapter.getAdapterData()!=null&&mAdapter.getAdapterData().size()>1) {
                    mHorizontalScrollView.updateContent();
                    TaobaoData.getInstance().startBottomBannerTimer(new CountDownTimer(Integer.MAX_VALUE, timerInterval * 1000) {
                        @Override
                        public void onTick(long l) {
                            mHorizontalScrollView.updateContent();
                        }
                        @Override
                        public void onFinish() {
                            mBannerView.setAutoScroll(timerInterval);
                        }
                    });
                }
                else
                    TaobaoData.getInstance().cancelBottomBannerTimer();
            }
        }
    }

    protected void setBannerDatas(final Context mContext, List<AdvertSDKManager.AdvertInfo> adList) {
        entities.clear();
        entities.addAll(adList);
        if (mBannerView != null) {
            mBannerView.setEntities(entities);
            startBannerTimer(true);
        }
        if (mHorizontalScrollView != null) {
            mAdapter = new HorizontalScrollViewAdapter(mContext, entities);
            mHorizontalScrollView.initDatas(mAdapter);
            startBannerTimer(true);

        }
    }

    /**
     * 加载顶部循环广告
     */
    public void requestTopMultiBannerData(final Context mContext) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
//                final List<AdvertSDKManager.AdvertInfo> ads = AdvertSDKManager.getAdvertInfos(mContext, "58");
                final List<AdvertSDKManager.AdvertInfo> ads = new ArrayList<AdvertSDKManager.AdvertInfo>();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ads != null && ads.size() > 0) {
                            setBannerDatas(mContext, ads);
                            setCardVisible(true);
                        } else {
                            setCardVisible(false);
                        }
                    }
                });
            }
        });
    }

    /**
     * 加载底部单张广告
     */
    public void requestSingleBannerData(final Context mContext) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
//                final List<AdvertSDKManager.AdvertInfo> ads = AdvertSDKManager.getAdvertInfos(mContext, "57");
                final List<AdvertSDKManager.AdvertInfo> ads = new ArrayList<AdvertSDKManager.AdvertInfo>();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ads != null && ads.size() > 0) {
                            ArrayList<AdvertSDKManager.AdvertInfo> bannerList = new ArrayList<AdvertSDKManager.AdvertInfo>();
                            if (ads.size()>0){
                                bannerList.add(ads.get(0));
                            }
                            setBannerDatas(mContext, bannerList);
                            setCardVisible(true);
                        } else {
                            setCardVisible(false);
                        }
                    }
                });
            }
        });
    }

}
