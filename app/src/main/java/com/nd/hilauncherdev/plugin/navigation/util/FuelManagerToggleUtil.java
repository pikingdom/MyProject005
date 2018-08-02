package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

public class FuelManagerToggleUtil {
	public static final String PACKNAME_IONEKEY_OFFSCREEN = "com.nd.android.widget.pandahome.ionekeyoffscreen";
	public static final String IONEKEY_OFFSCREEN_ACTIVITY = "com.nd.android.widget.pandahome.ionekeyoffscreen.OneKeyOffScreenActivity";
	public static final String IONEKEY_OFFSCREEN_RECEIVER = "com.nd.android.ihome.export.lockscreen";

	public static final String PACKNAME_FLASH_LIGHT = "com.nd.android.widget.pandahome.flashlight";
	public static final String FLASH_LIGHT_ACTIVITY = "com.nd.android.widget.pandahome.flashlight.FlashLightToggleActivity";
	public static final String FLASH_LIGHT_BROCAST = "com.nd.hilauncherdev.export.flashLight";

	// 二维码扫描
	public static final String PACKNAME_ZXING_SCAN = "com.nd.android.widget.pandahome.zxing";
	public static final String PACKNAME_ZXING_SCAN_ACTIVITY = "com.nd.android.widget.pandahome.zxing.CaptureActivity";

	/**
	 * 二维码扫描
	 * 
	 * @param ctx
	 */
	public static void widgetZxingScan(Context ctx) {
		widgetToggleSendActivity(ctx, PACKNAME_ZXING_SCAN, PACKNAME_ZXING_SCAN_ACTIVITY);
	}

	/**
	 * 发送Activity 方式统一 widget apk 操作
	 * 
	 * @param ctx
	 * @param packname
	 * @param classname
	 * @return boolean
	 */
	public static synchronized boolean widgetToggleSendActivity(final Context ctx, final String packname, final String classname) {
		PackageInfo packageInfo;
		PackageInfo packageFlashLight = null;
		String pack = "";
		String install_packname = PACKNAME_FLASH_LIGHT;
		CharSequence title = "";
		CharSequence text = "";
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(packname, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			try {
				packageFlashLight = ctx.getPackageManager().getPackageInfo(install_packname, 0);
			} catch (NameNotFoundException e1) {
				packageFlashLight = null;
				e1.printStackTrace();
			}
		}
		if (packageInfo == null && packageFlashLight == null) {// 安装
			if (PACKNAME_IONEKEY_OFFSCREEN.equals(packname)) {
				title = ctx.getText(R.string.hint_install_screenoff);
				text = ctx.getText(R.string.hint_install_screenoff_msg);
			} else if (PACKNAME_ZXING_SCAN.equals(packname)) {
				title = ctx.getText(R.string.hint_install_zxing_scan);
				text = ctx.getText(R.string.hint_install_zxing_scan_msg);
			}
			ApkTools.installWidgetApp(ctx, install_packname, title, text);
			return false;
		} else {
			if (packageInfo != null) {
				pack = packageInfo.packageName;
			} else {
				pack = packageFlashLight.packageName;
				if (hasNewVersion(ctx, pack, packageFlashLight.versionCode)) {// 升级
					if (PACKNAME_IONEKEY_OFFSCREEN.equals(packname)) {
						title = ctx.getText(R.string.upgrade_install_screenoff);
						text = ctx.getText(R.string.upgrade_install_screenoff_msg);
					} else if (PACKNAME_ZXING_SCAN.equals(packname)) {
						title = ctx.getText(R.string.upgrade_install_zxing_scan);
						text = ctx.getText(R.string.upgrade_install_zxing_scan_msg);
					}
					ApkTools.installWidgetApp(ctx, install_packname, title, text);
					return false;
				}
			}
			ComponentName comp = new ComponentName(pack, classname);
			Intent intent = new Intent().setComponent(comp);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// ctx.startActivity(intent);
			intent.putExtra("cuid_key", NavigationView2.CUID);
			intent.putExtra("pkg_name", ctx.getPackageName());
			SystemUtil.startActivitySafely(ctx, intent);
			return true;
		}
	}

	public static boolean hasNewVersion(Context context, String packageName, int oldVersionCode) {
		Resources res = context.getResources();
		int resId = res.getIdentifier(packageName, "string", context.getPackageName());
		if (0 != resId) {
			int newVersionCode = Integer.parseInt(res.getString(resId));
			return newVersionCode > oldVersionCode;
		}
		return false;
	}

}
