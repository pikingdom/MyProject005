package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by linliangbin on 16-7-12.
 * 微传阅卡片
 * 订阅号
 */
public class SubscribeSiteBean {
    
    //当前数据是否为默认数据
    public boolean isDummyData=false;
    //所属的cateId
    public int belongToCate = -1;
    //订阅号ID
    public int siteID ;
    
    public String siteName;
    
    public String summary;
    
    public String siteIcon;
    
    public String updateTime;
    
    public int subCount;
    
    public String cateName;
    
    public int flag;
    
    public int isauth;
    
    public void initDataFromJson(JSONObject jsonObject){
        if(jsonObject == null)
            return;
        siteID = jsonObject.optInt("id");
        siteName = jsonObject.optString("wxalias");
        subCount = jsonObject.optInt("subcount");
        cateName = jsonObject.optString("name");
        siteIcon = jsonObject.optString("logourl");
        flag = jsonObject.optInt("flag");
        summary = jsonObject.optString("description");
        if(!TextUtils.isEmpty(summary)){
            summary = summary.trim();
        }
        cateName = jsonObject.optString("categoryname");
        updateTime = jsonObject.optString("lastarticledate");
        
    }
}
