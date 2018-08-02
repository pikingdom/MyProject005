package com.nd.hilauncherdev.plugin.navigation.widget.book;

import android.content.Context;
import android.util.AttributeSet;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linliangbin on 2017/8/30 15:29.
 */

public class BookpageItemView extends BookItemView {

    public BookpageItemView(Context context) {
        super(context);
    }

    public BookpageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.bookpage_shelf_item;
    }
}
