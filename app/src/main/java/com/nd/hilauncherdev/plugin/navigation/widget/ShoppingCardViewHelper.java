package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProductsItem;
import com.nd.hilauncherdev.plugin.navigation.loader.ShoppingLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

public class ShoppingCardViewHelper extends CardInnerViewHelperBase {

	private LinearLayout icGrid = null;
	private LinearLayout textGrid1 = null;
	private LinearLayout textGrid2 = null;

	private int curIndex = 0;

	public ShoppingCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void showDataS(NavigationView2 navigationView, Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		final ArrayList<TaobaoProductsItem> objList = (ArrayList<TaobaoProductsItem>) msg.obj;
		ArrayList<TaobaoProductsItem> icList = new ArrayList<>();
		for (int i = curIndex; i < curIndex + ShoppingLoader.ONE_BATCH_SIZE_IC && i < objList.size(); i++) {
			icList.add(objList.get(i));
		}

		ArrayList<TaobaoProductsItem> textList = new ArrayList<>();
		for (int i = curIndex + ShoppingLoader.ONE_BATCH_SIZE_IC; i < curIndex + ShoppingLoader.ONE_BATCH_SIZE_IC + ShoppingLoader.ONE_BATCH_SIZE_TEXT && i < objList.size(); i++) {
			textList.add(objList.get(i));
		}

		icGrid = (LinearLayout) view.findViewById(R.id.navi_shopping_card_pic_grid);
		icGrid.removeAllViews();
		for (int i = 0; i < icList.size(); i++) {
			TaobaoProductsItem obj = icList.get(i);
			View objView = getView(i, obj, TaobaoProductsItem.TYPE_IC);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.weight = 1;
			icGrid.addView(objView, layoutParams);
		}

		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) icGrid.getLayoutParams();
		layoutParams.height = ScreenUtil.dip2px(mContext, 100);
		icGrid.setLayoutParams(layoutParams);

		textGrid1 = (LinearLayout) view.findViewById(R.id.navi_shopping_card_word_grid_1);
		textGrid1.removeAllViews();
		for (int i = 0; i < textList.size() / 2; i++) {
			TaobaoProductsItem obj = textList.get(i);
			View objView = getView(i, obj, TaobaoProductsItem.TYPE_TEXT);
			textGrid1.addView(objView);
		}

		textGrid2 = (LinearLayout) view.findViewById(R.id.navi_shopping_card_word_grid_2);
		textGrid2.removeAllViews();
		for (int i = textList.size() / 2; i < textList.size(); i++) {
			TaobaoProductsItem obj = textList.get(i);
			View objView = getView(i, obj, TaobaoProductsItem.TYPE_TEXT);
			textGrid2.addView(objView);
		}
	}

	@Override
	public String getCardDisName(Card card) {
		return "gwzn";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_shopping_ic;
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_shopping_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setText("换一批");
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("更多精品");
		cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData(navigationView.handler, card, true);
			}
		});
		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherCaller.openUrl(mContext, "", "http://url.felink.com/7vyiYr", IntegralTaskIdContent.NAVIGATION_SHOPPING_GUIDE,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SHOP_GUIDE_HOTWORD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SHOP_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			}
		});
	}

	private class ViewHolder {
		View allV;
		ImageView imageView;
		TextView textView;
	}

	public View getView(final int position, final TaobaoProductsItem item, final int type) {
		try {
			if (null == item) {
				return new View(mContext);
			}

			View convertView = LayoutInflater.from(mContext).inflate(R.layout.shopping_card_item, null);
			if (convertView.getTag() == null) {
				ViewHolder holder = new ViewHolder();
				holder.allV = convertView.findViewById(R.id.navi_shopping_all);
				holder.imageView = (ImageView) convertView.findViewById(R.id.navi_shopping_ic);
				holder.textView = (TextView) convertView.findViewById(R.id.navi_shopping_text);
				convertView.setTag(holder);
			}

			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.imageView.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
			if (type == TaobaoProductsItem.TYPE_IC) {
				viewHolder.textView.setVisibility(View.GONE);
			} else if (type == TaobaoProductsItem.TYPE_TEXT) {
				viewHolder.imageView.setVisibility(View.GONE);
			}

			if (viewHolder.textView != null && viewHolder.imageView != null) {
				PaintUtils2.assemblyTypeface(viewHolder.textView.getPaint());
				viewHolder.textView.setText(item.title);
				viewHolder.textView.invalidate();
				viewHolder.textView.setTag(item.urlClick);
				viewHolder.allV.setTag(item.urlClick);
				viewHolder.allV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String url = (String) v.getTag();
						//购物指南
						int resId = item.cvid;
						LauncherCaller.openUrl(mContext, "", url, IntegralTaskIdContent.NAVIGATION_SHOPPING_GUIDE,
									CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SHOP_GUIDE_HOTWORD_CLICK, resId, CvAnalysisConstant.RESTYPE_LINKS);
						PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "gwzn" + type + "_" + position);
					}
				});
				final String imgUrl = item.imageUrl;
				final ImageView ic = viewHolder.imageView;
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

			return convertView;
		} catch (Exception e) {
			return new View(mContext);
		}

	}

}
