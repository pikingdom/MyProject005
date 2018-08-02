package com.nd.hilauncherdev.plugin.navigation.bean;

import java.util.Date;

/**
 * description: 淘宝-限时抢购<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/18<br/>
 */
public class TaobaoSplashSale {

    public String promoter;// 物料id
    public String urlClick;// 广告点击跳转链接
    public String imageUrl;// 广告图片地址
    public String title;// 广告文案
    public String price;// 商品单价
    public String finalPrice;// 折扣价

    /**
     * 当前已购买数
     */
    public int saleProgress;

    /**
     * 总需购买数
     */
    public int totalSale;

    /**
     * 抢购结束时间 2016-01-20 12:00:00
     */
    public long endTime;

    /**
     * 抢购开始时间 2016-01-20 12:00:00
     */
    public long startTime;

    /**
     * 当前服务器时间
     */
    public long currentTime;

    /**
     * 更多链接
     */
    public String linkMore;
}
