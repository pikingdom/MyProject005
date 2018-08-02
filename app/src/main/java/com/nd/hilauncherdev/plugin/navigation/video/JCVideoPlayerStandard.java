package com.nd.hilauncherdev.plugin.navigation.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.VideoDataLoader;
import com.nd.hilauncherdev.plugin.navigation.util.BlurUtil;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.search.SearchLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JCVideoPlayerStandard extends JCVideoPlayer implements ImageCallback {

    protected static Timer DISMISS_CONTROL_VIEW_TIMER;

    public ImageView blurBgImage;
    public ImageView backButton;
    public ProgressBar bottomProgressBar, loadingProgressBar;
    public TextView titleTextView;
    public ImageView thumbImageView;
    public ImageView tinyBackImageView;

    public String thumbUrl;

    protected DismissControlViewTimerTask mDismissControlViewTimerTask;

    private VideoPlayCallback videoPlayCallback;



    public JCVideoPlayerStandard(Context context) {
        super(context);
    }

    public JCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public void setVideoPlayCallback(VideoPlayCallback videoPlayCallback) {
        this.videoPlayCallback = videoPlayCallback;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        blurBgImage = (ImageView) findViewById(R.id.iv_video_blur_bg);
        bottomProgressBar = (ProgressBar) findViewById(R.id.bottom_progress);
        titleTextView = (TextView) findViewById(R.id.title);
        backButton = (ImageView) findViewById(R.id.back);
        thumbImageView = (ImageView) findViewById(R.id.thumb);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        tinyBackImageView = (ImageView) findViewById(R.id.back_tiny);

        thumbImageView.setOnClickListener(this);
        backButton.setOnClickListener(this);
        tinyBackImageView.setOnClickListener(this);

    }

    @Override
    public void setUp(boolean isFlipping,String url, AdvertSDKManager.AdvertInfo advertInfo, int screen, Object... objects) {
        super.setUp(isFlipping,url, advertInfo, screen, objects);
        isPlayingAd = false;
        if (objects.length == 0) return;
        titleTextView.setText(objects[0].toString());
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setVisibility(GONE);
            backButton.setVisibility(View.VISIBLE);
            tinyBackImageView.setVisibility(View.INVISIBLE);
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jc_start_button_w_h_fullscreen));
        } else if (currentScreen == SCREEN_LAYOUT_NORMAL
                || currentScreen == SCREEN_LAYOUT_LIST) {
            fullscreenButton.setImageResource(R.drawable.sel_video_fullscreen);
            fullscreenButton.setVisibility(VISIBLE);
            backButton.setVisibility(View.GONE);
            titleTextView.setVisibility(GONE);
            tinyBackImageView.setVisibility(View.INVISIBLE);
            changeStartButtonSize((int) getResources().getDimension(R.dimen.jc_start_button_w_h_normal));
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            tinyBackImageView.setVisibility(View.VISIBLE);
            setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        }
        this.thumbUrl = objects[1].toString();
        /** 根据状态判断是否要立即显示图片 */
        if(!isOnFlipping){
            Drawable drawable = ImageLoader.getInstance().loadDrawable(thumbUrl, this);
            if (drawable != null) {
                imageLoaded(drawable, thumbUrl, null);
            } else {
                blurBgImage.setImageDrawable(getResources().getDrawable(R.drawable.fill_rect));
                thumbImageView.setImageDrawable(getResources().getDrawable(R.drawable.fill_rect));
            }
        }else{
            Drawable drawable = ImageLoader.getInstance().loadDrawableIfExistInMemory(thumbUrl);
            if(drawable != null){
                thumbImageView.setImageDrawable(drawable);
            }else{
                thumbImageView.setImageDrawable(getResources().getDrawable(R.drawable.fill_transparent_rect));
            }
            showMemoryBlurBitmap();
        }

    }

    public void showMemoryBlurBitmap(){
        try {
            final String imgPath = ImageLoader.getInstance().getDiskCache().get(thumbUrl).getAbsolutePath();
            if (FileUtil.isFileExits(imgPath)) {
                final Bitmap blurBitmap = BlurUtil.getBlurBitmapIfExistMemory(imgPath);
                if (blurBitmap != null && !blurBitmap.isRecycled() && currentScreen != SCREEN_WINDOW_FULLSCREEN) {
                    blurBgImage.setImageBitmap(blurBitmap);
                    return;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        blurBgImage.setImageDrawable(getResources().getDrawable(R.drawable.fill_rect));

    }

    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        lp = loadingProgressBar.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }

    @Override
    public int getLayoutId() {
        return R.layout.jc_layout_standard;
    }

    @Override
    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (currentState) {
            case CURRENT_STATE_NORMAL:
                changeUiToNormal();
                break;
            case CURRENT_STATE_PREPARING:
                changeUiToPreparingShow();
                break;
            case CURRENT_STATE_PLAYING:
                changeUiToPlayingShow();
                break;
            case CURRENT_STATE_PAUSE:
                changeUiToPauseShow();
                cancelDismissControlViewTimer();
                break;
            case CURRENT_STATE_ERROR:
                changeUiToError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                changeUiToCompleteShow();
                cancelDismissControlViewTimer();
                bottomProgressBar.setProgress(100);
                break;
            case CURRENT_STATE_PLAYING_BUFFERING_START:
                changeUiToPlayingBufferingShow();
                break;
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    onClickUiToggle();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.thumb) {
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR) {
                startPlayWithDlg();
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onClickUiToggle();
            }
        } else if (i == R.id.surface_container) {
            if(isPlayingAd){
                onClickVideoAd();
            }else{
                startDismissControlViewTimer();
            }
        } else if (i == R.id.back) {
            backPress();
            if(JCVideoPlayerManager.getCurrentJcvd() != null){
                JCVideoPlayerManager.getCurrentJcvd().setUiWitStateAndScreen(JCVideoPlayerManager.getCurrentJcvd().currentState);
            }
            if(JCVideoPlayerManager.getFirstFloor() != null){
                JCVideoPlayerManager.getFirstFloor().playOnThisJcvd();
            }
            if(videoPlayCallback != null){
                videoPlayCallback.onVideoPlayBack();
            }
        } else if (i == R.id.back_tiny) {
            if(isPlayingAd){
                resetAdStatus();
                isPlayingAd = false;
            }
            backPress();
            JCVideoPlayer.releaseAllVideos();
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void startVideo() {
        prepareMediaPlayer();
    }

    /**
     * @desc 控制顶部和底部工具条的显隐
     * @author linliangbin
     * @time 2017/6/7 15:23
     */
    public void onClickUiToggle() {
        if (currentState == CURRENT_STATE_PREPARING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPreparingClear();
            } else {
                changeUiToPreparingShow();
            }
        } else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (currentState == CURRENT_STATE_PAUSE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToCompleteClear();
            } else {
                changeUiToCompleteShow();
            }
        } else if (currentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingBufferingClear();
            } else {
                changeUiToPlayingBufferingShow();
            }
        }
    }


    @Override
    public void setBufferProgress(int bufferProgress) {
        /** 不显示缓存进度 */
//        super.setBufferProgress(bufferProgress);
//        if (bufferProgress != 0) bottomProgressBar.setSecondaryProgress(bufferProgress);
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
        bottomProgressBar.setProgress(0);
        bottomProgressBar.setSecondaryProgress(0);
    }

    //Unified management Ui
    public void changeUiToNormal() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPreparingShow() {
        if(isPlayingAd){
            setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
            return;
        }
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToPreparingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    //JustPreparedUi
    @Override
    public void onPrepared() {
        super.onPrepared();
        if (!isPlayingAd) {
            setAllControlsVisible(View.VISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        }else{
            onPlayVideoAd(AdvertSDKManager.VIDEO_PLAY_START);
        }
    }

    public void changeUiToPlayingShow() {
        if(isPlayingAd){
            setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
            return;
        }
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
        }

    }

    public void changeUiToPlayingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
        }

    }

    public void changeUiToPauseClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
        }

    }

    public void changeUiToPlayingBufferingShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToPlayingBufferingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToCompleteShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void changeUiToCompleteClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    @Override
    protected void displayAdTime(String progress) {
        super.displayAdTime(progress);
        tinyBackImageView.setVisibility(VISIBLE);
    }

    @Override
    protected void resetAdStatus() {
        super.resetAdStatus();
        tinyBackImageView.setVisibility(INVISIBLE);
    }

    public void changeUiToError() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }

    }

    public void setAllControlsVisible(int topCon, int bottomCon, int startBtn, int loadingPro,
                                      int thumbImg, int coverImg, int bottomPro) {
        topContainer.setVisibility(topCon);
        bottomContainer.setVisibility(bottomCon);
        startButton.setVisibility(startBtn);
        loadingProgressBar.setVisibility(loadingPro);
        thumbImageView.setVisibility(thumbImg);
        bottomProgressBar.setVisibility(bottomPro);
    }

    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setImageResource(R.drawable.ic_video_play_pause);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setImageResource(R.drawable.ic_video_play_error);
        } else {
            startButton.setImageResource(R.drawable.ic_video_play_normal);
        }
    }


    @Override
    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        cancelAllProgressTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);

        setProgressAndText();
        startProgressTimer();
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    public void cancelAllProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }

    }


    @Override
    public void imageLoaded(Drawable drawable, String s, Map map) {
        if (drawable != null && !TextUtils.isEmpty(s) && s.equals(thumbUrl)) {
            try {
                thumbImageView.setImageDrawable(drawable);
                final String imgPath = ImageLoader.getInstance().getDiskCache().get(s).getAbsolutePath();
                if (FileUtil.isFileExits(imgPath) && currentScreen != SCREEN_WINDOW_FULLSCREEN) {
                    ThreadUtil.executeMore(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap blurBitmap = BlurUtil.blutBitmap(getContext(),imgPath);
                            if (blurBitmap != null && !blurBitmap.isRecycled()) {
                                Global.runInMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        blurBgImage.setImageBitmap(blurBitmap);
                                    }
                                });
                            }
                        }
                    });

                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public void onAutoCompletion() {

        if (advertInfo == null || TextUtils.isEmpty(advertInfo.videoUrl) ||
                (!TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL) && JCMediaManager.CURRENT_PLAYING_URL == advertInfo.videoUrl)) {
            super.onAutoCompletion();
            cancelAllProgressTimer();
            cancelDismissControlViewTimer();
            JCVideoPlayerManager.completeAll();
            if (videoPlayCallback != null) {
                videoPlayCallback.onVideoPlayCompleted(index);
            }
            if(isPlayingAd){
                onPlayVideoAd(AdvertSDKManager.VIDEO_PLAY_COMPLETE);
                isPlayingAd = false;
                resetAdStatus();
            }
            SearchLayout.forceDisableSwitcher = false;
        } else if (!TextUtils.isEmpty(JCMediaManager.CURRENT_PLAYING_URL) && JCMediaManager.CURRENT_PLAYING_URL == url) {
            isPlayingAd = true;
            onShowVideoAd();
            prepareMediaPlayer(advertInfo.videoUrl);
            VideoDataLoader.getInstance(getContext()).requestVideoAd();
        }
    }

    @Override
    public void onCompletion() {
        cancelDismissControlViewTimer();
        cancelAllProgressTimer();
        super.onCompletion();

    }

    public interface VideoPlayCallback {

        /**
         * 视频播放结束回调
         */
        public void onVideoPlayCompleted(int completeIndex);

        /**
         * 视频返回处理
         */
        public void onVideoPlayBack();

    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            if (currentState != CURRENT_STATE_NORMAL
                    && currentState != CURRENT_STATE_ERROR
                    && currentState != CURRENT_STATE_AUTO_COMPLETE) {
                Global.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomContainer.setVisibility(View.INVISIBLE);
                        topContainer.setVisibility(View.INVISIBLE);
                        startButton.setVisibility(View.INVISIBLE);
                        bottomProgressBar.setVisibility(View.VISIBLE);
                    }
                });

            }
            cancelAllProgressTimer();
        }
    }
}
