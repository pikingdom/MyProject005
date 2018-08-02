package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.loader.PageDataManager;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.SquareImageView;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_COLLECTION_DETAIL_PAGE;
import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_COLLECTION_DETAIL_POS_PRODUCT;

/**
 * Created by linxiaobin on 2017/9/11.
 * 淘宝合辑页数据
 */

public class TaoBaoCollectionDetailAdapter extends AbsListViewAdapter<List<TaobaoProduct>, TaoBaoCollectionDetailAdapter.ViewHolder> implements View.OnClickListener {
    private AsyncImageLoader mImageLoader = new AsyncImageLoader();
    private PageDataManager<TaobaoProduct> dataManager;

    public View footerView;

    private Object lock = new Object();

    public TaoBaoCollectionDetailAdapter(Context context, ListView listView, PageDataManager<TaobaoProduct> dataManager) {
        super(context, listView, "");

        this.dataManager = dataManager;

        SHOW_LIST_END_FOOTER_LIMIT = 4;
        removeFirstForBanner = false;
        mFootView.getFooterView().setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    protected void setViewContent(ViewHolder holder, List<TaobaoProduct> bean, int position) {
    }

    @Override
    protected View createItem() {
        return LayoutInflater.from(mContext).inflate(R.layout.item_taobao_product_ext_2col, null);
    }

    @Override
    protected ViewHolder initHolder(View view) {
        return new ViewHolder(view);
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
    }

    private void setContent(TaobaoProduct bean, ViewHolder holder, boolean isIdle) {
        if (bean == null) {
            return;
        }
        switch (bean.view_type) {
            case TaobaoProduct.ITEM_LEFT:
                holder.leftHolder.setProduct(bean, isIdle);
                break;
            case TaobaoProduct.ITEM_RIGHT:
                holder.rightHolder.setProduct(bean, isIdle);
                break;
        }
    }

    @Override
    protected void doAddRequest() {
        dataManager.loadData(false);
    }

    @Override
    protected void doRefreshRequest() {
        dataManager.loadData(true);
    }

    public void onUpdateData(List<TaobaoProduct> appendList, boolean isReload) {
        if (!isAvailable()) return;

        // 只处理2个以上的商品
        if (appendList != null && appendList.size() > 1) {
            //数据请求成功，此时可以清除以往数据，装填新数据
            if (isReload) {
                mBeanList.clear();
            }

            calViewType(appendList);
        } else {
            // 有下一个专辑时，隐藏底部最后一页提示
            if (footerView != null) {
                changeRequestStatus(STATUS_READY);
            } else {
                appendData(new ArrayList<List<TaobaoProduct>>(), true, 0);
            }
        }

        if (footerView != null) {
            footerView.setVisibility(dataManager.isLastPage() ? View.VISIBLE : View.GONE);
        }

        notifyDataSetChanged();
    }

    /**
     * 计算左、右数据
     */
    private void calViewType(List<TaobaoProduct> list) {
        List<List<TaobaoProduct>> data = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (int j = 0; j < (list.size() + 1) / 2; j++) {
                List<TaobaoProduct> row = new ArrayList<>();
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
                    row.add(product);
                }
                if (row.size() == 2)
                    data.add(row);
            }
        }
        appendData(data, false, 0);
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
            showWebProduct((TaobaoProduct) item);
        }
    }

    /**
     * 打开网页详情
     */
    private void showWebProduct(TaobaoProduct product) {
        if (TextUtils.isEmpty(product.urlClick)) return;

        CvAnalysis.submitClickEvent(mContext.getApplicationContext(), TAOBAO_COLLECTION_DETAIL_PAGE, TAOBAO_COLLECTION_DETAIL_POS_PRODUCT, product.resId, product.resType, product.sourceId);
        NewsDetailActivity.startDetailActivity(mContext, product.urlClick);
    }

    class ViewHolder {
        OneViewHolder leftHolder;
        OneViewHolder rightHolder;

        public ViewHolder(View view) {
            leftHolder = new OneViewHolder(view.findViewById(R.id.layout_item_left));
            rightHolder = new OneViewHolder(view.findViewById(R.id.layout_item_right));
        }
    }

    private class OneViewHolder {
        View itemView;
        ImageView ivGoodsIcon;
        TextView tvGoodsDesc;
        TextView tvPrice;
        TextView tvPromoPrice;
        TextView tvDiscountDesc;

        OneViewHolder(View view) {
            if (view == null) return;

            itemView = view;
            ivGoodsIcon = (SquareImageView) view.findViewById(R.id.iv_goods_icon);
            tvGoodsDesc = (TextView) view.findViewById(R.id.tv_goods_name);
            tvPrice = (TextView) view.findViewById(R.id.tv_goods_price);
            tvPromoPrice = (TextView) view.findViewById(R.id.tv_goods_promo_price);
            tvDiscountDesc = (TextView) view.findViewById(R.id.tv_tb_huodong);

            view.setOnClickListener(TaoBaoCollectionDetailAdapter.this);
        }

        void setProduct(TaobaoProduct bean, boolean isIdle) {
            if (bean == null) return;

            itemView.setTag(bean);
            ivGoodsIcon.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
            tvGoodsDesc.setText(bean.title);
            ivGoodsIcon.setTag(bean.imageUrl);
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
                ivGoodsIcon.setImageDrawable(drawable);
            }
            tvPromoPrice.setText(bean.getProductsShowPrice());
            tvPrice.setText(bean.getProductsPrice());
            tvPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvDiscountDesc.setText(bean.discountDes);
            tvDiscountDesc.setVisibility(TextUtils.isEmpty(bean.discountDes) ? View.GONE : View.VISIBLE);
        }
    }
}
