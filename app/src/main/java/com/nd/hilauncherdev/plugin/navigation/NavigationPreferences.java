package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.SharedPreferences;

import com.nd.hilauncherdev.plugin.navigation.pluginAD.ChannelUtil;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.SettingsConstants;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;

/**
 * 0屏配置
 *
 * @author chenzhihong_9101910
 *
 */
public class NavigationPreferences {



	private static final String NAME = "configsp";

	private static NavigationPreferences ap;
	private static SharedPreferences sp;

	private boolean mSearchPageIsOpen=true;
	private boolean mNewsPageIsOpen=false;
	private boolean mTaobaoPageIsOpen=true;
	private boolean mOpenPageChange=false;
	private boolean mSohuPageIsOpen = true;
	private boolean mFenghuangPageIsOpen = false;
	private boolean mIReaderPageIsOpen = false;
	private boolean mTaobaoExPageIsOpen = false;
	private boolean mIReaderAndTaobaoPageIsOpen = false;
	private boolean mWebIsOpen = true;
	private int mOpenPageMode = 0;

	/**
	 * 各个屏对应的PageArray的位置
	 */
	//不展示
	public static final int INDEX_PAGE_NONE = -1;
	//零屏
	public static final int INDEX_PAGE_NAVIGATION = 0;
	//淘宝购物屏
	public static final int INDEX_PAGE_TAOBAO = 1;
	//网易新闻屏
	public static final int INDEX_PAGE_NEWS = 2;
	//点心购物屏
	public static final int INDEX_PAGE_VIP = 3;
	//搜狐新闻屏
	public static final int INDEX_PAGE_SOHU = 4;
	//凤凰新闻屏
	public static final int INDEX_PAGE_FENGHUANG = 5;
	//掌阅阅读屏
	public static final int INDEX_PAGE_IREADER = 6;
	//新淘宝屏
	public static final int INDEX_PAGE_TAOBAO_EXT = 7;
	//定制版快速搜索屏-阅读&购物屏
	public static final int INDEX_PAGE_DZSEARCH = 8;
	//web屏
	public static final int INDEX_PAGE_WEB = 9;


	private Context context;

