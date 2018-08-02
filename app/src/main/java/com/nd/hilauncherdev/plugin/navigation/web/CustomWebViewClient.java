package com.nd.hilauncherdev.plugin.navigation.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nd.hilauncherdev.kitset.util.reflect.NavigationKeepForReflect;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.uconfig.ContentConverter;
import com.nd.hilauncherdev.plugin.navigation.uconfig.UConfig;
import com.nd.hilauncherdev.plugin.navigation.uconfig.UConfigList;
import com.nd.hilauncherdev.plugin.navigation.util.DialogCommon;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 通用逻辑处理的webviewclient</br>
 * Author: cxy
 * Date: 2017/2/17.
 */

public class CustomWebViewClient extends BaseWebviewClient {


    private static final String TAG = "CustomWebViewClient";
    private static List<WebScheme> mSchemes = new ArrayList<WebScheme>();
    private static int mSchemeOpt = 0;
    // 当opt = 1时，该值生效，表示是否做弹窗提示，默认不弹窗
    private static boolean mPrompt = false;
    /**
     * 是否拦截下载
     */
    private boolean needInterruptDownload = false;
    private OnActionDispatch mActionDispatch;
    private DialogCommon mDialogForAskOpenThirdApp;
    private DialogCommon mDialogForAskDownloadApp;
    private String mDownloadUrl;
    private Intent mOpenIntent;
    private Context context;
    private String handleOnPageStartedUrl = null;

    public CustomWebViewClient(final Context context) {
        super(context);
        try {
            NavigationKeepForReflect.loadWebviewSchemeData();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        this.context = context;

        try {
            mDialogForAskOpenThirdApp = new DialogCommon(context, "即将打开外部应用",
                    "外部应用不受91桌面安全保护，可能产生恶意行为，是否继续打开？", "取消", "确定");
            mDialogForAskOpenThirdApp.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogForAskOpenThirdApp.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SystemUtil.startActivitySafely(context, mOpenIntent);
                    mDialogForAskOpenThirdApp.dismiss();
                }
            });


            mDialogForAskDownloadApp = new DialogCommon(context, context.getString(R.string.download_delete_title),
                    "是否立即下载该应用？", "取消", "确定");

            mDialogForAskDownloadApp.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogForAskDownloadApp.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialogForAskDownloadApp.dismiss();
                    if (mActionDispatch != null) {
                        mActionDispatch.startDownload(mDownloadUrl);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        initSchemeConfig(context);
    }

