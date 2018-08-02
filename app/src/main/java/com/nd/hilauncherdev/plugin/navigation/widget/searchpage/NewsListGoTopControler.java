package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsListViewAdapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * listview 置顶按钮工具类
 * Created by linliangbin on 2017/8/9 21:42.
 */

public class NewsListGoTopControler {


    private PluginAdViewChecker adViewChecker;

    public NewsListGoTopControler(NewsListViewAdapter adapter, final View gotoTop, final Context context) {

        if (gotoTop == null) return;
        final Animation animFadein = AnimationUtils.loadAnimation(context, R.anim.gototopin_ani);
        final Animation animout = AnimationUtils.loadAnimation(context, R.anim.gototopout_ani);

        adapter.setScrollListener(new NewsListViewAdapter.OnExtendScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (adViewChecker != null && adViewChecker.isAvailablePluinAdView()) {
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) gotoTop.getLayoutParams();
                    int bottomMargin = (int) (ScreenUtil.dip2px(context, 5) + ScreenUtil.dip2px(context, 10) + ScreenUtil.getCurrentScreenWidth(context) / 4.0f);
                    if (rlp.bottomMargin != bottomMargin) {
                        rlp.setMargins(rlp.leftMargin, rlp.topMargin, rlp.rightMargin, bottomMargin);
                        gotoTop.setLayoutParams(rlp);
                    }
                } else {
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) gotoTop.getLayoutParams();
                    int bottomMargin = ScreenUtil.dip2px(context, 24);
                    if (bottomMargin != rlp.bottomMargin) {
                        rlp.setMargins(rlp.leftMargin, rlp.topMargin, rlp.rightMargin, bottomMargin);
                        gotoTop.setLayoutParams(rlp);
                    }
                }
                if (firstVisibleItem >= 3) {
                    if (gotoTop.getVisibility() == INVISIBLE) {
                        Animation ani = gotoTop.getAnimation();
                        if (ani != null) {
                            ani.cancel();
                        }
                        gotoTop.startAnimation(animFadein);
                        gotoTop.setVisibility(VISIBLE);
                    }

                } else {
                    if (gotoTop.getVisibility() == VISIBLE) {
                        gotoTop.startAnimation(animout);
                        gotoTop.setVisibility(INVISIBLE);
                    }
                }
            }
        });
    }

    public void setAdViewChecker(PluginAdViewChecker adViewChecker) {
        this.adViewChecker = adViewChecker;
    }


    public interface PluginAdViewChecker {
        public boolean isAvailablePluinAdView();
    }

}
