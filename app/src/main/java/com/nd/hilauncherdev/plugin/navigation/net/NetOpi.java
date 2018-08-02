package com.nd.hilauncherdev.plugin.navigation.net;

import android.content.Context;

import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeCateBean;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.ServerResultHeader;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linliangbin on 2017/5/10 10:01.
 */

public class NetOpi {


    /**
     * 获取壁纸软文接口
     *
     * @param context
     * @return
     */
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_GIF = 2;
    public static final int MEDIA_TYPE_VIDEO_AND_GIF = 3;
    public static final int MEDIA_TYPE_VIDEP_AND_VIDEOPAPER = 4;
    public static final int SORT_TYPE_RECOMMEND = 1;
    public static final int SORT_TYPE_NEW = 2;

    /**
     * 获取壁纸软文接口
     *
     * @param context
     * @return
     */


    public static String loadWallpaperAndText(Context context, int type, int lastPicId, int lastTextId) {

        ArrayList<SubscribeCateBean> cateBeans = new ArrayList<SubscribeCateBean>();

        try {
            JSONObject paramsJO = new JSONObject();
            paramsJO.put("Type", type);
            paramsJO.put("Density", ScreenUtil.getDensity(context));
            paramsJO.put("Resolution", ScreenUtil.getCurrentScreenWidth(context) + "x" + ScreenUtil.getCurrentScreenHeight(context));
            paramsJO.put("PageSize", 20);
            paramsJO.put("PicId", lastPicId);
            paramsJO.put("TextId", lastTextId);

            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/wallpaperaction/7061");
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

    public static String loadVideoList(Context context, int pageIndex, int pageSize, int mediaType, int sortType) {


        try {
            JSONObject paramsJO = new JSONObject();
            paramsJO.put("PageIndex", pageIndex);
            paramsJO.put("PageSize", pageSize);
            paramsJO.put("MediaType", mediaType);
            paramsJO.put("SortType", sortType);

            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/wallpaperaction/7062");
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
     * @desc 获取推荐书籍数据
     * @author linliangbin
     * @time 2017/8/7 16:13
     */
    public static String loadRecommendBookList(Context context, int pageIndex, int pageSize) {
        return loadRecommendBookList(context, 0, pageIndex, pageSize);
    }

    public static String loadRecommendBookList(Context context, int type, int pageIndex, int pageSize) {
        try {
            ServerResultHeader csResult = loadRecommenFromNet(context, type, pageIndex, pageSize);
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

    public static ServerResultHeader loadRecommenFromNet(Context context, int type, int pageIndex, int pageSize) {

        try {
            JSONObject paramsJO = new JSONObject();
            paramsJO.put("PageIndex", pageIndex);
            paramsJO.put("PageSize", pageSize);
            paramsJO.put("Type", type);
            String jsonParams = paramsJO.toString();
            HashMap<String, String> paramsMap = new HashMap<String, String>();
            LauncherHttpCommon.addGlobalRequestValue(paramsMap, context, jsonParams);
            LauncherHttpCommon httpCommon = new LauncherHttpCommon(URLs.PANDAHOME_BASE_URL + "action.ashx/otheraction/9055");
            ServerResultHeader csResult = httpCommon.getResponseAsCsResultPost(paramsMap, jsonParams);
            return csResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
