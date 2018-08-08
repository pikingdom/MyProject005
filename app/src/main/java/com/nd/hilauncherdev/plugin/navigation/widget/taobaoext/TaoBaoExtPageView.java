package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ListViewTool;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.common.GoTopListView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoCardWrapper;
import com.nd.hilauncherdev.plugin.navigation.widget.taobao.TaobaoData;
import com.nd.hilauncherdev.plugin.navigation.widget.third_party.HeaderScrollHelper;

import static com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter.STATUS_ERROR;

/**
 * Created by linxiaobin on 2017/9/7.
 * 淘宝扩展页
 * 注意离开当前页面时要关闭Banner定时器（mTaobaoCardWrapper.enterPageView(false)）
 */

public class TaoBaoExtPageView extends GoTopListView implements PageActionInterface, HeaderScrollHelper.ScrollableContainer {

    PullToRefreshListView listView;
    TaoBaoExtListAdapter mAdapter;
    //淘宝购物屏headerView
    TaobaoCardWrapper mTaobaoCardWrapper;

    long mLastLeaveTime = 0;//控制自动刷新
    private boolean needMargin = true;

    public TaoBaoExtPageView(Context context) {
        super(context);
        init();
        initData();
    }
    public TaoBaoExtPageView(Context context,boolean needMargin) {
        super(context);
        this.needMargin = needMargin;
        init();
        initData();
    }

    public TaoBaoExtPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initData();
    }

    private void init() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.taobao_ext_page_layout, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(needMargin){
            layoutParams.topMargin = ScreenUtil.getStatusBarHeight(getContext());
        }
        addView(view, layoutParams);
        listView = (PullToRefreshListView) this.findViewById(R.id.tao_bao_list_view);
        listView.setPullEnable(true);
        listView.disableBannerHeader();
        //在setAdapter之前，设置addHeaderView
        mTaobaoCardWrapper = new TaobaoCardWrapper(getContext());
        listView.addHeaderView(mTaobaoCardWrapper, null, true);

        mAdapter = new TaoBaoExtListAdapter(getContext(), listView, mTaobaoCardWrapper);

        initGoTop();
        setScrollListen(mAdapter);
    }


    public void disablePull() {
        if (listView != null) {
            listView.setPullEnable(false);
        }
    }


    @Override
    public int getGoTopViewId() {
        return R.id.taobao_gototop;
    }

    @Override
    public int getListViewId() {
        return R.id.tao_bao_list_view;
    }

    private void initData() {
        TaobaoData.getInstance().start(mTaobaoCardWrapper, true);
    }

    @Override
    public void onEnterPage() {
        CvAnalysis.submitPageStartEvent(NavigationView2.activity, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID);
        mTaobaoCardWrapper.enterPageView(true);
        mTaobaoCardWrapper.notifyPageChanged(true);

        autoRefreshOnEnter();
    }

    @Override
    public void onLeavePage() {
        mLastLeaveTime = System.currentTimeMillis();

        mTaobaoCardWrapper.enterPageView(false);
        mTaobaoCardWrapper.notifyPageChanged(true);
        CvAnalysis.submitPageEndEvent(NavigationView2.activity, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID);
    }

    @Override
    public void onPageResume() {
        // 从其他Activity回到当前页面
        mTaobaoCardWrapper.enterPageView(true);
    }

    @Override
    public void onBackToPage() {

    }

    @Override
    public void onActionRefresh() {
        if (mAdapter != null) {
            mAdapter.changeRequestStatus(AbsListViewAdapter.STATUS_PULL_LOADING);
            mAdapter.doRefreshRequest();
            ListViewTool.scroolToTop(listView);
        }
    }

    @Override
    public void onActionRefreshSync() {
        if (mAdapter != null) {
            Global.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.changeRequestStatus(AbsListViewAdapter.STATUS_PULL_LOADING);
                    mAdapter.loadHeader();
                }
            });
            mAdapter.doRefreshRequestSync();
        }
    }

    @Override
    public void doDailyUpdate() {
        onActionRefresh();
    }

    @Override
    public View getScrollableView() {
        return findViewById(R.id.tao_bao_list_view);
    }


    /**
     * 进入页面时自动更新数据
     * 初始化视图时加载缓存数据，初次进入购物屏或距离上次离开购物屏超过60分钟，自动刷新数据
     */
    private void autoRefreshOnEnter() {
        if (!TelephoneUtil.isNetworkAvailable(getContext())) {
            if (mAdapter.getCount() == 0) {
                mAdapter.changeRequestStatus(STATUS_ERROR);
            }
            return;
        }

        if (mLastLeaveTime <= 0
                || (Math.abs(System.currentTimeMillis() - mLastLeaveTime) > 60 * 60000)
                || mAdapter.isCache()) {

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.doEnterRequest();
                }
            }, 100);
        }
    }
}
