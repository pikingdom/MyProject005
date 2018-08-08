package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.kitset.util.reflect.CommonKeepForReflect;
import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.NavigationData;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.WebSiteItem;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviSiteLoader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.GZipHttpUtil;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationSiteView.NavigationCategory;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationSiteView.NavigationCategoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NavigationLoader {

	private final static String TAG = "NavigationLoader";

	/**
	 * 网站Icon本地缓存目录
	 */
	private static final String NAV_ICON_DIR = CommonGlobal.getCachesHome() + "navigation/";
	private static final String NAV_ICON_FAV_DIR = NAV_ICON_DIR + "favorites/";
	private static final String NAV_ICON_SITE_DIR = NAV_ICON_DIR + "sites/";

	private final static String RECOMMEND_PATH = Environment.getDataDirectory() + "/data/" + Global.getPackageName() + "/files/recommend.txt";

	/**
	 * 分类内容的颜色
	 */
	public final static String COLOR_ITEM_TITLE = "#33b5e5";

	// ------------导航数据JSON的TAG------------//
	private final static String TAG_VERSION = "version";
	private final static String TAG_REC_VERSION = "recommendVesion";
	private final static String TAG_ICON_VERSION = "iconversion";
	private final static String TAG_CODE = "Code";
	private final static String TAG_NAVIGATION = "navigation";
	private final static String TAG_NAVIGATION_CATEGORY_TITLE = "title";
	private final static String TAG_NAVIGATION_CATEGORY_ITEMS = "items";
	private final static String TAG_NAVIGATION_CATEGORY_ICON_URL = "icon";
	private final static String TAG_NAVIGATION_ITEM_NAME = "name";
	private final static String TAG_NAVIGATION_ITEM_URL = "url";
	private final static String TAG_NAVIGATION_ITEM_RED = "red";
	private final static String TAG_NAVIGATION_ITEM_COLOR = "color";
	private final static String TAG_NAVIGATION_ITEM_BOLD = "bold";
	private final static String TAG_NAVIGATION_ITEM_ICON_V6 = "iconFortV6";
	private final static String TAG_NAVIGATION_ITEM_ID = "id";
	private final static String TAG_NAVIGATIN_ITEM_SOURCE_ID = "AdSource";
	private final static String TAG_NAVIGATIN_ITEM_CALLBACK = "CallBack";
	private final static String TAG_NAVIGATIN_ITEM_OPEN_TYPE = "OpenType";
	private final static String TAG_NAVIGATIN_ITEM_NEED_SESSION = "NeedSession";
	private final static String TAG_NAVIGATION_RECOMMAND = "icons";

	/**
	 * 导航推荐图标
	 */
	private static final int[] favoriteUrlIcons = { R.drawable.launcher_navigation_ad_taobao, R.drawable.launcher_navigation_ad_meituan, R.drawable.launcher_navigation_ad_jingdong,
			R.drawable.launcher_navigation_ad_qunar,R.drawable.launcher_navigation_ad_vip };

	/**
	 * 导航推荐名称
	 */
	private static final int[] favoriteUrlTexts = { R.string.navigation_i_taobao, R.string.navigation_meituan, R.string.navigation_jdjr, R.string.navigation_qunar , R.string.navigation_vip};

	/**
	 * 导航推荐链接
	 */
	private static final String[] favoriteUrls = { UrlConstant.HTTP_URL_FELINK_COM+"2muE3a",
			UrlConstant.HTTP_URL_FELINK_COM+"rQZNZ3", UrlConstant.HTTP_URL_FELINK_COM+"Anu2qa",
			UrlConstant.HTTP_URL_FELINK_COM+"3iqQja", UrlConstant.HTTP_URL_IFJING_COM+"aqqiUn"};

	private static final String[] favoriteSiteId = { "100000039", "100000080", "100000074", "100000045","100000081"};

	/**
	 * 随机推荐的位置
	 */
	public static int favoriteRandomIndex = -1;

	private static NavigationLoader instance = new NavigationLoader();

	private int loadDataCount = 0;// 连接次数
	private long loadDataDayTime = 0;// 连接时间

	/** 推荐ICON个数 */
	public static int FAVORITE_ICON_COUNT = 8;


	private NavigationLoader() {
	};

	public static NavigationLoader getInstance() {
		return instance;
	}

	/**
	 * 加载推荐网址与网址大全数据
	 *
	 * @param mContext
	 * @param mFavoriteSiteView
	 * @param mSiteView
	 */
	public static void loadRecommendedAndAllSites(final Context mContext, final NavigationFavoriteSiteView mFavoriteSiteView, final NavigationSiteView mSiteView) {
		createBaseDir();
		mFavoriteSiteView.loadSites();
		if (mSiteView != null) {
			mSiteView.loadSites();
		}
	}

	/**
	 * 加载网址大全数据
	 *
	 * @param mContext
	 * @param mSiteView
	 */
	public static void loadAllSites(final Context mContext, final NavigationSiteView mSiteView) {
		createBaseDir();
		mSiteView.loadSites();
	}

	/**
	 * 更新第0屏导航数据,在发布新版本的地方使用
	 *
	 * @param navigationVersion
	 *            发布桌面新版本时设置的本地导航版本号
	 * @param navigationIconVersion
	 *            发布桌面新版本时设置的本地导航网站Icon版本号
	 * @param recommendVersion
	 *            发布桌面新版本时设置的顶部推荐网址版本号
	 */
	public static void initNavigationDataVersion(Context ctx, int navigationVersion, int navigationIconVersion, int recommendVersion) {
		// 更新版本号
		ConfigPreferences cp = ConfigPreferences.getInstance(ctx);
		cp.setCurrentNavigationVersion(ctx, navigationVersion);
		cp.setCurrentNavigationIconVersion(navigationIconVersion);
		cp.setCurrentRecommendVersion(recommendVersion);
		// 删除旧文件
		FileUtil.delFile(NavigationData.getNavigationPath(ctx));
	}


	/**
	 * @desc 增加本地动作ICON
	 * 比如打开网址大全页面
	 * @author linliangbin
	 * @time 2017/5/15 10:51
	 */
	public static List<WebSiteItem> addLocalIcon(List<WebSiteItem> srcList){
		if(srcList == null || srcList.size() <=0){
			return srcList;
		}
		WebSiteItem webitem = new WebSiteItem();

		webitem.iconId = R.drawable.ic_favorite_detail_site;
		webitem.iconType = WebSiteItem.TYPE_LOCAL_RES_ICON;
		webitem.url = favoriteUrls[0];
		webitem.name = "网址导航";
		webitem.siteId = favoriteSiteId[0];
		webitem.actionType = WebSiteItem.ACTION_TYPE_LOCAL_ACTION_SITE_DETAIL;
		srcList.add(0,webitem);

		return srcList;
	}

	/**
	 * 获取推荐网址数据
	 *
	 * @param mainObject
	 *            从服务器上返回的json数据(暂时只支持从本地获取推荐网址)
	 * @param ctx
	 * @return
	 */

	public static List<WebSiteItem> getRecommendedSites(Context ctx,int totalCount,boolean needLocal) {
		JSONArray favoriteArray = getAllDataFromLocalFile(RECOMMEND_PATH);// 从本地读取已经从服务器上获取到的数据
		List<WebSiteItem> list = null;
		if (favoriteArray != null && favoriteArray.length() > 0) {
			list = getRecommendedSitesFromJson(favoriteArray);
		}
		if (list == null || list.size() == 0) {
			list = getRecommendedSitesFromCode(ctx,totalCount/2,needLocal);
			/** 已经添加本地图标，避免重复添加 */
			needLocal =false;
		}

		/** 定制版需要定制ICON */
		if(LauncherBranchController.isNavigationForCustomLauncher()){
			//获取定制版本数据
			List<WebSiteItem> customList = getRecommendSitesFroCustomLauncher(ctx);
			if(customList == null){
				customList = new ArrayList<WebSiteItem>();
			}

			if(customList.size() > totalCount){
				return customList.subList(0,totalCount);
			}

			int CUSTOME_SIZE = totalCount / 2;
			if(customList.size() + list.size() >= totalCount){
				CUSTOME_SIZE = totalCount;
			}
			for(int i =0; i<list.size() && customList.size() < CUSTOME_SIZE; i++){
				customList.add(list.get(i));
			}
			return customList;

		}else{
			if(needLocal){
			    list = addLocalIcon(list);
			}
			if(list != null && list.size() > totalCount){
				return list.subList(0,totalCount);
			}
		}
		return list;
	}

	/**
	 * @desc 获取定制版的推荐图标
	 * @author linliangbin
	 * @time 2017/2/27 10:06
	 */
	public static final int INDEX_ITEM_ACTION_TYPE = 0;
	public static final int INDEX_ITEM_URL_OR_PKG = 1;
	public static final int INDEX_ITEM_NAME = 2;
	public static final int INDEX_ITEM_ICON = 3;

	/** 系统目录下的推荐图标缓存目录 */
	public static final String SYSTEM_APP_ICON_PATH = "/system/etc/custominfo/";

	public static List<WebSiteItem> getRecommendSitesFroCustomLauncher(Context context){
		List<WebSiteItem> list = new ArrayList<WebSiteItem>();
		String iconStrings = ConfigPreferences.getInstance(context).getRecommendIconForFanyue();
		Log.i("llbeing","iconStrings:"+iconStrings);
		if(TextUtils.isEmpty(iconStrings)){
			return list;
		}
		String itemStrings[] = iconStrings.split("##");
		if(itemStrings.length <= 0){
			return list;
		}
		for(String itemString : itemStrings){
			try {
				String itemParams[] = itemString.split("\\|");
				/**每个ITEM  四个属性 */
				if(itemParams.length != 4){
					continue;
				}
				WebSiteItem webItem = new WebSiteItem();
				if("1".equals(itemParams[INDEX_ITEM_ACTION_TYPE])){
					webItem.actionType = WebSiteItem.ACTION_TYPE_OPEN_APP;
				}
				webItem.iconType = WebSiteItem.TYPE_LOCAL_FILE_ICON;
				try {
					if(LauncherBranchController.isShenlong(context)){
						webItem.iconPath = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/files/custominfo/" + itemParams[INDEX_ITEM_ICON];
					}else{
						webItem.iconPath = Environment.getExternalStorageDirectory() + "/" + ReflectInvoke.getBaseDirName(NavigationView2.activity) + "/custominfo/" + itemParams[INDEX_ITEM_ICON];
					}
					if(!FileUtil.isFileExits(webItem.iconPath)){
						webItem.iconPath = SYSTEM_APP_ICON_PATH + itemParams[INDEX_ITEM_ICON];
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}

				/** 打开动作 */
				if(webItem.actionType == WebSiteItem.ACTION_TYPE_OPEN_APP){

					String intentString = "";
					if(!TextUtils.isEmpty(itemParams[INDEX_ITEM_URL_OR_PKG])){
						String pkgClass[] = itemParams[INDEX_ITEM_URL_OR_PKG].split("/");
						if(pkgClass.length == 2){
							webItem.appPkg = pkgClass[0];
							webItem.appClass = pkgClass[1];
							Intent intent1 = new Intent();
							intent1.setClassName(webItem.appPkg,webItem.appClass);
							intentString = intent1.toUri(0);
							if(!SystemUtil.isApkInstalled(context,webItem.appPkg)){
								continue;
							}
						}else{
							webItem.appPkg = itemParams[INDEX_ITEM_URL_OR_PKG];
							Intent intent = SystemUtil.getAppLaunchIntent(context,itemParams[INDEX_ITEM_URL_OR_PKG]);
							if(intent == null){
								continue;
							}
							intentString = intent.toUri(0);
						}
					}else{
						webItem.appPkg = itemParams[INDEX_ITEM_URL_OR_PKG];
						Intent intent = SystemUtil.getAppLaunchIntent(context,itemParams[INDEX_ITEM_URL_OR_PKG]);
						if(intent == null){
							continue;
						}
						intentString = intent.toUri(0);
					}
					webItem.url = intentString;
					if(FileUtil.isFileExits(webItem.iconPath) && !TextUtils.isEmpty(itemParams[INDEX_ITEM_ICON])){
						webItem.iconType = WebSiteItem.TYPE_LOCAL_FILE_ICON;
					}else{
						webItem.iconType = WebSiteItem.TYPE_USE_APP_ICON;
					}
				}else{
					webItem.url = itemParams[INDEX_ITEM_URL_OR_PKG];
					if(FileUtil.isFileExits(webItem.iconPath) && !TextUtils.isEmpty(itemParams[INDEX_ITEM_ICON])){
						webItem.iconType = WebSiteItem.TYPE_LOCAL_FILE_ICON;
					}else{
						continue;
					}
				}
				webItem.name = itemParams[INDEX_ITEM_NAME];

				list.add(webItem);
			}catch (Exception e){
				e.printStackTrace();
			}

		}
		return list;
	}

	/**
	 * 获取网址大全
	 *
	 * @param mainObject
	 *            从服务器上返回的json数据
	 * @return
	 */
	public static List<NavigationCategory> getAllSites(Context ctx) {
		JSONArray navigationObject = getAllDataFromLocalFile(NavigationData.getNavigationPath(ctx)); // 从本地读取已经从服务器上获取到的数据
		List<NavigationCategory> catList = null;
		if (navigationObject != null && navigationObject.length() > 0) {
			catList = getAllSitesFromJson(ctx, navigationObject);
		}
		if (catList == null) {
			catList = getAllSitesFromCode(ctx);
		}

		return catList;
	}

	public static void clearFavIcon() {
		FileUtil.delAllFile(NAV_ICON_FAV_DIR);
	}

	/**
	 * 从json中获取顶部导航数据
	 *
	 * @param object
	 * @return
	 */
	private static List<WebSiteItem> getRecommendedSitesFromJson(JSONArray array) {
		Log.v(TAG, "getFavoriteFromJson...");
		try {
			List<WebSiteItem> list = new ArrayList<WebSiteItem>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				WebSiteItem item = new WebSiteItem();
				item.url = object.getString(TAG_NAVIGATION_ITEM_URL);
				item.name = object.getString(TAG_NAVIGATION_ITEM_NAME);
				item.iconURL = object.getString(TAG_NAVIGATION_ITEM_ICON_V6);
				item.CallBack = object.optInt(TAG_NAVIGATIN_ITEM_CALLBACK);
				item.AdSourceId = object.optInt(TAG_NAVIGATIN_ITEM_SOURCE_ID);
				item.openType = object.optInt(TAG_NAVIGATIN_ITEM_OPEN_TYPE);
				item.needSession = object.optBoolean(TAG_NAVIGATIN_ITEM_NEED_SESSION);
				if (object.has(TAG_NAVIGATION_ITEM_ID)) {
					item.siteId = object.getString(TAG_NAVIGATION_ITEM_ID);
				}
				list.add(item);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从代码中获取顶部导航数据
	 *
	 * @return
	 */
	public static List<WebSiteItem> getRecommendedSitesFromCode(Context ctx,int totalCount,boolean isAddLocal) {
		Log.v(TAG, "getFavoriteFromCode...");
		List<WebSiteItem> urlItems = new ArrayList<WebSiteItem>();
		try {
			int itemCounts = totalCount;
			if(isAddLocal){
				itemCounts = totalCount - 1;
			}
			// 添加固定推荐网址
			for (int i = 0; i < favoriteUrlIcons.length && i < itemCounts ; i++) {
				WebSiteItem item = new WebSiteItem();
				item.iconId = favoriteUrlIcons[i];
				item.iconType = WebSiteItem.TYPE_LOCAL_RES_ICON;
				item.url = favoriteUrls[i];
				item.name = ctx.getString(favoriteUrlTexts[i]);
				item.siteId = favoriteSiteId[i];
				urlItems.add(item);
			}
		} catch (Exception e) { // 捕获可能的空指针异常
			urlItems = new ArrayList<WebSiteItem>();
		}

		if(isAddLocal){
			addLocalIcon(urlItems);
		}
		return urlItems;
	}

	/**
	 * 从json中获取底部导航数据
	 *
	 * @param object
	 */
	private static List<NavigationCategory> getAllSitesFromJson(Context ctx, JSONArray array) {
		Log.v(TAG, "getNavigationFromJson...");
		try {
			// -----------解析数据----------//
			List<NavigationCategory> categoryList = new ArrayList<NavigationCategory>();
			int categoryLength = array.length();
			for (int i = 0; i < categoryLength; i++) {
				JSONObject categoryJsonObject = array.getJSONObject(i);
				NavigationCategory category = new NavigationCategory();
				category.title = categoryJsonObject.getString(NavigationLoader.TAG_NAVIGATION_CATEGORY_TITLE);
				if (category.title.indexOf("网址导航卡片") >= 0) {
					continue;
				}

				category.iconUrl = categoryJsonObject.getString(NavigationLoader.TAG_NAVIGATION_CATEGORY_ICON_URL);
				int catIndex = 0;
				for (String cat : NavigationData.getDefaultNavCategory(ctx)) {// 获取分类icon
					if (category.title.contains(cat)) {
						category.categoryIconRes = NavigationData.getDefaultNavCategoryIcon(ctx)[catIndex];
						break;
					}
					catIndex++;
				}
				category.items = new ArrayList<NavigationCategoryItem>();
				JSONArray categoryItemArray = categoryJsonObject.getJSONArray(NavigationLoader.TAG_NAVIGATION_CATEGORY_ITEMS);
				int itemLength = categoryItemArray.length();
				for (int j = 0; j < itemLength; j++) {
					JSONObject itemJsonObject = categoryItemArray.getJSONObject(j);
					NavigationCategoryItem item = new NavigationCategoryItem();
					item.name = itemJsonObject.optString(NavigationLoader.TAG_NAVIGATION_ITEM_NAME);
					item.url = itemJsonObject.optString(NavigationLoader.TAG_NAVIGATION_ITEM_URL);
					item.isRed = itemJsonObject.optInt(NavigationLoader.TAG_NAVIGATION_ITEM_RED, 0) == 0 ? false : true;
					item.color = itemJsonObject.optString(NavigationLoader.TAG_NAVIGATION_ITEM_COLOR, COLOR_ITEM_TITLE);
					item.bold = itemJsonObject.optBoolean(NavigationLoader.TAG_NAVIGATION_ITEM_BOLD, false);
					item.siteId = itemJsonObject.optString(NavigationLoader.TAG_NAVIGATION_ITEM_ID, null);
					category.items.add(item);
				}
				categoryList.add(category);
			}
			return categoryList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从代码中获取底部导航信息
	 *
	 * @return
	 */
	private static List<NavigationCategory> getAllSitesFromCode(Context ctx) {
		List<NavigationCategory> categoryList = new ArrayList<NavigationCategory>();

		if (Global.isZh(ctx)) {
			JSONArray jsonArray = getAllSitesJsonFromCode(ctx);
			if (jsonArray != null) {
				categoryList = getAllSitesFromJson(ctx, jsonArray);
			}
		} else {
			for (int i = 0; i < NavigationData.getDefaultNavCategory(ctx).length; i++) {
				NavigationCategory category = new NavigationCategory();
				category.title = NavigationData.getDefaultNavCategory(ctx)[i];
				category.categoryIconRes = NavigationData.getDefaultNavCategoryIcon(ctx)[i];
				category.items = new ArrayList<NavigationCategoryItem>();
				for (int j = 0; j < NavigationData.getDefaultNavTitle(ctx)[i].length; j++) {
					NavigationCategoryItem item = new NavigationCategoryItem();
					item.name = NavigationData.getDefaultNavTitle(ctx)[i][j];
					item.url = NavigationData.getDefaultNavUrl(ctx)[i][j];
					item.color = COLOR_ITEM_TITLE;
					item.bold = false;
					category.items.add(item);
				}
				categoryList.add(category);
			}
		}

		return categoryList;
	}

	private static JSONArray getAllSitesJsonFromCode(Context ctx) {
		InputStream is = null;
		JSONArray result = null;
		try {
			AssetManager am = ctx.getApplicationContext().getAssets();
			is = am.open("navigation_default.json");
			if (is != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
				JSONObject jsonObject = new JSONObject(sb.toString());
				result = (JSONArray) jsonObject.get(TAG_NAVIGATION);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
			}
		}

		return result;
	}

	/**
	 * 读取本地保存的所有数据
	 *
	 * @return
	 */
	private static JSONArray getAllDataFromLocalFile(String path) {
		// 服务器返回说明已经是最新数据，则读取放FILES文件夹里面的数据
		Log.v(TAG, "getAllDataFromFile...");
		String localData = FileUtil.readFileContent(path);
		if (TextUtils.isEmpty(localData)) {
			return null;
		}
		try {
			Log.v(TAG, "read local data from files");
			return new JSONArray(localData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从服务器读取顶部推荐网址和下面的导航数据 若是服务器数据为空或者格式不正确,或者无最新数据，则返回null
	 *
	 * @return
	 */
	private static JSONObject getAllDataFromServer(Context ctx) {
		Log.w(TAG, "getAllDataFromServer...");
		JSONObject object = null;
		if (!TelephoneUtil.isNetworkAvailable(ctx))
			return object;

		ConfigPreferences cp = ConfigPreferences.getInstance(ctx);
		int currentVersion = cp.getCurrentNavigationVersion();
		int currentRecVersion = cp.getCurrentRecommendVersion();
		if(currentRecVersion <= 0){
			currentRecVersion = 1;
		}
		String versionName = TelephoneUtil.getVersionName(ctx);
		if (TelephoneUtil.getVersionCode(ctx) < 6328 || CommonLauncherControl.AZ_PKG ||CommonLauncherControl.BD_PKG) {
			versionName = "6.3.3";
		}
		if (CommonLauncherControl.DX_PKG && TelephoneUtil.getVersionCode(ctx)>=7198) {
			versionName = "6.3.3";
		}

		String url = String.format(NavigationData.getNavigationUrl(ctx), TelephoneUtil.getFirmWareVersion(), currentVersion, currentRecVersion, TelephoneUtil.getVersionCode(ctx), versionName,FAVORITE_ICON_COUNT);
		url = NavigationData.addMoreParams(ctx,url);

		String serverData = GZipHttpUtil.get(url);
		if (serverData != null) {
			try {
				object = new JSONObject(serverData);
				// Log.e(TAG, object.toString());
				// 更新网址数据
				if (object != null && object.isNull(TAG_CODE)) {
					Log.w(TAG, "update Navigation site");
					int newVersion = object.optInt(TAG_VERSION);
					int newRecVersion = object.optInt(TAG_REC_VERSION);
					// 更新网址大全
					if (newVersion > currentVersion) {
						cp.setCurrentNavigationVersion(ctx, newVersion);
						FileUtil.writeFile(NavigationData.getNavigationPath(ctx), object.getJSONArray(TAG_NAVIGATION).toString(), false);
						NaviSiteLoader.saveNewSite(object.getJSONArray(TAG_NAVIGATION).toString());
					}
					// 更新推荐网址
					if (newRecVersion > currentRecVersion) {
						// FileUtil.delAllFile(NAV_ICON_FAV_DIR);
						cp.setCurrentRecommendVersion(newRecVersion);
						FileUtil.writeFile(RECOMMEND_PATH, object.getJSONArray(TAG_NAVIGATION_RECOMMAND).toString(), false);
					}
				} else {
					object = null;
					Log.v(TAG, "No update data from server");
				}

				// 每次启动桌面时，判断是否更新网站icon
				if (object != null) {
					int iconVersion = object.optInt(TAG_ICON_VERSION, 0);
					if (iconVersion > cp.getCurrentNavigationIconVersion() && TelephoneUtil.isSdcardExist()) {
						Log.w(TAG, "update Navigation icon");
						File iconSiteDir = new File(NAV_ICON_SITE_DIR);
						if (iconSiteDir.exists() && iconSiteDir.isDirectory()) {
							FileUtil.delAllFile(NAV_ICON_SITE_DIR);
						}
						cp.setCurrentNavigationIconVersion(iconVersion);
					}
				}
			} catch (Exception e) {
				Log.w(TAG, "server data error,data=" + serverData, e);
			}
		} else {
			Log.w(TAG, "server data is null");
		}

		// LOAD_SERVER_DATA = true;
		return object;
	}

	/**
	 * Description: 异步加载服务器端的推荐网址图片 Author: guojy Date: 2013-4-23 下午12:04:45
	 */
	public static void getRecommendedSiteIconFromServer(final Context ctx, final List<WebSiteItem> list, final NavigationFavoriteSiteView mFavoriteSiteView) {
		/**
		 * 某些机型上SD卡判断存在问题
		 * 后续机型不需要判断SD卡
		 */
//		if (!TelephoneUtil.isSdcardExist()) {
//			return;
//		}
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				boolean reload = false;
				for (WebSiteItem item : list) {
					if(item.iconType == WebSiteItem.TYPE_LOCAL_RES_ICON ){
						try {
							if(LauncherBranchController.isChaoqian(ctx)){
								item.icon = null;
							}else{
								item.icon = NavigationKeepForReflect.createMaskBitmap(BitmapFactory.decodeResource(ctx.getResources(),item.iconId), ctx);
							}
						}catch (Throwable t){
							t.printStackTrace();
						}
						continue;
					}
					if(item.iconType == WebSiteItem.TYPE_USE_APP_ICON){
						try {
							Bitmap launcherIcon = null;
							try {
								ComponentName componentName = ctx.getPackageManager().getLaunchIntentForPackage(item.appPkg).getComponent();
								if(componentName != null){
									launcherIcon = CommonKeepForReflect.getCachedIcon_V8498(componentName);
								}
							}catch (Throwable t){
								t.printStackTrace();
							}
							if(launcherIcon != null && !launcherIcon.isRecycled()){
								try {
									if(LauncherBranchController.isChaoqian(ctx)){
										item.icon = launcherIcon;
									}else{
										item.icon = NavigationKeepForReflect.createMaskBitmap(launcherIcon, ctx);
									}
								} catch (Throwable t) {
									t.printStackTrace();
								}

							}else{
								Drawable drawable = ctx.getPackageManager().getApplicationIcon(item.appPkg);
								try {
									if(LauncherBranchController.isChaoqian(ctx)){
										item.icon = ((BitmapDrawable) drawable).getBitmap();
									}else{
										item.icon = NavigationKeepForReflect.createMaskBitmap(((BitmapDrawable) drawable).getBitmap(), ctx);
									}
								} catch (Throwable t) {
									t.printStackTrace();
									item.icon = ((BitmapDrawable) drawable).getBitmap();
								}

							}

						} catch (Exception e) {
							e.printStackTrace();
							refreshDefaultFavoriteSites(mFavoriteSiteView);
							return;
						}
						reload = true;
						continue;
					}
					String iconPath = "";
					if (item.iconType == WebSiteItem.TYPE_SERVER_ICON) {
						if (item.iconURL == null || !item.iconURL.startsWith("http://")) {
							Log.v(TAG, "iconURL error!");
							refreshDefaultFavoriteSites(mFavoriteSiteView);
							return;
						}
						iconPath = NAV_ICON_FAV_DIR + getSiteIconName(item.iconURL);
					}
					if(item.iconType == WebSiteItem.TYPE_LOCAL_FILE_ICON){
						if(!FileUtil.isFileExits(item.iconPath)){
							refreshDefaultFavoriteSites(mFavoriteSiteView);
							return;
						}
						iconPath = item.iconPath;
					}

					if (!new File(iconPath).exists() && !TelephoneUtil.isNetworkAvailable(ctx)) {
						refreshDefaultFavoriteSites(mFavoriteSiteView);
						return;
					}

					if (!new File(iconPath).exists() && TelephoneUtil.isNetworkAvailable(ctx)) {
						String path = BitmapUtils.saveInternateImage(item.iconURL, iconPath);
						if (TextUtils.isEmpty(path)) {// 网络异常，没有下载到图片
							Log.e(TAG, "network error!");
							refreshDefaultFavoriteSites(mFavoriteSiteView);
							return;
						}
					}

					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inScaled = true;
					opts.inDensity = 320;
					opts.inTargetDensity = ctx.getResources().getDisplayMetrics().densityDpi;

					try {
						if(LauncherBranchController.isChaoqian(ctx)){
							item.icon = BitmapFactory.decodeFile(iconPath, opts);
						}else{
							item.icon = NavigationKeepForReflect.createMaskBitmap(BitmapFactory.decodeFile(iconPath, opts), ctx);
						}
					} catch (Throwable t) {
						t.printStackTrace();
						item.icon = BitmapFactory.decodeFile(iconPath, opts);
					}

					reload = true;
				}

				if (reload) {
					refreshRecommendedOrFavoriteSites(mFavoriteSiteView);
				}
			}
		});

	}

	/**
	 * Description: 异步加载服务端网址大全的分类图标
	 */
	public static void getCategoryIconFromServer(final Context ctx, final List<NavigationCategory> list, final NavigationSiteView siteView) {
		if (!TelephoneUtil.isSdcardExist()) {
			return;
		}
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				boolean reload = false;
				for (NavigationCategory item : list) {
					if (item.iconUrl == null || !item.iconUrl.startsWith("http://")) {
						continue;
					}
					String iconName = getSiteIconName(item.iconUrl);
					final String iconPath = NAV_ICON_SITE_DIR + iconName;
					if (!new File(iconPath).exists() && TelephoneUtil.isNetworkAvailable(ctx)) {
						BitmapUtils.saveInternateImage(item.iconUrl, iconPath);
					}

					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inScaled = true;
					opts.inDensity = 320;
					opts.inTargetDensity = ctx.getResources().getDisplayMetrics().densityDpi;
					item.icon = BitmapFactory.decodeFile(iconPath, opts);
					reload = true;
				}

				if (reload) {
					siteView.postInvalidate();
				}
			}
		});
	}

	private static String getSiteIconName(String itemName) {
		return itemName.hashCode() + ".png";
	}

	public static void createBaseDir() {
		FileUtil.createDir(NAV_ICON_DIR);
		FileUtil.createDir(NAV_ICON_FAV_DIR);
		FileUtil.createDir(NAV_ICON_SITE_DIR);
	}


	/**
	 * @desc 请求次数是否在一定范围内
	 * @author linliangbin
	 * @time 2017/9/22 16:50
	 */
	public boolean isRequestCounterIn() {
		// 每天访问不最多超过3次
		if (loadDataDayTime == 0) {// 第一次连接
			loadDataDayTime = DateUtil.getTodayTime();
		} else {
			if (DateUtil.getTodayTime() > loadDataDayTime) {// 第二天连接
				loadDataDayTime = DateUtil.getTodayTime();
				loadDataCount = 0;
			} else if (loadDataCount >= 3) {// 每天访问不最多超过3次
				return false;
			}
		}
		return true;
	}


	/**
	 * @desc 更新零屏ICON&网址导航数据
	 * 请求成功时返回true
	 * @author linliangbin
	 * @time 2017/9/22 17:00
	 */
	public boolean updateIconAndSiteData(final Context ctx) {
		createBaseDir();
		loadDataCount++;
		return getAllDataFromServer(ctx) != null;
	}


	public void updateAndRefreshNavigationViewForceWithRefresh(final Context ctx, final NavigationView2 navigationView) {
		createBaseDir();
		loadDataCount++;
		navigationView.cardViewHelper.loadCardRecommendWordFromServer();
		if (getAllDataFromServer(ctx) != null) {
			refreshNavigationView(navigationView);
		}else{
			if(LauncherBranchController.isNavigationForCustomLauncher()){
				refreshNavigationView(navigationView);
			}
			NaviSiteLoader.loadSiteCard(navigationView.context, navigationView);
		}
	}



	/**
	 * 刷新导航屏
	 *
	 * @param navigationView
	 */
	private static void refreshNavigationView(final NavigationView2 nav) {
		if (nav != null && nav.getHandler() != null) {
			nav.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if (nav.getFavoriteSiteView() != null) {
						nav.getFavoriteSiteView().loadSites();
					}

					NaviSiteLoader.loadSiteCard(nav.context, nav);
				}
			});
		}
	}

	/**
	 * 刷新导航页推荐网址，或刷新用户自定义添加网址页面
	 *
	 * @param mFavoriteSiteView
	 */
	private static void refreshRecommendedOrFavoriteSites(final NavigationFavoriteSiteView mFavoriteSiteView) {
		if (mFavoriteSiteView == null || mFavoriteSiteView.getHandler() == null)
			return;
		mFavoriteSiteView.getHandler().post(new Runnable() {
			@Override
			public void run() {
				mFavoriteSiteView.refreshSites();
			}
		});
	}

	private static void refreshDefaultFavoriteSites(final NavigationFavoriteSiteView mFavoriteSiteView) {
		if (mFavoriteSiteView == null || mFavoriteSiteView.getHandler() == null)
			return;
		mFavoriteSiteView.getHandler().post(new Runnable() {
			@Override
			public void run() {
				mFavoriteSiteView.loadDefaultSites();
			}
		});
	}
}
