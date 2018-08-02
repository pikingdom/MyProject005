package com.nd.hilauncherdev.plugin.navigation.loader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.widget.StarCardViewHelper;

public class StarLoader extends CardDataLoader {
	private static final String NAVI_FILE = "navi_card_star";
	private static final String NAVI_CARD_PATH = NaviCardLoader.NAV_DIR + NAVI_FILE;
	private static final String[] MORE_URLS = { "http://m.go108.com.cn/astro/baiyang?91zm", "http://m.go108.com.cn/astro/jinniu?91zm", "http://m.go108.com.cn/astro/shuangzi?91zm",
			"http://m.go108.com.cn/astro/juxie?91zm", "http://m.go108.com.cn/astro/shizi?91zm", "http://m.go108.com.cn/astro/chunv?91zm", "http://m.go108.com.cn/astro/tiancheng?91zm",
			"http://m.go108.com.cn/astro/tianxie?91zm", "http://m.go108.com.cn/astro/sheshou?91zm", "http://m.go108.com.cn/astro/mojie?91zm", "http://m.go108.com.cn/astro/shuiping?91zm",
			"http://m.go108.com.cn/astro/shuangyu?91zm" };

	@Override
	public String getCardShareImagePath() {
		return NaviCardLoader.NAV_DIR + "navigation_star_share.jpg";
	}

	@Override
	public String getCardShareImageUrl() {
		if (CommonLauncherControl.DX_PKG) {
			return "http://pic.ifjing.com/rbpiczy/pic/2016/04/25/59e65f98533346d9a109cecb82a10a20.jpg";
		}
		if (CommonLauncherControl.AZ_PKG) {
			return "http://pic.ifjing.com/rbpiczy/pic/2016/05/19/439192ca23e1483a99a7f5866e891226.png";
		}
		return "http://pic.ifjing.com/rbpiczy/pic/2016/03/11/e03554e339a1452d929c4c36508a215c.jpg";
	}

	@Override
	public String getCardShareWord() {
		return "你知道嘛，每天用@" + CommonLauncherControl.LAUNCHER_NAME + " 关注星座运势，才能更早遇到贵人啊！ ";
	}

	@Override
	public String getCardShareAnatics() {
		return "xzys";
	}

	@Override
	public String getCardDetailImageUrl() {
		return null;
	}

	@Override
	public void loadDataS(Context context, final Card card, boolean needRefreshData, int showSize) {
		mContext = context;
		int id = StarCardViewHelper.getInd(card.id);
		NaviCardLoader.createBaseDir();
		String localStr = FileUtil.readFileContent(NAVI_CARD_PATH + id);
		if (TextUtils.isEmpty(localStr)) {
			FileUtil.copyPluginAssetsFile(context, NAVI_FILE + id, NaviCardLoader.NAV_DIR, NAVI_FILE + id);
			localStr = FileUtil.readFileContent(NAVI_CARD_PATH + id);
		}

		ArrayList<String> objList = new ArrayList<>();
		objList.add(String.valueOf(card.id));
		objList.add(localStr);
		refreshView(objList);
	}

	@Override
	public void loadDataFromServer(final Context context, final Card card, final boolean needRefreshView) {
		try {
			JSONObject paramsJO = new JSONObject();
			int id = StarCardViewHelper.getInd(card.id);
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
			String date = tempDate.format(new java.util.Date());
			try {
				paramsJO.put("Horoscope", Integer.valueOf(id + 1));
				paramsJO.put("Date", date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String jsonParams = paramsJO.toString();
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
			LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9014");
			ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
			if (csResult == null) {
				return;
			}
			
			String resString = csResult.getResponseJson();
			try {
				JSONObject jo = new JSONObject(resString);
				if (needRefreshView) {
					ArrayList<String> objList = new ArrayList<>();
					objList.add(String.valueOf(card.id));
					objList.add(resString);
					refreshView(objList);
				}
				FileUtil.writeFile(NAVI_CARD_PATH + id, jo.toString(), false);
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

		Message msg = new Message();
		msg.what = CardViewHelper.MSG_LOAD_STAR_SUC;
		msg.obj = objList;
		handler.sendMessage(msg);
	}

	public static String getMoreUrl(int id) {
		return MORE_URLS[id - CardManager.CARD_ID_STAR];
	}
}
