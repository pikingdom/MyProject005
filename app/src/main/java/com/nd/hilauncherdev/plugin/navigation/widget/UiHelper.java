package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;

public class UiHelper {

	public static void composeStarView(Context ctx, LinearLayout linear, int n, int rid, int nid) {
		linear.removeAllViews();
		for (int i = 0; i < 5; i++) {
			ImageView view = new ImageView(ctx);
			view.setBackgroundResource(nid);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.LEFT;
			view.setLayoutParams(lp);
			linear.addView(view);
		}

		for (int i = 0; i < n; i++) {
			linear.getChildAt(i).setBackgroundResource(rid);
		}
	}

	public static void setCardIc(ImageView cardIV, Card card) {
		if (card.type == CardManager.CARD_HOT_WORD_TYPE) {
			cardIV.setImageResource(R.drawable.navi_card_hot_ic);
		}

		if (card.showType == Card.SHOW_TYPE_GROUP && card.type == CardManager.CARD_STAR_TYPE) {
			cardIV.setImageResource(R.drawable.navi_star_ic);
		}


		CardInnerViewHelperBase instance = CardViewFactory.getInstance().getCardViewHelper(card);
		if(instance != null){
			int resId = instance.getCardIcon(card);
			if(resId != 0){
				cardIV.setImageResource(resId);
			}
		}else{

		}


	}
}
