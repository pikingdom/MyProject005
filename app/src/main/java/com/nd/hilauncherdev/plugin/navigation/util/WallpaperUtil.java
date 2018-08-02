package com.nd.hilauncherdev.plugin.navigation.util;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

import java.io.FileInputStream;

/**
 * Created by linliangbin on 2017/6/2 15:11.
 */

public class WallpaperUtil {

    /**
     * 重新计算cellLayout上单元格的高宽，防止因屏幕高度获取不对引发的错误
     *
     * @return int[]
     */
    public static int[] getScreenWH(Context ctx) {
        int[] screenWH = {320, 480};
        if (ctx == null) {
            return screenWH;
        }

        final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        boolean isPortrait = display.getWidth() < display.getHeight();
        final int width = isPortrait ? display.getWidth() : display.getHeight();
        final int height = isPortrait ? display.getHeight() : display.getWidth();
        screenWH[0] = width;
        screenWH[1] = height;

        return screenWH;
    }


    /**
     * 是否大屏幕
     *
     * @return boolean
     */
    public static boolean isLargeScreen(Context ctx) {
        int w = getScreenWH(ctx)[0];
        if (w >= 480)
            return true;
        else
            return false;
    }

    /**
     * 获取壁纸高宽
     *
     * @return int[]
     */
    public static int[] getWallpaperWH(Context ctx) {
        int[] wh = getScreenWH(ctx);
        return new int[]{wh[0] * 2, wh[1]};
    }


    /**
     * 非线程应用壁纸
     *
     * @param ctx
     * @param wallpaperFileFullName
     */
    public static void applyWallpaper(final Context ctx, final String wallpaperFileFullName) {
        if (null == wallpaperFileFullName || wallpaperFileFullName.equals(""))
            return;

        try {
            // 获取宽高
            int[] wh = BitmapUtils.getImageWH(wallpaperFileFullName);
            if (isLargeScreen(ctx)) {// 大分辨率使用壁纸流加快速度
                if (wh[0] <= 960 && wh[1] <= 800) {
                    WallpaperManager.getInstance(ctx).setStream(
                            new FileInputStream(wallpaperFileFullName));
                    return;
                }
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            int[] wallpaperWH = getWallpaperWH(ctx);
            int inSampleSize = Math.max(wh[0] / wallpaperWH[0], wh[1] / wallpaperWH[1]);
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            if (inSampleSize < 1) {
                inSampleSize = 1;
            }
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(wallpaperFileFullName, options);
            if (null != bitmap) {
                int mSrcBitmapWidth = bitmap.getWidth();
                int mSrcBitmapHeight = bitmap.getHeight();
                Bitmap croppedImage = Bitmap.createBitmap(bitmap, 0, 0, mSrcBitmapWidth, mSrcBitmapHeight, null, false);
                WallpaperManager.getInstance(ctx).setBitmap(croppedImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
