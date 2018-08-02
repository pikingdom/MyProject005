package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.ThemeOperator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * 卡片管理
 *
 * @author chenzhihong_9101910
 */
public class CardManager {

    public static final int CARD_ONE_TYPE_MAX_NUM = 100;
    public static final int CARD_SITE_TYPE = 3;
    public static final int CARD_HOT_WORD_TYPE = 4;
    public static final int CARD_NEWS_TYPE = 5;
    public static final int CARD_BOOK_TYPE = 7;
    public static final int CARD_PIC_TYPE = 8;
    public static final int CARD_STAR_TYPE = 9;
    public static final int CARD_SHOPPING_TYPE = 11;
    public static final int CARD_TRAVEL_TYPE = 12;
    public static final int CARD_JOKE_TYPE = 13;
    public static final int CARD_VPH_AD_TYPE = 14;
    public static final int CARD_GAME_TYPE = 15;
    public static final int CARD_FUNNY_TYPE = 16;
    public static final int CARD_SUBSCRIBE_TYPE = 17;
    public static final int CARD_BOOKSHELF_TYPE = CARD_BOOK_TYPE;
    public static final int CARD_ID_SITE = 300;
    public static final int CARD_ID_HOT_WORD = 400;
    public static final int CARD_ID_NEWS_JRTT = 1000;
    public static final int CARD_ID_BOOK = 700;
    public static final int CARD_ID_PIC = 800;
    public static final int CARD_ID_STAR = 900;
    public static final int CARD_ID_SHOPPING = 1100;
    public static final int CARD_ID_TRAVEL = 1200;
    public static final int CARD_ID_JOKE = 1300;
    public static final int CARD_ID_VPH_AD = 1400;
    public static final int CARD_ID_GAME = 1500;
    public static final int CARD_ID_FUNNY = 1600;
    public static final int CARD_ID_SUBSCRIBE = 1700;
    public static final int CARD_ID_BOOKSHELF = CARD_ID_BOOK;
    public static final String SP_NAME = "navigation_card";
    public static final String SP_ALL_CARDS = "sp_all_cards";
    public static final String SP_CURRENT_CARDS = "sp_current_cards";
    public static final String SP_AVAIABLE_CARDS = "sp_avaiable_cards";
    public static final String SP_REFRESH_TIME = "sp_refresh_time";
    public static final String SP_NOTIFY_NEW_CARD = "sp_notify_new_card_idl";
    public static final String SP_NOTIFY_NEW_CARD_TYPE = "sp_notify_new_card_type";
    public static final String SP_NOTIFY_NEW_CARD_USER_COUNT = "sp_notify_new_card_user_count";
    public static final String SP_NOTIFY_NEW_CARD_USER_COUNT2 = "sp_notify_new_card_user_count2";
    public static final String SP_CARD_RECOMMEND_WORD_INDEX = "sp_card_recommend_word_index";
    public static final String SP_HAS_ADD_DEFAULT_CARD = "sp_add_default_card";
    public static final String SP_HAS_SNAP_TO_SOHU_NEWS_PAGE = "sp_has_snap_to_sohu_news_page";
    public static final String SP_HAS_FINISH_SOHU_NOTICE = "sp_has_finish_sohu_notice";
    public static final String SP_SOHU_PAGE_BG_STATUS = "sp_sohu_page_bg_status";
    public static final String SP_LAST_PIC_ID = "sp_last_pic_id";
    public static final String SP_LAST_TEXT_ID = "sp_last_text_id";
    public static final String SP_HAS_NOTICE_PLAY_NETWORK = "sp_has_notice_play_network";
    public static final String SP_VIDEO_LIST_INDEX = "sp_video_list_index";
    public static final String SP_LAST_PIC_INDEX = "sp_last_pic_index";
    public static final String SP_IS_DISABLE_VIDEO = "sp_is_disable_video";
    public static final String SP_FINISH_SHOW_DISABLE_CARD = "sp_has_finish_show_disable_card_";
    //引导界面选择不同的主题时，零屏展示不同的推荐添加卡片
    public static final String NOTIFY_CARD_DEFAULT = CARD_ID_STAR + "," + CARD_ID_PIC;
    public static final String NOTIFY_CARD_GIRL = CARD_ID_BOOK + "," + CARD_ID_SHOPPING;
    public static final String NOTIFY_CARD_BOY = CARD_ID_PIC + "," + CARD_ID_GAME;
    public static final int DEFAULT_WORD_INDEX = -1;
    public static final int SOHU_PAGE_BG_DEFAULT = 0;
    public static final int SOHU_PAGE_BG_TRANSPARENT = 1;
    public static final int SOHU_PAGE_BG_WHITE = 2;
    private static final String SP_SEP = ",";
    private static final String SP_ADDED_CARDS = "sp_added_cards";
    private static final String SP_IS_CARD_LIST_CHANGED = "sp_is_card_list_changed";
    private static final String BOOK_CARD_ADDED = "book_card_added";
    private static final String CARD_ADDED = "card_added";
    /**
     * 服务端配置下架的卡片
     */
    private static final String CARD_DISABLE = "card_disable";
    private static final String SP_IS_MY_BOOK_CHANGED = "sp_is_my_book_changed";
    private static final String SP_PIC_CLICK_URL = "sp_pic_click_url";
    public static Comparator<Card> comparator = new Comparator<Card>() {
        public int compare(Card card1, Card card2) {
            return card1.position - card2.position;
        }
    };
    static CardManager cardManager;

