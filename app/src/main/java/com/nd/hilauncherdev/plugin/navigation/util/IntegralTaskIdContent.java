package com.nd.hilauncherdev.plugin.navigation.util;

import com.nd.hilauncherdev.plugin.navigation.CardManager;

/**
 * 积分任务id常量列表
 * <p>Title: IntegralTaskIdContent</p>
 * <p>Description: </p>
 * <p>Company: ND</p>
 * @author    MaoLinnan
 * @date       2015年10月21日
 */
public class IntegralTaskIdContent {




    /*******************************************************************************/
    /****************** 		    V7积分任务ID号赋予规则		   *********************/
    /****************** 	          桌面版本号4位+系列号2位+内部序号2位                    *********************/
    /******************  最终需要在cosimple后台对应位置绑定taskid才会生效    *********************/
    /*******************************************************************************/
    /**
     * 01、搜索一下
     */
    //使用0屏搜索\上滑菜单的搜索打开百度搜索
    public static final int SEARCH_USE_NAVIGATION_OR_LAUNCHER_MENU_SEARCH = 70000101;
    //个性化-搜索页
    public static final int SEARCH_THEME_SHOP_SEARCH = 70000102;
    //热门游戏-游戏搜索
    public static final int SEARCH_HOT_GAME_GAME_SEARCH = 70000103;
    //应用商店-应用搜索
    public static final int SEARCH_APP_STORE_APP_SEARCH = 70000104;
    //推荐文件夹中的应用搜索
    public static final int SEARCH_RECOMMEND_FOLDER_APP_SEARCH = 70000105;
    //匣子搜索
    public static final int SEARCH_DRAWER_SEARCH = 70000106;

    /**
     * 02、下载推荐游戏
     */
    //SP=31下载的游戏被激活
    public static final int DOWNLOAD_RECOMMEND_GAME_THEME_RECOMMEND_GAME = 70000201;
    //热门游戏-推荐banner被点击
    public static final int DOWNLOAD_RECOMMEND_GAME_HOT_GAME_RECOMMEND_BANNER = 70000202;
    //热门游戏-推荐点击下载按钮
    public static final int DOWNLOAD_RECOMMEND_GAME_HOT_GAME_RECOMMEND_DOWNLOAD = 70000203;
    //热门游戏-榜单点击下载按钮
    public static final int DOWNLOAD_RECOMMEND_GAME_HOT_GAME_RANK_DOWNLOAD = 70000204;
    //热门游戏-游戏详情点击下载
    public static final int DOWNLOAD_RECOMMEND_GAME_HOT_GAME_DETAIL_DOWNLOAD = 70000205;

    /**
     * 03、点击搜索热门图标
     */
    //0屏图标icon
    public static final int NAVIGATION_ICON = 70000301;
    //0网址导航
    public static final int NAVIGATION_RECOMMEND_WEB = 70000302;

    /**
     * 04、点击浏览热点
     */
    //百度搜索小部件的热词
    public static final int BAIDU_WIDGET_HOTWORD = 70000401;
    //0屏实时热点
    public static final int NAVIGATION_REALTIME_HOTWORD = 70000402;
    //0屏小说阅读
    public static final int NAVIGATION_STORY = 70000403;
    //0屏购物指南
    public static final int NAVIGATION_SHOPPING_GUIDE = 70000404;
    //0屏品牌特卖
    public static final int NAVIGATION_SHOPPING_BRAND_SALE = 70000405;
    //0屏热门游戏
    public static final int NAVIGATION_HOT_GAMES = 70000406;


    /**
     * 05、欣赏广告
     */
    //点击广点通或淘宝广告条
    public static final int ADMIRE_ADS_TENCENT_OR_TAOBAO_BANNER = 70000501;
    //个性化-推荐页点击4个banner
    public static final int ADMIRE_ADS_THEME_SHOP_RECOMMEND_BANNER = 70000502;

