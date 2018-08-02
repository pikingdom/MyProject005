package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.RefreshLayout;

/**
 * Created by linliangbin on 16-5-11.
 */
public class ListViewWithNews extends PullToRefreshListView {


    private LinearLayout sc;
    private LayoutInflater inflater;

    public ListViewWithNews(Context context) {
        super(context);
        initLayout(context);
    }

    public ListViewWithNews(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }


    @Override
    public void initPullHeader() {
        mHeadView = new PullToRefreshSuperManHeader(mContext);
    }

    private void initLayout(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sc = (LinearLayout) inflater.inflate(R.layout.widget_main_sc, null);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(sc, 0);
        addHeaderView(linearLayout);
    }

    @Override
    protected void changeHeaderViewState() {
        super.changeHeaderViewState();
        switch (mRefreshState) {
            case PULL_TO_REFRESH: {
                TextView textView = (TextView) this.findViewById(R.id.refresh_bar_refresh_time_text);
                RefreshLayout.updateTimeText(mContext, textView,"点击即可刷新");
                break;

            }
        }
    }

    public void stopRefreshingByView(){
        onRefreshComplete();
    }

    /**
     * 开始下拉刷新时启动定时停止动画消息
     */
    public void sendCancaleMsgDelay(){
        if(AnimCancleHandler != null)
            AnimCancleHandler.sendEmptyMessageDelayed(0, 10000);
    }
    Handler AnimCancleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onRefreshComplete();
        }
    };

    private void onRefreshComplete() {
        AnimCancleHandler.removeMessages(0);
        if(mRefreshState != DONE){
            mRefreshState = DONE;
            changeHeaderViewState();
        }
    }
}
