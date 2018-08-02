package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.dynamic.plugin.PluginActivity;
import com.android.dynamic.plugin.PluginUtil;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.share.SharedPopWindow;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.UriParser;
import com.nd.hilauncherdev.plugin.navigation.util.WebViewSecureUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;

/**
 * Created by linliangbin_dian91 on 2016/4/26.
 */
public class FunnyDetailAct extends PluginActivity {


    public WebView mWebView;
    private HeaderView mHeaderView;
    private ProgressBar webProgressBar;
    private View webProgressBarFl;
    private String postUrl = "";
    private String postTitle = "";
    private String postSummary = "";
    private String postDate = "";
    private String postImg = "";
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = LayoutInflater.from(this).inflate(R.layout.theme_shop_v6_netplan_web_activity,null);
        setContentView(view);

        Intent intent = getIntent();
        postUrl = intent.getStringExtra("postUrl")==null?"":intent.getStringExtra("postUrl");
        postSummary = intent.getStringExtra("postSummary")==null?"":intent.getStringExtra("postSummary");
        postTitle = intent.getStringExtra("postTitle")==null?"":intent.getStringExtra("postTitle");
        postImg = intent.getStringExtra("postImg")==null?"":intent.getStringExtra("postImg");

        mHeaderView = (HeaderView) this.findViewById(R.id.headerView);
        mHeaderView.setTitle("详情");
        mHeaderView.setMenuImageResource(R.drawable.common_header_share);
        mHeaderView.setMenuVisibility(View.VISIBLE);
        mHeaderView.setMenuListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFunny();
                PluginUtil.invokeSubmitEvent(FunnyDetailAct.this, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "zxfx");
            }
        });
        mHeaderView.setGoBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunnyDetailAct.this.finish();
            }
        });

        //WebView设置
        webProgressBarFl = findViewById(R.id.web_progress_bar_fl);
        webProgressBar = (ProgressBar) findViewById(R.id.web_progress_bar);
        mWebView = (WebView) findViewById(R.id.theme_list_content);
        // 初始化工具栏
        mHeaderView = (HeaderView) findViewById(R.id.headerView);
        initWebView();
    }

//  TODO 图标文件地址
    protected void shareFunny() {
        SharedPopWindow sharePop = new SharedPopWindow(this,this);
        String path =  Global.CACHES_HOME_IMAGE_LOADER + String.valueOf(postImg.hashCode()+"0");
        boolean isDrawableExist = false;
        if(!TextUtils.isEmpty(path)){
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if(bitmap != null)
                    isDrawableExist = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Uri uri ;
        if(isDrawableExist){
            uri = Uri.parse("file://" + path);
        }else{
            uri = Uri.parse("file://" + SharedPopWindow.SHARE_IMG_PATH);
        }
        String subject = "";
        String content = postTitle + "\n" + postSummary;
        sharePop.setSharedContentSpecialWX(postTitle, postSummary, postUrl,uri,120);
        sharePop.showcf(this, view);
    }


    /**
     * 重新加载网页
     */
    private void loadWebView(){
        mWebView.setVisibility(View.VISIBLE);
        if ( WebViewSecureUtil.checkWebUrlSecure(postUrl) ){
            //添加桌面参数
            StringBuffer sb = new StringBuffer(postUrl);
            mWebView.loadUrl(sb.toString());
        }
    }

    private void initWebView(){

        WebViewSecureUtil.checkWebViewSecure(mWebView);
        WebSettings settings = mWebView.getSettings();

        //关闭下述开关，避免webview 显示变形问题
        settings.setSupportZoom(false);//支持拉伸
        settings.setBuiltInZoomControls(false);//加入拉伸工具条
        settings.setUseWideViewPort(false);//
        //settings.setPluginsEnabled(true);

        //Web加载进度更新
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                webProgressBar.setProgress(progress);
                if (progress >= 100) {
                    webProgressBar.setVisibility(View.GONE);
                    webProgressBarFl.setVisibility(View.GONE);
                } else {
                    webProgressBarFl.setVisibility(View.VISIBLE);
                    webProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String postUrl) {
                try {

                    PluginUtil.invokeSubmitEvent(FunnyDetailAct.this, AnalyticsConstant.NAVIGATION_SCREEN_INTERESTING_DISCOVERY_CARDS, "zxxq");
                    if (postUrl.contains(UriParser.ACTION_THEME_DOWNLOAD) ||
                            postUrl.contains(UriParser.ACTION_THEME_SHOP_MODULE_DOWNLOAD)) {//下载调用，使用service处理协议，使页面不跳转
                        postUrl = UriParser.changeActivityUriToService(postUrl);
                    } else if (postUrl.contains("weixin://wap/pay")) {//支持微信支付调用
                        Uri uri = Uri.parse(postUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        return true;
                    }

                    if (!TextUtils.isEmpty(postUrl) &&
                            (postUrl.startsWith("http://") || postUrl.startsWith("https://")) &&
                            (postUrl.endsWith("&isUserWebOpen=true") || postUrl.endsWith("?isUserWebOpen=true"))) {
                        //使用调用第三方浏览器打开
                        if (postUrl.endsWith("&isUserWebOpen=true")) {
                            postUrl = postUrl.replace("&isUserWebOpen=true", "");
                        } else {
                            postUrl = postUrl.replace("?isUserWebOpen=true", "");
                        }
                        LauncherCaller.openUrl(FunnyDetailAct.this, "", postUrl);
                    } else if (!TextUtils.isEmpty(postUrl) &&
                            (postUrl.startsWith("http://") || postUrl.startsWith("https://"))) {
                        //使用活动浏览器打开
                        startCampaignActivty(postUrl);
                    } else {
                        UriParser.handleUriEx(FunnyDetailAct.this, postUrl);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        loadWebView();
    }


    /**
     * 使用活动浏览器打开
     * 若活动浏览器打开失败，则使用零屏默认使用的浏览器规则打开
     * @param postUrl
     */
    private void startCampaignActivty(String postUrl){

        try {
            Intent openWebView = new Intent();
            openWebView.setClassName(FunnyDetailAct.this, "com.nd.hilauncherdev.menu.topmenu.campaign.CompainWebViewActivity");
            openWebView.putExtra("postUrl", postUrl);
            openWebView.putExtra("from", "from_navigation");
            openWebView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openWebView);
        }catch (Exception e){
            e.printStackTrace();
            LauncherCaller.openUrl(FunnyDetailAct.this, "", postUrl);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
