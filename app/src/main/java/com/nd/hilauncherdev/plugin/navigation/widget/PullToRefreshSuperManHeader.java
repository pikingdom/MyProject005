package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.RefreshLayout;

/**
 * Created by linliangbin on 16-5-11.
 */
public class PullToRefreshSuperManHeader extends PullToRefreshAbsHeader {

    ScaleAnimation scaleAnimation;
    TranslateAnimation translateAnimation;
    private View tailV;
    private View clothV;
    private View bodyV;
    protected TextView updateRecord;
    private LinearLayout header;
    private boolean isPlayingAnim = false;

    public PullToRefreshSuperManHeader(Context context) {
        super(context);
        init();
    }

    public PullToRefreshSuperManHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        initAnim();
        header = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.drag_drop_header, null);
        this.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tailV = header.findViewById(R.id.super_man_tail);
        clothV = header.findViewById(R.id.super_man_cloth);
        bodyV = header.findViewById(R.id.super_man_body);
        updateRecord = (TextView) header.findViewById(R.id.lastUpdateTime);

    }

    private void initAnim() {
        scaleAnimation = new ScaleAnimation(1f, 1.05f, 1f, 1.05f);
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(Integer.MAX_VALUE);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
//		clothV.startAnimation(scaleAnimation);
        translateAnimation = new TranslateAnimation(1f, 1f, 0.1f, 10f);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(false);
        translateAnimation.setRepeatCount(Integer.MAX_VALUE);
        translateAnimation.setRepeatMode(Animation.REVERSE);
//		header.startAnimation(translateAnimation);
    }

    private void startRefreshAnim() {
        isPlayingAnim = true;
        if (scaleAnimation != null && clothV != null) {
            clothV.startAnimation(scaleAnimation);
        }
        if (translateAnimation != null && header != null) {
            header.startAnimation(translateAnimation);
        }
    }

    private void stopRefreshAnim() {
        if (clothV != null) {
            clothV.clearAnimation();
        }
        if (header != null) {
            header.clearAnimation();
        }
        isPlayingAnim = false;
    }


    @Override
    public void changeReleaserToRefreshState() {
        startRefreshAnim();
    }

    @Override
    public void changePullToRefreshState(boolean mIsBack) {
        stopRefreshAnim();
        setRefreshTime();
    }

    @Override
    public void changeRefreshingState() {
        startRefreshAnim();
        updateRecord.setText(R.string.release_to_refresh);
    }

    @Override
    public void changeDoneState() {
        stopRefreshAnim();
    }

    @Override
    public void setAdapter() {

    }

    @Override
    public void setRefreshTime() {
        RefreshLayout.updateTimeText(mContext, updateRecord);
    }
}
