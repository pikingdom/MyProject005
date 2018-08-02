package com.nd.hilauncherdev.plugin.navigation.widget.openpage;

import android.content.Context;
import android.util.Log;

import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;

/**
 * 零屏页码工具类
 * Created by linliangbin on 2017/10/10 14:27.
 */

public class PageCountSetter {

    private static PageCountSetter instance;
    /**
     * pageCount: 当前屏幕数，不包含子view中的屏幕数
     * childPageCount: 当前屏幕书，包含子view 中的屏幕书
     */
    private int pageCount, currentPageIndex;

    public int getChildPageCount() {
        return childPageCount;
    }

    private int childPageCount;

    public synchronized static PageCountSetter getInstance() {
        if (instance == null) {
            instance = new PageCountSetter();
        }
        return instance;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setAllPageCount(Context context, int pageCount) {
        Log.i("llbeing", "PageCountSetter:setAllPageCount:" + pageCount);
        try {
            ReflectInvoke.setNavigationViewChildCount(context, pageCount);
            this.childPageCount = pageCount;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void setPageCount(Context context, int pageCount) {

        Log.i("llbeing", "PageCountSetter:setPageCount:" + pageCount);
        try {
            this.pageCount = pageCount;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    public void setPageIndex(Context context, int pageIndex) {
        Log.i("llbeing", "PageCountSetter:setPageIndex:" + pageIndex);
        try {
            ReflectInvoke.setNavigationViewChildIndex(context, pageIndex);
            this.currentPageIndex = pageIndex;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
