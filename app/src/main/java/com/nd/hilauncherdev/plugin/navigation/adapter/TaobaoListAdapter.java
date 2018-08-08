package com.nd.hilauncherdev.plugin.navigation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.db.TaobaoProductHelper;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.SquareImageView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.CardType;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoCardWrapper;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * description: <br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/28<br/>
 */
@Deprecated
public class TaobaoListAdapter extends AbsListViewAdapter<List<TaobaoProduct>, TaobaoListAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "TaobaoListAdapter";

    private static final int PAGE_SIZE = 14;
    private static final int INIT_AD_INDEX = -1;

    private ImageView mBannerImage;
    private TextView mBannerTitle;
    private View mSearchCard;
    private List<AdvertSDKManager.AdvertInfo> adInfos = new ArrayList<AdvertSDKManager.AdvertInfo>();
    private int lastAdIndex = INIT_AD_INDEX;
    private AdvertSDKManager.AdvertInfo targetAd;
    private Random random = new Random();
    private List<TaobaoProduct> mData = new ArrayList<TaobaoProduct>();

    private AsyncImageLoader mImageLoader;
    private boolean isFirstLoad = true;
    private boolean isRefresh = true;
    private TaobaoProductHelper dbHelper;
    private boolean isPageVisible = false;
    private Rect visibleRect = new Rect();
    private LinearLayout bannerLayout;
    private Object lock = new Object();
    private Handler mHandler = new Handler();

    /**
     * 头部视图
     */
    private TaobaoCardWrapper headerWrapper;

    /**
     * 下一次请求的时间 戳
     */
    private long mNextNonce = -1;
    /**
     * 下一次请求的页码
     */
    private int mNextPindx = -1;

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

    private int miniPage = 0;

    public TaobaoListAdapter(Context context) {
        super(context);
    }

    public TaobaoListAdapter(Context context, ListView listView, String url, TaobaoCardWrapper taobaoCardWrapper) {
        super(context, listView, url);
        if (listView instanceof PullToRefreshListView) {
            ((PullToRefreshListView) listView).setPullRateListent(listen);
        }
        headerWrapper = taobaoCardWrapper;
        dbHelper = new TaobaoProductHelper(context);
        mImageLoader = new AsyncImageLoader();
    }

    @Override
    protected void setViewContent(ViewHolder holder, List<TaobaoProduct> bean, int position) {
    }

    @Override
    protected View createItem() {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_taobao_product_2col, null);
        return convertView;
    }

    @Override
    protected ViewHolder initHolder(View view) {
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    protected void setOnScrollIdleView(ViewHolder holder, List<TaobaoProduct> bean, int position) {
        if (bean == null || bean.size() < 1 || bean.size() > 2) {
            return;
        }
        for (TaobaoProduct product : bean) {
            setContent(product, holder, true);
        }
    }

    @Override
    protected void setOnScrollViewContent(ViewHolder holder, List<TaobaoProduct> bean, int position) {
        if (bean == null || bean.size() < 1 || bean.size() > 2) {
            return;
        }
        for (TaobaoProduct product : bean) {
            setContent(product, holder, false);
        }

        onDataSetChanged(position);
    }

    private void setContent(TaobaoProduct bean, ViewHolder holder, boolean isIdle) {
        if (bean == null) {
            return;
        }
        switch (bean.view_type) {
            case TaobaoProduct.ITEM_LEFT:
                holder.left.setTag(bean);
                holder.ivGoodsIcon_1.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
                holder.tvGoodsDesc_1.setText(bean.title);
                holder.ivGoodsIcon_1.setTag(bean.imageUrl);
                Drawable drawable = null;
                if (isIdle) {
                    drawable = mImageLoader.loadDrawable(bean.imageUrl, new ImageCallback() {
                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                            if (imageDrawable != null) {
                                View view = mListView.findViewWithTag(imageUrl);
                                if (view instanceof ImageView) {
                                    ImageView imageView = (ImageView) view;
                                    imageView.setImageDrawable(imageDrawable);
                                }
                            }
                        }
                    });
                } else {
                    drawable = mImageLoader.loadDrawableOnlyCache(bean.imageUrl);
                }

                if (drawable != null) {
                    holder.ivGoodsIcon_1.setImageDrawable(drawable);
                }
                holder.tvGoodsPrice_1.setText(bean.getProductsShowPrice());
                break;
            case TaobaoProduct.ITEM_RIGHT:
                holder.right.setTag(bean);
                holder.ivGoodsIcon_2.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
                holder.tvGoodsDesc_2.setText(bean.title);
                holder.ivGoodsIcon_2.setTag(bean.imageUrl);
                if (isIdle) {
                    drawable = mImageLoader.loadDrawable(bean.imageUrl, new ImageCallback() {

                        @Override
                        public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                            if (imageDrawable != null) {
                                View view = mListView.findViewWithTag(imageUrl);
                                if (view instanceof ImageView) {
                                    ImageView imageView = (ImageView) view;
                                    imageView.setImageDrawable(imageDrawable);
                                }
                            }
                        }
                    });
                } else {
                    drawable = mImageLoader.loadDrawableOnlyCache(bean.imageUrl);
                }

                if (drawable != null) {
                    holder.ivGoodsIcon_2.setImageDrawable(drawable);
                }
                holder.tvGoodsPrice_2.setText(bean.getProductsShowPrice());
                break;
        }
    }

    @Override
    protected void doAddRequest() {
        isRefresh = false;
        doRequest("");
    }

    @Override
    protected void doRefreshRequest() {
        isRefresh = true;
        isLastPage = false;
        doRequest("");
        if(!isFirstLoad){
            loadBanner();
        }
    }

    @Override
    protected void doRequest(String url) {
        if (isRefresh) {
//            if (!calBanner()) {
//                loadBanner();
//            }
            // loadBanner();
            loadHeader();
        }
        loadMore(null);
    }

    public void loadMore(final IAdapter.CallBack callBack) {
        if (isLastPage) {
            appendData(new ArrayList<List<TaobaoProduct>>(), isLastPage, 0);
            if (callBack != null) {
                callBack.onSuccess(true, 0);
                callBack.onFinish();
            }
            return;
        }

        loadData(new ILoadDataCallBack<List<TaobaoProduct>>() {
            @Override
            public void onFinish() {
                if (callBack != null) {
                    callBack.onFinish();
                }
            }

            @Override
            public void onSuccess(List<TaobaoProduct> data, boolean isLastPage) {
                int size = 0;
                if (data != null && data.size() > 0) {
                    size = data.size();
                    notifyDataSetChanged();
                } else {
                    TaobaoListAdapter.this.isLastPage = true;
                }
                if (callBack != null) {
                    callBack.onSuccess(TaobaoListAdapter.this.isLastPage, size);
                }
                isFirstLoad = false;
            }

            @Override
            public void onFailure() {
                appendData(new ArrayList<List<TaobaoProduct>>(), isLastPage, 0);
                if (callBack != null) {
                    callBack.onError(false);
                }
            }
        });
    }

    private void loadData(final ILoadDataCallBack<List<TaobaoProduct>> callBack) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<TaobaoProduct> list = TaobaoLoader.getNetTaobaoProductsList(mContext, PAGE_SIZE);
                if (list == null || list.size() < 1) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isAvailable() && callBack != null) {
                                if (isFirstLoad && mBeanList.isEmpty()) loadSomeCache();
                                callBack.onFailure();
                                callBack.onFinish();
                            }
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isRefresh) isLastPage = false;
                            if (isAvailable() && callBack != null) {
                                distinct(list);
                                callBack.onSuccess(list, isLastPage);
                                callBack.onFinish();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isAvailable() {
        return lock != null;
    }

    private void loadSomeCache() {
        List<TaobaoProduct> list = dbHelper.select(0, PAGE_SIZE, null, true);
        distinct(list);
    }

    /**
     * 去重
     */
    private void distinct(List<TaobaoProduct> data) {
        if (data == null || data.size() < 1) {
            appendData(new ArrayList<List<TaobaoProduct>>(), isLastPage, 0);
            return;
        }
        //数据请求成功，此时可以清除以往数据，装填新数据
        if (isRefresh) {
            mData.clear();
            mBeanList.clear();
        }

        List<TaobaoProduct> tmpList = new ArrayList<TaobaoProduct>();
        if (mData.size() > 0) {
            for (TaobaoProduct item : data) {
                if (mData.contains(item)) {
                    continue;
                } else {
                    tmpList.add(item);
                }
            }
        } else {
            tmpList.addAll(data);
        }

        int reminder = PAGE_SIZE - tmpList.size();
        mData.addAll(tmpList);
        //不足一页大小时，去数据库取
        if (reminder > 0) {
            List<TaobaoProduct> dbRes = dbHelper.select(0, reminder, mData, true);
            if (dbRes == null || dbRes.size() < reminder) {
                isLastPage = true;
            }
            tmpList.addAll(dbRes);
            mData.addAll(dbRes);
        }
        calViewType(tmpList);
    }

    /**
     * 计算左、右数据
     */
    private void calViewType(List<TaobaoProduct> list) {
        List<List<TaobaoProduct>> data = new ArrayList<List<TaobaoProduct>>();
        if (list != null && list.size() > 0) {
            for (int j = 0; j < (list.size() + 1) / 2; j++) {
                List<TaobaoProduct> set = new ArrayList<TaobaoProduct>();
                for (int i = 0; i < 2; i++) {
                    int index = j * 2 + i;
                    if (index >= list.size()) {
                        break;
                    }
                    TaobaoProduct product = list.get(index);
                    if (i % 2 == 0) {
                        product.view_type = TaobaoProduct.ITEM_LEFT;
                    } else {
                        product.view_type = TaobaoProduct.ITEM_RIGHT;
                    }
                    set.add(product);
                }
                data.add(set);
            }
        }
        appendData(data, isLastPage, 0);
    }

    /**
     * 待轮播banner提取
     */
    private boolean calBanner() {
        int size = 0;
        //本地有，先加载本地数据
        if (adInfos != null && (size = adInfos.size()) > 0 && lastAdIndex < size) {
            if (size == 1) {
                targetAd = adInfos.get(0);
                lastAdIndex = 0;
            } else {
                if (++lastAdIndex >= size) {
                    lastAdIndex = INIT_AD_INDEX;
                    return false;
                } else {
                    targetAd = adInfos.get(lastAdIndex);
                }
            }
            return true;
        } else {//如果当前的banner队列为空时，此时去请求新的banner
            lastAdIndex = INIT_AD_INDEX;
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_item_left:
                Object left = v.getTag();
                if (left instanceof TaobaoProduct) {
                    TaobaoProduct productLeft = (TaobaoProduct) left;
                    CvAnalysis.submitClickEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_PRODUCT, CvAnalysisConstant.TAOBAO_SCREEN_RESID, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClass(mContext, NewsDetailActivity.class);
                    intent.putExtra("url", productLeft.urlClick);
                    SystemUtil.startActivitySafely(mContext, intent);
                }
                break;
            case R.id.layout_item_right:
                Object right = v.getTag();
                if (right instanceof TaobaoProduct) {
                    TaobaoProduct productRight = (TaobaoProduct) right;
                    CvAnalysis.submitClickEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_PRODUCT, CvAnalysisConstant.TAOBAO_SCREEN_RESID, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClass(mContext, NewsDetailActivity.class);
                    intent.putExtra("url", productRight.urlClick);
                    SystemUtil.startActivitySafely(mContext, intent);
                }
                break;
        }
    }


    class ViewHolder {
        View left;
        ImageView ivGoodsIcon_1;
        TextView tvGoodsDesc_1;
        TextView tvGoodsPrice_1;

        View right;
        ImageView ivGoodsIcon_2;
        TextView tvGoodsDesc_2;
        TextView tvGoodsPrice_2;

        public ViewHolder(View view) {
            left = view.findViewById(R.id.layout_item_left);
            ivGoodsIcon_1 = (SquareImageView) view.findViewById(R.id.iv_goods_icon_1);
            tvGoodsDesc_1 = (TextView) view.findViewById(R.id.tv_goods_name_1);
            tvGoodsPrice_1 = (TextView) view.findViewById(R.id.tv_goods_price_1);

            right = view.findViewById(R.id.layout_item_right);
            ivGoodsIcon_2 = (SquareImageView) view.findViewById(R.id.iv_goods_icon_2);
            tvGoodsDesc_2 = (TextView) view.findViewById(R.id.tv_goods_name_2);
            tvGoodsPrice_2 = (TextView) view.findViewById(R.id.tv_goods_price_2);

            left.setOnClickListener(TaobaoListAdapter.this);
            right.setOnClickListener(TaobaoListAdapter.this);
        }
    }

    @Override
    protected void onDataItemClick(View view, int position) {

    }

    /**
     * 加载淘宝头部数据
     */
    private void loadHeader() {
        if (headerWrapper == null) return;
        headerWrapper.refreshView();
    }

    /**
     * 加载广告
     */
    public void loadBanner() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
//                final List<AdvertSDKManager.AdvertInfo> ads = AdvertSDKManager.getAdvertInfos(mContext, "27");
                final List<AdvertSDKManager.AdvertInfo> ads = new ArrayList<AdvertSDKManager.AdvertInfo>();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isAvailable() && ads != null && ads.size() > 0) {
                            adInfos.clear();
                            adInfos.addAll(ads);
                            targetAd = adInfos.get(0);
//                            lastAdIndex = INIT_AD_INDEX;
//                            calBanner();
                            setBannerContent(null);
                        }
                    }
                });
            }
        });

        CardType type = CardType.getCardTypeByFlag(4);
        if (type != null) {
            if (type.isVisible()) {
                mSearchCard.setVisibility(View.VISIBLE);
            } else {
                mSearchCard.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected void addBannerView() {
        if (mListView instanceof PullToRefreshListView) {
            PullToRefreshListView ptrListView = (PullToRefreshListView) mListView;
            bannerLayout = ptrListView.getBannerContainer();
            bannerLayout.setPadding(0, 0, 0, ScreenUtil.dip2px(mContext, 4));
            bannerLayout.getLocalVisibleRect(visibleRect);

            //添加顶部广告
            View banner = mInflater.inflate(R.layout.sub_taobao_banner_header, null);
            mBannerImage = (ImageView) banner.findViewById(R.id.iv_shopping_banner);
            mBannerTitle = (TextView) banner.findViewById(R.id.tv_shopping_banner_desc);
            mSearchCard = banner.findViewById(R.id.layout_taobao_search);
            bannerLayout.addView(banner);

            banner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (targetAd != null) {
//                        AdvertSDKManager.submitClickEvent(mContext, mHandler, targetAd);
                        CvAnalysis.submitClickEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_BANNER, targetAd.id, CvAnalysisConstant.RESTYPE_ADS);
                        Intent intent = new Intent();
                        intent.setClass(mContext, NewsDetailActivity.class);
                        intent.putExtra("url", targetAd.actionIntent);
                        intent.putExtra("need_download_analytic", true);
                        SystemUtil.startActivitySafely(mContext, intent);
                    }
                }
            });

            mSearchCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_SEARCH, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClassName(Global.getPackageName(), "com.nd.hilauncherdev.widget.taobao.TaobaoWidgetActivity");
                    intent.putExtra("show_type", 2);
                    SystemUtil.startActivitySafely(mContext, intent);
                }
            });
        }
    }

    private PullToRefreshListView.OnPullRateListen listen = new PullToRefreshListView.OnPullRateListen() {
        @Override
        public void OnPullRate(float rate) {

            if (mBannerImage != null) {
                calculateMatrix(mBannerImage, mBannerImage.getDrawable(), rate);
                mBannerImage.setImageMatrix(bannerMatrix);
            }
        }
    };

    @Override
    protected void setBannerContent(List<List<TaobaoProduct>> list) {
        if (targetAd != null) {
//            if (TextUtils.isEmpty(targetAd.name)) {
//                mBannerTitle.setVisibility(View.GONE);
//            } else {
//                mBannerTitle.setVisibility(View.VISIBLE);
//                mBannerTitle.setText(targetAd.name);
//            }

            if (TextUtils.isEmpty(targetAd.picUrl)) {
                mBannerImage.setImageResource(R.drawable.taobao_banner_background);
            } else {
                mBannerImage.setTag(targetAd.picUrl);
                Drawable drawable = mImageLoader.loadDrawable(targetAd.picUrl, new ImageCallback() {
                    @Override
                    public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                        if (imageDrawable != null && targetAd.picUrl.equals(imageUrl)) {
                            if (isPageVisible) {
                                Log.d(TAG, "banner setBannerContent : visible : true");
//                                AdvertSDKManager.submitShowEvent(mContext, mHandler, targetAd);
                            }
                            CvAnalysis.submitShowEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_BANNER, targetAd.id, CvAnalysisConstant.RESTYPE_ADS);
                            calculateMatrix(mBannerImage, imageDrawable, 0);
                            mBannerImage.setImageMatrix(bannerMatrix);
                            mBannerImage.setImageDrawable(imageDrawable);
                            mBannerImage.invalidate();
                        }
                    }
                });
                if (drawable == null) {
                    mBannerImage.setImageResource(R.drawable.taobao_banner_background);
                } else {
                    if (isPageVisible) {
                        Log.d(TAG, "banner setBannerContent : visible : true");
//                        AdvertSDKManager.submitShowEvent(mContext, mHandler, targetAd);
                    }
                    CvAnalysis.submitShowEvent(mContext.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_BANNER, targetAd.id, CvAnalysisConstant.RESTYPE_ADS);
                    calculateMatrix(mBannerImage, drawable, 0);
                    mBannerImage.setImageMatrix(bannerMatrix);
                    mBannerImage.setImageDrawable(drawable);
                }
            }
        } else {
            mBannerImage.setImageResource(R.drawable.taobao_banner_background);
            //mBannerTitle.setVisibility(View.GONE);
        }
    }

    //baner图片要实现放大缩小效果
    private Matrix bannerMatrix = new Matrix();

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

    private void onDataSetChanged(int position) {
        int page = (position + 6) / 6;
        if (miniPage >= page) {
            return;
        }
        miniPage = page;
        PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_SHOPPING_CARD_SHOW_PAGE, "" + (miniPage > 10 ? "10+" : miniPage));
    }

    public void onDestroy() {
        lock = null;
    }

    public void notifyPageChanged(boolean visible) {
        isPageVisible = visible;
        if (!visible) {
        } else {
            if (targetAd != null) {
                Log.d(TAG, "banner notifyPageChanged : visible : " + visible);
//                AdvertSDKManager.submitShowEvent(mContext, mHandler, targetAd);
            } else {
                loadBanner();
            }
        }
    }

    public void notifyVisibleRectChanged(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0 && bannerLayout != null) {
            bannerLayout.getLocalVisibleRect(visibleRect);
            if (visibleRect != null && isPageVisible) {
                if (visibleRect.left >= 0 && visibleRect.top > 0) {
                    if (targetAd != null) {
                        //Log.d(TAG, "banner notifyVisibleRectChanged : visible : true");
//                        AdvertSDKManager.submitShowEvent(mContext, mHandler, targetAd);
                    }
                }
            }
        }
    }
}
