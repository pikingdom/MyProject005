package com.nd.hilauncherdev.plugin.navigation.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.StarLoader;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;

public class StarCardViewHelper extends CardInnerViewHelperBase {

	public static final int CARD_VIEW_NUM = 12;


	View[] starCardViewS = new View[StarCardViewHelper.CARD_VIEW_NUM];

	@Override
	public View getCardView(Card card) {
		return starCardViewS[StarCardViewHelper.getInd(card.id)];
	}

	public StarCardViewHelper(final NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void initCardView(View cardView, final Card card) {
		((TextView) cardView.findViewById(R.id.navi_card_base_next)).setVisibility(View.GONE);
		((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText("查看完整运势");
		cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherCaller.openUrl(mContext, "", StarLoader.getMoreUrl(card.id),0,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_STAR_CARD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_STAR_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			}
		});
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
		View objCardBaseV = cardViewHelper.inflater.inflate(R.layout.navi_star_card, null);
		cardInnerTotalV.addView(objCardBaseV);
		starCardViewS[StarCardViewHelper.getInd(card.id)] = cardInnerTotalV;
	}

	@Override
	public String getCardDisName(Card card) {
		return "xzys" + card.id;
	}

	@Override
	public int getCardIcon(Card card) {
		switch (card.id) {
			case CardManager.CARD_ID_STAR:
				return R.drawable.navi_star_0;
			case CardManager.CARD_ID_STAR + 1:
				return R.drawable.navi_star_1;
			case CardManager.CARD_ID_STAR + 2:
				return R.drawable.navi_star_2;
			case CardManager.CARD_ID_STAR + 3:
				return R.drawable.navi_star_3;
			case CardManager.CARD_ID_STAR + 4:
				return R.drawable.navi_star_4;
			case CardManager.CARD_ID_STAR + 5:
				return R.drawable.navi_star_5;
			case CardManager.CARD_ID_STAR + 6:
				return R.drawable.navi_star_6;
			case CardManager.CARD_ID_STAR + 7:
				return R.drawable.navi_star_7;
			case CardManager.CARD_ID_STAR + 8:
				return R.drawable.navi_star_8;
			case CardManager.CARD_ID_STAR + 9:
				return R.drawable.navi_star_9;
			case CardManager.CARD_ID_STAR + 10:
				return R.drawable.navi_star_10;
			case CardManager.CARD_ID_STAR + 11:
				return R.drawable.navi_star_11;

		}
		return R.drawable.navi_star_ic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
		View view = CardViewFactory.getInstance().getView(card);
		if (view == null) {
			return;
		}

		ArrayList<String> objL = (ArrayList<String>) msg.obj;
		final int id = Integer.valueOf(objL.get(0));
		view.findViewById(R.id.star_card_all).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LauncherCaller.openUrl(mContext, "", StarLoader.getMoreUrl(id),0,
						CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_STAR_CARD_CLICK,
						CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_STAR_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
			}
		});
		try {
			SimpleDateFormat tempDate = new SimpleDateFormat("MM-dd");
			String dateInfo = tempDate.format(new java.util.Date()) + "    " + DateUtil.getWeek();
			((TextView) view.findViewById(R.id.star_time)).setText(dateInfo);
			final ImageView ic = ((ImageView) view.findViewById(R.id.star_pic));
			ic.setImageResource(CommonLauncherControl.getJrttNewBigBackgroad());
			UiHelper.setCardIc(ic, card);
			JSONObject jo = new JSONObject(objL.get(1));
			int dataI = Integer.valueOf(jo.getString("PointsAll"));
			UiHelper.composeStarView(mContext, (LinearLayout) view.findViewById(R.id.star_star_v), dataI, R.drawable.market_star_choose, R.drawable.market_star_unchoose);
			dataI = Integer.valueOf(jo.getString("Numbers"));
			((TextView) view.findViewById(R.id.star_num)).setText("幸运数字 :   " + dataI);
			String data = jo.getString("LucklyColor");
			((TextView) view.findViewById(R.id.star_color)).setText("幸运颜色 :   " + data);
			data = jo.getString("Friends");
			((TextView) view.findViewById(R.id.star_constellation)).setText("贵人星座 :   " + data);
			data = jo.getString("ContentsAll");
			((TextView) view.findViewById(R.id.star_content)).setText(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getInd(int id) {
		return id - CardManager.CARD_ID_STAR;
	}
}
