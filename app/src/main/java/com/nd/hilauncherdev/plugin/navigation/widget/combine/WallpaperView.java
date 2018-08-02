package com.nd.hilauncherdev.plugin.navigation.widget.combine;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.TextBean;
import com.nd.hilauncherdev.plugin.navigation.bean.WallpaperBean;
import com.nd.hilauncherdev.plugin.navigation.loader.WallpaperDataLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.WallpaperUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Map;

/**
 * Created by linliangbin on 2017/4/28 13:39.
 */

public class WallpaperView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener, ImageCallback {

    public int distanceY = 0;
    protected float mLastMotionX = 0;
    protected float mLastMotionY = 0;
    ImageView wallpaperImage;
    View downloadText;
    View refreshText;
    TextView wordText;
    TextView authorText;

    private int downX;
    private int downY;

    private WallpaperBean currentWallpaper;
    private TextBean currentText;

    /**
     * 当前是否为上拉动作
     */
    private boolean isPullUp = true;
    /**
     * 父控件
     */
    private CombineNavigationView container;

    /**
     * 当前显示的壁纸本地路径
     */
    private String currentImagePath;


    private Animation rotateAnimation = null;
    private boolean isPlayingAnim = false;

    public WallpaperView(Context context) {
        super(context);
        init();
    }


    public WallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setContainer(CombineNavigationView container) {
        this.container = container;
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.layout_wallpaper, this);
        wallpaperImage = (ImageView) findViewById(R.id.iv_wallpaper);

        wordText = (TextView) findViewById(R.id.tv_wallpaper_word);

        authorText = (TextView) findViewById(R.id.tv_wallpaper_author);

        downloadText = findViewById(R.id.tv_wallpaper_download);
        downloadText.setOnClickListener(this);

        refreshText = findViewById(R.id.tv_wallpaper_refresh);
        refreshText.setOnClickListener(this);

        initRefresh();

        setOnTouchListener(this);

        refreshWallpaper();


    }


    private void initRefresh() {

        rotateAnimation = new RotateAnimation(0, 720, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(2000);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPlayingAnim = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isPlayingAnim = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    private void downloadWallpaper() {

        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_WALLPAPER, "yy");
        try {
            if (FileUtil.isFileExits(currentImagePath)) {
                WallpaperUtil.applyWallpaper(getContext(), currentImagePath);
            } else {
                SystemUtil.makeShortToast(getContext(), R.string.apply_wallpaper_fail);
                return;
            }
            SystemUtil.makeShortToast(getContext(), R.string.apply_wallpaper_success);
        } catch (Exception e) {
            e.printStackTrace();
            SystemUtil.makeShortToast(getContext(), R.string.apply_wallpaper_fail);
        }
    }

    private void refreshWallpaper() {

        if (!isPlayingAnim) {
            findViewById(R.id.tv_wallpaper_refresh).startAnimation(rotateAnimation);
            isPlayingAnim = true;
            currentText = WallpaperDataLoader.getInstance(getContext()).popText();
            currentWallpaper = WallpaperDataLoader.getInstance(getContext()).popWallpaper();
            if (currentText != null) {
                wordText.setText("\"" + currentText.content + "\"");
                authorText.setText("----" + currentText.author);
            }
            if (currentWallpaper != null) {
                Drawable drawable = ImageLoader.getInstance().loadDrawable(currentWallpaper.picUrl, this);
                if (drawable != null) {
                    imageLoaded(drawable, currentWallpaper.picUrl, null);
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_wallpaper_download) {
            downloadWallpaper();
        } else if (view.getId() == R.id.tv_wallpaper_refresh) {
            refreshWallpaper();
            PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_WALLPAPER, "xz");
        }
    }


    protected void onTouchDown(MotionEvent event) {

        isPullUp = false;
        mLastMotionX = event.getX();
        mLastMotionY = event.getY();

        downX = (int) event.getX();
        downY = (int) event.getY();

    }


    protected void onTouchMove(MotionEvent event) {


        int currentMove = (int) (downY - event.getY());
        if (currentMove > ScreenUtil.dip2px(getContext(), 30)) {
            isPullUp = event.getY() - downY > 0 ? false : true;
            if (isPullUp) {
                onUpdatePullPosition(currentMove);
            }
            mLastMotionX = event.getX();
            mLastMotionY = event.getY();
        }

    }

    protected void onTouchUp(MotionEvent event) {
        if (isPullUp) {
            startHideAnimation();
        }
    }


    public void startHideAnimation() {
        startAnimator(true, ScreenUtil.getCurrentScreenHeight(getContext()));
    }


    private void startAnimator(final boolean setVisible, int dstValue) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(distanceY, dstValue);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.setDuration(250);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                onUpdatePullPosition(((Float) valueAnimator.getAnimatedValue()).intValue());
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                distanceY = 0;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }


    /**
     * @desc 移动距离变时的回调
     * @author linliangbin
     * @time 2017/4/28 14:59
     */
    public void onUpdatePullPosition(int distanceY) {
        this.distanceY = distanceY;
        if (container != null) {
            container.onUpdatePullDistance(distanceY);
        }
    }


    private void handleTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                onTouchDown(event);
            }
            break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                onTouchUp(event);
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                onTouchMove(event);
            }
            break;
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        handleTouchEvent(event);
        return true;
    }

    @Override
    public void imageLoaded(Drawable drawable, String s, Map map) {
        if (!TextUtils.isEmpty(s) && s.equals(currentWallpaper.picUrl) && drawable != null) {
            try {
                String imgPath = ImageLoader.getInstance().getDiskCache().get(s).getAbsolutePath();
                if (FileUtil.isFileExits(imgPath)) {
                    currentImagePath = imgPath;
                    wallpaperImage.setImageBitmap(((BitmapDrawable) drawable).getBitmap());
                    if (container != null) {
                        container.onWallpaperUpdate(imgPath);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
