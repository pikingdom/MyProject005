package com.nd.hilauncherdev.plugin.navigation.widget.bookpage;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.config.NavigationConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.kit.AssetFileCache;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.net.NetOpi;
import com.nd.hilauncherdev.plugin.navigation.util.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linliangbin on 2017/8/24 17:07.
 */

public class IReaderListModel {

    public static final int PAGE_SIZE = 20;
    public static final String CACHE_FILE_NAME = "bookpage_recommend_data_cache";

    /**
     * @desc 获取推荐分页数据
     * @author linliangbin
     * @time 2017/8/24 17:37
     */
    public static ArrayList<Book> loadBookPage(Context context, int pageIndex) {

        ServerResultHeader resultHeader = NetOpi.loadRecommenFromNet(context, pageIndex, pageIndex, PAGE_SIZE);
        String data = "";
        if (resultHeader != null) {
            if (resultHeader.getResultCode() == 996) {
                NavigationConfigPreferences.getInstance(context).resetBookpageIndex();
            }
            data = resultHeader.getResponseJson();
        }
        ArrayList<Book> bookList = BookShelfLoader.parseBookList(data);
        if (EmptyUtils.isNotEmpty(bookList)) {
            writeFileCache(context, data);
        }
        return bookList;

    }

    public static List<Book> readFileCacheData(Context context) {

        String data = AssetFileCache.getInstance(context).readByFile(CACHE_FILE_NAME);
        List<Book> list = new ArrayList<Book>();
        if (TextUtils.isEmpty(data)) {
            return list;
        }

        list = BookShelfLoader.parseBookList(data);
        return list;
    }

    private static void writeFileCache(Context context, String data) {
        AssetFileCache.getInstance(context).writeToFile(CACHE_FILE_NAME, data);
    }


}
