package com.nd.hilauncherdev.plugin.navigation.util;

import android.text.TextUtils;

public class UrlUtilC {

	public static String setUrlParam(String urlStr, String name, String value) {
		if (TextUtils.isEmpty(urlStr)) {
			return urlStr;
		}

		try {
			if (urlStr.indexOf("?" + name + "=") < 0 && urlStr.indexOf("&" + name + "=") < 0) {
				if (urlStr.indexOf("?") > 0) {
					urlStr += "&";
				} else {
					urlStr += "?";
				}

				urlStr += name + "=" + value;
			} else {
				String nameAll = "?" + name + "=";
				int sta = urlStr.indexOf(nameAll);
				if (sta < 0) {
					nameAll = "&" + name + "=";
					sta = urlStr.indexOf("&" + name + "=");
				}

				int end = urlStr.indexOf("&", sta + nameAll.length());
				if (end < 0) {
					end = urlStr.length();
				}

				String oldParamAll = urlStr.substring(sta, end);
				urlStr = urlStr.replace(oldParamAll, nameAll + value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urlStr;
	}
}
