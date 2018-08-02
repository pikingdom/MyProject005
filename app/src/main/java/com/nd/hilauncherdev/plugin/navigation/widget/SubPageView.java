package com.nd.hilauncherdev.plugin.navigation.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.dynamic.plugin.PluginActivity;
import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.DialogCommon;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.web.CustomWebViewClient;
import com.nd.hilauncherdev.plugin.navigation.widget.webview.CustomWebView;

import java.lang.reflect.Field;


/**
 * Created by linliangbin_dian91 on 2016/2/15.
 * 活动界面，展示Webview
 */
public class SubPageView extends RelativeLayout implements View.OnClickListener {

    NewsDetailActivity.WebviewGoBack webviewGoBack;
    AdvertSDKManager.AdvertInfo currentDownloadAd;
    private Context mContext;
    private String showUrl = null;// BrowserOpenHelper.DEFAULT_URL;
    private CustomWebView baseWebView;
    private FrameLayout webFrameLayout;
    private ProgressBar webviewProgressBar;
    private View waitingView;
    private boolean needDonwloadAna = false;
    private LinearLayout retreat, advance, home, openBrowser;// 前进，后退，home和打开其他浏览器
    private ImageView advanceImage;
    public SubPageView(Context context, String showUrl) {
        super(context);
        mContext = context;
        this.showUrl = showUrl;
        init();
    }

    public SubPageView(Context context, String showUrl, WebViewClient client) {
        super(context);
        mContext = context;
        this.showUrl = showUrl;
        init(client);
    }

