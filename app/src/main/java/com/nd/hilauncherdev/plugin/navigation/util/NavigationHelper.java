package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.NavigationPreferences;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.openpage.PageCountSetter;

import java.util.HashMap;
import java.util.Map;

/**
 * 此类中的方法由桌面在指定的时机通过反射调用
 * 不可混淆
 * Created by linliangbin on 2016/9/13.
 */
public class NavigationHelper {

    
    /**
     * 处理桌面对零屏的每日统计回调
     */
    public static void handleNavigationUsingStateStastics(Context context){

        if(LauncherBranchController.isNavigationForCustomLauncher()){
            //零屏资讯屏打点统计,相同包名的定制版使用不同的lable
            int navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_LITIAN;

            if(LauncherBranchController.isTianpan(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_TIANPAN;
            }else if(LauncherBranchController.isFanYue(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_FANYUE;
            }else if(LauncherBranchController.isXiangshu(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_XIANGSHUWEILAI;
            }else if(LauncherBranchController.isShuaJiJingLing(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_SHUAJIJINGLING;
            }else if(LauncherBranchController.isXinShiKong(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_XINSHIKONG;
            }else if(LauncherBranchController.isLieYing(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_LIEYING;
            }else if(LauncherBranchController.isZhangShuo(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_ZHANGSHUO;
            }else if(LauncherBranchController.isMinglai(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_MINGLAI;
            }else if(LauncherBranchController.isShenlong(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_SHENLONG;
            }else if(LauncherBranchController.is2345(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_2345;
            }else if(LauncherBranchController.isHot(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_HOT;
            }else if(LauncherBranchController.isMohuShidai(context)){
                navigationAnaId = AnalyticsConstant.LAUNCHER_INFORMATION_TYPE_MOGU;
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowSohuPage()) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "sh");
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowNewsPage()) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "wy");
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowFenghuangPage()) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "fh");
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowSearchPage()) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "0p");
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowTaobaoExPage()) {
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "ntb");
            }

            if (NavigationPreferences.getInstance(NavigationView2.activity).isShowIreaderAndTaobaoPage()){
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, navigationAnaId, "ireatb");
            }

        }else{
            //桌面版本统计ID
            if(NavigationPreferences.getInstance(context).isShowSohuPage()){
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.LAUNCHER_INFORMATION_TYPE, "sh");
            }
            if(NavigationPreferences.getInstance(context).isShowNewsPage()){
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.LAUNCHER_INFORMATION_TYPE, "wy");
            }
        }

    }


    /**
     * @desc 根据当前模式初始化各个屏幕的显示顺序
     * @author linliangbin
     * @time 2017/2/8 15:11
     */
    public static void initPageByModeForCustomLauncher(Context context){

        // 定制版显示逻辑
        int mode = ConfigPreferences.getInstance(context).getNavigationNewsPageShowMode();
        NavigationPreferences.getInstance(context).setOpenPageMode(0);
        NavigationPreferences.getInstance(context).setShowIreaderPage(false);
        NavigationPreferences.getInstance(context).setShowNewsPage(false);
        NavigationPreferences.getInstance(context).setShowTaobaoPage(false);
        NavigationPreferences.getInstance(context).setShowSearchPage(false);
        NavigationPreferences.getInstance(context).setShowSohuPage(false);
        NavigationPreferences.getInstance(context).setShowFenghuangPage(false);
        NavigationPreferences.getInstance(context).setShowIreaderAndTaobaoPage(false);
        NavigationPreferences.getInstance(context).setShowWebPage(false);


        switch (mode){
            case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_NETEASE:
                NavigationPreferences.getInstance(context).setShowNewsPage(true);
                break;
            case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_SOHU:
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                break;
            case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_SEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_EMPTY:
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_SOHU_AND_SEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_SEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowNewsPage(true);
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG:
                NavigationPreferences.getInstance(context).setShowFenghuangPage(true);

                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG_AND_SEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowFenghuangPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_TAOBAOEX:
                NavigationPreferences.getInstance(context).setShowTaobaoExPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_SEARCH_TAOBAOEX_SOHU:
                NavigationPreferences.getInstance(context).setShowTaobaoExPage(true);
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_DZSEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowIreaderAndTaobaoPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_SOHU_AND_DZSEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowIreaderAndTaobaoPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_DZSEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowIreaderAndTaobaoPage(true);
                NavigationPreferences.getInstance(context).setShowNewsPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE:
                NavigationPreferences.getInstance(context).setShowIreaderAndTaobaoPage(true);
                NavigationPreferences.getInstance(context).setShowFenghuangPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_WEB:
                NavigationPreferences.getInstance(context).setShowWebPage(true);
                break;
            case ConfigPreferences.NAVIGATION_PAGE_MODE_SEARCH_WEB_SOHU:
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                NavigationPreferences.getInstance(context).setShowWebPage(true);
                break;

        }
    }

    /**
     * 处理新增用户对零屏的处理(所有渠道)
     */
    public static void handleNavigationForNewAllChannel(Context context){

        if(CommonGlobal.isBaiduLauncher(context)){

            NavigationPreferences.getInstance(context).setOpenPageMode(0);
            NavigationPreferences.getInstance(context).setShowNewsPage(true);
            NavigationPreferences.getInstance(context).setShowTaobaoPage(false);
            NavigationPreferences.getInstance(context).setShowSearchPage(true);
            NavigationPreferences.getInstance(context).setShowSohuPage(false);

        }else if(!LauncherBranchController.isNavigationForCustomLauncher()){
            //4.0 以上桌面显示搜狐新闻屏，4.0以下（包括）桌面显示网易新闻屏
            if(TelephoneUtil.getApiLevel() > 14){
                NavigationPreferences.getInstance(context).setOpenPageMode(0);
                NavigationPreferences.getInstance(context).setShowNewsPage(false);
                NavigationPreferences.getInstance(context).setShowTaobaoPage(false);
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(true);
                NavigationPreferences.getInstance(context).setSohuPageOnWhenUpgradTo761(true);
            }else{
                NavigationPreferences.getInstance(context).setOpenPageMode(0);
                NavigationPreferences.getInstance(context).setShowNewsPage(true);
                NavigationPreferences.getInstance(context).setShowTaobaoPage(true);
                NavigationPreferences.getInstance(context).setShowSearchPage(true);
                NavigationPreferences.getInstance(context).setShowSohuPage(false);
            }
        }


    }

    /**
     * 处理新增用户对零屏的处理（仅安智渠道）
     */

    public static void handleNavigationForNewAnzhi(Context context){
        
        NavigationPreferences.getInstance(context).setShowNewsPage(false);
        NavigationPreferences.getInstance(context).setShowSearchPage(false);
        NavigationPreferences.getInstance(context).setShowTaobaoPage(false);
        NavigationPreferences.getInstance(context).setShowSohuPage(false);

    }


    /**
     * 处理升级时对零屏显示逻辑的处理（所有渠道）
     */
    public static void handleNavigationForUpgradeAllChannel(Context context) {

        if(LauncherBranchController.isNavigationForCustomLauncher()){

        }else if(CommonGlobal.isBaiduLauncher(context)){

        }else if(CommonGlobal.isAndroidLauncher(context) || CommonGlobal.isDianxinLauncher(context)){
            int lastVersionCode = ConfigPreferences.getInstance(context).getLastVersionCode();
            /** 第一个同步91桌面零屏代码的点心桌面版本 */
            if(lastVersionCode <= 6230){
                NavigationPreferences NaviPreferences = NavigationPreferences.getInstance(context);

                if (TelephoneUtil.getApiLevel() > 14) {
                    NaviPreferences.setShowSohuPage(true);
                    NaviPreferences.setShowTaobaoPage(false);
                    NaviPreferences.setOpenPageMode(0);
                    NaviPreferences.setShowNewsPage(false);
                } else {
                    NaviPreferences.setShowTaobaoPage(true);
                    NaviPreferences.setShowNewsPage(true);
                    NaviPreferences.setShowSohuPage(false);
                    NaviPreferences.setOpenPageMode(0);
                }
                //保存升级到新版本后的初始化状态，确定搜狐新闻屏展示顺序时需要用到
                NaviPreferences.setSohuPageOnWhenUpgradTo761(NaviPreferences.isShowSohuPage());
            }

        }else{
            int lastVersionCode = ConfigPreferences.getInstance(context).getLastVersionCode();
            int versionCodeFrom = ConfigPreferences.getInstance(context).getVersionCodeFrom();

            //V8.3 之前的升级用户，强制重新开启之前崩溃的搜狐新闻
            if(versionCodeFrom < 8298){
                if(ConfigPreferences.getInstance(context).getSohuPageCrashed()){
                    NavigationPreferences.getInstance(context).setShowSohuPage(true);
                    NavigationPreferences.getInstance(context).setShowNewsPage(false);
                    NavigationPreferences.getInstance(context).setOpenPageChange(true);
                    ConfigPreferences.getInstance(context).clearHasForceDisableSohuFalse();
                    ConfigPreferences.getInstance(context).clearSohuPageCrashed();
                }
            }
            //版本V761 逻辑 2016.08.03
            if (lastVersionCode < 7608) {
                NavigationPreferences NaviPreferences = NavigationPreferences.getInstance(context);

                if (TelephoneUtil.getApiLevel() > 14) {
                    if (NaviPreferences.isShowSohuPage()) {
                        NaviPreferences.setShowSohuPage(true);
                        NaviPreferences.setOpenPageMode(0);
                        NaviPreferences.setShowNewsPage(false);
                    } else {
                        NaviPreferences.setShowSohuPage(false);
                        NaviPreferences.setOpenPageMode(1);
                        NaviPreferences.setShowNewsPage(false);
                    }
                } else {
                    NaviPreferences.setShowSohuPage(false);
                    NaviPreferences.setOpenPageMode(0);
                }
                //保存升级到新版本后的初始化状态，确定搜狐新闻屏展示顺序时需要用到
                NaviPreferences.setSohuPageOnWhenUpgradTo761(NaviPreferences.isShowSohuPage());

            }
        }
    }

    /**
     * 处理升级时对零屏显示逻辑的处理（仅安智渠道）
     */
    public static void handleNavigationForUpgradAnzhi(Context context){
        if(TelephoneUtil.getApiLevel() > 14){
            NavigationPreferences.getInstance(context).setShowNewsPage(false);
            NavigationPreferences.getInstance(context).setShowSohuPage(true);
        }else{
            NavigationPreferences.getInstance(context).setShowNewsPage(true);
            NavigationPreferences.getInstance(context).setShowSohuPage(false);
        }
        NavigationPreferences.getInstance(context).setShowSearchPage(true);
        NavigationPreferences.getInstance(context).setShowTaobaoPage(true);
        
        // 执行正常的升级逻辑
        NavigationPreferences NaviPreferences = NavigationPreferences.getInstance(context);
        if (TelephoneUtil.getApiLevel() > 14) {
            if (NaviPreferences.isShowSohuPage()) {
                NaviPreferences.setShowSohuPage(true);
                NaviPreferences.setOpenPageMode(0);
                NaviPreferences.setShowNewsPage(false);
            } else {
                NaviPreferences.setShowSohuPage(false);
                NaviPreferences.setOpenPageMode(1);
                NaviPreferences.setShowNewsPage(false);
            }
        } else {
            NaviPreferences.setShowSohuPage(false);
            NaviPreferences.setOpenPageMode(0);
        }
        //保存升级到新版本后的初始化状态，确定搜狐新闻屏展示顺序时需要用到
        NaviPreferences.setSohuPageOnWhenUpgradTo761(NaviPreferences.isShowSohuPage());
        
    }


    /**
     * 处理开放屏设定中设置改变的事件
     */
    public static void handleNavigationOpenPageSettingChanged(Context context,String key,boolean isChecked){
//        Log.i("llbeing","handleNavigationOpenPageSettingChanged");
        if (SettingsConstants.SETTINGS_NEWS_PAGE_SHOW.equals(key)) {
            NavigationPreferences.getInstance(context).setShowNewsPage(isChecked);
            NavigationPreferences.getInstance(context).setOpenPageChange(true);
        } else if (SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW.equals(key)) {
            NavigationPreferences.getInstance(context).setShowTaobaoPage(isChecked);
            NavigationPreferences.getInstance(context).setOpenPageChange(true);
        } else if(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW.equals(key)){
            NavigationPreferences.getInstance(context).setShowSohuPage(isChecked);
            NavigationPreferences.getInstance(context).setOpenPageChange(true);
        } else if(SettingsConstants.SETTINGS_SEARCH_PAGE_SHOW.equals(key)){
            NavigationPreferences.getInstance(context).setShowSearchPage(isChecked);
            NavigationPreferences.getInstance(context).setOpenPageChange(true);
        } else if(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW.equals(key)){
            NavigationPreferences.getInstance(context).setShowIreaderPage(isChecked);
            NavigationPreferences.getInstance(context).setOpenPageChange(true);
        }
    }


    /**
     * 处理零屏每次初始化之前的回调，目前主要处理搜狐新闻异常情况
     */
    public static void handleNavigationWhenInflateView(Context context){
        if(LauncherBranchController.isNavigationForCustomLauncher()){
            if(NavigationPreferences.getInstance(context).isShowSohuPage() && ConfigPreferences.getInstance(context).getSohuPageCrashed()){
                /** 处理搜狐新闻出现崩溃的情况 */
                int mode = ConfigPreferences.getInstance(context).getNavigationNewsPageShowMode();
                if(mode == ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_SOHU){
                    ConfigPreferences.getInstance(context).setNavigationNewsPageShowMode(ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_NETEASE);
                }else if(mode == ConfigPreferences.NAVIGATION_PAGE_MODE_SOHU_AND_SEARCH_PAGE){
                    ConfigPreferences.getInstance(context).setNavigationNewsPageShowMode(ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_SEARCH_PAGE);
                }
            }
            if(NavigationPreferences.getInstance(context).isShowFenghuangPage() && ConfigPreferences.getInstance(context).getFenghuangPageCrashed()){
                /** 处理凤凰新闻出现崩溃的情况 */
                int mode = ConfigPreferences.getInstance(context).getNavigationNewsPageShowMode();
                if(mode == ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG){
                    ConfigPreferences.getInstance(context).setNavigationNewsPageShowMode(ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_NETEASE);
                }else if(mode == ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG_AND_SEARCH_PAGE){
                    ConfigPreferences.getInstance(context).setNavigationNewsPageShowMode(ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_SEARCH_PAGE);
                }
            }
            initPageByModeForCustomLauncher(context);
        }else{
            //如果服务端强制关闭，则关闭搜狐新闻屏，改成显示网易新闻屏
            if((ConfigPreferences.getInstance(context).getSohuPageEnable() == ConfigPreferences.SOHU_PAGE_DISABLE &&
                    NavigationPreferences.getInstance(context).isShowSohuPage()) ||
                    (ConfigPreferences.getInstance(context).getSohuPageCrashed() && NavigationPreferences.getInstance(context).isShowSohuPage())){
                NavigationPreferences.getInstance(context).setShowSohuPage(false);
                NavigationPreferences.getInstance(context).setShowNewsPage(true);
                NavigationPreferences.getInstance(context).setOpenPageChange(true);
                ConfigPreferences.getInstance(context).setHasForceDisableSohu();
            }
        }

    }


    /**
     * 返回开放屏设置中显示的<key,标题>列表
     * 设置的key 作为map 的key
     * 设置的title + "|" + "true/false" 作为map 的title
     * @return
     */

    public static HashMap<String,String> getOpenPageSettingList(Context context){

        HashMap<String,String> keyTitleList = new HashMap<String,String>();

        keyTitleList.put(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW,"资讯|"+Boolean.TRUE);
        keyTitleList.put(SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW,"购物|"+Boolean.TRUE);
        keyTitleList.put(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW,"搜狐|"+Boolean.TRUE);
        if(CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)){
            keyTitleList.put(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW,"小说|"+Boolean.TRUE);
        }

        if(NavigationPreferences.getInstance(context).getOpenPageMode() == 1){
            keyTitleList.remove(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW);
            keyTitleList.remove(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW);
        }

        if(!NavigationPreferences.getInstance(context).getSohuPageOnWhenUpgradTo761()){
            //升级上来时不显示搜狐
            keyTitleList.remove(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW);
        }else{
            //升级上来是显示搜狐的，并且后面搜狐没有被强制关闭（崩溃或是服务端开关），则关闭新闻设置
            if(ConfigPreferences.getInstance(context).hasForcedDisableSohuBefore()){
                keyTitleList.remove(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW);
            }else{
                keyTitleList.remove(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW);
            }
        }

        for(Map.Entry<String,String> entry: keyTitleList.entrySet()){
            String key = entry.getKey();
    
            try {
                if(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW.equals(key)){
                    keyTitleList.put(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW,"资讯|"+NavigationPreferences.getInstance(context).isShowNewsPage());
                }else if(SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW.equals(key)){
                    keyTitleList.put(SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW,"购物|"+NavigationPreferences.getInstance(context).isShowTaobaoPage());
                }else if(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW.equals(key)){
                    keyTitleList.put(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW,"资讯|"+NavigationPreferences.getInstance(context).isShowSohuPage());
                }else if(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW.equals(key)){
                    keyTitleList.put(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW,"阅读|"+NavigationPreferences.getInstance(context).isShowIreaderPage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }

    
        }
    

        return keyTitleList;

    }

    /**
     * 获取当前开放屏屏幕数
     * @return
     */
    public static int getNavigationOpenPageCount(Context context){
        if(LauncherBranchController.isNavigationForCustomLauncher()){
            return PageCountSetter.getInstance().getChildPageCount();
        }else{
            return NavigationPreferences.getInstance(context).getOpenPageCount();
        }
    }
    
    
    /**
     * 获取当前是否显示快速搜索屏
     * @param context
     * @return
     */
    public static boolean isShowSearchPage(Context context){
        
        return NavigationPreferences.getInstance(context).isShowSearchPage();
    }


    /**
     * @desc 是否显示搜狐新闻屏
     * @author linliangbin
     * @time 2017/7/26 13:49
     */
    public static boolean isShowSohuPage(Context context){
        return NavigationPreferences.getInstance(context).isShowSohuPage();
    }


    /**
     * @desc 设置是否需要重新加载本地阅读数据
     * @author linliangbin
     * @time 2017/8/9 15:03
     */
    public static void setNeedReloadReadHistory(boolean isNeed){
        BookShelfLoader.mayNeedReloadRead = isNeed;
    }
}
