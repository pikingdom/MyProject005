package com.nd.hilauncherdev.plugin.navigation.http;

import android.content.Context;
import android.content.Intent;

/**
 * 下载操作<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class DownloadHelper {
	public static final int MSG_DOWNLOADING = 5001;
	public static final int MSG_DOWNLOAD_PAUSE = 5002;
	public static final int MSG_DOWNLOAD_CANCEL = 5003;
	public static final int MSG_DOWNLOAD_FINISHED = 5004;

	public static final int FILE_APK = 0;
	public static final int FILE_DYNAMIC_APK = 4;
	public static final String KEY_SILENT = "silent";
	public static final String DOWNLOAD_KEY_PRE = "navigation_";
	public static final String ACTION_PROGRESS_CHANGED = "_APK_DOWNLOAD_STATE";
	
	public static void pauseDownloadTask(Context context, String id) {
		Intent intent = new Intent(context.getPackageName() + ".FORWARD_SERVICE");
		intent.putExtra("identification", id);
		intent.putExtra("isSilent23G", true);
		intent.putExtra("operation", "pause");
		context.startService(intent);
	}
	
	public static void cancelDownloadTask(Context context, String id) {
		Intent intent = new Intent(context.getPackageName() + ".FORWARD_SERVICE");
		intent.putExtra("identification", id);
		intent.putExtra("isSilent23G", true);
		intent.putExtra("operation", "cancel");
		context.startService(intent);
	}
	
	public static void continueDownloadTask(Context context, String id) {
		Intent intent = new Intent(context.getPackageName() + ".FORWARD_SERVICE");
		intent.putExtra("identification", id);
		intent.putExtra("isSilent23G", true);
		intent.putExtra("operation", "continue");
		context.startService(intent);
	}
}