    public SubPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setNeedDonwloadAna(boolean needDonwloadAna) {
        this.needDonwloadAna = needDonwloadAna;

        if (needDonwloadAna) {
            baseWebView.setDownloadListener(new android.webkit.DownloadListener() {
                @Override
                public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    if (TelephoneUtil.isWifiEnable(mContext)) {
                        startDownloadService(url);
                    } else {
                        final DialogCommon dlg;
                        dlg = new DialogCommon(mContext, mContext.getResources().getText(R.string.download_delete_title),
                                mContext.getText(R.string.download_not_wifi_alert), mContext.getText(R.string.common_dialog_confirm), mContext.getText(R.string.common_button_cancel));

                        dlg.setClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                dlg.dismiss();
                                startDownloadService(url);
                            }
                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dlg.dismiss();
                                if (!TextUtils.isEmpty(showUrl) && showUrl.equals(url)) {
                                    if (webviewGoBack != null) {
                                        webviewGoBack.goBack();
                                    }
                                }
                            }
                        });
                        dlg.setCancelable(true);
                        dlg.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (!TextUtils.isEmpty(showUrl) && showUrl.equals(url)) {
                                    if (webviewGoBack != null) {
                                        webviewGoBack.goBack();
                                    }
                                }
                                return false;
                            }
                        });
                        dlg.setCanceledOnTouchOutside(true);
                        dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (!TextUtils.isEmpty(showUrl) && showUrl.equals(url)) {
                                    if (webviewGoBack != null) {
                                        webviewGoBack.goBack();
                                    }
                                }
                            }
                        });
                        dlg.show();

                    }
                }

                private void startDownloadService(String url) {
                    String identification = "download_" + System.currentTimeMillis();
                    String title = null;
                    if (NewsListViewAdapter.advertInfo != null) {
                        currentDownloadAd = NewsListViewAdapter.advertInfo;
                        AdvertSDKController.submitStartDownloadEvent(mContext.getApplicationContext(), currentDownloadAd, identification);
                        title = TextUtils.isEmpty(currentDownloadAd.name) ? "app" : currentDownloadAd.name;
                    } else {
                        title = "app_" + System.currentTimeMillis();
                    }
                    Intent intent = new Intent(CommonLauncherControl.PKG_NAME + ".FORWARD_SERVICE");
                    intent.putExtra("identification", identification);
                    intent.putExtra("fileType", 0);
                    intent.putExtra("downloadUrl", url);
                    intent.putExtra("title", title);
                    intent.putExtra("savedDir", CommonGlobal.WIFI_DOWNLOAD_PATH);
                    intent.putExtra("savedName", identification);
                    intent.putExtra("iconPath", "");
                    intent.putExtra("totalSize", "");
                    mContext.startService(intent);
                    if (!TextUtils.isEmpty(showUrl) && showUrl.equals(url)) {
                        if (webviewGoBack != null) {
                            webviewGoBack.goBack();
                        }
                    }
                }
            });
        }


    }

    public View getWaitingView() {
        return waitingView;
    }

    public void init() {
        init(null);
    }

    public void init(WebViewClient client) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sub_web_view_layout, null);
        LayoutParams rlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(v, rlp);
        waitingView = findViewById(R.id.wait_layout);
        waitingView.setVisibility(View.GONE);
        webFrameLayout = (FrameLayout) findViewById(R.id.webview_layout);
        Activity activity = getLoaderActivity(mContext);
        if (activity != null) {
            baseWebView = new CustomWebView(activity,getContext());
        } else {
            baseWebView = new CustomWebView(mContext.getApplicationContext(),getContext());
        }
        webFrameLayout.addView(baseWebView);


        retreat = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_retreat);
        advance = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_advance);
        home = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_home);
        openBrowser = (LinearLayout) findViewById(R.id.launcher_navigation_pandahome_browser_open_browser);
        advanceImage = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_advance_image);

        retreat.setOnClickListener(this);
        advance.setOnClickListener(this);
        home.setOnClickListener(this);
        openBrowser.setOnClickListener(this);

        webviewProgressBar = (ProgressBar) findViewById(R.id.launcher_navigation_pandahome_browser_web_progressbar);
        if (client != null) {
            baseWebView.setWebViewClient(client);
        } else {
            baseWebView.setWebViewClient(new CustomWebViewClient(getContext()));

        }

        baseWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (webviewProgressBar != null) {
                        webviewProgressBar.setVisibility(View.GONE);
                    }
                    if (advanceImage != null) {
                        if (baseWebView.canGoForward()) {
                            advanceImage.setImageResource(R.drawable.webview_right_button);
                        } else {
                            advanceImage.setImageResource(R.drawable.webview_right_button_unclick);
                        }
                    }
                } else {
                    if (webviewProgressBar != null) {
                        webviewProgressBar.setVisibility(View.VISIBLE);
                        webviewProgressBar.setProgress(newProgress);
                    }
                }
            }
        });
        baseWebView.loadUrl(showUrl);
    }

    /**
     * 获取当前真实activity 的引用对象
     * 解决动态插件中webview无法弹出对话框的问题
     * 测试地址：http://auto.sina.cn/?wm=3233_9010
     *
     * @param object
     * @return
     */
    private Activity getLoaderActivity(Object object) {
        Class clazz = object.getClass().getSuperclass();
        try {
            Field field = clazz.getDeclaredField("mPluginLoaderActivity");
            field.setAccessible(true);
            return (Activity) field.get((PluginActivity) object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == retreat.getId()) {// 后退
            if (baseWebView != null && baseWebView.canGoBack()) {
                baseWebView.goBack();
            } else if (webviewGoBack != null) {
                webviewGoBack.goBack();
            }
        } else if (viewId == advance.getId()) {// 前进

            if (baseWebView != null && baseWebView.canGoForward()) {
                baseWebView.goForward();
            }
        } else if (viewId == home.getId()) {// 主页
            if (baseWebView != null) {
                baseWebView.loadUrl(showUrl);
            }

        } else if (viewId == openBrowser.getId()) {// 打开其他浏览器
            String url = showUrl;
            if (baseWebView != null && !TextUtils.isEmpty(baseWebView.getUrl())) {
                url = baseWebView.getUrl();
            }
            //TODO：改成自己的
            LauncherCaller.openUrl(mContext, null, url);
        }

    }


    /**
     * 销毁webview
     */
    public void onDestory() {
        try {
            if (baseWebView != null) {
                if (TelephoneUtil.getApiLevel() >= 11)
                    baseWebView.onPause();
                baseWebView.loadUrl("about:blank");
                baseWebView.stopLoading();
                baseWebView.clearHistory();
                baseWebView.setVisibility(GONE);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        baseWebView.setVisibility(GONE);
    }

    public void setOnFinish(NewsDetailActivity.WebviewGoBack webviewGoBack) {
        this.webviewGoBack = webviewGoBack;
    }

    public boolean goBack() {
        if (baseWebView.canGoBack()) {
            baseWebView.goBack();
            return true;
        } else {
            return false;
        }
    }


    public void setupWebviewTouch(OnTouchListener touchListener) {

        if (baseWebView != null && touchListener != null) {
            baseWebView.setOnTouchCallback(touchListener);
            baseWebView.setOnTouchListener(touchListener);
        }

    }

}
