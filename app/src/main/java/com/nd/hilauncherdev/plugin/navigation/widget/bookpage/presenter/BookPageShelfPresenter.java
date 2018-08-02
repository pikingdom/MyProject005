package com.nd.hilauncherdev.plugin.navigation.widget.bookpage.presenter;

import android.content.Context;

import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item.IBookPageShelf;

import org.json.JSONArray;

import java.util.List;

/**
 * 掌阅阅读屏-书架界面
 * Created by linliangbin on 2017/8/24 14:56.
 */

public class BookPageShelfPresenter {

    private IBookPageShelf iView;

    public BookPageShelfPresenter(IBookPageShelf iBookPageShelf) {
        this.iView = iBookPageShelf;
    }

    /**
     * @desc 获取本地书架数据
     * @author linliangbin
     * @time 2017/8/24 15:10
     */
    public void loadShelfData(Context context) {

        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                List<Book> bookList = null;
                try {
                    bookList = BookShelfLoader.parseBookList(new JSONArray(NavigationKeepForReflect.getLocalReadHistory()));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                final List<Book> finalBookList = bookList;
                Global.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iView != null) {
                            iView.onReadHistoryLoaded(finalBookList);
                        }
                    }
                });
            }
        });

    }

}
