package com.nd.hilauncherdev.plugin.navigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;


/**
 * description: 购物屏单品数据库<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/22<br/>
 */
public class TaobaoProductDB extends SQLiteOpenHelper {

    /**
     * 数据库版本
     */
    static final int DB_VERSION = 3;

    public static final String TABLE_NAME = "taobao_goods";

    public TaobaoProductDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaobaoProduct.ID + " VARCHAR, " +
                TaobaoProduct.CATEGORY + " INTEGER, " +
                TaobaoProduct.PIC + " VARCHAR NOT NULL, " +
                TaobaoProduct.TITLE + " VARCHAR NOT NULL, " +
                TaobaoProduct.PRICE + " REAL, " +
                TaobaoProduct.PROMOPRICE + " REAL, " +
                TaobaoProduct.URL + " VARCHAR , " +

                TaobaoProduct.DISCOUNT_DES + " VARCHAR, " +
                TaobaoProduct.ACTIVITY_DESC + " VARCHAR, " +
                TaobaoProduct.ON_SELF_TIME + " VARCHAR, " +
                TaobaoProduct.ACTIVITY_TYPE + " INTEGER, " +
                TaobaoProduct.PRODUCT_COUNT + " INTEGER, " +
                TaobaoProduct.SOURCE_ID + " INTEGER, " +
                TaobaoProduct.RES_ID + " INTEGER, " +
                TaobaoProduct.RES_TYPE + " INTEGER" +
                ")"
        );
    }

    private static final String TAG = "TaobaoProductDB";

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: old version: " + oldVersion + " new version: " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
