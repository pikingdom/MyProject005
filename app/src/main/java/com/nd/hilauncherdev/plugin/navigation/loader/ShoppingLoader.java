package com.nd.hilauncherdev.plugin.navigation.loader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProductsItem;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

public class ShoppingLoader extends CardDataLoader {
	private static final String NAVI_FILE = "navi_card_shopping.txt";
	private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;

	public static final int ONE_BATCH_SIZE_IC = 3;
	public static final int BATCH_MIN_SIZE_IC = ONE_BATCH_SIZE_IC * 2;

	public static final int ONE_BATCH_SIZE_TEXT = 6;
	public static final int BATCH_MIN_SIZE_TEXT = ONE_BATCH_SIZE_TEXT * 2;

	public static final int ONE_BATCH_SIZE = ONE_BATCH_SIZE_IC + ONE_BATCH_SIZE_TEXT;

	private String urlparams = "";
	private int pageIndex = 1;
	public int curIndex = 0;
	private ArrayList<TaobaoProductsItem> preList = new ArrayList<TaobaoProductsItem>();

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_shopping_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/e9685541511b4c108f39ab20375a9aa4.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/bbeb5fc9552444d0b82202354c266b9e.png";
		}
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/90661324e5314cb78fb03314275e6076.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "自从用了@" + CommonLauncherControl.LAUNCHER_NAME + "，再也不用为买什么而发愁了，每天都是双十一呢～ ";
	}

	@Override
	public String getCardShareAnatics() {
		return "gwzn";
	}

	@Override
	public String getCardDetailImageUrl() {
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/3eebdbebe7fe48dd9cc17054762500e6.png";
	}

	@Override
	public void loadDataS(final Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;
		boolean isUseDefaultData = false;
		if (!needRefreshData && preList.size() >= ONE_BATCH_SIZE) {
			ArrayList<TaobaoProductsItem> objList = new ArrayList<TaobaoProductsItem>();
			for (TaobaoProductsItem obj : preList) {
				objList.add(obj);
			}

			refreshView(objList);
			return;
		}

		ArrayList<TaobaoProductsItem> objList = new ArrayList<TaobaoProductsItem>();
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		if (TextUtils.isEmpty(localStr)) {
			isUseDefaultData = true;
			FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
			localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		}

		try {
			ArrayList<TaobaoProductsItem> objLocalList = new ArrayList<TaobaoProductsItem>();
			JSONArray ja = new JSONArray(localStr);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject dataJo = ja.getJSONObject(i);
				objLocalList.add(TaobaoProductsItem.fromJson(dataJo));
			}

			for (int i = curIndex; i < curIndex + ONE_BATCH_SIZE && i < objLocalList.size(); i++) {
				objList.add(objLocalList.get(i));
			}

			curIndex += ONE_BATCH_SIZE;
			if (curIndex + ONE_BATCH_SIZE >= objLocalList.size()) {
				final boolean finalIsUseDefaultData = isUseDefaultData;
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						if(finalIsUseDefaultData){
							loadDataFromServer(context, card, true);
						}else{
							loadDataFromServer(context, card, false);
						}
					}
				});

				curIndex = 0;
			}
			refreshView(objList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
		if (handler == null) {
			return;
		}

		preList.clear();
		for (E obj : objList) {
			preList.add((TaobaoProductsItem) obj);
		}

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_SHOPPING_SUC;
		msg.obj = objList;
		handler.sendMessage(msg);
	}

	@Override
	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			ArrayList<TaobaoProductsItem> icObjList = new ArrayList<TaobaoProductsItem>();
			ArrayList<TaobaoProductsItem> textObjList = new ArrayList<TaobaoProductsItem>();
			JSONObject paramsJO = new JSONObject();
			try {
				paramsJO.put("SlotId", 65604);
				paramsJO.put("LayoutType", 12);
				paramsJO.put("RequestCount", 14);
				if (!TextUtils.isEmpty(urlparams)) {
					paramsJO.put("UrlParam", urlparams);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			String requestUrl = URLs.PANDAHOME_BASE_URL + "action.ashx/distributeaction/5034";
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return;
			}
			
			String resString = csResult.getResponseJson();
			JSONObject jo = null;
			try {
				jo = new JSONObject(resString);
				if (jo != null) {
					int status = jo.getInt("status");
					if (1 == status) {
						String urlImp = jo.getString("url_imp");
						new com.nd.hilauncherdev.plugin.navigation.util.HttpCommon(urlImp).getResponseAsEntityGet(null);
						JSONArray ja = jo.getJSONArray("promoters");
						int resId = jo.optInt("ad_resid",0);
						for (int i = 0; i < ja.length(); i++) {
							TaobaoProductsItem item = new TaobaoProductsItem();
							JSONObject jsonObject = ja.getJSONObject(i);
							item.promoter = jsonObject.optString("promoter");
							item.category = jsonObject.optInt("category");
							item.urlClick = jsonObject.optString("url_click");
							item.imageUrl = jsonObject.optString("img");
							item.cvid = resId;
							icObjList.add(item);
						}
						
						urlparams = jo.getString("url_params");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			paramsJO = new JSONObject();
			try {
				paramsJO.put("ApiType", 1);
				paramsJO.put("PageIndex", pageIndex);
				paramsJO.put("PageSize", 20);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			jsonParams = paramsJO.toString();
			paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			requestUrl = URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9013";
			httpCommon = new LauncherHttpCommon(requestUrl);
			csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return;
			}
			
			resString = csResult.getResponseJson();
			jo = null;
			try {
				jo = new JSONObject(resString);
				String res = jo.optString("Result");
				jo = new JSONObject(res);
				int cardResId = jo.optInt("card_resid");
				JSONArray ja = jo.getJSONObject("tbk_link_words_response").getJSONObject("results").getJSONArray("tbk_link_word");
				for (int i = 0; i < ja.length(); i++) {
					TaobaoProductsItem item = new TaobaoProductsItem();
					JSONObject jsonObject = ja.getJSONObject(i);
					item.urlClick = jsonObject.optString("url");
					item.title = jsonObject.optString("word");
					item.cvid = cardResId;
					textObjList.add(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			// pageIndex++;
			ArrayList<TaobaoProductsItem> resObjList = generateResObjL(icObjList, textObjList);
			if (resObjList.size() > 0 && needRefreshView) {
				refreshView(resObjList);
				curIndex += ONE_BATCH_SIZE;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	protected ArrayList<TaobaoProductsItem> generateResObjL(ArrayList<TaobaoProductsItem> icObjList, ArrayList<TaobaoProductsItem> textObjList) {
		ArrayList<TaobaoProductsItem> totalObjList = new ArrayList<TaobaoProductsItem>();
		if (icObjList.size() < BATCH_MIN_SIZE_IC || textObjList.size() < BATCH_MIN_SIZE_TEXT) {
			return totalObjList;
		}

		int i = 0;
		int j = 0;
		JSONArray ja = new JSONArray();
		while (i <= icObjList.size() - ONE_BATCH_SIZE_IC && j <= textObjList.size() - ONE_BATCH_SIZE_TEXT) {
			for (int k = i; k < i + ONE_BATCH_SIZE_IC; k++) {
				totalObjList.add(icObjList.get(k));
				ja.put(icObjList.get(k).toJson());
			}

			i += ONE_BATCH_SIZE_IC;

			for (int k = j; k < j + ONE_BATCH_SIZE_TEXT; k++) {
				totalObjList.add(textObjList.get(k));
				ja.put(textObjList.get(k).toJson());
			}

			j += ONE_BATCH_SIZE_TEXT;
		}

		ArrayList<TaobaoProductsItem> resObjList = new ArrayList<TaobaoProductsItem>();
		for (i = 0; i < ONE_BATCH_SIZE_IC + ONE_BATCH_SIZE_TEXT; i++) {
			resObjList.add(totalObjList.get(i));
		}

		FileUtil.writeFile(NAVI_CARD_PATH, ja.toString(), false);
		return resObjList;
	}
}
