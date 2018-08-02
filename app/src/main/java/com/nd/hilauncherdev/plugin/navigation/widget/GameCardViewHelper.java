package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.GameOpenHistoryAct;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.loader.GameH5HistoryLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.GameLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.BitmapUtils;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linliangbin_dian91 on 2015/10/21.
 */
public class GameCardViewHelper extends CardInnerViewHelperBase {

    private int recommendLayoutIDs[] = {R.id.game_card_layout_item1, R.id.game_card_layout_item2, R.id.game_card_layout_item3, R.id.game_card_layout_item4};
    private int h5LayoutIDs[] = {R.id.game_card_h5_layout_item1, R.id.game_card_h5_layout_item2, R.id.game_card_h5_layout_item3, R.id.game_card_h5_layout_item4};
    private int newsTextIDs[] = {R.id.game_card_news_item1, R.id.game_card_news_item2};

    private ArrayList<SoftReference<LinearLayout>> recommendLayout = new ArrayList<SoftReference<LinearLayout>>();
    private ArrayList<SoftReference<LinearLayout>> h5Layout = new ArrayList<SoftReference<LinearLayout>>();
    private ArrayList<SoftReference<TextView>> newsText = new ArrayList<SoftReference<TextView>>();

//    private ArrayList<LinearLayout> recommendLayout = new ArrayList<LinearLayout>();
//    private ArrayList<LinearLayout> h5Layout = new ArrayList<LinearLayout>();
//    private ArrayList<TextView> newsText = new ArrayList<TextView>();

    ArrayList<GameBean> recommendList = new ArrayList<GameBean>();
    ArrayList<GameBean> h5List = new ArrayList<GameBean>();
    ArrayList<GameBean> newsList = new ArrayList<GameBean>();

    private String recommendLastIcon[] = new String[4];
    private String h5LastIcon[] = new String[4];
    private static final int RECOMMEND_TYPE =1 ;
    private static final int H5_TYPE = 2 ;


    public GameCardViewHelper(NavigationView2 navigationView, Card card, Context context) {
        super(navigationView, card, context);
    }

    //H5 游戏监听
    private class H5GameDetailListener implements View.OnClickListener {
        String url;
        int index = -1;
        GameBean gameBean;
        @Override
        public void onClick(View v) {
            //打开H5 游戏页面
            LauncherCaller.openUrl(mContext, "", url,IntegralTaskIdContent.NAVIGATION_HOT_GAMES,
                    CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_CARD_CLICK,
                    CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
            GameH5HistoryLoader.addOpenH5Game(mContext, gameBean);
            PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "h5" + index);
        }
        public void setIndex(int i){ index = i;}
        public void setPkg(String p) { url = p;}
        public void setGame(GameBean game) { gameBean = game;}
    }

