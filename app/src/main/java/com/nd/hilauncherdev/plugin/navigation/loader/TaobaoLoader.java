package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.bean.TaoBaoCollection;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoCate;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoCps;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoSplashSale;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.net.UrlConstant;
import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nd.hilauncherdev.plugin.navigation.bean.TaobaoProduct.computeImageSize;

/**
 * description: <br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/24<br/>
 */
public class TaobaoLoader {

    /**
     * 获取商品类目信息
     *
     * @param ctx
     * @return
     */
    public static List<TaobaoCate> getNetTaobaoCateList(Context ctx) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5039";
        JSONObject jsonObject = new JSONObject();
        String jsonParams = "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        List<TaobaoCate> list = null;
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                try {
                    SharedPreferencesUtil util = new SharedPreferencesUtil(ctx);
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        JSONArray jsonArray = responseObj.optJSONArray("List");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            util.putString(SharedPreferencesUtil.TAOBAO_CATE, responseJson);
                            list = TaobaoCate.convertFromJsonString(responseJson);
                        }else{
                            util.putString(SharedPreferencesUtil.TAOBAO_CATE, "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 返回淘宝首页列表，包括阿里妈推荐广告列表和人工选品的商品，广告列表
     */
    public static List<TaobaoProduct> getNetTaoBaoHomeList(Context context, int pageIndex, int pageSize) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5062";
        Map<String, Object> params = new HashMap<>();
        params.put("SlotId", 67142);// 67142和67143是淘宝分配给我们的id
        params.put("LayoutType", 12);
        params.put("RequestCount", pageSize);
        params.put("pageindex", pageIndex);
        params.put("pagesize", pageSize);
        JSONObject responseObj = getJsonObject(context, requestUrl, params);
        if (responseObj == null) return null;

        List<TaobaoProduct> resultItems = new ArrayList<>();
        try {
            String urlImp = responseObj.optString("url_imp");// 广告位级别的展示反馈链接
            if (!TextUtils.isEmpty(urlImp)) {
                new LauncherHttpCommon(urlImp).getResponseAsStringGET();// 发送广告位展示链接
            }

            int adResId = responseObj.optInt("ad_resid");
            resultItems = TaobaoProduct.beansFromJson(context, responseObj.getJSONArray("list"));
            for (TaobaoProduct item : resultItems) {
                // 未同步到风灵服务器的商品，CV统计使用的资源ID
                if (item.resId == 0) {
                    item.resId = adResId;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return resultItems;
    }

    /**
     * 返回淘宝合辑商品列表，人工编辑的阿里妈妈专辑单品列表
     *
     * @param resId 合辑Id
     */
    public static TaoBaoCollection getNetTaoBaoCollectionProducts(Context context, int resId, int pageIndex, int pageSize) {
        if (resId <= 0) return null;

        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5063";
        Map<String, Object> params = new HashMap<>();
        params.put("res_id", resId);
        params.put("pageindex", pageIndex);
        params.put("pagesize", pageSize);
        JSONObject responseObj = getJsonObject(context, requestUrl, params);
        return TaoBaoCollection.beanFromJson(context, responseObj);
    }

    /**
     * 返回服务器时间，单位ms
     */
    public static long getWebTime(Context context) {
        String requestUrl = "http://pandahome.sj.91.com/action.ashx/commonaction/1";
        JSONObject responseObj = getJsonObject(context, requestUrl, null);
        if (responseObj != null) {
            try {
                String date = responseObj.getString("CurrentDateTime");
                if (!TextUtils.isEmpty(date)) {
                    return DateUtil.strToDate(date).getTime();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return System.currentTimeMillis();
    }

    private static JSONObject getJsonObject(Context ctx, String requestUrl, Map<String, Object> params) {

        String jsonParams = "";
        if (params != null && !params.isEmpty())
            jsonParams = new JSONObject(params).toString();

        HashMap<String, String> paramsMap = new HashMap<>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseContent = csResult.getResponseJson();
                try {
                    return new JSONObject(responseContent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取购物屏卡片配置
     *
     * @param ctx
     * @return
     */
    public static ServerResultHeader getTaobaoCardSetting(Context ctx) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5040";
        String jsonParams = "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                spClientCache(ctx, SharedPreferencesUtil.TAOBAO_CARDSERIES_CACHETIME, csResult);
                try {
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        JSONArray arr = responseObj.optJSONArray("List");
                        if (arr != null && arr.length() > 0) {
                            SharedPreferencesUtil util = new SharedPreferencesUtil(ctx);
                            util.putString(SharedPreferencesUtil.TAOBAO_CARDSERIES, responseJson);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return csResult;
    }

    /**
     * 获取CPS数据
     *
     * @param ctx
     * @return
     */
    public static List<TaobaoCps> getTaobaoCps(Context ctx) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5042";
        String jsonParams = "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        List<TaobaoCps> list = null;
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                spClientCache(ctx, SharedPreferencesUtil.TAOBAO_CPS_CACHETIME, csResult);
                try {
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        JSONArray arr = responseObj.optJSONArray("List");
                        if (arr != null && arr.length() > 0) {
                            list = TaobaoCps.convertFromJsonString(responseJson);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 获取头条数据
     *
     * @param ctx
     * @return
     */
    public static ServerResultHeader getTaobaoHeadLine(Context ctx) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5041";
        String jsonParams = "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        SharedPreferencesUtil util = new SharedPreferencesUtil(ctx);
        util.putString(SharedPreferencesUtil.TAOBAO_HEADLINE, "");
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                spClientCache(ctx, SharedPreferencesUtil.TAOBAO_HEADLINE_CACHETIME, csResult);
                try {
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        JSONArray arr = responseObj.optJSONArray("List");
                        if (arr != null && arr.length() > 0) {
                            util.putString(SharedPreferencesUtil.TAOBAO_HEADLINE, responseJson);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return csResult;
    }

    /**
     * 获取购物屏限时抢购数据
     *
     * @param ctx
     * @param num
     * @return
     */
    public static List<TaobaoSplashSale> getSplashSales(Context ctx, int num) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5043";
        JSONObject jsonObject = new JSONObject();
        String jsonParams = "";
        try {
            jsonObject.put("Ad_Id", "53854994");// 67142和67143是淘宝分配给我们的id
            jsonObject.put("Number", num);
            jsonParams = encodePostData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                try {
                    String tmpServerTime = csResult.getServerTime();
                    long serverTime = 0;
                    if (!TextUtils.isEmpty(tmpServerTime)) {
                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            serverTime = format.parse(tmpServerTime).getTime();
                        } catch (Exception e) {
                            serverTime = getServerTime(ctx);
                        }
                    } else {
                        serverTime = getServerTime(ctx);
                    }
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        spClientCache(ctx, SharedPreferencesUtil.TAOBAO_CARDSERIES_CACHETIME, csResult);
                        JSONObject content = responseObj.optJSONObject("tbk_cooperate_tqg_get_response");
                        if (content != null) {
                            JSONObject res = content.optJSONObject("results");
                            if (res != null) {
                                long end = res.optLong("end_time");
                                long start = res.optLong("start_time");
                                String linkmore = res.optString("link_more");
                                JSONObject jsonItems = res.optJSONObject("items");
                                if (jsonItems != null) {
                                    List<TaobaoSplashSale> list = new ArrayList<TaobaoSplashSale>();
                                    JSONArray jsonArray = jsonItems.optJSONArray("tbk_tqg_item");
                                    if (jsonArray != null) {
                                        for (int i = 0, len = jsonArray.length(); i < len; i++) {
                                            TaobaoSplashSale splashSale = jsonToTaobaoSplashSale(ctx, jsonArray.optJSONObject(i), end, start, serverTime, linkmore);
                                            if (splashSale != null) list.add(splashSale);
                                        }
                                    }
                                    return list;
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取服务器时间
     *
     * @param ctx
     * @return
     */
    public static long getServerTime(Context ctx) {
        String requestUrl = UrlConstant.HOST + "action.ashx/commonaction/1";
        String jsonParams = "";
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                try {
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date serverTime = dateFormat.parse(responseObj.optString("CurrentDateTime"));
                        return serverTime.getTime();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return 0;
    }

    private static TaobaoSplashSale jsonToTaobaoSplashSale(Context context, JSONObject json, long end, long start, long serverTime, String linkMore) {
        if (json == null) {
            return null;
        }

        TaobaoSplashSale item = new TaobaoSplashSale();
        item.currentTime = serverTime;
        item.imageUrl = json.optString("pic_url");
        if (!TextUtils.isEmpty(item.imageUrl)) {
            Pattern pattern = Pattern.compile("_\\d+[x]\\d+\\.jpg$");
            Matcher matcher = pattern.matcher(item.imageUrl);
            if (!matcher.find()) {
                item.imageUrl += "_210x210.jpg";
            }
            item.imageUrl = computeImageSize(context, item.imageUrl);
        }
        item.urlClick = json.optString("click_url");
        item.price = json.optString("reserve_price");
        item.finalPrice = json.optString("zk_final_price");
        item.endTime = end;
        item.startTime = start;

        item.saleProgress = json.optInt("sold_num");
        item.totalSale = json.optInt("total_amount");
        item.title = json.optString("title");
        return item;
    }

    private static String encodePostData(JSONObject jsonObject) {
//        if (com.nd.hilauncherdev.datamodel.Global.isBaiduLauncher()) {
//            try {
//                jsonObject.put("Mo", 2);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return jsonObject.toString();
    }

    private static void spClientCache(Context ctx, String spName, ServerResultHeader header) {
        SharedPreferencesUtil spUtil = new SharedPreferencesUtil(ctx);
        long cacheTime = header.getClientCache();
        if (cacheTime < 60) {
            //缓存获取时间不可以小于1个小时
            cacheTime = 60;
        }
        spUtil.putLong(spName, cacheTime);
    }

    /**
     * 返回单品列表
     * <p>
     * Title: getNetTaobaoProductsList
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @return
     * @author maolinnan_350804
     */
    @Deprecated
    public static List<TaobaoProduct> getNetTaobaoProductsList(Context context, int requestCount) {
        List<TaobaoProduct> taobaoProductsItems = new LinkedList<TaobaoProduct>();
        JSONArray ProductsItemArray = getNetTaobaoProductsListJson(context, requestCount);
        try {
            if (ProductsItemArray != null) {
                for (int i = 0; i < ProductsItemArray.length(); i++) {
                    TaobaoProduct item = new TaobaoProduct();
                    JSONObject jsonObject = ProductsItemArray.getJSONObject(i);
                    item.promoter = jsonObject.optString("promoter");
                    item.category = jsonObject.optInt("category");
                    item.urlClick = jsonObject.optString("url_click");
                    String imageUrl = jsonObject.optString("img");
                    // 替换最后面的_200x200为_400x400取大图片
                    imageUrl = TaobaoProduct.computeImageSize(context, imageUrl);
                    item.imageUrl = imageUrl;
                    item.title = jsonObject.optString("title");
                    item.price = jsonObject.optDouble("price");
                    item.promoprice = jsonObject.optDouble("promoprice");
                    taobaoProductsItems.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taobaoProductsItems;
    }

    /**
     * 获取淘宝返回json
     * <p>
     * Title: getNetTaobaoProductsListJson
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @return
     * @author maolinnan_350804
     */
    @Deprecated
    private static JSONArray getNetTaobaoProductsListJson(Context ctx, int requestCount) {
        String requestUrl = UrlConstant.HOST + "action.ashx/distributeaction/5034";
        JSONObject jsonObject = new JSONObject();
        String jsonParams = "";
        try {
            jsonObject.put("SlotId", 67142);// 67142和67143是淘宝分配给我们的id
            jsonObject.put("LayoutType", 12);
            jsonObject.put("RequestCount", requestCount);
            jsonObject.put("GetInHeader", 1);
            // if(!TextUtils.isEmpty(TAOBAO_URL_PARAM)){2
            //    jsonObject.put("UrlParam", TAOBAO_URL_PARAM);
            // }
            jsonParams = encodePostData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        LauncherHttpCommon.addGlobalRequestValue(paramsMap, ctx.getApplicationContext(), jsonParams);
        LauncherHttpCommon httpCommon = new LauncherHttpCommon(requestUrl);
        ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
        if (csResult != null) {
            if (csResult.isRequestOK()) {
                String responseJson = csResult.getResponseJson();
                spClientCache(ctx, SharedPreferencesUtil.TAOBAO_CACHETIME, csResult);
                try {
                    JSONObject responseObj = new JSONObject(responseJson);
                    if (responseObj != null) {
                        int status = responseObj.getInt("status");
                        if (1 == status) {// 淘宝正常返回的状态
                            String urlImp = responseObj.optString("url_imp");// 广告位级别的展示反馈链接
                            //TAOBAO_URL_PARAM = responseObj.getString("url_params");//去重复参数
                            new LauncherHttpCommon(urlImp).getResponseAsStringGET();// 发送广告位展示链接
                            JSONArray result = responseObj.getJSONArray("promoters");// 获取单品数组
                            return result;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
}
