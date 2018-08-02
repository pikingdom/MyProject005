package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;

/**
 * Created by linliangbin on 16-8-4.
 */
public class SubscribeLoadingView extends LoadingStateView {
    
    public SubscribeLoadingView(Context context) {
        super(context);
        initNoDataText();
    }
    
    public SubscribeLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initNoDataText();
    }
    
    private void initNoDataText(){
        TextView textView = (TextView) findViewById(R.id.nodate_desc);
        if(textView != null){
            textView.setText("您还未添加任何订阅");
        }
        ImageView nodate_img = (ImageView) findViewById(R.id.nodate_img);
        if (nodate_img != null) {
            nodate_img.setImageDrawable(getContext().getResources().getDrawable(R.drawable.subscribe_site_empty));
        }
    }
}
