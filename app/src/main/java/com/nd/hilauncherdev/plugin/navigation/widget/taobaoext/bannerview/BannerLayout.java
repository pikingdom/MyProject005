package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.AsyncImageLoader;
import com.nostra13.universalimageloader.ex.ImageCallback;

import java.util.Map;

/**
 * Created by yegaofei on 2017/9/8.
 */
public class BannerLayout extends FrameLayout {
  private ImageView mIvBanner;
  private AsyncImageLoader mImageLoader;
  private ShowListener showListener;

  public BannerLayout(Context context) {
    this(context, null);
  }

  public BannerLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.mImageLoader = new AsyncImageLoader();
    View.inflate(getContext(), R.layout.banner_layout, this);
    mIvBanner = (ImageView) this.findViewById(R.id.iv_banner);
  }
  public void setShowListener(ShowListener listener){
      this.showListener = listener;
  }
  public void setEntity(final AdvertSDKManager.AdvertInfo entity) {
      mIvBanner.setImageResource(R.drawable.taobao_banner_background);
      Drawable drawable = mImageLoader.loadDrawable(entity.picUrl, new ImageCallback() {
          @Override
          public void imageLoaded(Drawable imageDrawable, String imageUrl, Map map) {
              if (imageDrawable != null) {
                  if (showListener != null)
                      showListener.showEntity(entity);
                  if (mIvBanner instanceof ImageView) {
                      ImageView imageView = (ImageView) mIvBanner;
                      imageView.setImageDrawable(imageDrawable);
                  }
              }
          }
      });
      if (drawable != null) {
          mIvBanner.setImageDrawable(drawable);
          if (showListener != null)
              showListener.showEntity(entity);
      }
  }
  public interface ShowListener{
      public void showEntity(AdvertSDKManager.AdvertInfo entity);
  }
}
