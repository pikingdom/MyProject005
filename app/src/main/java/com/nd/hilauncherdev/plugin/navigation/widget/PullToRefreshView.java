package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;

import java.lang.reflect.Method;

/**
 * 支持下拉刷新的VIEW
 * Created by linliangbin on 2017/9/19 13:49.
 */

public abstract class PullToRefreshView extends LinearLayout {


    /**
     * 正在下拉的状态，这时些时放手会刷新的数据
     */
    protected final static int RELEASE_TO_REFRESH = 0;
    /**
     * 正在下拉的状态，但这时些时放手不会刷新的数据
     */
    protected final static int PULL_TO_REFRESH = 1;
    /**
     * 下拉放手后，正在加载数据的状态
     */
    protected final static int REFRESHING = 2;
    protected final static int DONE = 3;
    protected final static int TURN_PAGE_LOADDING = 4;

    protected final static int RATIO = 2;

    protected Context mContext = null;
    protected boolean mIsBack = false;
    protected boolean mIsRecored = false;
    protected int mHeadContentWidth = 0;
    protected int mHeadContentHeight = 0;
    protected int mStartY = 0;
    protected float mLastMotionX = 0;
    protected float mLastMotionY = 0;

    protected int mRefreshState = DONE;
    protected boolean mIsPullAble = true;

    protected LayoutInflater mInflater = null;
    protected PullToRefreshAbsHeader mHeadView = null;

    protected P2RListViewStateListener mListener = null;


    float Mm[] = new float[12];
    private OnPullRateListen mOnPullRateListen = null;

