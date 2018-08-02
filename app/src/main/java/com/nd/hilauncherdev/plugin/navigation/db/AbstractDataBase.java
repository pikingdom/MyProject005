package com.nd.hilauncherdev.plugin.navigation.db;

import android.content.Context;

/**
 * 数据库
 */
public abstract class AbstractDataBase extends AbstractDataBaseSuper {

    //数据库前缀，用于定制版需求
    private static final String DATABASE_PRE = "";

    public AbstractDataBase(Context c, String dbName, int version) {
        super(c, DATABASE_PRE + dbName, version);
    }
}
