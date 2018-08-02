package com.nd.hilauncherdev.plugin.navigation.widget.bookpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.ListViewTool;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nd.hilauncherdev.plugin.navigation.widget.third_party.HeaderScrollHelper;

/**
 * 掌阅阅读屏
 * Created by linliangbin on 2017/8/21 11:52.
 */

public class ReaderPageView extends RelativeLayout implements PageActionInterface, HeaderScrollHelper.ScrollableContainer {

    public static int CV_PAGE_ID = CvAnalysisConstant.DIANXIN_IREADER_PAGE_ID;
    public static int CV_POSITION_ID = CvAnalysisConstant.DIANXIN_IREADER_POSITION_ID;
    PullToRefreshListView listView;
    IReaderListAdapter adapter;


    public ReaderPageView(Context context) {
        super(context);
        init();
    }

    public ReaderPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            CV_PAGE_ID = CvAnalysisConstant.DZ_IREADER_PAGE_ID;
            CV_POSITION_ID = CvAnalysisConstant.DZ_IREADER_POSITION_ID;
        }
        LayoutInflater.from(getContext()).inflate(R.layout.ireader_page_layout, this);
        listView = (PullToRefreshListView) this.findViewById(R.id.ireader_listview);
        listView.setPullEnable(true);
        listView.disableBannerHeader();

        adapter = new IReaderListAdapter(getContext(), listView, "");

        adapter.doLaunchRequest();
        adapter.removeLoadingView();
    }


    /**
     * @desc 关闭搜索界面展示
     * @author linliangbin
     * @time 2017/9/15 18:45
     */
    public void disableSearch() {
        if (listView != null && listView instanceof ListViewWithBook) {
            ((ListViewWithBook) listView).removeSearchHeader();
        }
    }

    public void disablePullRefresh() {
        if (listView != null) {
            listView.setPullEnable(false);
        }
    }

    public void handleResume() {
        if (BookShelfLoader.mayNeedReloadRead) {
            if (listView != null && listView instanceof ListViewWithBook) {
                ((ListViewWithBook) listView).doShelfUpdate();
            }
        }
    }

    @Override
    public void onEnterPage() {
        CvAnalysis.submitPageStartEvent(NavigationView2.activity, CV_PAGE_ID);
        if (adapter != null && adapter.isUseCacheData()) {
            adapter.setUseCacheData(false);
            adapter.doEnterRequest();
        }
    }

    @Override
    public void onLeavePage() {
        CvAnalysis.submitPageEndEvent(NavigationView2.activity, CV_PAGE_ID);
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onBackToPage() {

    }

    @Override
    public void onActionRefresh() {
        if (adapter != null) {
            adapter.changeRequestStatus(AbsListViewAdapter.STATUS_PULL_LOADING);
            adapter.doRefreshRequest();
            ListViewTool.scroolToTop(listView);
        }
    }

    @Override
    public void onActionRefreshSync() {
        if (adapter != null) {
            Global.runInMainThread(new Runnable() {
                @Override
                public void run() {
                    adapter.changeRequestStatus(AbsListViewAdapter.STATUS_PULL_LOADING);
                }
            });
            adapter.doRefreshRequestSync();
        }
    }

    @Override
    public void doDailyUpdate() {
        onActionRefreshSync();
    }

    @Override
    public View getScrollableView() {
        return listView;
    }
}
