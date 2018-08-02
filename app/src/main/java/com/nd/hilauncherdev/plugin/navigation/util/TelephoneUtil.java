package com.nd.hilauncherdev.plugin.navigation.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelephoneUtil {

    private static final String TAG = "TelephoneUtil";

    /**
     * 获取数字型API_LEVEL 如：4、6、7
     *
     * @return int
     */
    @SuppressWarnings("deprecation")
    public static int getApiLevel() {
        int apiLevel = 7;
        try {
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiLevel;
        // android.os.Build.VERSION.SDK_INT Since: API Level 4
        // return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 取得IMEI号
     *
     * @param ctx
     * @return String
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm == null)
            return "";
        String result = null;
        try {
            result = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null)
            return "";

        return result;
    }

    /**
     * 取得MAC
     *
     * @param ctx
     * @return String
     */
    public static String getMAC(Context ctx) {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) (ctx.getSystemService(Context.WIFI_SERVICE));
            if (wifi == null) {
                return mac;
            }

            WifiInfo info = wifi.getConnectionInfo();
            if (info == null) {
                return mac;
            }
            mac = info.getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac;
    }


    /**
     * 返回屏幕分辨率,字符串型。如 320x480
     *
     * @param ctx
     * @return String
     */
    public static String getScreenResolution(Context ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        String resolution = width + "x" + height;
        return resolution;
    }


    public static String getDisplayDensity(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        String density = "";
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                density = "ldpi";
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                density = "mdpi";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                density = "hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                density = "xhdpi";
                break;
            case 480:
                density = "xxhdpi";
                break;
            case 640:
                density = "xxxhdpi";
                break;
            default:
                break;
        }

        return density;
    }


    public static String getSimOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimOperator();
    }


    /**
     * 获取当前语言
     *
     * @return String
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }


    /**
     * 是否拥有root权限
     */
    public static boolean hasRootPermission() {
        boolean rooted = true;
        try {
            File su = new File("/system/bin/su");
            if (su.exists() == false) {
                su = new File("/system/xbin/su");
                if (su.exists() == false) {
                    rooted = false;
                }
            }
        } catch (Exception e) {
            rooted = false;
        }
        return rooted;
    }


    /**
     * wifi是否启动
     *
     * @param ctx
     * @return boolean
     */
    public static boolean isWifiEnable(Context ctx) {
        if (ctx == null) {
            return false;
        }
        try {
            return isWifiNetwork(ctx);
        } catch (Exception e) {
            e.printStackTrace();
            return isWifiOpen(ctx);
        }
    }

    private static boolean isWifiOpen(Context ctx) {
        try {
            Object obj = ctx.getSystemService(Context.WIFI_SERVICE);
            if (obj == null)
                return false;

            WifiManager wifiManager = (WifiManager) obj;
            return wifiManager.isWifiEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isWifiNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 网络是否可用
     *
     * @param context
     * @return boolean
     */
    public synchronized static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        if (context == null) {
            return result;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (null != connectivityManager) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * @desc 当前是否是可用的流量网络
     * @author linliangbin
     * @time 2017/6/12 11:27
     */
    public synchronized static boolean isNonWifiNetworkAvailable(Context context) {
        try {
            boolean result = false;
            if (context == null) {
                return result;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (null != connectivityManager) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
                if (null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {
                    if (networkInfo.getType() == 1) {
                        result = false;
                    } else {
                        result = true;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 获取字符串型的固件版本，如1.5、1.6、2.1
     *
     * @return String
     */
    @SuppressWarnings("deprecation")
    public static String getFirmWareVersion() {
        // 获取固件版本号
        String versionName = null;
        String regEx = "([0-9]\\.[0-9])|([0-9]\\.[0-9]\\.[0-9])";
        try {
            versionName = Build.VERSION.RELEASE;
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(versionName);
            if (!matcher.matches()) {
                versionName = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            versionName = null;
        }
        if (versionName == null) {
            versionName = getFirmWareVersionFromCustom();
        }
        return versionName;
    }

    private static String getFirmWareVersionFromCustom() {
        final String version_3 = "1.5";
        final String version_4 = "1.6";
        final String version_5 = "2.0";
        final String version_6 = "2.0.1";
        final String version_7 = "2.1";
        final String version_8 = "2.2";
        final String version_9 = "2.3";
        final String version_10 = "2.3.3";
        final String version_11 = "3.0";
        final String version_12 = "3.1";
        final String version_13 = "3.2";
        final String version_14 = "4.0";
        final String version_15 = "4.0.3";
        final String version_16 = "4.1.1";
        final String version_17 = "4.2";
        final String version_18 = "4.3";
        final String version_19 = "4.4";
        final String version_20 = "4.4W";
        final String version_21 = "5.0";
        final String version_22 = "5.1";
        final String version_23 = "6.0";
        String versionName = "";
        try {
            // android.os.Build.VERSION.SDK_INT Since: API Level 4
            // int version = android.os.Build.VERSION.SDK_INT;
            int version = Build.VERSION.SDK_INT;
            if (version >= 24) {
                return version_23;
            }
            switch (version) {
                case 3:
                    versionName = version_3;
                    break;
                case 4:
                    versionName = version_4;
                    break;
                case 5:
                    versionName = version_5;
                    break;
                case 6:
                    versionName = version_6;
                    break;
                case 7:
                    versionName = version_7;
                    break;
                case 8:
                    versionName = version_8;
                    break;
                case 9:
                    versionName = version_9;
                    break;
                case 10:
                    versionName = version_10;
                    break;
                case 11:
                    versionName = version_11;
                    break;
                case 12:
                    versionName = version_12;
                    break;
                case 13:
                    versionName = version_13;
                    break;
                case 14:
                    versionName = version_14;
                    break;
                case 15:
                    versionName = version_15;
                    break;
                case 16:
                    versionName = version_16;
                    break;
                case 17:
                    versionName = version_17;
                    break;
                case 18:
                    versionName = version_18;
                    break;
                case 19:
                    versionName = version_19;
                    break;
                case 20:
                    versionName = version_20;
                    break;
                case 21:
                    versionName = version_21;
                    break;
                case 22:
                    versionName = version_22;
                    break;
                case 23:
                    versionName = version_23;
                    break;
                default:
                    versionName = version_7;
            }
        } catch (Exception e) {
            versionName = version_7;
        }
        return versionName;
    }


    /**
     * 获取软件版本号 code
     *
     * @param ctx
     * @return int
     */
    public static int getVersionCode(Context ctx) {
        return getVersionCode(ctx, ctx.getPackageName());
    }


    /**
     * sd卡是否存在
     *
     * @return boolean
     */
    public static boolean isSdcardExist() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }


    /**
     * 获取软件版本号 code
     *
     * @param ctx
     * @param packageName
     * @return int
     */
    public static int getVersionCode(Context ctx, String packageName) {
        int versionCode = 0;
        try {
            PackageInfo packageinfo = ctx.getPackageManager().getPackageInfo(packageName, PackageManager.GET_INSTRUMENTATION);
            versionCode = packageinfo.versionCode;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return versionCode;
    }

    /**
     * 获取软件版本名称
     */
    public static String getVersionName(Context ctx) {
        return getVersionName(ctx, ctx.getPackageName());
    }

    /**
     * 获取versionName
     *
     * @param context
     * @param packageName
     * @return String
     */
    public static String getVersionName(Context context, String packageName) {
        String versionName = "";
        try {
            PackageInfo packageinfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            versionName = packageinfo.versionName;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return versionName;
    }

    public static int getNavigationBarHeight(Activity activity) {
        int height = 0;
        try {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    @SuppressLint("NewApi")
    public static boolean checkDeviceHasNavigationBar(Context activity) {
        try {
            boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (!hasMenuKey && !hasBackKey) {
                return true;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return false;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static boolean isLetvMoble() {
        try {
            return getManufacturer().equalsIgnoreCase("letv") || getManufacturer().equalsIgnoreCase("leeco") || getManufacturer().equalsIgnoreCase("lemobile");
        } catch (Exception var1) {
            return false;
        }
    }


    public static boolean isVivoPhone() {
        try {
            return getMachineName().toLowerCase().contains("vivo") || getManufacturer().toLowerCase().contains("vivo") || getManufacturer().equalsIgnoreCase("BBK");
        } catch (Exception var1) {
            return false;
        }
    }

    public static boolean isOppoPhone() {
        try {
            return getMachineName().toLowerCase().contains("oppo") || getManufacturer().toLowerCase().contains("oppo");
        } catch (Exception var1) {
            return false;
        }
    }

    public static boolean isGioneePhone() {
        try {
            return getManufacturer().toLowerCase().contains("gionee");
        } catch (Exception var1) {
            return false;
        }
    }


    public static String getMachineName() {
        return Build.MODEL;
    }


}
