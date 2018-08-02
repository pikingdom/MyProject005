package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.text.TextUtils;
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
import com.nd.hilauncherdev.plugin.navigation.bean.JrttBean;
import com.nd.hilauncherdev.plugin.navigation.recommend.RecommendAppHelper;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.SPUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

/**
 * 新闻卡片操作类<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NewsCardViewHelper extends CardInnerViewHelperBase {

	public NewsCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	private static final String LOCAL_JRTT_ID_1 = "99996666635";
	private static final String LOCAL_JRTT_ID_2 = "99996666636";

	public static final String RECOMMEND_APP_PKG_TENCENT_KB = "com.tencent.reading";
	public static final String RECOMMEND_APP_PKG_TOUTIAO = "com.ss.android.article.news";

	public static final String TIANTIAN_KUAIBAO_NET = "kb.qq.com";

	public static final String MORE_NEWS_URL = "http://url.felink.com/y6zqAb";
	public static final int MORE_NEWS_RES_ID = -10000017;
	@Override
	public void showDataS(final NavigationView2 navigationView, final Card card, Message msg) {
		@SuppressWarnings("unchecked")
		final ArrayList<JrttBean> jrttBeanList = (ArrayList<JrttBean>) msg.obj;
		final View view = CardViewFactory.getInstance().getView(card);
		if (jrttBeanList.size() == 0 || view == null) {
			return;
		}

		JrttBean jrttBean = jrttBeanList.get(0);
		((TextView) view.findViewById(R.id.navi_news_big_title)).setText(jrttBean.title);
		((TextView) view.findViewById(R.id.navi_news_big_content)).setText(jrttBean.abstractContent);
		final ImageView ic = ((ImageView) view.findViewById(R.id.navi_news_ic));
		Drawable cachedImage = navigationView.asyncImageLoader.loadDrawable(true,jrttBean.imgUrl, new ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
				if (imageDrawable != null && imageUrl.equals(jrttBeanList.get(0).imgUrl)) {
					ic.setImageDrawable(imageDrawable);
				}
			}
		});
		if (cachedImage != null) {
			ic.setImageDrawable(cachedImage);
		} else {
			if (LOCAL_JRTT_ID_1.equals(jrttBean.groupId)) {
				ic.setImageResource(R.drawable.navi_jrtt_local_ic_1);
			} else if (LOCAL_JRTT_ID_2.equals(jrttBean.groupId)) {
				ic.setImageResource(R.drawable.navi_jrtt_local_ic_2);
			} else {
				ic.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
			}
		}

		view.findViewById(R.id.navi_news_big_all).setTag(jrttBean);
		view.findViewById(R.id.navi_news_big_all).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				JrttBean jrttBean = (JrttBean) v.getTag();
				LauncherCaller.openUrl(mContext, "", jrttBean.toutiaoUrl,
							CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,jrttBean.id,CvAnalysisConstant.RESTYPE_LINKS);
				//NewsLoader.sendJrttReadInfo(mContext, jrttBean.groupId);
				if(!TextUtils.isEmpty(jrttBean.toutiaoUrl) && jrttBean.toutiaoUrl.contains(TIANTIAN_KUAIBAO_NET)){
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "t1");
				}
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "1");
			}
		});
		if (jrttBeanList.size() > 1) {
			jrttBean = jrttBeanList.get(1);
			view.findViewById(R.id.navi_news_title_1).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.navi_news_title_1)).setText(jrttBean.title);
			view.findViewById(R.id.navi_news_title_1).setTag(jrttBean);
			view.findViewById(R.id.navi_news_title_1).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JrttBean jrttBean = (JrttBean) v.getTag();
					LauncherCaller.openUrl(mContext, "", jrttBean.toutiaoUrl,
								CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,jrttBean.id,CvAnalysisConstant.RESTYPE_LINKS);
					//启用CV 去除实时统计
					//NewsLoader.sendJrttReadInfo(mContext, jrttBean.groupId);
					if(!TextUtils.isEmpty(jrttBean.toutiaoUrl) && jrttBean.toutiaoUrl.contains(TIANTIAN_KUAIBAO_NET)){
						PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "t2");
					}
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "2");
				}
			});
		} else {
			view.findViewById(R.id.navi_news_title_1).setVisibility(View.GONE);
		}

		if (jrttBeanList.size() > 2) {
			jrttBean = jrttBeanList.get(2);
			view.findViewById(R.id.navi_news_title_2).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.navi_news_title_2)).setText(jrttBean.title);
			view.findViewById(R.id.navi_news_title_2).setTag(jrttBean);
			view.findViewById(R.id.navi_news_title_2).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JrttBean jrttBean = (JrttBean) v.getTag();
					LauncherCaller.openUrl(mContext, "", jrttBean.toutiaoUrl,
								CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,jrttBean.id,CvAnalysisConstant.RESTYPE_LINKS);
					//NewsLoader.sendJrttReadInfo(mContext, jrttBean.groupId);
					if(!TextUtils.isEmpty(jrttBean.toutiaoUrl) && jrttBean.toutiaoUrl.contains(TIANTIAN_KUAIBAO_NET)){
						PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "t3");
					}
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "3");
				}
			});
		} else {
			view.findViewById(R.id.navi_news_title_2).setVisibility(View.GONE);
		}
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navigation_news_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}
	
	@Override
	public String getCardDisName(Card card) {
		return "jrtt";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_jrtt_ic;
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		cardView.findViewById(R.id.navi_card_base_more).setVisibility(View.GONE);
		TextView moreV = (TextView) cardView.findViewById(R.id.navi_card_base_more);
		moreV.setVisibility(View.VISIBLE);
		moreV.setText("更多新闻");
		moreV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "gdtt");
				if (CommonLauncherControl.DX_PKG) {
					//未安装
					LauncherCaller.openUrl(mContext, "", MORE_NEWS_URL,
							CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,MORE_NEWS_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
				}else {
					/**
					 * 从7058前的版本升级上来的用户，推荐今日头条
					 * 其他用户推荐腾讯快报
					 */
					if (SPUtil.getVersionCodeFrom(navigationView.activity) <= 7058) {
						Intent intent = navigationView.activity.getPackageManager().getLaunchIntentForPackage(RECOMMEND_APP_PKG_TOUTIAO);
						if (RecommendAppHelper.isAppExist(navigationView.activity, RECOMMEND_APP_PKG_TOUTIAO) && intent != null) {
							//已安装
							RecommendAppHelper.recommendApp(mContext, navigationView.activity, navigationView.context, RECOMMEND_APP_PKG_TOUTIAO);
						} else {
							//未安装
							LauncherCaller.openUrl(mContext, "", MORE_NEWS_URL,
									CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,MORE_NEWS_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
						}
					} else {
						Intent intent = navigationView.activity.getPackageManager().getLaunchIntentForPackage(RECOMMEND_APP_PKG_TENCENT_KB);
						if (RecommendAppHelper.isAppExist(navigationView.activity, RECOMMEND_APP_PKG_TENCENT_KB) && intent != null) {
							//已安装
							RecommendAppHelper.recommendApp(mContext, navigationView.activity, navigationView.context, RECOMMEND_APP_PKG_TENCENT_KB);
						} else {
							//未安装
							LauncherCaller.openUrl(mContext, "", MORE_NEWS_URL,
									CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD_CLICK,MORE_NEWS_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
						}
					}
				}

			}
		});
		cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadData(navigationView.handler, card, true);
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_TODAY_HEADLINES_CARD_DISTRIBUTE_EFFECT, "hyp");
			}
		});
	}
}
