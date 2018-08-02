package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.book.ScaleImageView;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Locale;
import java.util.Map;

/**
 * Created by linxiaobin on 2017/9/11.
 * 淘宝合辑页面顶部倒计时
 */

public class TaoBaoCollectionDetailHeaderView extends LinearLayout {
    private long fixTimeInterval;
    private long startTimeInterval;
    private long endTimeInterval;
    TextView textView;
    private ScaleImageView bannerView;

    public TaoBaoCollectionDetailHeaderView(Context context) {
        super(context);
        init(context);
    }

    public TaoBaoCollectionDetailHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.taobao_collection_detail_head, this);

        textView = (TextView) findViewById(R.id.tv_tb_timer);
        bannerView = (ScaleImageView) findViewById(R.id.iv_tb_collection_banner);
        bannerView.setAutoRatio(true);
    }

    private CountDownTimer timer;

    public void startTimer() {
        stopTimer();

        timer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l) {
                updateContent();
            }

            @Override
            public void onFinish() {
                startTimer();
            }
        };
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void setBannerUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            bannerView.setVisibility(GONE);
            return;
        }

        AsyncImageLoader mImageLoader = new AsyncImageLoader();
        Drawable imageDrawable = mImageLoader.loadDrawable(url, new ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                if (imageDrawable != null) {
                    bannerView.setVisibility(VISIBLE);
                    bannerView.setImageDrawable(imageDrawable);
                }
            }
        });
        if (imageDrawable != null) {
            bannerView.setVisibility(VISIBLE);
            bannerView.setImageDrawable(imageDrawable);
        }
    }

    private static final String TAG = "TaoBaoCollectionDetailH";

    private void updateContent() {
        long currentInterval = System.currentTimeMillis() + fixTimeInterval;

        if (currentInterval <= endTimeInterval) {
            StringBuilder stringBuilder = new StringBuilder();

            long between;

            if (currentInterval <= startTimeInterval) {
                stringBuilder.append("距离开始: ");
                between = (startTimeInterval - currentInterval) / 1000;
            } else {
                stringBuilder.append("距离结束: ");
                between = (endTimeInterval - currentInterval) / 1000;
            }

            long seconds = between % 60;
            between /= 60;
            long minutes = between % 60;
            between /= 60;
            long hours = between % 24;
            long days = between / 24;
            if (days > 0) {
                stringBuilder.append(days).append("天 ");
            }
            stringBuilder.append(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
            Log.d(TAG, "updateContent: " + stringBuilder.toString());
            textView.setText(stringBuilder.toString());
        } else {
            textView.setText("活动已结束");
            stopTimer();
        }
    }

    public void setFixTimeInterval(long fixTimeInterval) {
        this.fixTimeInterval = fixTimeInterval;
    }

    public void setStartTimeInterval(long startTimeInterval) {
        this.startTimeInterval = startTimeInterval;
    }

    public void setEndTimeInterval(long endTimeInterval) {
        this.endTimeInterval = endTimeInterval;
    }
}
