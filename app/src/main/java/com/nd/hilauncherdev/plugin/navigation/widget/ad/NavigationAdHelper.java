package com.nd.hilauncherdev.plugin.navigation.widget.ad;

import android.content.Context;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.nd.hilauncherdev.plugin.navigation.pluginAD.AdViewManager.handler;

/**
 * Created by linliangbin on 2017/4/11 20:17.
 */

public class NavigationAdHelper {

    /**
     * 广告曝光信息保存路径
     */
    private static String AD_NAVIGATION_SAVE_PATH =  CommonGlobal.getBaseDir() + "/caches/navigation/ad";


    /** 当前内存缓存的广告列表 */
    private static LinkedList<AdvertSDKManager.AdvertInfo> adList;



    /**
     * 获取当前选中的广告信息
     * @return
     */
    public static AdvertSDKManager.AdvertInfo getNowChangeAdvertInfo(){
        if (adList == null || adList.size() == 0){
            //不存在缓存数据去SD卡里捞数据
            List<AdvertSDKManager.AdvertInfo> sdcardCache = getSDcardCacheAdvertInfo();
            if (sdcardCache != null && sdcardCache.size() > 0 ){
                //打乱广告列表
//                shuffle(sdcardCache);
                Collections.shuffle(sdcardCache, new Random(System.currentTimeMillis()));
                adList = new LinkedList<AdvertSDKManager.AdvertInfo>();
                adList.addAll(sdcardCache);
            }
        }
        if (adList != null && adList.size() > 0){
            AdvertSDKManager.AdvertInfo advertInfo = adList.poll();
            adList.add(advertInfo);
            return advertInfo;
        }else {
            return null;
        }
    }



    /**
     * 获取SD卡缓存下来的广告对象
     * @return
     */
    private static List<AdvertSDKManager.AdvertInfo> getSDcardCacheAdvertInfo(){
        File file = new File(AD_NAVIGATION_SAVE_PATH);
        if (!file.exists()){
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<AdvertSDKManager.AdvertInfo> list = (List<AdvertSDKManager.AdvertInfo>)ois.readObject();
            ois.close();
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    /**
     * @desc 获取零屏广告数据并将其保存到本地
     * @author linliangbin
     * @time 2017/4/11 20:16
     */
    public static void loadNavigationAd(final Context context){

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<AdvertSDKManager.AdvertInfo> adList = AdvertSDKController.getAdvertInfos(context,"52");
                File file = new File(AD_NAVIGATION_SAVE_PATH);
                if (file.exists()){//缓存已存在则删除
                    file.delete();
                }else if (!file.getParentFile().exists()){//不存在父文件夹，直接创建
                    file.getParentFile().mkdirs();
                }
                if (adList == null || adList.size() == 0){
                    return;
                }
                //序列化对象到文件中
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                    oos.writeObject(adList);
                    oos.flush();
                    oos.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                if(handler != null ){
                    Global.runInMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if(NavigationAdHelper.adList != null) {
                                NavigationAdHelper.adList.clear();
                            }
                        }
                    });
                }
            }
        });
    }


}