    public static CardManager getInstance() {
        if (cardManager == null) {
            cardManager = new CardManager();
        }

        return cardManager;
    }

    public static ArrayList<Card> getUpCardS(ArrayList<Card> oriCardS) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        for (Card card : oriCardS) {
            if (!card.canMoved) {
                cardS.add(card);
            } else {
                break;
            }
        }

        return cardS;
    }

    public static ArrayList<Card> getDownCardS(ArrayList<Card> oriCardS) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        for (Card card : oriCardS) {
            if (!card.canMoved) {
                continue;
            } else {
                cardS.add(card);
            }
        }

        return cardS;
    }

    public static int getCardTypeById(int id) {
        if (id >= CARD_ID_NEWS_JRTT && id < CARD_ID_NEWS_JRTT + CARD_ONE_TYPE_MAX_NUM) {
            return CARD_NEWS_TYPE;
        }

        return id / 100;
    }

    public static boolean isTypeIDMatched(Card card) {
        if (card.id >= CARD_ID_NEWS_JRTT && card.id < CARD_ID_NEWS_JRTT + CARD_ONE_TYPE_MAX_NUM) {
            if (card.type == CARD_NEWS_TYPE)
                return true;
            else
                return false;
        } else {
            if (card.id / 100 == card.type)
                return true;
            else
                return false;
        }
    }

    /**
     * 本地代码写死 不会更新
     *
     * @return
     */
    public static HashSet<Integer> getImplementCardIdM() {
        HashSet<Integer> implementCardIdM = new HashSet<Integer>();
        try {
            JSONArray ja = null;
            ja = new JSONArray(DefaultCardManager.getAvailableCardsDefult());

            for (int i = 0; i < ja.length(); i++) {
                implementCardIdM.add(ja.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return implementCardIdM;
    }

    /**
     * @desc 从当前所有卡片中获取卡片信息
     * @author linliangbin
     * @time 2017/8/9 10:53
     */
    public Card getCardByType(Context context, int type) {
        ArrayList<Card> allCards = getAllCardS(context);
        if (allCards == null || allCards.size() == 0) {
            return null;
        }

        for (Card card : allCards) {
            if (type == card.type) {
                return card;
            }
        }
        return null;
    }

    public Card getCardDetail(Context context, int type) {
        ArrayList<Card> allCards = getDefaultAllCardS(context);
        if (allCards == null || allCards.size() == 0) {
            return null;
        }

        for (Card card : allCards) {
            if (type == card.type) {
                return card;
            }
        }
        return null;
    }

    /**
     * 在所有卡片 和 可用卡片SP 中都存在的
     *
     * @param context
     * @return
     */
    public ArrayList<Card> getAllCardS(Context context) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String allCardSStr = sp.getString(SP_ALL_CARDS, DefaultCardManager.getAllCards(context));
        HashSet<Integer> avaiableCardS = getAaviableCardS(context);
        if (!TextUtils.isEmpty(allCardSStr)) {
            try {
                JSONArray cardsJa = new JSONArray(allCardSStr);
                for (int i = 0; i < cardsJa.length(); i++) {
                    JSONObject cardJo = cardsJa.getJSONObject(i);
                    Card card = new Card();
                    card.initCard(cardJo);
                    if (avaiableCardS.contains(card.id)) {
                        cardS.add(card);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cardS;
    }

    /**
     * @desc 获取默认的卡片列表
     * @author linliangbin
     * @time 2017/8/15 14:31
     */
    public ArrayList<Card> getDefaultAllCardS(Context context) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String allCardSStr = DefaultCardManager.getAllCards(context);
        if (!TextUtils.isEmpty(allCardSStr)) {
            try {
                JSONArray cardsJa = new JSONArray(allCardSStr);
                for (int i = 0; i < cardsJa.length(); i++) {
                    JSONObject cardJo = cardsJa.getJSONObject(i);
                    Card card = new Card();
                    card.initCard(cardJo);
                    cardS.add(card);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cardS;
    }

    /**
     * @desc 是否是当前使用的卡片
     * @author linliangbin
     * @time 2017/8/9 9:59
     */
    public boolean isInCurrentCard(Context context, int cardType) {

        ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
        for (Card curret : cardS) {
            if (cardType == curret.type) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当前卡片
     * 需要条件：
     * 1. SP_CURRENT_CARDS 中存在
     * 2. SP_AVAIABLE_CARDS 中存在
     *
     * @param context
     * @return
     */
    public ArrayList<Card> getCurrentCardS(Context context) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String currentCardSStr = sp.getString(SP_CURRENT_CARDS, DefaultCardManager.getDefaultCards(context));
        HashSet<Integer> avaiableCardS = getAaviableCardS(context);
        if (!TextUtils.isEmpty(currentCardSStr)) {
            try {
                JSONArray cardsJa = new JSONArray(currentCardSStr);
                for (int i = 0; i < cardsJa.length(); i++) {
                    JSONObject cardJo = cardsJa.getJSONObject(i);
                    Card card = new Card();
                    card.initCard(cardJo);
                    if (!isTypeIDMatched(card)) {
                        card.type = getCardTypeById(card.id);
                    }
                    if (avaiableCardS.contains(card.id)) {
                        cardS.add(card);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Collections.sort(cardS, comparator);
        }

        return cardS;
    }

    public ArrayList<Card> getDefaultCurrentCard(Context context) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String currentCardSStr = DefaultCardManager.getDefaultCards(context);
        HashSet<Integer> avaiableCardS = getAaviableCardS(context);
        if (!TextUtils.isEmpty(currentCardSStr)) {
            try {
                JSONArray cardsJa = new JSONArray(currentCardSStr);
                for (int i = 0; i < cardsJa.length(); i++) {
                    JSONObject cardJo = cardsJa.getJSONObject(i);
                    Card card = new Card();
                    card.initCard(cardJo);
                    if (!isTypeIDMatched(card)) {
                        card.type = getCardTypeById(card.id);
                    }
                    if (avaiableCardS.contains(card.id)) {
                        cardS.add(card);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Collections.sort(cardS, comparator);
        }

        return cardS;
    }

    /**
     * 所有卡片的SP 可用卡片的SP 可用卡片的硬编码中都存在 且canBeDeleted true的；
     *
     * @param context
     * @return
     */
    public ArrayList<Card> getCardSCanBeAdded(Context context) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        ArrayList<Card> allCardS = getInstance().getAllCardS(context);
        HashSet<Integer> implementCardIdM = CardManager.getImplementCardIdM();
        boolean isStarCardAdded = false;
        for (Card card : allCardS) {
            if (!implementCardIdM.contains(new Integer(card.id))) {
                continue;
            }

            if (card.canBeDeleted) {
                if (card.type != CARD_STAR_TYPE) {
                    card.showType = Card.SHOW_TYPE_SINGLE;
                    cardS.add(card);
                } else if (card.type == CARD_STAR_TYPE && !isStarCardAdded) {
                    isStarCardAdded = true;
                    card.showType = Card.SHOW_TYPE_GROUP;
                    card.name = "星座运势";
                    cardS.add(card);
                }
            }
        }

        return cardS;
    }

    public void addCard(Context context, Card cardToAdded) {
        ArrayList<Card> curCardS = getInstance().getCurrentCardS(context);
        for (Card card : curCardS) {
            if (card.id == cardToAdded.id) {
                return;
            }
        }

        curCardS.add(cardToAdded);
        JSONArray cardJA = new JSONArray();
        int position = 1;
        for (Card card : curCardS) {
            card.position = position;
            position++;
            cardJA.put(card.getJson());
        }

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_CURRENT_CARDS, cardJA.toString()).commit();

    }

    public void saveCardS(Context context, ArrayList<Card> cardUpS, ArrayList<Card> cardDownS) {
        JSONArray cardJA = new JSONArray();
        int position = 1;
        for (Card card : cardUpS) {
            card.position = position;
            position++;
            cardJA.put(card.getJson());
        }

        for (Card card : cardDownS) {
            card.position = position;
            position++;
            cardJA.put(card.getJson());
        }

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_CURRENT_CARDS, cardJA.toString()).commit();
    }

    public void saveCardSToOneSpField(Context context, String spFieldName, ArrayList<Card> cardS) {
        JSONArray cardJA = new JSONArray();
        for (Card card : cardS) {
            cardJA.put(card.getJson());
        }

        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(spFieldName, cardJA.toString()).commit();
    }

    public void addToAddedCardS(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String typeS = sp.getString(SP_ADDED_CARDS, "");
        typeS += SP_SEP + id;
        sp.edit().putString(SP_ADDED_CARDS, typeS).commit();
    }

    public HashSet<Integer> getAddedCardIdS(Context context) {
        HashSet<Integer> addedCardIdS = new HashSet<Integer>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String idS = sp.getString(SP_ADDED_CARDS, "");
        String[] idArr = idS.split(SP_SEP);
        for (String idStr : idArr) {
            if (!TextUtils.isEmpty(idStr)) {
                addedCardIdS.add(Integer.valueOf(idStr));
            }
        }

        return addedCardIdS;
    }

    public void setAddedCardS(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_ADDED_CARDS, value).commit();
    }

    public void setIsCardListChanged(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_IS_CARD_LIST_CHANGED, value).commit();
    }

    public boolean getIsCardListChanged(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_IS_CARD_LIST_CHANGED, false);
    }

    public void setBookCardAdded(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(BOOK_CARD_ADDED, value).commit();
    }

    public boolean getBookCardAdded(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(BOOK_CARD_ADDED, false);
    }

    public void setCardAdded(Context context, int cardId, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(CARD_ADDED + cardId, value).commit();
    }

    public long getRefreshTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(SP_REFRESH_TIME, 0);
    }

    public void setRefreshTime(Context context, long cur) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(SP_REFRESH_TIME, cur).commit();
    }

    public boolean getCardAdded(Context context, int cardId) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(CARD_ADDED + cardId, false);
    }

    /**
     * 是否添加过默认卡片
     *
     * @param context
     * @param hasAdded
     */
    public void setHasAddDefaultCard(Context context, boolean hasAdded) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_HAS_ADD_DEFAULT_CARD, hasAdded).commit();
    }

    public boolean getHasAddDefaultCard(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_HAS_ADD_DEFAULT_CARD, false);
    }

    public HashSet<Integer> getAaviableCardS(Context context) {
        HashSet<Integer> avaiableCardS = new HashSet<>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String avaiableCardStr;
        avaiableCardStr = sp.getString(SP_AVAIABLE_CARDS, DefaultCardManager.getAvailableCardsDefult());
        try {
            JSONArray ja = new JSONArray(avaiableCardStr);
            for (int i = 0; i < ja.length(); i++) {
                int value = ja.getInt(i);
                /**
                 * 订阅卡片由于第三方接口无数据问题，暂行移除该卡片 2017.07.24
                 */
                if (value == CARD_ID_SUBSCRIBE) {
                    continue;
                }
                avaiableCardS.add(ja.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return avaiableCardS;
    }

    public HashSet<Integer> getDefaultAvailableCards(Context context) {
        HashSet<Integer> avaiableCardS = new HashSet<>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String avaiableCardStr;
        avaiableCardStr = DefaultCardManager.getAvailableCardsDefult();
        try {
            JSONArray ja = new JSONArray(avaiableCardStr);
            for (int i = 0; i < ja.length(); i++) {
                int value = ja.getInt(i);
                /**
                 * 订阅卡片由于第三方接口无数据问题，暂行移除该卡片 2017.07.24
                 */
                if (value == CARD_ID_SUBSCRIBE) {
                    continue;
                }
                avaiableCardS.add(ja.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return avaiableCardS;
    }

    public void setAvaiableCardS(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_AVAIABLE_CARDS, value).commit();
    }

    public void tryAddDefaultCard(Context context) {
        tryAddDefaultCardInner(context, CARD_ID_BOOK, false);
        tryAddDefaultCardInner(context, CARD_ID_PIC, false);
    }

    public void tryAddDefaultCardInner(Context context, int cardId, boolean addToDefault) {
        if (getCardAdded(context, cardId)) {
            return;
        }

        ArrayList<Card> currentCardS = getCurrentCardS(context);
        boolean isAdded = false;
        for (Card card : currentCardS) {
            if (card.id == cardId) {
                isAdded = true;
                break;
            }
        }

        if (isAdded) {
            return;
        }

        ArrayList<Card> allCardS = getAllCardS(context);
        Card cardToAdd = null;
        for (Card card : allCardS) {
            if (card.id == cardId) {
                cardToAdd = card;
                break;
            }
        }

        if (cardToAdd == null) {
            try {
                JSONArray cardsJa = new JSONArray(DefaultCardManager.getAllCards(context));
                for (int i = 0; i < cardsJa.length(); i++) {
                    JSONObject cardJo = cardsJa.getJSONObject(i);
                    Card card = new Card();
                    card.initCard(cardJo);
                    if (card.id == cardId) {
                        cardToAdd = card;
                        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                        sp.edit().putString(SP_ALL_CARDS, DefaultCardManager.getAllCards(context)).commit();
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (cardToAdd == null) {
            return;
        }
        CardManager.getInstance().setAvaiableCardS(context, DefaultCardManager.getAvailableCardsDefult());
        if (addToDefault) {
            addCard(context, cardToAdd);
        }
        setCardAdded(context, cardId, true);
    }

    public void setIsMyBookChanged(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_IS_MY_BOOK_CHANGED, value).commit();
    }

    public boolean getIsMyBookChanged(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_IS_MY_BOOK_CHANGED, false);
    }

    public void setPicClickUrl(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_PIC_CLICK_URL, value).commit();
    }

    public String getPicClickUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_PIC_CLICK_URL, "http://image.baidu.com/wisebrowse/index?tag1=美女&tag2=全部&tag3=&pn=0&rn=10&fmpage=index&pos=magic#/home");
    }

    public String getNotifyNewCardIdL(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_NOTIFY_NEW_CARD, NOTIFY_CARD_DEFAULT);
    }

    public void setNotifyNewCardIdL(Context context, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_NOTIFY_NEW_CARD, value).commit();
    }

    public int getThemeChoose(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_NOTIFY_NEW_CARD_TYPE, ThemeOperator.NOTIFY_ADD_STYLE_DEFAULT);
    }

    public void setThemeChoose(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_NOTIFY_NEW_CARD_TYPE, value).commit();
    }

    /**
     * 获取推荐卡片的使用人数
     * 通过随机数产生
     *
     * @param context
     * @return
     */
    public int getUserCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_NOTIFY_NEW_CARD_USER_COUNT, 0);
    }

    public void setUserCount(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_NOTIFY_NEW_CARD_USER_COUNT, value).commit();
    }

    public int getUserCount2(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_NOTIFY_NEW_CARD_USER_COUNT2, 0);
    }

    public void setUserCount2(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_NOTIFY_NEW_CARD_USER_COUNT2, value).commit();
    }

    /**
     * 保存和获取标题栏推荐词的显示位置
     *
     * @param context
     * @return
     */
    public int getRecommendWordIndex(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_CARD_RECOMMEND_WORD_INDEX, DEFAULT_WORD_INDEX);
    }

    public void setRecommendWordIndex(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_CARD_RECOMMEND_WORD_INDEX, value).commit();
    }

    /**
     * 设置和保存当前是否已经滑动到搜狐新闻屏过
     *
     * @param context
     * @return
     */
    public boolean getHasSnapToSohuNewsPage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_HAS_SNAP_TO_SOHU_NEWS_PAGE, false);
    }

    public void setHasSnapToSohuNewsPage(Context context, boolean hasSnap) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_HAS_SNAP_TO_SOHU_NEWS_PAGE, hasSnap).commit();
    }

    /**
     * 设置和保存当前是否已经播放搜狐新闻引导完毕
     *
     * @param context
     * @return
     */
    public boolean getHasFinishedPlaySohuGuideAnim(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_HAS_FINISH_SOHU_NOTICE, false);
    }

    public void setHasFinishedPlaySohuGuideAnim(Context context, boolean hasSnap) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_HAS_FINISH_SOHU_NOTICE, hasSnap).commit();
    }

    /**
     * 获取和设置当前搜狐背景颜色状态
     *
     * @param context
     * @return
     */
    public int getSohuPageBgStatus(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_SOHU_PAGE_BG_STATUS, SOHU_PAGE_BG_DEFAULT);
    }

    public void setSohuPageBgStatus(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_SOHU_PAGE_BG_STATUS, value).commit();
    }

    public ArrayList<Card> getCardSCanBeAddedByType(Context context, int cardType) {
        ArrayList<Card> cardS = new ArrayList<Card>();
        ArrayList<Card> allCardS = getInstance().getAllCardS(context);
        for (Card card : allCardS) {
            if (card.canBeDeleted) {
                if (card.type == cardType) {
                    card.showType = Card.SHOW_TYPE_SINGLE;
                    cardS.add(card);
                }
            }
        }

        return cardS;
    }

    /**
     * 获取当前提示添加的卡片
     * 需要满足条件：
     * 1.SP_NOTIFY_NEW_CARD 中存在
     * 2.当前卡片中不存在
     * 3.所有卡片中存在
     *
     * @param context
     * @return
     */
    public ArrayList<Card> getNewCardL(Context context) {
        ArrayList<Card> cardL = new ArrayList<>();
        HashSet<Integer> cardIdL = new HashSet<>();
        String newCardIdL = getNotifyNewCardIdL(context);
        if (TextUtils.isEmpty(newCardIdL)) {
            return cardL;
        }

        String[] cardIdArr = newCardIdL.split(",");
        for (String cardId : cardIdArr) {
            int curCardId = -1;
            try {
                curCardId = Integer.valueOf(cardId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (curCardId != -1)
                cardIdL.add(curCardId);
        }

        ArrayList<Card> curCardL = getCurrentCardS(context);
        HashSet<Integer> curCardTypeL = new HashSet<>();
        for (Card card : curCardL) {
            curCardTypeL.add(card.type);
        }

        ArrayList<Card> allCardL = getAllCardS(context);
        for (Card card : allCardL) {
            if (cardIdL.contains(Integer.valueOf(card.id)) && !curCardTypeL.contains(Integer.valueOf(card.type))) {
                if (card.type == CARD_STAR_TYPE) {
                    card.showType = Card.SHOW_TYPE_GROUP;
                    card.name = "星座运势";
                } else {
                    card.showType = Card.SHOW_TYPE_SINGLE;
                }

                cardL.add(card);
            }
        }
        sortNotifyNewCard(cardL);
        return cardL;
    }

    /**
     * 对推荐添加的卡片进行排序
     * 当前排序：微订阅>星座>美图
     */
    private void sortNotifyNewCard(ArrayList<Card> cardL) {
        Collections.sort(cardL, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                if (rhs.id == CARD_ID_SUBSCRIBE)
                    return 1;

                if (rhs.id == CARD_ID_STAR && lhs.id != CARD_ID_SUBSCRIBE) {
                    return 1;
                }

                if (rhs.id == CARD_ID_STAR && lhs.id == CARD_ID_SUBSCRIBE) {
                    return -1;
                }

                if (rhs.id == CARD_ID_PIC)
                    return -1;

                return 0;
            }
        });

    }

    /**
     * 卡片已经添加后，清除添加提示
     *
     * @param context
     * @param id      需要清除的卡片ID
     */
    public void clearNotifyNewCardId(Context context, int id) {
        String idString = id + "";
        if (TextUtils.isEmpty(idString))
            return;

        String newCardIdL = getNotifyNewCardIdL(context);
        if (TextUtils.isEmpty(newCardIdL)) {
            return;
        }

        String[] cardIdArr = newCardIdL.split(",");
        String newNewCardIdL = "";
        for (int i = 0; i < cardIdArr.length; i++) {
            if (!idString.equals(cardIdArr[i]))
                newNewCardIdL += cardIdArr[i] + ",";
        }
        if (!TextUtils.isEmpty(newNewCardIdL) && newNewCardIdL.length() > 0)
            newNewCardIdL = newNewCardIdL.substring(0, newNewCardIdL.length() - 1);
        setNotifyNewCardIdL(context, newNewCardIdL);
    }

    /**
     * 尝试添加卡片到默认添加卡片中
     * 新用户仅执行一次
     */
    public void tryAddNewToDefaultCard(Context context) {
        /**
         * 2016.06.27
         * 7528 新用户默认添加趣发现卡片
         * 升级用户默认不添加趣发现卡片
         */
        try {
            if (!CardManager.getInstance().getHasAddDefaultCard(context)) {
                CardManager.getInstance().setHasAddDefaultCard(context, true);
                if (ConfigPreferences.getInstance(context).getVersionCodeFrom() >= 7528
                        || CommonGlobal.isDianxinLauncher(context) || CommonGlobal.isAndroidLauncher(context)) {
                    ArrayList<Card> allCards = CardManager.getInstance().getAllCardS(context);
                    for (int i = 0; i < allCards.size(); i++) {
                        Card card = allCards.get(i);
                        if (card.type == CardManager.CARD_FUNNY_TYPE) {
                            CardManager.getInstance().addCard(context, card);
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * @desc 壁纸接口获取设置PicId 和 TextId
     * @author linliangbin
     * @time 2017/5/27 11:04
     */

    public int getLastTextId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_LAST_TEXT_ID, 0);
    }

    public void setLastTextId(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_LAST_TEXT_ID, value).commit();
    }

    public int getLastPicId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_LAST_PIC_ID, 0);
    }

    public void setLastPicId(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_LAST_PIC_ID, value).commit();
    }

    public int getLastPicIndex(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_LAST_PIC_INDEX, 0);
    }

    public void setLastPicIndex(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_LAST_PIC_INDEX, value).commit();
    }


    /**
     * @desc 是否已经弹框提示过流量网络播放视频
     * @author linliangbin
     * @time 2017/6/6 18:02
     */
    public boolean getHasNoticeNetworkForVideo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_HAS_NOTICE_PLAY_NETWORK, false);
    }

    public void setHasNoticeNetworkForVideo(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_HAS_NOTICE_PLAY_NETWORK, value).commit();
    }


    /**
     * @desc 获取和设置视频列表的Index
     * @author linliangbin
     * @time 2017/6/6 18:02
     */
    public int getVideoListPageIndex(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_VIDEO_LIST_INDEX, 1);
    }

    public void setVideoListPageIndex(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_VIDEO_LIST_INDEX, value).commit();
    }

    /**
     * @desc 是否强制切换回旧零屏
     * @author linliangbin
     * @time 2017/6/6 18:02
     */
    public boolean getIsDisableVideo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_IS_DISABLE_VIDEO, false);
    }

    public void setIsDisableVideo(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_IS_DISABLE_VIDEO, value).commit();
    }


    /**
     * @desc 是否已经展示过关闭卡片的提示
     * @author linliangbin
     * @time 2017/8/11 16:59
     */
    public boolean getHasShowCardDisable(Context context, Card card) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(SP_FINISH_SHOW_DISABLE_CARD + card.id, false);
    }

    public void setHasShowCardDisable(Context context, Card card) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(SP_FINISH_SHOW_DISABLE_CARD + card.id, true).commit();
    }


    /**
     * @desc 获取和设置服务端关闭的卡片
     * @author linliangbin
     * @time 2017/8/17 19:25
     */
    public ArrayList<Card> getDisableCardList(Context context) {
        ArrayList<Card> disableCards = new ArrayList<Card>();
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String disableCardStrings = sp.getString(CARD_DISABLE, "");
        try {
            JSONArray ja = new JSONArray(disableCardStrings);
            if (ja != null && ja.length() > 0) {
                for (int i = 0; i < ja.length(); i++) {
                    try {
                        String cardId = ja.getString(i);
                        Card card = CardManager.getInstance().getCardDetail(context, CardManager.getCardTypeById(Integer.parseInt(cardId)));
                        if (card != null) {
                            disableCards.add(card);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return disableCards;
    }

    public void setDisableCardList(Context context, String disableCards) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(CARD_DISABLE, disableCards).commit();
    }

}
