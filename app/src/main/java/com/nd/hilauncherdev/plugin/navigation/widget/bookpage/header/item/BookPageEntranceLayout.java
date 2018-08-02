package com.nd.hilauncherdev.plugin.navigation.widget.bookpage.header.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.BookShelfLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.book.BookCommander;
import com.nd.hilauncherdev.plugin.navigation.widget.bookpage.ReaderPageView;

/**
 * 阅读屏-入口界面
 * Created by linliangbin on 2017/8/21 15:46.
 */

public class BookPageEntranceLayout extends RelativeLayout implements View.OnClickListener {

    public BookPageEntranceLayout(Context context) {
        super(context);
        init();
    }

    public BookPageEntranceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bookpage_entrance_layout, this);
        findViewById(R.id.layout_bookpage_entrance_category).setOnClickListener(this);
        findViewById(R.id.layout_bookpage_entrance_rank).setOnClickListener(this);
        findViewById(R.id.layout_bookpage_entrance_free).setOnClickListener(this);
        findViewById(R.id.layout_bookpage_entrance_comic).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.layout_bookpage_entrance_category:
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(),
                        BookCommander.getCategoryIntent());
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_CATEGORY, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "fl");
                break;
            case R.id.layout_bookpage_entrance_rank:
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(),
                        BookCommander.getRankIntent());
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_RANK, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "ph");
                break;
            case R.id.layout_bookpage_entrance_free:
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(),
                        BookCommander.getFreeIntent());
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_FREE, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "mf");
                break;
            case R.id.layout_bookpage_entrance_comic:
                BookShelfLoader.mayNeedReloadRead = true;
                SystemUtil.startActivitySafely(getContext(),
                        BookCommander.getComicIntent());
                CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                        CvAnalysisConstant.DIANXIN_IREADER_RESID_COMIC, CvAnalysisConstant.RESTYPE_LINKS);
                PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "mh");
                break;

        }
    }
}
