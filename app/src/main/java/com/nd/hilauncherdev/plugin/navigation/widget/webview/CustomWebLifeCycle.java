package com.nd.hilauncherdev.plugin.navigation.widget.webview;

import android.os.Build;
import android.os.Looper;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * @Description: </br>
 * @author: cxy </br>
 * @date: 2017年09月21日 18:44.</br>
 * @update: </br>
 */

public class CustomWebLifeCycle implements WebLifeCycle {

    private WebView mWebView;

    CustomWebLifeCycle(WebView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onResume() {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                mWebView.onResume();
            }
            mWebView.resumeTimers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        try {
            if (Build.VERSION.SDK_INT >= 11) {
                mWebView.onPause();
            }
            mWebView.pauseTimers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        try {
            clearWebView(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void clearWebView(WebView m) {
        if (m == null)
            return;
        if (Looper.myLooper() != Looper.getMainLooper())
            return;
        m.loadUrl("about:blank");
        m.stopLoading();
        if (m.getHandler() != null)
            m.getHandler().removeCallbacksAndMessages(null);
        m.removeAllViews();
        ViewGroup mViewGroup = null;
        if ((mViewGroup = ((ViewGroup) m.getParent())) != null)
            mViewGroup.removeView(m);
        m.setWebChromeClient(null);
        m.setWebViewClient(null);
        m.setTag(null);
        m.clearHistory();
        m.destroy();
        m = null;
    }
}