    //推荐游戏监听
    private class RecommendGameDetailLisener implements View.OnClickListener {
        String pkg;
        int index = -1;
        @Override
        public void onClick(View v) {
            //跳转到详情页面
            if (TextUtils.isEmpty(pkg))
                return;
            try {
                Intent i = new Intent();
                i.setComponent(new ComponentName(mContext.getPackageName(), "com.nd.hilauncherdev.appstore.AppStoreSwitchActivity"));
                if(CommonGlobal.isDianxinLauncher(mContext)){
                    i.putExtra("MYACTION", 11);
                    i.putExtra("IDENTIFIER", pkg);
                }else{
                    i.putExtra("MYACTION", 1);
                    i.putExtra("IDENTIFIER", pkg);
                    i.putExtra("VERSION",2);
                }
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(i);
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "rm" + index + pkg);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("llbeing", "start game detail activity fail");
            }
        }
        public void setIndex(int i){ index = i;}
        public void setPkg(String p) {
            pkg = p;
        }
    }

    private void showBitmap4Icon(final ImageView icon, final GameBean tempBeam, final View.OnClickListener onClickListener, final int iconIndex, final int type) {

		icon.setImageResource(CommonLauncherControl.getLoadingBackgroad());
        Drawable cachedDrawable = navigationView.asyncImageLoader.loadDrawable(tempBeam.iconUrl, new ImageCallback() {
            @Override
            public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                String targetUrl = "";
                if (type == H5_TYPE) {
                    targetUrl = h5LastIcon[iconIndex];
                } else if (type == RECOMMEND_TYPE) {
                    targetUrl = recommendLastIcon[iconIndex];
                }
                if (imageDrawable != null && !TextUtils.isEmpty(imageUrl) && imageUrl.equals(targetUrl)) {
                    BitmapDrawable bp = (BitmapDrawable) imageDrawable;
                    Bitmap bitmap = BitmapUtils.toRoundCorner(bp.getBitmap(), 15);
                    icon.setImageDrawable(new BitmapDrawable(bitmap));
                    icon.setOnClickListener(onClickListener);
                }
            }
        });
        if(cachedDrawable != null){
            icon.setImageDrawable(cachedDrawable);
            icon.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void showDataS(final NavigationView2 navigationView, Card card, Message msg) {
        ArrayList<ArrayList<GameBean>> allList = (ArrayList<ArrayList<GameBean>>) msg.obj;
        recommendList = allList.get(GameLoader.RECOMMEND_LIST_INDEX);
        h5List = allList.get(GameLoader.H5_LIST_INDEX);
        newsList = allList.get(GameLoader.NEWS_LIST_INDEX);


//        推荐游戏显示
        if (recommendList != null && recommendList.size() >= 0) {
            for (int i = 0; i < recommendLayout.size() && i < recommendList.size(); i++) {
                final GameBean tempBeam = recommendList.get(i);
                LinearLayout currentLayout = null;
                SoftReference<LinearLayout> currentLayoutRefer = recommendLayout.get(i);
                if (currentLayoutRefer != null)
                    currentLayout = currentLayoutRefer.get();
                if (currentLayout == null) {
                    currentLayout = (LinearLayout) getCardView(card).findViewById(recommendLayoutIDs[i]);
                }
                if (currentLayout == null)
                    continue;
                final TextView textView = (TextView) currentLayout.findViewById(R.id.game_download);
                final ImageView icon = (ImageView) currentLayout.findViewById(R.id.game_icon);
				icon.setImageResource(CommonLauncherControl.getLoadingBackgroad());
                icon.setOnClickListener(null);
                TextView name = (TextView) currentLayout.findViewById(R.id.game_name);
                name.setText(tempBeam.gameName);
                recommendLastIcon[i] = tempBeam.iconUrl;
                RecommendGameDetailLisener recommendGameDetailLisener = new RecommendGameDetailLisener();
                recommendGameDetailLisener.setPkg(tempBeam.pkg);
                recommendGameDetailLisener.setIndex(i);
                showBitmap4Icon(icon, tempBeam, recommendGameDetailLisener, i, RECOMMEND_TYPE);
                textView.setOnClickListener(recommendGameDetailLisener);
                name.setOnClickListener(recommendGameDetailLisener);

            }
        }

        //H5游戏显示
        if (h5List != null && h5List.size() >= 0) {
            for (int i = 0; i < h5Layout.size() && i < h5List.size(); i++) {
                final GameBean tempBeam = h5List.get(i);
                LinearLayout currentLayout = null;
                SoftReference<LinearLayout> currentLayoutRefer = h5Layout.get(i);
                if (currentLayoutRefer != null)
                    currentLayout = currentLayoutRefer.get();
                if (currentLayout == null) {
                    currentLayout = (LinearLayout) getCardView(card).findViewById(h5LayoutIDs[i]);
                }
                if (currentLayout == null)
                    continue;
                h5LastIcon[i] = tempBeam.iconUrl;
                final ImageView icon = (ImageView) currentLayout.findViewById(R.id.game_icon);
                icon.setOnClickListener(null);
                TextView name = (TextView) currentLayout.findViewById(R.id.game_name);
                name.setText(tempBeam.gameName);
                TextView textView = (TextView) currentLayout.findViewById(R.id.game_download);
                H5GameDetailListener h5GameDetailListener = new H5GameDetailListener();
                h5GameDetailListener.setPkg(tempBeam.downloadUrl);
                h5GameDetailListener.setIndex(i);
                h5GameDetailListener.setGame(tempBeam);
                textView.setOnClickListener(h5GameDetailListener);
                name.setOnClickListener(h5GameDetailListener);
                showBitmap4Icon(icon, tempBeam, h5GameDetailListener,i,H5_TYPE);
            }
        }

        //资讯显示
        if (newsList != null && newsList.size() >= 0) {
            for (int i = 0; i < newsText.size() && i < newsList.size(); i++) {
                final int currentIndex = i;
                final GameBean tempBeam = newsList.get(i);
                TextView currentText = null;
                SoftReference<TextView> currentTextRefer = newsText.get(i);
                if (currentTextRefer != null)
                    currentText = currentTextRefer.get();
                if (currentText == null) {
                    currentText = (TextView) getCardView(card).findViewById(newsTextIDs[i]);
                }
                if (currentText == null)
                    continue;

                currentText.setText(tempBeam.title);
                currentText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LauncherCaller.openUrl(mContext, "", tempBeam.downloadUrl, IntegralTaskIdContent.NAVIGATION_HOT_GAMES,
                                CvAnalysisConstant.NAVIGATION_SCREEN_INTO,CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_CARD_CLICK,
                                CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_GAME_RES_ID,CvAnalysisConstant.RESTYPE_LINKS);
                        PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "zx" + currentIndex);

                    }
                });
            }
        }

    }

    public void loadData(final Handler handler, final Card card, final boolean needRefreshData) {
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                CardDataLoaderFactory.getInstance().getCardLoader(card.type).handler = handler;
                CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(mContext, card, needRefreshData, 0);
            }
        });
    }


    @Override
    public void initCardView(View cardView, final Card card) {
        ((TextView) cardView.findViewById(R.id.navi_card_base_next)).setText(R.string.game_next);
        ((TextView)cardView.findViewById(R.id.navi_card_base_pre)).setText(R.string.game_history);
        ((TextView) cardView.findViewById(R.id.navi_card_base_more)).setText(R.string.game_more);

        ((TextView)cardView.findViewById(R.id.navi_card_base_pre)).setVisibility(View.VISIBLE);
        cardView.findViewById(R.id.navi_card_base_pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(mContext, GameOpenHistoryAct.class);
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "wdyy");

            }
        });

        cardView.findViewById(R.id.navi_card_base_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(navigationView.handler, card, true);
                PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "hyp");

            }
        });

        cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent();
                    i.setComponent(new ComponentName(mContext.getPackageName(), "com.nd.hilauncherdev.appstore.AppStoreSwitchActivity"));
                    i.putExtra("MYACTION", 10);
                    mContext.startActivity(i);
                    PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_GAME_CARD, "gdyx");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("llbeing", "start game detail activity fail");
                }
            }
        });
    }
    @Override
    public void generateCardView(CardViewHelper cardViewHelper, LinearLayout cardInnerTotalV, Card card) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.navigation_game_card, null);
        ImageView gameIconImageView= (ImageView) view.findViewById(R.id.game_icon);
		gameIconImageView.setImageResource(CommonLauncherControl.getLoadingBackgroad());
        recommendLayout.clear();
        for (int i = 0; i < recommendLayoutIDs.length; i++) {
            LinearLayout current = (LinearLayout) view.findViewById(recommendLayoutIDs[i]);
            recommendLayout.add(new SoftReference<>(current));
        }
        h5Layout.clear();
        for (int i = 0; i < h5LayoutIDs.length; i++) {
            LinearLayout current = (LinearLayout) view.findViewById(h5LayoutIDs[i]);
            ((TextView) current.findViewById(R.id.game_download)).setText(mContext.getString(R.string.game_h5_open));
            h5Layout.add(new SoftReference<>(current));
        }
        newsText.clear();
        for (int i = 0; i < newsTextIDs.length; i++) {
            TextView temp = (TextView) view.findViewById(newsTextIDs[i]);
            newsText.add(new SoftReference<>(temp));
        }

        setCardView(view);
        cardInnerTotalV.addView(view);

    }

    @Override
    public String getCardDisName(Card card) {
        return "rmyx";
    }

    @Override
    public int getCardIcon(Card card) {
        return R.drawable.navi_card_game_ic;
    }
}
