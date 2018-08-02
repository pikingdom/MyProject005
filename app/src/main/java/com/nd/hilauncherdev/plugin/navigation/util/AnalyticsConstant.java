package com.nd.hilauncherdev.plugin.navigation.util;

public class AnalyticsConstant {

	// 设置
	public static final int ZERO_SCREEN_SETTING = 14010903;
	// 导航图标（1-N：各位置）原有：label: 0-淘宝,1-爱奇艺,2-去哪儿,30-新浪,31-美团,32-安卓网
	public static final int EVENT_FIRST_SCREEN_ICON_CLICK = 14010901;
	// 导航链接（1-N：各位置）1~4 5为用户添加 6为自动生成
	public static final int EVENT_FIRST_SCREEN_LINK_CLICK = 14010902;
	// 二维码（1-0屏；2-百度插件）
	public static final int SEARCH_QR_CODE = 14010705;
	// 语音搜索（1-0屏；2-详情）
	public static final int SEARCH_VOICE_SEARCH = 14010704;
	// 桌搜索的入口分布(1-0屏；2-百度插件；3-常驻通知栏；4-桌面手势下滑)
	public static final int SEARCH_PERCENT_CONVERSION_SEARCH_INLET = 63101801;
	// 0屏网址导航卡片的流量分发效果
	public static final int NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT = 63301804;
	// 实时热点卡片的流量分发效果
	public static final int NAVIGATION_SCREEN_ACTUALTIME_CARD_DISTRIBUTE_EFFECT = 63301805;
	// 监测实今日头条卡片的流量分发效果
	public static final int NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT = 63301806;
	// 监测0屏卡片的分享
	public static final int NAVIGATION_SCREEN_CARD_SHARE = 63301807;
	// 监测0屏卡片的使用情况
	public static final int NAVIGATION_SCREEN_CARD_SERVICE_CONDITION = 1231554;
	// 0屏推荐词点击(点击位置)
	public static final int NAVIGATION_SCREEN_REOMMEND_WORD_CLICK = 66001809;
	//小说阅读卡片的流量分发效果（0-更多小说；1-阅读卡片1；2-阅读卡片2；3-阅读卡片3）
	public static final int NAVIGATION_SCREEN_STORY_CARD_DISTRIBUTE_EFFECT = 66001810;
	//小说阅读——我的栏目的流量分发效果(label:小说id)
	public static final int NAVIGATION_SCREEN_STORY_CARD_MY_COLUMN_DISTRIBUTE_EFFECT = 66001811;
	//小说阅读——推荐栏目的流量分发效果(label:小说id)
	public static final int NAVIGATION_SCREEN_STORY_CARD_RECOMMEND_COLUMN_DISTRIBUTE_EFFECT = 66001812;
	//小说阅读——加入入书架
	public static final int NAVIGATION_SCREEN_STORY_CARD_ADD_BOOKRACK = 66001813;
	//小说阅读——在线阅读
	public static final int NAVIGATION_SCREEN_STORY_CARD_ONLINE_READ = 66001814;
	//开心一刻
	public static final int NAVIGATION_SCREEN_HAPPY_MOMENT = 69101816;
	//0屏游戏卡片（hyp-换一批;gd-更多游戏;rm1~4-热门游戏1~4点击;h51~4-H5游戏1~4点击;zx1~2-咨询1~2点击）
	public static final int NAVIGATION_SCREEN_GAME_CARD = 70001818;
	//0屏唯品会卡片(gd-更多精品;ad1~5-广告1~5点击)
	public static final int NAVIGATION_SCREEN_VIPSHOP_CARD = 70001819;

	//0屏卡片-卡片推荐（mtmt-默认推美图;msmt-默认删美图;mtxz-默认推星座;msxz-默认删星座;gtxs-女生推小说;gsxs-女生删小说;
	//               gtgw-女生推购物;gsgw-女生删购物;btmt-男生推美图;bsmt-男生删美图;btyx-男生推游戏;bsyx-男生删游戏）
	public static final int NAVIGATION_SCREEN_CARDS_RECOMMEND = 71001820;

	//0屏卡片—下拉刷新(xl-下拉刷新；tj-添加卡片；gl-管理卡片)
	public static final int NAVIGATION_SCREEN_CARDS_PULL_REFRESH = 71001821;

	//0屏购物卡片显示页数（label代表1-10页及10页以上）
	public static final int NAVIGATION_SCREEN_SHOPPING_CARD_SHOW_PAGE = 73101824;

	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-非定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS = 75301827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-力天定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_LITIAN = 75301827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-2345定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_2345 =  78001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-HOT定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_HOT =  79001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-天湃定制版
	//由于旧版本错将力天定制版的数据打到了80001827，所以弃用该ID而使用80001828
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_TIANPAI = 80001828;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-天湃智桌面定制版（现改为天盘）
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_TIANPAN = 80101827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-帆悦定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_FANYUE = 81001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-刷机精灵定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_SHUAJIJINGLING = 82001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-椒盐定制版（现改为橡树未来）
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_XIANGSHUWEILAI = 83001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-猎鹰定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_LIEYING = 84001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-新时空定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_XINSHIKONG = 85001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-神龙定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_SHENLONG =  87001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-铭来定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_MINGLAI = 88001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-鼎开定制版（现改为掌硕定制版）
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_ZHANGSHUO = 89001827;
	//零屏-搜狐新闻(hr-成功滑入;hk-成功滑开)-蘑菇时代定制版
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_MOGU =  90001827;



