package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

public class ScreenUtil {

    public static int dip2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static boolean isOrientationLandscape(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        }
        return false;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int getCurrentScreenWidth(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        boolean isLand = isOrientationLandscape(context);
        if (isLand) {
            return metrics.heightPixels;
        }

        return metrics.widthPixels;
    }

    public static void setHeightForWrapContent(Context context, View view) {
        int screenWidth = getCurrentScreenWidth(context);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY);

        view.measure(widthMeasureSpec, heightMeasureSpec);
        int height = view.getMeasuredHeight();
        view.getLayoutParams().height = height;
    }

    public static int[] getScreenWH(Context ctx) {
        int[] screenWH = {720, 1280};
        try {
            if (ctx == null) {
                Log.e("ScreenUtil.getScreenWH", "ApplicationContext is null!");
                return screenWH;
            }

            final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            final Display display = windowManager.getDefaultDisplay();
            boolean isPortrait = display.getWidth() < display.getHeight();
            final int width = isPortrait ? display.getWidth() : display.getHeight();
            final int height = isPortrait ? display.getHeight() : display.getWidth();
            screenWH[0] = width;
            screenWH[1] = height;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return screenWH;
    }

    public static int getCurrentScreenHeight(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        boolean isLand = isOrientationLandscape(context);
        if (isLand) {
            return metrics.widthPixels;
        }
        return metrics.heightPixels;
    }

    /**
     * 获取屏幕信息，如大小、屏幕密度、字体缩放等
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getMetrics(Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        if (ctx == null) {
            Log.e("ScreenUtil.getMetrics", "ApplicationContext is null!");
            return metrics;
        }

        final WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        display.getMetrics(metrics);

        boolean isPortrait = display.getWidth() < display.getHeight();
        final int width = isPortrait ? display.getWidth() : display.getHeight();
        final int height = isPortrait ? display.getHeight() : display.getWidth();
        metrics.widthPixels = width;
        metrics.heightPixels = height;

        return metrics;
    }

    /**
     * 获取屏幕密度
     *
     * @return float
     */
    public static float getDensity(Context ctx) {
        DisplayMetrics metrics = getMetrics(ctx);
        return metrics.density;
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        return metrics.widthPixels;
    }


    /**
     * @desc 获取状态栏高度
     * @author linliangbin
     * @time 2017/9/15 10:40
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> localClass = Class.forName("com.android.internal.R$dimen");
            Object localObject = localClass.newInstance();
            int i = ((Integer) localClass.getField("status_bar_height").get(localObject)).intValue();
            int mStatusBarHeight = context.getResources().getDimensionPixelSize(i);
            return mStatusBarHeight;
        } catch (Throwable e) {
            e.printStackTrace();
            return ScreenUtil.dip2px(context, 25);
        }
    }

}
