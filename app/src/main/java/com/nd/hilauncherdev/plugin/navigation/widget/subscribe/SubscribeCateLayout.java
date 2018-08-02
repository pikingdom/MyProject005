package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeCateBean;
import com.nd.hilauncherdev.plugin.navigation.loader.SubscribeLoader;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import java.util.ArrayList;

/**
 * Created by linliangbin on 16-7-12.
 */
public class SubscribeCateLayout extends RelativeLayout implements mainSubInterface {
    
    Context context;
    private ListView mainListView,subListView;
    private SubscribeCateAdapter mainAdapter;
    private SubscribeCategroySiteAdapter subAdapter;
    
    private Handler handler=new Handler();
    
    public SubscribeCateLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }
    
    public SubscribeCateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }
    
    public void initView(){
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_cate_layout,null);
        addView(view,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    
        mainListView = (ListView) this.findViewById(R.id.main_list);
        subListView = (ListView) this.findViewById(R.id.sub_list);
    
        mainAdapter = new SubscribeCateAdapter(mainListView,context,this);
        mainListView.setAdapter(mainAdapter);
        
        subAdapter = new SubscribeCategroySiteAdapter(context,subListView,"","0");
        subAdapter.setCallback(this);

        loadMainAdapterData();
    }
    
    private void loadMainAdapterData(){
        
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final ArrayList<SubscribeCateBean> cateBeans = SubscribeLoader.loadSubscribCateData_6012(context);
                SubscribeCateBean subscribeCateBean = new SubscribeCateBean();
                subscribeCateBean.cateName = "我的订阅";
                subscribeCateBean.isLocal = true;
                subscribeCateBean.siteIdString = SubscribeHelper.getAddedSubscribeSite(context);
                cateBeans.add(0, subscribeCateBean);
                
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mainAdapter != null){
                            mainAdapter.setData(cateBeans);
                            //更新我的订阅列表
                            mainAdapter.onItemClick(null,null,0,0L);
                        }
                    }
                });
            }
        });
    }
    
    @Override
    public void scrollSubList(String selectCateId,boolean isLocal) {
        if(subListView != null){
            subAdapter.updateCate(selectCateId,isLocal);
        }
    }
    
    @Override
    public void updateMainList() {
        if(mainAdapter.getCount() <= 0){
            loadMainAdapterData();
        }
    }
    
    
}
