package com.nd.hilauncherdev.plugin.navigation.kit.advert;

/**
 * 广告SDK-广告内容数据库
 * Created by Administrator on 2016/8/8.
 */
public class AdvertDataBaseWrapper {

    public final static String TABLE_NAME = "NavigationAdShow";

    public final static String P_ID = "_id";
    public final static String P_KEY = "adkey";
    public final static String P_POS = "adpos";
    public final static String P_CONTENT = "content";
    public final static String P_SHOWCOUNT = "showcount";
    public final static String P_SHOWINTERVAL = "showinterval";
    public final static String P_LAST_SHOWTIME = "lasttime";
    public final static String P_SHOWABLE = "adshowable";

    public final static String CREATE_AD_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "("
            + P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + P_KEY + " TEXT NOT NULL,"
            + P_POS + " INTEGER DEFAULT -1,"
            + P_CONTENT + " TEXT,"
            + P_SHOWCOUNT + " INTEGER DEFAULT 0,"
            + P_SHOWINTERVAL + " INTEGER DEFAULT 0,"
            + P_LAST_SHOWTIME + " INTEGER DEFAULT 0,"
            + P_SHOWABLE + " INTEGER DEFAULT 0"
            + ");";
}
