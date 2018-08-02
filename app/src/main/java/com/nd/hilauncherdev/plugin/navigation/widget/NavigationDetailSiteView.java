package com.nd.hilauncherdev.plugin.navigation.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.nd.hilauncherdev.plugin.navigation.NavigationPreferences;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NavigationSiteDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

/**
 * <br>
 * Description: 网址大全页面 <br>
 * Author:chenzhihong_9101910<br>
 */
public class NavigationDetailSiteView extends RelativeLayout {

	private Activity activity;
	public Context context;
	private NavigationFavoriteSiteView mFavoriteSiteView;
	private NavigationSiteView mSiteView;
	private View showSitesTitle;
	private LinearLayout blankBottomView;
	private LinearLayout.LayoutParams blankBottomViewLP;
	private View navigationSiteViewLayout;
	private ScrollView mNavigationScrollLayout;

	private int blankBottomViewBaseLoc;
	private int screenHeight;
	private Handler handler = new Handler();
	public void initCallBack(NavigationSiteDetailActivity.onNaviClickCallBack call,boolean isNeed){
		mSiteView.initCallBack(call, isNeed);
		mFavoriteSiteView.initCallBack(call,isNeed);
	}
	public NavigationDetailSiteView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initView(int iconInRow,int iconCount) {
		mNavigationScrollLayout = (ScrollView) findViewById(R.id.navigationLayout);
		mNavigationScrollLayout.setVerticalScrollBarEnabled(false);
		// --------------------------- 网址导航 -----------------------------//
		blankBottomView = (LinearLayout) findViewById(R.id.navigationBlankView);
		// 推荐站点
		mFavoriteSiteView = (NavigationFavoriteSiteView) findViewById(R.id.navigationFavoriteWebView);
		mFavoriteSiteView.init(handler);
		mFavoriteSiteView.setNumColumns(iconInRow);
		if(iconInRow > 4){
			LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_favorite_site_container);
			if(linearLayout != null){
				int paddingLeft = ScreenUtil.dip2px(context,2);
				linearLayout.setPadding(paddingLeft,linearLayout.getTop(),paddingLeft,linearLayout.getPaddingBottom());

			}
		}
		mFavoriteSiteView.setIconCount(iconCount);
		mFavoriteSiteView.setAddLocalIcon(false);
		mFavoriteSiteView.setIconTextColor(Color.parseColor("#999999"));
		mFavoriteSiteView.setupOnItemClickListener();
		if (!Global.isZh(context)) {
			mFavoriteSiteView.setVisibility(View.GONE);
		}
		// 导航站点
		navigationSiteViewLayout = findViewById(R.id.navigationSiteViewLayout);
		mSiteView = (NavigationSiteView) findViewById(R.id.navigationWebView);
		mSiteView.setScrollView((ScrollView) findViewById(R.id.navigationLayout));
		mSiteView.setNavigationView(this);
		mSiteView.setOnCellTouchLinstener(mFavoriteSiteView);
		// 加载网站数据
		NavigationLoader.loadRecommendedAndAllSites(context, mFavoriteSiteView, mSiteView);
		// 打开导航页
		final ImageView showSitesIndex = (ImageView) findViewById(R.id.show_navigation_index);
		showSitesTitle = findViewById(R.id.show_navigation);
		showSitesTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ExpandCollapseAnimation animation = null;
				ScreenUtil.setHeightForWrapContent(context, navigationSiteViewLayout);
				if (navigationSiteViewLayout.getVisibility() == View.VISIBLE) {
					animation = new ExpandCollapseAnimation(navigationSiteViewLayout, 250, ExpandCollapseAnimation.ANIMATION_TYPE_COLLAPSE);
					showSitesIndex.setImageResource(R.drawable.navi_arrow_down);
					blankBottomView.setVisibility(View.VISIBLE);
					// HiAnalytics.submitEvent(context,
					// AnalyticsConstant.EVENT_FIRST_SCREEN_SITES_SHOW_CLICK,
					// String.valueOf(0));
				} else {
					animation = new ExpandCollapseAnimation(navigationSiteViewLayout, 500, ExpandCollapseAnimation.ANIMATION_TYPE_EXPAND);
					showSitesIndex.setImageResource(R.drawable.navi_arrow_up);
					blankBottomView.setVisibility(View.GONE);
					// HiAnalytics.submitEvent(context,
					// AnalyticsConstant.EVENT_FIRST_SCREEN_SITES_SHOW_CLICK,
					// String.valueOf(1));
				}
				navigationSiteViewLayout.startAnimation(animation);
			}
		});

		if (!NavigationPreferences.getInstance(context).isShowWebSites()) {
			hideWebSites();
			postDelayed(new Runnable() {
				@Override
				public void run() {
					measureBlankBottomView();
					invalidate();
				}
			}, 300);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureBlankBottomView();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	// 重新计算底部View的高度
	private void measureBlankBottomView() {
		try {
			int tempHeihgt = 0;
			blankBottomViewBaseLoc = ((LayoutParams) findViewById(R.id.navigationLayout).getLayoutParams()).topMargin;
			if (NavigationPreferences.getInstance(context).isShowWebSites()) {
				int h1 = (int) context.getResources().getDimension(R.dimen.show_navigation_height);
				int h2 = (int) context.getResources().getDimension(R.dimen.show_navigation_ex);
				int h3 = mFavoriteSiteView.getCustomHeight();
				tempHeihgt = blankBottomViewBaseLoc + h1 + h2 + h3 + 10;
			} else {
				tempHeihgt = blankBottomViewBaseLoc;
			}

			if (blankBottomViewLP == null) {
				blankBottomViewLP = (LinearLayout.LayoutParams) blankBottomView.getLayoutParams();
			}
			if (screenHeight == 0) {
				int[] wh = ScreenUtil.getScreenWH(context);
				screenHeight = wh[1];
			}
			blankBottomViewLP.height = screenHeight - tempHeihgt;
			blankBottomView.setLayoutParams(blankBottomViewLP);
		} catch (Exception e) {
			Log.w("NavigationView", "fail measure BlankBottomView");
		}

	}

	public void showNavigationLayout() {
		findViewById(R.id.navigationLayout).setVisibility(View.VISIBLE);
		// findViewById(R.id.navigationToolsView).setVisibility(View.VISIBLE);
		findViewById(R.id.search_view).setVisibility(View.VISIBLE);
	}

	public void hideNavigationLayout() {
		findViewById(R.id.navigationLayout).setVisibility(View.INVISIBLE);
		// findViewById(R.id.navigationToolsView).setVisibility(View.INVISIBLE);
		findViewById(R.id.search_view).setVisibility(View.INVISIBLE);
	}

	public void startVoiceRecognition() {
		VoiceRecognitionWindow.startVoiceRecognition(context, activity, 11002);
	}

	public void refreshFavoriteSiteView() {
		mFavoriteSiteView.refreshSites();
	}

	public void showWebSites() {
		findViewById(R.id.navigationView).setVisibility(View.VISIBLE);
		if (navigationSiteViewLayout.getVisibility() == View.VISIBLE) {
			blankBottomView.setVisibility(View.GONE);
		} else {
			blankBottomView.setVisibility(View.VISIBLE);
		}

		findViewById(R.id.navigationLayout).setVisibility(View.VISIBLE);
	}

	public void hideWebSites() {
		findViewById(R.id.navigationView).setVisibility(View.GONE);
		blankBottomView.setVisibility(View.VISIBLE);
	}

	public void hideSitesView() {
		mSiteView.setVisibility(View.GONE);
	}

	public Handler getHandler() {
		return handler;
	}

	public NavigationFavoriteSiteView getFavoriteSiteView() {
		return mFavoriteSiteView;
	}

	public NavigationSiteView getSiteView() {
		return mSiteView;
	}

	void setHistorySiteViewVisibility(int visibility) {
	}

	void refreshHistory() {

	}

	private void notifyDeleteHistory() {
		
	}

	public void deleteHistory() {
	}

	public void refresh() {
		try {
			if (mFavoriteSiteView != null && mSiteView != null ) {
				NavigationLoader.loadRecommendedAndAllSites(context, mFavoriteSiteView, mSiteView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
