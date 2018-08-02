package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.SharedPreferences;

import com.nd.hilauncherdev.plugin.navigation.util.StringUtil;

import java.util.ArrayList;

/**
 * 升级时重新配置变更了的默认卡片信息
 * Created by linliangbin_dian91 on 2015/10/23.
 */
public class CardUpdateManager {

    public static final String SP_CARD_VERSION = "sp_card_version";
    /**
     * 版本6： 推荐添加卡片出增加笑话卡片 桌面：6.9.2 首次添加该动作
     * 版本10：推荐添加卡片处添加微订阅卡片 桌面V7.6.2 首次添加该动作-2016.08.16
     * 版本11：默认卡片增加小说卡片
     */
    public static final int NAVIGTION_CARD_MANAGER_VERSION = 11;
    /**
     * 标记当前默认卡片的版本号，
     * 当SP中获得的版本号小于HARD CODE 的版本号时，
     * 则重新拷贝当前默认配置到SP中
     * 当卡片配置改变时，需要该版本号++;
     */
    private static final String UPDATE_NOTIFY_NEW_CARD = CardManager.CARD_ID_JOKE + ",";

    /**
     * 解决升级版本卡片配置无法生效问题
     */
    public static void doUpdate(Context mContext) {
        int lastVersion = getCardVersion(mContext);

        if (lastVersion < 10) {
            addSubscribeCardToNotifyNewCard(mContext);
        }

        if (lastVersion < NAVIGTION_CARD_MANAGER_VERSION) {
            updateAvailCard(mContext);
            updateAllCard(mContext);
        }

        if (lastVersion < 11) {
            addBookshelCardToDefault(mContext);
        }

        if (lastVersion < NAVIGTION_CARD_MANAGER_VERSION) {
            updateCardVersion(mContext, NAVIGTION_CARD_MANAGER_VERSION);
        }
    }

    public static final int getCardVersion(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(SP_CARD_VERSION, 0);
    }

    public static final void updateCardVersion(Context mContext, int cardVersion) {
        SharedPreferences sp = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(SP_CARD_VERSION, cardVersion).commit();
    }

    /**
     * 更新SP_AVAIABLE_CARDS SP
     *
     * @param mContext
     */
    public static final void updateAvailCard(Context mContext) {

        CardManager.getInstance().setAvaiableCardS(mContext, DefaultCardManager.getAvailableCardsDefult());

    }

    /**
     * 更新所有卡片的SP
     *
     * @param mContext
     */
    public static final void updateAllCard(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(CardManager.SP_ALL_CARDS, DefaultCardManager.getAllCards(mContext)).commit();
    }

    /**
     * 更新 提示添加通知卡片SP
     */
    public static final void updateNotifyNewCard(Context mContext) {
        SharedPreferences sp = mContext.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        String notigyCardStr = sp.getString(CardManager.SP_NOTIFY_NEW_CARD, CardManager.NOTIFY_CARD_DEFAULT);
        if (CardManager.NOTIFY_CARD_DEFAULT.equals(notigyCardStr))
            return;
        CardManager.getInstance().setNotifyNewCardIdL(mContext, StringUtil.mergeString(notigyCardStr, UPDATE_NOTIFY_NEW_CARD, false));
    }

    /**
     * 在当前推荐卡片中新增微订阅卡片
     *
     * @param mContext
     */
    public static final void addSubscribeCardToNotifyNewCard(Context mContext) {

        ArrayList<Card> newCard = CardManager.getInstance().getNewCardL(mContext);
        Card card = new Card();
        card.id = CardManager.CARD_ID_SUBSCRIBE;
        card.type = CardManager.CARD_SUBSCRIBE_TYPE;
        boolean hasAlreadyIn = false;
        for (Card card1 : newCard) {
            if (card1.id == CardManager.CARD_ID_SUBSCRIBE) {
                hasAlreadyIn = true;
                break;
            }
        }

        if (!hasAlreadyIn)
            newCard.add(card);

        String cardString = "";
        for (int i = 0; i < newCard.size(); i++) {
            cardString += newCard.get(i).id;
            if (i != newCard.size()) {
                cardString += ",";
            }
        }

        CardManager.getInstance().setNotifyNewCardIdL(mContext, cardString);

    }


    /**
     * @desc 将小说阅读卡片设置为默认展示卡片
     * @author linliangbin
     * @time 2017/8/9 10:35
     */
    public static final void addBookshelCardToDefault(Context context) {

        if (!CardManager.getInstance().isInCurrentCard(context, CardManager.CARD_BOOKSHELF_TYPE)) {
            Card bookCard = CardManager.getInstance().getCardByType(context, CardManager.CARD_BOOKSHELF_TYPE);
            if (bookCard != null) {
                CardManager.getInstance().addCard(context, bookCard);
            }
        }


    }
}
