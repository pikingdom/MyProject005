package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.GameBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linliangbin_dian91 on 2015/10/21.
 */
public class GameLoader extends CardDataLoader {

    private static final String RECOMMEND_GAME_NAVI_FILE = "navi_card_game_recommend.txt";
    private static final String H5_GAME_NAVI_FILE = "navi_card_game_h5.txt";
    private static final String NEWS_GAME_NAVI_FILE = "navi_card_game_news.txt";

    private static final String GAME_CARD_RECOMMEMD_CUR_PAGE = "game_card_recommend_cur_page";
    private static final String GAME_CARD_H5_CUR_PAGE = "game_card_h5_cur_page";
    private static final String GAME_CARD_NEWS_CUR_PAGE = "game_card_news_cur_page";


    private static final String RECOMMEND_GAME_NAVI_PATH = NaviCardLoader.NAV_DIR + RECOMMEND_GAME_NAVI_FILE;
    private static final String H5_GAME_NAVI_PATH = NaviCardLoader.NAV_DIR + H5_GAME_NAVI_FILE;
    private static final String NEWS_GAME_NAVI_PATH = NaviCardLoader.NAV_DIR + NEWS_GAME_NAVI_FILE;


    public static final int RECOMMEND_LIST_INDEX = 0;
    public static final int H5_LIST_INDEX = 1;
    public static final int NEWS_LIST_INDEX = 2;

    private int NEWS_PAGE_SIZE = 2;
    private int NEWS_TOTAL_DATA_SIZE = 10;

    private int PAGE_SIZE = 4;
    private int TOTAL_DATA_SIZE = 20;
    private int MAX_PAGE_COUNT = TOTAL_DATA_SIZE / PAGE_SIZE;
    private int current_show_page = 0;

    // 只请求前3页数据
    private int MAX_REQUEST_PAGE_RECOMMEND_H5 = 60 / TOTAL_DATA_SIZE;

    // 当前缓存的游戏列表 长度为20
    ArrayList<GameBean> recommendList = new ArrayList<GameBean>();
    ArrayList<GameBean> h5List = new ArrayList<GameBean>();
    ArrayList<GameBean> newsList = new ArrayList<GameBean>();

