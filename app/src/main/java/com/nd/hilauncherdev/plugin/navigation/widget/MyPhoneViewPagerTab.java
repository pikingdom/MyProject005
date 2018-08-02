package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.ColorUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 与MyPhoneViewPager联动的Tab
 */
public class MyPhoneViewPagerTab extends View {

    //如当前最多可见5个，总个数大于5个，从第3个滑动到第4个时就要提前将第6个露出
    private static final int AHEAD = 2;
    public static int DEFAULT_THEME = 0, RED_THEME = 1;
    int selectShadowColor, defaultShadowColor;
    // 主题
    private int theme = DEFAULT_THEME;
    private int selectColor, defaultColor;
    private float distanceScale, touchedTab = -1;
    private int selectTab = 0; //CommonGlobal.NO_DATA;
    /**
     * logo图标与文字的间距
     */
    private int topPadding;
    private boolean showMore = false;
    private int lineTop, textMargin, textHeight, textTop, hlLineInitCenter;
    //高亮line的宽度
    private int mHlLineWidth;
    //Tab宽度
    private int mTabWidth;
    //所有Tab的总宽度
    private int mTotalTabsWidth;
    private Context ctx;
    private PopupWindow popup;
    private MyPhoneViewPager viewpager;
    private Drawable line, hlLine, more, lineBg, selectedLineBg;
    private TextPaint textPaint = new TextPaint();
    private TextPaint textSelectedPaint = new TextPaint();
    private Map<String, String> showingTitle = new HashMap<String, String>();
    private List<TabInfo> tabs = new ArrayList<TabInfo>();
    private int mCornerMaskLeftOffset = 0;
    private int mMaxVisiableTab = 5;
    private int mXOffset;
    private boolean mInited = false;
    private OnTabChangeListener mOnTabChangeListener;

    public MyPhoneViewPagerTab(Context context) {
        super(context);
        init(context);
    }

    public MyPhoneViewPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyPhoneViewPagerTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        selectColor = res.getColor(R.color.common_title_little_text_color);
        selectShadowColor = ColorUtil.antiColorAlpha(255, selectColor);
//		defaultColor =  Color.parseColor("#315b81");
        defaultColor = res.getColor(R.color.common_little_text_color);
        defaultShadowColor = ColorUtil.antiColorAlpha(255, defaultColor);

