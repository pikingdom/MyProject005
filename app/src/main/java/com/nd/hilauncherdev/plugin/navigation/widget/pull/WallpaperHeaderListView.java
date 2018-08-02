package com.nd.hilauncherdev.plugin.navigation.widget.pull;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayer;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.combine.VideoNavigationView;
import com.nd.hilauncherdev.plugin.navigation.widget.search.HeaderSearchLayout;

/**
 * Created by linliangbin on 2017/4/28 11:20.
 */

public class WallpaperHeaderListView extends ListView implements AbsListView.OnScrollListener {


    /**
     * 当前是否为下拉动作
     */
    public static final int PULL_DOWN = 1;
    public static final int PULL_UP = -1;
    public static final int PULL_NOT_INIT = 0;
    private static final float DRAG_RATE = .5f;
    private static final int DRAG_MAX_DISTANCE = 120;
    protected float mLastMotionX = 0;
    protected float mLastMotionY = 0;
    private int distanceY = 0;
    private int downX;
    private int downY;
    private int mTotalDragDistance;
    private int mPullState = PULL_NOT_INIT;
    /**
     * 是否在第一个ITEM可见时下发
     */
    private boolean isDownWhenFirstVisible = false;
    /**
     * 父控件
     */
    private VideoNavigationView container;


    /**
     * 滚动事件的外部回调
     */
    private OnScrollListener outterLisenter;
    private ListViewScroll listViewScroll;

    public void setTouchCallback(TouchCallback touchCallback) {
        this.touchCallback = touchCallback;
    }

    private TouchCallback touchCallback;

    public WallpaperHeaderListView(Context context) {
        super(context);
    }

    public WallpaperHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOutterScrollLisenter(OnScrollListener outterLisenter) {
        this.outterLisenter = outterLisenter;
    }

    public void setListViewScroll(ListViewScroll listViewScroll) {
        this.listViewScroll = listViewScroll;
    }

    public void setContainer(VideoNavigationView container) {
        this.container = container;
    }

    public void init() {
        mTotalDragDistance = ScreenUtil.dip2px(getContext(), DRAG_MAX_DISTANCE);
        setOnScrollListener(this);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        handlerTouch(ev);
//        boolean result = super.onTouchEvent(ev);
//        return result;
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        handlerTouch(event);
        return super.dispatchTouchEvent(event);
    }

