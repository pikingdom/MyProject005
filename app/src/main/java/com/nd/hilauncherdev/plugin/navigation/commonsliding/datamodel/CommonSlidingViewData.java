package com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel;

import java.util.List;

/**
 * 滑屏数据<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CommonSlidingViewData implements ICommonData {

	/**
	 * 是否锁定 - 锁定该数据集情况下，Workspace将不能滑动至其他数据集
	 */
	private boolean isLock = false;

	/**
	 * 期望子View宽度
	 */
	private int childViewWidth;

	/**
	 * 期望子View高度
	 */
	private int childViewHeight;
	
	
	/**
	 * 实际子View宽度
	 */
	private int actualChildViewWidth;

	/**
	 * 实际子View高度
	 */
	private int actualChildViewHeight;
	/**
	 * 父View宽高较小时是否保持子View宽高
	 */
	private boolean isKeepChildViewWidthAndHeight;

	/**
	 * 列数
	 */
	private int columnNum;

	/**
	 * 行数
	 */
	private int rowNum;

	/**
	 * 附加信息
	 */
	private Object tag;

	/**
	 * 数据集
	 */
	private List<ICommonDataItem> dataList;
	
	/**
	 * 行间距
	 */
	private int rowPadding;
	
	/**
	 * 列间距
	 */
	private int columnPadding;

	public CommonSlidingViewData(int childViewWidth, int childViewHeight,
			int columnNum, int rowNum, List<ICommonDataItem> dataList) {
		this.childViewWidth = childViewWidth;
		this.childViewHeight = childViewHeight;
		this.columnNum = columnNum;
		this.rowNum = rowNum;
		this.dataList = dataList;
	}

	@Override
	public boolean isLock() {
		return isLock;
	}

	@Override
	public void setLock(boolean isLock) {
		this.isLock = isLock;
	}

	@Override
	public int getColumnNum() {
		return columnNum;
	}

	@Override
	public int getRowNum() {
		return rowNum;
	}

	@Override
	public Object getTag() {
		return tag;
	}

	@Override
	public void setTag(Object tag) {
		this.tag = tag;
	}

	@Override
	public List<ICommonDataItem> getDataList() {
		return dataList;
	}

	public void setDataList(List<ICommonDataItem> dataList) {
		this.dataList = dataList;
	}

	/**
	 * 返回该数据集占用页数
	 * 
	 * @return 页数
	 */
	@Override
	public int getPageNum() {
		if (dataList == null || dataList.size() == 0) {
			return 1;
		}
		return (dataList.size() - 1) / (rowNum * columnNum) + 1;
	}

	@Override
	public int getChildViewWidth() {
		return childViewWidth;
	}

	@Override
	public int getChildViewHeight() {
		return childViewHeight;
	}
	
	@Override
	public boolean isKeepChildViewWidthAndHeight() {
		return isKeepChildViewWidthAndHeight;
	}

	/**
	 * @param columnNum the columnNum to set
	 */
	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	/**
	 * @param rowNum the rowNum to set
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	/**
	 * @param childViewHeight the childViewHeight to set
	 */
	public void setChildViewHeight(int childViewHeight) {
		this.childViewHeight = childViewHeight;
	}

	/**
	 * @param childViewWidth the childViewWidth to set
	 */
	public void setChildViewWidth(int childViewWidth) {
		this.childViewWidth = childViewWidth;
	}

	/**
	 * @param isKeepChildViewWidthAndHeight
	 */
	public void setKeepChildViewWidthAndHeight(boolean isKeepChildViewWidthAndHeight) {
		this.isKeepChildViewWidthAndHeight = isKeepChildViewWidthAndHeight;
	}

	@Override
	public void setRowPadding(int rowPadding) {
		this.rowPadding = rowPadding;
	}

	@Override
	public int getRowPadding() {
		return rowPadding;	
	}

	@Override
	public void setColumnPadding(int columnPadding) {
		this.columnPadding = columnPadding;
	}

	@Override
	public int getColumnPadding() {
		return columnPadding;		
	}
	
	@Override
	public int getActualChildViewHeight() {

		return actualChildViewHeight;
	}
	@Override
	public int getActualChildViewWidth() {
		return actualChildViewWidth;
	}
	@Override
	public void setActualChildViewWidth(int actualWidth)
	{
		this.actualChildViewWidth=actualWidth;
	}
	@Override
	public void setActualChildViewHeight(int actualHeight)
	{
		this.actualChildViewHeight=actualHeight;
	}
	
}
