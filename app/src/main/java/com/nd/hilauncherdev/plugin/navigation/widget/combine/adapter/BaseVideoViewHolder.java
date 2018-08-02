package com.nd.hilauncherdev.plugin.navigation.widget.combine.adapter;

import android.graphics.Rect;
import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.items.ListItem;

/**
 * Created by linliangbin on 2017/5/15 15:11.
 */

public class BaseVideoViewHolder implements ListItem {

    private final Rect mCurrentViewRect = new Rect();

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }

    private void setVisibilityPercentsText(View currentView, int percents) {
    }


    @Override
    public int getVisibilityPercents(View currentView) {

        int percents = 100;
        currentView.getLocalVisibleRect(mCurrentViewRect);
        int height = currentView.getHeight();
        if (viewIsPartiallyHiddenTop()) {
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if (viewIsPartiallyHiddenBottom(height)) {
            percents = mCurrentViewRect.bottom * 100 / height;
        }
        setVisibilityPercentsText(currentView, percents);
        return percents;
    }

    @Override
    public void setActive(View newActiveView, int newActiveViewPosition) {
    }

    @Override
    public void deactivate(View currentView, int position) {
    }

}
