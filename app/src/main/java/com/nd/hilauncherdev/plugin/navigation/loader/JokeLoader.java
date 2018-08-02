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
import com.nd.hilauncherdev.plugin.navigation.bean.Joke;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

public class JokeLoader extends CardDataLoader {
	private static final String NAVI_FILE = "navi_card_joke.txt";
	private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;

	public static final int ONE_BATCH_SIZE = 1;
	public static final int BATCH_MIN_SIZE = ONE_BATCH_SIZE * 5;

	private static final String JOKE_CARD_CUR_INDEX = "joke_card_cur_index";
    private static final String JOKE_PAGE_INDEX = "joke_page_index";

	public int curIndex = 0;
    public static final int MAX_PAGE =50;
	public static final int PAGE_COUNT = 10;
	public static final int CACHE_START_LIMIT = PAGE_COUNT/2;
	private ArrayList<Joke> curJokeList = new ArrayList<Joke>();
	// 上一次显示的数据内容，当不需要刷新数据时使用
	private ArrayList<Joke> preJokeList = new ArrayList<Joke>();

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_joke_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if(CommonLauncherControl.DX_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/823ca375146241a493de87319ba9d8eb.jpg";
		}
		if(CommonLauncherControl.AZ_PKG){
			return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/4af3153a844e4afdb1ef350894db7a8f.png";
		}
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/26913f79f9c7448a84a97c435bae10ad.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "要知道，用@" + CommonLauncherControl.LAUNCHER_NAME + " 看开心一刻的人，运气都不会太差！";
	}

	@Override
	public String getCardShareAnatics() {
		return "xhdz";
	}

	@Override
	public String getCardDetailImageUrl() {
		return  "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/0ececf3b7f2a4b18830ebfd801a3a575.png";
	}

	@Override
	public void loadDataS(final Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;

		//当前是否在使用默认数据
		boolean isUsingDefaultData= false;
		
		if (!needRefreshData && preJokeList.size() >= ONE_BATCH_SIZE) {
			// 不需要刷新新数据
			ArrayList<Joke> jrttBeanList = new ArrayList<Joke>();
			for (Joke jrttBean : preJokeList) {
				jrttBeanList.add(jrttBean);
			}
			refreshView(jrttBeanList);
			return;
		}
		if (curIndex >= 0 && curIndex < curJokeList.size()) {
			ArrayList<Joke> objList = new ArrayList<Joke>();
			objList.add(curJokeList.get(curIndex));
			refreshView(objList);
			curIndex ++;
			setJokeCardCurIndex(context, curIndex);
			if(curIndex == CACHE_START_LIMIT){
				loadDataFromServer(mContext, card, false);
			}
			return;
		}
		
		if(curIndex >= 0 && curIndex < curJokeList.size() && curJokeList.size() > 0){
			curIndex = 0;
			setJokeCardCurIndex(context,curIndex);
		}
		if(curIndex < 0){
			curIndex = 0;
        	setJokeCardCurIndex(context,curIndex);
		}
		
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		if (TextUtils.isEmpty(localStr)) {
			isUsingDefaultData = true;
			FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
			localStr = FileUtil.readFileContent(NAVI_CARD_PATH);
		}

		try {
			ArrayList<Joke> objLocalList = new ArrayList<Joke>();
			JSONObject jo = new JSONObject(localStr);
			JSONArray ja = jo.getJSONArray("Data");
			if (ja.length() > 0) {
                curJokeList.clear();
                for (int i = 0; i < ja.length(); i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					Joke obj = new Joke(dataJo);
                    curJokeList.add(obj);
				}
			}else{

            }
			//出现问题时，请空文件以保证能够显示默认的笑话
			if(curJokeList != null && curJokeList.size() ==0){
				FileUtil.writeFile(NAVI_CARD_PATH, "", false);
			}
			curIndex = getJokeCardCurIndex(context);
            if(curIndex >= curJokeList.size() || curIndex < 0)
                curIndex = 0;
            objLocalList.add(curJokeList.get(curIndex));
            curIndex ++;
            setJokeCardCurIndex(context,curIndex);
			refreshView(objLocalList);
		} catch (Exception e) {
			e.printStackTrace();
			//RETRY: 本地文件出现问题时，重新拷贝默认文件 解决升级时段子第一次内容显示为空的问题
			try {
				String localStrRetry = "";
				isUsingDefaultData = true;
				FileUtil.copyPluginAssetsFile(context, NAVI_FILE, NaviCardLoader.NAV_DIR, NAVI_FILE);
				localStrRetry = FileUtil.readFileContent(NAVI_CARD_PATH);
				ArrayList<Joke> objLocalList = new ArrayList<Joke>();
				JSONObject joRetry = new JSONObject(localStrRetry);
				JSONArray ja = joRetry.getJSONArray("Data");
				if (ja.length() > 0) {
					curJokeList.clear();
					for (int i = 0; i < ja.length(); i++) {
						JSONObject dataJo = ja.getJSONObject(i);
						Joke obj = new Joke(dataJo);
						curJokeList.add(obj);
					}
				}
				curIndex = getJokeCardCurIndex(context);
				if(curIndex >= curJokeList.size() || curIndex < 0)
					curIndex = 0;
				objLocalList.add(curJokeList.get(curIndex));
				curIndex ++;
				setJokeCardCurIndex(context,curIndex);
				refreshView(objLocalList);
			}catch (Exception ee){
				ee.printStackTrace();
			}
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
			JSONObject paramsJO = new JSONObject();
			int index = getPageIndex(context);
			if(index < 0 || index > MAX_PAGE){
				index = 0;
			}
			try{
				paramsJO.put("PageIndex",index);
				paramsJO.put("PageSize",PAGE_COUNT);
			}catch (Exception e){
				e.printStackTrace();
			}
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9021");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null || !csResult.isRequestOK()) {
				return;
			}
			String resString = csResult.getResponseJson();
			try {
				JSONObject jo = new JSONObject(resString);
				ArrayList<Joke> objList = new ArrayList<Joke>();
				ArrayList<Joke> objLocalList = new ArrayList<Joke>();
				ArrayList<Joke> tempList = new ArrayList<Joke>();
				JSONObject resJo = new JSONObject(resString);
				JSONArray ja = resJo.getJSONArray("Data");
				for (int i = 0; i < ja.length(); i++) {
					JSONObject dataJo = ja.getJSONObject(i);
					Joke obj = new Joke(dataJo);
					objLocalList.add(obj);
					tempList.add(obj);
				}
		
				if (needRefreshView) {
					curIndex = getJokeCardCurIndex(context);
					if(curIndex >= objLocalList.size())
						curIndex = 0;
					for (int i = curIndex; i < curIndex + ONE_BATCH_SIZE && i < objLocalList.size() && i >=0; i++) {
						objList.add(objLocalList.get(i));
					}
					curIndex += ONE_BATCH_SIZE;
					setJokeCardCurIndex(context,curIndex);
					refreshView(objList);
					curJokeList.clear();
				}
				if(tempList != null && tempList.size() >0){
					FileUtil.writeFile(NAVI_CARD_PATH, jo.toString(), false);
					setPageIndex(context, ++index);
				}else{
					//请求数据出现问题时，重新访问第一页
					setPageIndex(context,1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public <E> void refreshView(ArrayList<E> objList) {
		if (handler == null) {
			return;
		}

		preJokeList.clear();
		for (int i = 0; i < objList.size(); i++) {
			Joke obj = (Joke) objList.get(i);
			preJokeList.add(obj);
		}

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_JOKE_SUC;
		msg.obj = objList;
		handler.sendMessage(msg);
	}


	public void setJokeCardCurIndex(Context context, int value) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putInt(JOKE_CARD_CUR_INDEX, value).commit();
	}

	public int getJokeCardCurIndex(Context context) {
		SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
		return sp.getInt(JOKE_CARD_CUR_INDEX, 0);
	}

    public void setPageIndex(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(JOKE_PAGE_INDEX, value).commit();
    }

    public int getPageIndex(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(JOKE_PAGE_INDEX, 1);
    }


}
