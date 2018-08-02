package com.nd.hilauncherdev.plugin.navigation.commonsliding;

import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.ICommonDataItem;


/**
 * 滑屏ViewHolder<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CommonViewHolder {
	
	/**
	 * 记录View在所在数据集中的位置
	 */
	public int positionInData;
	
	/**
	 * 记录View在所在屏幕中的位置
	 */
	public int positionInScreen;
	
	/**
	 * 记录View在所在屏幕
	 */
	public int screen;
	
	public ICommonDataItem item;
}
