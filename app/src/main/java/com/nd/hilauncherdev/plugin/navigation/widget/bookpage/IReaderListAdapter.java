package com.nd.hilauncherdev.plugin.navigation.widget.bookpage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.config.NavigationConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.EmptyUtils;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsListLoadingView;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookCommander;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookItemView;

import java.util.List;

/**
 * 掌阅阅读屏-数据Adapter
 * Created by linliangbin on 2017/8/24 15:30.
 */

public class IReaderListAdapter extends AbsListViewAdapter<Book, IReaderListAdapter.ViewHolder> {


    private AsyncImageLoader mAsyncImageLoader = new AsyncImageLoader();


    /**
     * 是否是刷新请求
     */
    private boolean isRefresh = false;

    /**
     * 预览图显示宽度
     */
    private int itemImageWidth = 0;
    private boolean useCacheData = false;

    public IReaderListAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
        isNoLoadingView = true;
        removeFirstForBanner = false;
        itemImageWidth = (int) ((ScreenUtil.getScreenWidth(context) - ScreenUtil.dip2px(context, 16 * 2 + 30)) * 0.8 / 3);
    }

    /**
     * 是否当前展示本地缓存
     *
     * @return
     */
    public boolean isUseCacheData() {
        return useCacheData;
    }

    public void setUseCacheData(boolean useCacheData) {
        this.useCacheData = useCacheData;
    }

    public void removeLoadingView() {

        if (mLoadView != null && mListView != null) {
            ((ViewGroup) mListView.getParent()).removeView(mLoadView);
            mListView.setEmptyView(null);
        }

    }


    @Override
    public void initLoadingView() {
        if (LauncherBranchController.isNavigationForCustomLauncher()) {
            mLoadView = new NewsListLoadingView(mContext);
        } else {
            super.initLoadingView();
        }
    }


    @Override
    protected void setOnScrollIdleView(IReaderListAdapter.ViewHolder holder, Book bean, int position) {
        holder.titleText.setText(BookItemView.getPureTitle(bean.name));
        holder.thumbImage.setTag(bean.icon);
        holder.descText.setText(bean.desc);
        holder.publishText.setText(bean.author);

        asyncImageLoader.showDrawable(bean.icon, holder.thumbImage, ImageView.ScaleType.FIT_XY, CommonLauncherControl.getThemeNoFindSmall());

    }

    private void adjustThumbWidth(ImageView imageView) {
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = itemImageWidth;
        }
    }

    @Override
    protected void setOnScrollViewContent(IReaderListAdapter.ViewHolder holder, Book bean, int position) {
        holder.titleText.setText(BookItemView.getPureTitle(bean.name));
        holder.thumbImage.setTag(bean.icon);
        holder.descText.setText(bean.desc);
        holder.publishText.setText(bean.author);

        Drawable cache = mAsyncImageLoader.loadDrawableOnlyCache(bean.icon);

        if (cache == null) {
            holder.thumbImage.setImageDrawable(null);
            holder.thumbImage.setBackgroundDrawable(
                    mContext.getResources().getDrawable(CommonLauncherControl.getThemeNoFindSmall()));
        } else {
            holder.thumbImage.setImageDrawable(cache);
        }

    }

    @Override
    protected View createItem() {
        View v = View.inflate(mContext, R.layout.bookpage_list_item_layout, null);
        return v;
    }

    @Override
    protected IReaderListAdapter.ViewHolder initHolder(View view) {

        IReaderListAdapter.ViewHolder viewHolder = new IReaderListAdapter.ViewHolder();
        viewHolder.thumbImage = (ImageView) view.findViewById(R.id.iv_bookpage_list_item_thumb);
        viewHolder.titleText = (TextView) view.findViewById(R.id.tv_bookpage_list_item_title);
        viewHolder.descText = (TextView) view.findViewById(R.id.tv_bookpage_list_item_desc);
        viewHolder.publishText = (TextView) view.findViewById(R.id.tv_bookpage_list_item_publish);
        adjustThumbWidth(viewHolder.thumbImage);
        return viewHolder;
    }



    /**
     * @desc 启动桌面时的刷新
     * 优先从缓存中获取
     * 缓存为空，则访问接口刷新
     * @author linliangbin
     * @time 2017/8/30 18:29
     */
    public void doLaunchRequest() {

        changeRequestStatus(STATUS_LOADING);
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                List<Book> list = IReaderListModel.readFileCacheData(mContext);
                if (EmptyUtils.isEmpty(list)) {
                    doRefreshRequest();
                } else {
                    useCacheData = true;
                    refreshList(list);
                }
            }
        });
    }

    /**
     * @desc 第一次滑入时的刷新
     * @author linliangbin
     * @time 2017/9/4 12:37
     */
    public void doEnterRequest(){
        performListRefreshing();
        doRefreshRequest();
    }

    @Override
    protected void doAddRequest() {
        isRefresh = false;
        doRequest("");
    }

    //下拉刷新时自动调用这个方法，请求数据可以在这个
    @Override
    public void doRefreshRequest() {
        isRefresh = true;
        doRequest("");
    }


    public void doRefreshRequestSync(){
        isRefresh = true;
        doRequestSync("");
    }

    @Override
    protected void doRequest(String url) {
        useCacheData = false;
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });
    }

    public void doRequestSync(String url){
        useCacheData = false;
        loadData();
    }

    private void refreshList(final List<Book> finalList) {

        Global.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (STATUS_PULL_LOADING == mCurrentStatus && EmptyUtils.isEmpty(finalList)) {
                    //下接刷新时，如果刷新失败，就还原是时间戳
                    NavigationConfigPreferences.getInstance(mContext).decreaseBookpageIndex();
                }

                if (isRefresh) {
                    if ((EmptyUtils.isNotEmpty(finalList)) || (mBeanList.size() == 0)) {
                        IReaderListAdapter.this.appendData(finalList, false, 0);
                    } else {
                        if (STATUS_PULL_LOADING == mCurrentStatus) {
                            ((PullToRefreshListView) mListView).onLoadingComplete();
                            changeRequestStatus(STATUS_PENDDING);
                        }
                    }
                } else {
                    IReaderListAdapter.this.appendData(finalList, false, 0);
                }

            }
        });
    }

    public void loadData() {

        if(NavigationConfigPreferences.getInstance(mContext).needResetBookpageIndex()){
            NavigationConfigPreferences.getInstance(mContext).setBookPageIndex(1);
            NavigationConfigPreferences.getInstance(mContext).setBookpageResetIndxTime();
        }
        List<Book> list = IReaderListModel.loadBookPage(mContext, NavigationConfigPreferences.getInstance(mContext).getBookpageIndex());
        NavigationConfigPreferences.getInstance(mContext).increateBookpageIndex();
        //返回的数据为空，并且当前列表也没有数据则尝试从SD从读缓存
        if (EmptyUtils.isEmpty(list) && mBeanList.isEmpty()) {
            list = IReaderListModel.readFileCacheData(mContext);
        }
        refreshList(list);

    }


    @Override
    protected void onDataItemClick(View view, int position) {
        Book book = getItem(position);
        if (book == null) {
            return;
        }
        BookShelfLoader.mayNeedReloadRead = true;
        SystemUtil.startActivitySafely(mContext, BookCommander.getDetailIntent(book.bookId));
        CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                CvAnalysisConstant.DIANXIN_IREADER_RESID_RECOMMEND_ITEM, CvAnalysisConstant.RESTYPE_LINKS);
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "dtj");
    }

    @Override
    protected void setViewContent(ViewHolder holder, Book bean, int position) {

    }

    public class ViewHolder {

        public ImageView thumbImage;

        public TextView titleText;

        public TextView descText;

        public TextView publishText;


    }


}
