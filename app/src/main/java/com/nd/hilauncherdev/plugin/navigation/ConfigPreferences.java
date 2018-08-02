package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.SharedPreferences;

import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * 配置文件
 * 
 * @author chenzhihong_9101910
 * 
 */
public class ConfigPreferences {

	/**
	 * 当前导航栏版本号
	 */
	public static final String CURRENT_NAVIGATION_VERSION = "current_navigation_version";
	/**
	 * 当前导航栏版本号 (海外版)
	 */
	public static final String CURRENT_NAVIGATION_VERSION_EN = "current_navigation_version_en";
	/**
	 * 当前导航栏网址图标版本号
	 */
	public static final String CURRENT_NAVIGATION_ICON_VERSION = "current_navigation_icon_version";
	/**
	 * 当前导航顶部推荐网址
	 */
	public static final String CURRENT_RECOMMEND_VERSION = "current_recommend_version";

	private static final String KEY_SOHU_WHOLE_PAGE_NEWS = "key_sohu_navigation_page_enable";

	private static final String KEY_SOHU_FORCE_DISABLE_PAGE = "key_sohu_force_disable_page";

	private static final String KEY_SOHU_PAGE_CRASHED = "key_sohu_page_crashed";

	private static final String KEY_FENGHUANG_PAGE_CRASHED = "key_fenghuang_page_crashed";

	private static final String KEY_NAVIGATION_SEARCH_TOUCH_ENABLE = "key_navigation_search_torch_enable";

	/**
	 * 零屏新闻屏展示模式,只在定制版使用
	 */
	private final static String KEY_NAVIGATION_NEWS_SHOW_MODE = "key_navigation_news_show_mode";

	/**
	 * 零屏是否出现webview 相关的崩溃
	 */
	private static final String KEY_NAVIGATION_WEBVIEW_CRASH = "key_navigation_webview_crash";

	/**
	 * 零屏强制关闭webview 界面
	 */
	private static final String KEY_NAVIGATION_WEBVIEW_FORCE_DISABLE = "key_navigation_force_disable_webview";
	/**
	 * 零屏返回键进入的屏Index
	 */
	private final static String KEY_NAVIGATION_MODE_BACK_KEY_PAGE_INDEX = "key_navigation_mode_back_key_page_index";



	public static final String KEY_WEB_BASED_INTENT_SCHEME_OPT = "key_web_based_intent_schemes_opt";
	public static final String KEY_WEB_BASED_INTENT_SCHEME_PROMPT = "key_web_based_intent_schemes_prompt";


	private static ConfigPreferences ap;
	private static SharedPreferences sp;

	private static final String SP_NAME = "configsp";

	private int currentNavigationVersion = 1;
	private int currentNavigationIconVersion = 0;
	private int currentRecommendVersion = 0;

	Context mContext;

	protected ConfigPreferences(Context ctx) {
		super();
		mContext = ctx;
		currentNavigationVersion = sp.getInt(getNavigationVersionCPName(ctx), 1);
		currentNavigationIconVersion = sp.getInt(CURRENT_NAVIGATION_ICON_VERSION, 0);
		currentRecommendVersion = sp.getInt(CURRENT_RECOMMEND_VERSION, 0);
	}

	public synchronized static ConfigPreferences getInstance(Context ctx) {
		if (sp == null) {
			sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}

		if (ap == null) {
			ap = new ConfigPreferences(ctx);
		}

		return ap;
	}

	public SharedPreferences getSP() {
		return sp;
	}

	/**
	 * 获取当前导航栏版本号
	 * 
	 * @return 版本号
	 */
	public int getCurrentNavigationVersion() {
		return currentNavigationVersion;
	}

	/**
	 * 设置当前导航栏版本号
	 * 
	 * @param currentNavigationVersion
	 *            版本号
	 */
	public void setCurrentNavigationVersion(Context ctx, int currentNavigationVersion) {
		this.currentNavigationVersion = currentNavigationVersion;
		sp.edit().putInt(getNavigationVersionCPName(ctx), currentNavigationVersion).commit();
	}

