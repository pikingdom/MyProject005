package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;

import java.util.ArrayList;

/**
 * 空的网址导航卡片loader
 * Created by linliangbin on 2017/8/4 16:30.
 */

public class NaviSiteLoaderStub extends CardDataLoader {

    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_site_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if (CommonLauncherControl.DX_PKG) {
            return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/aaae1e8e54424c639de462a04a2bfd31.jpg";
        }
        if (CommonLauncherControl.AZ_PKG) {
            return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/a7e4e587760144b296bfede42c76fd74.png";
        }
        return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/2e28380850484e24ae698306a75e112d.jpg";
    }

    @Override
    public String getCardShareWord() {
        return "浏览器输网址实在太慢，用@" + CommonLauncherControl.LAUNCHER_NAME + " 网址导航，轻松简单～ ";
    }

    @Override
    public String getCardShareAnatics() {
        return "wzdh";
    }

    @Override
    public String getCardDetailImageUrl() {
        return null;
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
