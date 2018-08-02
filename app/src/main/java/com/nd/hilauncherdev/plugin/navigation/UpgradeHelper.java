package com.nd.hilauncherdev.plugin.navigation;


import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.nd.hilauncherdev.plugin.navigation.http.DownloadHelper;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadState;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

/**
 * 插件升级
 * 
 * @author chenzhihong_9101910
 * 
 */
public class UpgradeHelper {
	public static final String WIFI_DOWNLOAD_PATH =  Global.BASE_DIR + "/WifiDownload/";
	public static final String PLUGIN_DIR = Global.BASE_DIR + "/myphone/plugin/";
	public static final String NAVIGATION_PLUGIN_NAME = "com.nd.hilauncherdev.plugin.navigation";
	public static final String NAVIGATION_PLUGIN_FILENAME = "com.nd.hilauncherdev.plugin.navigation.jar";
	public static final String NAVIGATION_PLUGIN_SAVENAME = "com.nd.hilauncherdev.plugin.navigation_temp.jar";

	public static String id;

	public static void upgradePlugin(Context ctx, String url, int ver) {
		try {
			id = DownloadHelper.DOWNLOAD_KEY_PRE + ver;
			Intent intent = new Intent(ctx.getPackageName() + ".FORWARD_SERVICE");
			intent.putExtra("isSilent23G", true);
			intent.putExtra("identification", id);
			intent.putExtra("fileType", DownloadHelper.FILE_DYNAMIC_APK);
			intent.putExtra("downloadUrl", url);
			intent.putExtra("title", NAVIGATION_PLUGIN_FILENAME);
			if(LauncherBranchController.isNavigationForCustomLauncher()){
				intent.putExtra("savedDir", Environment.getExternalStorageDirectory() + "/" + ReflectInvoke.getBaseDirName(NavigationView2.activity)+ "/WifiDownload/");
			}else{
				intent.putExtra("savedDir", CommonGlobal.getWifiDownloadPath());
			}
			intent.putExtra("savedName", NAVIGATION_PLUGIN_FILENAME);
			ctx.getApplicationContext().startService(intent);
		}catch (Throwable e){
			e.printStackTrace();
		}

	}

	public static void onDownloadProgressReceive(Context context, Handler handler, Intent intent) {
		final String id = intent.getStringExtra("identification");
		if (id == null || !id.startsWith(DownloadHelper.DOWNLOAD_KEY_PRE)) {
			return;
		}

		int dlState = intent.getIntExtra("state", DownloadState.STATE_NONE);
		int progress = intent.getIntExtra("progress", 0);
		if (dlState == DownloadState.STATE_DOWNLOADING) {
			Message msg = new Message();
			msg.obj = progress;
			msg.what = DownloadHelper.MSG_DOWNLOADING;
			handler.sendMessage(msg);
		} else if (dlState == DownloadState.STATE_PAUSE) {
			handler.sendEmptyMessage(DownloadHelper.MSG_DOWNLOAD_PAUSE);
		} else if (dlState == DownloadState.STATE_CANCLE) {
			handler.sendEmptyMessage(DownloadHelper.MSG_DOWNLOAD_CANCEL);
		} else if (dlState == DownloadState.STATE_FINISHED) {
			handler.sendEmptyMessage(DownloadHelper.MSG_DOWNLOAD_FINISHED);
		}
	}
}
