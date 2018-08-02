package com.nd.hilauncherdev.plugin.navigation.bean;


/**
 * 热词<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class HotwordItemInfo {
	public static final String TYPE_BAIDU_HOTWORD = "1"; 
	public static final String TYPE_TAOBAO_HOTWORD = "2"; 
	public static final String TYPE_EASOU_HOTWORD = "3"; 
	public static final String TYPE_91_HOTWORD = "4";
	/**
	 * 推荐应用类型
	 */
	public static final String TYPE_APP_HOTWORD = "5";
	/**
	 * 实时热词类型
	 */
	public static final String TYPE_REAL_TIME_HOTWORD = "6";
	/**
	 * 启动桌面内部界面热词类型
	 */
	public static final String TYPE_LAUNCHER_HOTWORD = "7";
	
	/**
	 * 桌面功能
	 */
	public static final String TYPE_LAUNCHER_FUNCTION = "8";

	/**
	 * 本地引导词
	 */
	public static final String TYPE_LOCAL_WORD = "9";

	/**
	 * CV 统计使用resId
	 */
	public int resId;
	
	public String id;
	
	public String identifier;
	
	public String name;
	
	public String author;
	
	public int star;
	
	public String size;
	
	public String iconUrl;
	
	public String downloadUrl;
	
	public String type;
	
	public String appType;//应用类型
	
	public int color = 0xff333333;
	
	/**
	 * 高亮颜色(highLightColor), 用于点击态或者focus时
	 */
	public int hlColor = 0xff0000ff;
	
	public String pubTime;
	
	public String updateTime;
	
	public String downloadNum;
	
	public String version;
	
	public int versionCode;
	
	public String detailUrl;
	
	public String desc;
	
	public Object tag;
	
	public int state = -1;

	public HotwordItemInfo() {}
	
	public HotwordItemInfo(String name, String url, String type) {
		this.name = name;
		this.detailUrl = url;
		this.type = type;
	}
	
	public HotwordItemInfo(String name, String url, String type, int color) {
		this.name = name;
		this.detailUrl = url;
		this.type = type;
		this.color = color;
	}
}
