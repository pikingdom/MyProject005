package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;

import org.json.JSONArray;

/**
 * Created by linliangbin on 2017/8/3 13:46.
 */

public class DefaultCardManager {

    public static int[] AVAIABLE_CARDS_DEFAULT = {
            100, 200,
            CardManager.CARD_ID_SITE,
            CardManager.CARD_ID_HOT_WORD,
            CardManager.CARD_ID_NEWS_JRTT,
            CardManager.CARD_ID_BOOK,
            CardManager.CARD_ID_PIC,
            CardManager.CARD_ID_STAR, 901, 902, 903, 904, 905, 906, 907, 908, 909, 910, 911,
            CardManager.CARD_ID_SHOPPING,
            CardManager.CARD_ID_JOKE,
            CardManager.CARD_ID_VPH_AD,
            CardManager.CARD_ID_GAME,
            CardManager.CARD_ID_FUNNY,
            CardManager.CARD_ID_BOOKSHELF
    };

    private static String ALL_CARDS = null;
    private static String DEFAULT_CARDS = null;

    /**
     * @desc 从文件中读取全部卡片的信息
     * @author linliangbin
     * @time 2017/8/3 13:45
     */
    public static String getAllCards(Context context) {
        if (TextUtils.isEmpty(ALL_CARDS)) {
            ALL_CARDS = FileUtil.readFromAssetsFile(context, "NAVI_ALL_CARDS");
        }
        return ALL_CARDS;
    }


    public static String getDefaultCards(Context context) {
        if (TextUtils.isEmpty(DEFAULT_CARDS)) {
            DEFAULT_CARDS = FileUtil.readFromAssetsFile(context, "NAVI_DEFAULT_CARDS");
        }
        return DEFAULT_CARDS;
    }

    public static String getAvailableCardsDefult() {
        String result = "";
        JSONArray ja = new JSONArray();
        for (int id : AVAIABLE_CARDS_DEFAULT) {
            ja.put(id);
        }
        return ja.toString();
    }


}
