package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoSplashSale;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * description: 淘宝-限时抢购卡片<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/15<br/>
 */
public class TaobaoSplashSaleCard extends TaobaoCard {
    private ViewHolder holderOne;
    private ViewHolder holderTwo;
    private ViewHolder holderThree;

    private static final String TAG = "TaobaoSplashSaleCard";

    private ViewGroup mParent;
    private List<TaobaoSplashSale> mData = new ArrayList<TaobaoSplashSale>();

    private TextView tvHour, tvMinute, tvSecond, btnMore;
    private CountDownTimer timer;
    private Context mContext;
    private AsyncImageLoader imageLoader;
    private long remainderTime = 1;//剩余倒计时时间
    private boolean isUpdated = true;

    private Handler handler = new Handler();

    public TaobaoSplashSaleCard(final Context context, ViewGroup parent) {
        super(parent);
        setType(CardType.SPLASHSALE);
        mParent = parent;

        mContext = context;
        imageLoader = new AsyncImageLoader();
        View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_flashsale, parent, false);
        setContentView(contentView);

        tvHour = (TextView) contentView.findViewById(R.id.tv_taobao_splashsale_hour);
        tvMinute = (TextView) contentView.findViewById(R.id.tv_taobao_splashsale_minute);
        tvSecond = (TextView) contentView.findViewById(R.id.tv_taobao_splashsale_seconds);
        btnMore = (TextView) contentView.findViewById(R.id.btn_taobao_splashsale_more);

