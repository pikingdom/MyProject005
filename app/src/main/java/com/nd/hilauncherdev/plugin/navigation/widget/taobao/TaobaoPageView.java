package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.adapter.TaobaoListAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.common.GoTopListView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;

/**
 * 淘宝购物屏
 * Created by linliangbin on 2017/9/1 15:15.
 *
 * @deprecated use {@link com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.TaoBaoExtPageView} instead.
 */

@Deprecated
public class TaobaoPageView extends GoTopListView implements PageActionInterface {

    //淘宝购物屏的view
    PullToRefreshListView mTaobaoListView;

    //淘宝购物屏headerView
    TaobaoCardWrapper mTaobaoCardWrapper;

    TaobaoListAdapter mTaobaoAdapter;

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public TaobaoPageView(Context context) {
        super(context);
        init();
        initData();
    }

    public TaobaoPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initData();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.taobao_page, this);
        initGoTop();
        //将第1屏和第2屏加入viewpager
        mTaobaoListView = (PullToRefreshListView) findViewById(R.id.gridview_shopping_list);
        mTaobaoListView.setPullEnable(true);

        //在setAdapter之前，设置addHeaderView
        mTaobaoCardWrapper = new TaobaoCardWrapper(getContext());
        mTaobaoListView.addHeaderView(mTaobaoCardWrapper, null, true);

        mTaobaoAdapter = new TaobaoListAdapter(getContext(), mTaobaoListView, "", mTaobaoCardWrapper);
        mTaobaoAdapter.refreshRequest();

        setScrollListen(mTaobaoAdapter);
    }

    @Override
    public int getGoTopViewId() {
        return R.id.taobao_gototop;
    }


    @Override
    public int getListViewId() {
        return R.id.gridview_shopping_list;
    }

    private void initData() {
        TaobaoData.getInstance().start(mTaobaoCardWrapper, true);
    }


    @Override
    public void onEnterPage() {
        mTaobaoCardWrapper.notifyPageChanged(true);
        mTaobaoAdapter.notifyPageChanged(true);
    }

    @Override
    public void onLeavePage() {

        mTaobaoCardWrapper.notifyPageChanged(false);
        mTaobaoAdapter.notifyPageChanged(false);
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onBackToPage() {

    }

    @Override
    public void onActionRefresh() {

    }

    @Override
    public void onActionRefreshSync() {

    }

    @Override
    public void doDailyUpdate() {

    }
}
