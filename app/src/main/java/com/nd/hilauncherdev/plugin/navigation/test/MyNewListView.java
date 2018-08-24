package com.nd.hilauncherdev.plugin.navigation.test;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.nd.hilauncherdev.plugin.navigation.widget.common.GoTopListView;

/**
 * Created by Administrator on 2018/8/24.
 */

public class MyNewListView extends GoTopListView {

    public MyNewListView(Context context) {
        this(context,null);
    }

    public MyNewListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.BLUE);
    }
}
