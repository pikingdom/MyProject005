package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.OtherWebView;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;

public class OpenScreenView extends RelativeLayout {
	private LinearLayout retreat, advance, home, openBrowser;// 前进，后退，home和打开其他浏览器
    private ImageView retreatImage, advanceImage, homeImage, openBrowserImage;// 控制键对应的image
    private ProgressBar progressBar;// 网页加载进度条
    private LinearLayout waitingView;//加载动画
    private OtherWebView pandahomeBrowserWebView;
    private LinearLayout noNetwork;// 无网络界面
    /* 主展示界面控制 */
    private RelativeLayout browser;// 浏览器界面

	public OpenScreenView(Context context) {
		super(context);
		initView();
	}

	public void initView(){
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0,0,0,0);
		addView(View.inflate(getContext(), R.layout.other_webview, null), layoutParams);
		progressBar = (ProgressBar) findViewById(R.id.launcher_navigation_pandahome_browser_web_progressbar_dx);
		advanceImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_advance_image_dx);
		waitingView=(LinearLayout) findViewById(R.id.wait_layout);
		noNetwork = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_nonetwork);
		browser = (RelativeLayout) findViewById(R.id.launcher_navigation_pandahome_browser);
		retreatImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_retreat_image);//后退
		advanceImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_advance_image_dx);//前进
		homeImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_home_image);//home
		openBrowserImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_open_browser_image);//浏览器
		retreat = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_retreat);//后退
		advance = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_advance);//前进
		home = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_home);//home
		openBrowser = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_open_browser);//浏览器
        retreat.setOnClickListener(mWebOnclick);
        advance.setOnClickListener(mWebOnclick);
        home.setOnClickListener(mWebOnclick);
        openBrowser.setOnClickListener(mWebOnclick);
       	pandahomeBrowserWebView = (OtherWebView)findViewById(R.id.custom_webview);
       	pandahomeBrowserWebView.setFocusable(true);
        pandahomeBrowserWebView.setProgressBar(progressBar);
        pandahomeBrowserWebView.setAdvanceImage(advanceImage);
        pandahomeBrowserWebView.setWebViewClient(new WebViewClient(){
          	 @Override
          	public boolean shouldOverrideUrlLoading(WebView view, String url) {
          		 if (url.contains("tel:")) {
						Uri uri=Uri.parse(url);
						try {
							Intent intent=new Intent(Intent.ACTION_DIAL,uri);
							intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
							getContext().startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}else if (url.contains("sms:")) {
						//获取短信号码，包含"sms:"
				        String address = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
				        //获取短信内容，如果没有则为""
				        String body = url.replaceAll(address, "");
				        body = body.contains("=") ? body.substring(body.indexOf("=") + 1) : body;
				        //对于中文内容进行Uri解码
				        body = Uri.decode(body);
				        Uri smsToUri = Uri.parse(address);// 联系人地址
				        try {
							Intent intent=new Intent(Intent.ACTION_SENDTO,smsToUri);
							intent.putExtra("sms_body", body);
							intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
							getContext().startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						return true;
					}
          		return false;
          	}
          	 
          	 @Override
          	public void onPageFinished(WebView view, String url) {
          		 waitingView.setVisibility(View.GONE);
          	}
           });
           pandahomeBrowserWebView.setWebChromeClient(new WebChromeClient(){
          	 @Override
          	public void onProgressChanged(WebView view, int newProgress) {
          		super.onProgressChanged(view, newProgress);
          		if(newProgress == 100){
                      if(advanceImage != null){
                          if(pandahomeBrowserWebView.canGoForward()){
                              advanceImage.setImageResource(R.drawable.webview_right_button);
                          }else{
                              advanceImage.setImageResource(R.drawable.webview_right_button_unclick);
                          }
                      }
                  }
          	}
           });
    	   pandahomeBrowserWebView.loadUrl(CommonGlobal.URL_VIP);
	}
	
	/**
     * 点心Webview底部栏按钮点击事件
     */
    private OnClickListener mWebOnclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int viewId = v.getId();
	        if (!com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil.isNetworkAvailable(getContext())) {
	        	Toast.makeText(getContext(), "网络异常，请检查网络。", Toast.LENGTH_SHORT).show();
	            return;
	        }
	        if (viewId == retreat.getId()) {// 后退
	        	if (pandahomeBrowserWebView != null && pandahomeBrowserWebView.canGoBack()) {
	        		pandahomeBrowserWebView.goBack();
	            }
	            else if(pandahomeBrowserWebView != null){
	            	pandahomeBrowserWebView.goBack();
	            }
	        } else if (viewId == advance.getId()) {// 前进
	        	if (pandahomeBrowserWebView != null && pandahomeBrowserWebView.canGoForward()) {
	        		pandahomeBrowserWebView.goForward();
	            }
	        } else if (viewId == home.getId()) {// 主页
	        	if(pandahomeBrowserWebView != null) {
					pandahomeBrowserWebView.loadUrl(CommonGlobal.URL_VIP);
	            }
	        } else if (viewId == openBrowser.getId()) {// 打开其他浏览器
	        	String url= CommonGlobal.URL_VIP;
	            if(pandahomeBrowserWebView != null && !TextUtils.isEmpty(pandahomeBrowserWebView.getUrl())){
	                url = pandahomeBrowserWebView.getUrl();
	            }
//	            if (url.equals(CommonGlobal.URL_58_ANALYSIS)) {//防止在主页上点击其他浏览器跳转获取到的是解析后的地址导致无法打开
//	            	url = CommonGlobal.URL_58;
//				}
	            //TODO：改成自己的
	            LauncherCaller.openUrl(getContext(),null,url);
	        }
			
		}
	};
	
}
