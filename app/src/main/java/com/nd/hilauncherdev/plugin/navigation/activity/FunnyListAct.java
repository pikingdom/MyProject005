package com.nd.hilauncherdev.plugin.navigation.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.FunnyListAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;

/**
 * Created by linliangbin_dian91 on 2016/4/26.
 * 趣发现卡片 活动列表页面
 */
public class FunnyListAct extends BaseActivity  {

    HeaderView headerView;
    PullToRefreshListView mListView;
    FunnyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.navigation_card_funny_list);
        headerView = (HeaderView) this.findViewById(R.id.head_view);
        headerView.setTitle("趣发现");
        headerView.setGoBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        //趣发现列表
        mListView = (PullToRefreshListView) this.findViewById(R.id.listview);
        mListView.setPullEnable(false);
        mListView.disableBannerHeader();
        mAdapter = new FunnyListAdapter(this, mListView, "");
        mAdapter.refreshRequest();
    }
}
