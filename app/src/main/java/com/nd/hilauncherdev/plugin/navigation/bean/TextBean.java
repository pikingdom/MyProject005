package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONObject;

/**
 * 软文: 短文 + 作者
 * Created by linliangbin on 2017/5/10 10:23.
 */

public class TextBean {


    public int textId = 0;
    public String content = "";
    public String author = "";


    public TextBean(JSONObject dataJo) {

        content = dataJo.optString("Content");
        author = dataJo.optString("Author");
        textId = dataJo.optInt("TextId");
    }

}
