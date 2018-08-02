package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayer;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayerManager;
import com.nd.hilauncherdev.plugin.navigation.video.JCVideoPlayerStandard;

/**
 * Created by linliangbin on 2017/6/6 19:35.
 */

public class FullScreenVideoPlayerActivity extends BaseActivity implements JCVideoPlayerStandard.VideoPlayCallback {


    public static final int FULLSCREEN_ID = R.id.video_fullscreen_window;

    public static final String INTENT_TAG_URL = "video_url";
    public static final String INTENT_TAG_TITLE = "video_title";
    public static final String INTENT_TAG_THUMB = "video_thumUrl";
    public static final String INTENT_TAG_POSITION = "video_position";
    public static final String INTENT_TAG_STATE = "video_state";


    private String downloadUrl;
    private String title;
    private String thumUrl;
    private int startTime = 0;
    private int startState = JCVideoPlayer.CURRENT_STATE_NORMAL;

    /** 当前是否为全屏播放，点击全屏播放按钮时设置true, 全屏界面展示完时设置为false */
    public static boolean isStartFullScreen = false;

    public static void startFullScreenPlayer(Context context, String downloadUrl, String title, String thumbUrl,int state,int time) {
        try {

            Intent intent = new Intent(context, FullScreenVideoPlayerActivity.class);
            intent.putExtra(INTENT_TAG_URL, downloadUrl);
            intent.putExtra(INTENT_TAG_TITLE, title);
            intent.putExtra(INTENT_TAG_THUMB, thumbUrl);
            intent.putExtra(INTENT_TAG_STATE,state);
            intent.putExtra(INTENT_TAG_POSITION,time);

            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStartFullScreen = false;
        initData();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    private void initData() {
        Intent fromIntent = getIntent();
        if (fromIntent == null) {
            SystemUtil.makeShortToast(this, R.string.video_unavailable_video);
            this.finish();
        }

        downloadUrl = fromIntent.getStringExtra(INTENT_TAG_URL);
        title = fromIntent.getStringExtra(INTENT_TAG_TITLE);
        thumUrl = fromIntent.getStringExtra(INTENT_TAG_THUMB);
        startState = fromIntent.getIntExtra(INTENT_TAG_STATE,JCVideoPlayer.CURRENT_STATE_NORMAL);
        startTime = fromIntent.getIntExtra(INTENT_TAG_POSITION,0);
        if (TextUtils.isEmpty(downloadUrl)) {
            SystemUtil.makeShortToast(this, R.string.video_unavailable_video);
            this.finish();
        }

    }

    private void initView() {
        try {
//            Log.i("llbeing", "FullScreenVideoPlayerActivity:onCreate");
            JCVideoPlayerStandard jcVideoPlayer = new JCVideoPlayerStandard(this);
            jcVideoPlayer.setId(FULLSCREEN_ID);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(jcVideoPlayer, lp);
            jcVideoPlayer.setUp(false,downloadUrl, null, JCVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, new Object[]{title, thumUrl});
            jcVideoPlayer.setUiWitStateAndScreen(startState);
            jcVideoPlayer.addTextureView();
            JCVideoPlayerManager.setSecondFloor(jcVideoPlayer);
            jcVideoPlayer.startDismissControlViewTimer();
            jcVideoPlayer.seekToInAdvance = startTime;
            jcVideoPlayer.setVideoPlayCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onVideoPlayCompleted(int completeIndex) {
        this.finish();
    }

    @Override
    public void onVideoPlayBack() {
        JCVideoPlayer.releaseAllVideos();
        this.finish();
    }
}
