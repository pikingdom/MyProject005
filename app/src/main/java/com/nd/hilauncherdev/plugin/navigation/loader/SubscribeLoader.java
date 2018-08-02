package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.text.TextUtils;
import com.nd.hilauncherdev.plugin.navigation.Card;
import com.nd.hilauncherdev.plugin.navigation.CardManager;
import com.nd.hilauncherdev.plugin.navigation.CardViewHelper;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeArticleBean;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeCateBean;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeSiteBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;
import com.nd.hilauncherdev.plugin.navigation.widget.subscribe.SubscribeHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linliangbin on 16-7-11.
 */
public class SubscribeLoader extends CardDataLoader {
    
    //下一次请求的文章列表页码
    int nextPi = 1;
    
    //当前显示的文章列表下标
    int currentIndex = 0;
    
    
    public static final int MAX_COUNT = 3;
    
    private static final String NAVI_FILE_RECOMMEND = "navi_card_subscribe_recommend.txt";
    private static final String NAVI_FILE_ARTICLE = "navi_card_subscribe_article.txt";
    
    private static final String NAVI_CARD_PATH_RECOMMEND = NaviCardLoader.NAV_DIR + NAVI_FILE_RECOMMEND;
    private static final String NAVI_CARD_PATH_ARTICLE = NaviCardLoader.NAV_DIR + NAVI_FILE_ARTICLE;
    
    public boolean isShowingRecommend = false;
    
    // 当前文章列表请求页码
    private static final String SUBSCRIBE_CARD_CUR_PAGE_INDEX = "subscribe_card_cur_page_index";
    
    // 当前订阅号列表发生改变
    private static final String SUBSCRIBE_CARD_SITE_CHANGE = "subscribe_card_site_change";

    /**
     * 当前是否需要将内存内容和和SD卡上的内容同步
     * 当文章列表到达接近末尾时，会获取数据并将数据保存到SD卡，但不会同步内存数据。此时置标记位为true
     * 当文章列表到达末尾并重新从SD卡读取数据时，同步SD卡的数据到内存，并将标记位置为false;
     *
     */
    private boolean needSyncArticleLocalWithMemory = false;

    //当前推荐订阅号列表
    private ArrayList<SubscribeSiteBean> recommendList = new ArrayList<SubscribeSiteBean>();
    private ArrayList<SubscribeArticleBean> articleBeans = new ArrayList<SubscribeArticleBean>();
    
    private ArrayList<SubscribeSiteBean> preRecommendList = new ArrayList<SubscribeSiteBean>();
    private ArrayList<SubscribeArticleBean> preArticleList = new ArrayList<SubscribeArticleBean>();
    
