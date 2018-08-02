package com.nd.hilauncherdev.plugin.navigation.widget.ad;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.kit.advert.AdControllerWrapper;
import com.nd.hilauncherdev.plugin.navigation.kit.advert.AdState;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Map;

/**
 * 零屏全屏弹出广告
 * Created by linliangbin on 2017/9/7 11:27.
 */

public class PopupAdLayout extends RelativeLayout implements View.OnClickListener, PageActionInterface {

    public static final int MSG_AD_AUTO_DISMISS = 100;
    public static final int MSG_SHOW_AD_DRAWABLE = 101;

    /**
     * 每次启动桌面后仅显示一次弹出广告
     */
    public static boolean hasShowPopupAd = false;
    AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
    ImageView adImage, closeImage;
    AdvertSDKManager.AdvertInfo currntAd;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_AD_AUTO_DISMISS) {
                AdControllerWrapper.getInstance().updateAdTime(currntAd, AdvertSDKController.AD_POSITION_POPUP_AD);
                hide(true);
            } else if (msg.what == MSG_SHOW_AD_DRAWABLE) {

                Drawable drawable = (Drawable) msg.obj;
                if (drawable != null) {
                    showDrawable(drawable);
                }
            }
        }
    };

    public PopupAdLayout(Context context) {
        super(context);
    }

    public PopupAdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.ad_popup_ad_layout, this);
        adImage = (ImageView) findViewById(R.id.iv_popup_ad_thumb);
        adImage.setOnClickListener(this);
        closeImage = (ImageView) findViewById(R.id.iv_popup_ad_close);
        closeImage.setOnClickListener(this);

    }

    public void showAd(final AdvertSDKManager.AdvertInfo ad) {
        this.currntAd = ad;
        Drawable drawable = asyncImageLoader.loadDrawable(true, ad.picUrl, new ImageCallback() {
            @Override
            public void imageLoaded(Drawable drawable, String s, Map map) {
                if (drawable != null && !TextUtils.isEmpty(s)
                        && s.equals(ad.picUrl)) {
                    startShowDrawable(drawable);
                }
            }
        });
        if (drawable != null) {
            startShowDrawable(drawable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getVisibility() == VISIBLE){
            return true;
        }
        return false;
    }

    private void startShowDrawable(Drawable drawable) {

        Message message = handler.obtainMessage();
        message.what = MSG_SHOW_AD_DRAWABLE;
        message.obj = drawable;
        handler.sendMessageDelayed(message, 1500);
    }

    private void showDrawable(Drawable drawable) {

        reset();
        PopupAdLayout.this.setVisibility(VISIBLE);
        init();
        if (drawable != null) {
            adImage.setImageDrawable(drawable);
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_win_top_in);
        this.startAnimation(animation);

        handler.removeMessages(MSG_AD_AUTO_DISMISS);
        handler.sendEmptyMessageDelayed(MSG_AD_AUTO_DISMISS, currntAd == null ? 3000 :
                currntAd.splashTime <= 3000 ? 3000 : currntAd.splashTime);
        hasShowPopupAd = true;
        AdvertSDKController.submitShowEvent(getContext(), new Handler(), currntAd);
    }

    public void hide(boolean showAnim) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.pop_win_top_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                reset();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (showAnim) {
            this.startAnimation(animation);
        } else {
            reset();
        }

    }

    @Override
    public void onClick(View v) {

        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_popup_ad_thumb:
                if (currntAd != null) {
                    LauncherCaller.openUrl(getContext(), "", currntAd.actionIntent);
                }
                AdvertSDKController.submitClickEvent(getContext(), new Handler(), currntAd);
                hide(false);
                if (currntAd != null) {
                    AdControllerWrapper.getInstance().updateAdTime(currntAd, AdvertSDKController.AD_POSITION_POPUP_AD);
                }
                break;
            case R.id.iv_popup_ad_close:
                hide(true);
                if (currntAd != null) {
                    AdControllerWrapper.getInstance().updateAdState(currntAd, AdState.FORCECLOSE, AdvertSDKController.AD_POSITION_POPUP_AD);
                }
                break;
        }

    }


    @Override
    public void onEnterPage() {

        if (hasShowPopupAd) {
            return;
        }

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {

                final AdvertSDKManager.AdvertInfo adInfo = AdControllerWrapper.getInstance().
                        getNextAd(AdvertSDKController.AD_POSITION_POPUP_AD);
                if (adInfo != null) {
                    Global.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            showAd(adInfo);
                        }
                    });
                }

            }
        });
    }

    private void reset() {

        this.setVisibility(GONE);
        this.removeAllViews();
        adImage = null;
        closeImage = null;
        handler.removeMessages(MSG_AD_AUTO_DISMISS);
        handler.removeMessages(MSG_SHOW_AD_DRAWABLE);

    }


    @Override
    public void onLeavePage() {
        hide(false);
    }

    @Override
    public void onPageResume() {

    }

    @Override
    public void onBackToPage() {

    }

    @Override
    public void onActionRefresh() {

    }

    @Override
    public void onActionRefreshSync() {

    }

    @Override
    public void doDailyUpdate() {

    }

}
