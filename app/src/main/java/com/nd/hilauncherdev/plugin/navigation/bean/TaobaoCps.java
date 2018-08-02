package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxuyu on 2016/5/5.
 */
public class TaobaoCps {

    public int id;
    public String picUrl;
    public String linkUrl;
    public String title;
    public String subTitle;
    public long startTime;
    public long endTime;

    public int adSourceId;
    public int callBackFlag;

    public static List<TaobaoCps> convertFromJsonString(String responseJson) {
        List<TaobaoCps> list = new ArrayList<TaobaoCps>();
        JSONArray jsonArray = null;
        try {
            if (responseJson != null && responseJson.length() > 0) {
                jsonArray = new JSONObject(responseJson).optJSONArray("List");
                if (jsonArray != null) {
                    for (int i = 0, len = jsonArray.length(); i < len; i++) {
                        JSONObject jsonE = jsonArray.getJSONObject(i);
                        if (jsonE != null) {
                            TaobaoCps item = new TaobaoCps();
                            item.id = jsonE.optInt("Id");
                            item.linkUrl = jsonE.optString("LinkUrl");
                            item.picUrl = jsonE.optString("ImgUrl");
                            item.title = jsonE.optString("Title");
                            item.subTitle = jsonE.optString("SubTitle");
                            item.startTime = 0;
                            item.endTime = 0;
                            item.adSourceId = jsonE.optInt("AdSourceID");
                            item.callBackFlag = jsonE.optInt("CallBack");

                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String start = jsonE.optString("StartTime");
                                String end = jsonE.optString("EndTime");
                                if (!TextUtils.isEmpty(start)) {
                                    item.startTime = dateFormat.parse(start).getTime();
                                }
                                if (!TextUtils.isEmpty(end)) {
                                    item.endTime = dateFormat.parse(end).getTime();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            list.add(item);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
