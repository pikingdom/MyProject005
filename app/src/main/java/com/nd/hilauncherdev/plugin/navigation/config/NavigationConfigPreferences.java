package com.nd.hilauncherdev.plugin.navigation.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;

/**
 * 零屏SP配置工具类
 * Created by linliangbin on 2017/9/4 13:42.
 */

public class NavigationConfigPreferences {


    private static final String SP_NAME = "navigation_config_sp";

    /**
     * 阅读屏-重置页码时间
     */
    private static final String KEY_BOOKPAGE_RESET_INDEX_TIME = "key_bookpage_reset_index";
    /**
     * 阅读屏页码
     */
    private static final String KEY_BOOKPAGE_PAGE_INDEX = "key_bookpage_page_index";

    private static NavigationConfigPreferences ap;
    private static SharedPreferences sp;
    private Context mContext;


    protected NavigationConfigPreferences(Context ctx) {
        super();
        mContext = ctx;
    }

    public synchronized static NavigationConfigPreferences getInstance(Context ctx) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }

        if (ap == null) {
            ap = new NavigationConfigPreferences(ctx);
        }

        return ap;
    }

    /**
     * @desc 获取和设置阅读屏页码
     * @author linliangbin
     * @time 2017/9/4 13:47
     */
    public int getBookpageIndex() {
        return sp.getInt(KEY_BOOKPAGE_PAGE_INDEX, 40);
    }

    public void resetBookpageIndex() {
        setBookPageIndex(1);
    }

    public void setBookPageIndex(int bookPageIndex) {
        sp.edit().putInt(KEY_BOOKPAGE_PAGE_INDEX, bookPageIndex).commit();
    }

    public void increateBookpageIndex(){
        int newIndex = getBookpageIndex() + 1;
        setBookPageIndex(newIndex);
    }

    public void decreaseBookpageIndex(){
        int newIndex = getBookpageIndex() - 1;
        if(newIndex <= 1){
            newIndex = 1;
        }
        setBookPageIndex(newIndex);
    }

    /**
     * @desc
     * @author linliangbin
     * @time 2017/9/4 13:52
     */
    public long getBookpageResetIndxTime() {
        return sp.getLong(KEY_BOOKPAGE_RESET_INDEX_TIME, 0L);
    }


    public void setBookpageResetIndxTime() {
        sp.edit().putLong(KEY_BOOKPAGE_RESET_INDEX_TIME, System.currentTimeMillis()).commit();
    }

    public boolean needResetBookpageIndex(){
        if(System.currentTimeMillis() - getBookpageResetIndxTime() >= DateUtil.DAY){
            return true;
        }
        if(getBookpageIndex() >= 50){
            return true;
        }
        return false;
    }


}
