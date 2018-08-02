package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.CardAddActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

/**
 * 刷新和添加卡片布局类
 * Created by linliangbin on 2017/8/9 20:43.
 */

public class RefreshLayout extends RelativeLayout implements View.OnClickListener {

    //操作栏刷新时间显示
    TextView optionalBarTimeText;
    /**
     * 当前是否在播放旋转刷新动画
     */
    private boolean isPlayingAnim = false;
    private Animation rotateAnimation = null;
    /**
     * 当前是否在通过操作栏刷新
     */
    private boolean isRefreshing = false;

    public void setNavigationView2(NavigationView2 navigationView2) {
        this.navigationView2 = navigationView2;
    }

    private NavigationView2 navigationView2;


    public RefreshLayout(Context context) {
        super(context);
        init();
    }


    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化下拉刷新和添加卡片控制条
     * 下拉刷新动作显示：下拉即可刷新
     * 点击刷新动作显示：点击即可刷新
     * 默认显示下拉即可刷新
     */
    public static void updateTimeText(Context context, TextView textView) {
        updateTimeText(context, textView, "下拉即可更新");
    }

    public static void updateTimeText(Context context, TextView textView, String defaultText) {
        long lastTime = CardManager.getInstance().getRefreshTime(context);
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastTime;
        duration = duration / (1000 * 60);
        String text = "";
        if (duration == 0) {
            text = 1 + "分钟前刷新";
        } else if (duration > 0 && duration < 60) {
            text = (duration + "分钟前刷新");
        } else if (duration >= 60 && duration < 60 * 24) {
            text = (duration / 60 + "小时前刷新");
        } else {
            text = defaultText;
        }
        if (textView != null && textView.getVisibility() == VISIBLE)
            textView.setText(text);

    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public void updateTime() {
        updateTimeText(getContext(), optionalBarTimeText, "点击即可刷新");
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.navigation_refresh_bar, this);
        findViewById(R.id.refresh_layout).setOnClickListener(this);
        findViewById(R.id.card_add_layout).setOnClickListener(this);
        initOptionBar();
    }

    /**
     * 初始化工具条布局（刷新时间&添加卡片）
     */
    private void initOptionBar() {
        optionalBarTimeText = (TextView) findViewById(R.id.refresh_bar_refresh_time_text);
        updateTimeText(getContext(), optionalBarTimeText, "点击即可刷新");
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

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.refresh_layout:
                    final ImageView progressBar = (ImageView) findViewById(R.id.refresh_progress);
                    if (!isPlayingAnim) {
                        progressBar.startAnimation(rotateAnimation);
                    }
                    if (!isRefreshing) {
                        isRefreshing = true;
                        if(navigationView2 != null){
                            new RefreshAsyncTask(getContext(),navigationView2).execute();
                        }
                    } else {
                        Toast.makeText(getContext(), "刷新中,请稍等....", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.card_add_layout:
                    try {
                        Intent intent = new Intent(getContext(), CardAddActivity.class);
                        intent.putExtra("is_show_manage_btn", true);
                        getContext().startActivity(intent);
                        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARDS_PULL_REFRESH, "xtj");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
