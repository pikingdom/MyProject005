package com.nd.hilauncherdev.plugin.navigation.widget;

import android.view.View;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.loader.LoaderClassMap;

public class CardViewFactory {

    private static CardViewFactory cardViewFactory;
    public NavigationView2 navigationView;

    private CardViewFactory() {
    }

    public static CardViewFactory getInstance() {
        if (cardViewFactory == null) {
            cardViewFactory = new CardViewFactory();
        }

        return cardViewFactory;
    }

    public CardInnerViewHelperBase getCardViewHelper(Card card) {
        CardInnerViewHelperBase instance = navigationView.cardViewHelper.getCardViewHelper(card);
        if (instance == null) {
            instance = LoaderClassMap.newViewHelperInstance(navigationView, card, navigationView.context);
            if (instance != null) {
                navigationView.cardViewHelper.setCardViewHelper(card, instance);
                return instance;
            }
        } else {
            return instance;
        }


        if (navigationView.cardViewHelper.defaultCardViewHelper == null) {
            navigationView.cardViewHelper.defaultCardViewHelper = new DefaultCardViewHelper(navigationView, card, navigationView.context);
        }

        return navigationView.cardViewHelper.defaultCardViewHelper;
    }

    public View getView(Card card) {
        if (navigationView != null && navigationView.cardViewHelper != null) {
            CardInnerViewHelperBase cardInnerViewHelperBase = navigationView.cardViewHelper.getCardViewHelper(card);
            if (cardInnerViewHelperBase != null) {
                View view = cardInnerViewHelperBase.getCardView(card);
                if (view != null) {
                    return view;
                }
            }
        }
        return navigationView.cardViewHelper.defaultCardView;
    }
}
