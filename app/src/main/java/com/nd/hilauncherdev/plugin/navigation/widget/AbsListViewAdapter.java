package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 重构ListViewAdapter,其目的是将Item项分离出来。其中B为数据，H为ViewHolder
 *
 * @author wen.yugang </br> 2012-5-7
 */
public abstract class AbsListViewAdapter<B, H> extends BaseAdapter implements OnScrollListener {


    // 当前数据加载的状态
    public static final int STATUS_READY = -2;
    public static final int STATUS_PENDDING = -1;
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_LIST_ISNULL = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_PULL_LOADING = 3;
    public static final int STATUS_NO_DATA = 4;
    /**
     * 列表滑动到底部的状态
     */
    public static final int STATUS_LIST_END = 4;
    public LinearLayout headView;
    public int from = 0;
    public int mCurrentStatus = STATUS_READY;
    public AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    // 是否移除第一条数据作为大图Banner
    public boolean removeFirstForBanner = true;
    /**
     * 当当前个数大于 SHOW_FOOTER_LIMIT时，才显示footer
     */
    public int SHOW_LIST_END_FOOTER_LIMIT = 10;
    /**
     * 是否显示loading view
     * 没有loading view情况下需要显示footer view
     */
    public boolean isNoLoadingView = false;
    protected List<B> mBeanList = new ArrayList<B>();
    protected LoadingStateView mLoadView;
    protected ListViewFooter mFootView;
    protected LayoutInflater mInflater;
    protected ListView mListView;
    //数据直接提供，不从服务端取，只有一页。
    protected boolean isLocalMode = false;
    //是否进行数据过滤，对已安装的进行过滤不显示
    protected boolean isFilter = false;
    // 当前加载页
    protected int mCurrentPage = 0;
    // 空数据的提示语
    protected String mNoDataTip;
    // 是否加载完数据,默认为最后一页。
    protected boolean isLastPage = false;
    // 环境上下文
    protected Context mContext;
    // 要请求的URL
    protected String mUrl;
    //优化方案，只有在快速滚动的时候，才调用
    protected boolean mOnScrolleFilpping = false;
    // 是否已添加了FooterView;
    protected boolean mAddFooter = false;
    protected List<B> mBannerData = new ArrayList<B>();

