package com.nd.hilauncherdev.plugin.navigation.commonsliding;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 滑屏<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CommonLayout extends ViewGroup {

	private int layoutNum;
	
	public CommonLayout(Context context) {
		super(context);
	}

	public CommonLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommonLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CommonLayout(Context context, CommonSlidingView workspace) {
		super(context);
		this.layoutNum = workspace.pageViews.size();
	}

	@Override
	public boolean addViewInLayout(View child, int index,
			LayoutParams params, boolean preventRequestLayout) {
		return super
				.addViewInLayout(child, index, params, preventRequestLayout);
	}

	@Override
	protected void setChildrenDrawingCacheEnabled(boolean enabled) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View view = getChildAt(i);
			view.setDrawingCacheEnabled(enabled);
			view.buildDrawingCache(enabled);
		}
		super.setChildrenDrawingCacheEnabled(enabled);
	}

	@Override
	public void setChildrenDrawnWithCacheEnabled(boolean enabled) {
		super.setChildrenDrawnWithCacheEnabled(enabled);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {

	}
	
	public int getLayoutNum(){
		return layoutNum;
	}
	
	public void callDispatchDraw(Canvas canvas){		
		super.dispatchDraw(canvas);
	}
	
}
