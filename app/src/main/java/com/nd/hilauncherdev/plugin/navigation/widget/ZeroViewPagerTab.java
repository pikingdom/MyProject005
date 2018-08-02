package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouheqiang_dian91 on 2016/3/15.
 */
public class ZeroViewPagerTab extends View {
    //普通TAB的颜色
    private final static int TEXT_COLOR = 0xaaffffff;
    //被选中的TAB的颜色
    private final static int SELECT__TEXT_COLOR = 0xFFffffff;
    /**
     * tab的个数
     */
    int mTabCount = 0;
    //用来写tab文字的tab
    private TextPaint mTextPaint = new TextPaint();
    /**
     * 用来保存tab相关信息
     */
    private List<TabInfo> tabs = new ArrayList<TabInfo>();
    /**
     * 隐藏最前面的TAB的数量
     */
    private int mHideTabCount;
    /**
     * 要显示的TAB的数量
     */
    private int mVisiableTabCount;
    /**
     * TAB所有页的长度
     */
    private int mTabTotalWidth;
    /**
     *
     * */

    private int mTabWidth;
    /**
     * TAB下面的滑动条
     * *
     */
    private Drawable hLine;
    /**
     * 屏幕宽度
     */
    private int screenW;
    /**
     * 滑动的比例0-1
     */
    private float mScrollRate = 0;
    /**
     * 每一个tab占用的滑动比例
     */
    private float mScrollUnitRate = 0;
    /**
     * 滑动条的左和右的padding
     */
    private int mHLineHPadding = 50;
    /**
     * 和这个tab联动的viewpager
     */
    private MyPhoneViewPager viewpager;
    /**
     * tab文字的top
     */
    private int mTextTop = 0;
    /**
     * 文字的高度
     */
    private float mTextH;
    /**
     * 文字升调的位置
     */

    //背景图的透明度
    private int backgroundAlpha[] = null;
    //背景图
    private Drawable backgroundDrawable = null;
    //黑色阴影背景图
    private Drawable backgroundDrawableBlack = null;
    /**
     * 背景图的透明度2
     * 加这个的需要是因为第二屏如果是列表，向上滑时会出现背景图，
     * 但是滑动到第三屏又不需要，所以滑动到第三屏时，需要隐藏
     */
    private float backgroundAlpha2 = 0;
    private Paint mPaint = new Paint();
    private int mSelectedTab = 0;
    private float mTextAscent;
    private float mTextPaddingTop = 0;

    public ZeroViewPagerTab(Context context) {
        super(context);
        init(context);
    }

