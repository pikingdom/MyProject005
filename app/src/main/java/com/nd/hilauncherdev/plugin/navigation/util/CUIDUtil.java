package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

import java.net.URLEncoder;

/**
 * CUID<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CUIDUtil {

	private static String CUID = "";

	private static String CUID_PART = "";

	public static String getCUIDPART(Context ctx) {
		if (TextUtils.isEmpty(CUID_PART) && ctx != null) {
			try {
				CUID = NavigationView2.CUID;
				String CUID_encode = URLEncoder.encode(CUID, "UTF-8");
				if (!TextUtils.isEmpty(CUID_encode)) {
					CUID_PART = "&CUID=" + CUID_encode;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CUID_PART;
	}

	public static String getCUID(Context ctx) {
		if(!TextUtils.isEmpty(Global.CUID)){
			return Global.CUID;
		}
		if (TextUtils.isEmpty(CUID) && ctx != null) {
			try {
				CUID = NavigationView2.CUID;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CUID;
	}

}
