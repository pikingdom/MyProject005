package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.UrlBean;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationView2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 网址卡片数据加载
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NaviSiteLoader {

	private static final String NAVI_CARD_SITE = "navi_card_site.txt";
	private static final String NAVI_CARD_SITE_PATH = NaviCardLoader.NAV_DIR + NAVI_CARD_SITE;

	/**
	 * @desc 获取网址导航卡片数据
	 * @author linliangbin
	 * @time 2017/9/22 17:06
	 */
	public static ArrayList<UrlBean> getSiteCardUrls(Context context) {

		NaviCardLoader.createBaseDir();
		ArrayList<UrlBean> urlList = new ArrayList<>();
		String siteStr = FileUtil.readFileContent(NAVI_CARD_SITE_PATH);
		if (TextUtils.isEmpty(siteStr)) {
			// 从Assets复制
			FileUtil.copyPluginAssetsFile(context, NAVI_CARD_SITE, NaviCardLoader.NAV_DIR, NAVI_CARD_SITE);
			siteStr = FileUtil.readFileContent(NAVI_CARD_SITE_PATH);
		}

		JSONArray siteJA = null;
		try {
			siteJA = new JSONArray(siteStr);
			urlList = generateUrlBeanList(siteJA);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (urlList.size() == 0) {
			FileUtil.copyPluginAssetsFile(context, NAVI_CARD_SITE, NaviCardLoader.NAV_DIR, NAVI_CARD_SITE);
			siteStr = FileUtil.readFileContent(NAVI_CARD_SITE_PATH);
			try {
				siteJA = new JSONArray(siteStr);
				urlList = generateUrlBeanList(siteJA);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return urlList;
	}

	public static void loadSiteCard(final Context context, final NavigationView2 navigationView) {
		ThreadUtil.executeMore(new Runnable() {

			@Override
			public void run() {
				try {
					navigationView.naviSiteCardBaseView.urlList = getSiteCardUrls(context);
					navigationView.naviSiteCardBaseView.showItemS();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static ArrayList<UrlBean> generateUrlBeanList(JSONArray siteJA) {
		ArrayList<UrlBean> urlList = new ArrayList<UrlBean>();
		if(siteJA == null ){
			return urlList;
		}
		for (int i = 0; i < siteJA.length(); i++) {
			try {
				UrlBean urlBean = new UrlBean(siteJA.getJSONObject(i));
				urlList.add(urlBean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return urlList;
	}

	public static void saveNewSite(String str) {
		try {
			JSONArray allJA = new JSONArray(str);
			for (int i = 0; i < allJA.length(); i++) {
				JSONObject cateJo = allJA.getJSONObject(i);
				if (cateJo.getString("title").indexOf("网址导航卡片") >= 0) {
					JSONArray itemJA = cateJo.getJSONArray("items");
					JSONArray saveJA = new JSONArray();
					for (int j = 0; j < itemJA.length(); j++) {
						JSONObject siteJO = itemJA.getJSONObject(j);
						JSONObject saveJO = new JSONObject();
						saveJO.put("id", siteJO.getInt("id"));
						saveJO.put("name", siteJO.getString("name"));
						saveJO.put("url", siteJO.getString("url"));
						saveJO.put("isRed", siteJO.getInt("red") == 0 ? false : true);
						saveJA.put(saveJO);
					}

					FileUtil.writeFile(NAVI_CARD_SITE_PATH, saveJA.toString(), false);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
