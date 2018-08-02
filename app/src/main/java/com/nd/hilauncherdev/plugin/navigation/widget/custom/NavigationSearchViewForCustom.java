package com.nd.hilauncherdev.plugin.navigation.widget.custom;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.web.CustomWebViewClient;
import com.nd.hilauncherdev.plugin.navigation.widget.SubPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;


/**
 * 定制版本零屏显示-显示带零屏动画的好123界面
 * Created by linliangbin on 2017/4/10 19:27.
 */

public class NavigationSearchViewForCustom extends BaseNavigationSearchView implements View.OnTouchListener {

    private int marginTop;


    private SubPageView subPageView;

    private CustomWebViewClient customWebViewClient;


    public NavigationSearchViewForCustom(Context context, int marginTop) {
        super(context);
        this.adMarginBottom = ScreenUtil.dip2px(context, 50);
        this.marginTop = marginTop;
        initView();
    }

    public void initView() {

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, marginTop, 0, 0);
        customWebViewClient = new CustomWebViewClient(context);
        subPageView = new SubPageView(this.getContext(), CommonGlobal.URL_AITAOBAO, customWebViewClient);
        subPageView.setupWebviewTouch(this);
        customWebViewClient.setWaitingView(subPageView.getWaitingView());
        addView(subPageView, layoutParams);
        customWebViewClient.setFirstLoadUrlTime(System.currentTimeMillis());

    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
//        Log.i("llbeing", "onTouch：" + motionEvent.getAction());

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (customWebViewClient != null) {
                customWebViewClient.setHasTouchBefore(true);
            }
        }
        return false;
    }
}
