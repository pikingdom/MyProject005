package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.reflect.ReflectInvoke;

public abstract class CardInnerViewHelperBase {
	public Context mContext;
	public NavigationView2 navigationView;
	public Card card;

	public void setCardView(View cardView) {
		this.cardView = cardView;
	}

	public View getCardView(Card card) {
		return cardView;
	}

	public View cardView;


	public CardInnerViewHelperBase(NavigationView2 navigationView, Card card, Context context) {
		this.navigationView = navigationView;
		this.mContext = context;
		this.card = card;
	}

	public abstract void showDataS(final NavigationView2 navigationView, Card card, Message msg);

	public abstract void initCardView(View cardView, Card card);

	public void loadData(final Handler handler, final Card card, final boolean needRefreshData) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).handler = handler;
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(mContext, card, needRefreshData, 0);
			}
		});
	}

	public void processMsg(Message msg, Card card) {
		if(mContext != null){
			ReflectInvoke.initImageLoaderConfig(mContext);
		}
		CardViewFactory.getInstance().getCardViewHelper(card).showDataS(navigationView, card, msg);
	}

	public abstract void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card);

	public abstract String getCardDisName(Card card);

	public abstract int getCardIcon(Card card);
}
