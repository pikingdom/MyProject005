package com.nd.hilauncherdev.plugin.navigation.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.Global;
import com.nd.hilauncherdev.plugin.navigation.util.PaintUtils2;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

public class ListViewFooter {
	View mFooterView;
	public ListViewFooter(LayoutInflater imflater ){
		mFooterView = imflater.inflate(getLayoutId(), null);
		showFooterViewWaiting();
	}

	public ListViewFooter(LayoutInflater imflater,int id){
		mFooterView = imflater.inflate(id, null);
		showFooterViewWaiting();
	}

	public int getLayoutId(){
		return R.layout.market_footer_wait;
	}
	public  View getFooterView(){
		return mFooterView;
	}
	public void hideFooterView(){
		mFooterView.setVisibility(View.GONE);
	}

	public void showFooterView(){
		mFooterView.setVisibility(View.VISIBLE);
		int footerHeight = ScreenUtil.dip2px(getFooterView().getContext(), 46);
		mFooterView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));
//		mFooterView.findViewById(R.id.market_list_end).setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));
//		mFooterView.findViewById(R.id.market_wait).setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, footerHeight));

	}

	public void showFooterViewError(boolean isEmptyList, View.OnClickListener listener) {

		showFooterViewError(listener);
		final View view = getFooterView();
		if (isEmptyList) {
			Global.runInMainThread(new Runnable() {
				@Override
				public void run() {
					int location[] = new int[2];
					view.getLocationOnScreen(location);
					int emptyHeight = ScreenUtil.getCurrentScreenHeight(view.getContext()) - location[1];
					if(emptyHeight < ScreenUtil.getCurrentScreenHeight(view.getContext())){
						mFooterView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, emptyHeight));
					}
				}
			});

		}
	}


	public void showFooterViewWaiting(boolean isEmptyList){
		showFooterViewWaiting();
		final View view = getFooterView();
		if (isEmptyList) {
			Global.runInMainThread(new Runnable() {
				@Override
				public void run() {
					int location[] = new int[2];
					view.getLocationOnScreen(location);
					int emptyHeight = ScreenUtil.getCurrentScreenHeight(view.getContext()) - location[1];
					if(emptyHeight < ScreenUtil.getCurrentScreenHeight(view.getContext())){
						mFooterView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, emptyHeight));
					}
				}
			},100);

		}
	}

	public void showFooterViewWaiting(){
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.market_wait).setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.market_refresh).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_list_end).setVisibility(View.INVISIBLE);
	}
	public void showFooterViewError(OnClickListener listener){
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.market_wait).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_list_end).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_refresh).setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.market_refresh).setOnClickListener(listener);
	}
	public void showFooterViewError(String tip,OnClickListener listener){
		mFooterView.setVisibility(View.VISIBLE);
		
		mFooterView.findViewById(R.id.market_list_end).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_wait).setVisibility(View.INVISIBLE);
		TextView text = (TextView)mFooterView.findViewById(R.id.tv_market_refresh);
		text.setVisibility(View.VISIBLE);
		text.setOnClickListener(listener);
		text.setText(tip);
	}
	
	/**
	 * 滑动到列表底部的时候显示提示
	 */
	public void showFooterViewListEnd(){
		mFooterView.setVisibility(View.VISIBLE);
		mFooterView.findViewById(R.id.market_wait).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_refresh).setVisibility(View.INVISIBLE);
		mFooterView.findViewById(R.id.market_list_end).setVisibility(View.VISIBLE);
		
	}
	public void initFont(){

		TextView textView = (TextView) mFooterView.findViewById(R.id.market_textView1);
		TextView textView2 = (TextView) mFooterView.findViewById(R.id.market_textView1);
		if(textView != null){
			PaintUtils2.assemblyTypeface(textView.getPaint());
		}
		if(textView2 != null){
			PaintUtils2.assemblyTypeface(textView2.getPaint());
		}

	}

}
