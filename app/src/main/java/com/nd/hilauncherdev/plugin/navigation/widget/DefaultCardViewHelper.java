package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.R;

public class DefaultCardViewHelper extends CardInnerViewHelperBase {

	public DefaultCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
		super(navigationView, card, context);
	}

	@Override
	public void showDataS(NavigationView2 navigationView, Card card, Message msg) {
	}

	@Override
	public void loadData(Handler handler, final Card card, boolean needRefreshData) {
	}

	@Override
	public void initCardView(View cardView, Card card) {
	}

	@Override
	public void processMsg(Message msg, Card card) {
	}

	@Override
	public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
	}

	@Override
	public String getCardDisName(Card card) {
		return "";
	}

	@Override
	public int getCardIcon(Card card) {
		return R.drawable.navi_card_wcy_ic;
	}
}
