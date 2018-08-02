package com.nd.hilauncherdev.plugin.navigation.util;

import java.util.Locale;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;

import android.content.Context;
import android.os.Handler;

/**
 * 全局类<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class Global {

	private static Handler globalHandler = new Handler();

	public final static String BASE_DIR = CommonGlobal.getBaseDir();
	
	public final static String CACHES_HOME = BASE_DIR + "/caches/";

	public static String CUID = "";

	/**
	 * 使用IMAGE LOADER 的图片缓存目录
	 */
	public final static String CACHES_HOME_IMAGE_LOADER = CACHES_HOME + "imageloader/";


	/*** 当前应用版本号 */
	public static int CURRENT_VERSION_CODE = 1;

	/** Context */
	public static Context context = null;

	public static boolean isZh(Context context) {
		Locale lo;
		if (null == context) {
			return true;
		} else {
			lo = context.getResources().getConfiguration().locale;
		}
		if (lo.getLanguage().equals("zh"))
			return true;
		return false;
	}

	public static String getPackageName() {
		return CommonLauncherControl.PKG_NAME;
	}
	
	public static String url2path (boolean newFormatPath,String url, String rootpath){
		if (url==null)
			url = "";
		String rs = rootpath;
		String picname = "";
		if(newFormatPath){
			picname = getPicNameFromUrl(url);
		}else {
			picname = getPicNameFromUrlWithSuff(url);
		}rs = rs+picname;
		rs = renameRes(rs);
		return rs;
	}
	

	public static String renameRes(String path) {
		if (path == null) {
			return null;
		}
		return path.replace(".png", ".a").replace(".jpg", ".b");
	}
	
	/**
	 * 从图片url中获得图片名
	 * @param url
	 * @return
	 */
	public static String getPicNameFromUrlWithSuff(String url){
		String str = "";
		try {
			if ( url==null || "".equals(url) )
				return "";
			str = url;
			String [] s = str.split("\\/");
			str = s[s.length-1];			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str; 
	}

	/**
	 * 针对特殊地址的图片导致缓存文件名相同的问题
	 * 如： http://inews.gtimg.com/newsapp_ls/0/144822875/0
	 * @return
	 */
	public static String getPicNameFromUrl(String url){

 		String str = "";
		try {
			str = url.hashCode() + "";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void initGlobalHandler(){
		if(globalHandler == null){
			globalHandler = new Handler();
		}
	}

	/**
	 * Description: 在主线程中执行的操作，避免重新创建对象
	 * Author: linliangbin
	 * Date: 2016/10/17 18:47
	 */
	public static void runInMainThread(Runnable task) {
		if (task == null)
			return;
		if(globalHandler == null){
			globalHandler = new Handler();
		}
		if(globalHandler != null){
			globalHandler.post(task);
		}
	}

	public static void runInMainThread(Runnable task,int delay) {

		if (task == null)
			return;
		if(globalHandler == null){
			globalHandler = new Handler();
		}
		if(globalHandler != null){
			globalHandler.postDelayed(task,delay);
		}
	}


}