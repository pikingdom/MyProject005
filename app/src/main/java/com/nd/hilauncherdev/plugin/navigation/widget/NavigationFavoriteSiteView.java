package com.nd.hilauncherdev.plugin.navigation.widget;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.kitset.util.reflect.CommonKeepForReflect;
import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.WebSiteItem;
import com.nd.hilauncherdev.plugin.navigation.activity.NavigationSiteDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.ExposedAnatics;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐网站GridView<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NavigationFavoriteSiteView extends GridView implements View.OnTouchListener {
	private Context mContext;
	public Activity activity;
	private List<WebSiteItem> siteItems = new ArrayList<WebSiteItem>();
	private Handler mHandler;

	private FavoriteUrlAdapter favoriteAdapter;

	private List<WebSiteItem> recommandSiteList;// 推荐网址
	private List<String> recommandSiteNameList = new ArrayList<String>();// 推荐网址名称
	private List<String> siteNameList = new ArrayList<String>();// 网址名称

	private boolean onSettingActivity = false;// 标示该View是否处于配置界面
	private boolean onlyShowRecommandSite = false;
	private boolean onlyShowFavoriteSite = false;

	private final Object mLock = new Object();

	private NavigationSiteDetailActivity.onNaviClickCallBack callBack;
	private boolean needJump = true;


	/** 需要显示的ICON总数量 必须为偶数 */
	private int iconCount = 8;


	/** 是否需要增加本地ICON */
	private boolean isAddLocalIcon = false;
	/** 是否需要添加icon遮罩 */
	private boolean isAddIconMask = false;

	public void setIconCount(int iconCount) {
		if(iconCount % 2 == 0){
			this.iconCount = iconCount;
		}
	}

	public void setAddLocalIcon(boolean addLocalIcon) {
		isAddLocalIcon = addLocalIcon;
	}


	public void setIconTextColor(int iconTextColor) {
		this.iconTextColor = iconTextColor;
	}

	public void setAddIconMask(boolean added) {
		this.isAddIconMask = added;
	}

	/**
	 * ICON title 字体颜色
	 */
	private int iconTextColor = -1;
	/**
	 * 是否在网址大全页面展示，
	 * 网址大全界面推荐网址的颜色显示不同
	 * @param fromSiteActivity
	 */
	public void setFromSiteActivity(boolean fromSiteActivity) {
		this.fromSiteActivity = fromSiteActivity;
	}
	
	private boolean fromSiteActivity = false;
	
	public void initCallBack(NavigationSiteDetailActivity.onNaviClickCallBack call,boolean isNeed){
		callBack = call;
		needJump = isNeed;
	}

	public NavigationFavoriteSiteView(Context context){
		super(context);
		initData(context);
	}

	public NavigationFavoriteSiteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	private void initData(Context context){
		mContext = context;

		int spacing = ScreenUtil.dip2px(context, 9);
		setHorizontalSpacing(spacing);
		setVerticalSpacing(spacing);
	}

	public void init(Handler mHandler) {
		favoriteAdapter = new FavoriteUrlAdapter(siteItems);
		this.mHandler = mHandler;
		setAdapter(favoriteAdapter);
		setSelector(R.drawable.myphone_click_item_blue);
	}

	/**
	 * 用于导航页推荐网址点击的监听
	 */
	public void setupOnItemClickListener() {
		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				WebSiteItem item = favoriteAdapter.getItem(position);
				if (null == item)
					return;
				// 打开网址
				if (item.type == WebSiteItem.TYPE_RECOMMAND) {
					if (position == 3) {
						if (activity != null) {
							PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.EVENT_FIRST_SCREEN_ICON_CLICK, "" + position + "" + NavigationLoader.favoriteRandomIndex);
						}
					} else {
						if (activity != null) {
							PluginUtil.invokeSubmitEvent(activity, AnalyticsConstant.EVENT_FIRST_SCREEN_ICON_CLICK, "" + position);
						}
					}
				}
				// 2014年6月18日，屏蔽热词点击直接使用百度浏览器打开
				// if(AndroidPackageUtils.isPkgInstalled(mContext,
				// "com.baidu.browser.apps")){//百度浏览器有安装,就用百度浏览器打开。
				// try{
				// if(!(item.url.startsWith("http://") ||
				// item.url.startsWith("https://"))){
				// item.url = "http://" + item.url;
				// }
				// Uri uri = Uri.parse(item.url);
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setData(uri);
				// intent.setClassName("com.baidu.browser.apps","com.baidu.browser.apps.BrowserActivity");
				// mContext.startActivity(intent);
				// HiAnalytics.submitEvent(mContext,
				// AnalyticsConstant.CLICK_LINKED_TO_BAIDU_BROWSER);
				// }catch (Exception e) {
				// e.printStackTrace();
				// DefaultAppAssit.startBrowserActivity(mContext, item.url);
				// }
				// }else{
				if(needJump) {
					int positionId = CvAnalysisConstant.getPositionID(position);
					int resId = 0;
					try{
						resId = Integer.parseInt(item.siteId);
					}catch (Exception e){
						e.printStackTrace();
					}

					if(item.actionType == WebSiteItem.ACTION_TYPE_OPEN_APP){
						try {
							if(!SystemUtil.isApkInstalled(getContext(),item.appPkg)){
								SystemUtil.makeShortToast(getContext(), R.string.activity_not_found);
							}else{
								try {
									if(!NavigationKeepForReflect.processXiaoMi7OpenApp(Intent.parseUri(item.url,0))){
										SystemUtil.startActivitySafely(mContext,Intent.parseUri(item.url,0));
									}
								}catch (Throwable t){
									t.printStackTrace();
									SystemUtil.startActivitySafely(mContext,Intent.parseUri(item.url,0));
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						CvAnalysis.submitClickEvent(mContext,CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
								positionId,resId,CvAnalysisConstant.RESTYPE_LINKS);
						if(LauncherBranchController.isNavigationForCustomLauncher()){
							NavigationKeepForReflect.eventNavigationApp_V8508(item.appPkg,position);
						}
					}else if( item.actionType == WebSiteItem.ACTION_TYPE_LOCAL_ACTION_SITE_DETAIL){

						Intent intent = new Intent(getContext(), NavigationSiteDetailActivity.class);
						intent.putExtra(NavigationSiteDetailActivity.INTENT_TAG_COUNT,5);
						SystemUtil.startActivitySafely(getContext(),intent);
						PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "wzdh");

					}else{

						if(positionId != -1){
							LauncherCaller.openUrl(mContext, "", item.url, IntegralTaskIdContent.NAVIGATION_ICON,
									CvAnalysisConstant.NAVIGATION_SCREEN_INTO, positionId, resId, CvAnalysisConstant.RESTYPE_LINKS,
									LauncherCaller.LOC_WITHOUT_QQ_BROWSER,item.openType,item.needSession,true);
						}
					}
				}else{
					callBack.onNaviSiteClicked(true, item.url);
				}

				// }
			}

		});
	}

	/**
	 * 用于用户自定义添加网址页面的监听
	 * 
	 * @param activity
	 */
	public void setupOnItemClickListener(final Activity activity) {
		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final WebSiteItem item = favoriteAdapter.getItem(position);
				if (null == item)
					return;
				if (item.type == WebSiteItem.TYPE_ADD) {
				} else if (item.type == WebSiteItem.TYPE_FAVORITE) {

				}

			}

		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mHeight = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mHeight);
	}



	/**
	 * @desc 仅更新应用类型的图标
	 * @author linliangbin
	 * @time 2017/7/27 16:35
	 */
	public void updateSiteIcon() {
		if (siteItems != null) {
			boolean needRefresh = false;
			for (WebSiteItem webSiteItem : siteItems) {
				if (webSiteItem.iconType == WebSiteItem.TYPE_USE_APP_ICON) {
					if (webSiteItem.iconType == WebSiteItem.TYPE_USE_APP_ICON) {
						try {
							Bitmap launcherIcon = null;
							try {
								ComponentName componentName = getContext().getPackageManager().getLaunchIntentForPackage(webSiteItem.appPkg).getComponent();
								if (componentName != null) {
									launcherIcon = CommonKeepForReflect.getCachedIcon_V8498(componentName);
								}
							} catch (Throwable t) {
								t.printStackTrace();
							}
							if (launcherIcon != null && !launcherIcon.isRecycled()) {
								webSiteItem.icon = launcherIcon;
								needRefresh = true;
							}

						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
						needRefresh = true;
					}
				}
			}
			if(needRefresh){
				favoriteAdapter.notifyDataSetChanged();
			}
		}
	}

	// 更新推荐网站
	public void loadSites() {
		recommandSiteList = NavigationLoader.getRecommendedSites(mContext,iconCount,isAddLocalIcon);
		recommandSiteNameList.clear();
		for (WebSiteItem site : recommandSiteList) {
			recommandSiteNameList.add(site.name);
		}
		// 加载服务器上的图片
		NavigationLoader.getRecommendedSiteIconFromServer(mContext, recommandSiteList, this);

		refreshSites();
	}

	/**
	 * 加载默认推荐网站，用于网络异常情况下
	 */
	public void loadDefaultSites() {
		recommandSiteList = NavigationLoader.getRecommendedSitesFromCode(mContext,iconCount/2,isAddLocalIcon);
		recommandSiteNameList.clear();
		for (WebSiteItem site : recommandSiteList) {
			recommandSiteNameList.add(site.name);
		}
		refreshSites();
	}

	/**
	 * 上报618曝光统计数据
	 */
	public void report618Anatics(){
		if(recommandSiteList != null && recommandSiteList.size() > 0){
			for(WebSiteItem webSiteItem : recommandSiteList){
				if(webSiteItem.CallBack == ExposedAnatics.NEED_REPORT_CALL_BACK){
//					AdvertSDKManager.submitECShowURL(mContext,ExposedAnatics.POSITION_NAVIGATION_ICON,webSiteItem.AdSourceId);
				}
			}
		}
	}

	public void refreshSites() {
		synchronized (mLock) {
			siteItems.clear();
			siteNameList.clear();

			if (!onSettingActivity) {// 网址导航页数据
				// 获取推荐网址
				if (recommandSiteList != null) {
					siteItems.addAll(recommandSiteList);
					siteNameList.addAll(recommandSiteNameList);
				}
			} else {// 网址自定义页数据
				if (onlyShowRecommandSite) {
					// 获取推荐网址
					if (recommandSiteList != null) {
						siteItems.addAll(recommandSiteList);
						siteNameList.addAll(recommandSiteNameList);
					}
				}

				if (onlyShowFavoriteSite) {

				}
			}

			getHandler().post(new Runnable() {
				@Override
				public void run() {
					favoriteAdapter.notifyDataSetChanged();
				}
			});
		}

	}

	public void setOnlyShowRecommandSite(boolean onlyShowRecommandSite) {
		this.onlyShowRecommandSite = onlyShowRecommandSite;
		this.onSettingActivity = true;
		setSelector(new ColorDrawable(Color.TRANSPARENT));
	}

	public void setOnlyShowFavoriteSite(boolean onlyShowFavoriteSite) {
		this.onlyShowFavoriteSite = onlyShowFavoriteSite;
		this.onSettingActivity = true;
	}

	public int getCustomHeight() {
		int h = (int) (mContext.getResources().getDimension(R.dimen.navigation_favorite_grid_item_height) + mContext.getResources().getDimension(R.dimen.navigation_favorite_grid_item_toppadding) + mContext
				.getResources().getDimension(R.dimen.navigation_favorite_grid_item_bottompadding));
		return (siteItems == null || siteItems.size() < 1) ? 0 : h * ((siteItems.size() - 1) / 4 + 1);
	}

	private class FavoriteUrlAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		private List<WebSiteItem> urlItems = new ArrayList<WebSiteItem>();

		private class ViewHolder {
			ImageView imageView;
			ImageView imageViewMask;
			TextView textView;
		}

		public FavoriteUrlAdapter(List<WebSiteItem> urlItems) {
			layoutInflater = LayoutInflater.from(mContext);
			this.urlItems = urlItems;
		}

		@Override
		public int getCount() {
			return urlItems.size();
		}

		@Override
		public WebSiteItem getItem(int position) {
			if (position < urlItems.size())
				return urlItems.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				final WebSiteItem item = getItem(position);
				if (null == item)
					return new View(mContext);

				if (layoutInflater == null) {
					layoutInflater = LayoutInflater.from(mContext);
				}

				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.launcher_navigation_favorite_grid_item, null);
				}
				if (convertView.getTag() == null) {
					ViewHolder mHolder = new ViewHolder();
					mHolder.imageViewMask = (ImageView) convertView.findViewById(R.id.item_icon_mask);
					mHolder.imageView = (ImageView) convertView.findViewById(R.id.item_icon);
					mHolder.textView = (TextView) convertView.findViewById(R.id.item_text);
					if(fromSiteActivity){
						mHolder.textView.setTextColor(Color.parseColor("#4f4f4f"));
					}
					if(iconTextColor != -1){
						mHolder.textView.setTextColor(iconTextColor);
					}
					convertView.setTag(mHolder);
				}
				ViewHolder mViewHolder = (ViewHolder) convertView.getTag();

				if (isAddIconMask) {
					mViewHolder.imageViewMask.setImageResource(R.drawable.ic_favorite_icon_mask);
				}
				if (mViewHolder.textView != null && mViewHolder.imageView != null) {
					mViewHolder.textView.setText(item.name);
					if (item.icon != null) {// 加载服务器上的图标
						mViewHolder.imageView.setImageBitmap(item.icon);
					} else {
						if (item.type == WebSiteItem.TYPE_ADD) {
							mViewHolder.imageView.setImageResource(R.drawable.gardening_crosshairs);
						} else if (item.type != WebSiteItem.TYPE_RECOMMAND) {
							mViewHolder.imageView.setImageResource(R.drawable.navigation_def_icon);
						} else {
							mViewHolder.imageView.setImageResource(item.iconId);
						}
					}
				}

				return convertView;
			} catch (Exception e) {
				return new View(mContext);
			}

		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public Handler getHandler() {
		return mHandler;
	}

}