    public static void initSchemeConfig(Context context) {
        // 获取OPT
        try {
            mSchemeOpt = ConfigPreferences.getInstance(context).getSchemeOpt();
            mPrompt = ConfigPreferences.getInstance(context).getPrompt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "getSchemes: " + mSchemeOpt);
        Log.d(TAG, "getSchemes: " + mPrompt);

        // 获取待匹配的scheme列表
        if (mSchemes != null && !mSchemes.isEmpty()) {
            return;
        }

        final List<WebScheme> schemes = UConfig.get().getContent(UConfigList.WEB_SUPPORTED_SCHEMES, new ContentConverter<List<WebScheme>>() {
            @Override
            public List<WebScheme> convert(String src) {
                List<WebScheme> list = new ArrayList<WebScheme>();
                try {
                    if (!TextUtils.isEmpty(src)) {
                        JSONArray jsonArray = new JSONArray(src);
                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0, len = jsonArray.length(); i < len; i++) {
                                JSONObject ele = jsonArray.optJSONObject(i);
                                WebScheme webScheme = new WebScheme();
                                webScheme.packageName = ele.optString("packageName");
                                webScheme.scheme = ele.optString("scheme");
                                webScheme.askable = ele.optInt("prompt", 0) == 1;
                                if (!TextUtils.isEmpty(webScheme.packageName) && !TextUtils.isEmpty(webScheme.scheme)) {
                                    list.add(webScheme);
                                }
                            }
                        }
                        return list;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        mSchemes.clear();
        if (schemes != null) {
            mSchemes.addAll(schemes);
        }
    }

    public void setNeedInterruptDownload(boolean needInterruptDownload) {
        this.needInterruptDownload = needInterruptDownload;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean result = super.shouldOverrideUrlLoading(view, url);
        Log.i("llbeing", "shouldOverrideUrlLoading:" + "," + result + "," + url);
        if (result) {
            return true;
        }
        // 在onPageStarted已经加载过的，不再加载
        if (!TextUtils.isEmpty(handleOnPageStartedUrl) && handleOnPageStartedUrl.equals(url)) {
            handleOnPageStartedUrl = null;
            return true;
        }
        if (filterScheme(view.getContext(), url)) {
            return true;
        }
        return false;
    }


    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
        /**解决系统webview HTTPS 证书问题时显示界面空白问题 */
        try {
            handler.proceed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        handleOnPageStartedUrl = null;
        if (url != null) {
            if (filterScheme(view.getContext(), url)) {
                handleOnPageStartedUrl = url;
            }
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME || errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
            if (mSchemeOpt == 1 || mSchemeOpt == 0) {
                String data = "<div>" +
                        "<b>正在跳转到：</b></br>" +
                        "<a href='" + failingUrl + "'>" + failingUrl + "</a></br>" +
                        "</div>";
                view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
            }
            return;
        }
    }

    private boolean filterScheme(Context ctx, String url) {
        //以.apk结尾的URL都直接跳转到下载管理中下载
        if (url.endsWith(".apk") && needInterruptDownload) {
            mDownloadUrl = url;
            if (mDialogForAskDownloadApp != null && !mDialogForAskDownloadApp.isShowing()) {
                mDialogForAskDownloadApp.show();
            }
            return true;
        }

        //所有以http:或https:开头的URL都直接返回false，让系统Webview去处理
        if (url.startsWith("http:") || url.startsWith("https:")) {
            return false;
        } else {//非http:或https:开头的URL且为合法的Intent URI，那么都默认不处理
            try {
                Uri uri = Uri.parse(url);
                if ("alipays".equals(uri.getScheme()) || "weixin".equals(uri.getScheme())) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    SystemUtil.startActivitySafely(ctx, intent);
                    return true;
                } else {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    // avoid based-intent scheme uri attack
                    intent.setComponent(null);
//                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    mOpenIntent = intent;
                    if (intent != null) {
                        //桌面本身的协议，不需要询问（白名单）
                        if ("zm91txwd20141231".equals(uri.getScheme()) || "zm91txwd20141231service".equals(uri.getScheme())) {
                            SystemUtil.startActivitySafely(ctx, intent);
                        } else {
                            boolean valided = false;//协议验证是否通过
                            boolean shouldPrompt = false;//是否弹窗提示
                            if (mSchemeOpt == 0) {//对协议有限制，白名单为Data数组,协议头在白名单中且对应的应用已经安装时提示，否则不提示
                                WebScheme ws = checkScheme(uri);
                                if (ws != null && SystemUtil.isApkInstalled(context, ws.packageName)) {
                                    valided = true;
                                    shouldPrompt = ws.askable;
                                } else {
                                    valided = false;
                                }
                            } else if (mSchemeOpt == 1) {//无协议限制，可以过滤所有协议
                                valided = true;
                                shouldPrompt = mPrompt;
                            } else if (mSchemeOpt == 2) {//对所有协议都做限制，也就是屏蔽所有协议
                                valided = false;
                            }
                            Log.e(TAG, "filterScheme: " + valided + " : " + shouldPrompt);
                            if (valided) {
                                if (shouldPrompt) {
                                    if (mDialogForAskOpenThirdApp != null && !mDialogForAskOpenThirdApp.isShowing()) {
                                        mDialogForAskOpenThirdApp.show();
                                    }
                                } else {
                                    SystemUtil.startActivitySafely(context, mOpenIntent);
                                }
                            }
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void setOnActionDispatch(OnActionDispatch actionDispatch) {
        this.mActionDispatch = actionDispatch;
    }

    public void onPause() {
        if (mDialogForAskOpenThirdApp != null) {
            mDialogForAskOpenThirdApp.dismiss();
        }
        if (mDialogForAskDownloadApp != null) {
            mDialogForAskDownloadApp.dismiss();
        }
    }

    /**
     * @desc 返回对应scheme 的包名
     * 返回空表示无任何匹配的scheme
     * @author linliangbin
     * @time 2017/2/24 14:023
     */
    private WebScheme checkScheme(Uri uri) {
        if (uri == null) {
            return null;
        }

        try {
            final String scheme = uri.getScheme();
            Log.d(TAG, scheme);
            if (!TextUtils.isEmpty(scheme) && mSchemes != null && !mSchemes.isEmpty()) {
                for (int i = 0, len = mSchemes.size(); i < len; i++) {
                    WebScheme tempScheme = mSchemes.get(i);
                    String match = tempScheme.scheme;
                    if (scheme.equals(match)) {
                        return tempScheme;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 动作分发
     */
    public interface OnActionDispatch {
        void startDownload(String downloadUrl);
    }

    static class WebScheme {
        String packageName;
        String scheme;
        boolean askable = false;//是否需要询问弹窗（跳转第三方协议）
    }
}
