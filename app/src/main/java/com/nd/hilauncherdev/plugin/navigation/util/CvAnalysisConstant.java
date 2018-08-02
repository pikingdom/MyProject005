package com.nd.hilauncherdev.plugin.navigation.util;

/**
 * CV统计常量
 * 编号和内容找姜维或者卢松攀拿，切勿自行添加
 * <p>Title: CvAnalysisConstant</p>
 * <p>Description: </p>
 * <p>Company: ND</p>
 *
 * @author MaoLinnan
 * @date 2015年11月23日
 */
public class CvAnalysisConstant {

    /***********************************
     * resType常量
     *******************************************/
    public static final int RESTYPE_ADS = 1;//广告
    public static final int RESTYPE_APP = 2;//应用
    public static final int RESTYPE_THEME = 3;//主题
    public static final int RESTYPE_RING = 4;//铃声
    public static final int RESTYPE_WALLPAPER = 5;//壁纸
    public static final int RESTYPE_FONT = 6;//字体
    public static final int RESTYPE_THEME_SERIES = 7;//桌面主题系列
    public static final int RESTYPE_PASTER = 8;//桌面贴纸
    public static final int RESTYPE_MITO = 9;//美图作品
    public static final int RESTYPE_POTO = 10;//Po图
    public static final int RESTYPE_THEME_MODULE = 11;//主题模块资源
    public static final int RESTYPE_LINKS = 12;//网址链接
    public static final int RESTYPE_CUSTOM_TIPS = 13;//自定义标签
    public static final int RESTYPE_CARDS = 14;//卡片
    public static final int RESTYPE_OTHER = 100;//其他，不能为空

    /***********************************CV点位*****************************************/
    /**
     * 个性化
     */
    //个性化loading页
    public static final int THEME_SHOP_LOADING = 91010001;
    //主题详情页
    public static final int THEME_SHOP_THEME_DETAIL = 91010002;
    //个性化首页
    public static final int THEME_SHOP_HOMEPAGE = 91020101;
    //个性化-混搭-锁屏-详情页
    public static final int THEME_SHOP_MODULE_LOCKSCREEN_DETAIL = 91030102;
    //个性化-混搭-壁纸
    public static final int THEME_SHOP_MODULE_WALLPAPER = 91030201;
    //个性化-混搭-壁纸-详情页
    public static final int THEME_SHOP_MODULE_WALLPAPER_DETAIL = 91030202;
    //个性化-混搭-图标-详情页
    public static final int THEME_SHOP_MODULE_ICON_DETAIL = 91030302;
    //个性化-混搭-时钟天气-详情页
    public static final int THEME_SHOP_MODULE_WEATHER_DETAIL = 91030402;
    //个性化-混搭-通讯录-详情页
    public static final int THEME_SHOP_MODULE_SMS_DETAIL = 91030502;
    //个性化-混搭-输入法-详情页
    public static final int THEME_SHOP_MODULE_INPUT_DETAIL = 91030602;
    //个性化-混搭-字体-详情页
    public static final int THEME_SHOP_MODULE_FONT_DETAIL = 91030802;

    /**
     * 应用商店热门游戏
     */
    //应用商店loading页
    public static final int APP_STORE_LOADING = 92010001;
    //热门游戏loading页
    public static final int HOT_GAMES_LOADING = 93010002;

    /**
     * 导航屏
     */
    //进入0屏（**0屏统一整理点位**）
    public static final int NAVIGATION_SCREEN_INTO = 94010001;
    //0屏-icon-icon1
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON1 = 94010102;
    //0屏-icon-icon2
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON2 = 94010103;
    //0屏-icon-icon3
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON3 = 94010104;
    //0屏-icon-icon4
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON4 = 94010105;
    //0屏-icon-icon5
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON5 = 94010106;
    //0屏-icon-icon6
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON6 = 94010107;
    //0屏-icon-icon7
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON7 = 94010108;
    //0屏-icon-icon8
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON8 = 94010109;
    //0屏-icon-icon9
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON9 = 97030005;
    //0屏-icon-icon10
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_ICON10 = 97030006;



    //0屏-网址导航-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_NAVIGATION_HOTWORD = 94010202;
    //0屏-网址导航-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_NAVIGATION_CLICK = 94010203;
    
    //0屏-实时热点-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_REALTIME_HOTWORDS = 94010302;
    //0屏-实时热点-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_REALTIME_HOTWORDS_CLICK = 94010303;
    
    //0屏-今日头条-框上的热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD = 94010402;
    //0屏-今日头条-链接
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK = 94010403;
    
    //0屏-购物指南-框上的热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SHOP_GUIDE_HOTWORD = 94010502;
    //0屏-购物指南-链接
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SHOP_GUIDE_HOTWORD_CLICK = 94010503;
    //0屏-购物指南-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SHOP_RES_ID = -10000006;
    
