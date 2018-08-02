package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.nd.hilauncherdev.plugin.navigation.NavigationPreferences;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView;

/**
 * 主Activity<br/>
 *
 * @author chenzhihong_9101910
 */
public class MainActivity extends BaseActivity {

    private static String chid = "067QWSGmas3haz+57JZRci2FIjs1a8UV";
    NavigationView navigationView;
    private Context mContext;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (navigationView != null)
            navigationView.unregisterReceiver();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        AsyncImageLoader.initImageLoaderConfig(mContext);
        try {
            System.loadLibrary("image");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        navigationView = new NavigationView(MainActivity.this);
        navigationView.setHotWordView(null);
        navigationView.setActivity(MainActivity.this);
        //哪些Page需要展示
        boolean[] whichPageShow = {true, true, true, true, true, true, true, true};
        int showPageCount = 0;
        for (int i = 0; i < whichPageShow.length; i++) {
            if (whichPageShow[i])
                showPageCount++;
        }

        int[] pageShowSequence = {
                -1, //NavigationView.INDEX_PAGE_TAOBAO,
                NavigationPreferences.INDEX_PAGE_NEWS,
                NavigationPreferences.INDEX_PAGE_NAVIGATION,
                NavigationPreferences.INDEX_PAGE_TAOBAO_EXT,
                NavigationPreferences.INDEX_PAGE_IREADER,
                -1,
                -1,
                -1};
        navigationView.init("067QWSGmas3haz+57JZRci2FIjs1a8UV", showPageCount, whichPageShow, pageShowSequence, 1);
        navigationView.setBackgroundColor(Color.parseColor("#aa000000"));
        setContentView(navigationView);

        navigationView.initSohuFragment();
        Global.runInMainThread(new Runnable() {
            @Override
            public void run() {
                navigationView.upgradePlugin("www.baidu.com", 11, false);
//				navigationView.onLauncherStart();
//				navigationView.updateAndRefreshSiteDetail();
            }
        }, 5000);
        // NaviCardLoader.loadCardInfo(MainActivity.this);

    }

    @Override
    protected void onResume() {
        if (navigationView != null) {
            navigationView.onShow();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (navigationView != null) {
            navigationView.handleNavigationWhenLauncherOnPause();
        }
    }

}
