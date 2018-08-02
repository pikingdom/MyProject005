package com.nd.hilauncherdev.plugin.navigation.widget.site;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationFavoriteSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationLoader;

/**
 * 推荐ICON布局
 * Created by linliangbin on 2017/5/25 10:54.
 */

public class FavoriteSiteLayout extends LinearLayout {

    NavigationFavoriteSiteView mFavoriteSiteView;
    boolean addLocalIcon = true;
    boolean addIconMask = false;

    public FavoriteSiteLayout(Context context) {
        super(context);
        init();
    }

    public FavoriteSiteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FavoriteSiteLayout(Context context, boolean addLocalIcon) {
        super(context);
        this.addLocalIcon = addLocalIcon;
        init();
    }

    public void setAddIconMask(boolean added) {
        this.addIconMask = added;
        if (mFavoriteSiteView != null) {
            mFavoriteSiteView.setAddIconMask(this.addIconMask);
        }
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.layout_video_navigation_favorite_site, this);

        NavigationLoader.FAVORITE_ICON_COUNT = 10;
        mFavoriteSiteView = (NavigationFavoriteSiteView) findViewById(R.id.view_video_favorite_site);
        mFavoriteSiteView.init(new Handler());
        mFavoriteSiteView.setupOnItemClickListener();
        mFavoriteSiteView.setIconCount(10);
        mFavoriteSiteView.setAddLocalIcon(addLocalIcon);
        mFavoriteSiteView.loadSites();
        mFavoriteSiteView.setAddIconMask(addIconMask);
        mFavoriteSiteView.setNumColumns(5);
        mFavoriteSiteView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mFavoriteSiteView.setIconTextColor(Color.parseColor("#999999"));
        if (!Global.isZh(getContext())) {
            mFavoriteSiteView.setVisibility(View.GONE);
        }
        int spacing = ScreenUtil.dip2px(getContext(), 9);
        mFavoriteSiteView.setHorizontalSpacing(spacing);
        mFavoriteSiteView.setVerticalSpacing(spacing);


    }

    public void loadSites(){
        if(mFavoriteSiteView != null){
            mFavoriteSiteView.loadSites();
        }
    }

}
