package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linliangbin on 16-8-12.
 */
public class NewsListLoadingView extends LoadingStateView {
    public NewsListLoadingView(Context context) {
        super(context);
        initNetworkText();
    }
    
    public NewsListLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNetworkText();
    }
    
    @Override
    public void inflateLayout(){
        inflate(getContext(), R.layout.theme_shop_v6_loadingview_for_news, this);
    }
    
    
    private void initNetworkText(){
        
        TextView textView = (TextView) findViewById(R.id.framework_viewfactory_err_textview);
        if(textView != null){
            textView.setText("网络不给力，与资讯屏暂时失去联络啦！\n" +
                    "请检查网络是否已断开");
        }
    
    }
}