    public ZeroViewPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZeroViewPagerTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        hLine = context.getResources().getDrawable(R.drawable.news_tab_hline);
        backgroundDrawable = context.getResources().getDrawable(R.drawable.news_table_bg_blue);
        backgroundDrawableBlack = context.getResources().getDrawable(R.drawable.news_tab_bg_black);
        screenW = ScreenUtil.getCurrentScreenWidth(context);
        mTextPaint.setAntiAlias(true);
        //mTextPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
        mTextPaint.setTextSize(ScreenUtil.dip2px(context, 15));
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextH = fm.descent - fm.ascent;
        mTextAscent = fm.ascent;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mVisiableTabCount > 0) {
            mTabWidth = viewWidth / mVisiableTabCount;
            mTabTotalWidth = mTabWidth * mTabCount;
            mTextTop = (int) ((viewHeight - mTextPaddingTop - mTextH) / 2 + mTextPaddingTop);
            mHLineHPadding = (int) (mTabWidth * 0.4f);
        }
    }

    /**
     * 默认显示的TAB
     *
     * @param title
     */
    public void addTitle(String title[], int hideTabCount) {
        final int N = title.length;
        backgroundAlpha = new int[N];
        for (int i = 0; i < N; i++) {
            TabInfo tab = new TabInfo();
            tab.title = title[i];
            tab.textWidth = mTextPaint.measureText(tab.title);
            tab.width = mTextPaint.measureText(tab.title);
            tabs.add(tab);
        }
        mHideTabCount = hideTabCount;
        mTabCount = title.length;
        mScrollUnitRate = 1.0f / mTabCount;
        mVisiableTabCount = title.length - mHideTabCount;
    }

    public void scrollHighLight(int scrollX) {
        float rate = 1.0f * scrollX / (screenW * mTabCount);
        mScrollRate = rate;
        mSelectedTab = (int) ((mScrollRate + mScrollUnitRate * 0.5) / mScrollUnitRate);

        if (mTabCount > 2) {
            float rate3 = (mScrollUnitRate * 2 - rate) / mScrollUnitRate;
            rate3 = rate3 > 1 ? 1 : rate3;
            rate3 = rate3 < 0 ? 0 : rate3;
            backgroundAlpha2 = 1 - rate3;
            // backgroundAlpha2 = backgroundAlpha2 > 1 ? 1 : backgroundAlpha2;

        }
        if (mScrollRate <= 0 && mHideTabCount > 0) {
            //this.setVisibility(INVISIBLE);
        } else if (getVisibility() == INVISIBLE) {
            this.setVisibility(VISIBLE);
        }
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        //画背景
        // drawColorBackground(canvas);
        int baseLeft = 0;
        int left = 0;
        int right = 0;
        int top = 0;
        int bottom = 0;

        //画滑动条
        if (mHideTabCount > 0) {
            //需要隐藏前面的部分tab
            float r = 1.0f / mTabCount * (mVisiableTabCount - 1);
            if (mScrollRate > r) {
                //滑到到隐藏页
                float offset = (r - mScrollRate) / r * screenW;
                //向右偏移量
                canvas.translate(offset, 0);
                //整体左移
                //canvas.translate((int) (-mTabWidth * mHideTabCount), 0);
                left = (int) (mTabTotalWidth * mHideTabCount * mScrollUnitRate + mHLineHPadding);
                right = (int) (left + mTabWidth - mHLineHPadding * 2);
                top = 0;
                bottom = top + getHeight();
            } else {
                //滑动到显示页
                // canvas.translate((int) (mTabWidth * mHideTabCount), 0);
                left = (int) (mTabTotalWidth * mScrollRate + mHLineHPadding);
                right = (int) (left + mTabWidth - mHLineHPadding * 2);
                top = 0;
                bottom = top + getHeight();
            }
        } else {
            //正常的TAB
            left = (int) (mTabTotalWidth * mScrollRate + mHLineHPadding);
            right = (int) (left + mTabWidth - mHLineHPadding * 2);
            top = 0;
            bottom = top + getHeight();
        }

        drawColorBackground(canvas, 0);

        hLine.setBounds(left, bottom - hLine.getIntrinsicHeight() - 2, right, bottom - 2);
        // canvas.drawRect(left, top, right, bottom, new Paint());
        hLine.draw(canvas);
        //Log.e("zhou","left="+left+"r="+right+"t="+(bottom - hLine.getIntrinsicHeight() -2)+"b="+(bottom - 2));

        for (int i = 0; i < mTabCount - mHideTabCount; i++) {
            int baseX = mTabWidth * i;
            drawOntTabTitle(canvas, baseX, mTextTop, tabs.get(i), i);
        }
        canvas.restore();
    }

    public void setViewpager(MyPhoneViewPager viewpager) {
        this.viewpager = viewpager;
    }

    public void updateSelected() {

    }

    /**
     * 画一个tab页的table
     */
    private void drawOntTabTitle(Canvas canvas, float baseX, float top, TabInfo info, int index) {
        float x = (mTabWidth - info.textWidth) / 2;
        if (index == mSelectedTab) {
            mTextPaint.setColor(SELECT__TEXT_COLOR);
        } else {
            mTextPaint.setColor(TEXT_COLOR);
        }
        float y = top - mTextAscent;
        canvas.drawText(info.title, (int) (baseX + x), y, mTextPaint);
    }

    /**
     * 画加深的背景页，由部分控制透明度0是为还是255
     */

    public void drawColorBackground(Canvas canvas, int startX) {
        int x = startX;
        int y = x + mVisiableTabCount * mTabWidth;
        //if(mScrollRate<mScrollUnitRate)return;
        int index = 0;
        float alpha = 255;
        if (backgroundAlpha == null) return;
        index = (int) (mScrollRate / mScrollUnitRate);
        if (index < 0 || index >= mTabCount) return;
        //后面没有了
        if (index + 1 > (mTabCount - 1)) {
            backgroundAlpha2 = 1;
            alpha = backgroundAlpha[index];
        } else {
            backgroundAlpha2 = (mScrollRate - index * mScrollUnitRate) / mScrollUnitRate;
            alpha = (backgroundAlpha[index + 1] - backgroundAlpha[index]) * backgroundAlpha2 + backgroundAlpha[index];
        }

        if (alpha > 0) {
            backgroundDrawable.setBounds(x, 0, y, getHeight() + 5);
            backgroundDrawable.setAlpha((int) alpha);
            backgroundDrawable.draw(canvas);
        }
        backgroundDrawableBlack.setAlpha((int) (255 - alpha));
        backgroundDrawableBlack.setBounds(x, 0, y, getHeight() + 5);
        backgroundDrawableBlack.draw(canvas);
    }

    public void setBackgroundAlpha(int alpha, int index) {
        if (index >= backgroundAlpha.length) return;
        backgroundAlpha[index] = alpha;
    }

    /**
     * 获取最合适的高度
     */
    public int getRecommendHeight() {
        return ScreenUtil.dip2px(getContext(), 48);
        //return getResources().getDrawable(R.drawable.common_title_repeat_bg).getIntrinsicHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int index = hitTab(event);
                if (index > -1) {
                    viewpager.snapToScreen(index);
                }

        }
        if (mScrollRate >= mScrollUnitRate * (mVisiableTabCount)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * */
    public void setTextTop(int paddingTop) {
        mTextPaddingTop = paddingTop;
    }

    //没命中返回-1，命中的也选中页，也返回-1
    public int hitTab(MotionEvent event) {
        if (mScrollRate > mScrollUnitRate * (mVisiableTabCount - 1)) {
            return -1;
        }
        int index = (int) (event.getX() / mTabWidth);
        if (index != mSelectedTab) {
            return index;
        }
        return -1;
    }

    /**
     * Tab 更新接口
     *
     * @author linliangbin
     * @desc
     * @time 2017/8/31 17:39
     */
    public interface TabLisener {

        public void setBackgroundAlpha(int alpha, int index);

        public void invalidateTab();

    }

    class TabInfo {
        Drawable logo;
        String title;
        float textWidth;
        float textHeight;
        float textLeft;
        float width;
        float left, top, center;
    }

}
