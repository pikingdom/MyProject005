package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * 刷新文字颜色修改的的下拉刷新头部
 * Created by linliangbin on 16-5-11.
 */
public class PullToRefreshSuperManWhiteHeader extends PullToRefreshSuperManHeader {

    public PullToRefreshSuperManWhiteHeader(Context context) {
        super(context);
        changeTextColor();
    }

    public PullToRefreshSuperManWhiteHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeTextColor();
    }

    private void changeTextColor() {
        if (updateRecord != null) {
            updateRecord.setTextColor(Color.parseColor("#ffa9a9a9"));
        }
    }

}
