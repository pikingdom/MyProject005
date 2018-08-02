package com.nd.hilauncherdev.plugin.navigation.video;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.Reflect;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p>统一管理MediaPlayer的地方,只有一个mediaPlayer实例，那么不会有多个视频同时播放，也节省资源。</p>
 * <p>Unified management MediaPlayer place, there is only one MediaPlayer instance, then there will be no more video broadcast at the same time, also save resources.</p>
 * Created by Nathen
 * On 2015/11/30 15:39
 */
public class JCMediaManager implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {
    public static final int HANDLER_PREPARE = 0;
    public static final int HANDLER_RELEASE = 2;
    public static String TAG = "llbeing";
    public static JCResizeTextureView textureView;
    public static SurfaceTexture savedSurfaceTexture;
    public static String CURRENT_PLAYING_URL;
    public static boolean CURRENT_PLING_LOOP;
    public static Map<String, String> MAP_HEADER_DATA;
    private static JCMediaManager JCMediaManager;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;
    HandlerThread mMediaHandlerThread;
    MediaHandler mMediaHandler;
    Handler mainThreadHandler;

    public JCMediaManager() {
        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler((mMediaHandlerThread.getLooper()));
        mainThreadHandler = new Handler();
    }

    public static JCMediaManager instance() {
        if (JCMediaManager == null) {
            JCMediaManager = new JCMediaManager();
        }
        return JCMediaManager;
    }

    public Point getVideoSize() {
        if (currentVideoWidth != 0 && currentVideoHeight != 0) {
            return new Point(currentVideoWidth, currentVideoHeight);
        } else {
            return null;
        }
    }

    public void prepare() {
        Log.i(TAG, "prepare");
        releaseMediaPlayer();
        Message msg = new Message();
        msg.what = HANDLER_PREPARE;
        mMediaHandler.sendMessage(msg);
    }

    public void releaseMediaPlayer() {
        Log.i(TAG, "releaseMediaPlayer");
        Message msg = new Message();
        msg.what = HANDLER_RELEASE;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
//        Log.i("llbeing", "onSurfaceTextureAvailable [" + this.hashCode() + "] ");
        if (savedSurfaceTexture == null) {
            savedSurfaceTexture = surfaceTexture;
            prepare();
        } else {
            //TODO llbeing 此处需要SDK16 的api
            if (TelephoneUtil.getApiLevel() >= 16) {
                Reflect.on(textureView).call("setSurfaceTexture", savedSurfaceTexture);
            }
//            textureView.setSurfaceTexture(savedSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        // 如果SurfaceTexture还没有更新Image，则记录SizeChanged事件，否则忽略
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + this.hashCode() + "] ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
//        Log.i(TAG, "onSurfaceTextureDestroyed");
//        if(TelephoneUtil.getApiLevel() < 21){
//            return false;
//        }
        return savedSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onPrepared();
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, final int percent) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onInfo(what, extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        currentVideoWidth = width;
        currentVideoHeight = height;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JCVideoPlayerManager.getCurrentJcvd() != null) {
                    JCVideoPlayerManager.getCurrentJcvd().onVideoSizeChanged();
                }
            }
        });
    }

    public void seekTo(int seekTo){
        try {
            if(mediaPlayer != null ){
                mediaPlayer.seekTo(seekTo);
            }
        }catch (Exception e){
            Log.i("llbeing","JCMeidaManager:seek to fail:"+e.getMessage());
        }

    }

    public boolean isNullMediaPlayer(){
        return mediaPlayer == null;
    }

    public void startPlaying() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void pausePlaying() {
        try {
            if (mediaPlayer != null &&
                    mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public int getCurrentPosition(){
        try {
            if(mediaPlayer != null){
                return mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDuration(){
        try {
            if(mediaPlayer != null){
                return mediaPlayer.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean isPlaying(){
        try {
            if(mediaPlayer != null){
                return mediaPlayer.isPlaying();
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public class MediaHandler extends Handler {
        public MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_PREPARE:
                    try {
                        currentVideoWidth = 0;
                        currentVideoHeight = 0;
                        mediaPlayer.release();
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        Class<MediaPlayer> clazz = MediaPlayer.class;
                        Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                        method.invoke(mediaPlayer, CURRENT_PLAYING_URL, MAP_HEADER_DATA);
                        mediaPlayer.setLooping(CURRENT_PLING_LOOP);
                        mediaPlayer.setOnPreparedListener(JCMediaManager.this);
                        mediaPlayer.setOnCompletionListener(JCMediaManager.this);
                        mediaPlayer.setOnBufferingUpdateListener(JCMediaManager.this);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        mediaPlayer.setOnSeekCompleteListener(JCMediaManager.this);
                        mediaPlayer.setOnErrorListener(JCMediaManager.this);
                        mediaPlayer.setOnInfoListener(JCMediaManager.this);
                        mediaPlayer.setOnVideoSizeChangedListener(JCMediaManager.this);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setSurface(new Surface(savedSurfaceTexture));
//                        Log.i("llbeing", "MediaPlay:prepare");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_RELEASE:
                    mediaPlayer.release();
                    break;
            }
        }
    }

}
