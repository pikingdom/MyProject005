package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.dian91.ad.AdvertSDKManager;
import com.dian91.ad.AdvertSDKManager.AdvertInfo;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.http.DownloadState;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;

import java.util.HashMap;
import java.util.List;

/**
 * Description: 封装了sdk控制和下载监听
 * Author: guojianyun_dian91 
 * Date: 2016年1月6日 下午6:55:03
 */
public class AdvertSDKController {

	public static HashMap<String, AdvertInfo> downloadAdvertMap = new HashMap<String, AdvertInfo>();
	private static BroadcastReceiver mProgressReceiver;
	public static final String EXTRA_IDENTIFICATION = "identification";
	public static final String EXTRA_DOWNLOAD_URL = "download_url";
	public static final String EXTRA_PROGRESS = "progress";
	public static final String EXTRA_DOWNLOAD_SIZE = "download_size";
	public static final String EXTRA_STATE = "state";
	public static final String EXTRA_TOTAL_SIZE = "total_size";
	public static final String EXTRA_ADDITION = "addition";
	public static final String EXTRA_FILE_TYPE = "file_type";


	/** 视频列表视频广告位 */
	public static final String AD_POSITION_VIDEO_AD = "54";
	/** 视频列表Banner广告位 */
	public static final String AD_POSITION_VIDEO_BANNER = "55";
	/** 全屏弹出广告*/
	public static final int AD_POSITION_POPUP_AD = 56;

	/**
	 * apk应用下载广播通知
	 */
	public static final String ACTION_DOWNLOAD_STATE = "com.nd.android.pandahome2_APK_DOWNLOAD_STATE";

	/**
	 * Description: 应用初始化时使用，注意如果有多个进程，每个进程初始化时需要调用
	 * Author: guojianyun_dian91 
	 * Date: 2016年1月6日 下午6:54:33
	 * @param context
	 */
	public static void init(Context context,String channel){

		AdvertSDKManager.init(context,Integer.valueOf( LauncherHttpCommon.getPid()), channel);

		if(mProgressReceiver == null){
			mProgressReceiver = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					String url =  intent.getStringExtra(EXTRA_DOWNLOAD_URL);
					String id =  intent.getStringExtra(EXTRA_IDENTIFICATION);
					int dlState = intent.getIntExtra(EXTRA_STATE, DownloadState.STATE_NONE);
					if (dlState == DownloadState.STATE_FINISHED) {
						for (String key : downloadAdvertMap.keySet()) {
							if ((id != null && id.contains(key)) || (url != null && url.contains(key))) {
								if (CommonGlobal.isDianxinLauncher(context)) {
									AdvertSDKManager.submitFinishDownloadEvent(context, downloadAdvertMap.get(key),CommonGlobal.DIANXIN_LAUNCHER_PKG_NAME);
								}else {
									AdvertSDKManager.submitFinishDownloadEvent(context, downloadAdvertMap.get(key),CommonGlobal.PKG_NAME);
								}
								downloadAdvertMap.remove(key);
								break;
							}
						}
					}
				}
			};
			IntentFilter filter = new IntentFilter(ACTION_DOWNLOAD_STATE);
			context.registerReceiver(mProgressReceiver, filter);
		}
	}
	
	
	/**
	 * Description: 获取广告信息(从9004接口获取)
	 * Author: guojianyun_dian91 
	 * Date: 2015年12月31日 下午4:30:23
	 * @param ctx
	 * @param pos 广告投放位置，多个用逗号隔开例如0,1,2，位置值请对照广告位置标识 
	 * @return
	 */
	public static List<AdvertInfo> getAdvertInfos(Context ctx, String pos) {
		return getAdvertInfos(ctx, pos, 0, 0);
	}
	
	/**
	 * Description: 获取广告信息(从9004接口获取)
	 * Author: guojianyun_dian91 
	 * Date: 2015年12月29日 下午3:53:32
	 * @param ctx
	 * @param pos 广告投放位置，多个用逗号隔开例如0,1,2，位置值请对照广告位置标识 
	 * @param width 广告位宽度
	 * @param height 广告位高度
	 * @return
	 */
	public static List<AdvertInfo> getAdvertInfos(Context ctx, String pos, int width, int height) {
		return AdvertSDKManager.getAdvertInfos(ctx, pos);
	}
	
	/**
	 * Description: 展示广告时调用
	 * Author: guojianyun_dian91 
	 * Date: 2015年12月31日 下午4:31:28
	 * @param act
	 * @param adverInfo
	 */
	public static void submitShowEvent(final Context act, final Handler mHandler, final AdvertInfo adverInfo){
		AdvertSDKManager.submitShowEvent(act, mHandler, adverInfo);
	}
	
	/**
	 * Description: 点击广告时调用
	 * Author: guojianyun_dian91 
	 * Date: 2015年12月31日 下午4:31:48
	 * @param act
	 * @param adverInfo
	 */
	public static void submitClickEvent(final Context act, final Handler mHandler, final AdvertInfo adverInfo){
		AdvertSDKManager.submitClickEvent(act, mHandler, adverInfo);
	}
	
	/**
	 * Description: 开始下载App时调用
	 * Author: guojianyun_dian91 
	 * Date: 2015年12月31日 下午4:33:33
	 * @param ctx
	 * @param adverInfo
	 * @param downLoadKey 可以唯一标示该下载
	 */
	public static void submitStartDownloadEvent(Context ctx, AdvertInfo adverInfo, String downLoadKey){
		if (CommonLauncherControl.DX_PKG) {
			AdvertSDKManager.submitStartDownloadEvent(ctx, adverInfo,CommonGlobal.DIANXIN_LAUNCHER_PKG_NAME);
		}else {
			AdvertSDKManager.submitStartDownloadEvent(ctx, adverInfo,CommonGlobal.PKG_NAME);
		}
		downloadAdvertMap.put(downLoadKey, adverInfo);
	}

	/**
	 * 广告SDK曝光统计
	 * @param context
	 * @param modelId
	 * @param resId
	 */
	public static void submitECShowURL(Context context,int modelId,int resId){
		AdvertSDKManager.submitECShowURL(context,modelId,resId);
	}

	public static void unregisterReceiver(Context context) {
		try {
			if (mProgressReceiver != null) {
				context.unregisterReceiver(mProgressReceiver);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
