package com.nd.hilauncherdev.plugin.navigation.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;

/**
 * Created by linliangbin on 16-7-12.
 */
public class SubscribSiteCateAct extends BaseActivity {
    
    
    HeaderView headerView;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AsyncImageLoader.initImageLoaderConfig(this);
        setContentView(R.layout.subscribe_site_cate);
        headerView = (HeaderView) this.findViewById(R.id.head_view);
        headerView.setTitle("添加订阅");
        headerView.setGoBackListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View arg0) {
                                             finish();
                                         }
                                     }
        );
        
    }
    
}