	/**
	 * 获取当前导航栏网址Icon版本号
	 * 
	 * @return 版本号
	 */
	public int getCurrentNavigationIconVersion() {
		return currentNavigationIconVersion;
	}

	/**
	 * 设置当前导航栏网址Icon版本号
	 * 
	 * @param currentNavigationIconVersion
	 *            版本号
	 */
	public void setCurrentNavigationIconVersion(int currentNavigationIconVersion) {
		this.currentNavigationIconVersion = currentNavigationIconVersion;
		sp.edit().putInt(CURRENT_NAVIGATION_ICON_VERSION, currentNavigationIconVersion).commit();
	}

	/**
	 * 获取当前导航顶部推荐网址版本号
	 * 
	 * @return 版本号
	 */
	public int getCurrentRecommendVersion() {
		return currentRecommendVersion;
	}

	/**
	 * 设置当前导航栏顶部推荐网址版本号
	 * 
	 * @param currentRecommendVersion
	 *            版本号
	 */
	public void setCurrentRecommendVersion(int currentRecommendVersion) {
		this.currentRecommendVersion = currentRecommendVersion;
		sp.edit().putInt(CURRENT_RECOMMEND_VERSION, currentRecommendVersion).commit();
	}

	/**
	 * 根据语言环境，获取存在xml文件中的“导航屏数据版本”的字符串
	 * 
	 * @return
	 */
	public String getNavigationVersionCPName(Context ctx) {
		if (Global.isZh(ctx))
			return CURRENT_NAVIGATION_VERSION;
		return CURRENT_NAVIGATION_VERSION_EN;
	}
	
	//用户第一次安装的桌面版本
	private static final String KEY_VERSION_FROM = "is_resident";
	/**
	 * 获取新增用户时记录的桌面版本号
	 * @Title: getVersionCodeForResident
	 * @author lytjackson@gmail.com
	 * @date 2014-3-31
	 * @return
	 */
	public int getVersionCodeFrom() {
		return sp.getInt(KEY_VERSION_FROM, -1);
	}


	public int getLastVersionCode(Context context) {
		int lastVersionCode = getLastVersionCode();
		if (lastVersionCode < 0) {
			ArrayList<Integer> list = getAllInstalledVersionCode();
			lastVersionCode = (list.size() < 2) ? getVersionCodeFrom() : list.get(list.size() - 2);
		}

		return lastVersionCode;
	}

	public int getLastVersionCode() {
		return sp.getInt("last_version_code", -1);
	}

	/**
	 * 获取所有已安装的91桌面版本Code 并从小到大排序
	 * @return 包含所有已安装的版本的版本号的列表
	 */
	public ArrayList<Integer> getAllInstalledVersionCode(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		Map<String, ?> configAll = sp.getAll();
		for (String key : configAll.keySet()) {
			if (key.startsWith("#")) {
				String version = key.substring(key.indexOf("#")+1);
				try{
					list.add(new Integer(version));
				}catch(NumberFormatException ex){
					ex.printStackTrace();
				}

			}
		}
		Collections.sort(list);
		return list;
	}

	public static final int SOHU_PAGE_ENABLE = 1;
	public static final int SOHU_PAGE_DISABLE = 0;

	public void setSohuPageEnable(int enable){
		sp.edit().putInt(KEY_SOHU_WHOLE_PAGE_NEWS, enable).commit();
	}

	public int getSohuPageEnable(){
		return  sp.getInt(KEY_SOHU_WHOLE_PAGE_NEWS, SOHU_PAGE_ENABLE);
	}

	/**
	 * 是否通过配置文件强制关闭搜狐新闻
	 */
	public void setHasForceDisableSohu(){
		sp.edit().putBoolean(KEY_SOHU_FORCE_DISABLE_PAGE,true).commit();
	}

	public void clearHasForceDisableSohuFalse(){
		sp.edit().putBoolean(KEY_SOHU_FORCE_DISABLE_PAGE,false).commit();
	}

	public boolean hasForcedDisableSohuBefore(){
		return sp.getBoolean(KEY_SOHU_FORCE_DISABLE_PAGE,false);
	}

