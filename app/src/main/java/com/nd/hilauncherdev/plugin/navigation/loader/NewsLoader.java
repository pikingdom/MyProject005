package com.nd.hilauncherdev.plugin.navigation.loader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.JrttBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

/**
 * 今日头条加载<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NewsLoader extends CardDataLoader {

	private static final String NAVI_NEWS_FILE = "navi_jrtt.txt";
	private static final String NEWS_CARD_CURSOR = "news_card_cursor";
	private static final String NEWS_CARD_SHOW_INDEX = "news_card_show_index";
	private static final String NEWS_CARD_KB_INDEX = "news_card_kb_index";
	private static final String NEWS_CARD_KB_MD5 = "news_card_kb_md5";
	private static final String NAVI_CARD_NEWS_PATH = NaviCardLoader.NAV_DIR + NAVI_NEWS_FILE;

	public static final int PAGE_SIZE = 18;
	public static final int ONE_BATCH_SIZE = 3;
	public static final int BATCH_MIN_SIZE = ONE_BATCH_SIZE * 2;

	public static final String JRTT_SITE_ID = "";

	public long cursor = 0;
	public String KbContentMD5 = "";
	public boolean firstInit = true;
	public int curIndex = 0;
	private ArrayList<JrttBean> preJrttBeanList = new ArrayList<JrttBean>();

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
		@SuppressWarnings("unchecked")
		ArrayList<JrttBean> jrttBeanList = (ArrayList<JrttBean>) objList;
		ArrayList<JrttBean> jrttBeanListRes = new ArrayList<JrttBean>();
		int hasPicIndex = -1;
		for (int i = 0; i < jrttBeanList.size(); i++) {
			JrttBean jrttBean = jrttBeanList.get(i);
			jrttBeanListRes.add(jrttBean);
			if (!TextUtils.isEmpty(jrttBean.imgUrl) && hasPicIndex == -1) {
				hasPicIndex = i;
			}
		}

		if (hasPicIndex != -1) {
			jrttBeanListRes.clear();
			jrttBeanListRes.add(jrttBeanList.get(hasPicIndex));
			for (int i = 0; i < jrttBeanList.size(); i++) {
				if (i != hasPicIndex) {
					jrttBeanListRes.add(jrttBeanList.get(i));
				}
			}
		}

		preJrttBeanList.clear();
		for (JrttBean jrttBean : jrttBeanListRes) {
			preJrttBeanList.add(jrttBean);
		}

		if (handler == null) {
			return;
		}

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_JRTT_SUC;
		msg.obj = jrttBeanListRes;
		handler.sendMessage(msg);
	}

	public static void sendJrttReadInfo(final Context context, final String groupId) {
		ThreadUtil.executeMore(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				JSONObject paramsJO = new JSONObject();
				try {
					paramsJO.put("Udid", TelephoneUtil.getIMEI(context));
					paramsJO.put("Openudid", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
					paramsJO.put("Mac", TelephoneUtil.getMAC(context));
					paramsJO.put("OS", "Android");
					paramsJO.put("OsVersion", Build.VERSION.RELEASE);
					paramsJO.put("OsApi", android.os.Build.VERSION.SDK);
					paramsJO.put("DeviceModel", Build.MODEL);
					paramsJO.put("Resolution", TelephoneUtil.getScreenResolution(context));
					paramsJO.put("DisplayDensity", TelephoneUtil.getDisplayDensity(context));
					paramsJO.put("Carrier", TelephoneUtil.getSimOperator(context));
					paramsJO.put("Language", TelephoneUtil.getLanguage());
					paramsJO.put("GroupID", Long.valueOf(groupId));
				} catch (Exception e) {
					e.printStackTrace();
				}

				String jsonParams = paramsJO.toString();
				HashMap<String, String> paramsMap = new HashMap<String, String>();
				LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
				LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9008");
				httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			}
		});
	}

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_jrtt_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/e23a5be1113e4c60ab45884d39a49fae.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/45595b18fe0c420c9f6c51c9a1fdf592.png";
		}
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/4fb5a01c40a34c328867f1fce928151c.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "嗨，我在用@" + CommonLauncherControl.LAUNCHER_NAME + " 看头条资讯，不装app用卡片，体验真是好！";
	}

	@Override
	public String getCardShareAnatics() {
		return "jrtt";
	}

	@Override
	public String getCardDetailImageUrl() {
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/8aa6192e84d84a22b89b9f7dc5a9e10b.png";
	}

	@Override
	public void loadDataS(final Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;
		
		/**
		 * 当前是否在使用默认数据展示
		 * 若是，则需要获取新数据并刷新界面
		 */
		boolean isUsingDefaultData = false;
		if (!needRefreshData && preJrttBeanList.size() >= ONE_BATCH_SIZE) {
			ArrayList<JrttBean> jrttBeanList = new ArrayList<JrttBean>();
			for (JrttBean jrttBean : preJrttBeanList) {
				jrttBeanList.add(jrttBean);
			}

			refreshView(jrttBeanList);
			return;
		}

		ArrayList<JrttBean> jrttBeanList = new ArrayList<JrttBean>();
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_NEWS_PATH);
		if (TextUtils.isEmpty(localStr)) {
			// 从Assets复制
			isUsingDefaultData = true;
			FileUtil.copyPluginAssetsFile(context, NAVI_NEWS_FILE, NaviCardLoader.NAV_DIR, NAVI_NEWS_FILE);
			localStr = FileUtil.readFileContent(NAVI_CARD_NEWS_PATH);
		}

		try {
			ArrayList<JrttBean> jrttBeanLocalList = new ArrayList<JrttBean>();
			JSONObject jo = new JSONObject(localStr);
			JSONArray newsCardJa = new JSONArray(jo.getString("News"));
			for (int i = 0; i < newsCardJa.length(); i++) {
				JSONObject newsJo = newsCardJa.getJSONObject(i);
				this.cursor = newsJo.getLong("Cursor");
				this.KbContentMD5 = newsJo.optString("KbContentMD5");
				JSONArray dataJa = newsJo.getJSONArray("Datas");
				for (int j = 0; j < dataJa.length(); j++) {
					JrttBean jrttBean = new JrttBean(dataJa.getJSONObject(j));
					jrttBeanLocalList.add(jrttBean);
				}
			}

			if(!firstInit){
				curIndex = getNewsShowIndex(context);
				curIndex += ONE_BATCH_SIZE;
			}else{
				curIndex = getNewsShowIndex(context);
				firstInit = false;
			}
			if (curIndex + ONE_BATCH_SIZE >= jrttBeanLocalList.size() - 1) {
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						loadDataFromServer(context, card, false);
					}
				});
			}

			if (curIndex + ONE_BATCH_SIZE > jrttBeanLocalList.size() || curIndex < 0) {
				curIndex = 0;
			}

			for (int i = curIndex; i < curIndex + ONE_BATCH_SIZE && i < jrttBeanLocalList.size(); i++) {
				jrttBeanList.add(jrttBeanLocalList.get(i));
			}
			setNewsShowIndex(context,curIndex);
			refreshView(jrttBeanList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(isUsingDefaultData){
			ThreadUtil.executeMore(new Runnable() {
				@Override
				public void run() {
					loadDataFromServer(context, card, true);
				}
			});
		}
	}

	@Override
	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			ArrayList<JrttBean> jrttBeanList = new ArrayList<JrttBean>();
			JSONObject paramsJO = new JSONObject();
			long reuqestCursor = getNewsCardCursor(context);
			int requestKbPageIndex = getKbNewsCardIndex(context);
			String requestKbMd5 = getKbNewsCardMd5(context);
			if(reuqestCursor <=0)
				reuqestCursor = 1428562391L;
			try {
				paramsJO.put("Udid", TelephoneUtil.getIMEI(context));
				paramsJO.put("Openudid", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
				paramsJO.put("Mac", TelephoneUtil.getMAC(context));
				paramsJO.put("OS", "Android");
				paramsJO.put("OsVersion", Build.VERSION.RELEASE);
				paramsJO.put("OsApi", android.os.Build.VERSION.SDK);
				paramsJO.put("DeviceModel", Build.MODEL);
				paramsJO.put("Resolution", TelephoneUtil.getScreenResolution(context));
				paramsJO.put("DisplayDensity", TelephoneUtil.getDisplayDensity(context));
				paramsJO.put("Carrier", TelephoneUtil.getSimOperator(context));
				paramsJO.put("Language", TelephoneUtil.getLanguage());
				paramsJO.put("NewsCardID", 1001);
				paramsJO.put("Cursor", reuqestCursor);
				paramsJO.put("KbPageIndex",requestKbPageIndex);
				paramsJO.put("KbContentMD5",requestKbMd5);
				paramsJO.put("PageSize", NewsLoader.PAGE_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9007");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult != null) {
				if (csResult.isRequestOK()) {
					try {
						JSONObject jo = new JSONObject(csResult.getResponseJson());
						JSONArray newsCardJa = new JSONArray(jo.getString("News"));
						long cursor = 0;
						int KbPageIndex = 1024;
						String res_MD5 = "";
						for (int i = 0; i < newsCardJa.length(); i++) {
							JSONObject newsJo = newsCardJa.getJSONObject(i);
							cursor = newsJo.getLong("Cursor");
							KbPageIndex = newsJo.optInt("KbPageIndex");
							res_MD5 = newsJo.optString("KbContentMD5");
							JSONArray dataJa = newsJo.getJSONArray("Datas");
							for (int j = 0; j < dataJa.length(); j++) {
								JrttBean jrttBean = new JrttBean(dataJa.getJSONObject(j));
								jrttBeanList.add(jrttBean);
							}
						}
						setNewsCardCursor(context,cursor);
						setKbNewsCardIndex(context, KbPageIndex);
						setKbNewsCardMd5(context,res_MD5);
						if (jrttBeanList.size() >= BATCH_MIN_SIZE) {
							FileUtil.writeFile(NAVI_CARD_NEWS_PATH, jo.toString(), false);
							if (needRefreshView) {
								curIndex = ONE_BATCH_SIZE;
								ArrayList<JrttBean> jrttBeanListToShow = new ArrayList<JrttBean>();
								for (int i = 0; i < ONE_BATCH_SIZE && i < jrttBeanList.size(); i++) {
									jrttBeanListToShow.add(jrttBeanList.get(i));
								}
								
								refreshView(jrttBeanListToShow);
							}else{
								curIndex = -3;
							}
							setNewsShowIndex(context,curIndex);
						} else {
							String localStr = FileUtil.readFileContent(NAVI_CARD_NEWS_PATH);
							JSONObject localJo = new JSONObject(localStr);
							localJo.getJSONArray("News").getJSONObject(0).put("Cursor", cursor);
							FileUtil.writeFile(NAVI_CARD_NEWS_PATH, localJo.toString(), false);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public void setNewsCardCursor(Context context, long value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putLong(NEWS_CARD_CURSOR, value).commit();
	}

	public long getNewsCardCursor(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getLong(NEWS_CARD_CURSOR, 0L);
	}

	/**
	 * 展示资讯当前的INDEX
	 * @param context
	 * @param index
	 */
	public void setNewsShowIndex(Context context, int index){
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(NEWS_CARD_SHOW_INDEX, index).commit();
	}

	public int getNewsShowIndex(Context context){
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(NEWS_CARD_SHOW_INDEX, 0);
	}

	/**
	 * 腾讯快报分页页码SP
	 * @param context
	 * @param value
	 */
	public void setKbNewsCardIndex(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(NEWS_CARD_KB_INDEX, value).commit();
	}

	public int getKbNewsCardIndex(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(NEWS_CARD_KB_INDEX, 1);
	}

	/**
	 * 腾讯快报内容MD5 SP
	 * @param context
	 * @param value
	 */
	public void setKbNewsCardMd5(Context context, String value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(NEWS_CARD_KB_MD5, value).commit();
	}

	public String getKbNewsCardMd5(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getString(NEWS_CARD_KB_MD5, "");
	}

}
