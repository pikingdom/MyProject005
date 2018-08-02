package com.nd.hilauncherdev.plugin.navigation.util;

import android.os.Build;
import android.webkit.WebView;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WebView安全工具
 */
public class WebViewSecureUtil {

	public static void checkWebViewSecure(WebView webView){
		if(Build.VERSION.SDK_INT >=11 && webView!=null){
			Class<? extends WebView> wbClass = webView.getClass();
			try {
				Method wbMethod = wbClass.getDeclaredMethod("removeJavascriptInterface", String.class);
				wbMethod.invoke(webView, "searchBoxJavaBridge_");
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}  
		}
	}
	
	/**
	 * 禁止加载本机文件及非自有域名下的url
	 * @param loadUrl
	 * @return 是否安全
	 */
	public static boolean checkWebUrlSecure(String loadUrl){
		
		if (loadUrl!=null){
			String urlStr = loadUrl.toLowerCase();
			if ( urlStr.startsWith("file://") || !isSecureWebUrl(urlStr) || isJavaScriptWebUrl(urlStr) ){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 是否自有域名下url
	 * @param urlStr
	 * @return
	 */
	private static boolean isSecureWebUrl(String urlStr){
		return true;
//		if (urlStr!=null){
//			String host = getHost(urlStr);
//			if (host!=null){
//				return host.endsWith("91.com");
//			}
//		}
//		return false;
	}
	
	/**
	 * 
	 * @param urlStr
	 * @return
	 */
	private static boolean isJavaScriptWebUrl(String urlStr){
		if (urlStr!=null){
			String lowUrlStr = urlStr.toLowerCase();
			if (lowUrlStr!=null){
				return lowUrlStr.indexOf("javascript")!=-1;
			}
		}
		return false;
	}
	
	public static String getHost(String url) {
		if (url == null || url.trim().equals("")) {
			return "";
		}
		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		Matcher matcher = p.matcher(url);
		if (matcher.find()) {
			host = matcher.group();
		}
		return host;
	}
}
