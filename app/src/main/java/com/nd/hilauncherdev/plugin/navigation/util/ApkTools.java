package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.View;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApkTools {
	/**
	 * 重写未安装方法
	 * @param ctx
	 * @param packname
	 * @param title
	 * @param text -1 = 不升级 0 = 升级 1 = 一键关屏升级 2= 一键关屏安装3 = 二维码扫描升级 4= 二维码扫描安装
	 * 
	 */
	public static void installWidgetApp(final Context ctx, final String packname, final CharSequence title, CharSequence text) {
		AssetManager am = ctx.getApplicationContext().getAssets();
		String[] files = null;
		try {
			files = am.list("");
		} catch (IOException e) {
			e.printStackTrace();
		}


		boolean hasApk = false;
		if(files != null){
			for (String apk : files) {
				if (apk.equalsIgnoreCase(packname)) {
					hasApk = true;
					break;
				}
			}
		}

		final DialogCommon dlg = new DialogCommon(ctx, title, text, ctx.getText(R.string.common_install_now), ctx.getText(R.string.common_button_cancel));
		final boolean finalHasApk = hasApk;
		dlg.setClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(finalHasApk){
					copyAndInstallPkg(ctx, packname);
				}else{
					downloadWidgetApp(ctx,packname,title.toString());
				}
				dlg.dismiss();
			}
		}, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		dlg.setCancelable(false);
		dlg.show();
	}


	public static void downloadWidgetApp(final Context ctx, final String packname, final String title){
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				try {
					String downloadUrl = URLs.getDownloadUrlFromPackageName(ctx,packname,null,AppAnalysisConstant.SP_LAUNCHER_RECOMMEND_OWN_APP);
					String id = DownloadHelper.DOWNLOAD_KEY_PRE + packname;
					Intent intent = new Intent(ctx.getPackageName() + ".FORWARD_SERVICE");
					intent.putExtra("identification", id);
					intent.putExtra("fileType", DownloadHelper.FILE_APK);
					intent.putExtra("downloadUrl", downloadUrl);
					intent.putExtra("title", title);
					intent.putExtra("savedDir", CommonGlobal.getWifiDownloadPath());
					intent.putExtra("savedName", packname);
					ctx.getApplicationContext().startService(intent);

				}catch (Throwable e){
					e.printStackTrace();
				}

			}
		});
	}
	
	/**
	 * 从Assets安装一个应用
	 * @param context
	 * @param fileName
	 */
	@SuppressWarnings("deprecation")
	public static void copyAndInstallPkg(Context context, String fileName) {
		AssetManager am = context.getApplicationContext().getAssets();
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = am.open(fileName);
			fos = context.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1)
				fos.write(buffer, 0, len);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		// 安装APK
		installApplication(context, new File(context.getFileStreamPath(fileName).getAbsolutePath()));
	}

	/**
	 * 安装应用程序(必须在UI线程中调用，否则会出错)
	 * @param ctx
	 * @param mainFile
	 */
	public static void installApplication(Context ctx, File mainFile) {
		ApkInstaller.installApplicationShoudSilent(ctx, mainFile);
	}
}
