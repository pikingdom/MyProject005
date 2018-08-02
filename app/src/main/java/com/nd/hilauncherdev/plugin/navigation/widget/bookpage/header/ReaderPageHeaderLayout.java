package com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item.BookPageShelfLayout;

/**
 * 掌阅阅读屏-listview header布局
 * Created by linliangbin on 2017/8/21 15:43.
 */

public class ReaderPageHeaderLayout extends LinearLayout {


    BookPageShelfLayout bookPageShelfLayout;

    public ReaderPageHeaderLayout(Context context) {
        super(context);
        init();
    }

    public ReaderPageHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bookpage_header_layout, this);
        bookPageShelfLayout = (BookPageShelfLayout) this.findViewById(R.id.view_bookpage_shelf);
    }

    public void doShelfUpdate() {
        if (bookPageShelfLayout != null) {
            bookPageShelfLayout.doShelfUpdate();
        }
    }

    public void removeSearchHeader() {
        View view = findViewById(R.id.layout_bookpage_title_layout);
        if (view != null) {
            view.setVisibility(GONE);
        }
    }
}
