package com.nd.hilauncherdev.plugin.navigation.bean;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.util.DateUtil;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

/**
 * Created by linxiaobin on 2017/9/18.
 * 淘宝合辑数据
 */

public class TaoBaoCollection {
    private String title;
    private String bannerUrl;
    private String startTime;
    private String endTime;
    private int hasNext;
    private List<TaobaoProduct> list;

    public String getTitle() {
        return title;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public long getStartTimeMills() {
        if (TextUtils.isEmpty(startTime)) return 0;
        return DateUtil.strToDate(startTime).getTime();
    }

    public long getEndTimeMills() {
        if (TextUtils.isEmpty(endTime)) return 0;
        return DateUtil.strToDate(endTime).getTime();
    }

    public boolean isHasNext() {
        return hasNext == 1;
    }

    public List<TaobaoProduct> getList() {
        return list;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public static TaoBaoCollection beanFromJson(Context context, JSONObject jsonObject) {
        if (jsonObject == null) return null;

        TaoBaoCollection rlt = new TaoBaoCollection();
        @SuppressWarnings("unchecked")
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (jsonObject.isNull(key)) continue;

            switch (key) {
                case "title": {
                    rlt.title = jsonObject.optString(key);
                    break;
                }
                case "banner": {
                    rlt.bannerUrl = jsonObject.optString(key);
                    break;
                }
                case "starttime": {
                    rlt.startTime = jsonObject.optString(key);
                    break;
                }
                case "endtime": {
                    rlt.endTime = jsonObject.optString(key);
                    break;
                }
                case "hasnext": {
                    rlt.hasNext = jsonObject.optInt(key);
                    break;
                }
                case "list": {
                    rlt.list = TaobaoProduct.beansFromJson(context, jsonObject.optJSONArray(key));
                    break;
                }
            }
        }

        return rlt;
    }
}
