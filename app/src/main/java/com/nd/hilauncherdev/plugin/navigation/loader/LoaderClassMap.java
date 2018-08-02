package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.BookCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.BookShelfCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.CardInnerViewHelperBase;
import com.nd.hilauncherdev.plugin.navigation.widget.FunnyCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.GameCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.JokeCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.NewsCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.PicCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.ShoppingCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.StarCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.TravelCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.VPHCardViewHelper;

import java.util.HashMap;

/**
 * 维护卡片的数据加载工具类和界面加载工具类
 * Created by linliangbin on 2017/8/3 18:37.
 */

public class LoaderClassMap {


    private static HashMap<Integer, LoaderClass> loaderClassMap;

    static {
        loaderClassMap = new HashMap<Integer, LoaderClass>();
        loaderClassMap.put(new Integer(CardManager.CARD_SITE_TYPE), new LoaderClass(NaviSiteLoaderStub.class, null));
        loaderClassMap.put(new Integer(CardManager.CARD_BOOK_TYPE), new LoaderClass(BookShelfLoader.class, BookShelfCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_PIC_TYPE), new LoaderClass(PicLoader.class, PicCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_STAR_TYPE), new LoaderClass(StarLoader.class, StarCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_NEWS_TYPE), new LoaderClass(NewsLoader.class, NewsCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_SHOPPING_TYPE), new LoaderClass(ShoppingLoader.class, ShoppingCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_TRAVEL_TYPE), new LoaderClass(TravelDataLoader.class, TravelCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_JOKE_TYPE), new LoaderClass(JokeLoader.class, JokeCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_VPH_AD_TYPE), new LoaderClass(VPHAdLoader.class, VPHCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_GAME_TYPE), new LoaderClass(GameLoader.class, GameCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_FUNNY_TYPE), new LoaderClass(FunnyLoader.class, FunnyCardViewHelper.class));
        loaderClassMap.put(new Integer(CardManager.CARD_SUBSCRIBE_TYPE), new LoaderClass(SubscribeLoader.class, FunnyCardViewHelper.class));
        if(TelephoneUtil.getApiLevel() >= 21 && !LauncherBranchController.isNavigationForCustomLauncher()){
            loaderClassMap.put(new Integer(CardManager.CARD_BOOK_TYPE), new LoaderClass(BookShelfLoader.class, BookShelfCardViewHelper.class));
        }else{
            loaderClassMap.put(new Integer(CardManager.CARD_BOOK_TYPE), new LoaderClass(BookLoader.class, BookCardViewHelper.class));
        }

    }

    public static HashMap<Integer, LoaderClass> getLoaderClassMap() {
        return loaderClassMap;
    }

    public static CardDataLoader newLoaderInstance(int type) {

        LoaderClass loaderClass = loaderClassMap.get(new Integer(type));
        if (loaderClass != null) {
            Class dataLoaderClass = loaderClass.getLoaderClass();
            if (dataLoaderClass != null) {
                try {
                    return (CardDataLoader) dataLoaderClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public static CardInnerViewHelperBase newViewHelperInstance(NavigationView2 navigationView2, Card card, Context context) {

        LoaderClass loaderClass = loaderClassMap.get(new Integer(card.type));
        if (loaderClass != null) {
            Class viewHelperClass = loaderClass.getViewHelperClass();
            if (viewHelperClass != null) {
                try {
                    return (CardInnerViewHelperBase) viewHelperClass.getConstructor(NavigationView2.class, Card.class, Context.class)
                            .newInstance(navigationView2, card, context);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static class LoaderClass {

        /**
         * 数据加载类
         */
        private Class loaderClass;
        /**
         * 界面加载类
         */
        private Class viewHelperClass;

        public LoaderClass(Class loaderClass, Class viewHelperClass) {
            this.loaderClass = loaderClass;
            this.viewHelperClass = viewHelperClass;
        }

        public Class getLoaderClass() {
            return loaderClass;
        }

        public Class getViewHelperClass() {
            return viewHelperClass;
        }

    }

}
