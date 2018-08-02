package com.nd.hilauncherdev.plugin.navigation.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.activity.BaseActivity;
import com.nd.hilauncherdev.plugin.navigation.activity.NavigationSiteDetailActivity;
import com.nd.hilauncherdev.plugin.navigation.util.AnalyticsConstant;
import com.nd.hilauncherdev.plugin.navigation.util.CvAnalysisConstant;
import com.nd.hilauncherdev.plugin.navigation.util.IntegralTaskIdContent;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

/**
 * 网址大全页面<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NavigationSiteView extends View {

	public static final String EXTRA_STAT_SITE_ID = "stat_site_id";
	public static final String EXTRA_STAT_IS_HISTORY = "stat_is_history";

	private OnCellTouchListener mOnCellTouchListener = null;
	private int paddingLeft, paddingTop, paddingRight;
	private Paint linePaint;
	/**
	 * 宽高
	 */
	private int width, height;

	/**
	 * 网址格的宽高
	 */
	private int siteWidth, siteHeight;
	// private int expandWidth;
	/**
	 * 网址格的行数
	 */
	protected int siteRow;

	/**
	 * 字体大小
	 */
	private float CELL_TEXT_SIZE = 21;
	/**
	 * 字体画笔
	 */
	private Paint textPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
	/**
	 * 标题以及其位置
	 */
	private List<NavigationCell> cellList = new ArrayList<NavigationCell>();
	protected List<NavigationCategory> categoryList;

	protected Handler mHandler = new Handler();
	/**
	 * 最开始选中的单元格
	 */
	private NavigationCell originCell;

	/**
	 * 显示加载中的文字
	 */
	private String loading;

	private Context mContext;

	private static final int SITE_COLUMN = 4; // 网址导航的列数

	protected boolean reInitCell = false;// 重新初始化导航网址数据

	private boolean isAddSiteActivity = false; // 是否是添加网址导航的页面。标志。

	protected NavigationDetailSiteView mNavigationView;
	private int mCategoryCellOccupyRow = 2;
	private int mCategoryCellOccupyCol = 1;
	private Drawable mCategoryBg, mCategoryMoreBg;
	private ScrollView mScrollView;
	private float mTouchDownY = -1;

	private NavigationSiteDetailActivity.onNaviClickCallBack callBack;
	private boolean needJump = true;

	public void initCallBack(NavigationSiteDetailActivity.onNaviClickCallBack call,boolean isNeed){
		callBack = call;
		needJump = isNeed;
	}

	public NavigationSiteView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		mContext = context;
		CELL_TEXT_SIZE = ScreenUtil.dip2px(context, 15);
		siteHeight = ScreenUtil.dip2px(context, 50);
		// expandWidth = ScreenUtil.dip2px(context, 40);

		linePaint = new Paint();
		if (isAddSiteActivity)
			linePaint.setStrokeWidth(0.5f);
		else
			linePaint.setStrokeWidth(1.0f);
		linePaint.setAntiAlias(true);

		textPaint.setTextSize(CELL_TEXT_SIZE);
		textPaint.setColor(getResources().getColor(R.color.navi_card_item_text_pressed));
		loading = context.getResources().getString(R.string.frame_viewfacotry_data_load_text) + "...";

		if (!needDrawCategoryCell()) {
			mCategoryCellOccupyRow = 0;
			mCategoryCellOccupyCol = 0;
		} else {
			mCategoryBg = getResources().getDrawable(R.drawable.launcher_navigation_category_bg);
			mCategoryMoreBg = getResources().getDrawable(R.drawable.launcher_navigation_category_bg_more);
		}
	}

	void setScrollView(ScrollView scrollView) {
		mScrollView = scrollView;
	}

	void setNavigationView(NavigationDetailSiteView view) {
		mNavigationView = view;
	}

	public void setIsAddSiteActivity(boolean bol) {
		isAddSiteActivity = bol;
	}

	public interface WebSiteClickCallback {
		void doAction(String name, String url);
	}

	public void setOnCellTouchLinstener(final Object target) {
		setOnItemTouchListener(new OnCellTouchListener() {
			NavigationCell mEventDownCell;

			@Override
			public void onTouch(NavigationCell cell, MotionEvent event) {
				final int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mEventDownCell = cell;
					break;
				case MotionEvent.ACTION_UP:
					NavigationCell upCell = cell;
					if (!upCell.equals(mEventDownCell)) {
						break;
					}
					Object cellObject = cell.getObject();
					if (cellObject instanceof NavigationCategoryItem) {// 点击分类子项
						NavigationCategoryItem item = (NavigationCategoryItem) cellObject;
						if (TextUtils.isEmpty(item.url))
							break;
						if (target instanceof NavigationFavoriteSiteView) {// 在网址导航页面
							if (mContext instanceof BaseActivity) {
								((BaseActivity) mContext).submitEvent(mContext, AnalyticsConstant.EVENT_FIRST_SCREEN_LINK_CLICK, cell.getDesc());
							}

							openUrl(item, isHistory());
						} else if (target instanceof WebSiteClickCallback) {// 在添加网址页面
							WebSiteClickCallback callback = (WebSiteClickCallback) target;
							callback.doAction(item.name, item.url);
						}
					} else if (cellObject instanceof NavigationCategory || cellObject instanceof NavigationCategoryExpand) {// 打开或关闭分类下的更多网址导航
						NavigationCategory cat;
						if (cellObject instanceof NavigationCategoryExpand) {
							cat = ((NavigationCategoryExpand) cellObject).category;
						} else {
							cat = (NavigationCategory) cellObject;
						}

						int maxWhenUnexpanded = (SITE_COLUMN - mCategoryCellOccupyCol) * getCategoryUnexpandedRow();
						if (cat.items.size() > maxWhenUnexpanded) {
							cat.showAll = !cat.showAll;
							final int rowDiff = (int) Math.ceil(((double) (cat.items.size() - maxWhenUnexpanded)) / SITE_COLUMN);
							siteRow = cat.showAll ? (siteRow + rowDiff) : (siteRow - rowDiff);
							requestLayout();
							reInitCell = true;

							if (cat.showAll && mScrollView != null) {
								int screenHeight = ScreenUtil.getCurrentScreenHeight(getContext());
								// 由于不好计算被点中的category当前在屏幕什么位置，故(rowDiff+1)，多滑动一点距离
								final int minSpace = siteHeight * (rowDiff + 1);
								if ((screenHeight - mTouchDownY) < minSpace) {
									post(new Runnable() {
										@Override
										public void run() {
											mScrollView.smoothScrollBy(0, minSpace);
										}
									});
								}
							}
						}
					}
					break;
				}
			}
		});
	}

	private void openUrl(NavigationCategoryItem item, boolean isHistory) {
		if (item == null) {
			return;
		}
		if(needJump) {
			int resId = 0;
			try {
				resId = Integer.parseInt(item.siteId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LauncherCaller.openUrl(mContext, "", item.url, IntegralTaskIdContent.NAVIGATION_RECOMMEND_WEB,
					CvAnalysisConstant.NAVIGATION_SCREEN_INTO, CvAnalysisConstant.NAVIGATION_SCREEN_CLASSIFICATION_NAVIGATION_CLICK, resId, CvAnalysisConstant.RESTYPE_LINKS,
					LauncherCaller.LOC_WITHOUT_QQ_BROWSER);
		}
		else
			callBack.onNaviSiteClicked(true,item.url);
	}

	/**
	 * 是否需要绘制分类
	 */
	protected boolean needDrawCategoryCell() {
		return true;
	}

	/**
	 * 分类未展开时显示的row
	 */
	private int getCategoryUnexpandedRow() {
		return Math.max(mCategoryCellOccupyRow, 1);
	}

	protected void refreshCategoryList() {
		categoryList = NavigationLoader.getAllSites(mContext);
	}

	/**
	 * Description: 初次加载网站大全内容 Author: guojy Date: 2013-4-1 上午10:39:36
	 */
	public void loadSites() {
		refreshCategoryList();
		siteRow = categoryList != null ? (categoryList.size() * getCategoryUnexpandedRow()) : 0;

		NavigationLoader.getCategoryIconFromServer(mContext, categoryList, this);

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				requestLayout();
				reInitCell = true;
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		paddingLeft = getPaddingLeft();
		paddingTop = getPaddingTop();
		paddingRight = getPaddingRight();

		width = MeasureSpec.getSize(widthMeasureSpec);
		if (cellList.size() == 0) {// 用于网络比较差加载慢时，显示提示词的高度
			height = ScreenUtil.dip2px(mContext, 50);
		} else {
			height = siteHeight * siteRow + getPaddingTop() + getPaddingBottom();
		}
		setMeasuredDimension(width, height);

		// siteWidth = (width - paddingLeft - paddingRight - expandWidth) /
		// SITE_COLUMN;
		siteWidth = (width - paddingLeft - paddingRight) / SITE_COLUMN;

		initCells();
	}

	private void initCells() {
		if (categoryList == null || !reInitCell)
			return;

		reInitCell = false;
		cellList.clear();
		int line = -1;
		Rect categoryRect = new Rect();
		Rect categoryExRect = new Rect();
		Rect cellRect = new Rect();
		NavigationCategory category;
		NavigationCategoryExpand ex;
		NavigationCategoryItem item;
		for (int i = 0; i < categoryList.size(); i++) {
			line++;
			// 分类
			categoryRect.left = paddingLeft;
			categoryRect.top = siteHeight * line + paddingTop;
			categoryRect.right = categoryRect.left + siteWidth;
			categoryRect.bottom = categoryRect.top + siteHeight * mCategoryCellOccupyRow;
			category = categoryList.get(i);
			int maxWhenUnexpanded = (SITE_COLUMN - mCategoryCellOccupyCol) * getCategoryUnexpandedRow();
			boolean hasMore = (category.items.size() > maxWhenUnexpanded);
			NavigationCell titleCell = new NavigationCell(category, new Rect(categoryRect), CELL_TEXT_SIZE, hasMore);
			cellList.add(titleCell);

			// 分类扩展图标
			// categoryExRect.left = categoryRect.left + siteWidth *
			// SITE_COLUMN;
			// categoryExRect.top = categoryRect.top;
			// categoryExRect.right = categoryExRect.left + expandWidth;
			// categoryExRect.bottom = categoryRect.bottom;
			// ex = new NavigationCategoryExpand();
			// ex.category = category;
			// NavigationCell expand = new NavigationCell(ex, new
			// Rect(categoryExRect));
			// cellList.add(expand);

			int size = category.showAll ? category.items.size() : Math.min(category.items.size(), (getCategoryUnexpandedRow() * (SITE_COLUMN - mCategoryCellOccupyCol)));
			int count = mCategoryCellOccupyCol + 1;
			int left = categoryRect.left;
			int top = categoryRect.top;
			int rowCount = 1;
			for (int j = 0; j < size; j++) {
				item = category.items.get(j);
				cellRect.left = left + siteWidth * ((count - 1) % SITE_COLUMN);
				cellRect.top = top;
				cellRect.right = cellRect.left + siteWidth;
				cellRect.bottom = cellRect.top + siteHeight;
				NavigationCell itemCell = new NavigationCell(item, new Rect(cellRect), CELL_TEXT_SIZE, item.isRed, titleCell.showAll);
				itemCell.setDesc(category.title + "[" + j + "]");
				cellList.add(itemCell);

				count++;
				if (count % SITE_COLUMN == 1 && (j + 1 < size)) {
					rowCount++;
					if (rowCount <= mCategoryCellOccupyRow) {
						count += mCategoryCellOccupyCol;
					}
					line++;
					top += siteHeight;
				}
			}

			int diffDefault = ((SITE_COLUMN - mCategoryCellOccupyCol) * mCategoryCellOccupyRow) - size;
			if (diffDefault > 0) {
				line += (diffDefault / (SITE_COLUMN - mCategoryCellOccupyCol));
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (cellList.size() == 0) {
			// 加载中
			textPaint.setTextAlign(Align.CENTER);
			canvas.drawText(loading, width / 2, height / 2, textPaint);
		} else {
			drawNavigationCells(canvas);
		}
	}

	/**
	 * 是否历史记录
	 */
	protected boolean isHistory() {
		return false;
	}

	/**
	 * Description: 绘制网址导航 Author: guojy Date: 2013-3-27 上午11:47:00
	 */
	private void drawNavigationCells(final Canvas canvas) {
		float startX = paddingLeft;
		float endX = width - paddingRight;
		int totalSize = cellList.size();
		for (int i = 0; i < cellList.size(); i++) {
			NavigationCell cell = cellList.get(i);
			// 画分割线
			if (cell.isCategory()) {// 分类
				final Rect bound = cell.getBound();
				linePaint.setColor(Color.parseColor("#e0e0e0"));
				canvas.drawLine(bound.right, bound.top, bound.right, bound.bottom, linePaint);

				if (!cell.showAll) {// 画横分割线
					float y = bound.bottom;
					drawCategorySep(canvas, startX, endX, y);
				} else {
					drawThinSep(canvas, cell.getBound().left, cell.getBound().right, cell.getBound().bottom);
				}
			} else if (cell.isCategoryExpand()) {// 分类扩展icon

			} else {// 非分类
				if (i + 1 < totalSize && cellList.get(i + 1).isCategory() && cell.showAll) {// 画横分割线,
																							// 去掉count
																							// >
																							// 4
																							// &&，海外版有小于4个网址的
					float y = cell.getBound().bottom;
					drawCategorySep(canvas, startX, endX, y);
				} else {
					drawThinSep(canvas, cell.getBound().left, cell.getBound().right, cell.getBound().bottom);
				}
			}

			// 画内容
			cell.draw(canvas);
		}
	}

	private void drawThinSep(Canvas canvas, float startX, float endX, float y) {
		linePaint.setColor(Color.parseColor("#19000000"));
		linePaint.setStrokeWidth(1);
		canvas.drawLine(startX, y, endX, y, linePaint);
	}

	private void drawCategorySep(Canvas canvas, float startX, float endX, float y) {
		if (mCategoryCellOccupyRow <= 0) {
			return;
		}

		if (isAddSiteActivity) {
			linePaint.setColor(Color.parseColor("#d7d7d7"));
			canvas.drawLine(startX, y, endX, y, linePaint);
		} else {
			linePaint.setColor(Color.parseColor("#19000000"));
			linePaint.setStrokeWidth(1);
			canvas.drawLine(startX, y, endX, y, linePaint);
			linePaint.setColor(Color.parseColor("#26ffffff"));
			linePaint.setStrokeWidth(2);
			canvas.drawLine(startX, y + 1, endX, y + 1, linePaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			mTouchDownY = event.getRawY();
			break;
		}

		if (mOnCellTouchListener != null) {
			for (NavigationCell cell : cellList) {
				if (cell.getBound().contains((int) event.getX(), (int) event.getY())) {
					mOnCellTouchListener.onTouch(cell, event);
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						// 按下去的时候，记下按下的单元格
						originCell = cell;
					}
					if (originCell != null && cell.equals(originCell) && event.getAction() != MotionEvent.ACTION_UP && event.getAction() != MotionEvent.ACTION_CANCEL) {
						// 如果手指在单元格内操作，则代表单元格被选中，刷新view
						cell.setSelected(true);
						invalidate();
					}
					break;// 退出遍历
				}
			}
			if (originCell != null && (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)) {
				// 手指拿起来且之前点击过的时候，刷新view
				originCell.setSelected(false);
				originCell = null;
				invalidate();
			}
		}
		return true;
	}

	/**
	 * 导航分类项目
	 * 
	 * @author linxin
	 * @Date 2012-11-26
	 */
	static class NavigationCategory {
		List<NavigationCategoryItem> items;
		String title;// 分类的标题
		int categoryIconRes = 0;// 分类的图标
		boolean showAll;// 是否显示分类下所有网址
		Bitmap icon;
		String iconUrl;

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (this == o) {
				return true;
			}
			if (o instanceof NavigationCategory) {
				NavigationCategory other = (NavigationCategory) o;
				return other.title.equals(this.title);
			}
			return false;
		}
	}

	static class NavigationCategoryExpand {
		NavigationCategory category;
	}

	/**
	 * 导航分类子项
	 * 
	 * @author linxin
	 * @Date 2012-11-26
	 */
	static class NavigationCategoryItem {
		String name;// 名称
		String url;// 链接
		String color;// 颜色
		boolean bold;// 加粗
		boolean isRed;// 是否标红
		String siteId = null; // 用于导航流量统计的id

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (this == o) {
				return true;
			}
			if (o instanceof NavigationCategoryItem) {
				NavigationCategoryItem item = (NavigationCategoryItem) o;
				return item.name.equals(name) && item.url.equals(url);
			}
			return false;
		}
	}

	/**
	 * 子项点击监听
	 * 
	 * @author linxin
	 * @Date 2012-11-28
	 */
	public interface OnCellTouchListener {
		public void onTouch(NavigationCell cell, MotionEvent event);
	}

	/**
	 * 设置点击监听器
	 * 
	 * @param mOnCellTouchListener
	 *            the mOnCellTouchListener to set
	 */
	public void setOnItemTouchListener(OnCellTouchListener mOnCellTouchListener) {
		this.mOnCellTouchListener = mOnCellTouchListener;
	}

	private class NavigationCell {
		/**
		 * 分类导航的标题颜色
		 */
		private final static String COLOR_CATEGORY_NAME = "#676B6E";
		private final static String COLOR_SITE_NAME = "#4f4f4f";
		private final static String COLOR_SITE_NAME_RED = "#EF4939";
		private final static String COLOR_BLACK = "#000000";
		/**
		 * 内容选中的颜色
		 */
		private final static String COLOR_ITEM_SELECTED = "#00BCD5";
		private Rect mBound = null;
		private Paint mPaint = PaintUtils2.getPaintAssemblyTypeface(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		private int dy;
		private Object object;
		private String text;
		private int textLength;// 能显示的字的长度
		private int textWidth;// 字所占用的宽度
		private boolean isSelected;
		private String desc;
		private String color;

		private int categoryPadding;

		private boolean showAll;
		private boolean hasMore;

		/**
		 * 用于分类标题
		 * 
		 * @param category
		 * @param rect
		 * @param textSize
		 */
		public NavigationCell(NavigationCategory category, Rect rect, float textSize, boolean hasMore) {
			this.object = category;
			this.text = category.title;
			this.mBound = rect;
			this.color = COLOR_CATEGORY_NAME;
			this.showAll = category.showAll;
			this.hasMore = hasMore;
			init(textSize);
			categoryPadding = ScreenUtil.dip2px(mContext, 7);
		}

		public NavigationCell(NavigationCategoryExpand categoryEx, Rect rect) {
			this.object = categoryEx;
			this.mBound = rect;
		}

		/**
		 * 用于分类子项
		 * 
		 * @param item
		 * @param rect
		 * @param textSize
		 */
		public NavigationCell(NavigationCategoryItem item, Rect rect, float textSize, boolean isRed, boolean showAll) {
			this.object = item;
			this.text = item.name;
			this.color = isRed ? COLOR_SITE_NAME_RED : COLOR_SITE_NAME;
			if (isAddSiteActivity)// 当前是否添加网址导航页面
				this.color = COLOR_BLACK;
			this.mPaint.setFakeBoldText(item.bold);
			this.mBound = rect;
			this.showAll = showAll;
			init(textSize);
		}

		/**
		 * 初始化公共的部分
		 * 
		 * @param textSize
		 */
		private void init(float textSize) {
			this.mPaint.setTextSize(textSize);
			this.dy = (int) Math.abs((mPaint.ascent() + mPaint.descent()) / 2);
			this.textLength = mPaint.breakText(text, true, mBound.width() - 5, null);
			this.textWidth = (int) mPaint.measureText(text.substring(0, textLength));
		}

		protected void draw(Canvas canvas) {
			if (isCategoryExpand()) {
				// NavigationCategory cat =
				// ((NavigationCategoryExpand)object).category;
				// int id = cat.showAll ?
				// R.drawable.launcher_navigation_category_show_all :
				// R.drawable.launcher_navigation_category_show_more;
				// Bitmap bmp =
				// ((BitmapDrawable)mContext.getResources().getDrawable(id)).getBitmap();
				// canvas.drawBitmap(bmp, mBound.centerX() - bmp.getWidth()/2,
				// mBound.centerY() - bmp.getHeight()/2, null);
			} else if (isSelected && !isCategory()) {// 选中态
				mPaint.setColor(Color.parseColor(COLOR_ITEM_SELECTED));

				int left = mBound.left + 10;
				if (textWidth < siteWidth - 10) {
					left = mBound.left + (siteWidth - textWidth) / 2;
				}
				final int startX = left;
				final int Y = mBound.centerY() + dy + 5;
				canvas.drawLine(startX, Y, startX + textWidth, Y, mPaint);
				canvas.drawText(text, 0, textLength, left, mBound.centerY() + dy, mPaint);
			} else {
				if (isCategory()) {// 分类
					if (mCategoryCellOccupyRow > 0) {
						// 绘制背景
						Drawable bg = hasMore ? mCategoryMoreBg : mCategoryBg;
						if (bg != null) {
							bg.setBounds(mBound);
							bg.draw(canvas);
						}

						int left = 0;
						int top = 0;
						int iconBottom = 0;
						int space = ScreenUtil.dip2px(getContext(), 5);
						Rect textBounds = new Rect();
						if (text != null) {
							mPaint.getTextBounds(text, 0, text.length(), textBounds);
						}

						// 绘制分类图标
						NavigationCategory category = ((NavigationCategory) (this.object));
						Drawable iconDrawable = null;
						if (category.icon != null) {
							iconDrawable = new BitmapDrawable(getResources(), category.icon);
						} else if (category.categoryIconRes > 0) {
							iconDrawable = getResources().getDrawable(category.categoryIconRes);
						}
						if (iconDrawable != null) {
							left = mBound.centerX() - iconDrawable.getIntrinsicWidth() / 2;
							top = mBound.centerY() - (iconDrawable.getIntrinsicHeight() + textBounds.height() + space) / 2;
							iconBottom = top + iconDrawable.getIntrinsicHeight();
							iconDrawable.setBounds(new Rect(left, top, left + iconDrawable.getIntrinsicWidth(), iconBottom));
							iconDrawable.draw(canvas);
						}

						// 绘制分类标题
						left = mBound.centerX() - textBounds.width() / 2;
						top = iconBottom > 0 ? (iconBottom + space + textBounds.height()) : (mBound.centerY() - textBounds.height() / 2 + textBounds.height());
						mPaint.setColor(Color.parseColor(color));
						canvas.drawText(text, left, top, mPaint);
					}
				} else {
					mPaint.setColor(Color.parseColor(color));
					int left = mBound.left + 10;
					if (textWidth < siteWidth - 10) {
						left = mBound.left + (siteWidth - textWidth) / 2;
					}
					canvas.drawText(text, 0, textLength, left, mBound.centerY() + dy, mPaint);
				}
			}
		}

		public Object getObject() {
			return this.object;
		}

		public boolean isCategory() {
			return (object instanceof NavigationCategory);
		}

		public boolean isCategoryExpand() {
			return (object instanceof NavigationCategoryExpand);
		}

		public Rect getBound() {
			return mBound;
		}

		public String getDesc() {
			return this.desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String toString() {
			return this.text + "(" + mBound.toString() + ")";
		}

		@Override
		public boolean equals(Object o) {
			if (mBound == null)
				return false;

			if (!(o instanceof NavigationCell))
				return false;

			final NavigationCell cell = (NavigationCell) o;
			if (cell.mBound == null)
				return false;

			final Rect bound = cell.mBound;
			return mBound.equals(bound);
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}

		public Paint getPaint() {
			return mPaint;
		}

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void refreshPaintAndView() {
		for (NavigationCell cell : cellList) {
			invalidate();
		}
	}
}
