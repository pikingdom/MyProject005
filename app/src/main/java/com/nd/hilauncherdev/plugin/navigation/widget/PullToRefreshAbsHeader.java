package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linliangbin on 16-5-11.
 */
public abstract class PullToRefreshAbsHeader extends LinearLayout implements PullToRefreshHeaderInterface{

    public Context mContext;

    public PullToRefreshAbsHeader(Context context) {
        super(context);
        mContext = context;
    }

    public PullToRefreshAbsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

}
