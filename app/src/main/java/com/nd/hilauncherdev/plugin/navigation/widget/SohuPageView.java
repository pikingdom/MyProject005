package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.sohu.android.plugin.internal.SHPluginMananger;
import com.sohu.news.mp.newssdk.NewsSDKScreenFragment;

/**
 * Created by linliangbin on 16-5-26.
 */
public class SohuPageView extends RelativeLayout {

    private static final int MSG_SHOW_TIPS = 100;
    //是否已经展示过关闭搜狐提示框
    private static final String KEY_HAS_SHOW_CLOSE_SOHU_TIPS = "has_show_close_sohu";
    Context mContext;
    Handler showSohuTipsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SHOW_TIPS) {
                try {
                    Intent i = new Intent();
                    i.setClassName(NavigationView2.activity, "com.nd.hilauncherdev.launcher.navigation.SohuCloseTipsActivity");
                    NavigationView2.activity.startActivity(i);
                    setHasShowCloseSohuTips(mContext, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private NewsSDKScreenFragment fragment;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {

                fragment = NewsSDKScreenFragment.instantiateScreenFragment(NavigationView2.activity);
                NavigationView2.activity.getFragmentManager().beginTransaction().add(R.id.scrrenContainer, fragment).commit();
                fragment.setChannelsVisible(true);

            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };
    /**
     * 是否为透明背景
     */
    private boolean needTransparentBg = false;


    public SohuPageView(Context context) {
        super(context);
        mContext = context;
    }


    public SohuPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * 搜狐新闻SDK 是否初始化成�?
     * API 14下下不执行该操作避免出现类找不到的情况
     *
     * @return
     */
    public static boolean isSohuInite() {
        try {
            if (TelephoneUtil.getApiLevel() <= 14) {
                return false;
            }
            if (SHPluginMananger.sharedInstance(NavigationView2.activity).loadPlugin("com.sohu.newsclient.lite.channel").isInited()) {
                return true;
            }
        } catch (Throwable t) {
            return false;
        }
        return false;
    }

    public void setNeedTransparentBg(boolean needTransparentBg) {
        this.needTransparentBg = needTransparentBg;
    }

    public void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.navigation_page_sohu, null);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
        try {
            if (fragment != null) {
                Log.i("llbeing", "onShowing");
                fragment.onHiddenChanged(false);
                if (!hasShowCloseSohuTips(mContext) && isSohuInite()) {
                    showCloseSohuPageTips();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    public void onLeaving() {
        try {
            if (fragment != null) {
                Log.i("llbeing", "onLeaving");
                fragment.onHiddenChanged(true);
                showSohuTipsHandler.removeMessages(MSG_SHOW_TIPS);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    private void showCloseSohuPageTips() {
        showSohuTipsHandler.sendEmptyMessageDelayed(MSG_SHOW_TIPS, 1000);

    }

    public boolean hasShowCloseSohuTips(Context mContext) {
        SharedPreferences launcherVersionSP = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return launcherVersionSP.getBoolean(KEY_HAS_SHOW_CLOSE_SOHU_TIPS, false);
    }

    public boolean setHasShowCloseSohuTips(Context mContext, boolean hasShow) {
        SharedPreferences launcherVersionSP = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return launcherVersionSP.edit().putBoolean(KEY_HAS_SHOW_CLOSE_SOHU_TIPS, hasShow).commit();
    }


}
