package com.nd.hilauncherdev.plugin.navigation.activity;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OtherWebView extends WebView{
	 private ProgressBar progressBar;
	    private ImageView advanceImage;

	    public OtherWebView(Context ctx){
	        super(ctx);
	        initSetting();
	    }

	    public OtherWebView(Context context, AttributeSet attrs){
	        super(context,attrs);
	        initSetting();
	    }

	    private void initSetting(){
	        WebSettings webSetting = this.getSettings();
	        webSetting.setJavaScriptEnabled(true);//打开JS
	        webSetting.setAllowFileAccess(true);
	        webSetting.setSupportZoom(true);//支持拉伸
	        webSetting.setBuiltInZoomControls(true);//加入拉伸工具条
	        webSetting.setUseWideViewPort(true);//
	        webSetting.setSupportMultipleWindows(false);
	        webSetting.setLoadWithOverviewMode(true);
	        webSetting.setAppCacheEnabled(true);
	        webSetting.setDatabaseEnabled(true);
	        webSetting.setDomStorageEnabled(true);
	        webSetting.setGeolocationEnabled(true);
	        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);

	        this.setWebViewClient(new WebViewClient() {
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                return false;
	            }

	            @Override
	            public void onPageFinished(WebView view, String url) {
	                super.onPageFinished(view, url);
	            }

	            @Override
	            public void onPageStarted(WebView view, String url, Bitmap favicon) {
	                super.onPageStarted(view, url, favicon);
	            }

	        });

	        this.setWebChromeClient(new WebChromeClient() {
	            @Override
	            public void onProgressChanged(WebView view, int newProgress) {

	                if (progressBar != null) {
	                    if (newProgress != 100) {
	                        progressBar.setVisibility(View.VISIBLE);
	                        progressBar.setProgress(newProgress);
	                    } else {
	                        progressBar.setVisibility(View.GONE);
	                        if (advanceImage != null) {
	                            if (canGoForward()) {
	                                advanceImage.setImageResource(R.drawable.webview_right_button);
	                            } else {
	                                advanceImage.setImageResource(R.drawable.webview_right_button);
	                            }
	                        }
	                    }
	                }
	                super.onProgressChanged(view, newProgress);
	            }
	        });
	        this.setDownloadListener(new android.webkit.DownloadListener() {
	            @Override
	            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
	                Uri uri = Uri.parse(url);
	                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	                SystemUtil.startActivitySafely(getContext(), intent);
	            }
	        });

	    }

	    /**
	     * 设置前进按钮
	     * <p>
	     * Title: setAdvanceImage
	     * </p>
	     * <p>
	     * Description:
	     * </p>
	     *
	     * @param advanceImage
	     * @author maolinnan_350804
	     */
	    public void setAdvanceImage(ImageView advanceImage) {
	        this.advanceImage = advanceImage;
	    }

	    /**
	     * 设置进度条
	     * <p>
	     * Title: setProgressBar
	     * </p>
	     * <p>
	     * Description:
	     * </p>
	     *
	     * @param progressBar
	     * @author maolinnan_350804
	     */
	    public void setProgressBar(ProgressBar progressBar) {
	        this.progressBar = progressBar;
	    }



	    @Override
	    public void goBack() {
	        super.goBack();
	    }

	    @Override
	    public void goForward() {
	        super.goForward();
	    }
}
