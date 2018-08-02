package com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item;

import com.nd.hilauncherdev.plugin.navigation.bean.Book;

import java.util.List;

/**
 * Created by linliangbin on 2017/8/24 14:58.
 */

public interface IBookPageShelf {

    /**
     * @desc 获取推荐数据成功的回调
     * @author linliangbin
     * @time 2017/8/24 14:58
     */
    public void onReadHistoryLoaded(List<Book> books);

    public void hide();

    public void show();

    /**
     * @desc 书架界面更新
     * @author linliangbin
     * @time 2017/8/28 15:28
     */
    public void doShelfUpdate();
}
