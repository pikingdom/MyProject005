package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;

public class CommonGlobal {
	public static final String BAIDU_LAUNCHER_PKG_NAME = "com.baidu.android.launcher";
	public static final String DIANXIN_LAUNCHER_PKG_NAME = "com.dianxinos.dxhome";
	public static final String ANDROID_LAUNCHER_PKG_NAME="com.nd.android.smarthome";
	
	public static String BASE_DIR_NAME = "";
	public static String PKG_NAME = "com.nd.android.pandahome2";
	public static final String TAG = "CommonGlobal";
	
	public static final String URL_VIP="http://url.felink.com/V3ANFb";
	public static final String URL_HAO123 = "http://m.hao123.com/?union=1&from=1017491s&tn=ops1017491s";
	public static final String URL_AITAOBAO = "http://url.ifjing.com/EreIvi";

//	public static final String URL_58="http://url.ifjing.com/RnEFne";
//	public static final String URL_58_ANALYSIS="http://luna.58.com/m/activity?utm_source=58un&spm=m-37368978192654-ms-f-801.chunyunhuangye_91desk0218";
	
	/**
	 * wifi�Զ�����Ŀ¼
	 */
	public static String WIFI_DOWNLOAD_PATH = getBaseDir() + "/WifiDownload/";
	
	public static String getWifiDownloadPath(){
		return  getBaseDir() + "/WifiDownload/";
	}

	public static String getBaseDir() {
		return Environment.getExternalStorageDirectory() + BASE_DIR_NAME;
	}
	
	public static String getCachesHome() {
		return getBaseDir()  + "/caches/";
	}
	
	/**
	 * 当前桌面是否是点心桌面
	 * @param ctx
	 * @return
	 */
	public static boolean isDianxinLauncher(Context ctx){
		try {
			String pkg = ctx.getPackageName();
			if(TextUtils.isEmpty(pkg)){
				return false;
			}
			if(DIANXIN_LAUNCHER_PKG_NAME.equals(pkg)){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return  false;
	}

	/**
	 * 当前桌面是否是安卓桌面
	 * @param ctx
	 * @return
	 */
	public static boolean isAndroidLauncher(Context ctx){
		try {
			String pkg = ctx.getPackageName();
			if(TextUtils.isEmpty(pkg)){
				return false;
			}
			if(ANDROID_LAUNCHER_PKG_NAME.equals(pkg)){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return  false;
	}

	/**
	 * 当前桌面是否是安卓桌面
	 * @param ctx
	 * @return
	 */
	public static boolean isBaiduLauncher(Context ctx){
		try {
			String pkg = ctx.getPackageName();
			if(TextUtils.isEmpty(pkg)){
				return false;
			}
			if(BAIDU_LAUNCHER_PKG_NAME.equals(pkg)){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return  false;
	}

}