        textPaint.setAntiAlias(true);
        textPaint.setColor(defaultColor);
        // textPaint.setShadowLayer(1, 1, 1, defaultShadowColor);
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize));
        textHeight = textPaint.getFontMetricsInt(null);
        textMargin = textHeight / 2;

        textSelectedPaint.setAntiAlias(true);
        textSelectedPaint.setColor(selectColor);
        textSelectedPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_selected_textsize));

        line = context.getResources().getDrawable(R.drawable.tab_bottom_second);
        hlLine = context.getResources().getDrawable(R.drawable.tab_scroll_tip);
        more = context.getResources().getDrawable(R.drawable.myphone_common_tab_more);
        ctx = context;
        topPadding = ScreenUtil.dip2px(ctx, 32);

        this.setBackgroundResource(R.drawable.common_tab_bg);
        lineBg = context.getResources().getDrawable(R.drawable.tab_bottom_second);
        selectedLineBg = context.getResources().getDrawable(R.drawable.tab_selected_bg);
        mCornerMaskLeftOffset = ScreenUtil.dip2px(ctx, 4);
    }

    /**
     * 设置为二级标题使用
     */
    public void setUsedAsSecondTitle() {
        setBackgroundResource(R.drawable.common_title_second_bg);
    }

    /**
     * 获取最合适的高度
     */
    public int getRecommendHeight() {
        return getResources().getDrawable(R.drawable.common_title_repeat_bg).getIntrinsicHeight();
    }

    /**
     * 默认显示的TAB
     *
     * @param logo  图标资源ID
     * @param title
     */
    public void addIconAndTitile(int logo[], String title[]) {
        final int N = title.length;
        for (int i = 0; i < N; i++) {
            showingTitle.put(title[i], "");
            TabInfo tab = new TabInfo();
            tab.logo = ctx.getResources().getDrawable(logo[i]);
            tab.title = title[i];
            tab.width = textPaint.measureText(tab.title);
            tabs.add(tab);
//			if (tab.width > maxWidth)
//				maxWidth = tab.width;
        }

        if (selectTab == MyPhoneViewPager.NO_DATA)
            selectTab = (N - 1) / 2;
    }

    /**
     * 设置角标
     */
    public void setCornerMask(int tabIndex, int resId) {
        try {
            setCornerMask(tabIndex, ctx.getResources().getDrawable(resId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置角标
     */
    public void setCornerMask(int tabIndex, Drawable drawable) {
        if (tabIndex >= tabs.size()) {
            return;
        }

        tabs.get(tabIndex).logo = drawable;
        invalidate();
    }

    public void removeCornerMask(int tabIndex) {
        if (tabIndex >= tabs.size()) {
            return;
        }

        tabs.get(tabIndex).logo = null;
        invalidate();
    }

    /**
     * 默认显示的TAB
     *
     * @param title
     */
    public void addTitle(String title[]) {
        final int N = title.length;
        for (int i = 0; i < N; i++) {
            showingTitle.put(title[i], "");
            TabInfo tab = new TabInfo();
            tab.title = title[i];
            tab.textWidth = textPaint.measureText(tab.title);
            tab.width = textPaint.measureText(tab.title);
            tabs.add(tab);
//			if (tab.width > maxWidth)
//				maxWidth = tab.width;
        }

        if (selectTab == MyPhoneViewPager.NO_DATA)
            selectTab = (N - 1) / 2;

        // hlLineWidth = (int) (maxWidth * 3 / 2);
    }

    public void setTextSize(int size) {
        textPaint.setTextSize(size);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int N = tabs.size();
        if (N <= 0) {
            return;
        }

        int visiableN = Math.min(N, mMaxVisiableTab);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mTabWidth = viewWidth / visiableN;
        mTotalTabsWidth = mTabWidth * N;

        if (!mInited) {
            mInited = true;
            if (selectTab > mMaxVisiableTab - 1)
                mXOffset = (mMaxVisiableTab - 1 - selectTab) * mTabWidth;
        }

        final int height = MeasureSpec.getSize(heightMeasureSpec);

        textTop = (height - (textHeight * 2 + textMargin)) / 2 + textHeight + textMargin;
        lineTop = textTop + textMargin;
//		float margin = (width - N * maxWidth) / N;
        float margin = (viewWidth - visiableN * mTabWidth) / visiableN;
        float startLeft = 0;
        for (int i = 0; i < N; i++) {
            TabInfo info = tabs.get(i);
            info.width = mTabWidth;
            info.left = startLeft + (mTabWidth + margin) * i + (mTabWidth - info.width) / 2;
            info.center = info.left + info.width / 2;
            info.top = textTop;
            info.textLeft = info.left + mTabWidth / 2 - info.textWidth / 2;
        }
        mHlLineWidth = mTabWidth;
//		distanceScale = (tabs.get(1).center - tabs.get(0).center) / width;
        distanceScale = ((float) mTabWidth) / viewWidth;
        line.setBounds(0, lineTop + hlLine.getIntrinsicHeight() / 2, viewWidth, lineTop + hlLine.getIntrinsicHeight());
        hlLineInitCenter = (int) (tabs.get(selectTab).center - mHlLineWidth / 2) + mXOffset;
        hlLine.setBounds(hlLineInitCenter + mHlLineWidth / 2 - 10, lineTop, hlLineInitCenter + mHlLineWidth / 2 + 10, lineTop + hlLine.getIntrinsicHeight());
        lineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + mHlLineWidth, lineTop + hlLine.getIntrinsicHeight());
        //把原来的长度缩成0.7
        Rect temp = lineBg.getBounds();
        int w = temp.width();
        temp.left = (int) (temp.left + w * 0.15f);
        temp.right = (int) (temp.right - w * 0.15f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int N = tabs.size();
        if (N <= 0) {
            return;
        }

        lineBg.draw(canvas);

        Rect bgRect = lineBg.getBounds();
        for (int i = 0; i < N; i++) {
            TabInfo info = tabs.get(i);

//			if (i == selectTab || i == (int)touchedTab) {
//				textPaint.setColor(selectColor);
//				// textPaint.setShadowLayer(1, 1, 1, selectShadowColor);
//			} else {
//				textPaint.setColor(defaultColor);
//				// textPaint.setShadowLayer(1, 1, 1, defaultShadowColor);
//			}

            if (i == touchedTab && selectTab != (int) touchedTab) {
                int hlLineInitCenter = (int) (tabs.get((int) touchedTab).center - mHlLineWidth / 2) + mXOffset;
                selectedLineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + mHlLineWidth, lineTop + hlLine.getIntrinsicHeight());
                selectedLineBg.draw(canvas);
            }

//			if (null != info.logo) {
//				final int left = (int) info.left - drawablePadding - ctx.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize);
//				int top = textTop - info.logo.getIntrinsicHeight() / 2 - 8;
//				info.logo.setBounds(left, top, left + info.logo.getIntrinsicWidth(), top + info.logo.getIntrinsicHeight());
//				info.logo.draw(canvas);
//			}
            String textTitle = info.title;
            char[] cs = textTitle.toCharArray();
            float leftToal = info.textLeft + mXOffset;
            for (int cj = 0; cj < cs.length; cj++) {
                char c = cs[cj];
                float wordW = textPaint.measureText(c + "");
                float temp = leftToal + wordW;
                Rect rect = new Rect((int) temp, (int) (info.top), (int) temp, (int) (info.top + 5));
                if (bgRect.contains(rect)) {
                    canvas.drawText(c + "", leftToal, info.top, textSelectedPaint);
                } else {
                    canvas.drawText(c + "", leftToal, info.top, textPaint);
                }
                leftToal = temp;

                if (cj == cs.length - 1 && info.logo != null) {
                    int cornerWidth = info.logo.getIntrinsicWidth();
                    int cornerHeight = info.logo.getIntrinsicHeight();
                    int cornerLeft = (int) leftToal - mCornerMaskLeftOffset;
                    int cornerTop = (int) info.top - textHeight - cornerHeight / 2 + mCornerMaskLeftOffset / 2;
                    info.logo.setBounds(cornerLeft,
                            cornerTop,
                            cornerLeft + cornerWidth,
                            cornerTop + cornerHeight);
                    info.logo.draw(canvas);
                }
            }

            if (showMore && i == selectTab) {
                final int left = (int) info.left - ctx.getResources().getDimensionPixelSize(R.dimen.frame_viewpager_tab_textsize);
                int top = textTop - more.getIntrinsicHeight() / 2 - 8;
                more.setBounds(left, top, left + more.getIntrinsicWidth(), top + more.getIntrinsicHeight());
                more.draw(canvas);
            }

        }

//		line.draw(canvas);
//		hlLine.draw(canvas);
        // 画tab之间的分割线
//		if (null != tabDivideImg) {
//			int divideWidth = ScreenUtil.dip2px(ctx, 1);
//			int divideHeight = textHeight;
//			int divideTop = textTop - textHeight;
//			for (int i = 0; i < N - 1; i++) {
//				int divideLeft = (i + 1) * mHlLineWidth - divideWidth / 2 + mXOffset;
//				tabDivideImg.setBounds(divideLeft, divideTop - divideHeight / 2, divideLeft + divideWidth, divideTop + divideHeight * 2);
//				tabDivideImg.draw(canvas);
//			}
//		}
    }

    public void scrollHighLight(int scrollX) {
        int scaleScrollX = (int) (scrollX * distanceScale);
//		hlLineInitCenter = mHlLineInitLeft + scaleScrollX;

        int viewWidth = getWidth();
        if (tabs.size() > mMaxVisiableTab && mMaxVisiableTab > AHEAD) {
            viewWidth -= (mTabWidth * AHEAD);
        }

        int right = scaleScrollX + mTabWidth;
        //当滑出默认最多可显示的tab个数时
        if (mTotalTabsWidth > viewWidth
                && right >= viewWidth) {

            int hlLineleft = viewWidth - mTabWidth;

            if (right <= (mTotalTabsWidth - mTabWidth * AHEAD)) {
                mXOffset = viewWidth - right;
                //从右边滑进来的tab露出的部分
                int rightTabOffset = (mXOffset * -1) % mTabWidth;
                if (rightTabOffset >= (mTabWidth / 2)) {
                    hlLineInitCenter = hlLineleft + (mTabWidth - rightTabOffset);
                } else {
                    hlLineInitCenter = hlLineleft + rightTabOffset;
                }
            } else {
                mXOffset = mMaxVisiableTab * mTabWidth - mTotalTabsWidth;
                hlLineInitCenter = hlLineleft + (right - (mTotalTabsWidth - mTabWidth * AHEAD));
            }
        } else {
            mXOffset = 0;
            hlLineInitCenter = scaleScrollX;
        }

        hlLine.setBounds(hlLineInitCenter + mHlLineWidth / 2 - 10, lineTop, hlLineInitCenter + mHlLineWidth / 2 + 10, lineTop + hlLine.getIntrinsicHeight());
        lineBg.setBounds(hlLineInitCenter, lineTop - topPadding, hlLineInitCenter + mHlLineWidth, getHeight());
        //把原来的长度缩成0.7
        Rect temp = lineBg.getBounds();
        int w = temp.width();
        temp.left = (int) (temp.left + w * 0.15f);
        temp.right = (int) (temp.right - w * 0.15f);
        invalidate();
    }

    public void updateSelected() {
        Rect hlBounds = hlLine.getBounds();
        Rect bound = new Rect(hlBounds.left, hlBounds.top, hlBounds.right, hlBounds.bottom);
        final int y = bound.top + (bound.bottom - bound.top) / 2;
        for (int i = 0; i < tabs.size(); i++) {
            if (!bound.contains((int) tabs.get(i).center + mXOffset, y))
                continue;

            selectTab = i;
            invalidate();
            if (mOnTabChangeListener != null) {
                mOnTabChangeListener.onChange(selectTab);
            }
            break;
        }
    }

    public int getSelectedTab() {
        return selectTab;
    }

    public void setViewpager(MyPhoneViewPager viewpager) {
        this.viewpager = viewpager;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            TabInfo hintInfo = getHitRangeInfo(event);
            if (hintInfo != null) {
                final int touchedTab = tabs.indexOf(hintInfo);
                if (touchedTab == selectTab)
                    showMore(hintInfo);
                else
                    viewpager.snapToScreen(tabs.indexOf(hintInfo));
            }
            touchedTab = -1;
            invalidate();
        } else if (action == MotionEvent.ACTION_DOWN) {
            TabInfo hintInfo = getHitRangeInfo(event);
            if (hintInfo != null) {
                touchedTab = tabs.indexOf(hintInfo);
                invalidate();
            }
        }
        return true;
    }

    /**
     * 弹出未被显示的TAB
     */
    private void showMore(TabInfo hintInfo) {
        if (popup == null)
            return;

        popup.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.frame_viewpager_tab_popup_bg));
        popup.setFocusable(true);
        int xoff = (int) (hlLineInitCenter + mHlLineWidth / 2 - popup.getWidth() / 2);
        xoff = xoff < 0 ? 0 : xoff;
        popup.showAsDropDown(this, xoff, (int) this.getTop());
        popup.update();
        // popup.setAnimationStyle(R.style.ShortCutMenuGrowFromTop);
    }

    private TabInfo getHitRangeInfo(MotionEvent e) {
        int x = Math.round(e.getX());
        int y = Math.round(e.getY());
        Rect hitRect;
        for (int index = 0; index < tabs.size(); index++) {
            TabInfo info = tabs.get(index);
            hitRect = new Rect((int) (index * info.width) + mXOffset,
                    (int) (info.top - textHeight - textMargin),
                    (int) ((index + 1) * info.width) + mXOffset,
                    (int) (info.top + textMargin));
            if (hitRect.contains(x, y)) {
                return info;
            }
        }

        return null;
    }

    /**
     * 指定默认显示的TAB
     *
     * @param tab
     */
    public void setInitTab(int tab) {
        this.selectTab = tab;
    }

    public void setMaxVisiableTab(int n) {
        mMaxVisiableTab = (n >= 1 ? n : 1);
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    /**
     * 跳转到指定屏幕,不带动画效果
     *
     * @param index
     */
    public void setToScreen(int index) {
        if (index < 0 || index > tabs.size() - 1)
            return;

        viewpager.snapToScreen(index);
        invalidate();
    }

    /****************************************************************
     * end add by linqiang(866116) 添加排序功能TAB
     ****************************************************************/

    public void setLineImg(Drawable drawable) {
        line = drawable;
    }

    public void setHlLineImg(Drawable drawable) {
        hlLine = drawable;
    }

    public void setSelectColor(int color) {
        selectColor = color;
    }

    public void setOnTabChangeListener(OnTabChangeListener listener) {
        mOnTabChangeListener = listener;
    }

    public static interface OnTabChangeListener {
        void onChange(int selected);
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

    /**
     * 未显示TAB列表
     */
    class TitleItemAdapter extends ArrayAdapter<String> {
        private LayoutInflater mInflater;

        public TitleItemAdapter(Context context, List<String> addressList) {
            super(context, 0, addressList);
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String title = getItem(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.framework_viewpager_tab_title_item, parent, false);
            }

            TextView titleview = (TextView) convertView.findViewById(R.id.frame_viewpager_tab_title_item_text);
            titleview.setText(title);
            return convertView;
        }
    }
}
