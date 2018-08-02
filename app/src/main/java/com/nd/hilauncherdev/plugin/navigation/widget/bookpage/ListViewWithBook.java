package com.nd.hilauncherdev.plugin.navigation.widget.bookpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.widget.PullToRefreshListView;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.ReaderPageHeaderLayout;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item.BookPageListHeaderTitle;

/**
 * 阅读屏的listview
 * 需要单独增加header
 * Created by linliangbin on 2017/8/24 18:53.
 */

public class ListViewWithBook extends PullToRefreshListView {

    View frontHeader;
    View rearHeader;

    public ListViewWithBook(Context context) {
        super(context);
    }

    public ListViewWithBook(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initPullHeader() {
        mHeadView = new BookPullToRefreshHeader(mContext);
    }

    @Override
    public void addFrontHeader() {
        super.addFrontHeader();
        frontHeader = new ReaderPageHeaderLayout(getContext());
        addHeaderView(frontHeader);
    }

    public void removeSearchHeader() {
        if (frontHeader != null && frontHeader instanceof ReaderPageHeaderLayout) {
            ((ReaderPageHeaderLayout) frontHeader).removeSearchHeader();
        }
    }

    @Override
    public void addRearHeader() {
        rearHeader = new BookPageListHeaderTitle(getContext());
        addHeaderView(rearHeader);
    }

    public void doShelfUpdate() {
        if (frontHeader != null) {
            ((ReaderPageHeaderLayout) frontHeader).doShelfUpdate();
        }
    }
}