    protected String tag = "AbsListViewAdapter";
    //空数据显示的LoadingView
    private View failView;
    // 排除重复
    private List<B> lastPageList = null;
    private OnExtendScrollListener mScrollListener;
    //无loading 下的刷新按钮的回调
    private OnClickListener mErrorClickLisenterWithoutLoading = new OnClickListener() {

        @Override
        public void onClick(View v) {
            refreshRequest();
        }
    };
    //点击lodingview中 刷新按钮的回调
    private OnClickListener mErrorClickLisenter = new OnClickListener() {

        @Override
        public void onClick(View v) {
            request();
        }
    };
    Handler mOnScrollChangedHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (!mOnScrolleFilpping) {
                notifyDataSetChanged();
            }
        }

        ;
    };

    public AbsListViewAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public AbsListViewAdapter(Context context, ListView listView, String url) {
        this(context);
        if (null == listView) {
            throw new NullPointerException("listview is null");
        }
        initCommonView();
        mUrl = url;
        initListView(listView);

    }

    public void setScrollListener(OnExtendScrollListener listener) {
        mScrollListener = listener;
    }

    /**
     * 初始化loadingview
     */
    public void initLoadingView() {
        mLoadView = new LoadingStateView(mContext);
    }

    public void initCommonView() {
        initLoadingView();
        mLoadView.setOnRefreshListening(new LoadingStateView.OnRefreshListening() {
            @Override
            public void onRefresh() {
                doNetErrorRefreshCallback();
                request();
            }
        });
    }

    /**
     * 设置数据列表为空时需要的提示信息
     *
     * @param resId 资源ID
     */
    public void setNullTip(int resId) {
        mNoDataTip = mContext.getString(resId);
    }

    public void setNullTip(String value) {
        mNoDataTip = value;
    }

    public LoadingStateView getLoadView() {
        return mLoadView;
    }

    /**
     * 重设数据内容。
     */
    public void reset() {
        lastPageList = null;
        mBeanList.clear();
        mCurrentPage = 0;
        isLastPage = false;
        mCurrentStatus = STATUS_READY;
        notifyDataSetChanged();
    }

    protected void addFooterView() {
        if (!mAddFooter) {
            if (mFootView == null) {
                mFootView = new ListViewFooter(mInflater);
            }
            mFootView.hideFooterView();
            mListView.addFooterView(mFootView.getFooterView());
            mAddFooter = true;
//			LogUtil.d(tag, "add FooterView");
        }
    }

    public void showListEndFootView() {
        if (mAddFooter && mFootView != null && mFootView.getFooterView() != null) {
            mFootView.showFooterViewListEnd();
        }

    }

    public void removeFootView() {
        if (mAddFooter && mFootView != null && mFootView.getFooterView() != null) {
            mListView.removeFooterView(mFootView.getFooterView());
            mAddFooter = false;
//			LogUtil.d(tag, "remove FooterView");
        }
    }

    /**
     * 初始化ListView,如果已经初始化将忽略
     */
    public void initListView(ListView listView) {
        if (null != mListView) {
            return;
        }
        mListView = listView;
        addBannerView();
        if (headView != null) {
            mListView.addHeaderView(headView);
        }
        addFooterView();
        if (mListView instanceof PullToRefreshListView) {
            ((PullToRefreshListView) mListView).setAdapter(this);
            ((PullToRefreshListView) mListView).setListener(new PullToRefreshListView.P2RListViewStateListener() {
                @Override
                public void onStateChanged(int state) {
                    onListViewStateChanged(state);
                }
            });
        } else {
            mListView.setAdapter(this);
        }
        ViewGroup.LayoutParams vlp = mListView.getLayoutParams();
        if (vlp != null) {
            mLoadView.setLayoutParams(vlp);
        } else {
            mLoadView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        ((ViewGroup) listView.getParent()).addView(mLoadView);
        listView.setEmptyView(mLoadView);
        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idx = position;
                if (mListView.getHeaderViewsCount() > 0 || mListView.getFooterViewsCount() > 0) {
                    // 由于设置headView，位置要减
                    idx = position - mListView.getHeaderViewsCount();
                }
                if (idx >= 0 && idx < mBeanList.size()) {
                    onDataItemClick(view, idx);
                }
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void request() {
        if (isLastPage && !isLocalMode) {
            return;
        }
        changeRequestStatus(STATUS_LOADING);
        doAddRequest();
    }

    public void refreshRequest() {
        changeRequestStatus(STATUS_LOADING);
        doRefreshRequest();
    }

    /**
     * 网络异常按钮刷新回调
     */
    public void doNetErrorRefreshCallback() {

    }

    /**
     * 向服务端请求数据
     */
    protected void doAddRequest() {
    }

    /**
     * 拖动刷新时向服务器请求数据
     */
    protected void doRefreshRequest() {
    }


    /**
     * @desc 列表展示成刷新中的状态
     * @author linliangbin
     * @time 2017/9/4 12:36
     */
    protected void performListRefreshing() {
        changeRequestStatus(STATUS_PULL_LOADING);
        if (mListView != null && mListView instanceof PullToRefreshListView) {
            ((PullToRefreshListView) mListView).performRefreshingState();
        }
    }

    /**
     * 如果网络请求有错误，请调用该函数来改变Foot状态
     */
    public void notifyRequestError() {

        if (STATUS_PULL_LOADING == mCurrentStatus) {
            changeRequestStatus(STATUS_PENDDING);
            ((PullToRefreshListView) mListView).setRecover();
        } else {
            changeRequestStatus(STATUS_ERROR);
        }
    }

    /**
     * 改变状态
     *
     * @param statusLoading
     */
    // String a[] = {"准备", "等待", "正在加载", "列表为空", "出错", "下拉正在加载"};
    public void changeRequestStatus(int statusLoading) {
        //Log.e("zhou","状态="+a[statusLoading+2]);
        mCurrentStatus = statusLoading;
        switch (mCurrentStatus) {
            case STATUS_PULL_LOADING:
            case STATUS_LIST_END:
                break;

            case STATUS_ERROR:
            case STATUS_LOADING:
            case STATUS_LIST_ISNULL:
                addFooterView();
                break;
            case STATUS_PENDDING:
            default:
                mFootView.hideFooterView();
                break;
        }

        switch (mCurrentStatus) {
            case STATUS_PULL_LOADING:
            case STATUS_PENDDING:
                break;
            case STATUS_ERROR:
                if (mBeanList.isEmpty()) {
                    mLoadView.setState(LoadingStateView.LoadingState.NetError);
                    if (isNoLoadingView) {
                        mFootView.showFooterViewError(mErrorClickLisenterWithoutLoading);
                    }
                } else {
                    mFootView.showFooterViewError(mErrorClickLisenter);
                }
                break;
            case STATUS_LOADING:
                if (mBeanList.isEmpty()) {
                    mLoadView.setState(LoadingStateView.LoadingState.Loading);
                    if (isNoLoadingView) {
                        mFootView.showFooterViewWaiting();
                    }
                } else {
                    mFootView.showFooterViewWaiting();
                }
                break;
            case STATUS_LIST_ISNULL:
                // 无数据
                if (mBeanList.isEmpty()) {
                    mLoadView.setState(LoadingStateView.LoadingState.NetError);
                } else {
                    mFootView.showFooterViewError(mNoDataTip, mErrorClickLisenter);
                }

                // 重新初始化一下数据，以便当数据为空的时候可以再次刷新界面。
                reset();
                break;
            case STATUS_NO_DATA:
                if (mBeanList.isEmpty()) {
                    if (mLoadView != null) {
                        mLoadView.setState(LoadingStateView.LoadingState.NoData);
                    }
                }
            default:
                break;
        }
    }

    /**
     * 向适配器添加数据,当网络请求完成的时候，调用这个函数，可以更新器状态和数据。
     *
     * @param list 数据
     * @param last 是否为最后一页。
     */
    public void appendData(List<B> list, boolean last) {
        if (null == list || list.isEmpty() && !last) {
            //第一次加载数据，但是从服务端没有数据返回。所以加载本地
            //从本地加载后，再判断一次
            if (null == list || list.isEmpty()) {
                changeRequestStatus(STATUS_ERROR);
                return;
            }
        }

        isLastPage = last;
        // 只有当前有数据时，才刷新页面
        if (!list.isEmpty()) {

            //第一条数据设置成头条
            if (mCurrentPage == 0) {
                mBeanList.clear();
                mBannerData.clear();
                mBannerData.add(list.get(0));
                /**
                 * 资讯屏需要移除第一条新闻作为Header展示，避免重复
                 * 可以自定义
                 */
                if (removeFirstForBanner)
                    list.remove(0);
            }

            mBeanList.addAll(removeDuplicateWithOrder(list));

            //设置baner的数据
            setBannerContent(mBannerData);
            notifyDataSetChanged();
            //有数据页码加1
            mCurrentPage++;

        }
        if (isLastPage && mBeanList != null && mBeanList.size() >= SHOW_LIST_END_FOOTER_LIMIT) {
            // 需要删除footView 不然会出现空白条
            showListEndFootView();
        }

        // 总数据为空，显示数据为空回调
        if (mBeanList.isEmpty()) {
            onDataEmpty();
        }

        boolean isEmpty = mBeanList.size() == 0 ? true : false;
        if (isEmpty) {
            changeRequestStatus(STATUS_LIST_ISNULL);
        } else if (isLastPage) {
            changeRequestStatus(STATUS_LIST_END);
        } else {
            changeRequestStatus(STATUS_PENDDING);
        }
    }

    public void
    appendData(List<B> list, boolean last, int index) {

        if (STATUS_PULL_LOADING == mCurrentStatus) {
            ((PullToRefreshListView) mListView).onLoadingComplete();
            //如查下拉刷新失败，则保存上一次的请求
            if (!list.isEmpty()) {
                reset();
            }

        } else if (1 == mCurrentPage && (mListView instanceof PullToRefreshListView)) {

            ((PullToRefreshListView) mListView).setRefreshTime();
        }
        appendData(list, last);
    }

    /**
     * 手动给列表赋值，用于软件专题。第一次数据来自于专题头信息请求
     *
     * @param list
     * @param last
     * @param index
     */
    public void appendDataByManual(List<B> list, boolean last, int index) {
        appendData(list, last);
    }

    /**
     * 总数据为空时，被调用。
     */
    protected void onDataEmpty() {

    }

    @Override
    public int getCount() {
        return mBeanList.size();
    }

    @Override
    public B getItem(int position) {
        return mBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 这里对view进行了优化。当ListView在滚动的时候，会调用两个函数，一个是，
     * {@link #setViewContent(Object, Object, int)} 和
     * {@link #setOnScrollViewContent(Object, Object, int)}.反之是
     * {@link #setViewContent(Object, Object, int)} 和
     * {@link #setOnScrollIdleView(Object, Object, int)}.你可以根据实际情况，选择你要初始化的方法。
     */
    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//		LogUtil.d(MessageFormat.format("position:{0}", position));
        View view = convertView;
        H holder = null;
        if (view == null) {
            view = createItem();
            holder = initHolder(view);
            view.setTag(holder);
        } else {
            holder = (H) view.getTag();
        }

        setViewContent(holder, getItem(position), position);
        if (!mOnScrolleFilpping) {
            setOnScrollIdleView(holder, getItem(position), position);
        } else {
            setOnScrollViewContent(holder, getItem(position), position);
        }
        return view;
    }

    /**
     * 当ListView不在滚动的时候调用，功能同getView
     *
     * @param holder
     * @param bean
     * @param position
     */
    protected void setOnScrollIdleView(H holder, B bean, int position) {
    }

    /**
     * 当ListView在滚动的时候调用,功能同getView
     *
     * @param holder
     * @param bean
     * @param position
     */
    protected void setOnScrollViewContent(H holder, B bean, int position) {
    }

    /**
     * 点击某一项事件
     *
     * @param view
     * @param position
     */
    protected void onDataItemClick(View view, int position) {
    }

    /**
     * 滚动时，向服务请求数据
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollListener != null)
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (STATUS_LOADING == mCurrentStatus || STATUS_PULL_LOADING == mCurrentStatus || mCurrentStatus == STATUS_READY || totalItemCount == 0 || isLocalMode) {
            return;
        }
        if (STATUS_ERROR == mCurrentStatus) {
            return;
        }
        // 滚到倒数第1行时，异步请求数据
        if (!TelephoneUtil.isNetworkAvailable(mContext)) {
            mFootView.showFooterViewError(mErrorClickLisenter);
        } else if (firstVisibleItem + visibleItemCount >= totalItemCount - 1 && TelephoneUtil.isNetworkAvailable(mContext)) {
            if (firstVisibleItem > 1) {
                request();
            } else {
                request();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //LogUtil.d(MessageFormat.format("current state:{0}", scrollState));
        if (isLocalMode) return;

        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
            mOnScrolleFilpping = true;
        } else {
            mOnScrolleFilpping = false;
            mOnScrollChangedHandle.sendEmptyMessage(500);
        }
    }

    public int getRequestState() {
        return mCurrentStatus;
    }

    @Override
    public void notifyDataSetChanged() {
        // 加载数据完成，进行删除会数据为空，需要显示空视图
        if (mCurrentStatus == STATUS_PENDDING && mBeanList.isEmpty()) {
            changeRequestStatus(STATUS_LIST_ISNULL);
        }
        super.notifyDataSetChanged();
    }

    protected void onListViewStateChanged(int state) {
        switch (state) {
            case PullToRefreshListView.P2RListViewStateListener.STATE_PULL: {
                lastPageList = null;
            }
            break;

            case PullToRefreshListView.P2RListViewStateListener.STATE_LOADING: {
                if (STATUS_LOADING == mCurrentStatus || STATUS_PULL_LOADING == mCurrentStatus) {
                    //正在翻页，不请求数据，直接完成
                    ((PullToRefreshListView) mListView).onLoadingComplete();
                    return;
                }


                changeRequestStatus(STATUS_PULL_LOADING);
                doRefreshRequest();
            }
            break;

            case PullToRefreshListView.P2RListViewStateListener.STATE_RECOVER: {

            }
            break;

            default:
                break;
        }
    }

    /**
     * 设置ListItem的内容
     */
    protected abstract void setViewContent(H holder, B bean, int position);

    /**
     * 创建ListItem
     */
    protected abstract View createItem();

    /**
     * 初始化ViewHolder
     */
    protected abstract H initHolder(View view);

    /**
     * 向服务端请求数据
     */
    protected void doRequest(String url) {

    }

    public void clear() {
        mListView.removeAllViews();
        mBeanList.clear();
        mBeanList = null;
        mListView = null;
        lastPageList = null;
    }

    public List<B> removeDuplicateWithOrder(List<B> list) {
        if (isLocalMode || (null != lastPageList && lastPageList.equals(list))) {
            return list;
        }
        if (lastPageList != null) {
            for (Iterator<B> iterator = list.iterator(); iterator.hasNext(); ) {
                if (lastPageList.contains((B) iterator.next())) {
                    iterator.remove();
                }
            }
        }
        lastPageList = list;
        return list;
    }

    protected void addBannerView() {

    }

    protected void setBannerContent(List<B> list) {

    }

    public interface OnExtendScrollListener {
        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

}