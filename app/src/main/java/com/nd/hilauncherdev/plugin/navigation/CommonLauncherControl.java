package com.nd.hilauncherdev.plugin.navigation;

import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;


public class CommonLauncherControl {

    public static boolean DX_PKG=false;//点心桌面
    public static boolean AZ_PKG=false;//安卓桌面
    public static boolean BD_PKG=false;//百度桌面
    public static String PKG_NAME="com.nd.android.pandahome2";//桌面包名
    public static String LAUNCHER_NAME="91桌面";//桌面名
    public static String DOWNLOAD_URL="http://zm.ifjing.com";//桌面下载地址


    /**\
     * @Title: getLoadingBackgroad
     * @Description: TODO(获取dx_theme_shop_v6_loading_bj_mid)
     * @param @return    设定文件
     * @return int    返回类型
     */
    public static int getThemeLoadingBackgroad(){
        if (DX_PKG) {
            return R.drawable.dx_theme_shop_v6_loading_bj_mid;
        }else {
            return R.drawable.theme_shop_v6_loading_bj_mid;
        }
    }

    /**
     * @Title: getJrttNewBigBackgroad
     * @Description: TODO(获取dx_jrtt_new_big_bg)
     * @param @return    设定文件
     * @return int    返回类型
     */
    public static int getJrttNewBigBackgroad() {
        if (DX_PKG) {
            return R.drawable.dx_jrtt_new_big_bg;
        }else {
            return R.drawable.jrtt_new_big_bg;
        }
    }

    /**
     * @Title: getVph
     * @Description: TODO(获取Vph)
     * @param @return    设定文件
     * @return int    返回类型
     */
    public static int getVph() {
        if (DX_PKG) {
            return R.drawable.icon_loading_dx;
        }else {
            return R.drawable.vph;
        }
    }

    /**
     * @Title: getLoadingBackgroad
     * @Description: TODO(获取dx_loading_bgcf)
     * @param @return    设定文件
     * @return int    返回类型
     */
    public static int getLoadingBackgroad() {
        if (DX_PKG) {
            return R.drawable.dx_loading_bgcf;
        }else {
            return R.drawable.loading_bgcf;
        }
    }

    /**
     * @Title: getThemeNoFindSmall
     * @Description: TODO(获取icon_loading_dx)
     * @param @return    设定文件
     * @return int    返回类型
     */
    public static int getThemeNoFindSmall() {
        if (DX_PKG) {
            return R.drawable.icon_loading_dx;
        }else {
            return R.drawable.theme_shop_v6_theme_no_find_small;
        }
    }

    /**\
     * 根据包名各桌面初始化赋值
     * @Title: initLauncher
     * @param @param pkg    设定文件
     * @return void    返回类型
     */
    public static void initLauncher(String pkg) {
        PKG_NAME=pkg;
        switch (pkg) {
            case CommonGlobal.BAIDU_LAUNCHER_PKG_NAME:{
                CommonGlobal.BASE_DIR_NAME="/BaiduLauncher";
                LAUNCHER_NAME="百度桌面";
                BD_PKG=true;
                DOWNLOAD_URL="http://android.myapp.com/myapp/detail.htm?apkName=com.nd.android.smarthome";
            }
            break;
            case CommonGlobal.DIANXIN_LAUNCHER_PKG_NAME:{
                CommonGlobal.BASE_DIR_NAME="/Dianxinos";
                LAUNCHER_NAME="点心桌面";
                DX_PKG=true;
                DOWNLOAD_URL="http://android.myapp.com/myapp/detail.htm?apkName=com.dianxinos.dxhome";

            }
            break;
            case CommonGlobal.ANDROID_LAUNCHER_PKG_NAME:{
                CommonGlobal.BASE_DIR_NAME="/SmartHome";
                LAUNCHER_NAME="安卓桌面";
                AZ_PKG=true;
                DOWNLOAD_URL="http://android.myapp.com/myapp/detail.htm?apkName=com.nd.android.smarthome";

            }
            break;
            default:{
                CommonGlobal.BASE_DIR_NAME="/PandaHome2";
                LAUNCHER_NAME="91桌面";
                DOWNLOAD_URL="http://zm.91.com";
            }
            break;
        }
    }

    /**
     * 返回半透明背景loading图片
     * @return
     */
    public static int getLoadingBgTranscunt(){
        if(DX_PKG){
            return R.drawable.icon_loading_dx;
        }else{
            return R.drawable.loading_bg_transcunt;
        }
    }

}
