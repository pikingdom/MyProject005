package com.nd.hilauncherdev.plugin.navigation.widget.searchpage;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdPluginDownloadManager;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.GlobalConst;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.widget.AbsListViewAdapter;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

import java.util.ArrayList;
import java.util.HashSet;

import static com.nd.hilauncherdev.plugin.navigation.widget.navigation.BaseNavigationSearchView.HANDLER_DOWNLOA_AD_FINISH;

/**
 * Created by linliangbin on 2017/8/9 21:04.
 */

public class RefreshAsyncTask extends AsyncTask<Void, Void, Void> {

    private static boolean isRefreshFail = false;
    private Context context;
    private NavigationView2 navigationView2;

    public RefreshAsyncTask(Context context, NavigationView2 navigationView2) {
        this.context = context;
        this.navigationView2 = navigationView2;
    }

    public static void refreshTask(Context context, final NavigationView2 navigationView2) {
        if (TelephoneUtil.isNetworkAvailable(context)) {
            //标题栏热词 推荐网址 网址导航 更新
            NavigationView2.needRefreshCardTitleRecommendWord = true;
            NavigationLoader.getInstance().updateAndRefreshNavigationViewForceWithRefresh(context, navigationView2);
            ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
            cardS = CardManager.getDownCardS(cardS);
            HashSet<Integer> implementCardIdM = CardManager.getImplementCardIdM();
            //当前卡片更新
            for (Card card : cardS) {
                if (!implementCardIdM.contains(new Integer(card.id))) {
                    continue;
                }
                //唯品会卡片没有换一批，所以重新从服务器读取数据
                if (card.type == CardManager.CARD_VPH_AD_TYPE) {
                    CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataFromServer(context, card, true);
                    continue;
                }
                CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataFromServerInNeed(navigationView2.handler, context, card, true, 1);
            }
            navigationView2.onActionRefreshSync();
            isRefreshFail = false;
            if (navigationView2.mListView != null && navigationView2.mAdapter != null) {
                navigationView2.mAdapter.changeRequestStatus(AbsListViewAdapter.STATUS_PULL_LOADING);
                navigationView2.mAdapter.setIsNeedRequestAD(true);
                navigationView2.mAdapter.doRefreshRequest();
            }
        } else {
            isRefreshFail = true;
        }
        PluginUtil.invokeSubmitEvent(NavigationView2.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARDS_PULL_REFRESH, "xl");
    }

    @Override
    protected Void doInBackground(Void... params) {
        refreshTask(context,navigationView2);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        if (isRefreshFail) {
            Toast.makeText(context, R.string.network_not_available, Toast.LENGTH_SHORT).show();
        }
        navigationView2.complete();
    }
}
