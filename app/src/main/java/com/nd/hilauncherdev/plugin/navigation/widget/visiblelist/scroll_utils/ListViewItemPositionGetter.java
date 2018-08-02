package com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.scroll_utils;

import android.view.View;
import android.widget.ListView;

/**
 * This class is an API for {@link com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator}
 * Using this class is can access all the data from ListView
 * <p>
 * Created by danylo.volokh on 1/17/2016.
 */
public class ListViewItemPositionGetter {

    private final ListView mListView;

    public ListViewItemPositionGetter(ListView listView) {
        mListView = listView;
    }


    public View getChildAt(int position) {
        return mListView.getChildAt(position);
    }

}
