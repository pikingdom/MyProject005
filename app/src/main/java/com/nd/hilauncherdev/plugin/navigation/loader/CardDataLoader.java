package com.nd.hilauncherdev.plugin.navigation.loader;

import java.util.ArrayList;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.widget.BookCardViewHelper;

import android.content.Context;
import android.os.Handler;

public abstract class CardDataLoader {
	public Handler handler;
	public Context mContext;

	/**
	 * @desc 获取卡片分享图的保存路径
	 * @author linliangbin
	 * @time 2017/8/4 18:19
	 */
	public abstract String getCardShareImagePath();

	/**
	 * @desc 获取卡片分享图的下载地址
	 * @author linliangbin
	 * @time 2017/8/4 18:20
	 */
	public abstract String getCardShareImageUrl();

	/**
	 * @desc 获取卡片分享词
	 * @author linliangbin
	 * @time 2017/8/4 18:20
	 */
	public abstract String getCardShareWord();


	/**
	 * @desc 获取卡片分享的统计lable
	 * @author linliangbin
	 * @time 2017/8/4 18:20
	 */
	public abstract String getCardShareAnatics();


	/**
	 * @desc 获取卡片详情页的预览图地址
	 * @author linliangbin
	 * @time 2017/8/4 18:20
	 */
	public abstract String getCardDetailImageUrl();

	public abstract void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize);

	public abstract void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView);

	public  void loadDataFromServerInNeed(final Handler mHandler ,Context context, final Card card, final boolean needRefreshData, int showSize){
		CardDataLoaderFactory.getInstance().getCardLoader(card.type).handler = mHandler;
		int size = 0;
		if(card != null && card.id == CardManager.CARD_ID_BOOK){
			size = BookCardViewHelper.CARD_BOOK_NUM;
		}
		CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(mContext, card, needRefreshData, size);
	}

	public abstract <E> void refreshView(ArrayList<E> objList);

	public void onLauncherStart(final Card card) {
		// 游戏卡片更新数据时同时更新界面
		if(card.id == CardManager.CARD_ID_GAME || card.id == CardManager.CARD_ID_FUNNY || card.id == CardManager.CARD_ID_SUBSCRIBE){
			loadDataFromServer(mContext, card, true);
		}else{
			loadDataFromServer(mContext, card, false);
		}
	}
}
