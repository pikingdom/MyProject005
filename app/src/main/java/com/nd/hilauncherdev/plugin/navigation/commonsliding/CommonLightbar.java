package com.nd.hilauncherdev.plugin.navigation.commonsliding;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 滑屏指示灯<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class CommonLightbar extends LinearLayout {
	
	private static final String TAG = "CommonLightbar";

	protected Drawable normal_lighter, selected_lighter;

    protected int items, lastPos;

    protected Context context;

    protected int mGap = 0;
	
	public CommonLightbar(Context context) {
		super(context);
		this.context = context;		
	}
	
	public CommonLightbar(Context context, AttributeSet attrs) {		
		super(context, attrs);
		this.context = context;
	}
	
	/**
	 * 刷新指示灯
	 * @param size 总页数
	 * @param current 当前页值
	 */
	public void refresh(int size, int current) {
		if (items == size) {
			return;
		}
		if (lastPos != -1) {
			ImageView imageView = ((ImageView)this.getChildAt(lastPos));
			if (imageView != null) {
				imageView.setImageDrawable(normal_lighter);
			}
		}
		if (items < size) {
			for (int i = items; i < size; i++) {
				ImageView iv = new ImageView(context);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				if (mGap > 0) {
					lp.setMargins(mGap/2, 0, mGap/2, 0);
				}
				iv.setLayoutParams(lp);
				iv.setImageDrawable(normal_lighter);
				this.addView(iv);
			}
		} else {
			this.removeViews(size, items - size); 
		}
		items = size;
		lastPos = -1;
		update(current);
		requestLayout();
	}
	
	/**
	 * 设置指示灯的间距
	 */
	public void setGap(int gap) {
		mGap = gap;
	}
	
	/**
	 * 更新指示灯图片(基于0计算)
	 * @param pos 需更新的指示灯的页值
	 */
	public void update(int pos) {
		if (pos < 0 || pos >= items) {
			Log.e(TAG, "pos out of range!!!");
			return;
		}
		if (pos == lastPos)
			return;
		
		((ImageView)this.getChildAt(pos)).setImageDrawable(selected_lighter);
		if (lastPos != -1) {
			((ImageView)this.getChildAt(lastPos)).setImageDrawable(normal_lighter);
		}
		lastPos = pos;
	}

	/**
	 * 设置普通指示灯图片
	 * @param normal_lighter
	 */
	public void setNormalLighter(Drawable normal_lighter) {
		this.normal_lighter = normal_lighter;
	}

	/**
	 * 设置当前页指示灯图片
	 * @param selected_lighter
	 */
	public void setSelectedLighter(Drawable selected_lighter) {
		this.selected_lighter = selected_lighter;
	}
}
