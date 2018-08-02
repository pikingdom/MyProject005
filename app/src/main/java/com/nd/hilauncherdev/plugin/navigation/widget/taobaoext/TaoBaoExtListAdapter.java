package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.activity.TaoBaoCollectionDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.db.TaobaoProductHelper;
import com.nd.hilauncherdev.plugin.navigation.loader.PageDataManager;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoCardWrapper;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoData;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct.TAO_BAO_RES_TYPE_COLLECTION;
import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_SCREEN_PAGEID;
import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_SCREEN_POS_PRODUCT;

/**
 * Created by linxiaobin on 2017/9/7.
 * 淘宝扩展页-数据Adapter
 */

class TaoBaoExtListAdapter extends AbsListViewAdapter<TaobaoProduct, TaoBaoExtListAdapter.ViewHolder> implements View.OnClickListener, PageDataManager.DataManagerListener<TaobaoProduct> {
    private static final int PAGE_SIZE = 14;

    /**
     * 头部视图
     */
    private TaobaoCardWrapper headerWrapper;

    private AsyncImageLoader mImageLoader = new AsyncImageLoader();
    private PageDataManager<TaobaoProduct> dataManager;
    private TaobaoProductHelper dbHelper;

    private Object lock = new Object();
    private boolean isCache = true;

    TaoBaoExtListAdapter(Context context, ListView listView, TaobaoCardWrapper taobaoCardWrapper) {
        super(context, listView, "");
        headerWrapper = taobaoCardWrapper;
        dbHelper = new TaobaoProductHelper(context);

        removeFirstForBanner = false;

        createDataManager();
        loadSomeCache();

        changeRequestStatus(STATUS_LOADING);
    }

    public boolean isCache() {
        return isCache;
    }

    @Override
    protected void setViewContent(ViewHolder holder, TaobaoProduct bean, int position) {

    }

    @Override
    protected View createItem() {
        return View.inflate(mContext, R.layout.taobao_ext_list_item, null);
    }

    @Override
    protected ViewHolder initHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void setOnScrollIdleView(ViewHolder holder, TaobaoProduct bean, int position) {
        setContent(bean, holder, true);
    }

    @Override
    protected void setOnScrollViewContent(ViewHolder holder, TaobaoProduct bean, int position) {
        setContent(bean, holder, false);
    }

    private static int[] ACTIVITY_IMG = {0, R.drawable.tb_activity_xian_shi_te_hui, R.drawable.tb_activity_zui_re_hao_huo};

