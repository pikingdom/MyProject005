package com.nd.hilauncherdev.plugin.navigation.widget.webview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.web.CustomWebViewClient;
import com.nd.hilauncherdev.plugin.navigation.widget.browser.IWebLoadUrlListener;

/**
 * Created by chenxuyu_dian91 on 2015/10/27.
 */
public class CustomWebView extends BaseWebView {

    private ProgressBar progressBar;
    private ImageView advanceImage;

    private Context context;
    private Context pluginContext;
    private String lastTitle;

    private CustomWebViewClient mWebViewClient;
    private WebLifeCycle mWebLifeCycle;

    public CustomWebView(Context ctx,Context pluginContext) {
        super(ctx);
        context = ctx;
        this.pluginContext = pluginContext;
        initSetting();
    }

    public CustomWebView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        context = ctx;
        this.pluginContext = getContext();
        mWebLifeCycle = new CustomWebLifeCycle(this);
        initSetting();
    }

    private void initSetting() {
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
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);

        mWebViewClient = new CustomWebViewClient(pluginContext != null ? pluginContext : context);
        mWebViewClient.setNeedInterruptDownload(true);
        mWebViewClient.setOnActionDispatch(new CustomWebViewClient.OnActionDispatch() {
            @Override
            public void startDownload(String downloadUrl) {
                startDownloadService(downloadUrl);
            }
        });

        this.setWebViewClient(mWebViewClient);

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress != 100) {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    }
                } else {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (advanceImage != null) {
                        if (canGoForward()) {
                            advanceImage.setImageResource(R.drawable.webview_right_button);
                        } else {
                            advanceImage.setImageResource(R.drawable.webview_right_button_unclick);
                        }
                    }
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                lastTitle = title;
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

    private void startDownloadService(String url) {
        try {
            String identification = "download_" + System.currentTimeMillis();
            String title = (TextUtils.isEmpty(lastTitle) ? "来自网页" : lastTitle) + "_" + System.currentTimeMillis();
            Intent intent = new Intent(context.getPackageName() + ".FORWARD_SERVICE");
            intent.putExtra("identification", identification);
            intent.putExtra("fileType", 0);
            intent.putExtra("downloadUrl", url);
            intent.putExtra("title", title);
            intent.putExtra("savedDir", CommonGlobal.WIFI_DOWNLOAD_PATH);
            intent.putExtra("savedName", identification);
            intent.putExtra("iconPath", "");
            intent.putExtra("totalSize", "");
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void onWebPause() {
        mWebViewClient.onPause();
        mWebLifeCycle.onPause();
    }

    public void onWebResume() {
        mWebLifeCycle.onResume();
    }

    public void setWebLoadUrlListener(IWebLoadUrlListener listener) {
        this.mWebViewClient.setWebLoadUrlListener(listener);
    }
}

