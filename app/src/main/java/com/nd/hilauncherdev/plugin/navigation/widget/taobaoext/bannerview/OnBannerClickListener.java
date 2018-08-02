package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import com.dian91.ad.AdvertSDKManager;

/**
 * Created by yegaofei on 2017/9/8.
 */
public interface OnBannerClickListener {
    void onClick(int position);
    void scrollToIndex(int position);
    void showEntity(AdvertSDKManager.AdvertInfo entity);
}
