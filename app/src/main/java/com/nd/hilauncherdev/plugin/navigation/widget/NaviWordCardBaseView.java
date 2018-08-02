package com.nd.hilauncherdev.plugin.navigation.widget;

import java.net.URISyntaxException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviWordLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

public class NaviWordCardBaseView extends LinearLayout {

	private final static int NUM_COL = 2;
	private final static int ROW_H = 35;
	private final static int ONE_BATCH_NUM = 6;

	public Handler handler = new Handler();
	private Context mContext;
	private GridView wordGrid;
	private GridAdapter gridAdapter;

	public NavigationView2 navigationView;
	private int cur_list_index = 0;

	public NaviWordCardBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void showItemS() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				ArrayList<HotwordItemInfo> curItemList = new ArrayList<>();
				if (navigationView.hotWordList.size() <= ONE_BATCH_NUM) {
					curItemList = navigationView.hotWordList;
					setNextBtn(View.GONE);
				} else {
					setNextBtn(View.VISIBLE);
					for (int i = cur_list_index; i < cur_list_index + ONE_BATCH_NUM && i < navigationView.hotWordList.size(); i++) {
						curItemList.add(navigationView.hotWordList.get(i));
					}
				}

				if (curItemList.size() == 0) {
					curItemList = NaviWordLoader.loadDefaultWordS(mContext);
				}

				if (navigationView.hotWordList.size() > ONE_BATCH_NUM) {
					cur_list_index = cur_list_index + ONE_BATCH_NUM;
					if (cur_list_index >= navigationView.hotWordList.size()) {
						cur_list_index = 0;
					}
				}

				gridAdapter = new GridAdapter(curItemList);
				wordGrid = (GridView) findViewById(R.id.navi_word_card_grid);
				wordGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
				wordGrid.setVerticalScrollBarEnabled(false);
				wordGrid.setAdapter(gridAdapter);
				gridAdapter.notifyDataSetChanged();
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) wordGrid.getLayoutParams();
				layoutParams.height = (int) Math.ceil((double) curItemList.size() / (double) NUM_COL) * ScreenUtil.dip2px(mContext, ROW_H);
				wordGrid.setLayoutParams(layoutParams);
			}
		});
	}

	protected void setNextBtn(int visibility) {
		if (navigationView.naviWordCardView != null) {
			navigationView.naviWordCardView.findViewById(R.id.navi_card_base_next).setVisibility(visibility);
			navigationView.naviWordCardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showItemS();
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_ACTUALTIME_CARD_DISTRIBUTE_EFFECT, "hyp");
				}
			});
		}
	}

	private class GridAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		private ArrayList<HotwordItemInfo> itemList = new ArrayList<>();

		private class ViewHolder {
			View allV;
			ImageView imageView;
			TextView textView;
		}

		public GridAdapter(ArrayList<HotwordItemInfo> itemList) {
			layoutInflater = LayoutInflater.from(mContext);
			this.itemList = itemList;
		}

		@Override
		public int getCount() {
			return itemList.size();
		}

		@Override
		public HotwordItemInfo getItem(int position) {
			if (position < itemList.size())
				return itemList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try {
				final HotwordItemInfo item = getItem(position);
				if (null == item) {
					return new View(mContext);
				}

				if (layoutInflater == null) {
					layoutInflater = LayoutInflater.from(mContext);
				}

				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.navi_word_card_item, null);
				}

				if (convertView.getTag() == null) {
					ViewHolder mHolder = new ViewHolder();
					mHolder.imageView = (ImageView) convertView.findViewById(R.id.navi_word_ic);
					mHolder.textView = (TextView) convertView.findViewById(R.id.navi_word_name);
					mHolder.allV = convertView;
					convertView.setTag(mHolder);
				}

				ViewHolder mViewHolder = (ViewHolder) convertView.getTag();
				if (mViewHolder.textView != null && mViewHolder.imageView != null) {
					PaintUtils2.assemblyTypeface(mViewHolder.textView.getPaint());
					mViewHolder.textView.setText(item.name);
					mViewHolder.textView.invalidate();
//					mViewHolder.allV.setTag(item.detailUrl);
					mViewHolder.allV.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String url = item.detailUrl;
							openWordUrl(url, item);
							//实时热点
				        	PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_ACTUALTIME_CARD_DISTRIBUTE_EFFECT, String.valueOf(position));
						}
					});
					if (item.color == Color.parseColor("#ff0000")) {
						mViewHolder.imageView.setVisibility(View.VISIBLE);
					} else {
						mViewHolder.imageView.setVisibility(View.GONE);
					}
				}

				return convertView;
			} catch (Exception e) {
				return new View(mContext);
			}

		}

		protected void openWordUrl(String url, HotwordItemInfo item) {
			if (TextUtils.isEmpty(url)) {
				return;
			}

			if (url.startsWith("#Intent")) {
				if (url.indexOf("com.nd.hilauncherdev.myphone.common.PluginBridgeServicei;i.action=" + LauncherCaller.ACTION_DEFAULT_LAUNCHER) >= 0) {
					Intent intent = new Intent();
					intent.setClassName(mContext, "com.nd.hilauncherdev.myphone.common.PluginBridgeService");
					intent.putExtra(LauncherCaller.ACTION, LauncherCaller.ACTION_DEFAULT_LAUNCHER);
					mContext.startService(intent);
				} else if (url.indexOf("com.nd.hilauncherdev.launcher.navigation.SearchActivity") >= 0) {
					Intent intent = new Intent();
					intent.setClassName(mContext, "com.nd.hilauncherdev.launcher.navigation.SearchActivity");
					intent.putExtra("from","open_from_navigation");
					SystemUtil.startActivityForResultSafely((Activity) navigationView.activity, intent, SystemUtil.REQUEST_SEARCH_ACTIVITY_POSITION);
				} else {
					try {
						Intent intent = Intent.parseUri(url, 0);
						mContext.startActivity(intent);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
				// 实时热点默认动作resId:9001
				CvAnalysis.submitClickEvent(NavigationView2.activity,CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_REALTIME_HOTWORDS_CLICK,9001,CvAnalysisConstant.RESTYPE_CUSTOM_TIPS);
			} else {
				LauncherCaller.openUrl(mContext, "", url, IntegralTaskIdContent.NAVIGATION_REALTIME_HOTWORD
						, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_REALTIME_HOTWORDS_CLICK,item.resId,CvAnalysisConstant.RESTYPE_LINKS);

			}
		}

	}
}
