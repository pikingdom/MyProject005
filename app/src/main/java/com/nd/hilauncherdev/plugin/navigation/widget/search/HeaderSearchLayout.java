package com.nd.hilauncherdev.plugin.navigation.widget.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * 作为头部的搜索框
 * 显示今日运势等信息
 * Created by linliangbin on 2017/5/2 11:45.
 */

public class HeaderSearchLayout extends SearchLayout {


    /**
     * @desc 将当前滚动距离转换成滚动比例
     * @author linliangbin
     * @time 2017/5/26 11:33
     */
    public static final float SCROLL_RATE_START = 1.0f;
    public static final float SCROLL_RATE_END = 0.0f;
    View searchStartView, searchEndView;
    /**
     * 搜索框距离界面顶部的高度，需要用于计算当前滚动比例
     */
    public static int SEARCH_LAYOUT_TOP_MARGIN = 0;

    public HeaderSearchLayout(Context context) {
        super(context);
        initHeader();
    }

    public HeaderSearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeader();
    }

    private void initHeader() {


        SEARCH_LAYOUT_TOP_MARGIN = (getContext().getResources().getDimensionPixelSize(R.dimen.header_layout_top_margin)
                + getContext().getResources().getDimensionPixelSize(R.dimen.search_layout_horoscope_height));

        searchStartView = findViewById(R.id.layout_start_search);
        searchEndView = findViewById(R.id.layout_end_search);

        initDailyFortune();
    }

    private void initDailyFortune() {

        View unsetView = findViewById(R.id.layout_fortune_unset);
        View detailView = findViewById(R.id.layout_fortune_detail);


        unsetView.setVisibility(INVISIBLE);
        detailView.setVisibility(INVISIBLE);

        /** 点心桌面无神婆运势，暂时做移除处理 **/
//        TextView scoreText = (TextView) findViewById(R.id.tv_fortune_score);
//        TextView titleText = (TextView) findViewById(R.id.tv_fortune_title);
//
//        DailyFortune dailyFortune = DailyFortuneLoader.getInstance(getContext()).getCurrentDailyFortune();
//        if (dailyFortune != null && dailyFortune.isAvailable()) {
//            unsetView.setVisibility(GONE);
//            detailView.setVisibility(VISIBLE);
//            scoreText.setText(dailyFortune.luckScore + "");
//            titleText.setText(dailyFortune.luckTitle);
//
//        } else {
//            unsetView.setVisibility(VISIBLE);
//            detailView.setVisibility(GONE);
//        }

//        this.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    // com.nd.android.pandahome2/com.nd.weather.widget.UI.weather.FortuneDetailActivity
//                    Intent intent = new Intent();
//                    intent.setClassName("com.nd.android.pandahome2", "com.nd.weather.widget.UI.weather.FortuneDetailActivity");
//                    getContext().startActivity(intent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


    }

    @Override
    public int getBaseLayoutResId() {
        return R.layout.layout_videonavi_header;
    }

    public void updateLeftRightMargin(float rate) {

        searchStartView.setAlpha(rate);
        searchEndView.setAlpha(1 - rate);
    }

    public static float getScrollRateByDistance(int scrollY) {

        float rate = 1 - scrollY / (float) SEARCH_LAYOUT_TOP_MARGIN;
        if (rate >= 1.0f) {
            rate = 1.0f;
        }
        if (rate <= 0.0f) {
            rate = 0.0f;
        }
        return rate;

    }
}
