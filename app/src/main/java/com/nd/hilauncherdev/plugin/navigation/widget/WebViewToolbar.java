package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

/**
 * @Description: </br>
 * @author: cxy </br>
 * @date: 2017年10月26日 16:56.</br>
 * @update: </br>
 */

public class WebViewToolbar extends FrameLayout implements View.OnClickListener {

    private View toolForward;
    private View toolBackward;
    private View toolHome;
    private View toolOpenBrowser;
    private ImageView ivForward;

    private OnWebToolBarClickListener mOnWebToolBarClickListener;
    private WebView mWebView;

    public WebViewToolbar(Context context) {
        this(context, null);
    }

    public WebViewToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.sub_web_toolbar_layout, this);
        toolForward = findViewById(R.id.launcher_navigation_pandahome_browser_advance);
        toolBackward = findViewById(R.id.launcher_navigation_pandahome_browser_retreat);
        toolHome = findViewById(R.id.launcher_navigation_pandahome_browser_home);
        toolOpenBrowser = findViewById(R.id.launcher_navigation_pandahome_browser_open_browser);
        ivForward = (ImageView) findViewById(R.id.launcher_navigation_pandahome_browser_advance_image);

        toolBackward.setOnClickListener(this);
        toolForward.setOnClickListener(this);
        toolHome.setOnClickListener(this);
        toolOpenBrowser.setOnClickListener(this);
    }

    public void setWebView(WebView web) {
        this.mWebView = web;
    }

    public ImageView getForwardImageView() {
        return this.ivForward;
    }


    @Override
    public void onClick(View v) {
        if (v == toolBackward) {
            if (mOnWebToolBarClickListener == null || !mOnWebToolBarClickListener.onWebBackwardClick()) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                }
            }
        } else if (v == toolForward) {
            if (mOnWebToolBarClickListener == null || !mOnWebToolBarClickListener.onWebForwardClick()) {
                if (mWebView != null && mWebView.canGoForward()) {
                    mWebView.goForward();
                }
            }
        } else if (v == toolHome) {
            if (mOnWebToolBarClickListener == null || !mOnWebToolBarClickListener.onWebHomeClick()) {
                //此处缺省，由实现类处理
            }
        } else if (v == toolOpenBrowser) {
            if (mOnWebToolBarClickListener == null || !mOnWebToolBarClickListener.onWebOpenBrowserClick()) {
                // 打开浏览器
                if (mWebView != null) {
                    try {
                        String url = mWebView.getUrl();
                        if (!TextUtils.isEmpty(url)) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(url));
                            SystemUtil.startActivitySafely(getContext(), browserIntent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setOnWebToolBarClickListener(OnWebToolBarClickListener listener) {
        mOnWebToolBarClickListener = listener;
    }

    public interface OnWebToolBarClickListener {

        boolean onWebBackwardClick();

        boolean onWebForwardClick();

        boolean onWebHomeClick();

        boolean onWebOpenBrowserClick();
    }
}
