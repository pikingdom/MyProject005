package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;

import java.util.ArrayList;

/**
 * 空的实时热点卡片数据loader
 * Created by linliangbin on 2017/8/4 16:40.
 */

public class HotwordLoaderStub extends CardDataLoader {
    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_hot_word_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if (CommonLauncherControl.DX_PKG) {
            return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/6b863c24e9f9404e9e7f3e71a157b9c9.jpg";
        }
        if (CommonLauncherControl.AZ_PKG) {
            return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/667c477a8ae94a18ae87fe87852d8c79.png";
        }
        return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/2a2fc54cf48842d8a0528caf29dfd249.jpg";
    }

    @Override
    public String getCardShareWord() {
        return "ssrd";
    }

    @Override
    public String getCardShareAnatics() {
        return null;
    }

    @Override
    public String getCardDetailImageUrl() {
        return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/897c35f9f6aa43269dd2f6872a3f8977.png";
    }

    @Override
    public void loadDataS(Context context, Card card, boolean needRefreshData, int showSize) {

    }

    @Override
    public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {

    }

    @Override
    public <E> void refreshView(ArrayList<E> objList) {

    }
}