        holderOne = new ViewHolder(contentView.findViewById(R.id.layout_flashsale_one));
        holderTwo = new ViewHolder(contentView.findViewById(R.id.layout_flashsale_two));
        holderThree = new ViewHolder(contentView.findViewById(R.id.layout_flashsale_three));
        //默认隐藏的
        setCardVisible(false);
    }

    private void setSaleProgress(ProgressBar progressBar, TextView progressTv, int total, int progress) {
        if (progressBar == null) {
            return;
        }
        int percent = 0;
        if (total > 0) {
            percent = (int) (progress / (total * 1.0f) * 100);
            percent = percent >= 100 ? 99 : percent;
        }
        progressBar.setProgress(percent);

        progressTv.setText(String.format("已抢%d%%", percent));
    }


    private void loadData() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<TaobaoSplashSale> list = TaobaoLoader.getSplashSales(mContext, 3);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (list == null || list.isEmpty()) {
                            setCardVisible(false);
                            if (timer != null) {
                                timer.cancel();
                            }
                        } else {
                            setCardVisible(true);
                            mData.clear();
                            mData.addAll(list);
                            isUpdated = true;
                            remainderTime = 0;//重新刷新时，记当前时间为0，表示已刷新数据
                            updateView();
                        }
                    }
                });
            }
        });
    }

    private void updateView() {
        //卡片不可见时，不要再去启动倒计时，强制隐藏
        if (getType() == null || !getType().isVisible()) {
            setCardVisible(false);
            if (timer != null) {
                timer.cancel();
            }
            return;
        }

        long countdowntime = 0;
        if (isUpdated) {
            int count = mData.size();
            for (int i = 0; i < count; i++) {
                TaobaoSplashSale item = mData.get(i);
                if (item != null) {
                    countdowntime = item.endTime - item.startTime - (item.currentTime - item.startTime < 0 ? 0 : item.currentTime - item.startTime);
                    switch (i) {
                        case 0:
                            holderOne.url = item.urlClick;
                            setItemView(holderOne, item);
                            break;
                        case 1:
                            holderTwo.url = item.urlClick;
                            setItemView(holderTwo, item);
                            break;
                        case 2:
                            holderThree.url = item.urlClick;
                            setItemView(holderThree, item);
                            break;
                    }
                }
                remainderTime = countdowntime;
            }
        } else {
            countdowntime = remainderTime;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(countdowntime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hour = millisUntilFinished / (1000L * 60L * 60L) % 24;//小时
                long minute = millisUntilFinished / (1000 * 60) % 60;//分钟
                long second = millisUntilFinished / 1000 % 60;//秒
                //long millis = millisUntilFinished % 1000 / 10;//毫秒
                if (!isPageVisible()) return;
                tvHour.setText(String.format("%02d", hour));
                tvMinute.setText(String.format("%02d", minute));
                tvSecond.setText(String.format("%02d", second));
                remainderTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                this.cancel();
                tvHour.setText("00");
                tvMinute.setText("00");
                tvSecond.setText("00");
                remainderTime = 0;
                //本页不可见或者该卡片不可见时，不去刷新数据
                if (!isPageVisible() || getType() == null || !getType().isVisible()) return;
                //倒计时完毕后，再自我更新一期出来
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 1200);
            }
        };

        try {
            String imei = TelephoneUtil.getIMEI(mContext);
            //Pattern pattern = Pattern.compile("^[0]*$");
            if (imei != null && !"".equals(imei.trim()) && imei.trim().length() >= 6) {
                //Matcher matcher = pattern.matcher(imei);
                if (imei.startsWith("000000") || imei.endsWith("000000"))
                    return;
                Log.d(TAG, "updateView() called with: " + " start timer");
                timer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String moreLink = getType().getMoreLink();
        if (TextUtils.isEmpty(moreLink)) {
            btnMore.setVisibility(View.GONE);
            btnMore.setOnClickListener(null);
        } else {
            btnMore.setVisibility(View.VISIBLE);
            btnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, NewsDetailActivity.class);
                    intent.putExtra("url", moreLink);
                    SystemUtil.startActivitySafely(mContext, intent);
                }
            });
        }
        isUpdated = false;
    }

    private void setItemView(final ViewHolder holder, TaobaoSplashSale data) {
        holder.desc.setText(data.title);
        holder.promoPrice.setText("￥" + filterPrice(data.price));
        holder.price.setText("￥" + filterPrice(data.finalPrice));
        setSaleProgress(holder.progressBar, holder.tvProgress, data.totalSale, data.saleProgress);
        holder.ivIcon.setImageDrawable(mContext.getResources().getDrawable(CommonLauncherControl.getVph()));
        holder.ivIcon.setTag(data.imageUrl);
        Drawable drawable = imageLoader.loadDrawable(data.imageUrl, new ImageCallback() {
            @Override
            public void imageLoaded(Drawable drawable, String s, Map map) {
                if (drawable != null && !TextUtils.isEmpty(s) && s.equals(holder.ivIcon.getTag())) {
                    holder.ivIcon.setImageDrawable(drawable);
                }
            }
        });
        if (drawable != null) {
            holder.ivIcon.setImageDrawable(drawable);
        }
    }

    @Override
    public void setPageVisible(boolean visible) {
        super.setPageVisible(visible);
        if (visible) {//如果可见时，就重新刷新一下当前视图，不做数据刷新
            updateView();
        }
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView desc;
        TextView price;
        TextView promoPrice;
        ProgressBar progressBar;
        TextView tvProgress;
        String url = "http://www.baidu.com/";

        public ViewHolder(final View view) {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(url)) {
                        return;
                    }
                    TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
                    CvAnalysis.submitClickEvent(v.getContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_SPLASHSALE, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClass(view.getContext(), NewsDetailActivity.class);
                    intent.putExtra("url", url);
                    SystemUtil.startActivitySafely(view.getContext(), intent);
                }
            });

            ivIcon = (ImageView) view.findViewById(R.id.iv_goods_icon);
            desc = (TextView) view.findViewById(R.id.tv_goods_name);
            price = (TextView) view.findViewById(R.id.tv_goods_price);
            promoPrice = (TextView) view.findViewById(R.id.tv_goods_price_old);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_splashsale);
            tvProgress = (TextView) view.findViewById(R.id.tv_splashsale_progress);
            promoPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    private String filterPrice(String current) {
        if (TextUtils.isEmpty(current)) {
            return "0";
        }

        int index = current.lastIndexOf(".");
        try {
            if (index > 0 && index < current.length() - 1) {
                String fl = current.substring(index + 1);
                int flint = Integer.valueOf(fl);
                if (flint == 0) {
                    return current.substring(0, index);
                } else if (flint % 10 == 0) {
                    return current.substring(0, index) + "." + (flint / 10);
                } else {
                    return current.substring(0, index) + "." + flint;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return current;
    }


    @Override
    public void update() {
        loadData();
    }
}
