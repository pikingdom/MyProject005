package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dian91.ad.AdvertSDKManager;
import com.nd.hilauncherdev.plugin.navigation.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yegaofei on 2017/9/8.
 */

public class BannerView extends FrameLayout {

  private long mAutoScrollDelay = 3000;
  private int mScrollInterval = 3000;
  private Timer mAutoScrollTimer;
  private NoScrollViewPager mVpBanner;
  private LinearLayout mLinearLayout;
  private Drawable mSelectedDrawable;
  private Drawable mUnselectedDrawable;
  private List<AdvertSDKManager.AdvertInfo> mEntities = new ArrayList<>();
  private BannerPagerAdapter mAdapter;
  private Handler mMainHandler;



  private int currentIdx = 0;
  private CountDownTimer mTimer = null;

  public BannerView(Context context) {
    this(context, null);
  }

  public BannerView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    View.inflate(getContext(), R.layout.banner_view, this);
    mVpBanner = (NoScrollViewPager) this.findViewById(R.id.vp_banner);
    mSelectedDrawable = generateDefaultDrawable(0xfff07bb5);
    mUnselectedDrawable = generateDefaultDrawable(0xffffffff);
    mLinearLayout = new LinearLayout(context);
    mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
    mLinearLayout.setGravity(Gravity.RIGHT);
    LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    linearLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
    int margin = dp2px(6);
    linearLayoutParams.setMargins(margin, margin, dp2px(14), margin);
    addView(mLinearLayout, linearLayoutParams);

