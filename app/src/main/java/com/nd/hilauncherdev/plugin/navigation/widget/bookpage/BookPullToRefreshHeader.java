package com.nd.hilauncherdev.plugin.navigation.widget.bookpage;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshHeader;

/**
 * Created by linliangbin on 2017/8/24 18:58.
 */

public class BookPullToRefreshHeader extends PullToRefreshHeader {

    public BookPullToRefreshHeader(Context context) {
        super(context);
    }

    public BookPullToRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.bookpage_pull_to_refresh_head;
    }

    @Override
    public int getBackgroundColor() {
        return Color.parseColor("#ffffff");
    }
}