    // 上次请求的游戏列表 用于不需要刷新界面的情况
    private ArrayList<GameBean> prerecommendList = new ArrayList<GameBean>();
    private ArrayList<GameBean> preh5List = new ArrayList<GameBean>();
    private ArrayList<GameBean> prenewsList = new ArrayList<GameBean>();

    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_game_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if(CommonLauncherControl.DX_PKG){
            return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/04/25/d13e6cc68ca44f118b663e556705f26f.jpg";
        }
        if(CommonLauncherControl.AZ_PKG){
            return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/05/19/6eaf44c6e70b40c28ddcacc590aef812.png";
        }
        return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/06/20/2f8d61eefbc64d0e9971460a8d414035.jpg";
    }

    @Override
    public String getCardShareWord() {
        return "最in游戏，不一样的体验  @" + CommonLauncherControl.LAUNCHER_NAME + " " ;
    }

    @Override
    public String getCardShareAnatics() {
        return "rmyx";
    }

    @Override
    public String getCardDetailImageUrl() {
        return UrlConstant.HTTP_PIC_IFJING_COM+"rbpiczy/pic/2016/06/20/d76e8543744b4ad182e79f739da8afc3.png";
    }

    @Override
    public void loadDataS(final Context context, Card card, boolean needRefreshData, int showSize) {
        mContext = context;
        NaviCardLoader.createBaseDir();
        String localStr = "";
        //不需要更新数据时，如手动调整卡片位置
        if(!needRefreshData && prerecommendList.size()>0 && preh5List.size()>0 && prenewsList.size()>0){
            ArrayList<ArrayList<GameBean>> allList = new ArrayList<ArrayList<GameBean>>();
            ArrayList<GameBean> prerecommendListTemp = new ArrayList<GameBean>();
            ArrayList<GameBean> preh5ListTemp = new ArrayList<GameBean>();
            ArrayList<GameBean> prenewsListTemp = new ArrayList<GameBean>();

            if(prerecommendList != null && prerecommendList.size()>0){
                for(GameBean game:prerecommendList)
                    prerecommendListTemp.add(game);
            }
            if(preh5List != null && preh5List.size()>0){
                for(GameBean game:preh5List)
                    preh5ListTemp.add(game);
            }
            if(prenewsList != null && prenewsList.size()>0){
                for(GameBean game:prenewsList)
                    prenewsListTemp.add(game);
            }

            allList.add(prerecommendListTemp);
            allList.add(preh5ListTemp);
            allList.add(prenewsListTemp);
            refreshView(allList);
            return;
        }

        if (current_show_page < MAX_PAGE_COUNT && current_show_page != 0) {
            //不需要重新获取；
            refreshView(getCurrentShowingList());
            current_show_page++;
            if (current_show_page == MAX_PAGE_COUNT - 1) {
                //缓存已经显示完，需要重新获取；
                loadDataFromServer(context, null, false);
            }
            return;
        }

        current_show_page = 0;

        /**
         * 是否需要重新获取网络数据
         * 当第一次使用时（本地数据为空） 则重新获取数据
         */
        boolean needInitData = false;
        try {
            //推荐游戏
            localStr = FileUtil.readFileContent(RECOMMEND_GAME_NAVI_PATH);
            if (TextUtils.isEmpty(localStr)) {
                needInitData = true;
                FileUtil.copyPluginAssetsFile(mContext, RECOMMEND_GAME_NAVI_FILE, NaviCardLoader.NAV_DIR, RECOMMEND_GAME_NAVI_FILE);
                localStr = FileUtil.readFileContent(RECOMMEND_GAME_NAVI_PATH);
            }

            JSONArray ja = new JSONArray(localStr);
            if (ja.length() > 0) {
                recommendList.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject dataJo = ja.getJSONObject(i);
                    GameBean gameBean = new GameBean(dataJo, GameBean.RECOMMEND_TYPE);
                    recommendList.add(gameBean);
                }
            }
        } catch (Exception e) {
            FileUtil.writeFile(RECOMMEND_GAME_NAVI_FILE, "", false);
            e.printStackTrace();
        }
        try {
            //H5游戏
            h5List.clear();
            localStr = FileUtil.readFileContent(H5_GAME_NAVI_PATH);
            if (TextUtils.isEmpty(localStr)) {
                needInitData = true;
                FileUtil.copyPluginAssetsFile(mContext, H5_GAME_NAVI_FILE, NaviCardLoader.NAV_DIR, H5_GAME_NAVI_FILE);
                localStr = FileUtil.readFileContent(H5_GAME_NAVI_PATH);
            }
            JSONArray ja = new JSONArray(localStr);
            if (ja.length() > 0) {
                h5List.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject dataJo = ja.getJSONObject(i);
                    GameBean gameBean = new GameBean(dataJo, GameBean.H5_TYPE);
                    h5List.add(gameBean);
                }
            }
        } catch (Exception e) {
            FileUtil.writeFile(H5_GAME_NAVI_FILE, "", false);
            e.printStackTrace();
        }

        try {
            //游戏新闻
            localStr = FileUtil.readFileContent(NEWS_GAME_NAVI_PATH);
            newsList.clear();
            if (TextUtils.isEmpty(localStr)) {
                needInitData = true;
                FileUtil.copyPluginAssetsFile(mContext, NEWS_GAME_NAVI_FILE, NaviCardLoader.NAV_DIR, NEWS_GAME_NAVI_FILE);
                localStr = FileUtil.readFileContent(NEWS_GAME_NAVI_PATH);
            }
            JSONArray ja = new JSONArray(localStr);
            if (ja.length() > 0) {
                newsList.clear();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject dataJo = ja.getJSONObject(i);
                    GameBean gameBean = new GameBean(dataJo, GameBean.NEWS_TYPE);
                    newsList.add(gameBean);
                }
            }

            refreshView(getCurrentShowingList());
            current_show_page = 1;
        } catch (Exception e) {
            FileUtil.writeFile(NEWS_GAME_NAVI_PATH, "", false);
            e.printStackTrace();
        }

        if(needInitData){
            ThreadUtil.executeMore(new Runnable() {
                @Override
                public void run() {
                    loadDataFromServer(context, null, true);
                }
            });
        }
    }

    private ArrayList<ArrayList<GameBean>> getCurrentShowingList() {
        ArrayList<ArrayList<GameBean>> allList = new ArrayList<ArrayList<GameBean>>();

        ArrayList<GameBean> temp_recommendList = new ArrayList<GameBean>();
        ArrayList<GameBean> temp_h5List = new ArrayList<GameBean>();
        ArrayList<GameBean> temp_newsList = new ArrayList<GameBean>();
        for (int i = current_show_page * PAGE_SIZE; i < (current_show_page + 1) * PAGE_SIZE && i < recommendList.size(); i++) {
            temp_recommendList.add(recommendList.get(i));
        }

        for (int i = current_show_page * PAGE_SIZE; i < (current_show_page + 1) * PAGE_SIZE && i < h5List.size(); i++) {
            temp_h5List.add(h5List.get(i));
        }

        for (int i = current_show_page * NEWS_PAGE_SIZE; i < (current_show_page + 1) * NEWS_PAGE_SIZE && i < newsList.size(); i++) {
            temp_newsList.add(newsList.get(i));
        }
        allList.add(RECOMMEND_LIST_INDEX, temp_recommendList);
        allList.add(H5_LIST_INDEX, temp_h5List);
        allList.add(NEWS_LIST_INDEX, temp_newsList);
        return allList;
    }


    @Override
    public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {
        try {
            JSONObject paramsJO = new JSONObject();
            try {
                paramsJO.put("Type", 7);
                int recommend_index = getRecommendCurPage(mContext);
                int h5_index = getH5CurPage(mContext);
                int news_index = getNewsCurPage(mContext);
                paramsJO.put("PageIndex_Recommend", recommend_index);
                paramsJO.put("PageIndex_H5", h5_index);
                paramsJO.put("PageIndex_News", news_index);
        
                recommend_index++;
                h5_index++;
                news_index++;
                // 限制最大访问长度为60
                if (recommend_index > MAX_REQUEST_PAGE_RECOMMEND_H5)
                    setRecommendCurPage(context, 1);
                else
                    setRecommendCurPage(context, recommend_index);
        
                if (h5_index > MAX_REQUEST_PAGE_RECOMMEND_H5)
                    setH5CurPage(context, 1);
                else
                    setH5CurPage(context, h5_index);
        
                if (news_index > MAX_REQUEST_PAGE_RECOMMEND_H5)
                    setNewsCurPage(context, 1);
                else
                    setNewsCurPage(context, news_index);
        
                paramsJO.put("PageSize_Recommend", 20);
                paramsJO.put("PageSize_H5", 20);
                paramsJO.put("PageSize_News", 10);
                paramsJO.put("FromBaitong",1);
        
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, mContext, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9020");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return;
            }
    
            String resString = csResult.getResponseJson();
            JSONObject jo = null;
            JSONArray recommend_json = null;
            JSONArray h5_json = null;
            JSONArray news_json = null;
    
            ArrayList<GameBean> localRecommend = new ArrayList<GameBean>();
            ArrayList<GameBean> localH5 = new ArrayList<GameBean>();
            ArrayList<GameBean> localNews = new ArrayList<GameBean>();
    
            //推荐游戏
            try {
                jo = new JSONObject(resString);
                recommend_json = jo.optJSONArray("RecommendGameList");
                if (recommend_json.length() > 0){
                    for (int i = 0; i < recommend_json.length(); i++) {
                        JSONObject dataJo = recommend_json.getJSONObject(i);
                        GameBean gameBean = new GameBean(dataJo, GameBean.RECOMMEND_TYPE);
                        localRecommend.add(gameBean);
                    }
                    FileUtil.writeFile(RECOMMEND_GAME_NAVI_PATH, recommend_json.toString(), false);
                }
                else {
                    setRecommendCurPage(context, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    
    
            //H5游戏
            try {
                h5_json = jo.optJSONArray("H5GameList");
                if (h5_json.length() > 0){
                    for (int i = 0; i < h5_json.length(); i++) {
                        JSONObject dataJo = h5_json.getJSONObject(i);
                        GameBean gameBean = new GameBean(dataJo, GameBean.H5_TYPE);
                        localH5.add(gameBean);
                    }
                    FileUtil.writeFile(H5_GAME_NAVI_PATH, h5_json.toString(), false);
                }
                else {
                    setH5CurPage(context, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            //游戏资讯
            try {
                news_json = jo.optJSONArray("GameNewsList");
                if (news_json.length() > 0){
                    for (int i = 0; i < news_json.length(); i++) {
                        JSONObject dataJo = news_json.getJSONObject(i);
                        GameBean gameBean = new GameBean(dataJo, GameBean.NEWS_TYPE);
                        localNews.add(gameBean);
                    }
                    FileUtil.writeFile(NEWS_GAME_NAVI_PATH, news_json.toString(), false);
                }
                else {
                    setNewsCurPage(context, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    
            if(needRefreshView){
                current_show_page = 0;
                ArrayList<ArrayList<GameBean>> allList = new ArrayList<ArrayList<GameBean>>();
                ArrayList<GameBean> temp_recommendList = new ArrayList<GameBean>();
                ArrayList<GameBean> temp_h5List = new ArrayList<GameBean>();
                ArrayList<GameBean> temp_newsList = new ArrayList<GameBean>();
                // 更新内存缓存
                recommendList.clear();
                for(int i = 0;i < localRecommend.size(); i++){
                    recommendList.add(localRecommend.get(i));
                }
                h5List.clear();
                for(int i = 0;i < localH5.size(); i++){
                    h5List.add(localH5.get(i));
                }
                newsList.clear();
                for(int i = 0;i < localNews.size(); i++){
                    newsList.add(localNews.get(i));
                }
        
                // 构造要显示的列表
                for (int i = current_show_page * PAGE_SIZE; i < (current_show_page + 1) * PAGE_SIZE && i < localRecommend.size(); i++) {
                    temp_recommendList.add(localRecommend.get(i));
                }
        
                for (int i = current_show_page * PAGE_SIZE; i < (current_show_page + 1) * PAGE_SIZE && i < localH5.size(); i++) {
                    temp_h5List.add(localH5.get(i));
                }
        
                for (int i = current_show_page * NEWS_PAGE_SIZE; i < (current_show_page + 1) * NEWS_PAGE_SIZE && i < localNews.size(); i++) {
                    temp_newsList.add(localNews.get(i));
                }
                allList.add(RECOMMEND_LIST_INDEX, temp_recommendList);
                allList.add(H5_LIST_INDEX, temp_h5List);
                allList.add(NEWS_LIST_INDEX, temp_newsList);
                current_show_page++;
                refreshView(allList);
            }
    
        }catch (Exception e){
            e.printStackTrace();
        }
        
    }


    @Override
    public <E> void refreshView(ArrayList<E> objList) {
        ArrayList<GameBean> prerecommendListLocal = (ArrayList<GameBean>) objList.get(RECOMMEND_LIST_INDEX);
        ArrayList<GameBean> preh5ListLocal = (ArrayList<GameBean>) objList.get(H5_LIST_INDEX);
        ArrayList<GameBean> prenewsListLocal = (ArrayList<GameBean>) objList.get(NEWS_LIST_INDEX);
        if(prerecommendListLocal != null && prerecommendListLocal.size()>0){
            prerecommendList.clear();
            for(GameBean game:prerecommendListLocal)
                prerecommendList.add(game);
        }
        if(preh5ListLocal != null && preh5ListLocal.size()>0){
            preh5List.clear();
            for(GameBean game:preh5ListLocal)
                preh5List.add(game);
        }
        if(prenewsListLocal != null && prenewsListLocal.size()>0){
            prenewsList.clear();
            for(GameBean game:prenewsListLocal)
                prenewsList.add(game);
        }

        Message msg = new Message();
        msg.what = CardViewHelper.MSG_LOAD_GAME_SUC;
        msg.obj = objList;
        handler.sendMessage(msg);
    }

    public void setRecommendCurPage(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(GAME_CARD_RECOMMEMD_CUR_PAGE, value).commit();
    }

    public int getRecommendCurPage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(GAME_CARD_RECOMMEMD_CUR_PAGE, 1);
    }

    public void setH5CurPage(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(GAME_CARD_H5_CUR_PAGE, value).commit();
    }

    public int getH5CurPage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(GAME_CARD_H5_CUR_PAGE, 1);
    }

    public void setNewsCurPage(Context context, int value) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(GAME_CARD_NEWS_CUR_PAGE, value).commit();
    }

    public int getNewsCurPage(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(GAME_CARD_NEWS_CUR_PAGE, 1);
    }
}
