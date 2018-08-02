package com.nd.hilauncherdev.plugin.navigation.loader;

import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

import java.util.HashMap;

public class CardDataLoaderFactory {

    private static CardDataLoaderFactory cardDataLoaderFactory;
    public NavigationView2 navigationView;
    public HashMap<Integer, CardDataLoader> loaderMap = new HashMap<Integer, CardDataLoader>();
    DefaultCardDataLoader defaultCardDataLoader;

    private CardDataLoaderFactory() {
        defaultCardDataLoader = new DefaultCardDataLoader();
    }

    public static CardDataLoaderFactory getInstance() {
        if (cardDataLoaderFactory == null) {
            cardDataLoaderFactory = new CardDataLoaderFactory();
        }

        return cardDataLoaderFactory;
    }


    public CardDataLoader getCardLoader(int type) {
        CardDataLoader dataLoader = loaderMap.get(new Integer(type));
        if (dataLoader == null) {
            dataLoader = LoaderClassMap.newLoaderInstance(type);
            if (dataLoader != null) {
                loaderMap.put(new Integer(type), dataLoader);
            }
        }
        if (dataLoader != null) {
            return dataLoader;
        }
        return defaultCardDataLoader;
    }
}
