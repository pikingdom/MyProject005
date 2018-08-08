package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.os.Build;

import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherLibUtil;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLs {

	public static final String PANDAHOME_BASE_URL = UrlConstant.HOST;

	public final static String NAVIGATION_URL = PANDAHOME_BASE_URL + "Soft.ashx/navv6?mt=4&fwv=%s&pc=%s&iconpc=%s&vc=%s&DivideVersion=%s&iconCount=%s";
//    /**
//     * 广告数据URL
//     */
    public final static String URL_DOCK_RECOMMEND_APP = PANDAHOME_BASE_URL + "commonuse/clientconfig.ashx?cname=PluginADV8098&ver=%s";
	/***
	 * 定制版全屏广告配置地址
	 */
	public final static String URL_DOCK_RECOMMEND_APP_FOR_CUSTOM_LAUNCHER = PANDAHOME_BASE_URL + "commonuse/clientconfig.ashx?cname=PluginADForDZ&ver=%s";

	/**
	 * MobileType 1代表IPHONE 2代表WINDOWSMOBILE 3代表 SYMBIAN S60 4代表ANDROID 5代表JAVA
	 * 6代表MTK
	 */
	public static final String MT = "4";
	/**
	 * 产品id 91桌面为6 安卓桌面为 39
	 */
	public static final String PID = "6";
	/**
	 * 熊猫桌面项目编号
	 */
	public static final String PROJECT_OPTION = "1900";

/**
	 * 桌面统一下载应用地址
	 */
	public static final String UNIFIED_DOWNLOAD_PATH = PANDAHOME_BASE_URL + "soft/download.aspx?Identifier=%s&sp=%d";


	/**
	 * 通过包名获取统一下载app地址
	 * Title: getDownloadUrlFromPackageName
	 * Description:
	 *
	 * @param context
	 * @param packageName
	 * @param sp          在当前这个类里有静态变量，请使用里定义的sp变量
	 * @param sessionID   能获取到就传
	 *                    ，获取不到就传null
	 *                    建议下载目录使用BaseConfig.WIFI_DOWNLOAD_PATH目录
	 * @return
	 * @author maolinnan_350804
	 */
	public static String getDownloadUrlFromPackageName(Context context, String packageName, String sessionID, int sp) {
		if (context == null || packageName == null || "".equals(packageName)) {
			return null;
		}
		String downloadAddress = String.format(UNIFIED_DOWNLOAD_PATH, packageName, sp);
		StringBuffer result = new StringBuffer(downloadAddress);
		// 拼接通用统计参数
		addGlobalRequestValue(context, result, sessionID);

		return result.toString();
	}


	/**
	 * 添加全局参数
	 * Title: addGlobalRequestValue
	 * Description:
	 *
	 * @param context
	 * @param sb
	 * @param sessionID 能获取到就传，获取不到就传null
	 * @author maolinnan_350804
	 */
	public static void addGlobalRequestValue(Context context, StringBuffer sb, String sessionID) {
		if (sb == null || context == null)
			return;

		try {
			String imsiNumber = LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getIMSI(context));
			;
			if (null == imsiNumber) {
				imsiNumber = "";
			}

			String imeiNumber = LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getIMEI(context));
			if (null == imeiNumber) {
				imeiNumber = "";
			}

			String DivideVersion = LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getDivideVersion(context));
			if (null == DivideVersion) {
				DivideVersion = "";
			}

			String SupPhone = LauncherHttpCommon.utf8URLencode(Build.MODEL);
			if (null == SupPhone) {
				SupPhone = "";
			}

			String SupFirm = LauncherHttpCommon.utf8URLencode(Build.VERSION.RELEASE);
			if (null == SupFirm) {
				SupFirm = "";
			}

			String CUID = URLEncoder.encode(LauncherLibUtil.getCUID(context), "UTF-8");
			if (null == CUID) {
				CUID = "";
			}

//TODO 空置company 是否OK
			appendAttrValue(sb, "mt", MT);
			appendAttrValue(sb, "tfv", "40000");
			appendAttrValue(sb, "pid", PID);
			appendAttrValue(sb, "imei", imeiNumber);
			appendAttrValue(sb, "imsi", imsiNumber);
			appendAttrValue(sb, "projectoption", PROJECT_OPTION);
			appendAttrValue(sb, "DivideVersion", encodeAttrValue(DivideVersion));
			appendAttrValue(sb, "SupPhone", encodeAttrValue(SupPhone)); // 型号
			appendAttrValue(sb, "supfirm", encodeAttrValue(SupFirm)); // Android版本号
			appendAttrValue(sb, "company", ""); // 制造商
			appendAttrValue(sb, "nt", ""); // 网络类型
			appendAttrValue(sb, "chl", ""); // 渠道ID
			appendAttrValue(sb, "CUID", CUID); //加入CUID
			appendAttrValue(sb, "JailBroken", "0"); // 渠道ID
			if (sessionID != null && !"".equals(sessionID)) {
				appendAttrValue(sb, "sessionid", sessionID);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 拼接参数
	 * <p/>
	 * Title: appendAttrValue
	 * Description:
	 *
	 * @param sb
	 * @param key
	 * @param values
	 * @author maolinnan_350804
	 */
	private static void appendAttrValue(StringBuffer sb, String key, String... values) {
		if (sb.indexOf("?" + key + "=") != -1 || sb.indexOf("&" + key + "=") != -1) {
			return;
		}
		for (String value : values) {
			if (sb.indexOf("?") == -1) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}
	}

	/**
	 * 对参数进行转码
	 * <p>Title: encodeAttrValue</p>
	 * <p>Description: </p>
	 *
	 * @param value
	 * @return
	 * @author maolinnan_350804
	 */
	private static String encodeAttrValue(String value) {
		String returnValue = "";
		try {
			value = URLEncoder.encode(value + "", "UTF-8");
			returnValue = value.replaceAll("\\+", "%20");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public static String getAdPluginUrl(Context context){
        String url = String.format(URL_DOCK_RECOMMEND_APP, 0);
		if(LauncherBranchController.isNavigationForCustomLauncher()){
			url = String.format(URL_DOCK_RECOMMEND_APP_FOR_CUSTOM_LAUNCHER, 0);
		}
        StringBuffer stringBuffer = new StringBuffer(url);

		try {
			stringBuffer.append("&CUID=" + URLEncoder.encode(LauncherLibUtil.getCUID(context), "UTF-8"));
			stringBuffer.append("&pid="+LauncherHttpCommon.getPid());
			stringBuffer.append("&mt=" + LauncherHttpCommon.MT);
			stringBuffer.append("&DivideVersion=" + LauncherHttpCommon.DivideVersion);
		} catch (Exception e) {
			e.printStackTrace();
		}

        return stringBuffer.toString();

	}
}
