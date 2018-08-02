package com.nd.hilauncherdev.plugin.navigation.widget.netease;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.LargeFontNewsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.ZeroViewPagerTab;
import com.nd.hilauncherdev.plugin.navigation.widget.common.GoTopListView;

/**
 * 网易资讯屏
 * Created by linliangbin on 2017/8/31 17:26.
 */

public class NeteaseNewsPageView extends GoTopListView {

    //新闻列表的view
    NewsListViewAdapter mAdapter;


    public NeteaseNewsPageView(Context context) {
        super(context);
        init(false);
    }

    public NeteaseNewsPageView(Context context, boolean isLargeFont) {
        super(context);
        init(isLargeFont);
    }

    public NeteaseNewsPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false);
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setTabLisener(ZeroViewPagerTab.TabLisener tabLisener) {
        this.tabLisener = tabLisener;
    }

    private void init(boolean isLargeFont) {

        LayoutInflater.from(getContext()).inflate(R.layout.news_page, this);
        initGoTop();
        if (mListView != null) {
            mListView.setPullEnable(true);
        }
        if (isLargeFont) {
            mAdapter = new LargeFontNewsListViewAdapter(getContext(), mListView, "");
        } else {
            mAdapter = new NewsListViewAdapter(getContext(), mListView, "");
        }
        mAdapter.refreshRequest();
        mListView.requestFocus();
        setScrollListen(mAdapter);

    }


    public void setIsNeedRequestAD(boolean isNeedRequest) {
        if (mAdapter != null) mAdapter.setIsNeedRequestAD(isNeedRequest);
    }

    public void forcedoRefreshRequest() {
        if (mListView != null) {
            mListView.forcedoRefreshRequest();
        }
    }

    public void callOnEnter() {
        if (mAdapter != null) {
            mAdapter.firstRequest = false;
            mAdapter.onlyRequestAD();
        }
    }

}
