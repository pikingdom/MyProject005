package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by linliangbin on 16-5-11.
 */
public class PullToRefreshHeader extends PullToRefreshAbsHeader {

    protected LinearLayout mHeadView = null;
    protected ImageView mArrowImageView = null;
    protected TextView mTipsTextView = null;
    protected TextView mLastUpdatedTextView = null;
    protected ProgressBar mProgressBar = null;
    protected RotateAnimation mAnimation = null;
    protected RotateAnimation mReverseAnimation = null;



    public PullToRefreshHeader(Context context) {
        super(context);
        init();
    }

    public PullToRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public int getLayoutId() {
        return R.layout.market_overefresh_head;
    }


    public int getBackgroundColor() {
        return Color.parseColor("#fff4f4f4");
    }

    public void init() {
        mHeadView = (LinearLayout) LayoutInflater.from(mContext).inflate(getLayoutId(),
                null);

        this.setBackgroundColor(getBackgroundColor());
        addView(mHeadView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mArrowImageView = (ImageView) mHeadView
                .findViewById(R.id.market_arrowImageView);
        mArrowImageView.setMinimumWidth(70);
        mArrowImageView.setMinimumHeight(50);

        mProgressBar = (ProgressBar) mHeadView.findViewById(R.id.market_progressBar);
        mTipsTextView = (TextView) mHeadView.findViewById(R.id.market_tipsTextView);
        mLastUpdatedTextView = (TextView) mHeadView
                .findViewById(R.id.market_lastUpdatedTextView);

        mAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(250);
        mAnimation.setFillAfter(true);

        mReverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseAnimation.setInterpolator(new LinearInterpolator());
        mReverseAnimation.setDuration(200);
        mReverseAnimation.setFillAfter(true);

    }

    public void changeReleaserToRefreshState() {

        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTipsTextView.setVisibility(View.VISIBLE);
        mLastUpdatedTextView.setVisibility(View.VISIBLE);

        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mAnimation);

        mTipsTextView.setText(R.string.market_overefresh_releasetorefresh);
    }

    public void changePullToRefreshState(boolean mIsBack) {
        mProgressBar.setVisibility(View.GONE);
        mTipsTextView.setVisibility(View.VISIBLE);
        mLastUpdatedTextView.setVisibility(View.VISIBLE);

        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.VISIBLE);
        mTipsTextView.setText(R.string.market_overefresh_pulltorefresh);
        if (true == mIsBack) {
            mIsBack = false;
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mReverseAnimation);
        }
    }

    public void changeRefreshingState() {
        mHeadView.setPadding(0, 0, 0, 0);

        mProgressBar.setVisibility(View.VISIBLE);
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.GONE);

        mTipsTextView.setText(R.string.market_overefresh_loading);
        mLastUpdatedTextView.setVisibility(View.VISIBLE);
    }

    public void changeDoneState() {
//        mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);

        mProgressBar.setVisibility(View.GONE);
        mArrowImageView.clearAnimation();
        mArrowImageView.setImageResource(R.drawable.ic_pull_refresh_down);

        mTipsTextView.setText(R.string.market_overefresh_pulltorefresh);
        mLastUpdatedTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setAdapter() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String date = format.format(new Date());
        mLastUpdatedTextView.setText(mContext
                .getString(R.string.market_overefresh_update) + date);
    }

    public void setRefreshTime() {

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String date = format.format(new Date());
        mLastUpdatedTextView.setText(mContext
                .getString(R.string.market_overefresh_update) + date);
    }

}
