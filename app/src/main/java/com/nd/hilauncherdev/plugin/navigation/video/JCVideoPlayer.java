package com.nd.hilauncherdev.plugin.navigation.video;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.DialogCommon;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView;
import com.nd.hilauncherdev.plugin.navigation.widget.search.SearchLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.nd.hilauncherdev.plugin.navigation.video.VideoPlayerHelper.FULLSCREEN_ID;

/**
 * Created by Nathen on 16/7/30.
 */
public abstract class JCVideoPlayer extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    public static final String TAG = "JieCaoVideoPlayer";
    public static final int TINY_ID = R.id.video_tiny_window;
    public static final int SCREEN_LAYOUT_NORMAL = 0;
    public static final int SCREEN_LAYOUT_LIST = 1;
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;
    public static final int SCREEN_WINDOW_TINY = 3;
    public static final int CURRENT_STATE_NORMAL = 0;
    public static final int CURRENT_STATE_PREPARING = 1;
    public static final int CURRENT_STATE_PLAYING = 2;
    public static final int CURRENT_STATE_PLAYING_BUFFERING_START = 3;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    public static final int CURRENT_STATE_ERROR = 7;
    public static int BACKUP_PLAYING_BUFFERING_STATE = -1;


    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    releaseAllVideos();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    JCMediaManager.instance().pausePlaying();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        }
    };

    protected static Timer UPDATE_PROGRESS_TIMER;
    public int currentState = -1;
    public int currentScreen = -1;
    public boolean loop = false;
    public Map<String, String> headData;
    public String url = "";
    public AdvertSDKManager.AdvertInfo advertInfo = null;
    public boolean isOnFlipping = false;
    public Object[] objects = null;
    public int seekToInAdvance = 0;
    public ImageView startButton;
    public TextView adDurationText;
    public SeekBar progressBar;
    public ImageView fullscreenButton;
    public TextView currentTimeTextView, totalTimeTextView;
    public ViewGroup textureViewContainer;
    public ViewGroup topContainer, bottomContainer;
    public int widthRatio = 16;
    public int heightRatio = 9;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected AudioManager mAudioManager;
    protected Handler mHandler;
    protected ProgressTimerTask mProgressTimerTask;
    protected boolean mTouchingProgressBar;
    protected int resId = -1;
    protected int index = -1;
    /** 当前是否在播放广告 */
    protected boolean isPlayingAd = false;
    public static HashMap<String,String> playIndexMap = new HashMap<String,String>();

    public void setOuterTimeView(View outerTimeView) {
        this.outerTimeView = outerTimeView;
    }

    /** 外部布局的时间长度view */
    private View outerTimeView;

    public JCVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JCVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public static boolean backPress() {

        if (JCVideoPlayerManager.getSecondFloor() != null) {
            JCVideoPlayer jcVideoPlayer = JCVideoPlayerManager.getSecondFloor();
            if (JCVideoPlayerManager.getFirstFloor() != null) {
                JCVideoPlayerManager.getFirstFloor().currentState = jcVideoPlayer.currentState;
            }
            jcVideoPlayer.clearFloatScreen();
            return true;
        } else if (JCVideoPlayerManager.getFirstFloor() != null &&
                (JCVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN ||
                        JCVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_TINY)) {

            //直接退出全屏和小窗
            JCVideoPlayerManager.getCurrentJcvd().currentState = CURRENT_STATE_NORMAL;
            JCVideoPlayerManager.getFirstFloor().clearFloatScreen();
            JCMediaManager.instance().releaseMediaPlayer();
            JCVideoPlayerManager.setFirstFloor(null);
            return true;
        }
        return false;
    }

    public static void releaseAllVideos() {
//        Log.i("llbeing", "releaseAllVideos");
        JCVideoPlayerManager.completeAll();
        JCMediaManager.instance().releaseMediaPlayer();
        Runtime.getRuntime().gc();
    }


    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        adDurationText = (TextView) findViewById(R.id.tv_duration);
        startButton = (ImageView) findViewById(R.id.start);
        fullscreenButton = (ImageView) findViewById(R.id.fullscreen);
        progressBar = (SeekBar) findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = (TextView) findViewById(R.id.current);
        totalTimeTextView = (TextView) findViewById(R.id.total);
        bottomContainer = (ViewGroup) findViewById(R.id.layout_bottom);
        textureViewContainer = (ViewGroup) findViewById(R.id.surface_container);
        topContainer = (ViewGroup) findViewById(R.id.layout_top);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        progressBar.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**解决在viewpager 中seekbar 无法滑动问题 */
                requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler();
    }

    public void setUp(boolean isFlipping,String url, AdvertSDKManager.AdvertInfo advertInfo, int screen, Object... objects) {
        this.isOnFlipping = isFlipping;
        if (!TextUtils.isEmpty(this.url) && TextUtils.equals(this.url, url)) {
            return;
        }
        this.url = url;
        this.advertInfo = advertInfo;
        this.objects = objects;
        this.currentScreen = screen;
        this.headData = null;
        Log.i(TAG, "setUp:" + this.hashCode());
        setUiWitStateAndScreen(CURRENT_STATE_NORMAL);

    }

    private void noticeNetworkForVideoPlay() {
        final DialogCommon dlg;
        Context mContext = getContext();
        dlg = new DialogCommon(getContext(), getContext().getResources().getText(R.string.notice_network_dlg_title),
                getContext().getText(R.string.notice_network_dlg_content),
                mContext.getText(R.string.notice_network_dlg_positive),
                mContext.getText(R.string.notice_network_dlg_negative));

        dlg.setClickListener(null, new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
                CardManager.getInstance().setHasNoticeNetworkForVideo(getContext(), true);
                prepareMediaPlayer();
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbbf");
                if(!playIndexMap.containsKey(resId+"")){
                    PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbqc");
                    playIndexMap.put(resId+"","");
                }
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.setCancelable(true);
        dlg.setCanceledOnTouchOutside(true);

        dlg.show();
    }

    /**
     * @desc 判断是否要弹框提示
     * @author linliangbin
     * @time 2017/6/13 15:13
     */
    protected void startPlayWithDlg(){
        if (!CardManager.getInstance().getHasNoticeNetworkForVideo(getContext())
                &&(TelephoneUtil.isNonWifiNetworkAvailable(getContext()))) {
            noticeNetworkForVideoPlay();
        } else {
            prepareMediaPlayer();
            PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbbf");
            if(!playIndexMap.containsKey(resId+"")){
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_VIDEO_LIST, "lbqc");
                playIndexMap.put(resId+"","");
            }
        }
    }


    public void startDismissControlViewTimer(){

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.start) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR) {
                startPlayWithDlg();
            } else if (currentState == CURRENT_STATE_PLAYING) {
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                SearchLayout.forceDisableSwitcher = false;
                JCMediaManager.instance().pausePlaying();
                setUiWitStateAndScreen(CURRENT_STATE_PAUSE);
            } else if (currentState == CURRENT_STATE_PAUSE) {
                SearchLayout.forceDisableSwitcher = true;
                JCMediaManager.instance().startPlaying();
                setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
                startDismissControlViewTimer();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                prepareMediaPlayer();
            }
        } else if (i == R.id.fullscreen) {
            Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ");
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                backPress();
            } else {
                Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ");
                VideoPlayerHelper.startWindowFullscreen(this, getContext(), url, objects[0].toString(), objects[1].toString(),currentState,getCurrentPositionWhenPlaying());
            }
        } else if (i == R.id.surface_container && currentState == CURRENT_STATE_ERROR) {
            Log.i(TAG, "onClick surfaceContainer State=Error [" + this.hashCode() + "] ");
            if(!isPlayingAd){
                prepareMediaPlayer();
            }
        }
    }


    public void prepareMediaPlayer(String url) {
        SearchLayout.forceDisableSwitcher = true;
        JCVideoPlayerManager.completeAll();
//        Log.i("llbeing", "prepareMediaPlayer [" + this.hashCode() + "] " + ",url="+url);
        initTextureView();
        addTextureView();
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        JCMediaManager.CURRENT_PLAYING_URL = url;
        JCMediaManager.CURRENT_PLING_LOOP = loop;
        JCMediaManager.MAP_HEADER_DATA = headData;
        setUiWitStateAndScreen(CURRENT_STATE_PREPARING);
        JCVideoPlayerManager.setFirstFloor(this);
        JCVideoPlayerManager.setSecondFloor(null);
        VideoPlayerHelper.isShowTiny = false;
        if(outerTimeView != null){
            outerTimeView.setVisibility(INVISIBLE);
        }
        try {
            if(NavigationView2.activity != null){
                NavigationView2.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void prepareMediaPlayer() {
        SearchLayout.forceDisableSwitcher = true;
        prepareMediaPlayer(url);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void initTextureView() {
        removeTextureView();
        JCMediaManager.textureView = new JCResizeTextureView(getContext());
        JCMediaManager.textureView.setSurfaceTextureListener(JCMediaManager.instance());
    }

    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(JCMediaManager.textureView, layoutParams);
    }

    public void removeTextureView() {
        JCMediaManager.savedSurfaceTexture = null;
        if (JCMediaManager.textureView != null && JCMediaManager.textureView.getParent() != null) {
            ((ViewGroup) JCMediaManager.textureView.getParent()).removeView(JCMediaManager.textureView);
        }
    }





    public void removeTexttureFromContainer() {

        if (textureViewContainer != null && JCMediaManager.textureView != null) {
            textureViewContainer.removeView(JCMediaManager.textureView);
        }else{
//            Log.i("llbeing","removeTexttureFromContainer:null");
        }
    }

    public void setUiWitStateAndScreen(int state) {

//        Log.i("llbeing", "setUiWitStateAndScreen:" + state);
        currentState = state;
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                adDurationText.setVisibility(INVISIBLE);
                cancelProgressTimer();
//                if (isCurrentJcvd()) {//这个if是无法取代的，否则进入全屏的时候会releaseMediaPlayer
//                    JCMediaManager.instance().releaseMediaPlayer();
//                }
                break;
            case CURRENT_STATE_PREPARING:
                resetProgressAndTime();
                break;
            case CURRENT_STATE_PLAYING:
            case CURRENT_STATE_PAUSE:
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                startDismissControlViewTimer();
                break;
            case CURRENT_STATE_ERROR:
                cancelProgressTimer();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                cancelProgressTimer();
                progressBar.setProgress(100);
                currentTimeTextView.setText(totalTimeTextView.getText());
                break;
        }
    }

    public void startProgressTimer() {
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 1000);
    }

    public void cancelProgressTimer() {
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    public static void cancelTimer(){
        if(UPDATE_PROGRESS_TIMER != null){
            UPDATE_PROGRESS_TIMER.cancel();
        }
    }

    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");

        if (currentState != CURRENT_STATE_PREPARING) return;
        if (seekToInAdvance != 0) {
            JCMediaManager.instance().seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        } else {
            JCMediaManager.instance().seekTo(0);
        }
        setUiWitStateAndScreen(CURRENT_STATE_PLAYING);
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (ViewGroup) (JCUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(R.id.video_navigation_container);
        if (vp != null) {
            View oldF = vp.findViewById(FULLSCREEN_ID);
            View oldT = vp.findViewById(TINY_ID);
            if (oldF != null) {
                vp.removeView(oldF);
            }
            if (oldT != null) {
                vp.removeView(oldT);
            }
        }
    }

    public void onAutoCompletion() {
        //加上这句，避免循环播放video的时候，内存不断飙升。
        Runtime.getRuntime().gc();
//        Log.i("llbeing", "onAutoCompletion ");
        cancelProgressTimer();
        setUiWitStateAndScreen(CURRENT_STATE_AUTO_COMPLETE);

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN || currentScreen == SCREEN_WINDOW_TINY) {
            backPress();
        }
        if (JCVideoPlayerManager.getCurrentJcvd() != null) {
            JCVideoPlayerManager.getCurrentJcvd().setUiWitStateAndScreen(CURRENT_STATE_NORMAL);
        }
        JCMediaManager.savedSurfaceTexture = null;

        if(outerTimeView != null){
            outerTimeView.setVisibility(VISIBLE);
        }
    }

    public void onCompletion() {
//        Log.i("llbeing", "onCompletion " + " [" + this.hashCode() + "] ");

        cancelProgressTimer();
        setUiWitStateAndScreen(CURRENT_STATE_NORMAL);
        // 清理缓存变量
        textureViewContainer.removeView(JCMediaManager.textureView);
        JCMediaManager.instance().currentVideoWidth = 0;
        JCMediaManager.instance().currentVideoHeight = 0;

        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        clearFullscreenLayout();
        clearFloatScreen();
        JCMediaManager.savedSurfaceTexture = null;

        try {
            if (NavigationView2.activity != null) {
                NavigationView2.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //退出全屏和小窗的方法
    public void playOnThisJcvd() {
//        Log.i("llbeing", "playOnThisJcvd " + " [" + this.hashCode() + "] ");
        //1.清空全屏和小窗的jcvd
        JCVideoPlayer second = JCVideoPlayerManager.getSecondFloor();
        if (second != null) {
            currentState = second.currentState;
        }
        clearFloatScreen();
        //2.在本jcvd上播放
        setUiWitStateAndScreen(currentState);
        addTextureView();
        JCVideoPlayerManager.setFirstFloor(this);
        JCVideoPlayerManager.setSecondFloor(null);
    }

    public void clearFloatScreen() {

        JCVideoPlayer secJcvd = JCVideoPlayerManager.getCurrentJcvd();
        secJcvd.textureViewContainer.removeView(JCMediaManager.textureView);
        ViewGroup vp = (ViewGroup) (JCUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(R.id.video_navigation_container);
        if (vp != null) {
            vp.removeView(secJcvd);
        }
        JCVideoPlayerManager.setSecondFloor(null);
        VideoPlayerHelper.isShowTiny = false;
    }


    public void onSeekComplete() {

    }

    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && what != -38) {
            setUiWitStateAndScreen(CURRENT_STATE_ERROR);
            if (isCurrentJcvd()) {
                Log.i(TAG, "onError");
                JCMediaManager.instance().releaseMediaPlayer();
            }
        }
    }

    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            if (currentState == CURRENT_STATE_PLAYING_BUFFERING_START) return;
            BACKUP_PLAYING_BUFFERING_STATE = currentState;
            setUiWitStateAndScreen(CURRENT_STATE_PLAYING_BUFFERING_START);//没这个case
            Log.d(TAG, "MEDIA_INFO_BUFFERING_START");
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (BACKUP_PLAYING_BUFFERING_STATE != -1) {
                setUiWitStateAndScreen(BACKUP_PLAYING_BUFFERING_STATE);
                BACKUP_PLAYING_BUFFERING_STATE = -1;
            }
            Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
        }
    }

    public void onVideoSizeChanged() {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ");
        JCMediaManager.textureView.setVideoSize(JCMediaManager.instance().getVideoSize());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelProgressTimer();
        ViewParent vpdown = getParent();
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true);
            vpdown = vpdown.getParent();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        ViewParent vpup = getParent();
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false);
            vpup = vpup.getParent();
        }
        if (currentState != CURRENT_STATE_PLAYING &&
                currentState != CURRENT_STATE_PAUSE) return;
        int time = seekBar.getProgress() * getDuration() / 100;
        JCMediaManager.instance().seekTo(time);
    }


    public int getCurrentPositionWhenPlaying() {
        int position = 0;
        if (JCMediaManager.instance().isNullMediaPlayer())
            return position;//这行代码不应该在这，如果代码和逻辑万无一失的话，心头之恨呐
        if (currentState == CURRENT_STATE_PLAYING ||
                currentState == CURRENT_STATE_PAUSE ||
                currentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            position = JCMediaManager.instance().getCurrentPosition();
        }
        return position;
    }

    public int getDuration() {
        int duration = 0;
        if (JCMediaManager.instance().isNullMediaPlayer())
            return duration;
        duration = JCMediaManager.instance().getDuration();
        return duration;
    }



    public void setProgressAndText() {
        int position = getCurrentPositionWhenPlaying();
        int duration = getDuration();
        int progress = position * 100 / (duration == 0 ? 1 : duration);
        if (!mTouchingProgressBar) {
            if (progress != 0) progressBar.setProgress(progress);
        }
        if (position != 0) currentTimeTextView.setText(JCUtils.stringForTime(position));
        totalTimeTextView.setText(JCUtils.stringForTime(duration));
        if(isPlayingAd){
            displayAdTime(JCUtils.stringForTime(duration-position));
            reportVideoAdAnatics(progress);
        }
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) progressBar.setSecondaryProgress(bufferProgress);
    }

    public void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(JCUtils.stringForTime(0));
        totalTimeTextView.setText(JCUtils.stringForTime(0));
    }

    public void release() {
        if (url.equals(JCMediaManager.CURRENT_PLAYING_URL)) {
            //在非全屏的情况下只能backPress()
            if (JCVideoPlayerManager.getSecondFloor() != null &&
                    JCVideoPlayerManager.getSecondFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//点击全屏
            } else if (JCVideoPlayerManager.getSecondFloor() == null && JCVideoPlayerManager.getFirstFloor() != null &&
                    JCVideoPlayerManager.getFirstFloor().currentScreen == SCREEN_WINDOW_FULLSCREEN) {//直接全屏
            } else {
                Log.d(TAG, "release [" + this.hashCode() + "]");
                releaseAllVideos();
            }
        }
    }

    public boolean isCurrentJcvd() {//虽然看这个函数很不爽，但是干不掉
        return JCVideoPlayerManager.getCurrentJcvd() != null
                && JCVideoPlayerManager.getCurrentJcvd() == this;
    }


    /**
     * @desc 广告统计方法
     * @author linliangbin
     * @time 2017/6/8 19:57
     */
    protected void onClickVideoAd(){
        LauncherCaller.openUrl(getContext(),"",advertInfo.videoAction);
        AdvertSDKManager.submitVideoClickEvent(getContext(),advertInfo);
        isPlayingAd = false;
        resetAdStatus();
        CvAnalysis.submitClickEvent(NavigationView2.activity, BaseNavigationSearchView.CV_PAGE_ID, CvAnalysisConstant.NAVIGATION_SCREEN_VIDEO_LIST_VIDEO_AD,
                advertInfo.id, CvAnalysisConstant.RESTYPE_ADS);
    }

    protected void onShowVideoAd(){
//        Log.i("llbeing","onShowVideoAd:");
        AdvertSDKManager.submitVideoShowEvent(getContext(),advertInfo);
        CvAnalysis.submitShowEvent(NavigationView2.activity, BaseNavigationSearchView.CV_PAGE_ID, CvAnalysisConstant.NAVIGATION_SCREEN_VIDEO_LIST_VIDEO_AD,
                advertInfo.id, CvAnalysisConstant.RESTYPE_ADS);
    }

    protected void onPlayVideoAd(int state){
//        Log.i("llbeing","onPlayVideoAd:"+state);
        AdvertSDKManager.submitVideoPlayEvent(getContext(),advertInfo,state);
    }

    private boolean report_1_4 = false;
    private boolean report_1_2 = false;
    private boolean report_3_4 = false;

    protected void resetAdStatus(){
        report_1_4 = false;
        report_1_2 = false;
        report_3_4 = false;
        adDurationText.setVisibility(INVISIBLE);
        isPlayingAd = false;
    }

    protected void reportVideoAdAnatics(int progress){
        if(progress >= 25 && !report_1_4){
            report_1_4 = true;
            onPlayVideoAd(AdvertSDKManager.VIDEO_PLAY_FIRST_QUAR);
        }
        if(progress >= 50 && !report_1_2){
            report_1_2 = true;
            onPlayVideoAd(AdvertSDKManager.VIDEO_PLAY_MID_POINT);
        }
        if(progress >=75 && !report_3_4){
            report_3_4 = true;
            onPlayVideoAd(AdvertSDKManager.VIDEO_PLAY_THIRD_QUAR);
        }
    }

    protected void displayAdTime(String progress){
        if(adDurationText != null){
            adDurationText.setVisibility(VISIBLE);
            adDurationText.setText(progress);
        }
    }


    public abstract int getLayoutId();


    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE || currentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setProgressAndText();
                    }
                });
            }
        }
    }

}
