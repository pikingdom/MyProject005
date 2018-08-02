package com.nd.hilauncherdev.plugin.navigation.kit.advert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.db.NavigationDataBase;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.Global;

import java.util.ArrayList;
import java.util.List;

/**
 * 广告内容数据库Helper
 * Created by Administrator on 2016/8/8.
 */
public class AdDBHelper {

    private static final String TAG = "AdDBHelper";
    private static AdDBHelper dbHelper;
    private Context context;

    private AdDBHelper() {
    }

    public static AdDBHelper getInstance() {
        if (dbHelper == null) {
            dbHelper = new AdDBHelper();
            dbHelper.context = Global.context;
        }
        return dbHelper;
    }

    public synchronized void createAdTabel() {
        Log.d(TAG, "createAdTabel: ");
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            db.execSQL(AdvertDataBaseWrapper.CREATE_AD_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 广告本地化
     *
     * @param key          广告KEY值
     * @param pos          广告位
     * @param content      广告的序列化后内容
     * @param showCount    可展示的次数
     * @param showInterval 展示间隔时间
     * @param currCount    当前展示总数
     */
    public synchronized void insert(String key, int pos, String content, int showCount, int showInterval, int currCount, long lastTime, AdState state) {
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            ContentValues cv = new ContentValues();
            cv.put(AdvertDataBaseWrapper.P_KEY, key);
            cv.put(AdvertDataBaseWrapper.P_POS, pos);
            cv.put(AdvertDataBaseWrapper.P_CONTENT, TextUtils.isEmpty(content) ? "" : content);
            cv.put(AdvertDataBaseWrapper.P_SHOWCOUNT, showCount);
            cv.put(AdvertDataBaseWrapper.P_SHOWINTERVAL, showInterval);
            cv.put(AdvertDataBaseWrapper.P_LAST_SHOWTIME, lastTime);
            cv.put(AdvertDataBaseWrapper.P_SHOWABLE, state.flag);
            long res = db.insertOrThrow(AdvertDataBaseWrapper.TABLE_NAME, null, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 更新某条广告的已展示次数
     *
     * @param key       广告KEY值
     * @param currCount 当前已展示次数
     */
    public synchronized void updateCount(String key, int currCount, long lastTime, AdState state) {
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            ContentValues cv = new ContentValues();
            cv.put(AdvertDataBaseWrapper.P_LAST_SHOWTIME, lastTime);
            cv.put(AdvertDataBaseWrapper.P_SHOWABLE, state.flag);
            int res = db.update(AdvertDataBaseWrapper.TABLE_NAME, cv, AdvertDataBaseWrapper.P_KEY + " = ?", new String[]{key + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 更新广告内容
     *
     * @param key          广告KEY
     * @param content      广告内容
     * @param showCount    可展示次数
     * @param showInterval 展示间隔时间
     * @param currCount    当前已展示次数
     */
    public synchronized void update(String key, String content, int showCount, int showInterval, int currCount, long lastTime, AdState state) {
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            ContentValues cv = new ContentValues();
            cv.put(AdvertDataBaseWrapper.P_CONTENT, TextUtils.isEmpty(content) ? "" : content);
            cv.put(AdvertDataBaseWrapper.P_SHOWCOUNT, showCount);
            cv.put(AdvertDataBaseWrapper.P_SHOWINTERVAL, showInterval);
            cv.put(AdvertDataBaseWrapper.P_LAST_SHOWTIME, lastTime);
            cv.put(AdvertDataBaseWrapper.P_SHOWABLE, state.flag);
            int res = db.update(AdvertDataBaseWrapper.TABLE_NAME, cv, AdvertDataBaseWrapper.P_KEY + " = ?", new String[]{key + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<SimpleAdInfo> query() {
        NavigationDataBase db = null;
        Cursor cursor = null;
        try {
            db = NavigationDataBase.getInstance(context);
            cursor = db.query(AdvertDataBaseWrapper.TABLE_NAME, null, null, null, null, null, null);
            return parseToSimpleInfo(cursor);
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
        return null;
    }

    public List<SimpleAdInfo> query(int pos, AdState... state) {
        if (state == null) return query(pos);

        NavigationDataBase db = null;
        Cursor cursor = null;
        try {
            db = NavigationDataBase.getInstance(context);
            StringBuffer selection = new StringBuffer(AdvertDataBaseWrapper.P_POS + " = ? AND " + AdvertDataBaseWrapper.P_SHOWABLE + " IN (");
            String[] selectionArgs = new String[state.length + 1];
            selectionArgs[0] = pos + "";
            for (int i = 0, len = state.length; i < len; i++) {
                selection.append("?,");
                selectionArgs[i + 1] = state[i].flag + "";
            }
            selection.deleteCharAt(selection.length() - 1);
            selection.append(")");

            Log.e(TAG, "query: " + selection);

            cursor = db.query(AdvertDataBaseWrapper.TABLE_NAME,
                    null, selection.toString(), selectionArgs, null, null, null);
            return parseToSimpleInfo(cursor);
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
        return null;
    }

    public SimpleAdInfo query(String key) {
        NavigationDataBase db = null;
        Cursor cursor = null;
        try {
            db = NavigationDataBase.getInstance(context);
            cursor = db.query(AdvertDataBaseWrapper.TABLE_NAME, null, AdvertDataBaseWrapper.P_KEY + " = ?", new String[]{key}, null, null, null);
            List<SimpleAdInfo> infos = parseToSimpleInfo(cursor);
            return (infos == null || infos.isEmpty()) ? null : infos.get(0);
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
        return null;
    }

    /**
     * 查找某个广告位的本地广告内容
     *
     * @param pos
     * @return
     */
    public List<SimpleAdInfo> query(int pos) {
        NavigationDataBase db = null;
        Cursor cursor = null;
        try {
            db = NavigationDataBase.getInstance(context);
            cursor = db.query(AdvertDataBaseWrapper.TABLE_NAME,
                    null, AdvertDataBaseWrapper.P_POS + " = ?", new String[]{pos + ""}, null, null, null);
            return parseToSimpleInfo(cursor);
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
        return null;
    }

    /**
     * 重置所有广告位（即对表进行清空）
     *
     * @return
     */
    public synchronized boolean restore() {
        Log.e(TAG, "restore: ");
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            boolean effective = db.delete(AdvertDataBaseWrapper.TABLE_NAME, null, null);
            return effective;
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
     * 重置指定广告位（即删除对应广告位置记录）
     *
     * @param pos
     * @return
     */
    public synchronized boolean restore(int pos) {
        NavigationDataBase db = null;
        try {
            db = NavigationDataBase.getInstance(context);
            boolean res = db.delete(AdvertDataBaseWrapper.TABLE_NAME, AdvertDataBaseWrapper.P_POS + "=?", new String[]{pos + ""});
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return false;
    }

    private List<SimpleAdInfo> parseToSimpleInfo(Cursor cursor) {
        if (cursor != null) {
            List<SimpleAdInfo> infolist = new ArrayList<SimpleAdInfo>();
            while (cursor.moveToNext()) {
                SimpleAdInfo info = new SimpleAdInfo();
                info.key = cursor.getString(cursor.getColumnIndex(AdvertDataBaseWrapper.P_KEY));
                info.pos = cursor.getInt(cursor.getColumnIndex(AdvertDataBaseWrapper.P_POS));
                info.content = cursor.getString(cursor.getColumnIndex(AdvertDataBaseWrapper.P_CONTENT));
                info.showCount = cursor.getInt(cursor.getColumnIndex(AdvertDataBaseWrapper.P_SHOWCOUNT));
                info.showInterval = cursor.getInt(cursor.getColumnIndex(AdvertDataBaseWrapper.P_SHOWINTERVAL));
                info.lastTime = cursor.getLong(cursor.getColumnIndex(AdvertDataBaseWrapper.P_LAST_SHOWTIME));
                info.state = getState(cursor.getInt(cursor.getColumnIndex(AdvertDataBaseWrapper.P_SHOWABLE)));
                infolist.add(info);
            }
            return infolist;
        }
        return null;
    }

    private AdState getState(int val) {
        return AdState.getStateByFlag(val);
    }

    public static class SimpleAdInfo {
        public String content;
        public String key;
        public int pos;
        public int showCount;
        public int showInterval;
        public int currentCount;
        public long lastTime;
        public AdState state = AdState.OTHER;

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!(o instanceof SimpleAdInfo)) return false;

            SimpleAdInfo info = (SimpleAdInfo) o;
            if (!TextUtils.isEmpty(key) && key.equals(info.key)) {
                return true;
            }
            return false;
        }

        /**
         * @desc 该广告是否已经可展示
         * 相同广告播放间隔一天
         * @author linliangbin
         * @time 2017/9/8 12:38
         */
        public boolean canShowNow() {
            if (System.currentTimeMillis() - lastTime >= DateUtil.DAY * 1) {
                return true;
            }
            return false;
        }
    }
}
