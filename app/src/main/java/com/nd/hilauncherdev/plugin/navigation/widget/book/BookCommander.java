package com.nd.hilauncherdev.plugin.navigation.widget.book;

import android.content.Intent;

import com.nd.hilauncherdev.plugin.navigation.util.Global;

/**
 * 打开阅读界面的接口工具类
 * Created by linliangbin on 2017/8/7 18:38.
 */

public class BookCommander {

    //默认-1.0版本支持
    public static final int ACTION_NONE = -1;

    //主页-1.0版本支持
    public static final int ACTION_MAIN = 100;

    //书架-1.0版本支持
    public static final int ACTION_SHELF = 101;

    //阅读书籍-1.0版本支持
    public static final int ACTION_READ = 102;

    //详情-1.0版本支持
    public static final int ACTION_DETAIL = 103;

    //打开搜索界面-2.0版本支持
    public static final int ACTION_SEARCH = 104;

    //分类界面-2.0
    public static final int ACTION_CATEGORY = 105;

    //排行界面-2.0
    public static final int ACTION_RANK = 106;

    //免费界面-2.0
    public static final int ACTION_FREE = 107;

    //漫画界面-2.0
    public static final int ACTION_COMIC = 108;


    public static final String INTENT_ACTION = "ACTION";
    public static final String INTENT_EXTRA = "EXTRA";


    public static Intent getActionIntent(int action) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_ACTION, action);
        intent.setClassName(Global.getPackageName(), "com.nd.hilauncherdev.readercenter.ReaderCenterActivity");
        return intent;
    }

    public static Intent getMainIntent() {
        Intent intent = getActionIntent(ACTION_MAIN);
        return intent;
    }


    public static Intent getShelfIntent() {
        Intent intent = getActionIntent(ACTION_SHELF);
        return intent;
    }

    public static Intent getReadIntent(String bookId) {
        Intent intent = getActionIntent(ACTION_READ);
        intent.putExtra(INTENT_EXTRA, bookId);
        return intent;
    }

    public static Intent getDetailIntent(String bookId) {
        Intent intent = getActionIntent(ACTION_DETAIL);
        intent.putExtra(INTENT_EXTRA, bookId);
        return intent;
    }

    public static Intent getBookSearchIntent() {
        Intent intent = getActionIntent(ACTION_SEARCH);
        return intent;
    }

    public static Intent getCategoryIntent() {
        Intent intent = getActionIntent(ACTION_CATEGORY);
        return intent;
    }

    public static Intent getRankIntent() {
        Intent intent = getActionIntent(ACTION_RANK);
        return intent;
    }

    public static Intent getFreeIntent() {
        Intent intent = getActionIntent(ACTION_FREE);
        return intent;
    }

    public static Intent getComicIntent() {
        Intent intent = getActionIntent(ACTION_COMIC);
        return intent;
    }


}
