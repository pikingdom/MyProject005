package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;

/**
 * description: 淘宝-淘一下卡片<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/18<br/>
 */
public class TaobaoSearchCard extends TaobaoCard {

    public TaobaoSearchCard(final Context context, ViewGroup parent) {
        super(parent);
        setType(CardType.SEARCH);

        View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_search, parent, false);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
                CvAnalysis.submitClickEvent(context, CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_SEARCH, CvAnalysisConstant.TAOBAO_SCREEN_RESIDEX, CvAnalysisConstant.RESTYPE_LINKS);
                Intent intent = new Intent();
                intent.setClassName(Global.getPackageName(), "com.nd.hilauncherdev.widget.taobao.TaobaoWidgetActivity");
                SystemUtil.startActivitySafely(context, intent);
            }
        });

        setContentView(contentView);
    }

    @Override
    public void update() {

    }
}
