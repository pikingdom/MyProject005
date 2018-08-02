package com.nd.hilauncherdev.plugin.navigation.widget.fenghuang;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

/**
 * 凤凰新闻展示界面
 * Created by linliangbin on 16-5-26.
 */
public class FenghuangPageView extends RelativeLayout {

    Context mContext;
    private Fragment fragment;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {

                fragment = Fragment.instantiate(NavigationView2.activity, NavigationKeepForReflect.getFenghuangFragmentName_V8508());
                NavigationView2.activity.getFragmentManager().beginTransaction().add(R.id.scrrenContainer, fragment).commit();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };


    public FenghuangPageView(Context context) {
        super(context);
        mContext = context;
    }

    public FenghuangPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    public void init() {
        NavigationKeepForReflect.initFenghuangSDK_V8508(NavigationView2.activity);
        View view = LayoutInflater.from(mContext).inflate(R.layout.navigation_page_sohu, null);
        LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (!CommonGlobal.isDianxinLauncher(mContext))
            rlp.topMargin = ScreenUtil.getStatusBarHeight(mContext);
        addView(view, rlp);
    }

    /**
     * 移除fragment
     */

    public void removeFragment() {
        try {
            handler.removeMessages(0);
            if (fragment != null)
                NavigationView2.activity.getFragmentManager().beginTransaction().remove(fragment).commit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 初始化fragment
     */
    public void initFragment() {
        handler.sendEmptyMessageDelayed(0, 1000);

    }

    public void onShowing() {
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_IFENG_NEWS_PAGE, "jr");
    }

    public void onLeaving() {
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_IFENG_NEWS_PAGE, "tc");
    }

}
