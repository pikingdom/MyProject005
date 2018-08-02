package com.nd.hilauncherdev.plugin.navigation.widget.taobaoext.bannerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dian91.ad.AdvertSDKManager;

import java.util.HashMap;
import java.util.Map;

public class MyHorizontalScrollView extends HorizontalScrollView implements
		OnClickListener
{

	/**
	 * 图片滚动时的回调接口
	 * 
	 * @author zhy
	 * 
	 */
	public interface CurrentImageChangeListener
	{
		void onCurrentImgChanged(int position, View viewIndicator);
	}

	private CurrentImageChangeListener mListener;

	private OnBannerClickListener mOnClickListener;

	private static final String TAG = "MyHorizontalScrollView";

	/**
	 * HorizontalListView中的LinearLayout
	 */
	private LinearLayout mContainer;

	/**
	 * 子元素的宽度
	 */
	private int mChildWidth;
	/**
	 * 子元素的高度
	 */
	private int mChildHeight;
	/**
	 * 当前最后一张图片的index
	 */
	private int mCurrentIndex = -1;
	/**
	 * 当前第一张图片的下标
	 */
	private int mFristIndex = -1;
	/**
	 * 当前第一个View
	 */
	private View mFirstView;
	/**
	 * 数据适配器
	 */
	private HorizontalScrollViewAdapter mAdapter;
	/**
	 * 每屏幕最多显示的个数
	 */
	private int mCountOneScreen;
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWitdh;


	private int mScrollInterval = 3000;
	private CountDownTimer mTimer = null;
	private LinearLayout mLinearLayout;
	private Drawable mSelectedDrawable;
	private Drawable mUnselectedDrawable;


	/**
	 * 保存View与位置的键值对
	 */
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	public MyHorizontalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// 获得屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;

		mSelectedDrawable = generateDefaultDrawable(0xfff07bb5);
		mUnselectedDrawable = generateDefaultDrawable(0xffffffff);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
		if (mContainer.getChildCount() > 0){

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
	private void createIndicators(ViewGroup parentView) {
		if (mAdapter.getAdapterData().size()<=1)
			return;
		if (mLinearLayout == null){
			mLinearLayout = new LinearLayout(parentView.getContext());
			mLinearLayout.setTag("indicators");
			mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			mLinearLayout.setGravity(Gravity.RIGHT);
			LayoutParams linearLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			linearLayoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			int margin = dp2px(6);
			linearLayoutParams.setMargins(margin, margin, dp2px(14), margin);
			parentView.addView(mLinearLayout, linearLayoutParams);

			mLinearLayout.removeAllViews();
			int mSpace = dp2px(10);
			int mSize = dp2px(6);
			for (int i = 0; i < mAdapter.getAdapterData().size(); i++) {
				ImageView img = new ImageView(getContext());
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = mSpace/2;
				lp.rightMargin = mSpace/2;
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
		}else{
			ViewGroup parentViewGroup = (ViewGroup)mLinearLayout.getParent();
			if (parentViewGroup != null){
				parentViewGroup.removeView(mLinearLayout);
			}
			parentView.addView(mLinearLayout);
		}
		switchIndicator();
	}
	/**
	 * 改变导航的指示点
	 */
	private void switchIndicator() {
		if (mLinearLayout != null && mLinearLayout.getChildCount() > 1) {
			mLinearLayout.bringToFront();
			int count = mLinearLayout.getChildCount();
			for (int i = 0; i < count; i++) {
				ImageView imageView = ((ImageView) mLinearLayout.getChildAt(i));
				imageView.setImageDrawable(
							i == mCurrentIndex % mAdapter.getAdapterData().size() ? mSelectedDrawable : mUnselectedDrawable);

			}
		}
	}

	/**
	 * set auto scroll time
	 *
	 * @param sec int the second between the switch
	 */
	public void setAutoScroll(int sec) {
		mScrollInterval = sec;
		cancelAutoScroll();
		if (mAdapter!=null&&mAdapter.getAdapterData()!=null) {
			if (mAdapter.getAdapterData().size() < 1)
				return;
			updateContent();
			mTimer = new CountDownTimer(Integer.MAX_VALUE, mScrollInterval * 1000) {
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
		loadNextImg();
		switchIndicator();
	}

	/**
	 * 加载下一张图片
	 */
	public void loadNextImg()
	{
		// 数组边界值计算
		if (mCurrentIndex == mAdapter.getCount() - 1)
		{
			mCurrentIndex = -1;
		}
		//移除第一张图片，且将水平滚动位置置0
		scrollTo(0, 0);
		for (int ii =0;ii<mContainer.getChildCount();ii++) {
			View firstView = mContainer.getChildAt(ii);
			if (firstView.getTag() != "indicators") {
				mViewPos.remove(firstView);
				mContainer.removeViewAt(ii);
			}
		}

		//获取下一张图片，并且设置onclick事件，且加入容器中
		ViewGroup view = mAdapter.getView(++mCurrentIndex, null, mContainer);
		if (view != null) {
			createIndicators(view);//fix every imageview add indicators

			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewPos.put(view, mCurrentIndex);
			//当前第一张图片小标
			mFristIndex++;
			//如果设置了滚动监听则触发
			if (mListener != null)
			{
				notifyCurrentImgChanged();
			}
		}
	}
	/**
	 * 加载前一张图片
	 */
	protected void loadPreImg()
	{
		//如果当前已经是第一张，则返回
		if (mFristIndex == 0)
			return;
		//获得当前应该显示为第一张图片的下标
		int index = mCurrentIndex - mCountOneScreen;
		if (index >= 0)
		{
//			mContainer = (LinearLayout) getChildAt(0);
			//移除最后一张
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);

			//将此View放入第一个位置
			View view = mAdapter.getView(index, null, mContainer);
			if (view != null) {
				mViewPos.put(view, index);
				mContainer.addView(view, 0);
				view.setOnClickListener(this);
				//水平滚动位置向左移动view的宽度个像素
				scrollTo(mChildWidth, 0);
				//当前位置--，当前第一个显示的下标--
				mCurrentIndex--;
				mFristIndex--;
				//回调
				if (mListener != null) {
					notifyCurrentImgChanged();
				}
			}
		}
	}

	/**
	 * 滑动时的回调
	 */
	public void notifyCurrentImgChanged()
	{
		mListener.onCurrentImgChanged(mFristIndex, mContainer.getChildAt(0));
	}

	/**
	 * 初始化数据，设置数据适配器
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter)
	{
		this.mAdapter = mAdapter;
		this.mAdapter.setShowListener(new HorizontalScrollViewAdapter.ShowListener() {
			@Override
			public void showEntity(AdvertSDKManager.AdvertInfo entity) {
				if (mOnClickListener != null){
					mOnClickListener.showEntity(entity);
				}
			}
		});
		mContainer = (LinearLayout) getChildAt(0);
		if (mAdapter.getAdapterData().size()>0) {
			// 获得适配器中第一个View
			final ViewGroup view = mAdapter.getView(0, null, mContainer);
			mContainer.addView(view);
			// 强制计算当前View的宽和高
			if (mChildWidth == 0 && mChildHeight == 0)
			{
				int w = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);
				int h = View.MeasureSpec.makeMeasureSpec(0,
						View.MeasureSpec.UNSPECIFIED);
				view.measure(w, h);
				mChildHeight = view.getMeasuredHeight();
				mChildWidth = view.getMeasuredWidth();
				//fix view width,height
				mCountOneScreen = 1;
				mChildWidth = mScreenWitdh;
				ViewGroup.LayoutParams childParams = view.getLayoutParams();
				if (childParams != null){
					childParams.width = mScreenWitdh;
					childParams.height = mChildHeight;
					view.setLayoutParams(childParams);
				}
			}
			//初始化第一屏幕的元素
			initFirstScreenChildren(mCountOneScreen);

			if (mAdapter!=null&&mAdapter.getAdapterData().size()>1){
				createIndicators(view);
			}
		}
	}

	/**
	 * 加载第一屏的View
	 * 
	 * @param mCountOneScreen
	 */
	public void initFirstScreenChildren(int mCountOneScreen)
	{
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();

		for (int i = 0; i < mCountOneScreen; i++)
		{
			View view = mAdapter.getView(i, null, mContainer);
			if (view != null) {
				view.setOnClickListener(this);
				mContainer.addView(view);
				mViewPos.put(view, i);
				//mCurrentIndex = i;
			}
		}

		if (mListener != null)
		{
			notifyCurrentImgChanged();
		}

	}
	/*
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_MOVE:
//			Log.e(TAG, getScrollX() + "");

			int scrollX = getScrollX();
			// 如果当前scrollX为view的宽度，加载下一张，移除第一张
			if (scrollX >= mChildWidth)
			{
				loadNextImg();
			}
			// 如果当前scrollX = 0， 往前设置一张，移除最后一张
			if (scrollX == 0)
			{
				loadPreImg();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}
	*/

	@Override
	public void onClick(View v)
	{
		if (mOnClickListener != null)
		{
			mOnClickListener.onClick(mViewPos.get(v));
		}
	}

	public void setOnItemClickListener(OnBannerClickListener mOnClickListener)
	{
		this.mOnClickListener = mOnClickListener;
	}

	public void setCurrentImageChangeListener(
			CurrentImageChangeListener mListener)
	{
		this.mListener = mListener;
	}

}