    public PullToRefreshView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context);
    }

    public static void setListViewOverScrollModeNever(Object obj) {
        int level = TelephoneUtil.getApiLevel();
        if (level >= 9) {
            // 2.3以上版本反射实现 mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            try {
                Class cls = obj.getClass();
                Method method = cls
                        .getMethod("setOverScrollMode", Integer.TYPE);
                method.invoke(obj, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setHeaderView(PullToRefreshAbsHeader view) {
        this.mHeadView = view;
        initHeaderView();
    }

    public void initHeaderView() {

        if (mHeadView == null) {
            return;
        }
        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadContentWidth = mHeadView.getMeasuredWidth();

        mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
        mHeadView.invalidate();

    }

    protected void init(Context context) {

        mInflater = LayoutInflater.from(context);


        mRefreshState = DONE;
        setListViewOverScrollModeNever(this);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (true == mIsPullAble) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    onTouchDownByList(event);
                }
                break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    onTouchUpByList(event);
                }
                break;

                case MotionEvent.ACTION_MOVE: {
                    onTouchMoveByList(event);
                }
                break;

                default: {

                }
                break;
            }
        }

        return super.dispatchTouchEvent(event);
    }


    public void setListener(P2RListViewStateListener listener) {

        mListener = listener;
    }

    /**
     * 设置是否可下拉刷新
     *
     * @param enable
     */
    public void setPullEnable(boolean enable) {

        mIsPullAble = enable;
    }


    public void performRefreshingState() {
        if (mHeadView != null) {
            mHeadView.setPadding(0, 0, 0, 0);
            mHeadView.changeRefreshingState();
        }
    }

    //String a[]={"下拉到刷新位置","刚刚下拉","正在刷新","刷新完成"};
    protected void changeHeaderViewState() {
        switch (mRefreshState) {
            case RELEASE_TO_REFRESH: {
                if (mHeadView != null)
                    mHeadView.changeReleaserToRefreshState();
            }
            break;

            case PULL_TO_REFRESH: {
                if (mHeadView != null)
                    mHeadView.changePullToRefreshState(mIsBack);
                mIsBack = false;
            }
            break;

            case REFRESHING: {
                if (mHeadView != null) {
                    mHeadView.setPadding(0, 0, 0, 0);
                    mHeadView.changeRefreshingState();
                }

            }
            break;

            case DONE: {
                if (mHeadView != null) {
                    mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
                    mHeadView.changeDoneState();
                }

            }
            break;

        }
    }

    protected void measureView(View child) {

        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (null == params) {

            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
                params.width);
        int lpHeight = params.height;
        int childHeightSpec = 0;
        if (lpHeight > 0) {

            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {

            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }

        child.measure(childWidthSpec, childHeightSpec);
    }

    public void onLoadingComplete() {

        mRefreshState = DONE;
        if (mHeadView != null) {
            mHeadView.setRefreshTime();
        }
        changeHeaderViewState();
        onRecover();
    }

    public void setRecover() {

        mRefreshState = DONE;
        changeHeaderViewState();
        onRecover();
    }

    public void setRefreshTime() {
        if (mHeadView != null)
            mHeadView.setRefreshTime();
    }

    protected void onPull() {

        if (null != mListener) {

            mListener.onStateChanged(P2RListViewStateListener.STATE_PULL);
        }
    }

    protected void onLoading() {

        if (null != mListener) {
            mListener.onStateChanged(P2RListViewStateListener.STATE_LOADING);
        }
    }

    protected void onRecover() {

        if (null != mListener) {

            mListener.onStateChanged(P2RListViewStateListener.STATE_RECOVER);
        }
    }

    protected void onTouchDownByList(MotionEvent event) {

        mLastMotionX = event.getX();
        mLastMotionY = event.getY();
    }

    protected void onTouchUpByList(MotionEvent event) {
        if (mOnPullRateListen != null) mOnPullRateListen.OnPullRate(0);
        if (REFRESHING != mRefreshState) {

            switch (mRefreshState) {
                case DONE: {
                    onRecover();
                }
                break;

                case PULL_TO_REFRESH: {
                    mRefreshState = DONE;
                    changeHeaderViewState();
                    onRecover();
                }
                break;

                case RELEASE_TO_REFRESH: {
                    mRefreshState = REFRESHING;
                    changeHeaderViewState();
                    onLoading();
                }
                break;

                default: {

                }
                break;
            }
        }

        mIsRecored = false;
        mIsBack = false;
    }

    protected void onTouchMoveByList(MotionEvent event) {

        int tempX = (int) event.getX();
        int tempY = (int) event.getY();

        if (false == mIsRecored) {
            final int xDiff = (int) Math.abs(tempX - mLastMotionX);
            final int yDiff = (int) Math.abs(tempY - mLastMotionY);
            if (yDiff > xDiff) {
                mIsRecored = true;
                mStartY = tempY;
            }

        }

        mLastMotionX = event.getX();
        mLastMotionY = event.getY();

        if (REFRESHING != mRefreshState && true == mIsRecored) {

            if (RELEASE_TO_REFRESH == mRefreshState) {

                if (((tempY - mStartY) / RATIO < mHeadContentHeight)
                        && (tempY - mStartY) > 0) {

                    mRefreshState = PULL_TO_REFRESH;
                    changeHeaderViewState();
                } else if (tempY - mStartY <= 0) {

                    mRefreshState = DONE;
                    changeHeaderViewState();
                }
            }

            if (PULL_TO_REFRESH == mRefreshState) {

                //下拉到一定高度，转变为重新加载的状态
                if ((tempY - mStartY) / RATIO >= mHeadContentHeight) {

                    mRefreshState = RELEASE_TO_REFRESH;
                    mIsBack = true;
                    changeHeaderViewState();
                } else if (tempY - mStartY <= 0) {
                    //下接小于0时，转变为初始状态
                    mRefreshState = DONE;
                    changeHeaderViewState();
                }
            }

            if (DONE == mRefreshState) {
                if (tempY - mStartY > 0) {

                    mRefreshState = PULL_TO_REFRESH;
                    changeHeaderViewState();
                    onPull();
                }
            }

            if (PULL_TO_REFRESH == mRefreshState) {
                mHeadView.setPadding(0, -1 * mHeadContentHeight
                        + (tempY - mStartY) / RATIO, 0, 0);

                if (mOnPullRateListen != null) {
                    float rate = ((tempY - mStartY)) / (1.0f * mHeadContentHeight);
                    mOnPullRateListen.OnPullRate(rate);
                }
            }

            if (RELEASE_TO_REFRESH == mRefreshState) {
                mHeadView.setPadding(0, (tempY - mStartY) / RATIO
                        - mHeadContentHeight, 0, 0);
                if (mOnPullRateListen != null) {
                    float rate = ((tempY - mStartY)) / (1.0f * mHeadContentHeight);
                    mOnPullRateListen.OnPullRate(rate);
                }
            }
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 通过代码调用这个函数，强制下拉刷新
     */
    public void forcedoRefreshRequest() {
        mRefreshState = REFRESHING;
        changeHeaderViewState();
        onLoading();
    }

    public interface P2RListViewStateListener {

        public final static int STATE_PULL = 0;
        public final static int STATE_LOADING = 1;
        public final static int STATE_RECOVER = 2;

        public void onStateChanged(int state);
    }


    /**
     * 下拉操作完成的比例，范围从0-n(N上限应该是可下拉的屏幕/刷新显示条的高度/2)
     */
    public static interface OnPullRateListen {
        public void OnPullRate(float rate);
    }
}
