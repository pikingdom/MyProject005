package com.nd.hilauncherdev.plugin.navigation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

    //淘宝购物屏单品-缓存请求时间间隔
    public static final String TAOBAO_CACHETIME = "taobao_cachetime";
    //淘宝购物屏头条-缓存请求时间间隔
    public static final String TAOBAO_HEADLINE_CACHETIME = "taobao_headline_cachetime";
    //淘宝购物屏CPS-缓存请求时间间隔
    public static final String TAOBAO_CPS_CACHETIME = "taobao_cps_cachetime";
    //淘宝购物屏-卡片配置
    public static final String TAOBAO_CARDSERIES = "taobao_series";

    public static final String TAOBAO_CARDSERIES_CACHETIME = "taobao_series_cachetime";
    //淘宝购物屏banner-缓存请求时间间隔
    public static final String TAOBAO_TOPBANNER_CACHETIME = "taobao_topbanner_cachetime";

    public static final String TAOBAO_HEADLINE = "taobao_headline";
    public static final String TAOBAO_CATE = "taobao_cate";
    public static final String TAOBAO_CPS = "taobao_cps";

    private SharedPreferences sp;
    private Editor editor;
    private final static String SP_NAME = "SP_ShareData";

    public SharedPreferencesUtil(Context context) {
        sp = context.getSharedPreferences(SP_NAME, 0);
        editor = sp.edit();
    }

    public boolean putString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }

    public String getString(String key) {
        String str = null;
        str = sp.getString(key, null);
        return str;
    }

    public String getString(String key, String def) {
        String str = def;
        str = sp.getString(key, def);
        return str;
    }

    public boolean putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public boolean getBoolean(String key) {
        boolean res = false;
        res = sp.getBoolean(key, false);
        return res;
    }

    public boolean putInt(String key, int value) {
        editor.putInt(key, value);
        return editor.commit();
    }

    public boolean putLong(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }

    public int getInt(String key, int def) {
        int str = -1;
        str = sp.getInt(key, def);
        return str;
    }

    public boolean putFloat(String key, float value) {
        editor.putFloat(key, value);
        return editor.commit();
    }

    public float getFloat(String key, float def) {
        float str = -1f;
        str = sp.getFloat(key, def);
        return str;
    }

    public long getLong(String key, long def) {
        long str = -1;
        str = sp.getLong(key, def);
        return str;
    }


    //默认
    public boolean getBoolean(String key, boolean def) {
        boolean res = def;
        res = sp.getBoolean(key, def);
        return res;
    }

    public boolean contains(String key) {
        return sp.contains(key);
    }
}
