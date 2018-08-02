package com.nd.hilauncherdev.plugin.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.activity.CardAddSubAct;
import com.nd.hilauncherdev.plugin.navigation.activity.CardDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.activity.NavigationSiteDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.bean.WordBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviSiteLoader;
import com.nd.hilauncherdev.plugin.navigation.share.SharedPopWindow;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.BookCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.CardInnerViewHelperBase;
import com.nd.hilauncherdev.plugin.navigation.widget.CardViewFactory;
import com.nd.hilauncherdev.plugin.navigation.widget.DefaultCardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.NaviSiteCardBaseView;
import com.nd.hilauncherdev.plugin.navigation.widget.NaviWordCardBaseView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;
import com.nd.hilauncherdev.plugin.navigation.widget.UiHelper;
import com.nd.hilauncherdev.plugin.navigation.widget.searchpage.ThemeOperator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * 生成、操作卡片view
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CardViewHelper {

	public static final int MSG_LOAD_JRTT_SUC = 10000;
	public static final int MSG_LOAD_MY_BOOK_SUC = 10001;
	public static final int MSG_LOAD_RECOMMEND_BOOK_SUC = 10002;
	public static final int MSG_LOAD_PIC_SUC = 10003;
	public static final int MSG_LOAD_STAR_SUC = 10004;
	public static final int MSG_LOAD_SHOPPING_SUC = 10005;
	public static final int MSG_LOAD_TRAVEL_SUC = 10006;
	public static final int MSG_LOAD_JOKE_SUC = 10007;
	public static final int MSG_LOAD_VPH_AD_SUC = 10008;
	public static final int MSG_LOAD_GAME_SUC = 10009;
	public static final int MSG_LOAD_FUNNY_SUC = 10010;
	public static final int MSG_LOAD_SUBSCRIBE_SITE_SUC = 10011;
	public static final int MSG_LOAD_SUBSCRIBE_ARTICLE_SUC = 10012;
	public static final int MSG_LOAD_BOOKSHELF_SUC = 10013;


	public static final String SITE_ID_WORD_RECOMMEND = "30003000";

	private static final int MANAGE_OP_DEL = 0;
	private static final int MANAGE_OP_TOP = 1;
	@SuppressWarnings("unused")
	private static final int MANAGE_OP_SHARE = 2;
	private static final int FIRST_CARD_INDEX = 3;

	private static final String NAVI_CARD_WORD_FILE = "card_word_recommend.txt";
	private static final String NAVI_CARD_WORD_PATH = NaviCardLoader.NAV_DIR + NAVI_CARD_WORD_FILE;

	private Context context;
	private Handler handler;

	public DefaultCardViewHelper defaultCardViewHelper;

	private HashMap<Integer,CardInnerViewHelperBase> viewHelperMap = new HashMap<Integer, CardInnerViewHelperBase>();

	public CardInnerViewHelperBase getCardViewHelper(Card card) {
		if (viewHelperMap == null) {
			return null;
		}
		CardInnerViewHelperBase instance = viewHelperMap.get(new Integer(card.type));
		return instance;
	}


	public void setCardViewHelper(Card card, CardInnerViewHelperBase instance) {
		if (viewHelperMap == null) {
			viewHelperMap = new HashMap<Integer, CardInnerViewHelperBase>();
		}
		viewHelperMap.put(card.type, instance);
	}


	public View defaultCardView;
	private NavigationView2 navigationView;
	private LinearLayout cardAllV;
	public LayoutInflater inflater;
	private HashMap<Integer, View> cardIdViewMap = new HashMap<>();
	private HashMap<Integer, ArrayList<WordBean>> cardIdWordMap = new HashMap<>();

	private Animation manageShowAnimation;
	private Animation manageHideAnimation;
	private Animation cardDisappearAnimation;
	private Animation cardTopAnimation;

	private int user_range_below = 100000;
	private int user_range_up = 900000;

	public CardViewHelper(Context context, Handler handler, NavigationView2 navigationView, LayoutInflater inflater) {
		this.context = context;
		this.handler = handler;
		this.navigationView = navigationView;
		defaultCardView = new View(context);
		cardAllV = (LinearLayout) navigationView.findViewById(R.id.navigation_card_all);
		this.inflater = inflater;
		manageShowAnimation = AnimationUtils.loadAnimation(context, R.anim.card_manage_show);
		manageHideAnimation = AnimationUtils.loadAnimation(context, R.anim.card_manage_hide);
		cardDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.card_disappear);
		cardTopAnimation = AnimationUtils.loadAnimation(context, R.anim.card_top);
	}

	public View getCardView(Card card, boolean fromCache, boolean needRefreshData) {
		View cardOuterTotalView = null;
		View baseCardV = null;
		if (cardIdViewMap.containsKey(card.id) && fromCache) {
			cardOuterTotalView = cardIdViewMap.get(card.id);
		} else {
			baseCardV = inflater.inflate(R.layout.navigation_card, null);
			cardOuterTotalView = baseCardV;
		}

		if (baseCardV != null) {
			LinearLayout cardInnerTotalV = (LinearLayout) cardOuterTotalView.findViewById(R.id.navigation_card_base);
			switch (card.type) {
			case CardManager.CARD_SITE_TYPE:
				View siteCardBaseV = inflater.inflate(R.layout.navigation_site_card, null);
				cardInnerTotalV.addView(siteCardBaseV);
				navigationView.naviSiteCardBaseView = (NaviSiteCardBaseView) siteCardBaseV;
				navigationView.naviSiteCardView = cardOuterTotalView;
				navigationView.naviSiteCardBaseView.navigationView = navigationView;
				NaviSiteLoader.loadSiteCard(context, navigationView);
				break;
			case CardManager.CARD_HOT_WORD_TYPE:
				View wordCardBaseV = inflater.inflate(R.layout.navigation_word_card, null);
				cardInnerTotalV.addView(wordCardBaseV);
				navigationView.naviWordCardBaseView = (NaviWordCardBaseView) wordCardBaseV;
				navigationView.naviWordCardBaseView.navigationView = navigationView;
				navigationView.naviWordCardView = cardOuterTotalView;
				navigationView.naviWordCardBaseView.showItemS();
				break;
			default:
				break;
			}

			CardViewFactory.getInstance().getCardViewHelper(card).generateCardView(this, cardInnerTotalV, card);
			initCardView(baseCardV, card, needRefreshData);
		}

		if (cardOuterTotalView == null) {
			return new View(context);
		}

		View moreV = cardOuterTotalView.findViewById(R.id.navi_card_base_more);
		moreV.setTag(card);
		final View opV = cardOuterTotalView.findViewById(R.id.navi_card_op);
		opV.setVisibility(View.GONE);
		opV.setTag(card);
		View opDelV = cardOuterTotalView.findViewById(R.id.navi_card_op_del);
		opDelV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeCard(v);
			}
		});
		opDelV.setTag(card);
		View opTopV = cardOuterTotalView.findViewById(R.id.navi_card_op_top);
		opTopV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				topCard(v);
			}
		});
		opTopV.setTag(card);
		View shareV = cardOuterTotalView.findViewById(R.id.navi_card_op_share);
		shareV.setTag(card);
		shareV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				shareCard(v);
			}
		});
		shareV.setTag(card);
		cardOuterTotalView.findViewById(R.id.navi_card_base_manage).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dealCardBaseManageClick((ImageView) v, opV);
			}
		});
		if (context.getPackageName().equals(CommonGlobal.BAIDU_LAUNCHER_PKG_NAME)) {
			cardOuterTotalView.findViewById(R.id.navi_card_base_manage).setVisibility(View.GONE);
		}

		if (cardOuterTotalView != null) {
			cardIdViewMap.put(card.id, cardOuterTotalView);
		}

		return cardOuterTotalView;
	}

	/**
	 *
	 * @param cardView 整个卡片的布局
	 * @param card
	 * @param needRefreshData
	 */
	private void initCardView(View cardView, Card card, boolean needRefreshData) {
		((TextView) cardView.findViewById(R.id.navi_card_base_name)).setText(card.name);
		if (card.type == CardManager.CARD_HOT_WORD_TYPE) {
			cardView.findViewById(R.id.navi_card_base_more).setVisibility(View.GONE);
		} else if (card.type == CardManager.CARD_SITE_TYPE) {
			cardView.findViewById(R.id.navi_card_base_more).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, NavigationSiteDetailActivity.class);
					PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_DISTRIBUTE_EFFECT, "gdwz");
					context.startActivity(intent);
				}
			});
		}

		CardViewFactory.getInstance().getCardViewHelper(card).initCardView(cardView, card);
		CardViewFactory.getInstance().getCardViewHelper(card).loadData(navigationView.handler, card, needRefreshData);
	}

	protected void dealCardBaseManageClick(ImageView manageV, final View opV) {
		Card card = (Card) opV.getTag();
		final View opDelV = opV.findViewById(R.id.navi_card_op_del);
		final View opTopV = opV.findViewById(R.id.navi_card_op_top);
		final View shareV = opV.findViewById(R.id.navi_card_op_share);

		if (opV.getVisibility() == View.VISIBLE) {
			manageV.setImageResource(R.drawable.navi_card_manage_normal);
			opV.startAnimation(manageHideAnimation);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					opDelV.setVisibility(View.GONE);
					opTopV.setVisibility(View.GONE);
					shareV.setVisibility(View.GONE);
					opV.setVisibility(View.GONE);
				}
			}, 120);
		} else {
			Iterator<Integer> it = cardIdViewMap.keySet().iterator();
			while (it.hasNext()) {
				View cardView = cardIdViewMap.get(it.next());
				cardView.findViewById(R.id.navi_card_op).setVisibility(View.GONE);
				((ImageView) cardView.findViewById(R.id.navi_card_base_manage)).setImageResource(R.drawable.navi_card_manage_normal);
			}

			manageV.setImageResource(R.drawable.navi_card_manage_pressed);
			opV.startAnimation(manageShowAnimation);
			opV.setVisibility(View.VISIBLE);
			opDelV.setVisibility(View.VISIBLE);
			opTopV.setVisibility(View.VISIBLE);
			shareV.setVisibility(View.VISIBLE);
			LinearLayout opInnerV = (LinearLayout) opV.findViewById(R.id.navi_card_op_inner);
			opInnerV.setVisibility(View.VISIBLE);
			for (int i = 0; i < opInnerV.getChildCount(); i++) {
				View childV = opInnerV.getChildAt(i);
				if (i == MANAGE_OP_DEL) {
					if (card.canBeDeleted) {
						childV.setVisibility(View.VISIBLE);
					} else {
						childV.setVisibility(View.GONE);
						continue;
					}
				}

				if (i == MANAGE_OP_TOP) {
					if (card.position == FIRST_CARD_INDEX) {
						childV.setVisibility(View.GONE);
						continue;
					} else {
						childV.setVisibility(View.VISIBLE);
					}
				}

				ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
				animation.setDuration(100 * (i + 1));
				childV.startAnimation(animation);
			}
		}
	}

	protected void removeCard(View v) {
		Card card = (Card) v.getTag();
		ArrayList<Card> currentCardS = CardManager.getInstance().getCurrentCardS(context);
		ArrayList<Card> cardDownS = CardManager.getDownCardS(currentCardS);
		Card cardToRemove = null;
		for (Card c : cardDownS) {
			if (c.id == card.id) {
				cardToRemove = c;
				break;
			}
		}

		if (cardToRemove != null) {
			final View cardView = cardIdViewMap.get(card.id);
			cardView.startAnimation(cardDisappearAnimation);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					cardAllV.removeView(cardView);
					View topOpV = cardAllV.getChildAt(0).findViewById(R.id.navi_card_op);
					Card card = (Card) topOpV.getTag();
					card.position = FIRST_CARD_INDEX;
					topOpV.setTag(card);

				}
			}, 500);
			cardDownS.remove(cardToRemove);
			ArrayList<Card> cardUpS = CardManager.getUpCardS(currentCardS);
			CardManager.getInstance().saveCardS(context, cardUpS, cardDownS);
		}
	}

	protected void shareCard(View v) {
		navigationView.sharePop = new SharedPopWindow(navigationView.activity, navigationView.context);
		Uri uri;
		if (CommonGlobal.isDianxinLauncher(context)) {
			uri = Uri.parse("file://" + SharedPopWindow.SHARE_IMG_PATH_DX);
		}else {
			uri = Uri.parse("file://" + SharedPopWindow.SHARE_IMG_PATH);
		}
		Card card = (Card) v.getTag();
		String subject = CommonLauncherControl.LAUNCHER_NAME;
		String shareWord = "";
		CardDataLoader dataLoader = CardDataLoaderFactory.getInstance().getCardLoader(card.type);
		if(dataLoader != null){
			String filePath = dataLoader.getCardShareImagePath();
			if(FileUtil.isFileExits(filePath)){
				uri = Uri.parse("file://" + filePath);
			}
			shareWord = dataLoader.getCardShareWord();
			PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_CARD_SHARE, dataLoader.getCardShareAnatics() );
		}else{
			navigationView.sharePop.setSharedContentSpecialWX(CommonLauncherControl.LAUNCHER_NAME, CommonLauncherControl.LAUNCHER_NAME+context.getString(R.string.settings_home_assistance_share_text_other)+CommonLauncherControl.DOWNLOAD_URL, CommonLauncherControl.DOWNLOAD_URL + "", uri,120);
		}
		navigationView.sharePop.setSharedContentSpecialWX(subject, shareWord , CommonLauncherControl.DOWNLOAD_URL + "", uri, 120);
		navigationView.sharePop.showcf(navigationView.activity, navigationView);
	}

	protected void topCard(View v) {
		Card card = (Card) v.getTag();
		ArrayList<Card> currentCardS = CardManager.getInstance().getCurrentCardS(context);
		ArrayList<Card> cardDownS = CardManager.getDownCardS(currentCardS);
		Card cardToTop = null;
		for (Card c : cardDownS) {
			if (c.id == card.id) {
				cardToTop = c;
				break;
			}
		}

		if (cardToTop != null) {
			final View cardView = cardIdViewMap.get(card.id);
			cardView.startAnimation(cardDisappearAnimation);
			final Card cardToTopFinal = cardToTop;
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					cardAllV.removeAllViews();
					View cardNewView = null;
					ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
					cardS = CardManager.getDownCardS(cardS);
					HashSet<Integer> implementCardIdM = CardManager.getImplementCardIdM();
					for (int i = 0; i < cardS.size(); i++) {
						Card card = cardS.get(i);
						View view = null;
						if (i == 0 || i == 1) {
							view = getCardView(card, false, false);
						} else {
							view = getCardView(card, true, false);
						}

						if (view == null) {
							continue;
						}

						if (!implementCardIdM.contains(new Integer(card.id))) {
							continue;
						}

						if (cardToTopFinal.id == card.id) {
							cardNewView = view;
						}

						cardAllV.addView(view);
					}

					showCardRecommendWord();
					cardNewView.setVisibility(View.INVISIBLE);
					notifyNewCard(cardAllV);
				}
			}, 500);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					//修改零屏卡片底部显示资讯情况置顶卡片动画消失问题
					try {
						if(NavigationView2.isShowNewsBelowCard){
							navigationView.mListView.setSelection(0);
						}else {
							((ScrollView) navigationView.findViewById(R.id.navigationLayout)).scrollTo(0, 0);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					cardAllV.getChildAt(0).setVisibility(View.VISIBLE);
					cardAllV.getChildAt(0).startAnimation(cardTopAnimation);
					navigationView.refreshPaintAndView();
				}
			}, 600);
			cardDownS.remove(cardToTop);
			cardDownS.add(0, cardToTop);
			ArrayList<Card> cardUpS = CardManager.getUpCardS(currentCardS);
			CardManager.getInstance().saveCardS(context, cardUpS, cardDownS);
		}
	}

	@SuppressWarnings("rawtypes")
	public void showCardRecommendWord() {
		String str = FileUtil.readFileContent(NAVI_CARD_WORD_PATH);
		try {
			if (TextUtils.isEmpty(str)) {
				return;
			}

			JSONObject jo = new JSONObject(str);
			JSONArray resultJa = jo.getJSONArray("ResultList");
			for (int i = 0; i < resultJa.length(); i++) {
				JSONObject cardJo = resultJa.getJSONObject(i);
				Integer cardId = cardJo.getInt("CardId");
				JSONArray wordJa = cardJo.getJSONArray("Words");
				ArrayList<WordBean> wordBeanS = new ArrayList<>();
				for (int j = 0; j < wordJa.length(); j++) {
					WordBean wordBean = new WordBean(wordJa.getJSONObject(j));
					wordBeanS.add(wordBean);
				}

				cardIdWordMap.put(cardId, wordBeanS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (cardIdWordMap.size() == 0) {
			return;
		}

		for (Iterator it = cardIdViewMap.keySet().iterator(); it.hasNext();) {
			final Integer cardId = (Integer) it.next();
			View view = cardIdViewMap.get(cardId);
			if (cardIdWordMap.containsKey(cardId)) {
				final ArrayList<WordBean> allWordBeanS = cardIdWordMap.get(cardId);
				final ArrayList<WordBean> wordBeanS = new ArrayList<WordBean>();
				int curIndex = -1;
				//网址导航卡片 推荐热词轮播
				if(cardId == CardManager.CARD_ID_SITE){
					curIndex = CardManager.getInstance().getRecommendWordIndex(context);
					if(NavigationView2.needRefreshCardTitleRecommendWord){
						curIndex += 1;
						NavigationView2.needRefreshCardTitleRecommendWord = false;
					}
				}

				if(curIndex < 0 || curIndex >= allWordBeanS.size()){
					curIndex = 0;
				}
				//保存网址导航卡片标题栏处推荐热词index
				if(cardId == CardManager.CARD_ID_SITE){
					CardManager.getInstance().setRecommendWordIndex(context,curIndex);
				}
				if(allWordBeanS.size() > 0) {
					wordBeanS.add(allWordBeanS.get(curIndex));
				}
				// 无热词，清空旧的显示
				if(wordBeanS.size() == 0){
					view.findViewById(R.id.navi_card_base_word1).setVisibility(View.GONE);
					view.findViewById(R.id.navi_card_base_word2).setVisibility(View.GONE);
				}
				if (wordBeanS.size() > 0) {
					TextView tv = (TextView) view.findViewById(R.id.navi_card_base_word1);
					view.findViewById(R.id.navi_card_base_word2).setVisibility(View.GONE);
					tv.setTag(wordBeanS.get(0).url);
					tv.setText(wordBeanS.get(0).word);
					tv.setVisibility(View.VISIBLE);
					tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 每个卡片标题栏处的热词点击
							int TaskID = IntegralTaskIdContent.getTaskIDbyCard(CardManager.getCardTypeById(cardId));
							int positionId = getHotWordPositionByCard(cardId);
							if(TaskID != 0 ){
								if(positionId != -1)
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID,
											CvAnalysisConstant.NAVIGATION_SCREEN_INTO,positionId,wordBeanS.get(0).id,CvAnalysisConstant.RESTYPE_LINKS);
								else
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID);
							}
							else{
								if(positionId != -1)
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID,
											CvAnalysisConstant.NAVIGATION_SCREEN_INTO,positionId,wordBeanS.get(0).id,CvAnalysisConstant.RESTYPE_LINKS);
								else
									LauncherCaller.openUrl(context, "", (String) v.getTag());
							}
							PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_REOMMEND_WORD_CLICK, cardId + "_1");
						}
					});
				}

				if (wordBeanS.size() > 1) {
					TextView tv = (TextView) view.findViewById(R.id.navi_card_base_word2);
					tv.setTag(wordBeanS.get(1).url);
					tv.setText(wordBeanS.get(1).word);
					tv.setVisibility(View.VISIBLE);
					tv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int TaskID = IntegralTaskIdContent.getTaskIDbyCard(CardManager.getCardTypeById(cardId));
							int positionId = getHotWordPositionByCard(cardId);
							if(TaskID != 0 ){
								if(positionId != -1)
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID,
											CvAnalysisConstant.NAVIGATION_SCREEN_INTO,positionId,wordBeanS.get(1).id,CvAnalysisConstant.RESTYPE_LINKS);
								else
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID);

							}
							else{
								if(positionId != -1)
									LauncherCaller.openUrl(context, "", (String) v.getTag(),TaskID,
											CvAnalysisConstant.NAVIGATION_SCREEN_INTO,positionId,wordBeanS.get(1).id,CvAnalysisConstant.RESTYPE_LINKS);
								else
									LauncherCaller.openUrl(context, "", (String) v.getTag());
							}
							PluginUtil.invokeSubmitEvent(navigationView.activity, AnalyticsConstant.NAVIGATION_SCREEN_REOMMEND_WORD_CLICK, cardId + "_2");
						}
					});
				}
			} else {
				view.findViewById(R.id.navi_card_base_word1).setVisibility(View.GONE);
				view.findViewById(R.id.navi_card_base_word2).setVisibility(View.GONE);
			}
		}
	}


	private int getHotWordPositionByCard(int cardId){
		switch (cardId){
			case CardManager.CARD_ID_SITE:
				return CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_NAVIGATION_HOTWORD;
			case CardManager.CARD_ID_HOT_WORD:
				return CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_REALTIME_HOTWORDS;
			case CardManager.CARD_ID_NEWS_JRTT:
				return CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_HEADLINE_HOTWORD;
			case CardManager.CARD_ID_SHOPPING:
				return CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_SHOP_GUIDE_HOTWORD;
		}
		return -1;
	}

	public void loadCardRecommendWordFromServer() {
		String cardID = "";
		HashSet<Integer> avaiableCardS = CardManager.getInstance().getAaviableCardS(context);
		for (Integer id : avaiableCardS) {
			cardID += id + ",";
		}

		JSONObject paramsJO = new JSONObject();
		try {
			paramsJO.put("Type", 2);
			if (!TextUtils.isEmpty(cardID)) {
				cardID = cardID.substring(0, cardID.length() - 1);
			}

			paramsJO.put("CardID", cardID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String jsonParams = paramsJO.toString();
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
		LauncherHttpCommon httpCommon = new LauncherHttpCommon("http://pandahome.ifjing.com/action.ashx/otheraction/9006");
		ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
		if (csResult != null) {
			if (csResult.isRequestOK()) {
				try {
					JSONObject jo = new JSONObject(csResult.getResponseJson());
					JSONArray resultJa = jo.getJSONArray("ResultList");
					for (int i = 0; i < resultJa.length(); i++) {
						JSONObject cardJo = resultJa.getJSONObject(i);
						Integer cardId = cardJo.getInt("CardId");
						JSONArray wordJa = cardJo.getJSONArray("Words");
						ArrayList<WordBean> wordBeanS = new ArrayList<>();
						for (int j = 0; j < wordJa.length(); j++) {
							WordBean wordBean = new WordBean(wordJa.getJSONObject(j));
							wordBeanS.add(wordBean);
						}

						cardIdWordMap.put(cardId, wordBeanS);
					}

					FileUtil.writeFile(NAVI_CARD_WORD_PATH, jo.toString(), false);
					handler.post(new Runnable() {

						@Override
						public void run() {
							showCardRecommendWord();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}


	}

	public void processMsg(Message msg) {
		Card card = null;
		switch (msg.what) {
		case CardViewHelper.MSG_LOAD_JRTT_SUC:
			card = new Card();
			card.type = CardManager.CARD_NEWS_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_PIC_SUC:
			card = new Card();
			card.type = CardManager.CARD_PIC_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_STAR_SUC:
			card = new Card();
			@SuppressWarnings("unchecked")
			ArrayList<String> objL = (ArrayList<String>) msg.obj;
			card.id = Integer.valueOf(objL.get(0));
			card.type = CardManager.CARD_STAR_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_MY_BOOK_SUC:
			card = new Card();
			card.type = CardManager.CARD_BOOK_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_SHOPPING_SUC:
			card = new Card();
			card.type = CardManager.CARD_SHOPPING_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_TRAVEL_SUC:
			card = new Card();
			card.type = CardManager.CARD_TRAVEL_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_JOKE_SUC:
			card = new Card();
			card.type = CardManager.CARD_JOKE_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_VPH_AD_SUC:
			card = new Card();
			card.type = CardManager.CARD_VPH_AD_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_GAME_SUC:
			card = new Card();
			card.type = CardManager.CARD_GAME_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_FUNNY_SUC:
			card = new Card();
			card.type = CardManager.CARD_FUNNY_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_SUBSCRIBE_SITE_SUC:
		case CardViewHelper.MSG_LOAD_SUBSCRIBE_ARTICLE_SUC:
			card = new Card();
			card.type = CardManager.CARD_SUBSCRIBE_TYPE;
			break;
		case CardViewHelper.MSG_LOAD_BOOKSHELF_SUC:
			card = new Card();
			card.type = CardManager.CARD_BOOKSHELF_TYPE;
			break;
		default:
			break;
		}

		if (card != null) {
			CardViewFactory.getInstance().getCardViewHelper(card).processMsg(msg, card);
		}
	}

	/**
	 * 如果存在笑话卡片，则移动到顶部显示
	 * @param cardList
	 */
	private static ArrayList<Card> moveJokeCardtoHead(ArrayList<Card> cardList){
		Card jokeCard = null;
		for(int i = 0 ;i< cardList.size();i++){
			if(cardList.get(i).id == CardManager.CARD_ID_JOKE){
				jokeCard = cardList.get(i);
				break;
			}
		}

		if(jokeCard == null)
			return cardList;
		ArrayList<Card> result = new ArrayList<Card>();
		result.add(jokeCard);
		for(int i=0;i<cardList.size();i++){
			if(cardList.get(i).id != CardManager.CARD_ID_JOKE)
				result.add(cardList.get(i));
		}
		return result;
	}
	/**
	 * 添加卡片新增推荐:卡片介绍的展示方式
	 *
	 * @param cardAllV
	 */
	public void notifyNewCard(LinearLayout cardAllV) {
		// 桌面引导界面选择的主题不是默认的主题，则使用卡片内容方式展示
		if(CardManager.getInstance().getThemeChoose(context) != ThemeOperator.NOTIFY_ADD_STYLE_DEFAULT){
			notifyNewCardWithContent(cardAllV);
			return;
		}
		ArrayList<Card> cardL = CardManager.getInstance().getNewCardL(context);
		for (Card card : cardL) {
			final View convertView = inflater.inflate(R.layout.navi_notiy_card_item, null);
			View allV = convertView.findViewById(R.id.navi_add_card_all);
			ImageView cardIV = (ImageView) convertView.findViewById(R.id.navi_card_ic);
			UiHelper.setCardIc(cardIV, card);
			TextView nameV = (TextView) convertView.findViewById(R.id.navi_list_card_name);
			TextView descV = (TextView) convertView.findViewById(R.id.navi_list_card_desc);
			TextView infoV = (TextView) convertView.findViewById(R.id.navi_list_card_info);
			allV.setTag(card);
			allV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					try {
						Card tagCard = (Card) v.getTag();
						if (tagCard.showType == Card.SHOW_TYPE_SINGLE) {
							Intent intent = new Intent();
							intent.setClass(context, CardDetailActivity.class);
							intent.putExtra(Card.IS_FROM_NEW_CARD_NOTIFY, true);
							intent.putExtra("cardType", tagCard.type);
							intent.putExtra("cardId", tagCard.id);
							intent.putExtra("viaNotify", true);
							context.startActivity(intent);
						} else if (tagCard.showType == Card.SHOW_TYPE_GROUP) {
							Intent intent = new Intent();
							intent.setClass(context, CardAddSubAct.class);
							intent.putExtra(Card.IS_FROM_NEW_CARD_NOTIFY, true);
							intent.putExtra("cardType", tagCard.type);
							intent.putExtra("viaNotify", true);
							context.startActivity(intent);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					}
				}

				);
				infoV.setTag(card);
				infoV.setOnClickListener(new

				OnClickListener() {
					@Override
					public void onClick (View v){

						try{
							Card tagCard = (Card) v.getTag();
							if (tagCard.showType == Card.SHOW_TYPE_SINGLE) {
								submitDefaultNotifyCancleAndAdd(context, tagCard, true);
								CardManager.getInstance().addCard(context, tagCard);
								CardManager.getInstance().addToAddedCardS(context, tagCard.id);
								CardManager.getInstance().setIsCardListChanged(context, true);
								navigationView.initCardV();
							} else if (tagCard.showType == Card.SHOW_TYPE_GROUP) {
								Intent intent = new Intent();
								intent.setClass(context, CardAddSubAct.class);
								intent.putExtra(Card.IS_FROM_NEW_CARD_NOTIFY, true);
								intent.putExtra("cardType", tagCard.type);
								intent.putExtra("viaNotify", true);
								context.startActivity(intent);
							}

							convertView.setVisibility(View.GONE);
							CardManager.getInstance().clearNotifyNewCardId(context, tagCard.id);
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}

				);
				nameV.setText(card.name);
				descV.setText(card.desc);
				convertView.findViewById(R.id.navi_card_cancel).
				setTag(card);
				convertView.findViewById(R.id.navi_card_cancel).
				setOnClickListener(new OnClickListener() {
									   @Override
									   public void onClick(View v) {
										   Card tagCard = (Card) v.getTag();
										   convertView.setVisibility(View.GONE);
										   CardManager.getInstance().clearNotifyNewCardId(context, tagCard.id);
										   submitDefaultNotifyCancleAndAdd(context, tagCard, false);
									   }
								   }

				);
				cardAllV.addView(convertView);
			}
		}


	/**
	 * 添加卡片新增推荐:卡片内容的展示方式
	 *
	 * @param cardAllV
	 */
	public void notifyNewCardWithContent(final LinearLayout cardAllV) {
		ArrayList<Card> cardL = CardManager.getInstance().getNewCardL(context);
		int index = 0;
		for (Card card : cardL) {
			final Card curCard = card;
			final View convertView = inflater.inflate(R.layout.navi_notiy_card_item_sp, null);
			final View cardView = getCardView(card, false, true);
//			//取消底部padding
			cardView.setPadding(0,0,0,0);
			//取消顶部margin
			LinearLayout rl2 = (LinearLayout) cardView.findViewById(R.id.card_title_layout);
			LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) rl2.getLayoutParams();
			rlp.setMargins(rlp.leftMargin,0,rlp.rightMargin,rlp.bottomMargin);
			((RelativeLayout) convertView.findViewById(R.id.card_content)).addView(cardView);
			//获取使用用户数，未设置时随机产生,前两张卡片随机数保存SP
			int user = 0;
			if(index == 0){
				user = CardManager.getInstance().getUserCount(context);
				if(user <= 0) {
					user = user_range_below + (int) (Math.random() * (user_range_up - user_range_below));
					CardManager.getInstance().setUserCount(context,user);
				}
			}else if(index == 1){
				user = CardManager.getInstance().getUserCount2(context);
				if(user <= 0) {
					user = user_range_below + (int) (Math.random() * (user_range_up - user_range_below));
					CardManager.getInstance().setUserCount2(context, user);
				}
			}else {
				user = user_range_below + (int) (Math.random() * (user_range_up - user_range_below));
			}
			index ++;
			((TextView)(convertView.findViewById(R.id.notify_tips_user_count))).setText(user + "");
			// 删除动作
			View opDelV = cardView.findViewById(R.id.navi_card_op_del);
			opDelV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CardManager.getInstance().clearNotifyNewCardId(context, curCard.id);
					startDeleteAnim(cardAllV,convertView);
					submitRecommendCancleAndAdd(context, curCard, false);
				}
			});
			//置顶动作
			View opTopV = cardView.findViewById(R.id.navi_card_op_top);
			opTopV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SystemUtil.makeShortToast(context, R.string.notify_should_add_card_first);
				}
			});
			// 添加动作
			((TextView) convertView.findViewById(R.id.notify_add)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CardManager.getInstance().clearNotifyNewCardId(context, curCard.id);
					CardManager.getInstance().addCard(context, curCard);
					CardManager.getInstance().addToAddedCardS(context, curCard.id);
					CardManager.getInstance().setIsCardListChanged(context, true);
					cardAllV.removeView(convertView);
					navigationView.initCardV();
					submitRecommendCancleAndAdd(context, curCard, true);
				}
			});
			// 取消动作
			((TextView) convertView.findViewById(R.id.notify_cancle)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CardManager.getInstance().clearNotifyNewCardId(context, curCard.id);
					startDeleteAnim(cardAllV,convertView);
					submitRecommendCancleAndAdd(context, curCard, false);

				}
			});
			cardAllV.addView(convertView);
		}


	}

	/**
	 * 播放移除动画并移除
	 * @param removeLayout
	 * @param removeView
	 */
	private void startDeleteAnim(final LinearLayout removeLayout, final View removeView){
		if(cardDisappearAnimation == null){
			cardDisappearAnimation = AnimationUtils.loadAnimation(context, R.anim.card_disappear);
		}
		removeView.startAnimation(cardDisappearAnimation);
		cardDisappearAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				if(removeLayout != null){
					removeLayout.removeView(removeView);
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}


	/**
	 * 默认主题推荐添加打点
	 * @param card 卡片类型
	 *
	 */
	public static void submitDefaultNotifyCancleAndAdd(Context context,Card card,boolean isAdd){
		switch (card.type){
			case CardManager.CARD_PIC_TYPE:
				if(isAdd){
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"mtmt");
				}
				else{
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"msmt");
				}
				break;
			case CardManager.CARD_STAR_TYPE:
				if(isAdd){
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"mtxz");
				}
				else{
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"msxz");
				}
				break;
		}

	}


	/**
	 * 选定男/女生主题后推荐添加打点
	 * @param card 卡片类型
	 * @param isAdd true-添加 false-删除
	 */
	private static void submitRecommendCancleAndAdd(Context context, Card card, boolean isAdd){
		switch (card.type){
			//女生推荐
			case CardManager.CARD_BOOK_TYPE:
				if(isAdd)
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"gtxs");
				else
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"gsxs");
				break;
			case CardManager.CARD_SHOPPING_TYPE:
				if(isAdd)
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"gtgw");
				else
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"gsgw");
				break;
			//男生推荐
			case CardManager.CARD_PIC_TYPE:
				if(isAdd)
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"btmt");
				else
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"bsmt");
				break;
			case CardManager.CARD_GAME_TYPE:
				if(isAdd)
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"btyx");
				else
					PluginUtil.invokeSubmitEvent(NavigationView2.activity,AnalyticsConstant.NAVIGATION_SCREEN_CARDS_RECOMMEND,"bsyx");
				break;

		}

	}


	public void onLauncherStart() {
		long delay = 3 * NavigationView2.MINUTE;// 3分钟
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {

						ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
						for (final Card card : cardS) {
							if (handler == null) {
								continue;
							}
							CardDataLoaderFactory.getInstance().getCardLoader(card.type).onLauncherStart(card);
						}
					}
				});
			}
			// //////////////////////// test
			// }, 1000);
			}, delay);

	}

	public void setTypeface() {
		ArrayList<Card> cardS = CardManager.getInstance().getCurrentCardS(context);
		for (final Card card : cardS) {
			View cardView = null;
			if (card.type == CardManager.CARD_SITE_TYPE) {
				NaviSiteLoader.loadSiteCard(context, navigationView);
				cardView = navigationView.naviSiteCardView;
			}

			if (card.type == CardManager.CARD_HOT_WORD_TYPE) {
				navigationView.naviWordCardBaseView.showItemS();
				cardView = navigationView.naviWordCardView;
			}

			if (card.type == CardManager.CARD_BOOK_TYPE) {
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(context, card, false, BookCardViewHelper.CARD_BOOK_NUM);
			} else {
				CardDataLoaderFactory.getInstance().getCardLoader(card.type).loadDataS(context, card, false, 0);
			}

			if (cardView == null) {
				cardView = CardViewFactory.getInstance().getView(card);
			}

			if (cardView != null) {
				TextView tv = (TextView) cardView.findViewById(R.id.navi_card_base_name);
				if (tv != null) {
					tv.setText(card.name);
				}
			}
		}
	}
}
