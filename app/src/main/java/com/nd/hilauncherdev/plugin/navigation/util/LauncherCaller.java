package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.widget.NavigationSiteView;

public class LauncherCaller {


	/**
	 * 	91桌面X5Launcher优化为较为通用的参数配置，具体如下：
	 from : (String) 调用者标示
	 loc : (int) 要调用的逻辑，默认为0，有效值0\1\2\3。各参数值意义如下：
	 0：表示 QQ浏览器->简版QB->系统默认浏览器->自带WebView
	 1、表示 简版QB->系统默认浏览器->自带WebView
	 2、表示 系统默认浏览器->自带WebView
	 3、表示自带WebView.
	 url:(String) 要打开的网页链接地址
	 */

	private static final String URL_FROM = "from";
	private static final String LOC = "loc";
	private static final String NEED_ANATICS = "need_anatics";
	private static final String URL_FROM_NAV = "from_nav";

	public static final int LOC_WITHOUT_QQ_BROWSER = 2;
	public static final int LOC_WITH_QQ_BROWSER = 0;

	public static final String ACTION = "action";
	public static final int ACTION_DEFAULT_LAUNCHER = 1;

	// 打开浏览器时使用的跳转的app
	public static final String TARGET_OPEN_URL_ACTIVITY = "com.nd.hilauncherdev.app.activity.X5Launcher";

	/**
	 * 本地类型选择
	 */
	public static final String LOCAL_DIY = "diy";
	public static final int DIY_WEBVIEW = 0;//默认 自定义WebView
	public static final int DIY_PERFERENCE = 1;//优选框
	
	
	/**
	 * 使用系统ACTION 打开浏览器
	 * 在使用桌面浏览器打开失败的情况下调用
	 * @param context
	 * @param url
	 */
	public static void openUrlBySystem(Context context,String url){
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			context.startActivity(intent);
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	/**
	 * 默认使用QQ浏览器逻辑
	 * 默认使用 BROWSE_HOTSPOT_NAVIGATION_HOTWORD_OR_WIDGET_HOTWORD = 70000401 领取积分
	 * @param context
	 * @param id
	 * @param url
	 */
	public static void openUrl(Context context, String id, String url) {
		openUrl(context, id,  url, 0);
	}

	/**
	 * 默认使用QQ浏览器逻辑
	 * 使用给定的TaskID 领取积分
	 * @param context
	 * @param id
	 * @param url
	 * @param TaskId
	 */
	public static void openUrl(Context context, String id, String url,int TaskId) {
		openUrl(context, id,  url, TaskId,0, 0, 0, 0);
	}


	/**
	 * 默认使用QQ浏览器逻辑
	 * 默认使用 BROWSE_HOTSPOT_NAVIGATION_HOTWORD_OR_WIDGET_HOTWORD = 70000401 领取积分
	 * 并使用CV统计
	 * @param context
	 * @param id
	 * @param url
	 */
	public static void openUrl(Context context, String id, String url,int pageId,int posId,int resId,int resType) {
		openUrl(context, id,  url, 0,pageId,posId, resId, resType,LOC_WITH_QQ_BROWSER,DIY_WEBVIEW);
	}

	/**
	 * 默认使用QQ浏览器逻辑
	 * 积分+CV
	 * @param context
	 * @param id
	 * @param url
	 * @param TaskId
	 * @param pageId
	 * @param posId
	 * @param resId
	 * @param resType
	 */
	public static void openUrl(Context context, String id, String url,int TaskId,int pageId,int posId,int resId,int resType) {
		openUrl(context,id,url,TaskId,pageId,posId,resId,resType,LOC_WITH_QQ_BROWSER);
	}
	
	
	public static void openUrl(Context context, String id, String url,int TaskId,int pageId, int posId, int resId, int resType,int loc ) {
		openUrl(context,id,url,TaskId,pageId,posId,resId,resType,loc,DIY_WEBVIEW);
	}

	public static void openUrl(Context context, String id, String url,int TaskId,int pageId, int posId, int resId, int resType,int loc,int diy) {
		openUrl(context,id,url,TaskId,pageId,posId,resId,resType,loc,diy,false);
	}

	/**
	 * 给定TaskID 领取积分，使用CV统计
	 *
	 * @param context
	 * @param id
	 * @param url
	 * @param TaskId
	 */
	public static void openUrl(Context context, String id, String url, int TaskId, int pageId, int posId, int resId, int resType, int loc, int diy, boolean needSession) {
		openUrl(context, id, url, TaskId, pageId, posId, resId, resType, loc, diy, needSession, false);
	}

	public static void openUrl(Context context, String id, String url, int TaskId, int pageId, int posId, int resId, int resType, int loc, int diy, boolean needSession, boolean needAnatics) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setClassName(context, TARGET_OPEN_URL_ACTIVITY);
			intent.setData(Uri.parse("http://www.google.com"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("url", url);
			intent.putExtra(URL_FROM, URL_FROM_NAV);
			intent.putExtra(LOC, loc);
			intent.putExtra(LOCAL_DIY, diy);
			intent.putExtra(NEED_ANATICS, needAnatics);
			if (resId != 0) {
				intent.putExtra("use_cv", true);
				intent.putExtra("pageId", pageId);
				intent.putExtra("posId", posId);
				intent.putExtra("resId", resId);
				intent.putExtra("resType", resType);
			} else {
				intent.putExtra("use_cv", false);
			}

			if (!TextUtils.isEmpty(id)) {
				intent.putExtra(NavigationSiteView.EXTRA_STAT_SITE_ID, id);
			}
			intent.putExtra("task_id", TaskId);
			if (needSession) {
				intent.putExtra("behavior", 1);
			}
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
			//跳转桌面浏览器失败，使用系统浏览器打开
			openUrlBySystem(context, url);
		}
	}


}