    //0屏-小说阅读-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_HOTWORD_CLICK = 94010602;
    //0屏-小说阅读-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK = 94010603;
    //0屏-小说卡片-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID = -10000005;
    //0屏-书架卡片-resId-更多小说
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_MORE = -10000025;
    //0屏-书架卡片-resId-打开书架
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_SHELF = -10000035;
    //0屏-书架卡片-resId-进入书城
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_MAIN = -10000045;
    //0屏-书架卡片-resId-小说点击
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_BOOKCLICK = -10000055;


    //0屏-开心一刻-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_JOKE_CARD_HOTWORD_CLICK = 94010702;
    //0屏-开心一刻-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_JOKE_CARD_CLICK = 94010703;
    //0屏-开心一刻-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_JOKE_RES_ID = -10000024;
    
    
    //0屏-热门游戏-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_GAME_CARD_HOTWORD_CLICK = 94010802;
    //0屏-热门游戏-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_GAME_CARD_CLICK = 94010803;
    //0屏-开心一刻-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_GAME_RES_ID = -10000026;
    
    //0屏-品牌特卖-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_VPH_CARD_HOTWORD_CLICK = 94010902;
    //0屏-品牌特卖-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_VPH_CARD_CLICK = 94010903;
    //0屏-品牌特卖-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_VPH_RES_ID = -10000025;
    
    
    //0屏-每日美图-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_PIC_CARD_HOTWORD_CLICK = 94011002;
    //0屏-每日美图-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_PIC_CARD_CLICK = 94011003;
    //0屏-美图卡片-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_PIC_RES_ID = -10000002;
    
    //0屏-星座运势-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_STAR_CARD_HOTWORD_CLICK = 94011102;
    //0屏-星座运势-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_STAR_CARD_CLICK = 94011103;
    //0屏-星座运势-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_STAR_RES_ID = -10000003;
    

    //0屏-大资讯卡片-新闻
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_NEWS_CARD_NEWS = 94011202;
    //0屏-大资讯卡片-广告
    
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_NEWS_CARD_AD = 94011203;
    //0屏-趣发现-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_FUNNY_CARD_HOTWORD = 94011302;
    //0屏-趣发现-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_FUNNY_CARD_CLICK = 94011303;
    //0屏-趣发现-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_FUNNY_RES_ID = -10000027;
    
    //0屏-微订阅-热词
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_CARD_HOTWORD = 94011402;
    //0屏-微订阅-框
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_CARD_CLICK = 94011403;
    //0屏-微订阅-resId
    public static final int NAVIGATION_SCREEN_CLASSIFICATION_SUBSCRIBE_RES_ID = -10000023;

    //视频信息流视频广告
    public static final int NAVIGATION_SCREEN_VIDEO_LIST_VIDEO_AD = 94010110;

    //视频信息流视频广告
    public static final int NAVIGATION_SCREEN_VIDEO_LIST_BANNER_AD = 94010111;


    //--------------------淘宝购物屏-PageId,ResId,PosId start------------------//
    //零屏-淘宝购物屏-PageId
    public static final int TAOBAO_SCREEN_PAGEID = 97020001;
    //零屏-淘宝购物屏-单品列表-ResId
    public static final int TAOBAO_SCREEN_RESID = -10000012;
    //零屏-淘宝购物屏-顶部循环Banner
    public static final int TAOBAO_SCREEN_POS_BANNER = 97020101;
    //零屏-淘宝购物屏-分类导航
    public static final int TAOBAO_SCREEN_POS_CATE = 97020201;
    //零屏-淘宝购物屏-单品列表
    public static final int TAOBAO_SCREEN_POS_PRODUCT = 97020301;

    //零屏-淘宝购物屏-底部单张Banner
    public static final int TAOBAO_SCREEN_POS_SINGLEBANNER = 97030010;

    //零屏-淘宝购物屏-ResId other
    public static final int TAOBAO_SCREEN_RESIDEX = -10000011;
    //零屏-淘宝购物屏-搜索
    public static final int TAOBAO_SCREEN_POS_SEARCH = 97020401;
    //零屏-淘宝购物屏-头条
    public static final int TAOBAO_SCREEN_POS_HEADLINE = 97020501;
    //零屏-淘宝购物屏-限时抢购
    public static final int TAOBAO_SCREEN_POS_SPLASHSALE = 97020601;
    //零屏-淘宝购物屏-品牌特卖（左一）
    public static final int TAOBAO_SCREEN_POS_CPS_ONE = 97020701;
    //零屏-淘宝购物屏-9.9包邮（右上）
    public static final int TAOBAO_SCREEN_POS_CPS_TWO = 97020801;
    //零屏-淘宝购物屏-天天特价（右下）
    public static final int TAOBAO_SCREEN_POS_CPS_THREE = 97020901;
    //--------------------淘宝购物屏-PageId,ResId,PosId end------------------//