	private NavigationPreferences(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		mSearchPageIsOpen=sp.getBoolean(SettingsConstants.SETTINGS_SEARCH_PAGE_SHOW, true);
		mNewsPageIsOpen=sp.getBoolean(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW, true);
		mTaobaoPageIsOpen=sp.getBoolean(SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW, true);
		mSohuPageIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW,false);
		mFenghuangPageIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_FENGHUANG_PAGE_SHOW,false);
		mIReaderPageIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW,true);
		mTaobaoExPageIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_TAOBAOEX_PAGE_SHOW,false);
		mIReaderAndTaobaoPageIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_IREADER_AND_TAOBAO_PAGE_SHOW,false);
		mWebIsOpen = sp.getBoolean(SettingsConstants.SETTINGS_DZ_WEB_SHOW, true);
		mOpenPageMode  =sp.getInt(SettingsConstants.SETTINGS_OPEN_PAGE_MODE, 0);
	}

	public synchronized static NavigationPreferences getInstance(Context context) {
		if (ap != null)
			return ap;

		ap = new NavigationPreferences(context);
		return ap;
	}





	public boolean isShowWebSites() {
		return true;
		// return sp.getBoolean(SettingsConstants.NAVIGATION_LAYOUT_SITES_SHOW, true);
	}



	/**
	 * 升级到V761以及以后版本时搜狐新闻开关状态
	 * 不受用户的手动开关所影响
	 * 默认是关的
	 * @param isShow
	 */
	public void setSohuPageOnWhenUpgradTo761(boolean isShow) {
		sp.edit().putBoolean(SettingsConstants.SETTINGS_SOHU_PAGE_SHOU_WHEN_UPGRAD_TO_761, isShow).commit();
	}



	public boolean getSohuPageOnWhenUpgradTo761() {
		return sp.getBoolean(SettingsConstants.SETTINGS_SOHU_PAGE_SHOU_WHEN_UPGRAD_TO_761, false);
	}



	public void setShowSearchPage(boolean isShow) {
		mSearchPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_SEARCH_PAGE_SHOW, isShow).commit();
		setNavigation();
	}


	public void setShowNewsPage(boolean isShow) {
		mNewsPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_NEWS_PAGE_SHOW, isShow).commit();
		setNavigation();
	}


	public void setShowTaobaoPage(boolean isShow) {
		mTaobaoPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_TAOBAO_PAGE_SHOW, isShow).commit();
		setNavigation();
	}


	public void setShowSohuPage(boolean isShow){
		mSohuPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_SOHU_PAGE_SHOW, isShow).commit();
		setNavigation();
	}

	public void setShowFenghuangPage(boolean isShow){
		mFenghuangPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_FENGHUANG_PAGE_SHOW, isShow).commit();
		setNavigation();
	}

	public void setShowIreaderPage(boolean isShow){
		mIReaderPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_IREADER_PAGE_SHOW, isShow).commit();
		setNavigation();
	}

	public void setShowTaobaoExPage(boolean isShow){
		mTaobaoExPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_TAOBAOEX_PAGE_SHOW, isShow).commit();
		setNavigation();
	}

	public void setShowIreaderAndTaobaoPage(boolean isShow){
		mIReaderAndTaobaoPageIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_IREADER_AND_TAOBAO_PAGE_SHOW, isShow).commit();
		setNavigation();
	}

	public void setShowWebPage(boolean isShow) {
		mWebIsOpen = isShow;
		sp.edit().putBoolean(SettingsConstants.SETTINGS_DZ_WEB_SHOW, isShow).commit();
		setNavigation();
	}

	public void setOpenPageChange(boolean change) {
		mOpenPageChange = change;
		//sp.edit().putBoolean(SettingsConstants.SETTINGS_NAVIGATION_CHANGE, change).commit();
	}


	public void setNavigation()
	{
		int count=getOpenPageCount();
		boolean isShow=count>0?true:false;

		if(isShow == ReflectInvoke.isShowNavigationView(context))
			return;
		if(isShow) {
			ReflectInvoke.setShowNavigationView(context,true);
		}else {
			//关闭
			ReflectInvoke.setShowNavigationView(context,false);
		}

	}

	public boolean isShowSearchPage() {
		return mSearchPageIsOpen;
	}
	public boolean isShowNewsPage() {
		return mNewsPageIsOpen;
	}


	public boolean isShowTaobaoPage() {
		return mTaobaoPageIsOpen;
	}


	public boolean isShowSohuPage(){
		return mSohuPageIsOpen;
	}

	public boolean isShowFenghuangPage(){ return mFenghuangPageIsOpen; }

	public boolean isOpenPageChange() {
		return mOpenPageChange;
	}

	public boolean isShowIreaderPage(){
		return mIReaderPageIsOpen;
	}

	public boolean isShowIreaderAndTaobaoPage(){
		return mIReaderAndTaobaoPageIsOpen;
	}

	public boolean isShowWebPage() {
		return mWebIsOpen;
	}



	public boolean isShowTaobaoExPage(){ return mTaobaoExPageIsOpen; }

	public int getOpenPageCount() {
		int count = 0;
		int[] pageShowSequence = getShowPageSequence();
		boolean[] pageArray = getOpenPageDisplayArray();
		for(int i =0;i<pageShowSequence.length;i++){
			if(pageShowSequence[i] != INDEX_PAGE_NONE && pageArray[pageShowSequence[i]]){
				count++;
			}
		}
		return count;
	}

	/**
	 * 0是搜索屏，1是淘宝页，2是资讯页，3.唯品会 4.搜狐 5.凤凰新闻 6.掌阅阅读屏 7.新淘宝购物屏 8.阅读淘宝屏 9.定制web屏
	 * */
	public boolean[] getOpenPageDisplayArray() {
		boolean r[] = new boolean[]{false, false, false,false,false,false,false,false,false,false};
		if (mSearchPageIsOpen) r[0] = true;
		if (mTaobaoPageIsOpen) r[1] = true;
		if (mNewsPageIsOpen) r[2] = true;
		if (mSohuPageIsOpen) r[4] = true;
		if (mFenghuangPageIsOpen) r[5] = true;
		if (mIReaderPageIsOpen) r[6] = true;
		if (mTaobaoExPageIsOpen) r[7] = true;
		if (mIReaderAndTaobaoPageIsOpen) r[8] = true;
		if (mWebIsOpen) r[9] = true;
		return r;
	}

	/**
	 * @param mode 0表示3屏模式，1表示新模式，即资讯屏和0屏合并
	 *             2.X 版本不适用新模式
	 * **/
	public void setOpenPageMode(int mode) {
		mOpenPageMode = mode;
		sp.edit().putInt(SettingsConstants.SETTINGS_OPEN_PAGE_MODE, mode).commit();
		setNavigation();
	}
	/**
	 *   0表示3屏模式，1表示新模式，即资讯屏和0屏合并
	 * **/
	public int  getOpenPageMode() {
		return  mOpenPageMode;
	}
	//零屏是否在最左标记
	private boolean isNavigationAtLeft = false;
	//零屏是否在最左标记是否已经设置
	private boolean hasInitNavigationLeft = false;
	/**
	 * 零屏是否在最左边显示
	 * @return
	 */
	public boolean isNavigationAtLeft(Context context){
		//当前是否已经初始化完零屏是否在最左边的值
		if(hasInitNavigationLeft){
			return isNavigationAtLeft;
		}

		int pageShow[] = getShowPageSequence();
		if (pageShow != null) {
			if (pageShow[0] == INDEX_PAGE_NAVIGATION) {
				isNavigationAtLeft = true;
			} else {
				isNavigationAtLeft = false;
			}
			hasInitNavigationLeft = true;
		}

		return  isNavigationAtLeft;
	}



	/** 91桌面线上版本的展示顺序逻辑（非定制版）
	 * @desc
	 * @author linliangbin
	 * @time 2017/2/8 15:01
	 */
	private int[] getShowPageSequenceForNormal91Launcher(){

		int versionCodeFrom = ConfigPreferences.getInstance(context).getVersionCodeFrom();

		/**
		 * V7.6 以前的升级用户
		 */
		if(versionCodeFrom < 7598){
			// 如果后台强制关闭搜狐新闻，则以网易新闻顺序显示
			if(ConfigPreferences.getInstance(context).hasForcedDisableSohuBefore()){
				return new int[]{
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}
			if (TelephoneUtil.getApiLevel() > 14) {
				return new int[]{
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_SOHU,
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}else{
				return new int[]{
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}
		}else if(versionCodeFrom == 7598){
			/**
			 * V7.6 的升级用户
			 */
			if(ConfigPreferences.getInstance(context).hasForcedDisableSohuBefore()){
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}
			if (TelephoneUtil.getApiLevel() > 14) {
				if (getSohuPageOnWhenUpgradTo761()) {
					//升级上来时，展示搜狐时，搜狐在右边
					return new int[]{
							INDEX_PAGE_NAVIGATION,
							INDEX_PAGE_TAOBAO,
							INDEX_PAGE_SOHU,
							INDEX_PAGE_NONE,
							INDEX_PAGE_NONE
					};
				}else{
					//否则零屏在右边
					return new int[]{
							INDEX_PAGE_TAOBAO,
							INDEX_PAGE_NEWS,
							INDEX_PAGE_NAVIGATION,
							INDEX_PAGE_NONE,
							INDEX_PAGE_NONE
					};
				}
			}else{
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}

		} else {
			//V7.6.1 以后的所有用户

			//V7608 部分用户不显示搜狐新闻
			if(versionCodeFrom == 7608 && getOpenPageMode() == 1){
				return new int[]{
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}

			if(ConfigPreferences.getInstance(context).hasForcedDisableSohuBefore()){
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}

			if (ChannelUtil.isSEMChannel(context)) {
				boolean isNewUser = (versionCodeFrom == -1) || (versionCodeFrom >= 8618);
				if (isNewUser) {
					if (TelephoneUtil.getApiLevel() > 14) {
						return new int[]{
								INDEX_PAGE_NAVIGATION,
								INDEX_PAGE_TAOBAO,
								INDEX_PAGE_WEB,
								INDEX_PAGE_SOHU,
								INDEX_PAGE_NONE
						};
					} else {
						return new int[]{
								INDEX_PAGE_NAVIGATION,
								INDEX_PAGE_TAOBAO,
								INDEX_PAGE_WEB,
								INDEX_PAGE_NEWS,
								INDEX_PAGE_NONE
						};
					}
				}
			}

			if(TelephoneUtil.getApiLevel() > 14){
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_SOHU,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}else{
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO,
						INDEX_PAGE_NEWS,
						INDEX_PAGE_NONE,
						INDEX_PAGE_NONE
				};
			}

		}
	}


	/**
	 * 百度桌面零屏展示逻辑（非定制版）
	 *
	 * @desc
	 * @author linliangbin
	 * @time 2017/2/8 15:01
	 */
	private int[] getShowPageSequenceForBaiduLauncher() {

		return new int[]{
				INDEX_PAGE_NAVIGATION,
				INDEX_PAGE_NEWS,
				INDEX_PAGE_NONE,
				INDEX_PAGE_NONE,
				INDEX_PAGE_NONE
		};

	}



	/**
	 * 点心桌面/安卓桌面零屏展示逻辑（非定制版）
	 *
	 * @desc
	 * @author linliangbin
	 * @time 2017/2/8 15:01
	 */
	private int[] getShowPageSequenceForDxAzLauncher() {


		if (ConfigPreferences.getInstance(context).hasForcedDisableSohuBefore()) {
			return new int[]{
					INDEX_PAGE_NAVIGATION,
					INDEX_PAGE_TAOBAO,
					INDEX_PAGE_NEWS,
					INDEX_PAGE_IREADER,
			};
		}

		if (TelephoneUtil.getApiLevel() > 14) {
			return new int[]{
					INDEX_PAGE_NAVIGATION,
					INDEX_PAGE_TAOBAO,
					INDEX_PAGE_SOHU,
					INDEX_PAGE_IREADER,
			};
		} else {
			return new int[]{
					INDEX_PAGE_NAVIGATION,
					INDEX_PAGE_TAOBAO,
					INDEX_PAGE_NEWS,
					INDEX_PAGE_IREADER,
			};
		}
	}



	/**
	 * @desc
	 * 线下定制版本的展示顺序逻辑（包括力天帆悦椒盐猎鹰等）
	 * 目前有五种模式：
	 * a.显示网易新闻  b.显示搜狐新闻  c.显示零屏 d.所有屏都不显示 e.显示搜狐新闻&零屏
	 * @author linliangbin
	 * @time 2017/2/8 15:01
	 */
	private int[] getShowPageSequenceForCustomLauncher(){
		int mode = ConfigPreferences.getInstance(context).getNavigationNewsPageShowMode();
		switch (mode){
			case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_NETEASE:
				return new int[]{
						INDEX_PAGE_NEWS
				};
			case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_SOHU:
				return new int[]{
						INDEX_PAGE_SOHU
				};
			case ConfigPreferences.NAVIGATION_NEWS_PAGE_MODE_SEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_NAVIGATION
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_EMPTY:
				return new int[]{
						INDEX_PAGE_NEWS
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_SOHU_AND_SEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_SOHU
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_SEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_NEWS
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG:
				return new int[]{
						INDEX_PAGE_FENGHUANG,
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG_AND_SEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_FENGHUANG,
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_TAOBAOEX:
				return new int[]{
						INDEX_PAGE_TAOBAO_EXT,
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_SEARCH_TAOBAOEX_SOHU:
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_TAOBAO_EXT,
						INDEX_PAGE_SOHU
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_DZSEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_DZSEARCH
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_SOHU_AND_DZSEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_DZSEARCH,
						INDEX_PAGE_SOHU
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_NETEASE_AND_DZSEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_DZSEARCH,
						INDEX_PAGE_NEWS
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_FENGHUANG_AND_DZSEARCH_PAGE:
				return new int[]{
						INDEX_PAGE_DZSEARCH,
						INDEX_PAGE_FENGHUANG
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_WEB:
				return new int[]{
						INDEX_PAGE_WEB
				};
			case ConfigPreferences.NAVIGATION_PAGE_MODE_SEARCH_WEB_SOHU:
				return new int[]{
						INDEX_PAGE_NAVIGATION,
						INDEX_PAGE_WEB,
						INDEX_PAGE_SOHU
				};
		}
		return new int[]{
				INDEX_PAGE_NEWS
		};
	}

	/**
	 * 获取各个屏幕的展示顺序
	 * 点心桌面目前展示的顺序（左到右）：
	 * 零屏=》唯品会=》搜狐新闻
	 * @return
	 */
	public int[] getShowPageSequence() {
		if(LauncherBranchController.isNavigationForCustomLauncher()){
			return getShowPageSequenceForCustomLauncher();
		}else if(CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)){
			return getShowPageSequenceForDxAzLauncher();
		}else if(CommonGlobal.isBaiduLauncher(context)){
			return getShowPageSequenceForBaiduLauncher();
		}else {
			return getShowPageSequenceForNormal91Launcher();
		}
	}


	/**
	 * 当前是否为新增用户第一次启动零屏
	 * 第一次启动零屏时需要相应操作
	 * @param isNewUser
	 */
	public void setNavigationFlagForNewInstall(boolean isNewUser){
		sp.edit().putBoolean(SettingsConstants.SETTINGS_NAVIGATION_NEED_HANDLE_NEW_INSTALL, isNewUser).commit();
	}
	public boolean isNavigationFlagForNewInstall(){
		return sp.getBoolean(SettingsConstants.SETTINGS_NAVIGATION_NEED_HANDLE_NEW_INSTALL, false);
	}

}
