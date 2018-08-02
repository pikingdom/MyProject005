package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.BuildConfig;


/**
 * Description: 定制版不同产商配置
 * Author: guojianyun
 * Date: 2016年8月17日 上午10:42:23
 */
public class LauncherBranchController {

	/**
	 * @desc 当前是否为定制版零屏
	 * @author linliangbin
	 * @time 2017/2/8 13:57
	 */
	public static boolean isNavigationForCustomLauncher(){
		return BuildConfig.NAVIGATION_FOR_CUSTOM_LAUNCHER;
	}

	/**
	 * 定制版版本号范围：
	 *
	 * 超前（illusion）- com.android.launcher8
		 7.5.2 ~7.7.9

	   2345 - com.android.newline.lineuplauncher
	 	 7.8 ~ 7.8.x

	   炽热互动 - com.android.superhot.launcher
	     7.9 ~ 7.9.x

	   天湃-智桌面（天盘）智桌面 - com.android.newline.smarthome
	 	 8.0 ~ 8.0.9

	   帆悦(Color) - com.android.custom.launcher
	     8.1 ~ 8.1.9

	   刷机精灵
	     8.2 ~ 8.2.9

	   橡树未来（future桌面）
	     8.3 ~ 8.3.9 - com.android.custom.desktop

	   猎鹰
	 	 8.4 ~ 8.4.9 -

	   新时空
	     8.5 ~ 8.5.9 -

	   神龙(趣桌面) - com.android.newline.funlauncher
	     8.7 ~ 8.7.9

	   铭来（live桌面） - com.android.desktop
	     8.8 ~ 8.8.9

	   展硕（灵犀桌面）- com.android.newline.spiritlauncher
	     8.9 ~ 8.9.9

	   蘑菇时代
	     9.0 ~ 9.0.9
	 */



	/**
	 * Description: 是否力天定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:51:01
	 * @return
	 */
	public static boolean isChaoqian(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("7.5")){
				return true;
			}
			if(!TextUtils.isEmpty(versName) && versName.startsWith("7.6")){
				return true;
			}
			if(!TextUtils.isEmpty(versName) && versName.startsWith("7.7")){
				return true;
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Description: 是否天湃智桌面定制版（天盘）
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:51:01
	 * @return
	 */
	public static boolean isTianpan(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.0")){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}



	/**
	 * Description: 是否鼎开定制版（改名为掌硕）
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:51:01
	 * @return
	 */
	public static boolean isZhangShuo(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.9")){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}






	/**
	 * Description: 是否帆悦定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:51:01
	 * @return
	 */
	public static boolean isFanYue(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.1")){
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}



	/**
	 * Description: 是否刷机精灵
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:51:01
	 * @return
	 */
	public static boolean isShuaJiJingLing(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.2")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}



	/**
	 * Description:是否椒盐（改为像素未来定制版）
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isXiangshu(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.3")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Description:是否猎鹰定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isLieYing(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.4")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Description:是否新时空定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isXinShiKong(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.5")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Description:是否铭来定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isMinglai(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.8")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Description:是否神龙定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isShenlong(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("8.7")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Description:是否2345定制版
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean is2345(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("7.8")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Description:是否Hot桌面
	 * Author: guojianyun
	 * Date: 2016年8月17日 上午10:44:24
	 * @return
	 */
	public static boolean isHot(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("7.9")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * @desc 是否为蘑菇时代定制版
	 * @author linliangbin
	 * @time 2017/9/21 11:44
	 */
	public static boolean isMohuShidai(Context context){
		try {
			String versName = TelephoneUtil.getVersionName(context);
			if(!TextUtils.isEmpty(versName) && versName.startsWith("9.0")) {
				return true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}



	/**
	 * 获取搜狐新闻的统计ID
	 * 包名相同的定制版，ID也不相同以做区分
	 * @param context
	 * @return
     */
	public static int getSohuAnaticsID(Context context){

		if(LauncherBranchController.isNavigationForCustomLauncher()){
			if(isTianpan(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_TIANPAN;
			}else if(isFanYue(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_FANYUE;
			}else if(isShuaJiJingLing(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_SHUAJIJINGLING;
			}else if(isXiangshu(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_XIANGSHUWEILAI;
			}else if(isLieYing(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_LIEYING;
			}else if(isShuaJiJingLing(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_SHUAJIJINGLING;
			}else if(isXinShiKong(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_XINSHIKONG;
			}else if(isZhangShuo(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_ZHANGSHUO;
			}else if(isMinglai(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_MINGLAI;
			}else if(isShenlong(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_SHENLONG;
			}else if(is2345(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_2345;
			}else if(isHot(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_HOT;
			}else if(isMohuShidai(context)){
				return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_MOGU;
			}
			return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS_LITIAN;
		}else{
			return AnalyticsConstant.NAVIGATION_SCREEN_SOUHU_NEWS;
		}

	}
}
