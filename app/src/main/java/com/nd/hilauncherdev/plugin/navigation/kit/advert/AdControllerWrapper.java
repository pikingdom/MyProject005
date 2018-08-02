package com.nd.hilauncherdev.plugin.navigation.kit.advert;

import android.content.Context;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.EmptyUtils;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.MD5FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * 广告控制器封装
 * Created by Administrator on 2016/8/8.
 */
public class AdControllerWrapper {

    private static AdControllerWrapper obj = new AdControllerWrapper();

    private AdControllerWrapper() {
    }

    public static AdControllerWrapper getInstance() {
        return obj;
    }

    /**
     * 获取广告的KEY值
     *
     * @param pos 广告位
     * @param id  广告ID
     * @return
     */
    public static String getAdKey(int pos, int id, String action) {
        //key = MD5( 广告位 + 广告ID + 广告动作)
        String key = MD5FileUtil.getMD5String(pos + "|" + id + "|" + action);
        return key == null ? "" : key;
    }

    /**
     * @desc 清空缓存的广告信息
     * @author linliangbin
     * @time 2017/9/8 12:26
     */
    public boolean clearCacheAd() {
        boolean effective = false;
        try {
            effective = AdDBHelper.getInstance().restore();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return effective;
    }

    public boolean clearCacheAd(int pos) {
        try {
            return AdDBHelper.getInstance().restore(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void updateAdTime(AdvertSDKManager.AdvertInfo targetAd, int pos) {

        if (targetAd == null) {
            return;
        }
        AdDBHelper.SimpleAdInfo simple = AdDBHelper.getInstance().query(getAdKey(pos, targetAd.id, targetAd.actionIntent));
        if (simple == null) {
            return;
        }
        AdDBHelper.getInstance().updateCount(simple.key, simple.currentCount, System.currentTimeMillis(), simple.state);

    }

    public void updateAdState(AdvertSDKManager.AdvertInfo targetAd, AdState state, int pos) {

        if (targetAd == null) {
            return;
        }
        AdDBHelper.SimpleAdInfo simple = AdDBHelper.getInstance().query(getAdKey(pos, targetAd.id, targetAd.actionIntent));
        if (simple == null) {
            return;
        }
        AdDBHelper.getInstance().updateCount(simple.key, simple.currentCount, simple.lastTime, state);

    }

    /**
     * @desc 缓存全屏广告信息
     * @author linliangbin
     * @time 2017/9/8 11:46
     */
    public void saveNetworkAd(Context context, final int pos) {

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {

                List<AdvertSDKManager.AdvertInfo> list = AdvertSDKController.getAdvertInfos(Global.context, pos + "");
                AdDBHelper helper = AdDBHelper.getInstance();
                if (EmptyUtils.isEmpty(list)) {
                    clearCacheAd(pos);
                } else {
                    for (AdvertSDKManager.AdvertInfo targetAd : list) {
                        if (targetAd != null) {

                            int showTimes = 0;
                            int showInterval = 0;
                            AdDBHelper.SimpleAdInfo simple = helper.query(getAdKey(pos, targetAd.id, targetAd.actionIntent));
                            if (simple != null) {
                                helper.update(simple.key, format(targetAd), simple.showCount, simple.showInterval,
                                        simple.currentCount, simple.lastTime, simple.state);
                            } else {
                                AdState currentState = getState(showTimes, showInterval, 0, 0);
                                helper.insert(getAdKey(pos, targetAd.id, targetAd.actionIntent), pos,
                                        format(targetAd), showTimes, showInterval, 0, 0, currentState);
                            }
                        }
                    }
                }
            }
        });

    }

    /**
     * @desc 获取下一个可以展示的广告
     * return null 无可展示的广告
     * @author linliangbin
     * @time 2017/9/8 12:28
     */
    public AdvertSDKManager.AdvertInfo getNextAd(int position) {
        List<AdDBHelper.SimpleAdInfo> simpleAds = AdDBHelper.getInstance().query(position);
        if (EmptyUtils.isEmpty(simpleAds)) {
            return null;
        }
        for (AdDBHelper.SimpleAdInfo simple : simpleAds) {
            //点击过关闭的广告
            if (simple.state == AdState.FORCECLOSE) {
                continue;
            }
            AdvertSDKManager.AdvertInfo adInfo = parse(simple.content);
            if (adInfo == null) {
                continue;
            }

            //已经过了有效期的广告
            if (System.currentTimeMillis() - DateUtil.strToDateLong(adInfo.endTime).getTime() >= 0) {
                continue;
            }
            //展示时间间隔是否已经达到
            if (simple.canShowNow()) {
                return adInfo;
            }
        }
        return null;
    }


    private AdState getState(int showCount, int showInterval, int currentCount, long lastTime) {
        return AdState.getState(showCount, showInterval, currentCount, lastTime);
    }


    /**
     * 序列化字符转化为相应对象
     *
     * @param content
     * @return
     */
    private AdvertSDKManager.AdvertInfo parse(String content) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            String resSet = URLDecoder.decode(content, "UTF-8");
            bais = new ByteArrayInputStream(resSet.getBytes("ISO-8859-1"));
            ois = new ObjectInputStream(bais);
            AdvertSDKManager.AdvertInfo info = (AdvertSDKManager.AdvertInfo) ois.readObject();
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 对象转化为相应的序列化字符
     *
     * @param info
     * @return
     */
    private String format(Object info) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(info);
            String objSet = baos.toString("ISO-8859-1");
            return URLEncoder.encode(objSet, "UTF-8");
        } catch (Exception e) {

        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
