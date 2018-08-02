package com.nd.hilauncherdev.plugin.navigation.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.TaobaoCate;
import com.nd.hilauncherdev.plugin.navigation.loader.TaobaoLoader;
import com.nd.hilauncherdev.plugin.navigation.util.AdvertSDKController;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.SharedPreferencesUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * description: 淘宝类目Adapter<br/>
 * author: chenxuyu_dian91<br/>
 * date: 2016/3/22<br/>
 */
public class TaobaoCateAdapter extends BaseAdapter {

    private SharedPreferencesUtil spUtil;
    List<TaobaoCate> cacheList;
    private Context ctx;
    private LayoutInflater mInflater;
    private Handler mHandler = new Handler();
    private Object lock;
    private List<TaobaoCate> mData = new ArrayList<TaobaoCate>();
    private AsyncImageLoader mImageLoader;
    private GridView mGridView;
    private boolean hasSubmited = false;

    public TaobaoCateAdapter(Context context, GridView gridview) {
        this.ctx = context;
        this.mInflater = LayoutInflater.from(ctx);
        this.lock = new Object();
        this.mImageLoader = new AsyncImageLoader();
        this.mGridView = gridview;
        this.spUtil = new SharedPreferencesUtil(context);

        final String cateJsonString = spUtil.getString(SharedPreferencesUtil.TAOBAO_CATE);
        cacheList = TaobaoCate.convertFromJsonString(cateJsonString);
        updateCateList(cacheList);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_taobao_cate, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Object obj = getItem(position);
        if (obj instanceof TaobaoCate) {
            TaobaoCate item = (TaobaoCate) obj;
            holder.tvCateName.setText(item.name);
            holder.ivCateIcon.setTag(item.picUrl);
            try {
                holder.ivCateIcon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_taobao_cate_def));
            }catch (Exception e){
                e.printStackTrace();
            }
            Drawable drawable = mImageLoader.loadDrawable(item.picUrl, new ImageCallback() {
                @Override
                public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                    if (imageDrawable != null) {
                        View view = mGridView.findViewWithTag(imageUrl);
                        if (view instanceof ImageView) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageDrawable(imageDrawable);
                        }
                    }
                }
            });

            //不需要报展示
            //CvAnalysis.submitShowEvent(ctx, CvAnalysisConstant.TAOBAO_SCREEN, CvAnalysisConstant.TAOBAO_SCREEN_POS_CATE, item.id, CvAnalysisConstant.RESTYPE_LINKS);
            if (drawable != null) {
                holder.ivCateIcon.setImageDrawable(drawable);
            }
        }

        return convertView;
    }
    private void updateCateList(List<TaobaoCate> cacheList){
        mData.clear();
        hasSubmited = false;
        mData.addAll(cacheList);
        notifyDataSetChanged();
    }
    public void refresh(final IAdapter.CallBack callBack) {
        String cateJsonString = spUtil.getString(SharedPreferencesUtil.TAOBAO_CATE);
        cacheList = TaobaoCate.convertFromJsonString(cateJsonString);
        ThreadUtil.executeMore(new Runnable() {
            @Override
            public void run() {
                final List<TaobaoCate> list = TaobaoLoader.getNetTaobaoCateList(ctx);
                if (list != null && list.size() > 0) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isAvailable()) {
                                updateCateList(list);
                            }
                            if (callBack != null)
                                callBack.onSuccess(false,mData.size());
                        }
                    });
                }else{
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cacheList==null||(cacheList!=null&&cacheList.size() == 0)) {
                                if (callBack != null)
                                    callBack.onError(false);
                            }else{
                                if (callBack != null)
                                    callBack.onSuccess(false,mData.size());
                            }
                        }
                    });
                }
            }
        });
    }

    public void onDestroy() {
        lock = null;
        mHandler.removeCallbacksAndMessages(null);
        mData = null;
    }

    protected boolean isAvailable() {
        return lock != null;
    }

    public void setPageVisible(boolean pageVisible){
        if(!pageVisible){
            hasSubmited = false;
        }
    }

    public void handleSubmit(boolean isPageVisible){
        if(isPageVisible && !hasSubmited){
            for (TaobaoCate item: mData) {
                if(item.callBackFlag == 1) {
                    AdvertSDKController.submitECShowURL(ctx, 3, item.adSourceId);
                    hasSubmited = true;
                    break;
                }
            }
        }
    }

    private class ViewHolder {
        ImageView ivCateIcon;
        TextView tvCateName;

        public ViewHolder(View view) {
            ivCateIcon = (ImageView) view.findViewById(R.id.iv_cate_icon);
            tvCateName = (TextView) view.findViewById(R.id.tv_cate_name);
        }
    }
}
