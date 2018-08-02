package com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel;

import java.util.List;

/**
 * 滑屏数据接口<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public interface ICommonData {

	/**
	 * 是否锁定 - 锁定该数据集情况下，CommonSlidingView将不能滑动至其他数据集<br>
	 * 该方法仅供CommonSlidingView调用，一般情况下外部不直接调用该方法<br>
	 * 仅当当前数据集已锁定，且需锁定非当前数据集时，外部可调用该方法锁定非当前数据集<br>
	 * 若需锁定当前数据集，请调用CommonSlidingView中的lockData(boolean isLock)方法<br>
	 * 若当前数据集不锁定，而滑入一个锁定的数据集，将导致显示异常
	 */
	public void setLock(boolean isLock);

	public boolean isLock();

	public int getChildViewWidth();

	public int getChildViewHeight();

	public int getColumnNum();

	public int getRowNum();

	public void setTag(Object tag);

	public Object getTag();

	public List<ICommonDataItem> getDataList();

	public int getPageNum();
	
	public void setRowPadding(int rowPadding);
	
	public int getRowPadding();
	
	public void setColumnPadding(int columnPadding);
	
	public int getColumnPadding();
	
	/**
	 * 父View宽高较小时是否保持子View宽高
	 */
	public boolean isKeepChildViewWidthAndHeight();
	
	public void setKeepChildViewWidthAndHeight(boolean isKeepChildViewWidthAndHeight);
	
	public int getActualChildViewWidth();
	
	public int getActualChildViewHeight();
	
	public void setActualChildViewWidth(int actualViewWidth);
	
	public void setActualChildViewHeight(int actualChildViewHeight);
}
