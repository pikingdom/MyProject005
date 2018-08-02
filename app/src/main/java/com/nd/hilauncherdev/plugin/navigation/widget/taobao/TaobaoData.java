package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.db.TaobaoProductHelper;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import java.util.List;

/**
 * 淘宝购物屏缓存处理
 * Created by chenxuyu on 2016/5/19.
 */
public class TaobaoData {

    private static final String TAG = "TaobaoData";
    private static TaobaoData obj = new TaobaoData();

    private static final int HANDLER_TAOBAO_CACHE_LOADED = 100;
    private static final int HANDLER_TAOBAO_CARDSERIES = 101;
    private static final int HANDLER_TAOBAO_CPS = 102;
    private static final int HANDLER_TAOBAO_HEADLINE = 103;
    private static final int HANDLER_TAOBAO_UPDATE_SETTING = 104;//更新配置

    private Context context;
    private TaobaoProductHelper dbHelper;
    public SharedPreferencesUtil spUtil;
    private TaobaoCardWrapper wrapper;
    private boolean isPageExists = true;
    //顶部banner的定时器
    public CountDownTimer mTopTimer = null;
    //底部banner的定时器
    public CountDownTimer mBottomTimer = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_TAOBAO_CACHE_LOADED:
                    //loadTaobaoCache();
                    break;
                case HANDLER_TAOBAO_CARDSERIES:
                    //loadTaobaoCardSeries();
                    break;
                case HANDLER_TAOBAO_HEADLINE:
                    //loadTaobaoHeadLine();
                    break;
                case HANDLER_TAOBAO_CPS:
                    //loadTaobaoCps();
                    break;
                case HANDLER_TAOBAO_UPDATE_SETTING://主动更新卡片配置
                    Log.d(TAG, "update setting!");
                    if(wrapper != null) wrapper.assemble();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private TaobaoData(){

    }

    public static TaobaoData getInstance(){
        if(obj == null){
            obj = new TaobaoData();
        }
        return obj;
    }

    public void init(Context context){
        Log.e(TAG, "init: ");
        this.context = context;
        handler.removeCallbacksAndMessages(null);
        dbHelper = new TaobaoProductHelper(context);
        spUtil = new SharedPreferencesUtil(context);
        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CACHETIME, 4 * 60);
        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CARDSERIES_CACHETIME, 1 * 60);
        spUtil.putLong(SharedPreferencesUtil.TAOBAO_HEADLINE_CACHETIME, 1 * 60);
        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CPS_CACHETIME, 1 * 60);
    }

    public synchronized void clearCallback(){
        handler.removeCallbacksAndMessages(null);
        isPageExists = false;
    }

    public synchronized void start(TaobaoCardWrapper wrapper, boolean exists){
        this.wrapper = wrapper;
        if(exists != isPageExists)
            handler.removeCallbacksAndMessages(null);
        isPageExists = exists;
        Log.e(TAG, "start: card is exists ? " + exists);
        if(!isAccessable()) return;

        /* 2017/09/12 lxb 关闭旧版本淘宝购物屏预加载缓存数据 */
        // loadTaobaoCache();
        /* 2017/09/25 ygf 关闭旧版本淘宝购物屏预加载缓存数据 */
//        loadTaobaoCardSeries();
//        loadTaobaoCps();
//        loadTaobaoHeadLine();
    }
    /**
     * 取消banner定时器
     */
    public void cancelBannerTimers(){
        cancelTopBannerTimer();
        cancelBottomBannerTimer();
    }
    /**
     * 顶部的banner定时器
     */
    public void startTopBannerTimer(CountDownTimer timer){
        cancelTopBannerTimer();
        if (timer != null) {
            TaobaoData.getInstance().mTopTimer = timer;
            TaobaoData.getInstance().mTopTimer.start();
        }
    }
    public void cancelTopBannerTimer(){
        if (TaobaoData.getInstance().mTopTimer != null) {
            TaobaoData.getInstance().mTopTimer.cancel();
            TaobaoData.getInstance().mTopTimer = null;
        }
    }
    /**
     * 底部的banner定时器
     */
    public void startBottomBannerTimer(CountDownTimer timer){
        cancelBottomBannerTimer();
        if (timer != null) {
            TaobaoData.getInstance().mBottomTimer = timer;
            TaobaoData.getInstance().mBottomTimer.start();
        }
    }
    public void cancelBottomBannerTimer(){
        if (TaobaoData.getInstance().mBottomTimer != null) {
            TaobaoData.getInstance().mBottomTimer.cancel();
            TaobaoData.getInstance().mBottomTimer = null;
        }
    }
    /**
     * 每4小时取淘宝购物屏单品数据进行缓存
     */
    private void loadTaobaoCache() {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<TaobaoProduct> list = TaobaoLoader.getNetTaobaoProductsList(context, 200);
                    if (list != null && list.size() > 0) {
                        dbHelper.deleteAll();
                        for (TaobaoProduct item : list) {
                            dbHelper.add(item);
                        }
                    }

                    long cacheTime = spUtil.getLong(SharedPreferencesUtil.TAOBAO_CACHETIME, 4 * 60);
                    if (cacheTime < 60) {
                        cacheTime = 60;
                        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CACHETIME, cacheTime);
                    }
                    handler.sendEmptyMessageDelayed(HANDLER_TAOBAO_CACHE_LOADED, cacheTime * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 每2个小时加载购物屏卡片配置
     */
    public void loadTaobaoCardSeries(){
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    TaobaoLoader.getTaobaoCardSetting(context);
                    long cacheTiem = spUtil.getLong(SharedPreferencesUtil.TAOBAO_CARDSERIES_CACHETIME, 1 * 60);
                    if (cacheTiem < 60) {
                        cacheTiem = 60;
                        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CARDSERIES_CACHETIME, cacheTiem);
                    }
                    handler.sendEmptyMessage(HANDLER_TAOBAO_UPDATE_SETTING);//请求结束后，主动去更新卡片配置
                    handler.sendEmptyMessageDelayed(HANDLER_TAOBAO_CARDSERIES, cacheTiem * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 每1小时加载购物屏CPS数据
     */
    public void loadTaobaoCps(){
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    TaobaoLoader.getTaobaoCps(context);
                    long cacheTiem = spUtil.getLong(SharedPreferencesUtil.TAOBAO_CPS_CACHETIME, 1 * 60);
                    if (cacheTiem < 60) {
                        cacheTiem = 60;
                        spUtil.putLong(SharedPreferencesUtil.TAOBAO_CPS_CACHETIME, cacheTiem);
                    }
                    handler.sendEmptyMessageDelayed(HANDLER_TAOBAO_CPS, cacheTiem * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 每1小时加载购物屏头条数据
     */
    public void loadTaobaoHeadLine(){
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {
                    TaobaoLoader.getTaobaoHeadLine(context);
                    long cacheTiem = spUtil.getLong(SharedPreferencesUtil.TAOBAO_HEADLINE_CACHETIME, 1 * 60);
                    if (cacheTiem < 60) {
                        cacheTiem = 60;
                        spUtil.putLong(SharedPreferencesUtil.TAOBAO_HEADLINE_CACHETIME, cacheTiem);
                    }
                    handler.sendEmptyMessageDelayed(HANDLER_TAOBAO_HEADLINE, cacheTiem * 60 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isAccessable(){
        return isPageExists;
    }
}