    mMainHandler = new Handler(context.getMainLooper());
  }

  public void setEntities(List<AdvertSDKManager.AdvertInfo> entities) {
    addExtraPage(entities);
    showBanner();
    if (entities == null)
      return;
    mAdapter.notifyDataSetChanged();
    if (entities.size()>1){
      createIndicators();
    }
  }

  private void addExtraPage(List<AdvertSDKManager.AdvertInfo> entities) {
    if (entities == null)
      return;
    mEntities.clear();
    if (entities.size()>1) {
      mEntities.add(entities.get(entities.size() - 1));
      mEntities.addAll(entities);
      mEntities.add(entities.get(0));
    }else{
      mEntities.addAll(entities);
    }
  }

  private int dp2px(int dp) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            Resources.getSystem().getDisplayMetrics());
  }

  /**
   * 默认指示器是一系列直径为4dp的小圆点
   */
  private GradientDrawable generateDefaultDrawable(int color) {
    GradientDrawable gradientDrawable = new GradientDrawable();
    gradientDrawable.setSize(dp2px(4), dp2px(4));
    gradientDrawable.setCornerRadius(dp2px(4));
    gradientDrawable.setColor(color);
    return gradientDrawable;
  }
  /**
   * 指示器整体由数据列表容量数量的AppCompatImageView均匀分布在一个横向的LinearLayout中构成
   * 使用AppCompatImageView的好处是在Fragment中也使用Compat相关属性
   */
  private void createIndicators() {
    mLinearLayout.removeAllViews();
    int mSpace = dp2px(10);
    int mSize = dp2px(6);
    for (int i = 0; i < mEntities.size()-2; i++) {
      ImageView img = new ImageView(getContext());
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      lp.leftMargin = mSpace / 2;
      lp.rightMargin = mSpace / 2;
      lp.bottomMargin = dp2px(6);
      if (mSize >= dp2px(4)) { // 设置了indicatorSize属性
        lp.width = lp.height = mSize;
      } else {
        // 如果设置的resource.xml没有明确的宽高，默认最小2dp，否则太小看不清
        img.setMinimumWidth(dp2px(2));
        img.setMinimumHeight(dp2px(2));
      }
      img.setImageDrawable(i == 0 ? mSelectedDrawable : mUnselectedDrawable);
      mLinearLayout.addView(img, lp);
    }
    switchIndicator();
  }
  /**
   * 改变导航的指示点
   */
  private void switchIndicator() {
    if (mLinearLayout != null && mLinearLayout.getChildCount() > 1) {
        int count = mLinearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
          ImageView imageView = ((ImageView) mLinearLayout.getChildAt(i));
          if (currentIdx == 0){
              if (i == count -1)
                  imageView.setImageDrawable(mSelectedDrawable);
              else
                  imageView.setImageDrawable(mUnselectedDrawable);
          }else if(currentIdx == mEntities.size()-1){
              if (i == 0)
                  imageView.setImageDrawable(mSelectedDrawable);
              else
                  imageView.setImageDrawable(mUnselectedDrawable);
          }else {
              imageView.setImageDrawable(
                      i == (currentIdx - 1) % (mEntities.size()-2) ? mSelectedDrawable : mUnselectedDrawable);
          }
        }
    }
  }
  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  private void showBanner() {
    if (mAdapter == null) {
      mAdapter = new BannerPagerAdapter(getContext(), mEntities);
    }else{
      mAdapter.updatePageAdapter(getContext(),mEntities);
    }
    mVpBanner.setAdapter(mAdapter);
    mVpBanner.setCurrentItem(1, true);
    currentIdx = 1;
    mVpBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentIdx = position;
        if (positionOffsetPixels == 0.0) {
          setViewPagerItemPosition(position);
        }
        switchIndicator();
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  /**
   * set auto scroll time
   *
   * @param sec int the second between the switch
   */
  public void setAutoScroll(int sec) {
    mScrollInterval = sec;
    cancelAutoScroll();
    if (mEntities!=null) {
      if (mEntities.size() < 1)
        return;
      mTimer = new CountDownTimer(Integer.MAX_VALUE, sec * 1000) {
        @Override
        public void onTick(long l) {
          updateContent();
        }

        @Override
        public void onFinish() {
          setAutoScroll(mScrollInterval);
        }
      };
      mTimer.start();
    }
  }
  public void cancelAutoScroll(){
    if (this.mTimer != null) {
      this.mTimer.cancel();
      this.mTimer = null;
    }
  }

  public void updateContent(){
    if (mEntities!=null&&mEntities.size()>1) {
      if (currentIdx >= mEntities.size()) {
        currentIdx = 0;
      }
      mVpBanner.setCurrentItem(currentIdx, true);
      currentIdx++;
    }
  }

  private void setViewPagerItemPosition(int position) {
    if (mEntities.size()>1){
      int index = position;
      if (position == mEntities.size() - 1) {
        mVpBanner.setCurrentItem(1, false);
        //显示第0个
        index = 0;
      } else if (position == 0) {
        mVpBanner.setCurrentItem(mEntities.size() - 2, false);
        //显示最后一个
        index = mEntities.size()-3;
      } else {
        mVpBanner.setCurrentItem(position);
        //显示当前页 或 -1
        index = position -1;
      }
      if (mAdapter != null) {
        mAdapter.scrollTo(index);
      }
    }else {
      if (mAdapter != null) {
        mAdapter.scrollTo(position);
      }
    }

  }

  @TargetApi(Build.VERSION_CODES.GINGERBREAD)
  private void nextScroll() {
    int position = mVpBanner.getCurrentItem();
    setViewPagerItemPosition(position + 1);
  }

  public void startAutoScroll() {
    stopAutoScroll();
    mAutoScrollTimer = new Timer();
    mAutoScrollTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        mMainHandler.post(new Runnable() {
          @Override
          public void run() {
            nextScroll();
          }
        });
      }
    }, mAutoScrollDelay, mAutoScrollDelay);
  }

  public void stopAutoScroll() {
    if (mAutoScrollTimer != null) {
      mAutoScrollTimer.cancel();
      mAutoScrollTimer = null;
    }
  }

  public void setAutoScrollDelay(long delay) {
    this.mAutoScrollDelay = delay;
  }

  public void setOnBannerClickListener(OnBannerClickListener clickListener) {
    if (mAdapter != null) {
      mAdapter.setOnBannerClickListener(clickListener);
    }
  }
}
