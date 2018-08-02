package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 横向滚动viewGroup 内部的viewGroup
 */
public class MyPhoneViewPagerInner extends MyPhoneViewPager {

    public MyPhoneViewPagerInner(Context context) {
        super(context);
    }

    public MyPhoneViewPagerInner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_CANCEL:
                snapToDestination();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * @desc 处理内部嵌套滑动事件
     * @author linliangbin
     * @time 2017/9/15 17:01
     */
    public boolean handleInnerTouch(MotionEvent event) {
        return false;
    }


}
