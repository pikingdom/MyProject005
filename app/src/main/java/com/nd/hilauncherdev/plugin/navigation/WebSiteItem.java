package com.nd.hilauncherdev.plugin.navigation;

import android.graphics.Bitmap;

/**
 * 网址
 * 
 * @author chenzhihong_9101910
 * 
 */
public class WebSiteItem {
	
	//用于本地图标
	public int iconId;
	//用于服务器上图标
	public String iconURL = null;
	//用于本地文件ICON
	public String iconPath = null;
	public Bitmap icon = null;
	/** 图标类型 */
	public static final int TYPE_SERVER_ICON = 1;
	public static final int TYPE_LOCAL_FILE_ICON = 2;
	public static final int TYPE_LOCAL_RES_ICON = 3;
	public static final int TYPE_USE_APP_ICON = 4;
	public int iconType = TYPE_SERVER_ICON;
	
	/** 文本资源ID **/
	public String name;
	
	/** 链接地址 **/
	public String url;
	
	public String siteId = null;

	//618曝光统计：使用ID
	public int AdSourceId = 0;
	//618曝光统计：使用统计
	public int CallBack = 0;

	//点击打开方式: 0-先默认浏览器，后系统浏览器 1-先默认浏览器，后优选框   默认为0
	public int openType = 0;

	//打开地址时是否拼接Session
	public boolean needSession=false;

	//网址类型：0 为推荐网址，1为用户添加的网址， 2为用户最近浏览网址，3为“添加”网址
	public static final int TYPE_RECOMMAND = 0;
	public static final int TYPE_FAVORITE = 1;
	public static final int TYPE_RECENT_BROWSE = 2;
	public static final int TYPE_ADD = 3;
	public int type = TYPE_RECOMMAND;


	/** 点击图标时的动作类型 */
	public static final int ACTION_TYPE_OPEN_URL = 0;
	public static final int ACTION_TYPE_OPEN_APP = 1;
	public static final int ACTION_TYPE_LOCAL_ACTION_SITE_DETAIL = 2;
	public int actionType = ACTION_TYPE_OPEN_URL;
	/** 点击时打开应用的包名 */
	public String appPkg = "";
	/** 点击时打开应用的类名 */
	public String appClass = "";



	public WebSiteItem(){
	}
	
	public WebSiteItem(String name, String url){
		this.name = name;
		this.url = url;
	}
	
	public WebSiteItem(String name, String url, String siteId){
		this(name, url);
		this.siteId = siteId;
	}
}
