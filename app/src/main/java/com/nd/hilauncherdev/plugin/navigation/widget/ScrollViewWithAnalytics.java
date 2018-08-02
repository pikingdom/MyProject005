package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * 下拉刷新ScrollView
 * Created by linliangbin_dian91 on 2015/10/14.
 */
public class ScrollViewWithAnalytics extends ScrollView {


    private LinearLayout sc;
    private LayoutInflater inflater;
    private int headerHeight;
    private int lastHeaderPadding;
    private boolean isBack;
    private int headerState = DONE;
    private RefreshCallBack callBack;
    private PullToRefreshAbsHeader header;
    static final private int RELEASE_To_REFRESH = 0;
    static final private int PULL_To_REFRESH = 1;
    static final private int REFRESHING = 2;
    static final private int DONE = 3;
    private boolean hasUpdate = false;
    private int beginY;
    private boolean isPlayingAnim = false;

    public void setListener(IOnScrollListener listener) {
        this.listener = listener;
    }

    private IOnScrollListener listener;

    public ScrollViewWithAnalytics(Context context) {
        super(context);
        init(context);
    }

    public ScrollViewWithAnalytics(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollViewWithAnalytics(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(listener != null){
            listener.onScrollChanged(this,l,t,oldl,oldt);
        }
    }

    public interface IOnScrollListener {
        void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (beginY == 0) {
                    beginY = (int) event.getRawY();
                }
                if (!hasUpdate) {
                    updateTimeText();
                    hasUpdate = true;
                }
                if ((getScrollY() == 0 || lastHeaderPadding > (-1 * headerHeight)) && headerState != REFRESHING) {
                    int interval = (int) (event.getRawY() - beginY);
                    if (interval > 0) {
                        interval = interval / 2;
                        lastHeaderPadding = interval + (-1 * headerHeight);
                        if (lastHeaderPadding <= 40)
                            header.setPadding(0, lastHeaderPadding, 0, 0);
                        else {
                            header.setPadding(0, 40, 0, lastHeaderPadding - 40);
                        }
                        if (lastHeaderPadding > 40) {
                            headerState = RELEASE_To_REFRESH;
                            if (!isBack) {
                                isBack = true;
                                changeHeaderViewByState();
                            }
                        } else {
                            headerState = PULL_To_REFRESH;
                            changeHeaderViewByState();
                        }
                    }
                } else {
                    beginY = 0;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                beginY = (int) event.getRawY();
                if (!hasUpdate) {
                    updateTimeText();
                    hasUpdate = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                beginY = 0;
                if (headerState != REFRESHING) {
                    switch (headerState) {
                        case DONE:
                            break;
                        case PULL_To_REFRESH:
                            headerState = DONE;
                            lastHeaderPadding = -1 * headerHeight;
                            header.setPadding(0, lastHeaderPadding, 0, 0);
                            changeHeaderViewByState();
                            break;
                        case RELEASE_To_REFRESH:
                            isBack = false;
                            headerState = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            break;
                        default:
                            break;
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (lastHeaderPadding > (-1 * headerHeight) && headerState != REFRESHING) {
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void init(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        sc = (LinearLayout) inflater.inflate(R.layout.widget_main_sc, null);
        header = new PullToRefreshSuperManHeader(context);
        measureView(header);
        headerHeight = header.getMeasuredHeight();
        lastHeaderPadding = (-1 * headerHeight);
        header.setPadding(0, lastHeaderPadding, 0, 0);
        header.invalidate();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        linearLayout.addView(header, 0);
        linearLayout.addView(sc, 1);
        addView(linearLayout);
    }



    private void updateTimeText(){
        if(header != null)
            header.setRefreshTime();
    }


    public void setRefreshCallBack(RefreshCallBack callBack) {
        this.callBack = callBack;
    }

    private void changeHeaderViewByState() {
        switch (headerState) {
            case PULL_To_REFRESH:
                if (isBack) {
                    isBack = false;
                }

                if(header != null)
                    header.changePullToRefreshState(false);

                break;
            case RELEASE_To_REFRESH:
                if(header != null)
                    header.changeReleaserToRefreshState();

                break;
            case REFRESHING:
                lastHeaderPadding = 0;
                header.setPadding(0, lastHeaderPadding, 0, 0);
                header.invalidate();
                if(header != null)
                    header.changeRefreshingState();

                break;
            case DONE:
                lastHeaderPadding = -1 * headerHeight;
                header.setPadding(0, lastHeaderPadding, 0, 0);
                header.invalidate();
                hasUpdate = false;
                if(header != null)
                    header.changeDoneState();
                break;
            default:
                break;
        }
    }

    public void stopRefreshingByView(){
        onRefreshComplete();
    }

    public boolean isPlayAnim(){return isPlayingAnim;};

    Handler AnimCancleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onRefreshComplete();
        }
    };

    private void onRefresh() {
        new RefreshAsyncTask().execute();
        AnimCancleHandler.sendEmptyMessageDelayed(0, 10000);
    }



    private class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            if (callBack != null) {
                callBack.doInBackground();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            onRefreshComplete();
            saveCurrentTime();
            if (callBack != null) {
                callBack.complete();
            }
        }
    }

    public interface RefreshCallBack {
        public void doInBackground();

        public void complete();
    }

    public void onRefreshComplete() {
        AnimCancleHandler.removeMessages(0);
        if(headerState != DONE){
            headerState = DONE;
            changeHeaderViewByState();
        }
    }

    private void saveCurrentTime(){
        CardManager.getInstance().setRefreshTime(this.getContext(), System.currentTimeMillis());
    }
    private void measureView(View childView) {
        android.view.ViewGroup.LayoutParams p = childView.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int height = p.height;
        int childHeightSpec;
        if (height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        childView.measure(childWidthSpec, childHeightSpec);
    }




}
