package com.nd.hilauncherdev.plugin.navigation.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdBean;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdPluginDownloadManager;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.AdViewManager;
import com.nd.hilauncherdev.plugin.navigation.pluginAD.GlobalConst;
import com.nd.hilauncherdev.plugin.navigation.util.ActualTimeAnalysis;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nostra13.universalimageloader.ex.ImageCallback;
import java.util.Map;


/**
 * Created by linliangbin_dian91 on 2015/10/14.
 */
public class AdViewHelper {
    private String SP_NAME = "naviagation_ad";
    private String SP_HAS_AD_REMOVED = "navigation_ad_hasremoved";
    AsyncImageLoader asyncImageLoader;


    public void initCardView(final Context context, final LinearLayout bannerLayout) {
        final ImageView bannerImage = (ImageView) bannerLayout.findViewById(R.id.navigation_view_ad_banner);
        final TextView closeText = (TextView) bannerLayout.findViewById(R.id.navigation_view_ad_close);
        try {
            final AdBean ad = AdPluginDownloadManager.getPackageNamebyTypePosition(context,GlobalConst.AD_TYPE_91LAUNCHER, GlobalConst.AD_POSITION_NAVIGATION_BANNER);
            if(bannerLayout == null)
                return;
            if(ad == null){
                bannerLayout.setVisibility(View.GONE);
                return;
            }
            bannerLayout.setVisibility(View.VISIBLE);
            String lastCloseAd = getAdRemoved(context);
            final String currentAd = ad.iconUrl + "_" + ad.targetUrl;
            //手动关闭过图片地址和跳转链接相同的广告，跳过继续显示
            if (!TextUtils.isEmpty(lastCloseAd) &&  lastCloseAd.equals(currentAd)) {
                bannerLayout.setVisibility(View.GONE);
                return;
            }

            if (ad != null && AdViewManager.isBetween(ad.startTime, ad.endTime) && !TextUtils.isEmpty(ad.iconUrl) && !TextUtils.isEmpty(ad.targetUrl)) {
                if (asyncImageLoader == null)
                    asyncImageLoader = new AsyncImageLoader();
                Drawable cachedImage = asyncImageLoader.loadDrawable(ad.iconUrl, new ImageCallback() {
                    @Override
                    public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
                        if (imageDrawable != null && imageUrl.equals(ad.iconUrl)) {
                            bannerLayout.setVisibility(View.VISIBLE);
                            bannerImage.setImageDrawable(imageDrawable);
                            int width = ScreenUtil.getScreenWidth(context);
                            int height = (int)(width*((float)160/600));
                            ViewGroup.LayoutParams rlp = bannerImage.getLayoutParams();
                            rlp.height = height;
                            rlp.width = width;
                            bannerImage.setLayoutParams(rlp);

                        } else if (imageDrawable == null && imageUrl.equals(ad.iconUrl)) {
                            bannerLayout.setVisibility(View.GONE);
                        }
                    }
                });

                if (cachedImage != null) {
                    bannerImage.setImageDrawable(cachedImage);
                }
                bannerImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LauncherCaller.openUrl(context,"",ad.targetUrl);
                        ActualTimeAnalysis.sendActualTimeAnalysis(context, ActualTimeAnalysis.TAOBAO_VALID_HITS, "0tlc");
                    }
                });

                closeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerLayout.setVisibility(View.GONE);
                        setCurrentAdClosed(context, currentAd);
                    }
                });

            } else {
                bannerLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCurrentAdClosed(Context context, String ad) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_HAS_AD_REMOVED, ad).commit();
    }

    public String getAdRemoved(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_HAS_AD_REMOVED, "");
    }



}
