package com.nd.hilauncherdev.plugin.navigation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 图片处理<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class BitmapUtils {

	protected static String TAG = "BitmapUtils";

	protected static int sIconWidth = -1;
	protected static int sIconHeight = -1;

	protected static final Rect sOldBounds = new Rect();
	protected static final Canvas sCanvas = new Canvas();
	private static Rect maxRect;
	private static Rect minRect;
	private static Rect defaultThemeIconMaskRect;

	static {
		sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}



	public static int[] getImageWH(String path) {
		int[] wh = new int[]{-1, -1};
		if(path == null) {
			return wh;
		} else {
			File file = new File(path);
			if(file.exists() && !file.isDirectory()) {
				FileInputStream is = null;

				try {
					BitmapFactory.Options e = new BitmapFactory.Options();
					e.inJustDecodeBounds = true;
					is = new FileInputStream(path);
					BitmapFactory.decodeStream(is, (Rect)null, e);
					wh[0] = e.outWidth;
					wh[1] = e.outHeight;
				} catch (Throwable var13) {
					Log.w(TAG, "getImageWH Throwable.", var13);
				} finally {
					if(is != null) {
						try {
							is.close();
						} catch (Exception var12) {
							var12.printStackTrace();
						}
					}

				}
			}

			return wh;
		}
	}




	/**
	 * 保存在线图片
	 * 
	 * @param urlString
	 * @param filePath
	 * @return 成功返回保存的路径，失败返回null
	 */
	public static String saveInternateImage(String urlString, String filePath) {

		InputStream is = null;
		Bitmap bmp = null;
		try {
			// HttpResponse response =
			// WebUtil.getSimpleHttpGetResponse(urlString, null);
			// if (response == null)
			// return null;
			HttpCommon httpCommon = new HttpCommon(urlString);
			HttpEntity entity = httpCommon.getResponseAsEntityGet(null);
			if (entity == null)
				return null;
			Header resHeader = entity.getContentType();
			String contentType = resHeader.getValue();
			CompressFormat format = null;
			if (contentType != null && contentType.equals("image/png")) {
				format = Bitmap.CompressFormat.PNG;
			} else {
				format = Bitmap.CompressFormat.JPEG;
			}
			is = entity.getContent();
			bmp = BitmapFactory.decodeStream(is);
			if (saveBitmap2file(bmp, filePath, format))
				return filePath;
			else
				// 失败，删除文件
				FileUtil.delFile(filePath);
		} catch (Exception e) {
			Log.d(TAG, "function saveInternateImage expose exception:" + e.toString());
			// 失败，删除文件
			FileUtil.delFile(filePath);
		} finally {

			try {
				if (is != null)
					is.close();
				if (bmp != null)
					bmp.recycle();
			} catch (IOException e) {
			}
		}

		return null;
	}

	/**
	 * 保存Bitmap为文件
	 * 
	 * @param bmp
	 * @param filePath
	 *            保存的文件路径
	 * @param format
	 *            压缩格式
	 * @return 成功返回true
	 */
	public static boolean saveBitmap2file(Bitmap bmp, String filePath, CompressFormat format) {
		if (bmp == null || bmp.isRecycled())
			return false;

		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null == stream)
			return false;

		return bmp.compress(format, quality, stream);
	}

	public static int getHeightByWidth(Drawable drawable, int width) {
		try {
			int bmpW = ((BitmapDrawable) drawable).getBitmap().getWidth();
			int bmpH = ((BitmapDrawable) drawable).getBitmap().getHeight();
			return bmpH * width / bmpW;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * bitmap转换为圆角
	 *
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap) {
		return toRoundCorner(bitmap, 10);
	}

	/**
	 * bitmap转换为圆角 服务端统计的崩溃日志，有相当多的trying to use a recycled
	 * bitmap，从这个方法抛出，因此加了异常捕获
	 *
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if (bitmap == null || bitmap.isRecycled())
			return null;
		Bitmap output = null;

		try {
			if (bitmap.getWidth()<=0||bitmap.getHeight()<=0) {
				return null;
			}
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
//			bitmap.recycle();
			canvas = null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return output;
	}

}
