package com.nd.hilauncherdev.plugin.navigation.widget;

/**
 * 横向滑动的子View当前状态接口
 */
public interface ISlidingConflict {
	
	/**
	 * 设置支持横向滑动的子View当前状态
	 * @param isHorizontalSlidingInChild 子View是否支持横向滑动
	 * @param isChildEndlessScrolling 子View是否循环滚动
	 * @param childViewTotalScreen 支持横向滑动的子View总页数
	 * @param childViewCurrentScreen 支持横向滑动的子View当前所在页
	 */
	public void setChildSlidingViewState(boolean isHorizontalSlidingInChild, boolean isChildEndlessScrolling, int childViewTotalScreen, int childViewCurrentScreen);
	
	/**
	 * 清除支持横向滑动的子View当前状态
	 */
	public void clearChildSlidingViewState();

}
