package com.nd.hilauncherdev.plugin.navigation.loader;

import java.util.ArrayList;

import com.nd.hilauncherdev.plugin.navigation.Card;

import android.content.Context;

public class DefaultCardDataLoader extends CardDataLoader {
	@Override
	public String getCardShareImagePath() {
		return null;
	}

	@Override
	public String getCardShareImageUrl() {
		return null;
	}

	@Override
	public String getCardShareWord() {
		return null;
	}

	@Override
	public String getCardShareAnatics() {
		return null;
	}

	@Override
	public String getCardDetailImageUrl() {
		return null;
	}

	public void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize) {
	}

	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
	}
}
