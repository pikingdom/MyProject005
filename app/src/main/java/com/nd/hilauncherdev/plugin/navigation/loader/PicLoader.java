package com.nd.hilauncherdev.plugin.navigation.loader;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.PicBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

public class PicLoader extends CardDataLoader {
	private static final String NAVI_FILE = "navi_card_pic.txt";
	private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;

	private static final String PIC_CARD_CUR_PAGE = "pic_card_cur_page";
	private static final String PIC_CARD_CUR_INDEX = "pic_card_cur_index";
	private static final String JSON_LIST = "List";
	public static final int PAGE_SIZE = 20;
	public static final int ONE_BATCH_SIZE = 1;
	public static final int BATCH_MIN_SIZE = ONE_BATCH_SIZE * 3;
	private static final int SIZE_ZERO_TIMES_MAX = 3;

	public int cursor = 1;
	public int curIndex = 0;
	private int sizeZeroTimes = 0;
	private boolean isInited = false;
	private ArrayList<PicBean> preList = new ArrayList<PicBean>();
	private ArrayList<PicBean> allList = new ArrayList<PicBean>();

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_pic_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/04/25/10154cf2269b48139661d6194da8a30c.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/05/19/5d26ec8b013a452e98c01d3340647509.png";
		}
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/e492a6f5507a48448965427346b73684.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "桌面有佳人，倾国而倾城，晨起看美图，身心都舒服。";
	}

	@Override
	public String getCardShareAnatics() {
		return "jrmt";
	}

	@Override
	public String getCardDetailImageUrl() {
		return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/03/11/fa88fe1e8dc944dbb16a0782ebc28a33.png";
	}

	@Override
	public void loadDataS(final Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;
		boolean isUsingDefaultData = false;
		
		if (!needRefreshData && preList.size() >= ONE_BATCH_SIZE) {
			ArrayList<PicBean> objList = new ArrayList<PicBean>();
			for (PicBean picBean : preList) {
				objList.add(picBean);
			}

			refreshView(objList);
			return;
		}

		ArrayList<PicBean> objList = new ArrayList<PicBean>();
		if (showSize == -1) {
			if (allList.size() < 1) {
				return;
			}

			curIndex = getPicCardCurIndex(context);
			if (curIndex <= 0) {
				curIndex = allList.size() - 1;
			}

			if (curIndex == 1) {
				curIndex = allList.size() + 1;
			}

			for (int i = curIndex - ONE_BATCH_SIZE - 1; i < curIndex - 1 && i < allList.size() && i >= 0; i++) {
				objList.add(allList.get(i));
			}

			curIndex = curIndex - 1;
			setPicCardCurIndex(context, curIndex);
			refreshView(objList);
			return;
		}

		NaviCardLoader.createBaseDir();
		String localStr = "";
		try {
			if (allList.size() == 0) {
				localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
				if (TextUtils.isEmpty(localStr)) {
					isUsingDefaultData = true;
					FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
					localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
				}

				JSONObject jo = new JSONObject(localStr);
				JSONArray ja = new JSONArray(jo.getString(JSON_LIST));
				for (int i = 0; i < ja.length(); i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					PicBean picBean = new PicBean(dataJo);
					allList.add(picBean);
				}

				curIndex = getPicCardCurIndex(context);
				if (curIndex < 0) {
					curIndex = 0;
				}
			}

			if (!isInited) {
				isInited = true;
				if (curIndex > 0) {
					curIndex -= 1;
				}
			}

			for (int i = curIndex; i < curIndex + ONE_BATCH_SIZE && i < allList.size() && i >= 0; i++) {
				objList.add(allList.get(i));
			}

			curIndex += ONE_BATCH_SIZE;
			if (curIndex + ONE_BATCH_SIZE >= allList.size()) {
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						boolean needRefresh = false;
						if (curIndex >= allList.size() + 1) {
							needRefresh = true;
							curIndex = curIndex - 1;
						}

						loadDataFromServer(context, card, needRefresh);
					}
				});
			}

			if (curIndex > allList.size() + ONE_BATCH_SIZE + 2) {
				curIndex = allList.size();
			}

			setPicCardCurIndex(context, curIndex);
			refreshView(objList);
			if(isUsingDefaultData){
				ThreadUtil.executeMore(new Runnable() {
					@Override
					public void run() {
						loadDataFromServer(context,card,true);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
		if (handler == null) {
			return;
		}

		ArrayList<PicBean> objListRes = new ArrayList<PicBean>();
		for (int i = 0; i < objList.size(); i++) {
			objListRes.add((PicBean) objList.get(i));
		}

		preList.clear();
		for (PicBean picBean : objListRes) {
			preList.add(picBean);
		}

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_PIC_SUC;
		msg.obj = objListRes;
		handler.sendMessage(msg);
	}

	@Override
	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			cursor = getPicCardCurPage(context);
			ArrayList<PicBean> objList = new ArrayList<PicBean>();
			JSONObject paramsJO = new JSONObject();
			try {
				paramsJO.put("ImgType", 1);
				paramsJO.put("PageIndex", cursor);
				paramsJO.put("PageSize", PAGE_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9012");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				cursor -= 1;
				if (cursor <= 0) {
					cursor = 1;
				}
				
				setPicCardCurPage(context, cursor);
				return;
			}
			
			cursor += 1;
			setPicCardCurPage(context, cursor);
			String resString = csResult.getResponseJson();
			JSONObject jo = null;
			JSONArray ja = null;
			try {
				jo = new JSONObject(resString);
				ja = new JSONArray(jo.getString(JSON_LIST));
				for (int i = 0; i < ja.length(); i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					PicBean picBean = new PicBean(dataJo);
					objList.add(picBean);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			int newBeanSize = objList.size();
			
			if (objList.size() == 0) {
				sizeZeroTimes++;
				cursor = 1;
				setPicCardCurPage(context, cursor);
				if (sizeZeroTimes < SIZE_ZERO_TIMES_MAX) {
					loadDataFromServer(context, card, true);
				} else {
					sizeZeroTimes = 0;
				}
				
				return;
			}
			
			if (objList.size() >= BATCH_MIN_SIZE) {
				JSONObject saveJo = new JSONObject();
				JSONArray saveJa = new JSONArray();
				try {
					String localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
					JSONArray localJa = new JSONObject(localStr).getJSONArray(JSON_LIST);
					if (localJa != null) {
						for (int i = 0; i < localJa.length(); i++) {
							saveJa.put(localJa.getJSONObject(i));
						}
					}
					
					for (int i = 0; i < ja.length(); i++) {
						saveJa.put(ja.getJSONObject(i));
					}
					
					saveJo.put(JSON_LIST, saveJa);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FileUtil.writeFile(NAVI_CARD_PATH, saveJo.toString(), false);
				for (PicBean picBean : objList) {
					allList.add(picBean);
				}
				
				if (allList.size() > 79) {
					try {
						String localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
						JSONArray localJa = new JSONObject(localStr).getJSONArray(JSON_LIST);
						for (int i = 0; i < 20; i++) {
							allList.remove(0);
						}
						
						saveJo = new JSONObject();
						saveJa = new JSONArray();
						for (int i = 20; i < localJa.length(); i++) {
							saveJa.put(localJa.get(i));
						}
						
						saveJo.put(JSON_LIST, saveJa);
						FileUtil.writeFile(NAVI_CARD_PATH, saveJo.toString(), false);
					} catch (Exception e) {
						e.printStackTrace();
					}
					curIndex -= 20;
					if (curIndex < 0) {
						curIndex = 0;
					}
				}
				
				if (needRefreshView) {
					if(allList != null && allList.size() > newBeanSize){
						curIndex = allList.size() - newBeanSize;
					}
					ArrayList<PicBean> objListToShow = new ArrayList<PicBean>();
					for (int i = curIndex; i < curIndex + ONE_BATCH_SIZE && i < allList.size(); i++) {
						if (i >= 0) {
							objListToShow.add(allList.get(i));
						}
					}
					
					curIndex += ONE_BATCH_SIZE;
					refreshView(objListToShow);
				}
				
				setPicCardCurIndex(context, curIndex);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public void setPicCardCurPage(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(PIC_CARD_CUR_PAGE, value).commit();
	}

	public int getPicCardCurPage(Context context) {
		if(context == null)
			return 1;
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(PIC_CARD_CUR_PAGE, 1);
	}

	public void setPicCardCurIndex(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(PIC_CARD_CUR_INDEX, value).commit();
	}

	public int getPicCardCurIndex(Context context) {
		if(context == null)
			return 0;
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(PIC_CARD_CUR_INDEX, 0);
	}
}
