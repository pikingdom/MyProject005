package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
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
import com.nd.hilauncherdev.plugin.navigation.activity.FunnyDetailAct;
import com.nd.hilauncherdev.plugin.navigation.activity.FunnyListAct;
import com.nd.hilauncherdev.plugin.navigation.bean.FunnyBean;
import com.nd.hilauncherdev.plugin.navigation.bean.Joke;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FunnyCardViewHelper extends CardInnerViewHelperBase implements OnClickListener {


	AsyncImageLoader asyncImageLoader = new AsyncImageLoader();

	public FunnyCardViewHelper(final NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setVisibility(View.VISIBLE);
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setText(mContext.getString(R.string.card_joke_next));
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setVisibility(View.VISIBLE);
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("更多发现");
		cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "hyp");
				loadData(navigationView.handler, card, true);
			}
		});
		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "gd");
				startFunnyListAct();
			}
		});
	}

	/**
	 * 跳转到趣发现活动列表页面
	 */
	private void startFunnyListAct(){
		try {
			Intent intent = new Intent(mContext, FunnyListAct.class);
			mContext.startActivity(intent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_funny_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public String getCardDisName(Card card) {
		return "qfx";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_funny_ic;
	}

	@Override
	public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		View bigView = view.findViewById(R.id.funny_big_item);
		bigView.setOnClickListener(this);
		View smallView1 = view.findViewById(R.id.funny_small_item1);
		smallView1.setOnClickListener(this);
		View smallView2 = view.findViewById(R.id.funny_small_item2);
		smallView2.setOnClickListener(this);

		try {
			HashMap<String,ArrayList<FunnyBean>> map = (HashMap<String, ArrayList<FunnyBean>>) msg.obj;
			ArrayList<FunnyBean> big = map.get("big");
			ArrayList<FunnyBean> small = map.get("small");

			if(big != null && big.size() > 0){
				FunnyBean funnyBean = big.get(0);
				bigView.setTag(funnyBean);
				asyncImageLoader.showDrawable(funnyBean.BannerImgUrl, (ImageView) view.findViewById(R.id.funny_big_img),
						ImageView.ScaleType.CENTER_CROP, CommonLauncherControl.getLoadingBgTranscunt());
				((TextView)view.findViewById(R.id.funny_big_title)).setText(funnyBean.Title);
			}

			if(small != null && small.size() > 0 ){
				if(small.size() >= 1) {
					smallView1.setTag(small.get(0));
					showSmallItem(small.get(0), view.findViewById(R.id.funny_small_item1));
				}
				if(small.size() >= 2){
					smallView2.setTag(small.get(1));
					showSmallItem(small.get(1), view.findViewById(R.id.funny_small_item2));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSmallItem(FunnyBean funnyBean,View view){
		asyncImageLoader.showDrawable(funnyBean.ImgUrl,(ImageView) view.findViewById(R.id.funny_small_icon),
				ImageView.ScaleType.CENTER_CROP, CommonLauncherControl.getLoadingBgTranscunt(), ImageView.ScaleType.FIT_CENTER);
		((TextView)view.findViewById(R.id.funny_small_title)).setText(funnyBean.Title);
		((TextView)view.findViewById(R.id.funny_small_desc)).setText(funnyBean.Summary);
	}

	@Override
	public void onClick(View v) {
		try {
			FunnyBean funnyBean = (FunnyBean) v.getTag();
			startFunnyDetail(mContext,funnyBean);
			CvAnalysis.submitClickEvent(mContext, CvAnalysisConstant.NAVIGATION_SCREEN_INTO,
					CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_FUNNY_CARD_CLICK,
					CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_FUNNY_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			switch (v.getId()){
				case R.id.funny_big_item:
					PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "dtdj");
					break;
				case R.id.funny_small_item1:
					PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "x1dj");
					break;
				case R.id.funny_small_item2:
					PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "x2dj");
					break;
			}
			PluginUtil.invokeSubmitEvent(mContext, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_DETAIL, funnyBean.TopicId+"");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 展示活动页面
	 * @param mContext
	 */
	public static void startFunnyDetail(Context mContext,FunnyBean funnyBean){
		try {
			Intent intent = new Intent(mContext, FunnyDetailAct.class);
			intent.putExtra("postTitle",funnyBean.Title);
			intent.putExtra("postSummary",funnyBean.Summary);
			intent.putExtra("postUrl",funnyBean.PageUrl);
			intent.putExtra("postDate",funnyBean.publish);
			intent.putExtra("postImg",funnyBean.ImgUrl);
			mContext.startActivity(intent);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
