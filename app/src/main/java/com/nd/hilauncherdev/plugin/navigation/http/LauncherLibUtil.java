package com.nd.hilauncherdev.plugin.navigation.http;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import com.nd.hilauncherdev.plugin.navigation.util.CUIDUtil;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

/**
 * 网络操作辅助<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class LauncherLibUtil {

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 取得IMEI号
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIMEI(Context ctx) {
		if (ctx == null)
			return "91";

		String imei = "91";
		try {
			TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			if (imei == null || "".equals(imei))
				return "91";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imei;
	}

	public static String getIMSI(Context ctx) {
		if (ctx == null)
			return "91";

		String imsi = "91";
		try {
			TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
			if (imsi == null || "".equals(imsi))
				return "91";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imsi;
	}

	/**
	 * 通过反射的方法，获取CUID
	 * 
	 * @param ctx
	 */
	public static String getCUID(Context ctx) {
		if (null == ctx)
			return "";

		return CUIDUtil.getCUID(ctx);
	}

	/**
	 * 获取versionName
	 * 
	 * @param context
	 * @param packageName
	 * @return String
	 */
	public static String getDivideVersion(Context context) {
		String versionName = "";
		try {
			PackageInfo packageinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			versionName = packageinfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static boolean isZh(Context context) {
		Locale lo;
		if (null == context) {
			return true;
		} else {
			lo = context.getResources().getConfiguration().locale;
		}
		if (lo.getLanguage().equals("zh"))
			return true;
		return false;
	}

	public static String md5Hex(String data) {
		return new String(encodeHex(md5(data)));
	}

	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}

		return out;
	}

	static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private static MessageDigest getMd5Digest() {
		return getDigest("MD5");
	}

	public static byte[] md5(byte[] data) {
		return getMd5Digest().digest(data);
	}

	public static byte[] md5(String data) {
		return md5(data.getBytes());
	}
}
