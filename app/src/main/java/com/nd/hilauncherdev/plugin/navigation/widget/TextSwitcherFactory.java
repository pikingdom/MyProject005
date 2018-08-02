package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TextSwitcherFactory implements android.widget.ViewSwitcher.ViewFactory {

    Context mContext;
    int textColor = -1;
    private TextView textView;
    public TextSwitcherFactory(Context context) {
        mContext = context;
    }

    public TextSwitcherFactory(Context context, int color) {
        mContext = context;
        textColor = color;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public View makeView() {

        TextView tv = new TextView(mContext);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);
        if (textColor != -1) {
            tv.setTextColor(textColor);
        } else {
            tv.setTextColor(Color.parseColor("#99ffffff"));
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(lp);
        return tv;

    }

}
