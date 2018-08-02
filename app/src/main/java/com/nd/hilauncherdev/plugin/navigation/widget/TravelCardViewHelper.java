package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.TravelBean;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class TravelCardViewHelper extends CardInnerViewHelperBase {

	private GridView menuGrid = null;
	private ObjAdapter menuAdapter = null;
	private GridView icGrid = null;
	private ObjAdapter icAdapter = null;
	private GridView textGrid = null;
	private ObjAdapter textAdapter = null;

	private int curIndex = 0;

	public TravelCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		final ArrayList<TravelBean> objList = (ArrayList<TravelBean>) msg.obj;
		ArrayList<TravelBean> menuList = new ArrayList<>();
		ArrayList<TravelBean> icList = new ArrayList<>();
		ArrayList<TravelBean> textList = new ArrayList<>();
		for (int i = curIndex; i < objList.size(); i++) {
			TravelBean travelBean = objList.get(i);
			if (travelBean.type == TravelBean.TYPE_MENU) {
				menuList.add(travelBean);
			} else if (travelBean.type == TravelBean.TYPE_PIC) {
				icList.add(travelBean);
			} else if (travelBean.type == TravelBean.TYPE_TEXT) {
				textList.add(travelBean);
			}
		}

		menuGrid = (GridView) view.findViewById(R.id.navi_travel_card_menu_grid);
		menuGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (menuAdapter == null) {
			menuAdapter = new ObjAdapter();
			menuAdapter.type = TravelBean.TYPE_MENU;
		}
		menuAdapter.setList(menuList);
		menuGrid.setNumColumns(menuList.size());
		menuGrid.setAdapter(menuAdapter);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) menuGrid.getLayoutParams();
		layoutParams.height = ScreenUtil.dip2px(mContext, 75);
		menuGrid.setLayoutParams(layoutParams);

		icGrid = (GridView) view.findViewById(R.id.navi_travel_card_pic_grid);
		icGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (icAdapter == null) {
			icAdapter = new ObjAdapter();
			icAdapter.type = TravelBean.TYPE_PIC;
		}
		icAdapter.setList(icList);
		icGrid.setAdapter(icAdapter);
		layoutParams = (LinearLayout.LayoutParams) icGrid.getLayoutParams();
		layoutParams.height = ScreenUtil.dip2px(mContext, 110);
		icGrid.setLayoutParams(layoutParams);

		textGrid = (GridView) view.findViewById(R.id.navi_travel_card_word_grid);
		textGrid.setSelector(new ColorDrawable(Color.TRANSPARENT));
		if (textAdapter == null) {
			textAdapter = new ObjAdapter();
			textAdapter.type = TravelBean.TYPE_TEXT;
		}
		textAdapter.setList(textList);
		textGrid.setAdapter(textAdapter);
		LinearLayout.LayoutParams layoutParamsText = (LinearLayout.LayoutParams) textGrid.getLayoutParams();
		layoutParamsText.height = ScreenUtil.dip2px(mContext, 60);
		textGrid.setLayoutParams(layoutParamsText);
	}

	@Override
	public void initCardView(View cardView, Card card) {
		cardView.findViewById(R.id.navi_card_base_next).setVisibility(View.GONE);
		cardView.findViewById(R.id.navi_card_base_more).setVisibility(View.GONE);
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_travel_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public String getCardDisName(Card card) {
		return "lycx";
	}

	@Override
	public int getCardIcon(Card card) {
		return 0;
	}

	private class ObjAdapter extends BaseAdapter {

		private LayoutInflater layoutInflater;
		public int type;
		public ArrayList<TravelBean> objList = new ArrayList<>();

		private class ViewHolder {
			View allV;
			ImageView imageView;
			TextView textView;
		}

		public ObjAdapter() {
			layoutInflater = LayoutInflater.from(mContext);
		}

		public void setList(ArrayList<TravelBean> passObjList) {
			objList.clear();
			for (TravelBean obj : passObjList) {
				objList.add(obj);
			}
		}

		@Override
		public int getCount() {
			return objList.size();
		}

		@Override
		public TravelBean getItem(int position) {
			if (position < objList.size())
				return objList.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			try {
				final TravelBean item = getItem(position);
				if (null == item) {
					return new View(mContext);
				}

				if (layoutInflater == null) {
					layoutInflater = LayoutInflater.from(mContext);
				}

				if (convertView == null) {
					if (type == TravelBean.TYPE_PIC) {
						convertView = layoutInflater.inflate(R.layout.travel_card_pic_item, null);
					} else {
						convertView = layoutInflater.inflate(R.layout.travel_card_menu_item, null);
					}
				}

				if (convertView.getTag() == null) {
					ViewHolder holder = new ViewHolder();
					holder.allV = convertView.findViewById(R.id.navi_travel_all);
					holder.imageView = (ImageView) convertView.findViewById(R.id.navi_travel_ic);
					holder.textView = (TextView) convertView.findViewById(R.id.navi_travel_text);
					convertView.setTag(holder);
				}

				ViewHolder viewHolder = (ViewHolder) convertView.getTag();
				
				viewHolder.imageView.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
				
				if (type == TravelBean.TYPE_MENU) {
					LinearLayout.LayoutParams layoutParamsText = (LinearLayout.LayoutParams) viewHolder.textView.getLayoutParams();
					layoutParamsText.leftMargin = 0;
					viewHolder.textView.setLayoutParams(layoutParamsText);
					viewHolder.imageView.setVisibility(View.VISIBLE);
					viewHolder.textView.setVisibility(View.VISIBLE);
				} else if (type == TravelBean.TYPE_PIC) {
					viewHolder.imageView.setVisibility(View.VISIBLE);
					viewHolder.textView.setVisibility(View.VISIBLE);
				} else if (type == TravelBean.TYPE_TEXT) {
					LinearLayout.LayoutParams layoutParamsText = (LinearLayout.LayoutParams) viewHolder.textView.getLayoutParams();
					layoutParamsText.leftMargin = ScreenUtil.dip2px(mContext, 6);
					layoutParamsText.gravity = Gravity.LEFT;
					viewHolder.textView.setLayoutParams(layoutParamsText);
					viewHolder.imageView.setVisibility(View.GONE);
					viewHolder.textView.setVisibility(View.VISIBLE);
				}

				if (viewHolder.textView != null && viewHolder.imageView != null) {
					PaintUtils2.assemblyTypeface(viewHolder.textView.getPaint());
					viewHolder.textView.setText(item.title);
					viewHolder.textView.invalidate();
					viewHolder.textView.setTag(item.url);
					viewHolder.allV.setTag(item.url);
					viewHolder.allV.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String url = (String) v.getTag();
							LauncherCaller.openUrl(mContext, "", url);
							PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "ylcx" + "_" + position);
						}
					});
					final String imgUrl = item.cover;
					final ImageView ic = viewHolder.imageView;
					if ("plane".equals(imgUrl)) {
						ic.setImageResource(R.drawable.plane);
					} else if ("hotel".equals(imgUrl)) {
						ic.setImageResource(R.drawable.ic_taobao_cate_def);
					} else if ("train".equals(imgUrl)) {
						ic.setImageResource(R.drawable.train);
					} else if ("ticket".equals(imgUrl)) {
						ic.setImageResource(R.drawable.ticket);
					} else if ("travel".equals(imgUrl)) {
						ic.setImageResource(R.drawable.travel);
					} else {
						Drawable cachedDrawable = navigationView.asyncImageLoader.loadDrawable(imgUrl, new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
								if (imageDrawable != null && imageUrl.equals(imgUrl)) {
									ic.setImageDrawable(imageDrawable);
								}
							}
						});
						if (cachedDrawable != null) {
							ic.setImageDrawable(cachedDrawable);
						}
					}
				}

				return convertView;
			} catch (Exception e) {
				return new View(mContext);
			}
		}
	}
}
