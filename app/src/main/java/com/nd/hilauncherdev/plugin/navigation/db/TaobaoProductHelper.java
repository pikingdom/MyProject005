package com.nd.hilauncherdev.plugin.navigation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;

import java.util.ArrayList;
import java.util.List;

import static com.nd.hilauncherdev.plugin.navigation.db.TaobaoProductDB.DB_VERSION;

/**
 * description: 数据CURD工具类<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/22<br/>
 */
public class TaobaoProductHelper {

    /**
     * 数据库名称
     */
    private static final String DB_NAME = "taobaoapi.db";

    private TaobaoProductDB dbHelper;

    private static final String SELECT_ALL = "select * from " + TaobaoProductDB.TABLE_NAME;
    private static final String DELETE_ALL = "delete from " + TaobaoProductDB.TABLE_NAME;

    private static final String SELECT_COUNT = "select count(*) from " + TaobaoProductDB.TABLE_NAME;

    public TaobaoProductHelper(Context context) {
        dbHelper = new TaobaoProductDB(context, DB_NAME, null, DB_VERSION);
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * 查询总条数
     *
     * @return
     */
    public int selectCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = -1;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(SELECT_COUNT, null);
            while (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return count;
    }

    /**
     * 删除所有
     */
    public boolean deleteAll() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            return 0 != db.delete(TaobaoProductDB.TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 添加
     *
     * @param goods
     * @return
     */
    public boolean add(TaobaoProduct goods) {
        if (goods == null) return false;
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put(TaobaoProduct.ID, goods.promoter);
            data.put(TaobaoProduct.TITLE, goods.title);
            data.put(TaobaoProduct.CATEGORY, goods.category);
            data.put(TaobaoProduct.PIC, goods.imageUrl);
            data.put(TaobaoProduct.PRICE, goods.price);
            data.put(TaobaoProduct.PROMOPRICE, goods.promoprice);
            data.put(TaobaoProduct.URL, goods.urlClick);

            data.put(TaobaoProduct.ON_SELF_TIME, goods.onShelfTime);
            data.put(TaobaoProduct.DISCOUNT_DES, goods.discountDes);
            data.put(TaobaoProduct.ACTIVITY_TYPE, goods.activityType);
            data.put(TaobaoProduct.ACTIVITY_DESC, goods.activityDesc);
            data.put(TaobaoProduct.PRODUCT_COUNT, goods.productCount);
            data.put(TaobaoProduct.SOURCE_ID, goods.sourceId);
            data.put(TaobaoProduct.RES_ID, goods.resId);
            data.put(TaobaoProduct.RES_TYPE, goods.resType);

            return db.insert(TaobaoProductDB.TABLE_NAME, null, data) != -1;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<TaobaoProduct> selectAll() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<TaobaoProduct> list = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(SELECT_ALL, null);
            while (cursor.moveToNext()) {
                TaobaoProduct goods = getTaobaoProduct(cursor);
                list.add(goods);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

    private TaobaoProduct getTaobaoProduct(Cursor cursor) {
        TaobaoProduct goods = new TaobaoProduct();
        goods.promoter = cursor.getString(cursor.getColumnIndex(TaobaoProduct.ID));
        goods.title = cursor.getString(cursor.getColumnIndex(TaobaoProduct.TITLE));
        goods.imageUrl = cursor.getString(cursor.getColumnIndex(TaobaoProduct.PIC));
        goods.price = cursor.getFloat(cursor.getColumnIndex(TaobaoProduct.PRICE));
        goods.promoprice = cursor.getFloat(cursor.getColumnIndex(TaobaoProduct.PROMOPRICE));
        goods.urlClick = cursor.getString(cursor.getColumnIndex(TaobaoProduct.URL));
        goods.category = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.CATEGORY));

        goods.onShelfTime = cursor.getString(cursor.getColumnIndex(TaobaoProduct.ON_SELF_TIME));
        goods.discountDes = cursor.getString(cursor.getColumnIndex(TaobaoProduct.DISCOUNT_DES));
        goods.activityType = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.ACTIVITY_TYPE));
        goods.activityDesc = cursor.getString(cursor.getColumnIndex(TaobaoProduct.ACTIVITY_DESC));
        goods.productCount = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.PRODUCT_COUNT));
        goods.sourceId = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.SOURCE_ID));
        goods.resId = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.RES_ID));
        goods.resType = cursor.getInt(cursor.getColumnIndex(TaobaoProduct.RES_TYPE));
        return goods;
    }

    /**
     * 分页查询
     *
     * @param start
     * @param size
     * @param distinctList 排除promoter相同项
     * @param isDistinct   是否唯一性
     * @return
     */
    public List<TaobaoProduct> select(int start, int size, List<TaobaoProduct> distinctList, boolean isDistinct) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<TaobaoProduct> list = new ArrayList<>();
        try {
            db = dbHelper.getReadableDatabase();
            StringBuffer where = null;
            if (distinctList != null && distinctList.size() > 0) {
                where = new StringBuffer(" where " + TaobaoProduct.ID + " not in (");
                for (TaobaoProduct item : distinctList) {
                    where.append("'" + item.promoter + "'");
                    where.append(",");
                }
                where.deleteCharAt(where.length() - 1);
                where.append(")");
            }
            String sql = "select " + (isDistinct ? "distinct" : "") + " * from " + TaobaoProductDB.TABLE_NAME + (where == null ? "" : where.toString()) + " limit ?,?";
            cursor = db.rawQuery(sql, new String[]{start + "", size + ""});
            while (cursor.moveToNext()) {
                TaobaoProduct goods = getTaobaoProduct(cursor);
                list.add(goods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return list;
    }

}
