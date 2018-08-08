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
import com.nd.hilauncherdev.plugin.navigation.bean.TravelBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

public class TravelDataLoader extends CardDataLoader {

	private static final String NAVI_FILE = "navi_card_travel.txt";
	private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;

	public static final int MENU_NUM = 5;
	public static final int PIC_NUM = 2;
	public static final int TEXT_NUM = 2;

	private ArrayList<TravelBean> preList = new ArrayList<TravelBean>();

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_travel_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/04/25/ae33b22cd0a74fcaa3439bf20c1e2219.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/05/19/660e1c82550b434fbd1457ae08de54ab.png";
		}
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/048ea2f9e07e4f2a84cfcc1d956f0add.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "一场说走就走的旅行，从" + CommonLauncherControl.LAUNCHER_NAME + "开始！ " ;
	}

	@Override
	public String getCardShareAnatics() {
		return "lycx";
	}

	@Override
	public String getCardDetailImageUrl() {
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/6a14ae34378d4c86b58638402f381563.png";
	}

	public void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize) {
		if (!needRefreshData && preList.size() > 0) {
			ArrayList<TravelBean> objList = new ArrayList<TravelBean>();
			for (TravelBean obj : preList) {
				objList.add(obj);
			}

			refreshView(objList);
			return;
		}

		ArrayList<TravelBean> objList = new ArrayList<TravelBean>();
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		if (TextUtils.isEmpty(localStr)) {
			FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
			localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		}

		try {
			ArrayList<TravelBean> objLocalList = new ArrayList<TravelBean>();
			JSONArray ja = new JSONArray(localStr);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject dataJo = ja.getJSONObject(i);
				TravelBean travelBean = new TravelBean(dataJo);
				objLocalList.add(travelBean);
			}

			for (int i = 0; i < objLocalList.size(); i++) {
				objList.add(objLocalList.get(i));
			}

			refreshView(objList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			ArrayList<TravelBean> objList = new ArrayList<TravelBean>();
			JSONObject paramsJO = new JSONObject();
			try {
				paramsJO.put("Type", 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9015");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return;
			}
			
			String resString = csResult.getResponseJson();
			try {
				JSONArray ja = new JSONArray(resString);
				for (int i = 0; i < ja.length() && i < MENU_NUM; i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					TravelBean travelBean = new TravelBean(dataJo);
					travelBean.type = TravelBean.TYPE_MENU;
					objList.add(travelBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			ArrayList<TravelBean> objListToShow = new ArrayList<TravelBean>();
			if (objList.size() >= MENU_NUM || objList.size() >= MENU_NUM - 2) {
				for (int i = 0; i < MENU_NUM && i < objList.size(); i++) {
					objListToShow.add(objList.get(i));
				}
			} else {
				return;
			}
			
			objList = new ArrayList<TravelBean>();
			paramsJO = new JSONObject();
			try {
				paramsJO.put("Type", 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			jsonParams = paramsJO.toString();
			paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9015");
			csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return;
			}
			
			resString = csResult.getResponseJson();
			try {
				JSONArray ja = new JSONArray(resString);
				for (int i = 0; i < ja.length() && i < PIC_NUM + TEXT_NUM; i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					TravelBean travelBean = new TravelBean(dataJo);
					if (i < PIC_NUM) {
						travelBean.type = TravelBean.TYPE_PIC;
					} else {
						travelBean.type = TravelBean.TYPE_TEXT;
					}
					objList.add(travelBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
			if (objList.size() >= PIC_NUM + TEXT_NUM) {
				for (int i = 0; i < PIC_NUM + TEXT_NUM && i < objList.size(); i++) {
					objListToShow.add(objList.get(i));
				}
			} else {
				return;
			}
			
			JSONArray saveJa = new JSONArray();
			for (int i = 0; i < objListToShow.size(); i++) {
				JSONObject jo = objListToShow.get(i).toJson();
				saveJa.put(jo);
			}
			
			FileUtil.writeFile(NAVI_CARD_PATH, saveJa.toString(), false);
			if (needRefreshView) {
				refreshView(objListToShow);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public <E> void refreshView(ArrayList<E> objList) {
		if (handler == null) {
			return;
		}

		ArrayList<TravelBean> objListRes = new ArrayList<TravelBean>();
		for (int i = 0; i < objList.size(); i++) {
			objListRes.add((TravelBean) objList.get(i));
		}

		preList.clear();
		for (TravelBean obj : objListRes) {
			preList.add(obj);
		}

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_TRAVEL_SUC;
		msg.obj = objListRes;
		handler.sendMessage(msg);
	}

}
