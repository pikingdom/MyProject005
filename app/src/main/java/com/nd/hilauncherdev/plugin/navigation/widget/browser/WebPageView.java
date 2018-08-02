package com.nd.hilauncherdev.plugin.navigation.widget.browser;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.ConfigPreferences;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherHttpCommon;
import com.nd.hilauncherdev.plugin.navigation.http.LauncherLibUtil;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.ChannelUtil;
import com.nd.hilauncherdev.plugin.navigation.uconfig.UConfig;
import com.nd.hilauncherdev.plugin.navigation.uconfig.UConfigList;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.StringUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.LoadingStateView;
import com.nd.hilauncherdev.plugin.navigation.widget.WebViewToolbar;
import com.nd.hilauncherdev.plugin.navigation.widget.navigation.PageActionInterface;
import com.nd.hilauncherdev.plugin.navigation.widget.webview.CustomWebView;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 定制版web页面</br>
 * @author: cxy </br>
 * @date: 2017年10月26日 10:00.</br>
 * @update: <ul>
 * <li>针对力天定制版（7.6.5、7.6.6），增加纯web页，展示特定视图-date:2017.10.26</li>
 * </ul>
 */

public class WebPageView extends FrameLayout
        implements PageActionInterface, LoadingStateView.OnRefreshListening, WebViewToolbar.OnWebToolBarClickListener, IWebLoadUrlListener {
    private CustomWebView mWebView;
    private LoadingStateView mLoadingStateView;
    private WebViewToolbar mWebViewToolBar;
    private ProgressBar mWebProgressBar;
    private String mHomeUrl;
    private int mLastErrorCode = 0;
    private boolean hasLoaded = false;

    public WebPageView(Context context) {
        this(context, null);
    }

    public WebPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        if(!isAvailable()){
//            return;
//        }
        init();
    }

    private void init() {
        Log.d("cxydebug", "web init");
        PluginUtil.invokeSubmitEvent(getContext(), AnalyticsConstant.NAVIGATION_WEB_PAGE_INIT, null);
        mHomeUrl = genHomeUrl();
        inflate(getContext(), R.layout.layout_web_page, this);
        setPadding(getPaddingLeft(), getPaddingTop() + ScreenUtil.getStatusBarHeight(getContext()), getPaddingRight(), getPaddingBottom());
        mWebView = (CustomWebView) findViewById(R.id.web_dz_web_page);
        mLoadingStateView = (LoadingStateView) findViewById(R.id.loading_dz_web_page);
        mWebViewToolBar = (WebViewToolbar) findViewById(R.id.web_toolbar_dz_web_page);
        mWebProgressBar = (ProgressBar) findViewById(R.id.launcher_navigation_pandahome_browser_web_progressbar);
        mWebViewToolBar.setWebView(mWebView);
        mWebViewToolBar.setOnWebToolBarClickListener(this);

        mWebView.setWebLoadUrlListener(this);
        mWebView.setProgressBar(mWebProgressBar);
        mWebView.setAdvanceImage(mWebViewToolBar.getForwardImageView());

        mLoadingStateView.setOnRefreshListening(this);
        if (!TelephoneUtil.isNetworkAvailable(getContext())) {
            mLoadingStateView.setState(LoadingStateView.LoadingState.NetError);
        } else {
            mLoadingStateView.setState(LoadingStateView.LoadingState.None);
        }
    }

    public boolean isAvailable() {
        if (LauncherBranchController.isChaoqian(getContext())
                && (TelephoneUtil.getVersionName(getContext()).equalsIgnoreCase("7.6.5")
                || TelephoneUtil.getVersionName(getContext()).equalsIgnoreCase("7.6.6"))) {
            return true;
        }
        return false;
    }

    private String genHomeUrl() {
        setDefaultWebSchemesSupport();
        StringBuffer url = new StringBuffer("http://pandahomeurl.ifjing.com/service/adreportcv.aspx?");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("pid", LauncherHttpCommon.getPid());
        params.put("platform", "4");
        params.put("sourceid", "14");
        params.put("position", "-9");
        params.put("reporttype", "1");

        params.put("imei", getUrlEncodeString(LauncherLibUtil.getIMEI(getContext())));
        params.put("imsi", getUrlEncodeString(LauncherLibUtil.getIMSI(getContext())));
        params.put("subphone", getUrlEncodeString(Build.MODEL));
        params.put("subfirm", getUrlEncodeString(Build.VERSION.RELEASE));
        params.put("dividerVersion", getUrlEncodeString(LauncherLibUtil.getDivideVersion(getContext())));
        params.put("mac", getUrlEncodeString(TelephoneUtil.getMAC(getContext())));
        params.put("cuid", getUrlEncodeString(LauncherLibUtil.getCUID(getContext())));

        if (LauncherBranchController.isChaoqian(getContext())) {
            if (TelephoneUtil.getVersionName(getContext()).equalsIgnoreCase("7.6.5")) {
                params.put("adid", "1");
            } else if (TelephoneUtil.getVersionName(getContext()).equalsIgnoreCase("7.6.6")) {
                params.put("adid", "2");
            }
        } else if (ChannelUtil.isSEMChannel(getContext())) {
            params.put("adid", "3");
        }


        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey());
            url.append("=");
            url.append(entry.getValue());
            url.append("&");
        }

        if (url.charAt(url.length() - 1) == '&') {
            url.deleteCharAt(url.length() - 1);
        }
        return url.toString();
    }

    private String getUrlEncodeString(String src) {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * 设置默认支持的web第三方协议
     */
    private void setDefaultWebSchemesSupport() {
        String val = UConfig.get().getContent(UConfigList.WEB_SUPPORTED_SCHEMES);
        Log.d("cxydebug", "val : " + val);
        if (TextUtils.isEmpty(val) || "null".equalsIgnoreCase(val)) {
            ConfigPreferences.getInstance(getContext()).setSchemeOpt(1);
            UConfig.get().commit(UConfigList.WEB_SUPPORTED_SCHEMES, "{\"opt\":1,\"version\":0}");
        }
    }

    @Override
    public void onEnterPage() {
        if (StringUtil.isNetEmpty(mWebView.getOriginalUrl()) || StringUtil.isNetEmpty(mWebView.getUrl())) {
            mWebView.loadUrl(mHomeUrl);
        }
        PluginUtil.invokeSubmitEvent(getContext(), AnalyticsConstant.NAVIGATION_WEB_PAGE_VISIBILITY, "jr");
        CvAnalysis.submitPageStartEvent(getContext(), CvAnalysisConstant.WEB_PAGE_ID);
        Log.d("cxydebug", "web e");
        if (mWebView != null) {
            mWebView.onWebResume();
        }
    }

    @Override
    public void onLeavePage() {
        PluginUtil.invokeSubmitEvent(getContext(), AnalyticsConstant.NAVIGATION_WEB_PAGE_VISIBILITY, "lk");
        CvAnalysis.submitPageEndEvent(getContext(), CvAnalysisConstant.WEB_PAGE_ID);
        Log.d("cxydebug", "web l");
        if (mWebView != null) {
            mWebView.onWebPause();
        }
    }

    @Override
    public void onPageResume() {
    }

    @Override
    public void onBackToPage() {
    }

    @Override
    public void onActionRefresh() {
    }

    @Override
    public void onActionRefreshSync() {
    }

    @Override
    public void doDailyUpdate() {
    }

    @Override
    public void onRefresh() {
        if (TelephoneUtil.isNetworkAvailable(getContext())) {
            if (mWebView != null) {
                if (StringUtil.isNetEmpty(mWebView.getOriginalUrl())) {
                    mWebView.loadUrl(mHomeUrl);
                } else {
                    mWebView.reload();
                }
                mLoadingStateView.setState(LoadingStateView.LoadingState.None);
            }
        }
    }

    @Override
    public boolean onWebBackwardClick() {
        return false;
    }

    @Override
    public boolean onWebForwardClick() {
        return false;
    }

    @Override
    public boolean onWebHomeClick() {
        if (mWebView != null) {
            mWebView.loadUrl(mHomeUrl);
            return true;
        }
        return false;
    }

    @Override
    public boolean onWebOpenBrowserClick() {
        return false;
    }

    @Override
    public void onPageStarted() {
        mLastErrorCode = 0;
        if (!hasLoaded && mLoadingStateView != null) {
            mLoadingStateView.setState(LoadingStateView.LoadingState.Loading);
        }
    }

    @Override
    public void onPageFinished() {
        if (mLoadingStateView != null && mLastErrorCode == 0) {
            hasLoaded = true;
            mLoadingStateView.setState(LoadingStateView.LoadingState.None);
        }
    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
        mLastErrorCode = errorCode;
        if (mLoadingStateView != null) {
            if (errorCode == WebViewClient.ERROR_CONNECT
                    || errorCode == WebViewClient.ERROR_TIMEOUT
                    || errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION
                    || errorCode == WebViewClient.ERROR_AUTHENTICATION) {
                mLoadingStateView.setState(LoadingStateView.LoadingState.NetError);
            } else {
                mLoadingStateView.setState(LoadingStateView.LoadingState.None);
            }
        }
    }
}
