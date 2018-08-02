package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

/**
 * Created by linliangbin on 2017/8/9 21:19.
 */

public class SohuNoticeLayout extends RelativeLayout {

    private SohuNoticeListener listener;

    public SohuNoticeLayout(Context context) {
        super(context);
        init();
    }

    public SohuNoticeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setListener(SohuNoticeListener listener) {
        this.listener = listener;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.navigation_sohu_notice, this);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_ANIM_GUIDE, "kd");
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.sohu_guide_dismiss);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (listener != null) {
                            listener.onSohuNoticeEnd();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                SohuNoticeLayout.this.startAnimation(animation);


            }
        });
    }

    public interface SohuNoticeListener {
        public void onSohuNoticeEnd();
    }
}