    private void handlerTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                onTouchDown(event);
            }
            break;

            case MotionEvent.ACTION_UP: {
                onTouchUp(event);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                onTouchMove(event);
            }
            break;
        }
        if(touchCallback != null){
            touchCallback.handleTouchEvent();
        }

    }

    protected void onTouchDown(MotionEvent event) {

        mLastMotionX = event.getX();
        mLastMotionY = event.getY();

        downX = (int) event.getX();
        downY = (int) event.getY();

        mPullState = PULL_NOT_INIT;

        if (getFirstVisiblePosition() == 0) {
            isDownWhenFirstVisible = true;
        } else {
            isDownWhenFirstVisible = false;
        }

    }

    protected void onTouchUp(MotionEvent event) {
        mPullState = event.getY() - downY > 0 ? PULL_DOWN : PULL_UP;
        if (mPullState == PULL_DOWN && isDownWhenFirstVisible) {
            if (distanceY >= ScreenUtil.dip2px(getContext(), 100)) {
                startHideAnimation();
            } else {
                startShowAnimation();
            }
        } else if (mPullState == PULL_UP) {
            if (container != null && container.getCurrentRate() != HeaderSearchLayout.SCROLL_RATE_END && TelephoneUtil.getApiLevel() >= 11) {
                this.post(new Runnable() {
                              @Override
                              public void run() {
                                  WallpaperHeaderListView.this.smoothScrollToPositionFromTop(0, 0, 200);
                              }
                          }
                );
            }

        }
        mPullState = PULL_NOT_INIT;
    }

    protected void onTouchMove(MotionEvent event) {


        if (!isDownWhenFirstVisible) {
            return;
        }

        if (mPullState == PULL_NOT_INIT) {
            mPullState = event.getY() - downY > 0 ? PULL_DOWN : PULL_UP;
        }


        if (mPullState == PULL_DOWN) {
            float diffY = (event.getY() - downY);
            int targetDistance = calculateDistanceY(diffY);
            if (targetDistance > 0) {
                onUpdatePullPosition(targetDistance);
            }
        }
        mLastMotionX = event.getX();
        mLastMotionY = event.getY();

    }

    private int calculateDistanceY(float diffY) {

        float scrollTop = diffY * DRAG_RATE;
        float dragPercent = scrollTop / mTotalDragDistance;
        if (dragPercent >= 0) {
            float boundedDragPercent = Math.min(1f, Math.abs(dragPercent));
            float extraOs = Math.abs(scrollTop) - mTotalDragDistance;
            float slingShotDist = mTotalDragDistance;
            float tersionSlingShotPercent = Math.max(0, Math.min(extraOs, slingShotDist * 2) / slingShotDist);
            float tensionPercent = (float) ((tersionSlingShotPercent / 4 - Math.pow(tersionSlingShotPercent / 4, 2)) * 2f);
            float extraMove = slingShotDist * tensionPercent / 2;
            int targetY = (int) (slingShotDist * boundedDragPercent + extraMove);
            return targetY;
        }
        return 0;
    }

    public void startHideAnimation() {
//        Log.i("llbeing", "startShowAnimation");
        startAnimator(true, ScreenUtil.getCurrentScreenHeight(getContext()));
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_WALLPAPER, "jr");

    }

    public void startShowAnimation() {
//        Log.i("llbeing", "startHideAnimation");
        startAnimator(false, 0);

    }

    private void startAnimator(final boolean setVisible, int dstValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distanceY, dstValue);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                onUpdatePullPosition(((Float) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (container != null && setVisible) {
                    container.setVisibility(GONE);
                    JCVideoPlayer.releaseAllVideos();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    public int getDistanceY() {
        return distanceY;
    }

    public void setDistanceY(int distanceY) {
        this.distanceY = distanceY;
        invalidate();
    }

    /**
     * @desc 移动距离变时的回调
     * @author linliangbin
     * @time 2017/4/28 14:59
     */
    public void onUpdatePullPosition(int distanceY) {

        this.distanceY = distanceY;
        if (container != null) {
            container.setNeedDispatchDraw(false);
        }
        invalidate();
//        container.invalidate();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {//因为内容要从顶部向下走，所以在这绘制偏移区域

        canvas.translate(0, distanceY);

        boolean shouldDrawMask = true;
        int sc = -1;
        if (shouldDrawMask) {
            sc = canvas.saveLayer(this.getLeft(), 0, this.getRight(), this.getBottom(), null, Canvas.ALL_SAVE_FLAG);
        }
        super.dispatchDraw(canvas);
        if (sc != -1 && shouldDrawMask) {
            canvas.restoreToCount(sc);
        }

    }


    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (listViewScroll != null) {
            listViewScroll.onScrollStateChanged(scrollState);
        }
        if (outterLisenter != null) {
            outterLisenter.onScrollStateChanged(view, scrollState);
        }
    }

    private int lastScrollY = 0;
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int scrollY = getScrollY(view);
        if(Math.abs( scrollY - lastScrollY) >=2 ){

            if (listViewScroll != null) {
                listViewScroll.onListViewScroll(scrollY);
            }
            if (outterLisenter != null) {
                outterLisenter.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            lastScrollY = scrollY;
        }


    }


    public interface ListViewScroll {
        public void onListViewScroll(int scrollY);

        public void onScrollStateChanged(int scrollState);
    }


    public interface TouchCallback {
        /** 处理touch 事件的回调 */
        public void handleTouchEvent();
    }

}