    //--------------------淘宝购物屏专辑详情-PageId,ResId,PosId start------------------//
    // 零屏-淘宝购物屏-专辑详情-PageId
    public static final int TAOBAO_COLLECTION_DETAIL_PAGE = 97030007;
    // 零屏-淘宝购物屏-专辑详情-单品列表
    public static final int TAOBAO_COLLECTION_DETAIL_POS_PRODUCT = 97030009;
    // 零屏-淘宝购物屏-专辑详情-跳转下个专辑按钮
    public static final int TAOBAO_COLLECTION_DETAIL_POS_NEXT = 97030008;

    //--------------------淘宝专辑详情-PageId,ResId,PosId end------------------//

    //--------------------91桌面搜狐新闻屏-PageId,ResId,PosId start------------------//
    public static final int SOHU_PAGE_ID = 97030001;
    //--------------------91桌面搜狐新闻屏-PageId,ResId,PosId end------------------//

    
    
    //--------------------点心桌面唯品会屏-PageId,ResId,PosId start------------------//
    public static final int DIANXIN_VIP_PAGE_ID = 47010001;
    //--------------------点心桌面唯品会屏-PageId,ResId,PosId end------------------//


    //--------------------点心桌面搜狐新闻屏-PageId,ResId,PosId start------------------//
    public static final int DIANXIN_SOHU_PAGE_ID = 47020001;
    //--------------------点心桌面搜狐新闻屏-PageId,ResId,PosId end------------------//

    //--------------------定制版凤凰新闻屏-PageId,ResId,PosId start------------------//
    public static final int DINGZHI_IFENG_PAGE_ID = 97030002;
    //--------------------点心桌面搜狐新闻屏-PageId,ResId,PosId end------------------//


    //--------------------点心桌面掌阅阅读屏 start -----------------------------------------//
    public static final int DIANXIN_IREADER_PAGE_ID = 95020002;
    public static final int DIANXIN_IREADER_POSITION_ID = 95020003;
    public static final int DIANXIN_IREADER_RESID_SEARCH = 3001;
    public static final int DIANXIN_IREADER_RESID_SHELF_ITEM = 3002;
    public static final int DIANXIN_IREADER_RESID_RECOMMEND_ITEM = 3003;
    public static final int DIANXIN_IREADER_RESID_OPEN_SHELF = 3004;
    public static final int DIANXIN_IREADER_RESID_ENTER_CENTER = 3005;
    public static final int DIANXIN_IREADER_RESID_CATEGORY = 3006;
    public static final int DIANXIN_IREADER_RESID_RANK = 3007;
    public static final int DIANXIN_IREADER_RESID_FREE = 3008;
    public static final int DIANXIN_IREADER_RESID_COMIC = 3009;
    //--------------------点心桌面掌阅阅读屏 end -------------------------------------------//


    //--------------------定制版掌阅阅读屏 start -----------------------------------------//
    public static final int DZ_IREADER_PAGE_ID = 97030003;
    public static final int DZ_IREADER_POSITION_ID = 97030004;
    //--------------------定制版掌阅阅读屏 end -------------------------------------------//

    //-------------------------------web屏 start-----------------------------------//
//    public static final int WEB_PAGE_ID = 97030005;
    public static final int WEB_PAGE_ID = 97030033;
    //-------------------------------web屏 end  -----------------------------------//



    //资讯屏Pageid
    public static final int OPEN_PAGE_NEWS_PAGE_ID = 97010001;
    //资讯屏大图 position id
    public static final int OPEN_PAGE_BIG_NEWS_POSTTION_ID = 97010101;
    //资讯屏大图 position id
    public static final int OPEN_PAGE_SMALL_NEWS_POSTTION_ID = 97010201;
    //资讯屏广告 position id
    public static final int OPEN_PAGE_AD_POSITION_ID = 97010301;
    /**
     * 资讯屏-UC新闻 position id
     */
    public static final int OPEN_PAGE_UC_NEWS_POSITION_ID = 97030003;
    /**
     * 资讯屏-UC新闻广告 position id
     */
    public static final int OPEN_PAGE_UC_AD_NEWS_POSITION_ID = 97030004;

    //资讯屏小图 res id
    public static final int OPEN_PAGE_BIG_NEWS_RES_ID = -10000014;

    //资讯屏小图 res id
    public static final int OPEN_PAGE_SMALL_NEWS_RES_ID = -10000015;

    //资讯屏UC新闻 res id
    public static final int OPEN_PAGE_UC_NEWS_RES_ID = 10000038;

    //资讯屏UC新闻广告 res id
    public static final int OPEN_PAGE_UC_AD_NEWS_RES_ID = 10000039;


    /**
     * 零屏-导航网址获取不同的Position ID
     *
     * @param position
     * @return
     */
    public static int getPositionID(int position) {
        switch (position) {
            case 0:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON1;
            case 1:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON2;
            case 2:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON3;
            case 3:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON4;
            case 4:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON5;
            case 5:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON6;
            case 6:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON7;
            case 7:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON8;
            case 8:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON9;
            case 9:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON10;
            default:
                return NAVIGATION_SCREEN_CLASSIFICATION_ICON1;
        }
    }
}