	//0屏趣发现(dtdj-大图资讯点击;x1dj-小图资讯1点击;x2dj-小图资讯2点击;hyp-换一批点击;gd-更多发现点击;zxlb-资讯列表内的点击;zxfx-资讯分享点击;zxxq-资讯详情页面内点击)
	public static final int NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS = 75301826;
	//零屏趣发现卡片查看详情页面(label-咨询id)
	public static final int NAVIGATION_SCREEN_INTERESTING_DISCOVERY_DETAIL = 75301828;
	//桌面零屏资讯类型(sh-搜狐，wy-网易)
	public static final int LAUNCHER_INFORMATION_TYPE =  76000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-力天定制版
	public static final int LAUNCHER_INFORMATION_TYPE_LITIAN =  76000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-2345定制版
	public static final int LAUNCHER_INFORMATION_TYPE_2345 =  78000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-HOT定制版
	public static final int LAUNCHER_INFORMATION_TYPE_HOT =  79000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-天湃定制版
	public static final int LAUNCHER_INFORMATION_TYPE_TIANPAI =  80000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-天湃智桌面定制版（现改为天盘）
	public static final int LAUNCHER_INFORMATION_TYPE_TIANPAN =  80100002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-帆约定制版
	public static final int LAUNCHER_INFORMATION_TYPE_FANYUE =  81000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-刷机精灵定制版
	public static final int LAUNCHER_INFORMATION_TYPE_SHUAJIJINGLING =  82000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-椒盐定制版（现改为橡树未来）
	public static final int LAUNCHER_INFORMATION_TYPE_XIANGSHUWEILAI =  83000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-新时空定制版
	public static final int LAUNCHER_INFORMATION_TYPE_XINSHIKONG =  84000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-猎鹰定制版
	public static final int LAUNCHER_INFORMATION_TYPE_LIEYING =  85000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-神龙定制版
	public static final int LAUNCHER_INFORMATION_TYPE_SHENLONG =  87000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-铭来定制版
	public static final int LAUNCHER_INFORMATION_TYPE_MINGLAI =  88000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-鼎开定制版（现改为掌硕定制版）
	public static final int LAUNCHER_INFORMATION_TYPE_ZHANGSHUO =  89000002;
	//桌面零屏资讯类型(sh-搜狐，wy-网易 0p-零屏 fh-凤凰)-蘑菇时代定制版
	public static final int LAUNCHER_INFORMATION_TYPE_MOGU =  90000002;




	//零屏-搜狐新闻引导卡片点击
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_GUIDE_CARDS_CLICK = 76101830;
	//零屏微传阅卡片统计一级界面(hyp-换一批;gd-更多订阅;wzc-卡片文章点击;)
	public static final int NAVIGATION_SCREEN_CARD_SUBSCRIBE_FIRST_SCREEN = 76101831;
	//零屏微传阅卡片统计二级界面(jr-进入订阅号详情;wzc-二级页面的文章点击;tj-订阅号添加)
	public static final int NAVIGATION_SCREEN_CARD_SUBSCRIBE_SECOND_SCREEN = 76101832;
	//零屏-搜狐新闻引导  kz-卡片展示 kd-卡片点击 dz-动画展示
	public static final int NAVIGATION_SCREEN_SOUHU_NEWS_ANIM_GUIDE = 76201834;

	//新零屏画报屏(jr-进入 xz-下载 yy-应用)
	public static final int NAVIGATION_SCREEN_WALLPAPER = 85081836;

	//新零屏视频信息流(lbbf-列表播放 xc-小窗播放 qp-全屏播放 lbqc-列表视频播放去重)
	public static final int NAVIGATION_SCREEN_VIDEO_LIST = 85081837;
	//零屏凤凰新闻屏
	public static final int NAVIGATION_IFENG_NEWS_PAGE = 87001836;

	//0屏小说书架(gd-更多小说;dk-打开书架;jr-进入书城;dj-小说点击)
	public static final int NAVIGATION_SCREEN_STORY_BOOKRACK = 85302406;

	//掌阅阅读屏使用情况(ss-点击搜索;dsj-点击书架小说;dtj-点击推荐小说;sj-打开书架;sc-进入书城;fl-分类;ph-排行;mf-免费;mh-漫画)
	public static final int READ_SCREEN_USE_INFO = 86201837;

	//定制版零屏搜索框  zs-展示 dj-点击
	public static final int NAVIGATION_DZ_SEARCH_PAGE_SEARCH_INPUT = 87001837;

	//导航屏-web屏初始化
	public static final int NAVIGATION_WEB_PAGE_INIT = 86004002;
	//导航屏-web屏可见性(jr：进入；lk：离开)
	public static final int NAVIGATION_WEB_PAGE_VISIBILITY = 86004003;

	/**
	 * APP 统计状态
	 */
	/**
	 * 应用分发通用下载源：助手
	 *
	 * 从桌面代码移植而来
	 */
	//public static final int DOWNLOAD_FROM_91ASSIST_POOL = 0;

	//零屏今日头条app 下载链接
	public static final int SP_NAVIGATION_NEWS_APP_DOWNLOAD = 14;

	//资讯屏的广告位置ID
	public static  final  String NEWS_PAGE_AD_POSITION="30";


}
