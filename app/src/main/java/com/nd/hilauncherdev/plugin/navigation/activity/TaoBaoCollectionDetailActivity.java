package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginActivity;
import com.felink.sdk.common.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.TaoBaoCollection;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.loader.PageDataManager;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoCollectionDetailAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoCollectionDetailFooterView;
import com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoCollectionDetailHeaderView;

import java.util.List;

import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_COLLECTION_DETAIL_PAGE;
import static com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant.TAOBAO_COLLECTION_DETAIL_POS_NEXT;

/**
 * Created by linxiaobin on 2017/9/11.
 * 淘宝合辑页
 */
public class TaoBaoCollectionDetailActivity extends PluginActivity implements PageDataManager.DataManagerListener<TaobaoProduct> {
    private static final int PAGE_SIZE = 14;

    private static final String INTENT_KEY_COLLECTION_ID_LIST = "COLLECTION_ID_LIST";
    private int resId; //合辑Id
    private String title;
    private String bannerUrl; //顶部Banner图地址
    private long startTime;
    private long endTime;
    private long fixTime;

    private List<TaobaoProduct> nextCollections;
    PullToRefreshListView listView;
    TaoBaoCollectionDetailAdapter mAdapter;
    private PageDataManager<TaobaoProduct> dataManager;
    private TextView tvTitle;
    private TaoBaoCollectionDetailHeaderView headerView;

    public static void startCollectionActivity(Context context, List<TaobaoProduct> products) {
        if (products == null || products.isEmpty()) return;

        int max = 3;
        for (int i = products.size() - 1; i >= max; i--) {
            products.remove(i);
        }

        Intent intent = new Intent();
        intent.setClass(context, TaoBaoCollectionDetailActivity.class);
        intent.putExtra(INTENT_KEY_COLLECTION_ID_LIST, TaobaoProduct.items2Json(products));

        SystemUtil.startActivitySafely(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tao_bao_collection);

        loadIntent();
        configViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (headerView != null) {
            headerView.startTimer();
        }

        loadWebTime();
        CvAnalysis.submitPageStartEvent(this, TAOBAO_COLLECTION_DETAIL_PAGE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (headerView != null) {
            headerView.stopTimer();
        }

        CvAnalysis.submitPageEndEvent(this, TAOBAO_COLLECTION_DETAIL_PAGE);
    }

    private static final String TAG = "TaoBaoCollectionDetailA";

    private void loadIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        nextCollections = TaobaoProduct.json2Items(this, intent.getStringExtra(INTENT_KEY_COLLECTION_ID_LIST));
        Log.d(TAG, "loadIntent: " + nextCollections);
        if (nextCollections != null && !nextCollections.isEmpty()) {
            resId = nextCollections.get(0).resId;
            nextCollections.remove(0);
        }
        Log.d(TAG, "loadIntent: " + resId);

        if (resId <= 0) {
            finish();
        }
    }

    private void loadWebTime() {
        if (fixTime == 0) {
            ThreadUtil.executeMore(new Runnable() {
                @Override
                public void run() {
                    long webTime = TaobaoLoader.getServerTime(TaoBaoCollectionDetailActivity.this);
                    if (Math.abs(webTime - System.currentTimeMillis()) > 1000) {
                        fixTime = webTime - System.currentTimeMillis();
                    }
                }
            });
        }
    }

    private void configViews() {
        createDataManager();

        listView = (PullToRefreshListView) this.findViewById(R.id.tao_bao_list_view);
        listView.setPullEnable(true);
        listView.disableBannerHeader();

        headerView = new TaoBaoCollectionDetailHeaderView(this);
        headerView.setBannerUrl(bannerUrl);
        listView.addHeaderView(headerView);

        TaoBaoCollectionDetailFooterView footerView = null;
        if (nextCollections != null && !nextCollections.isEmpty()) {
            footerView = new TaoBaoCollectionDetailFooterView(this);
            footerView.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onGoNextCollection();
                }
            });
            listView.addFooterView(footerView);
        }

        mAdapter = new TaoBaoCollectionDetailAdapter(this, listView, dataManager);
        mAdapter.footerView = footerView;
        mAdapter.refreshRequest();

        tvTitle = (TextView) findViewById(R.id.tv_tao_bao_collection_title);
        ImageView back = (ImageView) findViewById(R.id.iv_tb_collection_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // 去下一个专场
    private void onGoNextCollection() {
        if (nextCollections == null || nextCollections.isEmpty()) return;

        TaobaoProduct product = nextCollections.get(0);
        CvAnalysis.submitClickEvent(getApplicationContext(), TAOBAO_COLLECTION_DETAIL_PAGE, TAOBAO_COLLECTION_DETAIL_POS_NEXT, product.resId, product.resType, product.sourceId);

        startCollectionActivity(this, nextCollections);
    }

    private void createDataManager() {
        dataManager = new PageDataManager<>(new PageDataManager.DataRequester<TaobaoProduct>() {
            @Override
            public List<TaobaoProduct> requestData(int pageIndex, int pageSize) {
                return TaoBaoCollectionDetailActivity.this.requestData(pageIndex, pageSize);
            }
        });
        dataManager.setPageSize(PAGE_SIZE);
        dataManager.setFirstPageIndex(1);
        dataManager.setListener(this);
    }

    private List<TaobaoProduct> requestData(int pageIndex, int pageSize) {
        TaoBaoCollection collection = TaobaoLoader.getNetTaoBaoCollectionProducts(this, resId, pageIndex, pageSize);
        if (collection == null) return null;

        if (!collection.isHasNext()) {
            dataManager.setLastPage(true);
        }
        if (!TextUtils.isEmpty(collection.getTitle())) {
            title = collection.getTitle();
            bannerUrl = collection.getBannerUrl();
            startTime = collection.getStartTimeMills();
            endTime = collection.getEndTimeMills();
        }

        if (tvTitle != null) {
            tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    updateTitleAndHead();
                }
            });
        }

        return collection.getList();
    }

    @Override
    public void onUpdateData(PageDataManager<TaobaoProduct> dataManager, boolean isReload) {
        mAdapter.onUpdateData(dataManager.getAppendList(), isReload);
    }

    private void updateTitleAndHead() {
        if (tvTitle == null) return;

        tvTitle.setText(title);
        headerView.setBannerUrl(bannerUrl);

        headerView.setFixTimeInterval(fixTime);
        headerView.setStartTimeInterval(startTime);
        headerView.setEndTimeInterval(endTime);
        headerView.startTimer();
    }
}
