package com.nd.hilauncherdev.plugin.navigation.bean;

import org.json.JSONException;
import org.json.JSONObject;


public class TaobaoProductsItem {
	public static final int CATEGORY_PRODUCTS = 16;// 单品广告
	public static final int TYPE_IC = 1;
	public static final int TYPE_TEXT = 2;

	public int cvid;//CV 统计专用ID
	public String promoter;// 物料id
	public int category;// 广告来源
	public String urlClick = "";// 广告点击跳转链接
	public String imageUrl = "";// 广告图片地址
	public String title = "";// 广告文案
	public double price;// 商品单价
	public double promoprice;// 折扣价
	
	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("urlClick", urlClick);
			jo.put("imageUrl", imageUrl);
			jo.put("title", title);
			jo.put("cvid",cvid);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jo;
	}

	public static TaobaoProductsItem fromJson(JSONObject jo) {
		TaobaoProductsItem taobaoProductsItem = new TaobaoProductsItem();
		taobaoProductsItem.urlClick = jo.optString("urlClick");
		taobaoProductsItem.imageUrl = jo.optString("imageUrl");
		taobaoProductsItem.title = jo.optString("title");
		taobaoProductsItem.cvid = jo.optInt("cvid");
		return taobaoProductsItem;
	}
}
