package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


/**
 * 支持横向滑屏ViewGroup
 */
public class MyPhoneViewPager extends ViewGroup implements ISlidingConflict {
    public static final int NO_DATA = -1;
    static final String TAG = "ViewPager";
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;
    protected int mTouchSlop;
    protected float mLastMotionX, mLastMotionY, mDownX, mDownY, mLastMotionRawX, mLastMotionRawy;
    private int mCurScreen;
    private int init = NO_DATA;
    private int mTouchState = TOUCH_STATE_REST;
    private boolean isFirstLayout = true;
    private ZeroViewPagerTab tab;
    private MyPhoneViewPagerTab myPhoneViewPagerTab;
    private Scroller mScroller;
    /**
     * 页面选中的回调监听
     */
    private OnPageSelectedListenner onPageSelectedListenner;
    /**
     * 达到最后一页时继续往前滑动的回调
     */
    private OnTouchedLastPageListenner onTouchedLastPageListenner;
    private VelocityTracker mVelocityTracker;
    /**
     * 子View是否支持横向滑动
     */
    private boolean isHorizontalSlidingInChild = false;
    /**
     * 子View是否循环滚动
     */
    private boolean isChildEndlessScrolling = false;
    /**
     * 支持横向滑动的子View总页数
     */
    private int childViewTotalScreen = 0;
    /**
     * 支持横向滑动的子View当前所在页
     */
    private int childViewCurrentScreen = 0;
    /**
     * 是否让子视图滑动。
     */
    private boolean isChildScrolling = true;
    private ISlidingConflict isc;
    private boolean mScrollable = true;
    /**
     * 滑动时是否需要透明度渐变
     */
    private boolean needAlphaChange = true;
    private NavigationView mNavigationView;

    public MyPhoneViewPager(Context context) {
        super(context);
        initWorkspace(context);
    }

