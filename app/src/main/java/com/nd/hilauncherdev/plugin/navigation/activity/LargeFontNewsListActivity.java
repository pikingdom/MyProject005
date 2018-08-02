package com.nd.hilauncherdev.plugin.navigation.activity;

import android.os.Bundle;

import com.nd.hilauncherdev.plugin.navigation.widget.netease.NeteaseNewsPageView;

/**
 * 大文字新闻列表页 提供桌面显示调用
 * Created by linliangbin on 2017/9/12 16:40.
 */

public class LargeFontNewsListActivity extends BaseActivity {

    NeteaseNewsPageView newsPageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsPageView = new NeteaseNewsPageView(this, true);
        setContentView(newsPageView);
    }
}
