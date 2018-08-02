package com.nd.hilauncherdev.plugin.navigation.widget.book;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * @Description: </br>
 * @author: cxy </br>
 * @date: 2017年05月03日 21:20.</br>
 * @update: </br>
 */

public class ScaleImageView extends ImageView {

    private int mDirection = 0;//基于宽来计算高 1：基于高来计算宽
    private float mRatio = 0.6f;
    private boolean autoRatio = false;

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        mDirection = array.getInt(R.styleable.ScaleImageView_direction, 0);

        mRatio = array.getFloat(R.styleable.ScaleImageView_ratio, 0.5627f);

        array.recycle();
    }

    public void setRatio(float mRatio) {
        this.mRatio = mRatio;
    }

    public void setAutoRatio(boolean autoRatio) {
        this.autoRatio = autoRatio;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (autoRatio && drawable != null && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
            mRatio = (float) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
        }

        super.setImageDrawable(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = MeasureSpec.getMode(heightMeasureSpec);
        int size2= MeasureSpec.getSize(heightMeasureSpec);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        if (mDirection == 0) {
            h = (int) (w / mRatio);
            setMeasuredDimension(w, h);
        } else {
            w = (int) (h * mRatio);
            setMeasuredDimension(w, h);
        }
    }
}
