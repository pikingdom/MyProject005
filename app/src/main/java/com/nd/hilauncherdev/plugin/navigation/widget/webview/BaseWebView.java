package com.nd.hilauncherdev.plugin.navigation.widget.webview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;


/**
 * Created by chenxuyu_dian91 on 2015/10/27.
 */
public class BaseWebView extends WebView {

    private ProgressBar progressBar;
    private ImageView advanceImage;
    private Context mContext;


    private long showTimeLimit = 3000;
    private long startTime = 0L;
    private long endTime = 0L;


    public void setOnTouchCallback(OnTouchListener onTouchCallback) {
        this.onTouchCallback = onTouchCallback;
    }

    private OnTouchListener onTouchCallback;


    public void setLoadingHandler(Handler loadingHandler) {
        this.loadingHandler = loadingHandler;
    }

    private Handler loadingHandler;
    public BaseWebView(Context ctx){
        super(ctx);
        mContext = ctx;
        initSetting();
    }

    public BaseWebView(Context context, AttributeSet attrs){
        super(context,attrs);
        mContext = context;
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
        webSetting.setAppCacheEnabled(false);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);


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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(onTouchCallback != null){
            onTouchCallback.onTouch(this,event);
        }
        return super.onTouchEvent(event);

    }
}

