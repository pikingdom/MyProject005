package com.nd.hilauncherdev.plugin.navigation.util;

import java.io.File;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.SettingsPreference;

public class PaintUtils2 {

	private static Typeface mTypeface;
	private static String mFontStyle; // flag var for reuse typeface obj

	
	/**
	 * 返回一个装配typeface属性的paint
	 * @param Flag 属性
	 * @return Paint对象
	 */
	public static Paint getPaintAssemblyTypeface(int Flag){
		Paint paint = new Paint(Flag);
		// assemblyTypeface(paint);
		return paint;
	}
	
	/**
	 * 为paint对象装配typeface属性, 属性初始值由SettingsPreference中取得
	 * @param paint 被装配的对象
	 */
	public static final void assemblyTypeface(Paint paint) {
		String fontStyle = SettingsPreference.getFontStyle();
		if (TextUtils.isEmpty(fontStyle)) {
			mTypeface = null;
			mFontStyle = "";
			paint.setTypeface(null); // Pass null to clear any previous
										// typeface.
			return;
		}

		try {
			if (!fontStyle.equals(mFontStyle) || mTypeface == null) {
				File file = new File(fontStyle);
				if (!file.exists() || !file.isFile())
					return;

				mTypeface = Typeface.createFromFile(file);
				mFontStyle = fontStyle;
				Log.d("FONTTEXT", "重建typeface对象!");
			} // else (fontStyle equals mFontStyle && mTypeface != null),
				// reusemTypeface obj

			paint.setTypeface(mTypeface);

		} catch (Exception e) {
			e.printStackTrace();
			paint.setTypeface(null);
		}
	}
}
