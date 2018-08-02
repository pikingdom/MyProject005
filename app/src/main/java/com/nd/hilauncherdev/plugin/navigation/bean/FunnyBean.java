package com.nd.hilauncherdev.plugin.navigation.bean;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * Created by linliangbin_dian91 on 2016/4/26.
 */
public class FunnyBean {

    /**
     * 资源ID
     */
    public int TopicId = 0;

    /**
     * 资源类型
     * 0: 错误类型
     * 1：BANNER图
     * 2：小图
     */
    public static final int NONE_IMG = 0;
    public static final int BIG_IMG = 1;
    public static final int SMALL_IMG = 2;

    public int type;

    /**
     * 标题
     */
    public String Title;

    /**
     * 描述
     */
    public String Summary;

    /**
     * 跳转地址
     */
    public String PageUrl;

    /**
     * 图片地址
     */
    public String ImgUrl;

    public String BannerImgUrl;

    /**
     * 发布日期
     */
    public String publish;

    public void initDataFromJson(JSONObject jsonObject){

        TopicId = jsonObject.optInt("TopicId");
        Title = jsonObject.optString("Title");
        Summary = jsonObject.optString("Summary");
        ImgUrl = jsonObject.optString("ImgUrl");
        BannerImgUrl = jsonObject.optString("BannerImgUrl");
        PageUrl = jsonObject.optString("PageUrl");
        publish = jsonObject.optString("PublishDate");
        if(!TextUtils.isEmpty(BannerImgUrl) && !TextUtils.isEmpty(ImgUrl)){
            type = BIG_IMG;
        }
        if(TextUtils.isEmpty(BannerImgUrl) && !TextUtils.isEmpty(ImgUrl)){
            type = SMALL_IMG;
        }
        if(!TextUtils.isEmpty(BannerImgUrl) && TextUtils.isEmpty(ImgUrl)){
            type = SMALL_IMG;
            ImgUrl = BannerImgUrl;
        }

    }
}
