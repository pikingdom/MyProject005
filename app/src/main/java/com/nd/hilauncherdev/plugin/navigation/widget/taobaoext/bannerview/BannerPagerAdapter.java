package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dian91.ad.AdvertSDKManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yegaofei on 2017/9/8.
 */
public class BannerPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<AdvertSDKManager.AdvertInfo> mEntities = new ArrayList<>();
    private ArrayList<BannerLayout> mLayouts = new ArrayList<>();
    private OnBannerClickListener mClickListener;

    public BannerPagerAdapter(Context context, List<AdvertSDKManager.AdvertInfo> entities) {
        mContext = context;
        this.mEntities = entities;
        setLayouts();
    }

    public void updatePageAdapter(Context context, List<AdvertSDKManager.AdvertInfo> entities){
        mContext = context;
        this.mEntities = entities;
        setLayouts();
    }

    private void setLayouts() {
        mLayouts.clear();
        for (AdvertSDKManager.AdvertInfo entity : mEntities) {
            BannerLayout layout = new BannerLayout(mContext);
            layout.setShowListener(new BannerLayout.ShowListener() {
                @Override
                public void showEntity(AdvertSDKManager.AdvertInfo entity) {
                    if (mClickListener != null)
                        mClickListener.showEntity(entity);
                }
            });
            layout.setEntity(entity);
            mLayouts.add(layout);
        }
    }

    @Override
    public int getCount() {
        return mLayouts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        BannerLayout bannerLayout = mLayouts.get(position);
        bannerLayout.setTag(position);

        bannerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    if (v instanceof BannerLayout) {
                        int tag = (Integer)v.getTag();
                        if (mEntities.size() > 1) {
                            mClickListener.onClick(tag - 1);
                        } else {
                            mClickListener.onClick(tag);
                        }
                    }
                }
            }
        });
        container.addView(bannerLayout, 0);
        return mLayouts.get(position);
    }
    public void scrollTo(int index){
        if (mClickListener != null){
            mClickListener.scrollToIndex(index);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mLayouts.get(position));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    public void setOnBannerClickListener(OnBannerClickListener clickListener) {
        this.mClickListener = clickListener;
    }

}
