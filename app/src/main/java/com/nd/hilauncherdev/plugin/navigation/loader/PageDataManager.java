package com.nd.hilauncherdev.plugin.navigation.loader;

import android.os.Handler;

import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linxiaobin on 2017/8/28.
 * 分页列表管理类
 */

public class PageDataManager<T> {
    public interface DataManagerListener<DATA> {
        void onUpdateData(PageDataManager<DATA> dataManager, boolean isReload);
    }

    public interface DataRequester<DATA> {
        List<DATA> requestData(int pageIndex, int pageSize);
    }

    private DataManagerListener<T> listener;
    private DataRequester requester;
    private List<T> showList = new ArrayList<>();
    private List<T> appendList = new ArrayList<>();
    private List<T> moreListAtLastPage;   // 固定在最后一页数据后面的元素
    private int pageSize = 20; // 默认分页大小
    private int pageIndex = 0;
    private int firstPageIndex = 0; // 默认起始页下标
    private boolean isReload;
    protected boolean loading;
    private boolean isLastPage;
    private Handler mHandler = new Handler();

    public PageDataManager(DataRequester<T> requester) {
        this.requester = requester;
    }

    /**
     * 返回完整列表
     */
    public List<T> getShowList() {
        return showList;
    }

    /**
     * 返回最近增加的列表
     */
    public List<T> getAppendList() {
        return appendList;
    }

    public int getFirstPageIndex() {
        return firstPageIndex;
    }

    public PageDataManager<T> setFirstPageIndex(int firstPageIndex) {
        this.firstPageIndex = firstPageIndex;
        return this;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public DataManagerListener<T> getListener() {
        return listener;
    }

    public void setListener(DataManagerListener<T> listener) {
        this.listener = listener;
    }

    public List<T> getMoreListAtLastPage() {
        return moreListAtLastPage;
    }

    public void setMoreListAtLastPage(List<T> moreListAtLastPage) {
        this.moreListAtLastPage = moreListAtLastPage;
    }

    synchronized public void loadDataSync(final boolean reload) {
        if (loading) return;
        loading = true;
        isLastPage = false;
        appendList = new ArrayList<>();
        isReload = reload;

        int index = pageIndex;
        if (reload) {
            index = firstPageIndex;
        }
        List<T> list = requester.requestData(index, getPageSize());
        receiveData(list);
    }

    @SuppressWarnings("unchecked")
    synchronized public void loadData(final boolean reload) {
        if (loading) return;

        loading = true;
        isLastPage = false;
        appendList = new ArrayList<>();
        isReload = reload;

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                int index = pageIndex;
                if (reload) {
                    index = firstPageIndex;
                }
                List<T> list = requester.requestData(index, getPageSize());
                receiveData(list);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void finishLoadData(boolean lastPage) {
        loading = false;
        isLastPage = lastPage;
        if (!lastPage) {
            pageIndex ++;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onUpdateData(PageDataManager.this, isReload);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    protected void receiveData(List<T> list) {
        if (list != null && !list.isEmpty()) {
            if (isReload) {
                showList.clear();
                pageIndex = firstPageIndex;
            }

            appendList = new ArrayList<>();
            for (T item : list) {
                if (!showList.contains(item)) {
                    appendList.add(item);
                    showList.add(item);
                }
            }

            finishLoadData(false);
        } else {
            if (getMoreListAtLastPage() != null && !getMoreListAtLastPage().isEmpty()) {
                showList.addAll(getMoreListAtLastPage());
                appendList.addAll(getMoreListAtLastPage());
            }
            finishLoadData(true);
        }
    }
}
