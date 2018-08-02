package com.nd.hilauncherdev.plugin.navigation.util;

import android.graphics.Color;

/**
 * 颜色相关 工具类
 */
public final class ColorUtil {
	/**
	 * 取相反颜色
	 * @param alpha
	 * @param color
	 * @return int
	 */
	public static int antiColorAlpha(int alpha, int color) {
		if(-1 == alpha){
			alpha = Color.alpha(color);
			if(255 == alpha){
				alpha = 200;
			}
		}
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.argb(alpha, 255 - r, 255 - g, 255 - b);
	}

	/**
	 * 解析颜色值
	 * @param colorStr
	 * @return int
	 */
	public static int parseColor(String colorStr) {
		int color = 0xff000000;
		try {
			color = Color.parseColor(colorStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return color;
	}
}
