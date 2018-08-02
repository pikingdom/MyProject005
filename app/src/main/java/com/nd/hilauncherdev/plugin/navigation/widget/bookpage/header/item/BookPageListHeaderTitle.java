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
 * 阅读推荐列表-标题栏界面
 * Created by linliangbin on 2017/8/24 15:25.
 */

public class BookPageListHeaderTitle extends RelativeLayout implements View.OnClickListener {

    public BookPageListHeaderTitle(Context context) {
        super(context);
        init();
    }

    public BookPageListHeaderTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.bookpage_list_head_layout, this);
        findViewById(R.id.tv_book_main).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BookShelfLoader.mayNeedReloadRead = true;
        SystemUtil.startActivitySafely(getContext(), BookCommander.getMainIntent());
        CvAnalysis.submitClickEvent(NavigationView2.activity, ReaderPageView.CV_PAGE_ID, ReaderPageView.CV_POSITION_ID,
                CvAnalysisConstant.DIANXIN_IREADER_RESID_ENTER_CENTER, CvAnalysisConstant.RESTYPE_LINKS);
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.READ_SCREEN_USE_INFO, "sc");
    }
}
