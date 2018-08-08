package com.nd.hilauncherdev.plugin.navigation;

import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherLibUtil;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.HttpCommon;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.URLs;

import android.content.Context;
import android.os.Environment;

import java.net.URLEncoder;

/**
 * 导航数据加载
 *
 * @author chenzhihong_9101910
 *
 */
public class NavigationData {
	public static Context context;

    private final static String NAVIGATION_PATH = Environment.getDataDirectory() + "/data/" + Global.getPackageName()
	+ "/files/navigation.txt";
	private final static String NAVIGATION_PATH_EN = Environment.getDataDirectory() + "/data/" + Global.getPackageName()
	+ "/files/navigation_en.txt";

	/**
	 * 本地缺省网址导航数据
	 */
	private static final String[] defaultNavCategory = {
			"综合", "新闻", "小说", "生活",
			"社交", "团购", "科技", "购物",
			"彩票", "旅游", "体育", "视频",
			"音乐", "便民", "查询", "游戏" };
	private static final int[] defaultNavCategoryIcon = {
		R.drawable.launcher_navigation_category_complex,
		R.drawable.launcher_navigation_category_news,
		R.drawable.launcher_navigation_category_novel,
		R.drawable.launcher_navigation_category_life,
		R.drawable.launcher_navigation_category_community,
		R.drawable.launcher_navigation_category_group_purchase,
		R.drawable.launcher_navigation_category_tech,
		R.drawable.launcher_navigation_category_shopping,
		R.drawable.launcher_navigation_category_cai,
		R.drawable.launcher_navigation_category_travel,
		R.drawable.launcher_navigation_category_sports,
		R.drawable.launcher_navigation_category_video,
		R.drawable.launcher_navigation_category_music,
		R.drawable.launcher_navigation_category_convenience,
		R.drawable.launcher_navigation_category_search,
		R.drawable.launcher_navigation_category_game
	};
	private static final String[][] defaultNavTitle = {};
	private static final String[][] defaultNavUrl = {};

	/**
	 * 本地缺省网址导航数据 (英文版)
	 */
	private static final String[] defaultNavCategoryEn = {
			"Social", "Photo", "Entertain", "Blog",
			"Email", "News", "Tech", "Sports",
			"Hot Video", "Video", "Pop Music", "Music",
			"Daily", "Queries", "Shopping", "Delivery" };
	private static final int[] defaultNavCategoryIconEn = {
		R.drawable.launcher_navigation_category_community,
		0,
		0,
		0,
		0,
		R.drawable.launcher_navigation_category_news,
		R.drawable.launcher_navigation_category_tech,
		R.drawable.launcher_navigation_category_sports,
		R.drawable.launcher_navigation_category_video,
		R.drawable.launcher_navigation_category_video,
		R.drawable.launcher_navigation_category_music,
		R.drawable.launcher_navigation_category_music,
		0,
		0,
		R.drawable.launcher_navigation_category_shopping,
		0
	};
	private static final String[][] defaultNavTitleEn = {
			{ "Twitter", "Facebook", "Google+", "Match", "MocoSpace", "Linkedin"},
			{ "Imgur", "500px", "Flickr", "Picasa", "Photobucket", "TinyPic"},
			{ "Y!OMG", "Horoscope", "9Gag"},
			{ "Blogger", "Tumblr", "WordPress"},

			{ "Hotmail", "Y!Mail", "Gmail"},
			{ "CNN", "FOX", "NYTimes", "HuffPost", "WSJ", "Reuters"},
			{ "Techcrunch", "TheVerge", "CNet", "Engadget", "Slashdot", "Thinkdigit"},
			{ "Analysis", "ESPN", "Y!Sports", "FOX"},

			{ "Hollywood", "Upcoming"},
			{ "IMDB", "Hulu", "Veoh", "Youtube", "Break", "Vevo"},
			{ "Artists", "Top 100", "Premieres", "Country", "Hip Hop", "80s"},
			{ "Last.fm", "BBC Music", "MTV", "Billboard", "Metrolyrics", "Bigpond", "Vuclip", "Twitmusic", "Insound"},

			{ "Weather", "Health", "Travel", "Zillow", "Dexknows", "Craigslist"},
			{ "Wikipedia", "About", "Ask", "DMOZ", "Y!Direct", "YellowPages"},
			{ "Amazon", "eBay", "Paypal", "Bestbuy", "Groupon", "Walmart"},
			{ "Fedex", "UPS", "USPS"}};
	private static final String[][] defaultNavUrlEn = {
			// Social
			{ "http://mobile.twitter.com/session/new",
			  "http://m.facebook.com/",
			  "https://m.google.com/app/plus/x/",
			  "http://mobile.match.com/",
			  "http://m.mocospace.com/",
			  "https://touch.www.linkedin.com/" },

			//Photo
			{ "http://m.imgur.com/",
			  "http://500px.com/popular",
			  "http://m.flickr.com/#/home",
			  "http://picasaweb.google.com/m/",
			  "http://photobucket.com/",
			  "http://m.tinypic.com/" },

			//Entertainment
			{ "http://omg.yahoo.com/",
			  "http://shine.yahoo.com/horoscope/",
			  "http://m.9gag.com/"},

			//Blog
			{ "http://www.blogger.com/",
			  "http://m.tumblr.com/",
			  "http://m.wordpress.com/"},

			// Email
			{ "http://hotmail.com",
			  "http://yahoo.com/",
			  "https://mail.google.com/"},

			//News
			{ "http://cnnmobile.com/",
			  "http://www.foxnews.mobi/",
			  "http://mobile.nytimes.com/",
			  "http://m.huffpost.com/us/",
			  "http://m.wsj.com/",
			  "http://mobile.reuters.com/home?irpc=932" },

			//Tech
			{ "http://techcrunch.com/",
			  "http://mobile.theverge.com/",
			  "http://m.cnet.com/?ds=1",
			  "http://www.engadget.com/",
			  "http://m.slashdot.org/",
			  "http://www.thinkdigit.com/"},

			//Sports Express
			{ "http://m.espn.go.com/wireless/analysis",
			  "http://m.espn.go.com/wireless/",
			  "http://m.yahoo.com/w/sports",
			  "http://t.foxsports.msn.com/"},

			//Hot Video
			{ "http://www.hollywood.com/movies/",
			  "http://www.imdb.com/movies-coming-soon/" },

			//Video Sites
			{ "http://m.imdb.com/",
			  "http://www.hulu.com/",
			  "http://www.veoh.com/",
			  "http://m.youtube.com/",
			  "http://www.break.com/",
			  "http://m.vevo.com/videos"},

			//Pop music
			{"http://m.metrolyrics.com/artists.html",
			  "http://m.metrolyrics.com/top100.html",
			  "http://www.mtv.com/music/videos/premieres/",
			  "http://mp3.com/top-downloads/genre/country/",
			  "http://mp3.com/top-downloads/genre/hip%20hop/",
			  "http://mp3.com/top-downloads/genre/80s/" },

			//Music Sites
			{"http://m.last.fm/",
			  "http://www.bbc.co.uk/music",
			  "http://m.mtv.com/",
			  "http://m.billboard.com/",
			  "http://m.metrolyrics.com/",
			  "http://bigpondmusic.com/",
			  "http://m.vuclip.com/",
			  "http://www.twitmusic.com/songs",
			  "http://www.insound.com/digital/free-mp3s/" },

			//Daily Life
			{ "http://m.ewather.com/forecast",
			  "http://m.weather.com/health/main",
			  "http://travel.yahoo.com/",
			  "http://www.zillow.com/",
			  "http://m.dexknows.com/",
			  "http://mobile.craigslist.org/"},

			//Daily Queries
			{ "http://en.m.wikipedia.org/wiki/Main_Page",
			  "http://m.about.com/",
			  "http://www.ask.com/answers/browse",
			  "http://www.dmoz.org/",
			  "http://dir.yahoo.com/",
			  "http://www.yellowpages.com/"},

			//Online Shopping
			{ "http://www.amazon.com/",
			  "http://mobileweb.ebay.com/",
			  "http://mobile.paypal.com/us/cgi-bin/wapapp?cmd=_wapapp-homepage",
			  "http://m.bestbuy.com/m/e/digital/",
			  "http://touch.groupon.com/intercept",
			  "http://mobile.walmart.com/"},

			//Express Delivery
			{ "http://m.fedex.com/",
			  "http://m.ups.com/",
			  "http://mobile.usps.com/"}
	};

