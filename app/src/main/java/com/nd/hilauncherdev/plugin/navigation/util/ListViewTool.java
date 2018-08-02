package com.nd.hilauncherdev.plugin.navigation.util;

import android.widget.ListView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linliangbin on 2017/9/22 15:28.
 */

public class ListViewTool {

    /**
     * @desc 平滑滚动到列表头部
     * @author linliangbin
     * @time 2017/9/22 15:30
     */
    public static void scroolToHeader(final ListView listView) {
        if (listView == null) {
            return;
        }
        if (TelephoneUtil.getApiLevel() > 11) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPositionFromTop(listView.getHeaderViewsCount(),
                            listView.getContext().getResources().getDimensionPixelOffset(R.dimen.search_layout_input_box_height), 300);
                }
            });
        }
    }


    /**
     * @desc 平滑滚动到列表头部
     * @author linliangbin
     * @time 2017/9/22 15:30
     */
    public static void scroolToTop(final ListView listView) {
        if (listView == null) {
            return;
        }
        if (TelephoneUtil.getApiLevel() > 11) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPositionFromTop(0, 300);
                }
            });
        }
    }


}
