package com.nd.hilauncherdev.plugin.navigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static com.nd.hilauncherdev.plugin.navigation.kit.advert.AdvertDataBaseWrapper.CREATE_AD_TABLE;


/**
 * 桌面配置数据库
 */
public class NavigationDataBase extends AbstractDataBase {
    public static final String DB_NAME = "navigation.db";
    public static final int DB_VERSION = 1;
    private static NavigationDataBase mConfigDataBase;

    private NavigationDataBase(Context c) {
        super(c, DB_NAME, DB_VERSION);
    }

    public static NavigationDataBase getInstance(Context c) {
        if (mConfigDataBase == null) {
            mConfigDataBase = new NavigationDataBase(c.getApplicationContext());
        }
        return mConfigDataBase;
    }

    @Override
    public void onDataBaseCreate(SQLiteDatabase db) {
        // config表
        db.execSQL(CREATE_AD_TABLE);
    }

    @Override
    public void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
