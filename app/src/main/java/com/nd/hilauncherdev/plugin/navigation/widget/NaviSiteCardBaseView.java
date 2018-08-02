package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
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
import com.nd.hilauncherdev.plugin.navigation.bean.UrlBean;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

/**
 * 卡片基础View<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NaviSiteCardBaseView extends LinearLayout {

	private final static int NUM_COL = 4;
	private final static int ROW_H = 40;
	private final static int ONE_BATCH_NUM = 12;

	public Handler handler = new Handler();
	private Context mContext;
	private GridView siteGrid;
	private UrlAdapter urlAdapter;

	public NavigationView2 navigationView;

	public ArrayList<UrlBean> urlList = new ArrayList<>();
	private int cur_list_index = 0;

	public NaviSiteCardBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public void showItemS() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				ArrayList<UrlBean> curItemList = new ArrayList<>();
				if (urlList.size() <= ONE_BATCH_NUM) {
					curItemList = urlList;
					setNextBtn(View.GONE);
				} else {
					setNextBtn(View.VISIBLE);
					for (int i = cur_list_index; i < cur_list_index + ONE_BATCH_NUM && i < urlList.size(); i++) {
						curItemList.add(urlList.get(i));
					}

					cur_list_index += ONE_BATCH_NUM;
					if (cur_list_index >= urlList.size()) {
						cur_list_index = 0;
					}
				}

				urlAdapter = new UrlAdapter(curItemList);
				siteGrid = (GridView) findViewById(R.id.navi_site_card_grid);
				siteGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
				siteGrid.setAdapter(urlAdapter);
				urlAdapter.notifyDataSetChanged();
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) siteGrid.getLayoutParams();
				layoutParams.height = (int) Math.ceil((double) curItemList.size() / (double) NUM_COL) * ScreenUtil.dip2px(mContext, ROW_H);
				siteGrid.setLayoutParams(layoutParams);
			}
		});
	}

	protected void setNextBtn(int visibility) {
		if (navigationView.naviSiteCardView != null) {
			navigationView.naviSiteCardView.findViewById(R.id.navi_card_base_next).setVisibility(visibility);
			navigationView.naviSiteCardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showItemS();
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "hyp");
				}
			});
		}
	}

	private class UrlAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;

		private ArrayList<UrlBean> urlList = new ArrayList<>();

		private class ViewHolder {
			View allV;
			ImageView imageView;
			TextView textView;
		}

		public UrlAdapter(ArrayList<UrlBean> urlList) {
			layoutInflater = LayoutInflater.from(mContext);
			this.urlList = urlList;
		}

		@Override
		public int getCount() {
			return urlList.size();
		}

		@Override
		public UrlBean getItem(int position) {
			if (position < urlList.size())
				return urlList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try {
				final UrlBean item = getItem(position);
				if (null == item) {
					return new View(mContext);
				}

				if (layoutInflater == null) {
					layoutInflater = LayoutInflater.from(mContext);
				}

				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.navi_site_card_item, null);
				}

				if (convertView.getTag() == null) {
					ViewHolder mHolder = new ViewHolder();
					mHolder.allV = convertView;
					mHolder.imageView = (ImageView) convertView.findViewById(R.id.navi_site_ic);
					mHolder.textView = (TextView) convertView.findViewById(R.id.navi_site_word);
					convertView.setTag(mHolder);
				}

				ViewHolder mViewHolder = (ViewHolder) convertView.getTag();
				if (mViewHolder.textView != null && mViewHolder.imageView != null) {
					PaintUtils2.assemblyTypeface(mViewHolder.textView.getPaint());
					mViewHolder.textView.setText(item.name);
					mViewHolder.textView.invalidate();
					mViewHolder.textView.setTag(item.url);
//					mViewHolder.allV.setTag(item.url);
					mViewHolder.allV.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							//网址导航卡片点击
							String url = item.url;
							LauncherCaller.openUrl(mContext, String.valueOf(item.id), url, IntegralTaskIdContent.NAVIGATION_RECOMMEND_WEB,
									CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_NAVIGATION_CLICK, item.id, CvAnalysisConstant.RESTYPE_LINKS,
									LauncherCaller.LOC_WITHOUT_QQ_BROWSER,LauncherCaller.DIY_WEBVIEW,false,true);
							PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "wzkp" + String.valueOf(item.id));
						}
					});
					if (item.isRed) {
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

	}
}