    /**
     * 清除本地推荐订阅号数据缓存
     * @return
     */
    public static final void clearRecommendCache(final Context context){
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                try {

                    if(FileUtil.isFileExits(NAVI_CARD_PATH_RECOMMEND)){
                        FileUtil.delFile(NAVI_CARD_PATH_RECOMMEND);
                    }
                    if(FileUtil.isFileExits(NAVI_CARD_PATH_ARTICLE)){
                        FileUtil.delFile(NAVI_CARD_PATH_ARTICLE);
                    }
                    SubscribeHelper.clearAddedSubscribeSite(context);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public String getCardShareImagePath() {
        return NaviCardLoader.NAV_DIR + "navigation_subscribe_share.jpg";
    }

    @Override
    public String getCardShareImageUrl() {
        if(CommonLauncherControl.DX_PKG){
            return "http://pic.ifjing.com/rbpiczy/pic/2016/09/19/fce6d8ad2daa4497be245b1b8e691e71.jpg";
        }
        if(CommonLauncherControl.AZ_PKG){
            return "http://pic.ifjing.com/rbpiczy/pic/2016/09/19/0d9847c2d6a845fb9a02eb12aa077eff.jpg";
        }
        return "http://pic.ifjing.com/rbpiczy/pic/2016/08/04/6e5ebeae8f72467ead991104a680f1e3.jpg";
    }

    @Override
    public String getCardShareWord() {
        return "微订阅——大千世界，随心订阅  用@" + CommonLauncherControl.LAUNCHER_NAME + " ，订阅各类资讯，享受美好生活";
    }

    @Override
    public String getCardShareAnatics() {
        return "wdy";
    }

    @Override
    public String getCardDetailImageUrl() {
        return "http://pic.ifjing.com/rbpiczy/pic/2016/08/02/d0063ca66cd841b7a721c8ee05d1bb70.png";
    }

    @Override
    public void loadDataS(Context context, Card card, boolean needRefreshData, int showSize) {
        mContext = context;
        boolean isUsingDefaultRecommendSite = false;
        boolean isUsingDefaultArticle = false;
        //当前要显示的文章列表
        ArrayList<SubscribeArticleBean> tempArticle = new ArrayList<SubscribeArticleBean>();
        
        //展示推荐订阅号信息
        if(SubscribeHelper.getAddedSubscribeSiteCount(mContext) == 0){
            if(!needRefreshData && preRecommendList != null && preRecommendList.size() >= MAX_COUNT){
                refreshSite(preRecommendList);
                return;
            }
            isShowingRecommend = true;
            //展示推荐订阅号信息
            if(recommendList != null && recommendList.size() >= MAX_COUNT){
                refreshSite(recommendList);
            }else{
                NaviCardLoader.createBaseDir();
                String localStr = FileUtil.readFileContent(NAVI_CARD_PATH_RECOMMEND);
                if (TextUtils.isEmpty(localStr)) {
                    if (FileUtil.copyPluginAssetsFile(context, NAVI_FILE_RECOMMEND, NaviCardLoader.NAV_DIR, NAVI_FILE_RECOMMEND)) {
                        localStr = FileUtil.readFileContent(NAVI_CARD_PATH_RECOMMEND);
                        recommendList = readSiteListFromString(localStr);
                        isUsingDefaultRecommendSite = true;
                        refreshSite(recommendList);
                    }
        
                }else{
                    recommendList = readSiteListFromString(localStr);
                    isUsingDefaultRecommendSite = false;
                    refreshSite(recommendList);
                }
    
            }
        }else{
            //展示文章列表
            if(!needRefreshData && preArticleList != null && preArticleList.size() >= MAX_COUNT){
                refreshArticle(preArticleList);
                if(getSubscribeCardSiteChange(context)){
                    requestArticleAndRefreshView();
                    setSubscribeCardSiteChange(context,false);
                }
                return;
            }
            isShowingRecommend = false;
            if (articleBeans != null && articleBeans.size() >= 3) {
                //当前数据已经接近尾部
                if (currentIndex + MAX_COUNT >= articleBeans.size()) {
                    for (; currentIndex < articleBeans.size(); currentIndex++) {
                        tempArticle.add(articleBeans.get(currentIndex));
                    }
            
                    currentIndex = 0;
            
                    NaviCardLoader.createBaseDir();
                    String localStr = FileUtil.readFileContent(NAVI_CARD_PATH_ARTICLE);
                    articleBeans = readArticleListFromString(localStr);
                    needSyncArticleLocalWithMemory = false;
                    if (articleBeans != null && articleBeans.size() + tempArticle.size() >= MAX_COUNT) {
                        while(currentIndex < articleBeans.size()) {
                            tempArticle.add(articleBeans.get(currentIndex++));
                            if (tempArticle.size() >= MAX_COUNT) {
                                break;
                            }
                        }
                    }
                    refreshArticle(tempArticle);
                }else{
                    for (int i =0; currentIndex < articleBeans.size() && i < 3;i++) {
                        tempArticle.add(articleBeans.get(currentIndex++));
                    }
                    refreshArticle(tempArticle);
                }
                
            } else {
                //展示文章列表
                NaviCardLoader.createBaseDir();
                String localStr = FileUtil.readFileContent(NAVI_CARD_PATH_ARTICLE);
                if (TextUtils.isEmpty(localStr)) {
                    isUsingDefaultRecommendSite = true;
                    if (FileUtil.copyPluginAssetsFile(context, NAVI_FILE_ARTICLE, NaviCardLoader.NAV_DIR, NAVI_FILE_ARTICLE)) {
                        localStr = FileUtil.readFileContent(NAVI_CARD_PATH_ARTICLE);
                    }
                }
                currentIndex = 0;
                articleBeans = readArticleListFromString(localStr);
    
                if (articleBeans != null && articleBeans.size() >= MAX_COUNT) {
                    while (currentIndex < articleBeans.size()) {
                        tempArticle.add(articleBeans.get(currentIndex++));
                        if (tempArticle.size() >= MAX_COUNT) {
                            break;
                        }
                    }
                }
                refreshArticle(tempArticle);
                
        
            }

        }
    
        nextPi = getSubscribeCardCurPageIndex(mContext);
    
        /**
         * 获取推荐列表
         */
        if(isUsingDefaultRecommendSite){
            String data = loadSubscribSiteById_6015_InString(context,"",2);
            recommendList = readSiteListFromString(data);
            if(recommendList != null && recommendList.size() >= 3){
                refreshSite(recommendList);
                FileUtil.writeFile(NAVI_CARD_PATH_RECOMMEND, data, false);
            }
        }
    
        /**
         * 获取文章信息
         */
        if(isUsingDefaultArticle || getSubscribeCardSiteChange(context)){
            requestArticleAndRefreshView();
            needSyncArticleLocalWithMemory = false;
            setSubscribeCardSiteChange(context,false);
        }else if(articleBeans != null && currentIndex +6 >= articleBeans.size() && !needSyncArticleLocalWithMemory){
            //剩余可用数据不足六个时，请求数据
            String data = loadSubscribArticleById_6014_InString(context,SubscribeHelper.getAddedSubscribeSite(context),nextPi++);
            setSubscribeCardCurPageIndex(context,nextPi);
            ArrayList<SubscribeArticleBean> currentData = readArticleListFromString(data);
            if(currentData != null && currentData.size() >=3){
                FileUtil.writeFile(NAVI_CARD_PATH_ARTICLE, data, false);
            }else if(!TextUtils.isEmpty(data)){
                //data不为空，排除网络访问异常，到达数据列表结尾，
                setSubscribeCardCurPageIndex(context,0);
            }
            needSyncArticleLocalWithMemory = true;
        }
        
    }
    
    
    /**
     * 获取数据并刷新界面
     */
    private void requestArticleAndRefreshView(){
        nextPi = 0;
        String data = loadSubscribArticleById_6014_InString(mContext,SubscribeHelper.getAddedSubscribeSite(mContext),nextPi++);
        setSubscribeCardCurPageIndex(mContext,nextPi);
        ArrayList<SubscribeArticleBean> tempList = readArticleListFromString(data);
        if (tempList != null && tempList.size() > 0) {
            articleBeans = tempList;
            FileUtil.writeFile(NAVI_CARD_PATH_ARTICLE, data, false);
            //当前要显示的文章列表
            ArrayList<SubscribeArticleBean> tempArticle = new ArrayList<SubscribeArticleBean>();
            if (articleBeans != null && articleBeans.size() >= 3) {
                for (currentIndex = 0; currentIndex < articleBeans.size() && currentIndex < 3; currentIndex++) {
                    tempArticle.add(articleBeans.get(currentIndex));
                }
                refreshArticle(tempArticle);
            } else if (!TextUtils.isEmpty(data)) {
                setSubscribeCardCurPageIndex(mContext, 0);
            }
        }
    }
    
    
    @Override
    public void loadDataFromServer(Context context, Card card, boolean needRefreshView) {
        if(SubscribeHelper.getAddedSubscribeSiteCount(context) == 0){
            //更新推荐数据
            String data = loadSubscribSiteById_6015_InString(context,"",2);
            recommendList = readSiteListFromString(data);
            if(recommendList != null && recommendList.size() >= 3){
                FileUtil.writeFile(NAVI_CARD_PATH_RECOMMEND, data, false);
                if(needRefreshView){
                    refreshSite(recommendList);
                }
            }
        }else{
            //更新文章数据
            nextPi = 0;
            String data = loadSubscribArticleById_6014_InString(mContext,SubscribeHelper.getAddedSubscribeSite(mContext),nextPi++);
            setSubscribeCardCurPageIndex(mContext,nextPi);
            ArrayList<SubscribeArticleBean> tempList = readArticleListFromString(data);
            if (tempList != null && tempList.size() > 0) {
                articleBeans = tempList;
                FileUtil.writeFile(NAVI_CARD_PATH_ARTICLE, data, false);
                if(needRefreshView){
                    //当前要显示的文章列表
                    ArrayList<SubscribeArticleBean> tempArticle = new ArrayList<SubscribeArticleBean>();
                    if (articleBeans != null && articleBeans.size() >= 3) {
                        for (currentIndex = 0; currentIndex < articleBeans.size() && currentIndex < 3; currentIndex++) {
                            tempArticle.add(articleBeans.get(currentIndex));
                        }
                        refreshArticle(tempArticle);
                    } 
                }

            }
        }
    }
    
    
    private void refreshArticle(ArrayList<SubscribeArticleBean> articleBeans){
        if (articleBeans != null && articleBeans.size() >= MAX_COUNT) {
            Message msg = new Message();
            msg.what = CardViewHelper.MSG_LOAD_SUBSCRIBE_ARTICLE_SUC;
            msg.obj = articleBeans;
            handler.sendMessage(msg);
            preArticleList = articleBeans;
        }
    }
    
    private void refreshSite(ArrayList<SubscribeSiteBean> list){
    
        try {
            if (list != null && list.size() >= MAX_COUNT) {
                Message msg = new Message();
                msg.what = CardViewHelper.MSG_LOAD_SUBSCRIBE_SITE_SUC;
                msg.obj = list;
                handler.sendMessage(msg);
                preRecommendList = list;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
    @Override
    public <E> void refreshView(ArrayList<E> objList) {
        
    }
    
    
    public static ArrayList<SubscribeSiteBean> readSiteListFromString(String resString){
        
        ArrayList<SubscribeSiteBean> cateBeans = new ArrayList<SubscribeSiteBean>();
    
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(resString);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject tempObject = (JSONObject) jsonArray.get(i);
                SubscribeSiteBean subscribeCateBean = new SubscribeSiteBean();
                subscribeCateBean.initDataFromJson(tempObject);
                cateBeans.add(subscribeCateBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cateBeans;
    }
    
    
    public static ArrayList<SubscribeArticleBean> readArticleListFromString(String resString){
        
        ArrayList<SubscribeArticleBean> cateBeans = new ArrayList<SubscribeArticleBean>();
        try {
            JSONObject jsonObject = null;
            jsonObject = new JSONObject(resString);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject tempObject = (JSONObject) jsonArray.get(i);
                SubscribeArticleBean subscribeArticleBean = new SubscribeArticleBean();
                subscribeArticleBean.initDataFromJson(tempObject);
                cateBeans.add(subscribeArticleBean);
            }
            return cateBeans;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cateBeans;
    }
    
    /**
     * 获取订阅号分类信息
     * @param context
     * @return
     */
    public static ArrayList<SubscribeCateBean> loadSubscribCateData_6012(Context context){
        
        ArrayList<SubscribeCateBean> cateBeans = new ArrayList<SubscribeCateBean>();
        
        try {
            JSONObject paramsJO = new JSONObject();
//            paramsJO.put("Type",type);
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_EX_BASE_URL + "action.ashx/UserInfoAction/6012");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return cateBeans;
            }
            String resString = csResult.getResponseJson();
    
            JSONObject jsonObject = new JSONObject(resString);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject tempObject = (JSONObject) jsonArray.get(i);
                SubscribeCateBean subscribeCateBean = new SubscribeCateBean();
                subscribeCateBean.initDataFromJson(tempObject);
                cateBeans.add(subscribeCateBean);
            }
            return cateBeans;
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cateBeans;
                
    }
    
    
    
    /**
     * 根据订阅号分类ID获取订阅列表信息
     * @param context
     * @return
     */
    public static ArrayList<SubscribeSiteBean> loadSubscribSiteByCate_6013(Context context,int pi,String cateId){
        
        ArrayList<SubscribeSiteBean> cateBeans = new ArrayList<SubscribeSiteBean>();
        
        try {
            JSONObject paramsJO = new JSONObject();
            paramsJO.put("Pi",pi);
            paramsJO.put("Ps",10);
            paramsJO.put("cateId",cateId);
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_EX_BASE_URL + "action.ashx/UserInfoAction/6013");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return cateBeans;
            }
            String resString = csResult.getResponseJson();
            JSONObject jsonObject = new JSONObject(resString);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject tempObject = (JSONObject) jsonArray.get(i);
                SubscribeSiteBean subscribeCateBean = new SubscribeSiteBean();
                subscribeCateBean.initDataFromJson(tempObject);
                cateBeans.add(subscribeCateBean);
            }
            return cateBeans;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cateBeans;
        
    }
    
    
    /**
     * 返回String 类型的订阅号列表
     * @param context
     * @param ids
     * @param type
     * @return
     */
    public static String loadSubscribSiteById_6015_InString(Context context,String ids,int type){
        try {
            JSONObject paramsJO = new JSONObject();
            if(!TextUtils.isEmpty(ids)){
                paramsJO.put("Ids",ids);
            }else{
                paramsJO.put("Ids","");
            }
            paramsJO.put("Type", type);
            paramsJO.put("Cache",1);
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_EX_BASE_URL + "action.ashx/UserInfoAction/6015");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return "";
            }
            String resString = csResult.getResponseJson();
            return resString;
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 根据订阅号ID获取订阅号信息
     * type = 2: 获取推荐信息
     * type = 1: 根据传递的ID获取列表信息
     * @param context
     * @return
     */
    public static ArrayList<SubscribeSiteBean> loadSubscribSiteById_6015(Context context,String ids,int type){
        
        ArrayList<SubscribeSiteBean> cateBeans = new ArrayList<SubscribeSiteBean>();
        
        try {
            String resString = loadSubscribSiteById_6015_InString(context, ids, type);
            return readSiteListFromString(resString);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cateBeans;
        
    }
    
    
    
    /**
     * 根据订阅号ID获取文章列表
     * 返回String 类型
     * @param context
     * @return
     */
    public static String loadSubscribArticleById_6014_InString(Context context,String ids,int pi){
        
        ArrayList<SubscribeArticleBean> cateBeans = new ArrayList<SubscribeArticleBean>();
        
        try {
            JSONObject paramsJO = new JSONObject();
            if(!TextUtils.isEmpty(ids)){
                paramsJO.put("Ids",ids);
            }else{
                paramsJO.put("Ids","");
            }
            paramsJO.put("Pi",pi);
            paramsJO.put("Ps",20);
            
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_EX_BASE_URL + "action.ashx/UserInfoAction/6014");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            if (csResult == null || !csResult.isRequestOK()) {
                return "";
            }
            String resString = csResult.getResponseJson();
            return resString;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
        
    }
    
    
    /**
     * 根据订阅号ID获取文章列表
     * 
     * @param context
     * @return
     */
    public static ArrayList<SubscribeArticleBean> loadSubscribArticleById_6014(Context context,String ids,int pi){
        
        String res = loadSubscribArticleById_6014_InString(context, ids, pi);
        if(TextUtils.isEmpty(res))
            return null;
        return readArticleListFromString(res);

    }
    
    
    /**
     * 获取当前文章列表请求页码
     * @param context
     * @param value
     */
    public void setSubscribeCardCurPageIndex(Context context, int value) {
        if(context != null){
            SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
            sp.edit().putInt(SUBSCRIBE_CARD_CUR_PAGE_INDEX, value).commit();
        }
    }
    
    public int getSubscribeCardCurPageIndex(Context context) {
        if(context != null){
            SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
            return sp.getInt(SUBSCRIBE_CARD_CUR_PAGE_INDEX, 0);
        }
        return 0;
    }
    
    /**
     * 获取当前订阅号ID 是否改变
     * @param context
     * @param value
     */
    public static void setSubscribeCardSiteChange(Context context, boolean value) {
        if(context != null){
            SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
            sp.edit().putBoolean(SUBSCRIBE_CARD_SITE_CHANGE, value).commit();
        }
    }
    
    public static boolean getSubscribeCardSiteChange(Context context) {
        if(context != null){
            SharedPreferences sp = context.getSharedPreferences(CardManager.SP_NAME, Context.MODE_PRIVATE);
            return sp.getBoolean(SUBSCRIBE_CARD_SITE_CHANGE, false);
        }
        return false;
    }
    
}
