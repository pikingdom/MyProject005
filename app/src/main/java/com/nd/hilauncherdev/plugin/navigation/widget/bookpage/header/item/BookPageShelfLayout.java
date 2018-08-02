package com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.EmptyUtils;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookCommander;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookItemView;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookpageItemView;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.ReaderPageView;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.presenter.BookPageShelfPresenter;

import java.util.List;

/**
 * 阅读屏-书架界面
 * Created by linliangbin on 2017/8/21 15:44.
 */

public class BookPageShelfLayout extends RelativeLayout implements IBookPageShelf, View.OnClickListener {

    public static final int SHOW_BOOK_COUNT = 3;
    BookpageItemView bookViews[] = new BookpageItemView[SHOW_BOOK_COUNT];

    BookPageShelfPresenter presenter;

    ViewGroup bookContainer;


    public BookPageShelfLayout(Context context) {
        super(context);
        init();
    }

    public BookPageShelfLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bookpage_shelf_layout, this);
        bookContainer = (ViewGroup) this.findViewById(R.id.layout_shelf_item);
        findViewById(R.id.tv_bookpage_shelf_layout_open).setOnClickListener(this);
        presenter = new BookPageShelfPresenter(this);
        presenter.loadShelfData(getContext());
    }


    @Override
    public void onReadHistoryLoaded(List<Book> books) {
        if (EmptyUtils.isNotEmpty(books)) {
            show();
            for (int i = 0; i < SHOW_BOOK_COUNT; i++) {
                if (bookViews[i] == null) {
                    BookpageItemView bookItemView = new BookpageItemView(getContext());
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bookItemView.getLayoutParams();
                    if (layoutParams == null) {
                        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    layoutParams.weight = 1;
                    layoutParams.leftMargin = ScreenUtil.dip2px(getContext(), 5);
                    layoutParams.rightMargin = ScreenUtil.dip2px(getContext(), 5);
                    bookItemView.setThumbHeight((int) ((ScreenUtil.getCurrentScreenWidth(getContext()) - ScreenUtil.dip2px(getContext(), 11 * 2 + 20)) / 3
                            / 0.7481f));
                    bookContainer.addView(bookItemView, layoutParams);
                    bookViews[i] = bookItemView;
                }
                if (books.size() <= i) {
                    bookViews[i].setVisibility(View.INVISIBLE);
                    continue;
                }
                bookViews[i].setVisibility(View.VISIBLE);
                bookViews[i].setOnClickListener(this);
                Book book = books.get(i);
                bookViews[i].showBookInfo(book.icon, book.name, false);
                bookViews[i].setTag(book);
            }
        } else {
            hide();
        }
    }

    @Override
    public void hide() {
        if (this.getVisibility() == GONE) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.card_disappear);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                BookPageShelfLayout.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animation);
    }

    @Override
    public void show() {
        if (this.getVisibility() == VISIBLE) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bookpage_enter);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                BookPageShelfLayout.this.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animation);
    }

    @Override
    public void doShelfUpdate() {
        if (presenter != null) {
            presenter.loadShelfData(getContext());
        }
    }

    @Override
    public void onClick(View v) {

        if (v == null) {
            return;
        }
        if (v instanceof BookItemView) {
            Book bookTag = (Book) v.getTag();
            if (bookTag != null) {
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(), BookCommander.getReadIntent(bookTag.bookId));
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_SHELF_ITEM, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "dsj");
                return;
            }
        }

        switch (v.getId()) {
            case R.id.tv_bookpage_shelf_layout_open:
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(), BookCommander.getShelfIntent());
                BookShelfLoader.mayNeedReloadRead = true;
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_OPEN_SHELF, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "sj");
                break;
        }

    }


}
