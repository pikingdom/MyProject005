package com.nd.hilauncherdev.plugin.navigation.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.widget.browser.IWebLoadUrlListener;

/**
 * Description: 通用逻辑处理的webviewclient</br>
 * Author: cxy
 * Date: 2017/2/17.
 */

public class BaseWebviewClient extends WebViewClient {


    /**
     * 最小地址重定向时间: 10s
     */
    public static final long MIN_OVERLOAD_TIME = 5 * 1000;
    private Context context;
    private View waitingView = null;
    private long firstLoadUrlTime = 0;
    private boolean hasTouchBefore = false;
    private IWebLoadUrlListener mWebLoadUrlListener;

    public BaseWebviewClient(final Context context) {
        this.context = context;

    }

    public void setWaitingView(View waitingView) {
        this.waitingView = waitingView;
    }

    public void setWebLoadUrlListener(IWebLoadUrlListener listener) {
        this.mWebLoadUrlListener = listener;
    }

    public void setFirstLoadUrlTime(long firstLoadUrlTime) {
        this.firstLoadUrlTime = firstLoadUrlTime;
    }

    public void setHasTouchBefore(boolean hasTouchBefore) {
        this.hasTouchBefore = hasTouchBefore;
    }

    private void startPhone(String url) {
        Uri uri = Uri.parse(url);
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startSms(String url) {

        //获取短信号码，包含"sms:"
        String address = url.contains("?") ? url.substring(0, url.indexOf("?")) : url;
        //获取短信内容，如果没有则为""
        String body = url.replaceAll(address, "");
        body = body.contains("=") ? body.substring(body.indexOf("=") + 1) : body;
        //对于中文内容进行Uri解码
        body = Uri.decode(body);
        Uri smsToUri = Uri.parse(address);// 联系人地址
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
            intent.putExtra("sms_body", body);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        Log.i("llbeing","hasTouchBefore="+hasTouchBefore + "," + (System.currentTimeMillis() - firstLoadUrlTime));
        if (url.contains("tel:")) {
            startPhone(url);
            return true;
        } else if (url.contains("sms:")) {
            startSms(url);
            return true;
        } else if ((url.startsWith("http") || url.startsWith("https"))
                && System.currentTimeMillis() - firstLoadUrlTime > MIN_OVERLOAD_TIME
                && hasTouchBefore) {
            LauncherCaller.openUrl(context, "", url);
            return true;
        }
        return false;
    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (waitingView != null && waitingView.getVisibility() == View.VISIBLE) {
            waitingView.setVisibility(View.GONE);
        }
        if (mWebLoadUrlListener != null) {
            mWebLoadUrlListener.onPageFinished();
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mWebLoadUrlListener != null) {
            mWebLoadUrlListener.onPageStarted();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (mWebLoadUrlListener != null) {
            mWebLoadUrlListener.onReceivedError(errorCode, description, failingUrl);
        }
    }
}
