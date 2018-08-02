package com.nd.hilauncherdev.plugin.navigation.widget.search;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * 固定显示的搜索条
 * Created by linliangbin on 2017/5/26 11:40.
 */

public class PinnedSearchLayout extends SearchLayout {


    private SeachActionListener listener;
    private boolean isPlayingAnim = false;

    public PinnedSearchLayout(Context context) {
        super(context);
        initRefresh();
    }

    public PinnedSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRefresh();
    }

    private void initRefresh(){

        rotateAnimation = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2000);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPlayingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPlayingAnim = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onEndAnimation(){
        rotateAnimation.cancel();
        findViewById(R.id.navi_qrcode).clearAnimation();
    }

    public void setListener(SeachActionListener listener) {
        this.listener = listener;
    }


    private Animation rotateAnimation = null;

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.navi_qrcode:
                    if(!isPlayingAnim){

                        findViewById(R.id.navi_qrcode).startAnimation(rotateAnimation);
                        if (listener != null) {
                            listener.clickRefresh();
                        }
                    }
                    return;
            }
        }
        super.onClick(v);
    }

    @Override
    public int getBaseLayoutResId() {
        return R.layout.layout_header_end_search;
    }

    @Override
    public int getTextSwitcherId() {
        return R.id.navi_search_text_switcher_end;
    }

    @Override
    public void initSeachBoxView() {
        super.initSeachBoxView(Color.parseColor("#999999"));
    }


    public interface SeachActionListener {

        /**
         * 搜索栏点击刷新回调
         */
        public void clickRefresh();
    }
}
