package com.nd.hilauncherdev.plugin.navigation.widget.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.ZeroViewPagerTab;

/**
 * 支持置顶功能的Listview
 * Created by linliangbin on 2017/9/1 18:30.
 */

public class GoTopListView extends RelativeLayout {

    public int pageIndex;
    public ZeroViewPagerTab.TabLisener tabLisener;
    public PullToRefreshListView mListView;
    //ListView回到顶端的按钮
    View mGotoTopBt = null;

    public GoTopListView(Context context) {
        super(context);
    }

    public GoTopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getGoTopViewId() {
        return R.id.news_gototop;
    }

    public int getListViewId() {
        return R.id.listview;
    }


    public void initGoTop() {

        mListView = (PullToRefreshListView) findViewById(getListViewId());
        mGotoTopBt = findViewById(getGoTopViewId());
        mGotoTopBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelection(0);
            }
        });
    }


    public void setScrollListen(AbsListViewAdapter adapter) {

        if (adapter == null) {
            return;
        }
        if (mGotoTopBt == null) return;
        final Animation animFadein = AnimationUtils.loadAnimation(getContext(), R.anim.gototopin_ani);
        final Animation animout = AnimationUtils.loadAnimation(getContext(), R.anim.gototopout_ani);


        adapter.setScrollListener(new NewsListViewAdapter.OnExtendScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    if (mGotoTopBt.getVisibility() == INVISIBLE) {
                        Animation ani = mGotoTopBt.getAnimation();
                        if (ani != null) {
                            ani.cancel();
                        }
                        mGotoTopBt.startAnimation(animFadein);
                        mGotoTopBt.setVisibility(VISIBLE);
                    }
                    if (tabLisener != null) {
                        tabLisener.setBackgroundAlpha(255, pageIndex);
                    }
                } else {
                    if (mGotoTopBt.getVisibility() == VISIBLE) {
                        mGotoTopBt.startAnimation(animout);
                        mGotoTopBt.setVisibility(INVISIBLE);
                    }
                    View child = mListView.getChildAt(0);
                    if (tabLisener != null && child != null) {
                        int scrolly = -child.getTop() + mListView.getFirstVisiblePosition() * child.getHeight();
                        float alpha = scrolly / (child.getHeight() * 0.5f) * 255;
                        alpha = alpha > 255 ? 255 : alpha;
                        tabLisener.setBackgroundAlpha((int) alpha, pageIndex);
                        tabLisener.invalidateTab();
                    }
                }
            }
        });
    }

}
