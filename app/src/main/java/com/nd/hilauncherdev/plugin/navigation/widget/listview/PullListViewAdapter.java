package com.nd.hilauncherdev.plugin.navigation.widget.listview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.ListViewFooter;
import com.nd.hilauncherdev.plugin.navigation.widget.listview.footer.VideoListViewFooter;

/**
 * 一个支持上拉刷新的List Adapter
 */
public abstract class PullListViewAdapter extends BaseAdapter implements OnScrollListener, View.OnClickListener {

    public static final int STATUS_READY = 1;
    public static final int STATUS_PULL_LOADING = 2;
    public static final int STATUS_PULL_ERROR = 3;
    public static final int STATUS_PULL_END = 4;
    protected Context context;
    protected ListView listView;
    // 当前数据加载的状态
    int mCurrentStatus = STATUS_READY;
    ListViewFooter listViewFooter;
    private OnScrollListener outerListener;

    public PullListViewAdapter(Context context, ListView listView) {

        this.context = context;
        this.listView = listView;
        init();
    }

    public void setOuterListener(OnScrollListener outerListener) {
        this.outerListener = outerListener;
    }

    private void init() {

        listViewFooter = new VideoListViewFooter(LayoutInflater.from(context));
        listViewFooter.hideFooterView();
        listView.addFooterView(listViewFooter.getFooterView());

    }


    protected void syncFooterViewState(int newState) {

        if (listViewFooter == null) {
            return;
        }

        mCurrentStatus = newState;
        switch (mCurrentStatus) {
            case STATUS_READY:
                listViewFooter.hideFooterView();
                break;
            case STATUS_PULL_LOADING:
                listViewFooter.showFooterView();
                listViewFooter.showFooterViewWaiting(isEmptyList());
                break;
            case STATUS_PULL_ERROR:
                listViewFooter.showFooterView();
                listViewFooter.showFooterViewError(isEmptyList(),this);
                break;
            case STATUS_PULL_END:
                listViewFooter.showFooterView();
                listViewFooter.showFooterViewListEnd();
                break;
        }
    }

    public abstract boolean isEmptyList();

    public abstract void requestPageData();

    /**
     * 滚动时，向服务请求数据
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (mCurrentStatus != STATUS_READY) {
            return;
        }
        // 滚到倒数第1行时，异步请求数据
        if (!TelephoneUtil.isNetworkAvailable(context)) {
            syncFooterViewState(STATUS_PULL_ERROR);
        } else if (firstVisibleItem + visibleItemCount >= totalItemCount - 1 && TelephoneUtil.isNetworkAvailable(context)) {
            if(firstVisibleItem > 1){
                syncFooterViewState(STATUS_PULL_LOADING);
                requestPageData();
            }else{
                syncFooterViewState(STATUS_PULL_ERROR);
            }
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    @Override
    public void onClick(View v) {
        requestPageData();
    }
}