	public static String[] getDefaultNavCategory(Context ctx){
		if(Global.isZh(ctx))
			return defaultNavCategory;

		return defaultNavCategoryEn;
	}

	public static int[] getDefaultNavCategoryIcon(Context ctx){
		if(Global.isZh(ctx))
			return defaultNavCategoryIcon;

		return defaultNavCategoryIconEn;
	}

	public static String[][] getDefaultNavTitle(Context ctx){
		if(Global.isZh(ctx))
			return defaultNavTitle;

		return defaultNavTitleEn;
	}

	public static String[][] getDefaultNavUrl(Context ctx){
		if(Global.isZh(ctx))
			return defaultNavUrl;

		return defaultNavUrlEn;
	}


	public static String addMoreParams(Context ctx,String url){
		StringBuffer sb = new StringBuffer(url);
		try {
			HttpCommon.appendAttrValue(sb, "pid", LauncherHttpCommon.getPid());
			HttpCommon.appendAttrValue(sb, "DivideVersion", LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getDivideVersion(ctx)));
			HttpCommon.appendAttrValue(sb, "mac", URLEncoder.encode(TelephoneUtil.getMAC(ctx)));
			HttpCommon.appendAttrValue(sb, "Supfirm", TelephoneUtil.getFirmWareVersion());    //Android版本号
			HttpCommon.appendAttrValue(sb, "SupPhone", URLEncoder.encode(TelephoneUtil.getMachineName())); //型号
			HttpCommon.appendAttrValue(sb, "IMEI", LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getIMEI(ctx)));
			HttpCommon.appendAttrValue(sb, "IMSI", LauncherHttpCommon.utf8URLencode(LauncherLibUtil.getIMSI(ctx)));
			HttpCommon.appendAttrValue(sb, "CUID", URLEncoder.encode(LauncherLibUtil.getCUID(ctx)));
			return sb.toString();
		} catch (Exception e) {
			return url;
		}
	}

	public static String getNavigationUrl(Context ctx){
		return URLs.NAVIGATION_URL;
	}

	public static String getNavigationPath(Context ctx){
		if(Global.isZh(ctx))
			return NAVIGATION_PATH;

		return NAVIGATION_PATH_EN;
	}
}
