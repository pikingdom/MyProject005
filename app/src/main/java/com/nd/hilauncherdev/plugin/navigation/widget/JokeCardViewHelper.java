package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.Joke;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;

public class JokeCardViewHelper extends CardInnerViewHelperBase {

	// 使用短链接，目标地址：www.budejie.com/m/?baidu91
	public static final String url = UrlConstant.HTTP_URL_FELINK_COM+"QbAfIv";
	public JokeCardViewHelper(final NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setVisibility(View.VISIBLE);
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setText(mContext.getString(R.string.card_joke_next));
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setVisibility(View.VISIBLE);
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("更多段子");
		cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_HAPPY_MOMENT, "hyp");
				loadData(navigationView.handler, card, true);
			}
		});
		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_HAPPY_MOMENT, "gddz");
				LauncherCaller.openUrl(mContext, "", url,0,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_JOKE_CARD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_JOKE_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			}
		});
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_joke_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		setCardView(cardInnerTotalV);
	}

	@Override
	public String getCardDisName(Card card) {
		return "xhdz";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_joke_ic;
	}

	@Override
	public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		try {
			@SuppressWarnings("unchecked")
			ArrayList<Joke> objL = (ArrayList<Joke>) msg.obj;
			if (objL.size() > 0) {
				((TextView) view.findViewById(R.id.joke_content)).setText(objL.get(0).content);
				((TextView) view.findViewById(R.id.joke_content)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						LauncherCaller.openUrl(mContext,"",url,0,
								CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_JOKE_CARD_CLICK,
								CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_JOKE_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
						PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_HAPPY_MOMENT, "xhxq");
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
