package com.nd.hilauncherdev.plugin.navigation.widget.listview.footer;

import android.text.Html;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.widget.ListViewFooter;

/**
 * Created by linliangbin on 2017/6/6 16:46.
 */

public class VideoListViewFooter extends ListViewFooter {

    public VideoListViewFooter(LayoutInflater imflater) {
        super(imflater);
        init();
    }

    public VideoListViewFooter(LayoutInflater imflater, int id) {
        super(imflater, id);
        init();
    }

    private void init(){
        TextView textView = (TextView) getFooterView().findViewById(R.id.tv_market_refresh);
        try {
            textView.setText(Html.fromHtml("数据获取失败，点击<font color='#09b7b7'>重试</font>"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.layout_video_footer;
    }
}
