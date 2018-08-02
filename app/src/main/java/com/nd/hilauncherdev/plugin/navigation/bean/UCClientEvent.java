package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: </br>
 * @author: cxy </br>
 * @date: 2017年09月04日 18:49.</br>
 * @update: </br>
 */

public class UCClientEvent {

    /**
     * 事件类型。此处ac必须等于1
     */
    public int ac = 1;
    /**
     * 事件发生的时间，毫秒
     */
    public long tm;
    /**
     * 文章ID
     */
    public String aid;
    /**
     * 子文ID，暂无用
     */
    public String subAid;
    /**
     * 批次ID
     */
    public String recoid;
    /**
     * 阅读时长
     */
    public long duration;
    /**
     * 频道ID
     */
    public long cid;
    /**
     * 文章类型
     */
    public int itemType;
    /**
     * 为空
     */
    public String content;

    public UCClientEvent() {

    }

    /**
     * 开始阅读
     */
    public void startRead() {
        tm = System.currentTimeMillis();
    }

    /**
     * 结束阅读
     */
    public void endRead() {
        if (tm <= 0) {
            //需要先展示再关闭
            return;
        }
        duration = System.currentTimeMillis() - tm;
    }

    public JSONObject toJsonObject() {
        try {
            JSONObject json = new JSONObject();
            json.put("ac", ac);
            json.put("tm", tm);
            json.put("aid", aid);
            if (!TextUtils.isEmpty(subAid)) {
                json.put("sub_aid", subAid);
            }
            json.put("recoid", recoid);
            json.put("duration", duration);
            json.put("cid", cid);
            json.put("item_type", itemType);
            if (!TextUtils.isEmpty(content)) {
                json.put("content", content);
            }
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UCClientEvent format(News news) {
        if (news == null) {
            return null;
        }

        if (TextUtils.isEmpty(news.ucAid) || news.ucChannelId == -1 || TextUtils.isEmpty(news.ucRecoId)) {
            return null;
        }

        UCClientEvent event = new UCClientEvent();
        event.aid = news.ucAid;
        event.recoid = news.ucRecoId;
        event.cid = news.ucChannelId;
        event.itemType = news.ucItemType;

        return event;
    }

    public static UCClientEvent format(JSONObject json) {
        if (json != null) {
            UCClientEvent event = new UCClientEvent();
            event.ac = json.optInt("ac", 1);
            event.tm = json.optLong("tm");
            event.aid = json.optString("aid");
            event.subAid = json.optString("sub_aid");
            event.recoid = json.optString("recoid");
            event.cid = json.optInt("cid");
            event.itemType = json.optInt("item_type");
            event.content = json.optString("content");
            return event;
        }
        return null;
    }

    @Override
    public String toString() {
        return "UCClientEvent{" +
                "ac=" + ac +
                ", tm=" + tm +
                ", aid='" + aid + '\'' +
                ", subAid='" + subAid + '\'' +
                ", recoid='" + recoid + '\'' +
                ", duration=" + duration +
                ", cid=" + cid +
                ", itemType=" + itemType +
                ", content='" + content + '\'' +
                '}';
    }
}