    private void setContent(TaobaoProduct bean, ViewHolder holder, boolean isIdle) {
        if (bean == null) {
            return;
        }

        holder.container.setTag(bean);
        String productCount = bean.getProductCount();
        holder.tvProductCount.setText(productCount);
        holder.tvProductCount.setVisibility(TextUtils.isEmpty(productCount) ? View.GONE : View.VISIBLE);

        boolean isToday = bean.isToday();
        holder.tvTitle.setText(isToday ? "\u3000\u3000\u0020" + bean.title : bean.title);
        holder.tvToday.setVisibility(isToday ? View.VISIBLE : View.GONE);
        holder.tvDiscountDesc.setText(bean.discountDes);
        holder.tvDiscountDesc.setVisibility(TextUtils.isEmpty(bean.discountDes) ? View.GONE : View.VISIBLE);
        holder.tvPromoPrice.setText(bean.getProductsShowPrice());
        holder.tvPrice.setText(bean.getProductsPrice());
        holder.tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        // 活动类型，已购人数
        holder.tvActivityDesc.setText(bean.activityDesc);
        holder.tvActivityDesc.setVisibility(TextUtils.isEmpty(bean.activityDesc) ? View.GONE : View.VISIBLE);
        int activity = ACTIVITY_IMG[bean.activityType];
        holder.tvActivityDesc.setBackgroundResource(activity);
        if (activity == 0) {
            holder.tvActivityDesc.setPadding(0, 0, ScreenUtil.dip2px(mContext, 4), ScreenUtil.dip2px(mContext, 4));
        }
        holder.tvActivityDesc.setTextColor(activity > 0 ? mContext.getResources().getColor(R.color.tao_bao_primary) : mContext.getResources().getColor(R.color.tb_activity_desc_color_gray));


        holder.thumbImage.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
        holder.thumbImage.setTag(bean.imageUrl);
        Drawable drawable;
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
            holder.thumbImage.setImageDrawable(drawable);
        }
    }

    /**
     * 滑入时的自动刷新
     */
    public void doEnterRequest(){
        changeRequestStatus(STATUS_LOADING);
        performListRefreshing();
        doRefreshRequest();
    }

    @Override
    protected void doAddRequest() {
        dataManager.loadData(false);
    }

    @Override
    protected void doRefreshRequest() {
        isLastPage = false;

        loadHeader();
        dataManager.loadData(true);
    }

    public void doRefreshRequestSync(){
        isLastPage = false;
        Global.runInMainThread(new Runnable() {
            @Override
            public void run() {
                loadHeader();
            }
        });
        dataManager.loadDataSync(true);
    }

    private void createDataManager() {
        dataManager = new PageDataManager<>(new PageDataManager.DataRequester<TaobaoProduct>() {
            @Override
            public List<TaobaoProduct> requestData(int pageIndex, int pageSize) {
                return TaobaoLoader.getNetTaoBaoHomeList(mContext, pageIndex, pageSize);
            }
        });
        dataManager.setPageSize(PAGE_SIZE);
        dataManager.setFirstPageIndex(1);
        dataManager.setListener(this);
    }

    @Override
    public void onUpdateData(PageDataManager<TaobaoProduct> dataManager, boolean isReload) {
        if (!isAvailable()) return;

        List<TaobaoProduct> appendList = dataManager.getAppendList();
        if (appendList != null && !appendList.isEmpty()) {
            isCache = false;

            //数据请求成功，此时可以清除以往数据，装填新数据
            if (isReload) {
                mBeanList.clear();
            }

            appendData(appendList, isLastPage, 0);

            notifyDataSetChanged();

            cacheData(appendList, dataManager.getPageIndex());
        } else {
            isLastPage = true;
            if (dataManager.getShowList().isEmpty()) loadSomeCache();

            appendData(new ArrayList<TaobaoProduct>(), isLastPage, 0);
            notifyDataSetChanged();
        }
    }

    private void cacheData(List<TaobaoProduct> list, int pageIndex) {
        // 只缓存第一页数据
        if (pageIndex > 2 || list == null || list.isEmpty()) return;
        dbHelper.deleteAll();
        for (TaobaoProduct item : list) {
            dbHelper.add(item);
        }
    }

    private void loadSomeCache() {
        isCache = true;
        List<TaobaoProduct> cacheList = dbHelper.select(0, PAGE_SIZE, null, true);
        appendData(cacheList, true, 0);
    }

    /**
     * 加载淘宝头部数据
     */
    public void loadHeader() {
        if (headerWrapper == null) return;
        headerWrapper.refreshView();
    }

    private boolean isAvailable() {
        return lock != null;
    }

    public void onDestroy() {
        lock = null;
    }

    @Override
    public void onClick(View view) {
        Object item = view.getTag();
        if (item instanceof TaobaoProduct) {
            TaobaoProduct product = (TaobaoProduct) item;

            CvAnalysis.submitClickEvent(mContext.getApplicationContext(), TAOBAO_SCREEN_PAGEID, TAOBAO_SCREEN_POS_PRODUCT, product.resId, product.resType, product.sourceId);

            switch (product.resType) {
                case TAO_BAO_RES_TYPE_COLLECTION: {
                    showCollection(product);
                    break;
                }
                default: {
                    showWebProduct(product);
                    break;
                }
            }
        }
    }

    /**
     * 打开网页详情
     */
    private void showWebProduct(TaobaoProduct product) {
        if (TextUtils.isEmpty(product.urlClick)) return;

        TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器

        NewsDetailActivity.startDetailActivity(mContext, product.urlClick);
    }

    /**
     * 打开本地合辑
     */
    private void showCollection(TaobaoProduct product) {
        if (product.resId < 0) return;

        TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器

        List<TaobaoProduct> originList = mBeanList;
        int index = originList.indexOf(product);
        ArrayList<TaobaoProduct> promoters = new ArrayList<>();
        promoters.add(product);
        if (index >= 0 && index < originList.size()) {
            for (int i = index + 1; i < originList.size(); i++) {
                TaobaoProduct p = originList.get(i);
                if (p.resType == TaobaoProduct.TAO_BAO_RES_TYPE_COLLECTION && p.resId > 0) {
                    promoters.add(p);
                }
            }
        }

        TaoBaoCollectionDetailActivity.startCollectionActivity(mContext, promoters);
    }

    public class ViewHolder {
        View container;
        ImageView thumbImage;
        TextView tvProductCount;
        TextView tvToday;
        TextView tvTitle;
        TextView tvPromoPrice;
        TextView tvDiscountDesc;
        TextView tvPrice;
        TextView tvActivityDesc;


        public ViewHolder(View view) {
            container = view.findViewById(R.id.tb_layout);
            thumbImage = (ImageView) view.findViewById(R.id.iv_tb_preview);
            tvProductCount = (TextView) view.findViewById(R.id.tv_product_count);
            tvToday = (TextView) view.findViewById(R.id.tv_tb_today);
            tvTitle = (TextView) view.findViewById(R.id.tv_tb_title);
            tvPromoPrice = (TextView) view.findViewById(R.id.tv_tb_promo_price);
            tvDiscountDesc = (TextView) view.findViewById(R.id.tv_tb_huodong);
            tvPrice = (TextView) view.findViewById(R.id.tv_tb_price);
            tvActivityDesc = (TextView) view.findViewById(R.id.tv_tb_activity_desc);

            container.setOnClickListener(TaoBaoExtListAdapter.this);
        }
    }
}
