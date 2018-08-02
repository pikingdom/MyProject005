package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * Created by linliangbin on 16-7-12.
 * 我的订阅文章BEAN
 */
public class SubscribeArticleBean {
    
    public int articleId;
    
    public String title;
    
    //图片缩略图地址
    public String img;
    
    //来源
    public String from;
    
    //详情地址
    public String url;
    
    //阅读量
    public int readCount;
    
    //更新时间
    public String updateTime;
    
    public void initDataFromJson(JSONObject jsonObject){
    
        if (jsonObject == null)
            return;
        JSONObject articleObject = jsonObject.optJSONObject("articleinfo");
        JSONObject accountObject = jsonObject.optJSONObject("accountinfo");
        if (accountObject != null) {
            articleId = articleObject.optInt("id");
            title = articleObject.optString("title");
            readCount = articleObject.optInt("hitcount");
            img = articleObject.optString("smallpic");
            url = articleObject.optString("detailurl");
        }
        if (accountObject != null) {
            from = accountObject.optString("wxalias");
        }
        

    }
    
}
