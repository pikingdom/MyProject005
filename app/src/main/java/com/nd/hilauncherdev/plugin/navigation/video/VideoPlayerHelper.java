package com.nd.hilauncherdev.plugin.navigation.video;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.FullScreenVideoPlayerActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;

/**
 * Created by linliangbin on 2017/5/24 15:39.
 */

public class VideoPlayerHelper {

    public static final int FULLSCREEN_ID = R.id.video_fullscreen_window;
    public static final int TINY_ID = R.id.video_tiny_window;
    public static boolean isShowTiny = false;


    public static void startWindowFullscreen(JCVideoPlayer originPlayer, Context context, String downloadUrl, String title, String thumUrl,int state,int time) {

        FullScreenVideoPlayerActivity.isStartFullScreen = true;
        originPlayer.removeTexttureFromContainer();
        ViewGroup vp = (ViewGroup) (JCUtils.scanForActivity(context)).findViewById(R.id.video_navigation_container);
        View old = vp.findViewById(FULLSCREEN_ID);
        if (old != null) {
            vp.removeView(old);
        }
        FullScreenVideoPlayerActivity.startFullScreenPlayer(context,downloadUrl,title,thumUrl,state,time);
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "qp");

    }


    public static void startWindowTiny(JCVideoPlayer originPlayer, Context context, String downloadUrl, String title, String thumUrl) {
        if (isShowTiny) {
            Log.i("llbeing","startWindowTiny:already start,return");
            return;
        }
        isShowTiny = true;
        if(originPlayer != null){
            originPlayer.removeTexttureFromContainer();
            downloadUrl = originPlayer.url;
            title = (String) originPlayer.objects[0];
            thumUrl = (String) originPlayer.objects[1];
        }

        ViewGroup vp = (ViewGroup) (JCUtils.scanForActivity(context)).findViewById(R.id.video_navigation_container);
        View old = vp.findViewById(TINY_ID);
        if (old != null) {
            vp.removeView(old);
        }

        try {
            JCVideoPlayerStandard jcVideoPlayer = new JCVideoPlayerStandard(context);
            jcVideoPlayer.setId(TINY_ID);
            int width = (ScreenUtil.getCurrentScreenWidth(context) - ScreenUtil.dip2px(context,32))/2;
            int height = (int) (width * (85.0f/151));
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,height);
            if(BaseNavigationSearchView.isCurrentShowSmallAdView){
                lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
                lp.leftMargin = ScreenUtil.dip2px(context,16);
                lp.rightMargin = ScreenUtil.dip2px(context,0);
            }else{
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                lp.leftMargin = ScreenUtil.dip2px(context,0);
                lp.rightMargin = ScreenUtil.dip2px(context,16);
            }
            lp.bottomMargin = ScreenUtil.dip2px(context,16);
            vp.addView(jcVideoPlayer, lp);
            jcVideoPlayer.setUp(false,downloadUrl, null, JCVideoPlayerStandard.SCREEN_WINDOW_TINY, new Object[]{title, thumUrl});
            jcVideoPlayer.setUiWitStateAndScreen(JCVideoPlayer.CURRENT_STATE_PLAYING);
            jcVideoPlayer.addTextureView();
            if(originPlayer != null){
                JCVideoPlayerManager.setFirstFloor(originPlayer);
            }
            JCVideoPlayerManager.setSecondFloor(jcVideoPlayer);
            if(originPlayer != null){
                originPlayer.cancelProgressTimer();
            }
            PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "xc");
            Log.i("llbeing","startWindowTiny:successs");
        } catch (Exception e) {
            Log.i("llbeing","startWindowTiny:fail");
            e.printStackTrace();
        }
    }
}
