package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 设置
 * 
 * @author chenzhihong_9101910
 * 
 */
public class SettingsPreference {

	private static final String SP_NAME = "settings";
	private final static String SETTING_FONT_STYLE = "settings_font_style";

	private static String fontStyle = "";

	public static String getFontStyle() {
		return fontStyle;
	}

	public static String initFontStyle(Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		fontStyle = sp.getString(SETTING_FONT_STYLE, "");
		return fontStyle;
	}

}