	/**
	 * 是否出现搜狐新闻产生的崩溃
	 */
	public void setSohuPageCrashed(){
		sp.edit().putBoolean(KEY_SOHU_PAGE_CRASHED, true).commit();
	}

	public void clearSohuPageCrashed(){
		sp.edit().putBoolean(KEY_SOHU_PAGE_CRASHED, false).commit();
	}

	public boolean getSohuPageCrashed(){
		return sp.getBoolean(KEY_SOHU_PAGE_CRASHED, false);
	}


	/**
	 * 是否凤凰新闻产生的崩溃
	 */
	public void setFenghuangPageCrashed(){
		sp.edit().putBoolean(KEY_FENGHUANG_PAGE_CRASHED, true).commit();
	}

	public boolean getFenghuangPageCrashed(){
		return sp.getBoolean(KEY_FENGHUANG_PAGE_CRASHED, false);
	}

	/**
	 * 定制版零屏组合类型
	 */

	//网易资讯屏
	public static final int NAVIGATION_NEWS_PAGE_MODE_NETEASE = 1 ;
	//搜狐新闻屏
	public static final int NAVIGATION_NEWS_PAGE_MODE_SOHU = 2;
	//快速搜索屏
	public static final int NAVIGATION_NEWS_PAGE_MODE_SEARCH_PAGE = 3;
	//不显示任何屏
	public static final int NAVIGATION_PAGE_MODE_EMPTY = 4;
	//快速搜索屏》搜狐新闻屏
	public static final int NAVIGATION_PAGE_MODE_SOHU_AND_SEARCH_PAGE = 5;
	//快速搜索屏》网易资讯屏
	public static final int NAVIGATION_PAGE_MODE_NETEASE_AND_SEARCH_PAGE = 6;
	//凤凰新闻屏
	public static final int NAVIGATION_PAGE_MODE_FENGHUANG = 7;
	//快速搜索屏》凤凰新闻屏
	public static final int NAVIGATION_PAGE_MODE_FENGHUANG_AND_SEARCH_PAGE = 8;
	//新淘宝购物屏
	public static final int NAVIGATION_PAGE_MODE_TAOBAOEX = 9;
	//快速搜索屏》新淘宝购物屏》搜狐新闻屏
	public static final int NAVIGATION_PAGE_MODE_SEARCH_TAOBAOEX_SOHU = 10;
	//定制版快速搜索屏
	public static final int NAVIGATION_PAGE_MODE_DZSEARCH_PAGE = 11;
	//定制版快速搜索屏》搜狐新闻屏
	public static final int NAVIGATION_PAGE_MODE_SOHU_AND_DZSEARCH_PAGE = 12;
	//定制版快速搜索屏》网易资讯屏
	public static final int NAVIGATION_PAGE_MODE_NETEASE_AND_DZSEARCH_PAGE = 13;
	//定制版快速搜索屏》凤凰新闻屏
	public static final int NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE = 14;
	//web屏
	public static final int NAVIGATION_PAGE_MODE_WEB = 15;
	//快速搜索屏>>web屏>>搜狐新闻屏
	public static final int NAVIGATION_PAGE_MODE_SEARCH_WEB_SOHU = 16;




