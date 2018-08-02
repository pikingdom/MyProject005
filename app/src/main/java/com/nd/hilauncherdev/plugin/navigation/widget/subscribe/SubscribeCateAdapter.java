package com.nd.hilauncherdev.plugin.navigation.widget.subscribe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.SubscribeCateBean;
import java.util.ArrayList;

/**
 * Created by linliangbin on 16-7-12.
 */
public class SubscribeCateAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    
    private  Context context;
    private ArrayList<SubscribeCateBean> subscribeCateBeans = new ArrayList<SubscribeCateBean>();
    mainSubInterface callback;
    private ListView listView;
    
    //当前选中位置
    private int currentSelected = -1;
    
    
    public SubscribeCateAdapter(ListView listView, Context context, mainSubInterface callback){
        this.context = context;
        this.listView = listView;
        this.callback = callback;
        this.listView.setOnItemClickListener(this);
    }
    
    @Override
    public int getCount() {
        return subscribeCateBeans.size();
    }
    
    @Override
    public Object getItem(int position) {
        return subscribeCateBeans.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.subscribe_cate_main_list_item,null);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.cate_name);
            convertView.setTag(viewHolder);
        }
        SubscribeCateBean subscribeCateBean = (SubscribeCateBean) getItem(position);
        if (subscribeCateBean != null) {
            viewHolder.nameText.setText(subscribeCateBean.cateName);
            if (subscribeCateBean.isSelected) {
                viewHolder.nameText.setBackgroundColor(context.getResources().getColor(R.color.game_card_white));
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.navi_card_subscribe_selected));
            } else {
                viewHolder.nameText.setBackgroundColor(context.getResources().getColor(R.color.common_header_content_sep));
                viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.subscribe_unselected_color));
            }
        }
        return convertView;
    }
    
    /**
     * 设置数据
     * @param subscribeCateBeans
     */
    public void setData(ArrayList<SubscribeCateBean> subscribeCateBeans){
        this.subscribeCateBeans = subscribeCateBeans;
        if (this.subscribeCateBeans != null && this.subscribeCateBeans.size() > 0) {
            updateSelected(0);
            if(this.subscribeCateBeans.size() > currentSelected){
                SubscribeCateBean subscribeCateBean = this.subscribeCateBeans.get(currentSelected);
                if(subscribeCateBean.isLocal){
                    callback.scrollSubList(subscribeCateBean.siteIdString,true);
                }else{
                    callback.scrollSubList(subscribeCateBean.cateId+"",false);
                }
                
                notifyDataSetChanged();
            }
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        
        try {
            if(currentSelected == position)
                return;
    
    
            updateSelected(position);
            SubscribeCateBean subscribeCateBean = (SubscribeCateBean) getItem(position);
            if(subscribeCateBean != null){
//            scrollListViewToPosition(position);
                if(subscribeCateBean.isLocal){
                    callback.scrollSubList(SubscribeHelper.getAddedSubscribeSite(context),true);
                }else{
                    callback.scrollSubList(subscribeCateBean.cateId+"",false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
    
    /**
     * 让当前选中的item 滚动到显示的中间
     * @param position
     */
    private void scrollListViewToPosition(int position){
        
        int firstVisible = listView.getFirstVisiblePosition();
        int listViewHeight = listView.getMeasuredHeight();
        int ItenHeight = listView.getChildAt(position-firstVisible).getMeasuredHeight();
        int showCount = listViewHeight / ItenHeight;
    
        int middle = firstVisible + showCount/2;
        if(position > middle){
            listView.smoothScrollToPosition(position - showCount/2);
            listView.setSelection(position - showCount);
        }else{
            listView.smoothScrollToPosition(position - showCount/2);
            listView.setSelection(position - showCount);
        }

    }
    /**
     * 更新当前显示位置
     * @param selected
     */
    public void updateSelected(int selected){
        
        for(int i=0;i<getCount();i++){
            SubscribeCateBean subscribeCateBean = (SubscribeCateBean) getItem(i);
            if(subscribeCateBean == null)
                continue;
            if(i == selected){
                subscribeCateBean.isSelected = true;
            }else if(subscribeCateBean.isSelected == true){
                subscribeCateBean.isSelected =false;
            }
        }
        currentSelected = selected;
        notifyDataSetChanged();
    }
    
    class ViewHolder{
        TextView nameText;
    }

}