    /**
     * 06、下载推荐应用
     */
    //SP=27下载的应用被激活（积分墙）
    public static final int DOWNLOAD_RECOMMEND_APP_JIFENQIANG = 70000601;
    //SP=1~35除31、27之外的应用被激活
    public static final int DOWNLOAD_RECOMMEND_APP_OTHER_APP = 70000602;
    //应用商店-推荐页banner点击
    public static final int DOWNLOAD_RECOMMEND_APP_APP_STORE_RECOMMEND_BANNER = 70000603;
    //应用商店-推荐页下载按钮点击
    public static final int DOWNLOAD_RECOMMEND_APP_APP_STORE_RECOMMEND_DOWNLOAD = 70000604;
    //应用商店-热门页点击下载
    public static final int DOWNLOAD_RECOMMEND_APP_APP_STORE_HOT_DOWNLOAD = 70000605;
    //应用商店-应用详情页点击下载
    public static final int DOWNLOAD_RECOMMEND_APP_APP_STORE_APP_DETAIL_DOWNLOAD = 70000606;

    /**
     * 07、欣赏开机大屏页面
     */
    //个性化、应用商店、热门游戏loading界面
    public static final int ADMIRE_LOADING = 70000701;

    /**
     * 08、分享
     */
    //主题分享
    public static final int SHARE_THEME_SHARE = 70000801;
    //美图制作并分享
    public static final int SHARE_THEME_MITO_SHARE = 70000802;
    //DIY主题制作并分享
    public static final int SHARE_THEME_DIY_THEME_SHARE = 70000803;

    /**
     * 09、设置默认桌面
     */
    //设置默认桌面成功
    public static final int SETTING_DEFAULT_LAUNCHER = 70000901;

    /**
     * 10、个性化资源下载
     */
    //主题下载成功
    public static final int THEME_SHOP_THEME_DOWNLOAD_SUCCESS = 70001001;
    //锁屏、图标、时钟天气、通讯录、输入法下载成功
    public static final int THEME_SHOP_LOCK_ICON_WEATHER_ADDRESSLIST_INPUTMETHOD_DOWNLOAD_SUCCESS = 70001002;
    //铃声、字体下载成功
    public static final int THEME_SHOP_RING_FONT_DOWNLOAD_SUCCESS = 70001003;
    //壁纸下载成功
    public static final int THEME_SHOP_WALLPAPER_DOWNLOAD_SUCCESS = 70001004;

    /**
     * 11、桌面美化
     */
    //长按添加小插件成功
    public static final int LAUNCHER_GLORIFY_LONGTOUCH_ADD_WIDGET = 70001101;
    //长按添加应用成功
    public static final int LAUNCHER_GLORIFY_LONGTOUCH_ADD_APP = 70001102;
    //更换主题成功
    public static final int LAUNCHER_GLORIFY_CHANGE_THEME = 70001103;

    /**
     * 12、参与活动
     */
    //参与每日新鲜活动点击
    public static final int PARTAKE_ACTIVITY_EVERYDAY_NEW_ACTIVITY = 70001201;
    //点击幸运转盘
    public static final int PARTAKE_ACTIVITY_LELELUCKY = 70001202;


    public static final int getTaskIDbyCard(int cardId) {
        switch (cardId) {
            case CardManager.CARD_SITE_TYPE:
                return NAVIGATION_RECOMMEND_WEB;
            case CardManager.CARD_HOT_WORD_TYPE:
                return NAVIGATION_REALTIME_HOTWORD;
            case CardManager.CARD_BOOK_TYPE:
                return NAVIGATION_STORY;
            case CardManager.CARD_SHOPPING_TYPE:
                return NAVIGATION_SHOPPING_GUIDE;
            case CardManager.CARD_VPH_AD_TYPE:
                return NAVIGATION_SHOPPING_BRAND_SALE;
            case CardManager.CARD_GAME_TYPE:
                return NAVIGATION_HOT_GAMES;
        }
        return 0;
    }
}