	/**
	 * 零屏展示模式
	 * 天湃定制版默认不显示零屏
	 * 其他定制版默认显示搜狐新闻
	 */
	public int getNavigationNewsPageShowMode() {
		if(LauncherBranchController.isLieYing(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE, NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isFanYue(mContext) && TelephoneUtil.isLetvMoble()){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_NETEASE_AND_DZSEARCH_PAGE);
		}
		if (LauncherBranchController.isFanYue(mContext) && TelephoneUtil.isGioneePhone()){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_FENGHUANG);
		}
		if(LauncherBranchController.isFanYue(mContext) && !TelephoneUtil.isLetvMoble() && !TelephoneUtil.isGioneePhone()){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isTianpan(mContext)){
			if(TelephoneUtil.isOppoPhone() || TelephoneUtil.isVivoPhone()){
				return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_EMPTY);
			}else{
				return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE);
			}
		}
		if(LauncherBranchController.isZhangShuo(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isXinShiKong(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isMinglai(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_NEWS_PAGE_MODE_SOHU);
		}
		if(LauncherBranchController.isXiangshu(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isShenlong(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.is2345(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isHot(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_DZSEARCH_PAGE);
		}
		if(LauncherBranchController.isMohuShidai(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE,NAVIGATION_PAGE_MODE_NETEASE_AND_DZSEARCH_PAGE);
		}

		if(!LauncherBranchController.isChaoqian(mContext)){
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE, NAVIGATION_PAGE_MODE_EMPTY);
		}else{
			String vn = TelephoneUtil.getVersionName(mContext);
			if (vn.equalsIgnoreCase("7.6.5")
					|| vn.equalsIgnoreCase("7.6.6")) {
				return NAVIGATION_PAGE_MODE_SEARCH_WEB_SOHU;
//				return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE, NAVIGATION_PAGE_MODE_SEARCH_WEB_SOHU);
			}
			return sp.getInt(KEY_NAVIGATION_NEWS_SHOW_MODE, NAVIGATION_PAGE_MODE_SEARCH_TAOBAOEX_SOHU);
		}
	}

	/**
	 * 零屏展示模式
	 */
	public void setNavigationNewsPageShowMode(int mode) {
		sp.edit().putInt(KEY_NAVIGATION_NEWS_SHOW_MODE, mode).commit();
	}

	/**
	 * @desc 获取帆悦定制版推荐图标
	 * @author linliangbin
	 * @time 2017/2/27 10:08
	 */
	public String getRecommendIconForFanyue(){
		return sp.getString("fanyue_navigation_icons_info","");
    }


	/**
	 * @desc 零屏是否显示手电筒，默认显示
	 * @author linliangbin
	 * @time 2017/3/16 15:32
	 */
	public boolean isTorchEnable(){
		return sp.getBoolean(KEY_NAVIGATION_SEARCH_TOUCH_ENABLE,true);
	}




	/**
	 * 零屏是否出现webview 相关的崩溃
	 * @return
	 */
	public boolean getNavigationWebviewCrash() {
		return sp.getBoolean(KEY_NAVIGATION_WEBVIEW_CRASH, false);
	}

	public void setNavigationWebviewCrash(boolean enable) {
		sp.edit().putBoolean(KEY_NAVIGATION_WEBVIEW_CRASH, enable).commit();
	}

	/**
	 * 零屏是否强制关闭webview 展示
	 * @return
	 */
	public boolean getNavigationWebviewForceClose() {
		return sp.getBoolean(KEY_NAVIGATION_WEBVIEW_FORCE_DISABLE, false);
	}

	public void setNavigationWebviewForceClose(boolean enable) {
		sp.edit().putBoolean(KEY_NAVIGATION_WEBVIEW_FORCE_DISABLE, enable).commit();
	}


	public int getSchemeOpt(){
		return sp.getInt(KEY_WEB_BASED_INTENT_SCHEME_OPT,0);
	}

	public void setSchemeOpt(int i){
		sp.edit().putInt(KEY_WEB_BASED_INTENT_SCHEME_OPT, i).commit();
	}

	public boolean getPrompt(){
		return sp.getInt(KEY_WEB_BASED_INTENT_SCHEME_PROMPT, 0) == 1;
	}


	/**
	 * @desc 设置和获取零屏返回键进入的屏
	 * @author linliangbin
	 * @time 2017/11/3 16:24
	 */
	public static final int NAVIGATION_PAGE_INDEX_UNKNOWN = -1;
	public void setNavigationModeBackKeyToPageIndex(int pageIndex) {
		sp.edit().putInt(KEY_NAVIGATION_MODE_BACK_KEY_PAGE_INDEX, pageIndex).commit();
	}
	public int getNavigationModeBackKeyToPageIndex() {
		return sp.getInt(KEY_NAVIGATION_MODE_BACK_KEY_PAGE_INDEX,NAVIGATION_PAGE_INDEX_UNKNOWN);
	}




}