    public MyPhoneViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWorkspace(context);
    }

    public MyPhoneViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWorkspace(context);
    }

    public void setNeedAlphaChange(boolean needAlphaChange) {
        this.needAlphaChange = needAlphaChange;
    }

    public void setSlidingParent(ISlidingConflict isc) {
        this.isc = isc;
    }

    private void initWorkspace(Context ctx) {
        mScroller = new Scroller(ctx);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (isFirstLayout) {
            if (init != NO_DATA && init >= 0 && init < getChildCount())
                scrollTo(init * width, 0);
            else
                scrollTo((getChildCount() - 1) / 2 * width, 0);

            isFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    public void snapToDestination() {
        int screenWidth = getWidth();
        int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    /**
     * 跳转到指定屏幕,带动画效果
     *
     * @param whichScreen
     */
    public void snapToScreen(int whichScreen) {
        if (whichScreen >= getChildCount() && onTouchedLastPageListenner != null) {
            //Log.e("zhou","当前页大于 child " +whichScreen);
            onTouchedLastPageListenner.onUpdate();
        }

        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            int duration = Math.abs(delta) * 2;
            if (duration > 500)
                duration = 500;

            mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
            mCurScreen = whichScreen;
            if (onPageSelectedListenner != null) {
                //Log.e("zhou", "放手时 目标页= " + whichScreen);
                onPageSelectedListenner.onUpdate(mCurScreen, false);
            }
            invalidate();
        }
    }

    /**
     * 跳转到指定屏幕,不带动画效果
     *
     * @param whichScreen
     */
    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        setPageAlpha(whichScreen * getWidth());
        scrollTo(whichScreen * getWidth(), 0);
        if (onPageSelectedListenner != null) {
            onPageSelectedListenner.onUpdate(mCurScreen, true);
        }
    }

    /**
     * 处理滑动动画
     *
     * @see View#computeScroll()
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            setPageAlpha(mScroller.getCurrX());
            if (tab != null) {
                tab.scrollHighLight(mScroller.getCurrX());
            }
            if (myPhoneViewPagerTab != null) {
                myPhoneViewPagerTab.scrollHighLight(mScroller.getCurrX());
            }
            // 判断滑动是否停止
            if (mScroller.isFinished()) {
                // 停止时更新选中的Tab标签
                if (tab != null) {
                    tab.updateSelected();
                }
                if (myPhoneViewPagerTab != null) {
                    myPhoneViewPagerTab.updateSelected();
                }
                if (onPageSelectedListenner != null)
                    //Log.e("zhou","滑动停止 page="+mCurScreen);
                    onPageSelectedListenner.onUpdate(mCurScreen, true);
            }
        }
    }

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mScrollable) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    setToScreen(mCurScreen);
                }
                mLastMotionX = x;
                mLastMotionY = y;
                mLastMotionRawX = event.getRawX();
                mLastMotionRawy = event.getRawY();
                mDownX = x;
                mDownY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (mLastMotionX - x);

                mLastMotionX = x;
                mLastMotionY = y;


                mLastMotionRawX = event.getRawX();
                mLastMotionRawy = event.getRawY();
                if (isChildScrolling) {
                    scrollBy(deltaX, 0);
                    setPageAlpha(getScrollX());
                    if (tab != null) {
                        tab.scrollHighLight(this.getScrollX());
                    }
                    if (myPhoneViewPagerTab != null) {
                        myPhoneViewPagerTab.scrollHighLight(this.getScrollX());
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);

                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                    if (isChildScrolling) {
                        snapToScreen(mCurScreen - 1);
                    }
                } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
                    if (isChildScrolling) {
                        snapToScreen(mCurScreen + 1);
                    }
                } else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                mDownX = 0;
                mDownY = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            return false;
        }

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST) && !isHorizontalSlidingInChild) {
            return true;
        }

        final float x = ev.getRawX();
        final float y = ev.getRawY();
        final int xDiff = (int) Math.abs(mLastMotionX - x);
        final int yDiff = (int) Math.abs(mLastMotionY - y);
//        Log.i("llbeing", "MyPhoneViewPager:onInterceptTouchEvent:" + ",DIFF:" + xDiff + "," + yDiff);

        switch (action) {
            case MotionEvent.ACTION_MOVE:

                View view = getChildAt(getCurrentScreen());
                if (view instanceof InternalTouchHandler && ((InternalTouchHandler) view).innerHandleTouch(ev)) {
//                    Log.i("llbeing", "MyPhoneViewPager:onInterceptTouchEvent:innerHandleTouch:FALSE");
                    mTouchState = TOUCH_STATE_REST;
                } else if (xDiff > mTouchSlop && yDiff < mTouchSlop && xDiff > yDiff) {
//                    Log.i("llbeing", "MyPhoneViewPager:onInterceptTouchEvent:innerHandleTouch:true:" + xDiff + "," + yDiff);
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;


                mLastMotionRawX = ev.getRawX();
                mLastMotionRawy = ev.getRawY();

                mDownX = x;
                mDownY = y;
                clearChildSlidingViewState();
                if (isc != null) {
                    isc.setChildSlidingViewState(true, false, getChildCount(), mCurScreen);
                }
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        boolean b = mTouchState != TOUCH_STATE_REST;
        boolean result = b;
        if (b) {
            final int xDiff2 = (int) (mLastMotionX - x);
//            Log.i("llbeing", "MyPhoneViewPager:xdiff2:" + xDiff2);
            if (isHorizontalSlidingInChild && Math.abs(xDiff2) > mTouchSlop) {
                if (isChildEndlessScrolling) {
                    result = false;
                }
                if (xDiff2 < 0 && childViewCurrentScreen == 0) {
                    result = true;
                } else if (xDiff2 > 0 && childViewCurrentScreen == childViewTotalScreen - 1) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                return false;
            }
        }

//        Log.i("llbeing", "MyPhoneViewPager:onInterceptTouchEvent:" + result + "," + ev.getAction() + "," + ev.getX() + "," + ev.getY());
        return result;
    }

    public void setTab(ZeroViewPagerTab tab) {
        this.tab = tab;
    }

    public void setTab(MyPhoneViewPagerTab tab) {
        this.myPhoneViewPagerTab = tab;
    }

    public void setInitTab(int tab) {
        this.init = tab;
        mCurScreen = tab;
    }

    /**
     * 获得当前屏幕序号
     */
    public int getCurrentScreen() {
        return mCurScreen;
    }

    /**
     * 加入页面选中时的回调接口
     *
     * @param onPageSelectedListenner
     */
    public void setOnPageSelectedListenner(OnPageSelectedListenner onPageSelectedListenner) {
        this.onPageSelectedListenner = onPageSelectedListenner;
    }

    /**
     * 加入达到最后一页时继续往前滑动的回调接口
     *
     * @param onTouchedLastPageListenner
     */
    public void setOnTouchedLastPageListenner(OnTouchedLastPageListenner onTouchedLastPageListenner) {
        this.onTouchedLastPageListenner = onTouchedLastPageListenner;
    }

    @Override
    public void setChildSlidingViewState(boolean isHorizontalSlidingInChild, boolean isChildEndlessScrolling,
                                         int childViewTotalScreen, int childViewCurrentScreen) {
        if (childViewTotalScreen <= 0 || childViewCurrentScreen < 0 || childViewCurrentScreen >= childViewTotalScreen) {
            return;
        }
        this.isHorizontalSlidingInChild = isHorizontalSlidingInChild;
        this.isChildEndlessScrolling = isChildEndlessScrolling;
        this.childViewTotalScreen = childViewTotalScreen;
        this.childViewCurrentScreen = childViewCurrentScreen;
    }

    @Override
    public void clearChildSlidingViewState() {
        this.isHorizontalSlidingInChild = false;
        this.isChildEndlessScrolling = false;
        this.childViewTotalScreen = 0;
        this.childViewCurrentScreen = 0;
    }

    public void setChildScrolling(boolean isChildScrolling) {
        this.isChildScrolling = isChildScrolling;
    }

    public void setNavigationView(NavigationView navigationView) {
        mNavigationView = navigationView;
    }

    /***/
    public void setPageAlpha(int scroll) {
        if (!needAlphaChange) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 14 && getChildCount() > 1) {
            float alpha = scroll * 1.0f / getWidth();
            alpha = 1 - alpha;
            alpha = alpha < 0 ? 0 : alpha;
            alpha = alpha > 1 ? 1 : alpha;
            View child = getChildAt(0);
            if (child != null) {
                child.setAlpha(alpha);
            }
            if (getChildCount() < 2) {
                return;
            }
            child = getChildAt(1);
            if (child != null) {
                child.setAlpha(1 - alpha);
            }
        }
    }

    /**
     * 页面选中的回调接口
     */
    public interface OnPageSelectedListenner {
        /**
         * 更新时回调
         */
        void onUpdate(int selectedPage, boolean finish);
    }

    /**
     * 达到最后一页时继续往前滑动的回调
     */
    public interface OnTouchedLastPageListenner {
        /**
         * 更新时回调
         */
        void onUpdate();
    }


    /**
     * 子view 处理事件
     *
     * @author linliangbin
     * @desc
     * @time 2017/9/15 19:09
     */
    public interface InternalTouchHandler {

        /**
         * @desc 子viewpager 内部是否处理该touch事件
         * @author linliangbin
         * @time 2017/10/10 11:42
         */
        public boolean innerHandleTouch(MotionEvent event);

        /**
         * @desc 获取当前页码
         * @author linliangbin
         * @time 2017/10/10 11:41
         */
        public int getCurrentPageIndex();

        /**
         * @desc 获取总页数
         * @author linliangbin
         * @time 2017/10/10 11:42
         */
        public int getPageCount();
    }

}
