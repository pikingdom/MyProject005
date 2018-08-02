package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 淘宝商品类目<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/22<br/>
 */
public class TaobaoCate {

    /**
     * 类名
     */
    public String name;
    
    /**
     * ID
     */
    public int id;

    /**
     * 跳转链接
     */
    public String url;

    /**
     * 图标链接
     */
    public String picUrl;

    public int adSourceId;

    public int callBackFlag;

    public static List<TaobaoCate> convertFromJsonString(String responseJson){
        List<TaobaoCate> list = new ArrayList<TaobaoCate>();
        JSONArray jsonArray = null;
        try {
            if (responseJson != null && responseJson.length() > 0) {
                jsonArray = new JSONObject(responseJson).optJSONArray("List");
                if (jsonArray != null) {
                    for (int i = 0, len = jsonArray.length(); i < len; i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj == null) continue;
                        TaobaoCate cate = new TaobaoCate();
                        cate.picUrl = obj.optString("IconUrl");
                        cate.name = obj.optString("Name");
                        cate.url = obj.optString("LinkUrl");
                        cate.id = obj.optInt("CatlId");
                        cate.adSourceId = obj.optInt("AdSourceID");
                        cate.callBackFlag = obj.getInt("CallBackFlag");
                        list.add(cate);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
