package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.CardAddActivity;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 卡片提示布局
 * Created by linliangbin on 2017/8/11 15:08.
 */

public class CardNoticeLayout extends LinearLayout implements View.OnClickListener {


    ArrayList<Card> unAvailableCards = new ArrayList<Card>();

    public CardNoticeLayout(Context context) {
        super(context);
        init();
    }

    public CardNoticeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        unAvailableCards = CardManager.getInstance().getDisableCardList(getContext());

        this.removeAllViews();
        for (Card unavailableCard : unAvailableCards) {

            if (!CardManager.getInstance().getHasShowCardDisable(getContext(), unavailableCard)) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.card_notice_layout, null);
                TextView textView = (TextView) view.findViewById(R.id.tv_notice_message);
                textView.setText(String.format(getContext().getResources().getString(R.string.notice_card_disable_msg), unavailableCard.name));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.bottomMargin = ScreenUtil.dip2px(getContext(), 5);
                view.setTag(unavailableCard);
                view.setOnClickListener(this);
                addView(view, layoutParams);
            }

        }

    }

    public boolean update() {
        ArrayList<Card> currentUnavailable = CardManager.getInstance().getDisableCardList(getContext());

        HashMap<String, String> idMap = new HashMap<String, String>();
        if (unAvailableCards != null && unAvailableCards.size() > 0) {
            for (Card current : unAvailableCards) {
                idMap.put(current.id + "", "");
            }
        }

        if (currentUnavailable != null && currentUnavailable.size() > 0) {
            for (Card card : currentUnavailable) {
                if (!idMap.containsKey(card.id + "")) {
                    this.setVisibility(VISIBLE);
                    init();
                    return true;
                }
            }
        } else {
            this.removeAllViews();
            this.setVisibility(GONE);
        }
        return false;
    }

    @Override
    public void onClick(final View v) {
        if (v == null) {
            return;
        }
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.sohu_guide_dismiss);
        Object cardObject = v.getTag();
        Card clickCard = null;
        if (cardObject != null && cardObject instanceof Card) {
            clickCard = (Card) cardObject;
            CardManager.getInstance().setHasShowCardDisable(getContext(), clickCard);
        }


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(GONE);
                Intent intent = new Intent();
                intent.setClass(getContext(), CardAddActivity.class);
                SystemUtil.startActivitySafely(getContext(), intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(animation);

    }
}
