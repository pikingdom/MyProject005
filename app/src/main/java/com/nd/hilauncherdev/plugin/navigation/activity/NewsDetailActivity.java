package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.dynamic.plugin.PluginActivity;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.UCClientEvent;
import com.nd.hilauncherdev.plugin.navigation.loader.ZeroNewsReporter;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.SubPageView;

import org.json.JSONObject;


/**
 * Created by linliangbin_dian91 on 2016/3/14.
 */
public class NewsDetailActivity extends PluginActivity {
    String url = "";
    int currentSdk = 14;
    WebviewGoBack webviewGoBack;

    SubPageView subPageView = null;

    /**
     * 是否需要统计下载和广告SDK数据
     */
    boolean needSubmitAnalytics = false;
    /**
     * 是否需要展示loading界面
     * 新闻页面由于加载时间较长，不展示loading 界面
     * 广告页面展示loading 数据
     */
    boolean needLoading = false;

    /**
     * 是否需要统计展示时常数据
     * 目前只有网易新闻需要
     */
    boolean needReport = false;

    UCClientEvent ucClientEvent;

    private long startTime = 0L;

    public static void startDetailActivity(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) return;

        Intent intent = new Intent();
        intent.setClass(context, NewsDetailActivity.class);
        intent.putExtra("url", url);
        SystemUtil.startActivitySafely(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try {
//            currentSdk = getApplicationInfo().targetSdkVersion;
//            Log.i("llbeing","getCurrentSdkVersion:"+currentSdk);
//            if(TelephoneUtil.getApiLevel() >= 19)
//                getApplicationInfo().targetSdkVersion = TelephoneUtil.getApiLevel();
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        url = getIntent().getStringExtra("url");
        needSubmitAnalytics = getIntent().getBooleanExtra("need_download_analytic", false);
        needLoading = getIntent().getBooleanExtra("need_loading",false);
        needReport = getIntent().getBooleanExtra("need_report",false);
        String ucEvent = getIntent().getStringExtra("uc_event");
        parseUCClientEvent(ucEvent);
        if(needReport){
            startTime = System.currentTimeMillis();
            if(ucClientEvent != null){
                ucClientEvent.startRead();
            }
        }
        if(TextUtils.isEmpty(url)){
            finish();
        }
        subPageView = new SubPageView(this,url);
        subPageView.setNeedDonwloadAna(needSubmitAnalytics);
        View waitingView = subPageView.findViewById(R.id.wait_layout);
        if(waitingView != null && !needLoading)
            waitingView.setVisibility(View.GONE);
        setContentView(subPageView);

        webviewGoBack = new WebviewGoBack() {
            @Override
            public void goBack() {
                NewsDetailActivity.this.finish();
            }
        };
        subPageView.setOnFinish(webviewGoBack);
    }

    private void parseUCClientEvent(String ucEvent){
        if(!TextUtils.isEmpty(ucEvent)){
            try {
                JSONObject jsonEvent = new JSONObject(ucEvent);
                ucClientEvent = UCClientEvent.format(jsonEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            getApplicationInfo().targetSdkVersion = currentSdk;
//            Log.i("llbeing","getTargetSdkVersion:"+getApplicationInfo().targetSdkVersion);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        if (needReport) {
            long readDuration = System.currentTimeMillis() - startTime;
            if (ucClientEvent != null) {
                //UC阅读客户端事件上报
                ucClientEvent.endRead();
                ZeroNewsReporter.reportUCClientEvent(this.getApplicationContext(), ucClientEvent);
            } else {
                ZeroNewsReporter.reportNewsRead(this, url, 1, readDuration + "");
            }
        }
        if(subPageView != null){
            subPageView.onDestory();
        }
        super.onDestroy();
    }

    public interface WebviewGoBack{

        public void goBack();
    }

    @Override
    public void onBackPressed() {
        if(!subPageView.goBack()){
            super.onBackPressed();
        }
    }
}
