package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.News;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.StringUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhouheqiang_dian91 on 2016/3/4.
 */
public class ZeroNewsLoader {
    public static final String CACHE_PATH = NaviCardLoader.NAV_DIR + "zero_news_cache.dat";
    private final static int PAGESIZE = 11;

    /**
     * 加载网易和zaker的数据
     */

    public static News.NewsList loadZakerAndWyNews(final Context context, long nextNonce, int nextPindex, String ucRecoId, long ucftime) {
        ArrayList<News> data = new ArrayList<News>();
        boolean haveError = false;
        News.NewsList list = new News.NewsList();
        list.mList = data;
        JSONObject paramsJO = new JSONObject();
        try {
            if (nextNonce != -1) {
                paramsJO.put("Nonce", nextNonce);
            }
            if (nextPindex != -1) {
                paramsJO.put("PageIndex", nextPindex);
            }
            paramsJO.put("Udid", TelephoneUtil.getIMEI(context));
            paramsJO.put("PageSize", PAGESIZE);
            paramsJO.put("ScreenWidth", ScreenUtil.getCurrentScreenWidth(context));
            paramsJO.put("ScreenHeight", ScreenUtil.getCurrentScreenHeight(context));
            paramsJO.put("UcRecoId", StringUtil.isNetEmpty(ucRecoId) ? "" : ucRecoId);
            paramsJO.put("Ucftime", (ucftime == -1l || ucftime == 0l) ? "" : "" + ucftime);

        } catch (Exception e) {
            haveError = true;
        }
        if (!haveError) {
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9026");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult != null) {
                try {
                    String str = csResult.getResponseJson();
                    JSONObject jo = new JSONObject(str);
                    long newNextNonce = jo.optLong("NextNonce");
                    int newNextPIndex = jo.optInt("NextPIndex");
                    String newUcRecoId = jo.optString("UcRecoId");
                    newUcRecoId = (StringUtil.isNetEmpty(newUcRecoId)) ? "" : newUcRecoId;
                    long newUcftime = jo.optLong("Ucftime");
                    //服务端返回的下一次时间戳或者页码，和请求的一致，说明已经没有数据了
                    if (newNextNonce == nextNonce && newNextPIndex == nextNonce) {
                        list.isLast = true;
                    }
                    list.ucRecoId = newUcRecoId;
                    list.ucftime = newUcftime;
                    list.mNextPindx = newNextPIndex;
                    list.mNextNonce = newNextNonce;
                    JSONArray newsjo = jo.optJSONArray("News");
                    for (int i = 0; i < newsjo.length(); i++) {
                        News news = new News(newsjo.getJSONObject(i));
                        news.ucRecoId = list.ucRecoId;
                        //uc过滤掉视频卡片
                        if (news == null || news.ucItemType == 30) {
                            continue;
                        }
                        data.add(news);
                    }
                    if (nextNonce == -1 && nextPindex == -1) {
                        //保存最新的一次刷新请求
                        FileUtil.writeFile(CACHE_PATH, str, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private static int i = 0;

    public static void readCacheNesFromSD(News.NewsList list) {
        String json = FileUtil.readFileContent(CACHE_PATH);
        if (json != null) {
            try {
                JSONObject jo = new JSONObject(json);
                JSONArray newsjo = jo.optJSONArray("News");
                for (int i = 0; i < newsjo.length(); i++) {
                    News news = new News(newsjo.getJSONObject(i));
                    list.mList.add(news);
                }
                list.mNextNonce = jo.optLong("NextNonce");
                list.mNextPindx = 2;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //保存最新的新闻SD卡
    protected void saveCacheNewsToSD() {
        //不为空直接保存
        /*
        try {
            JSONObject root = new JSONObject();
            JSONArray newsList = new JSONArray();
            root.put("News", newsList);
            if (!mBannerData.isEmpty()) {
                News news = mBannerData.get(0);
                JSONObject object = news.toJson();
                newsList.put(object);
            }
            for (int i = 0; i < mBeanList.size(); i++) {
                News news = mBeanList.get(i);
                JSONObject object = news.toJson();
                newsList.put(object);
            }
            String json = root.toString();
            FileUtil.writeFile(cache_path, json, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

    }

}
