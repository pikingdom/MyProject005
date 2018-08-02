package com.nd.hilauncherdev.plugin.navigation.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * apk包安装应用工具类<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class ApkInstaller {

	/**
	 * 应用安装的广播action
	 */
	public final static String RECEIVER_APP_SILENT_INSTALL = "receiver_app_silent_install";
	/**
	 * Intent传递安装状态
	 */
	public final static String EXTRA_APP_INSTALL_STATE = "extra_app_install_state";

	/**
	 * Intent传递安装应用的包名Key
	 */
	public final static String EXTRA_APP_INSTALL_PACAKGE_NAME = "extra_app_install_pacakge_name";

	/**
	 * Intent传递安装应用的包路径Key
	 */
	public final static String EXTRA_APP_INSTALL_APK_PATH = "extra_app_install_apk_path";

	/** 安装状态--正在安装 */
	public final static int INSTALL_STATE_INSTALLING = 10000;
	/** 安装状态--安装成功 */
	public final static int INSTALL_STATE_INSTALL_SUCCESS = 20000;
	/** 安装状态--安装失败 */
	public final static int INSTALL_STATE_INSTALL_FAILED = 30000;

	/**
	 * 具备静默安装的应用安装方法
	 * 
	 * @param context
	 * @param apkFile
	 */
	public static void installApplicationShoudSilent(final Context context, final File apkFile) {
		if (TelephoneUtil.hasRootPermission())// 有root权限
		{

			installApplicationNormal(context, apkFile);

		} else {
			installApplicationNormal(context, apkFile);
		}

	}// end installApplicationShoudSilent

	/**
	 * 安装应用程序,普通安装方式
	 * 
	 * @param ctx
	 * @param mainFile
	 * @return boolean
	 */
	public static boolean installApplicationNormal(Context ctx, File mainFile) {
		try {
			Uri data = Uri.fromFile(mainFile);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(data, "application/vnd.android.package-archive");
			ctx.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
