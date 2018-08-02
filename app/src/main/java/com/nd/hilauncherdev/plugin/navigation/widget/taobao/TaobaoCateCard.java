package com.nd.hilauncherdev.plugin.navigation.widget.taobao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.NewsDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.adapter.IAdapter;
import com.nd.hilauncherdev.plugin.navigation.adapter.TaobaoCateAdapter;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoCate;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.NotScrollGridView;

/**
 * description: 分类卡片<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/4/15<br/>
 */
public class TaobaoCateCard extends TaobaoCard {

    private TaobaoCateAdapter mAdapter;
    private NotScrollGridView mGridView;

    public TaobaoCateCard(final Context context, ViewGroup parent) {
        super(parent);
        setType(CardType.CATE);

        View contentView = LayoutInflater.from(context).inflate(R.layout.taobao_viewtype_cate, parent, false);
        setContentView(contentView);
        mGridView = (NotScrollGridView) contentView.findViewById(R.id.gridview_cate);
        mAdapter = new TaobaoCateAdapter(context, mGridView);
        if (mAdapter.getCount()<=0){
            setCardVisible(false);
        }else {
            setCardVisible(true);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent == null || position < 0 || position >= parent.getCount()) return;
                Object obj = parent.getItemAtPosition(position);
                if (obj instanceof TaobaoCate) {
                    TaobaoCate cate = (TaobaoCate) obj;
                    TaobaoData.getInstance().cancelBannerTimers();//离开页面停止定时器
                    CvAnalysis.submitClickEvent(context.getApplicationContext(), CvAnalysisConstant.TAOBAO_SCREEN_PAGEID, CvAnalysisConstant.TAOBAO_SCREEN_POS_CATE, cate.id, CvAnalysisConstant.RESTYPE_LINKS);
                    Intent intent = new Intent();
                    intent.setClass(context, NewsDetailActivity.class);
                    intent.putExtra("url", cate.url);
                    SystemUtil.startActivitySafely(context, intent);
                }
            }
        });
    }

    @Override
    public void setPageVisible(boolean visible) {
        super.setPageVisible(visible);
        mAdapter.setPageVisible(visible);
        if(visible){
            notifyVisibleRectChanged(visibleRect);
        }
    }

    @Override
    public void notifyVisibleRectChanged(Rect visibleRect) {
        super.notifyVisibleRectChanged(visibleRect);
        if(visibleRect != null){
            if(visibleRect.top >= 0){
                mAdapter.handleSubmit(isPageVisible());
            }
        }
    }

    @Override
    public void update() {
        if (mAdapter != null) {
            mAdapter.refresh(new IAdapter.CallBack() {
                @Override
                public void onFinish() {

                }
                @Override
                public void onError(boolean isFirstLoad) {
                    setCardVisible(false);
                }
                @Override
                public void onSuccess(boolean isNoMore, int refreshDataSize) {
                    setCardVisible(true);
                }
            });
        }
    }
}
