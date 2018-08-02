package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.widget.ListView;

/**
 * Created by linliangbin on 2017/9/12 17:00.
 */

public class LargeFontNewsListViewAdapter extends NewsListViewAdapter {

    public LargeFontNewsListViewAdapter(Context context) {
        super(context);
    }

    public LargeFontNewsListViewAdapter(Context context, ListView listView, String url) {
        super(context, listView, url);
    }

    @Override
    public int getNewsTitleTextSize() {
        return 20;
    }

    @Override
    public int getNewsDescTextSize() {
        return 17;
    }

    @Override
    public int getPublicInfoTextSize() {
        return 14;
    }
}
