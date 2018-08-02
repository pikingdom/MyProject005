package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;


/**
 * CV统计提交类
 * <p>Title: CvAnalysis</p>
 * <p>Description: </p>
 * <p>Company: ND</p>
 * @author    MaoLinnan
 * @date       2015年11月23日
 */
public class CvAnalysis {
	private CvAnalysis(){};
	

	/**
	 * 提交Activity进入
	 * <p>Title: submitPageStartEvent</p>
	 * <p>Description: </p>
	 * @param ctx
	 * @param pageId	页面id
	 * @author maolinnan_350804
	 */
	public static void submitPageStartEvent(Context ctx,int pageId){
		ReflectInvoke.submitPageStartEvent(ctx,pageId);
		Log.e("CvAnalysis:market", pageId + "=========================submitPageStartEvent==========================");

	}
	
	/**
	 * 提交Activity退出
	 * <p>Title: submitPageEndEvent</p>
	 * <p>Description: </p>
	 * @param ctx
	 * @param pageId	页面id
	 * @author maolinnan_350804
	 */
	public static void submitPageEndEvent(Context ctx,int pageId){
		ReflectInvoke.submitPageEndEvent(ctx, pageId);
		Log.e("CvAnalysis:market", pageId + "=========================submitPageEndEvent==========================");
	}

	/**
	 * 提交展示事件
	 * <p>Title: submitShowEvent</p>
	 * <p>Description: </p>
	 * @param ctx
	 * @param pageId	页面id
	 * @param posId		位置id
	 * @param resId		资源id
	 * @param resType	资源类型
	 * @author maolinnan_350804
	 */
	public static void submitShowEvent(Context ctx,int pageId,int posId,int resId,int resType){
		ReflectInvoke.submitShowEvent(ctx,pageId,posId,resId,resType);
		Log.e("CvAnalysis:market", pageId + "==" + posId + "==" + resId + "==" + resType + "=========================submitShowEvent==========================");
	}
	
	/**
	 * 提交点击事件
	 * <p>Title: submitClickEvent</p>
	 * <p>Description: </p>
	 * @param ctx
	 * @param pageId	页面id
	 * @param posId		位置id
	 * @param resId		资源id
	 * @param resType	资源类型
	 * @author maolinnan_350804
	 */
	public static void submitClickEvent(Context ctx,int pageId,int posId,int resId,int resType){
		ReflectInvoke.submitClickEvent(ctx,pageId,posId,resId,resType);
		Log.e("CvAnalysis:market", pageId + "==" + posId + "==" + resId + "==" + resType + "=========================submitClickEvent==========================");
	}

	/**
	 * 提交点击事件
	 * <p>Title: submitClickEvent</p>
	 * <p>Description: </p>
	 * @param ctx
	 * @param pageId	页面id
	 * @param posId		位置id
	 * @param resId		资源id
	 * @param resType	资源类型
	 * @author maolinnan_350804
	 */
	public static void submitClickEvent(Context ctx,int pageId,int posId,int resId,int resType,int sourceId){
		ReflectInvoke.submitClickEvent(ctx,pageId,posId,resId,resType,sourceId);
		Log.e("CvAnalysis:market", pageId + "==" + posId + "==" + resId + "==" + resType + "=========================submitClickEvent==========================");
	}

}
