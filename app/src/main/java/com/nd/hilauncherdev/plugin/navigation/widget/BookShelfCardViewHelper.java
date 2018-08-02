package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Book;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookCommander;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookItemView;

import java.util.ArrayList;

/**
 * 书架卡片界面工具类
 * Created by linliangbin on 2017/8/3 17:58.
 */

public class BookShelfCardViewHelper extends CardInnerViewHelperBase implements View.OnClickListener {

    /**
     * 展示的书本数
     */
    public static final int SHOW_BOOK_COUNT = 3;
    BookItemView bookViews[] = new BookItemView[SHOW_BOOK_COUNT];
    LinearLayout bookShelfContainer;


    public BookShelfCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
        super(navigationView, card, context);
    }

    @Override
    public void showDataS(NavigationView2 navigationView, Card card, Message msg) {
        ArrayList<Book> bookList = (ArrayList<Book>) msg.obj;
        if (bookList != null && bookList.size() > 0) {
            for (int i = 0; i < SHOW_BOOK_COUNT; i++) {
                if (bookViews[i] == null) {
                    continue;
                }
                if (bookList.size() <= i) {
                    bookViews[i].setVisibility(View.INVISIBLE);
                    continue;
                }
                bookViews[i].setVisibility(View.VISIBLE);
                bookViews[i].setOnClickListener(this);
                Book book = bookList.get(i);
                bookViews[i].showBookInfo(book.icon, book.name, book.isRecommend);
                bookViews[i].setTag(book);
            }
        }
    }

    @Override
    public void initCardView(View cardView, Card card) {
        for (int i = 0; i < SHOW_BOOK_COUNT; i++) {
            BookItemView bookItemView = new BookItemView(mContext);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) bookItemView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            layoutParams.weight = 1;
            layoutParams.leftMargin = ScreenUtil.dip2px(mContext, 5);
            layoutParams.rightMargin = ScreenUtil.dip2px(mContext, 5);

            bookShelfContainer.addView(bookItemView, layoutParams);
            bookViews[i] = bookItemView;
        }
        ((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("更多小说");
        (cardView.findViewById(R.id.navi_card_base_next)).setVisibility(View.GONE);
        (cardView.findViewById(R.id.navi_card_base_more)).setOnClickListener(this);
        cardView.findViewById(R.id.tv_bookshelf_shelf).setOnClickListener(this);
        cardView.findViewById(R.id.tv_bookshelf_main).setOnClickListener(this);


    }

    @Override
    public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
        View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_shelf_card, null);
        bookShelfContainer = (LinearLayout) objCardBaseV.findViewById(R.id.layout_bookshelf_container);
        cardInnerTotalV.addView(objCardBaseV);
        setCardView(cardInnerTotalV);
    }

    @Override
    public String getCardDisName(Card card) {
        return "xssj";
    }


    @Override
    public int getCardIcon(Card card) {
        return R.drawable.navi_card_book_ic;
    }


    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }

        if (v instanceof BookItemView) {
            Book bookTag = (Book) v.getTag();
            if (bookTag != null) {
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_BOOKCLICK,
                        CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_STORY_BOOKRACK, "dj");
                if (bookTag.isRecommend) {
                    SystemUtil.startActivitySafely(mContext, BookCommander.getDetailIntent(bookTag.bookId));
                    BookShelfLoader.mayNeedReloadRead = true;
                    return;
                } else {
                    SystemUtil.startActivitySafely(mContext, BookCommander.getReadIntent(bookTag.bookId));
                    BookShelfLoader.mayNeedReloadRead = true;
                    return;
                }
            }
        }

        switch (v.getId()) {
            case R.id.navi_card_base_more:

                SystemUtil.startActivitySafely(mContext, BookCommander.getMainIntent());
                BookShelfLoader.mayNeedReloadRead = true;
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_STORY_BOOKRACK, "gd");
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_MORE,
                        CvAnalysisConstant.RESTYPE_LINKS);
                break;

            case R.id.tv_bookshelf_main:
                SystemUtil.startActivitySafely(mContext, BookCommander.getMainIntent());
                BookShelfLoader.mayNeedReloadRead = true;
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_STORY_BOOKRACK, "dk");
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_MAIN,
                        CvAnalysisConstant.RESTYPE_LINKS);
                break;
            case R.id.tv_bookshelf_shelf:
                SystemUtil.startActivitySafely(mContext, BookCommander.getShelfIntent());
                BookShelfLoader.mayNeedReloadRead = true;
                CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_CARD_CLICK,
                        CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_BOOK_RES_ID_SHELF,
                        CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_STORY_BOOKRACK, "jr");
                break;
        }
    }


}
