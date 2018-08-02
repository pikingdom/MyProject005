package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 无滚动的GridView
 * Created by chenxuyu_dian91 on 2016/2/18.
 */
public class NotScrollGridView extends GridView {

    public NotScrollGridView(Context context) {
        super(context);
    }

    public NotScrollGridView(Context context, AttributeSet attrs,
                             int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
