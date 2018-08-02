package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * Created by linliangbin on 16-7-12.
 *  订阅号分类bean
 * **/

public class SubscribeCateBean {
    public String cateName;
    //包含的订阅号数
    public int count=10;
    
    public boolean isSelected = false;
    
    public int cateId = 0;
    
    public int flag = 0;
    
    public String icon;
    
    //是否为本地添加的订阅
    public boolean isLocal = false;
    //本地订阅号列表
    public String siteIdString;
    
    public void initDataFromJson(JSONObject jsonObject){
        if(jsonObject == null)
            return;
        cateId = jsonObject.optInt("id");
        cateName = jsonObject.optString("name");
        flag = jsonObject.optInt("flag");
        icon = jsonObject.optString("icon");
    }
    
 }
