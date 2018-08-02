package com.nd.hilauncherdev.plugin.navigation.widget.visiblelist.calculator;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by linliangbin on 2017/5/15 17:38.
 */

public class VisibleCaculator {

    private static VisibleCaculator visibleCaculator;
    private final Rect mCurrentViewRect = new Rect();


    private VisibleCaculator() {

    }

    public synchronized static VisibleCaculator getInstance() {
        if (visibleCaculator == null) {
            visibleCaculator = new VisibleCaculator();
        }
        return visibleCaculator;
    }

    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }

    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }

    public int getViewVisiblePercent(View currentView) {

        if(currentView == null){
            return 0;
        }
        int percents = 100;

        currentView.getLocalVisibleRect(mCurrentViewRect);

        int height = currentView.getHeight();
        if (viewIsPartiallyHiddenTop()) {
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if (viewIsPartiallyHiddenBottom(height)) {
            percents = mCurrentViewRect.bottom * 100 / height;
        }

        return percents;
    }
}
