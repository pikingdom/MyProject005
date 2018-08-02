package com.nd.hilauncherdev.plugin.navigation.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.R;

public class SystemUtil {

	/** 用于在SearchActivity中的定位动作 */
	public static final int REQUEST_SEARCH_ACTIVITY_POSITION = 11005;
	
	/**
	 * 安全打开一个APP
	 * 
	 * @param ctx
	 * @param intent
	 */
	public static void startActivitySafely(Context ctx, Intent intent) {
		if (ctx == null)
			return;
		if (intent == null) {
			makeShortToast(ctx, R.string.dockbar_null_intent);
			return;
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			ctx.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			makeShortToast(ctx, R.string.activity_not_found);
			Log.e(CommonGlobal.TAG, "Unable to launch. intent=" + intent, e);
		} catch (SecurityException e) {
			makeShortToast(ctx, R.string.activity_not_found);
			Log.e(CommonGlobal.TAG, e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建toast
	 * 
	 * @param ctx
	 * @param resId
	 */
	public static void makeShortToast(Context ctx, int resId) {
		if (resId == 0)
			return;
		Toast.makeText(ctx, ctx.getText(resId), Toast.LENGTH_SHORT).show();
	}

	public static void startActivityForResultSafely(Activity act, Intent intent, int resCode) {
		try {
			act.startActivityForResult(intent, resCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @desc 获取应用启动intent
	 * @author linliangbin
	 * @time 2017/2/27 10:30
	 */
	public static Intent getAppLaunchIntent(Context context,String packageName){
		try {
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


			Intent resultIntent = new Intent(Intent.ACTION_MAIN);
			resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resultIntent.setComponent(intent.getComponent());
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			return resultIntent;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据包名判断apk是否安装
	 * @author dingdj
	 * Date:2014-6-17下午5:46:29
	 *  @param packageName
	 *  @return
	 */
	public static boolean isApkInstalled(Context ctx, String packageName){
		try {
			PackageManager pm = ctx.getPackageManager();
			pm.getPackageInfo(packageName, 0);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
