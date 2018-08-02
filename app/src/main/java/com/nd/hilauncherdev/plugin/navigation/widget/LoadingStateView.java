package com.nd.hilauncherdev.plugin.navigation.widget;


import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;


public class LoadingStateView extends RelativeLayout{
	
	private LinearLayout noDataLy;
	private ImageView nodate_img;
	private TextView nodate_desc;
	
	private LinearLayout netError;
	private Button netSettingBtn;
	private View refreshView;
	
	private LinearLayout loadingLy;
	private ProgressBar progress_small_title;
	
	public enum LoadingState{
		NetError,NoData,Loading,None
	}
	
	private OnRefreshListening mOnRefresh;
	
	public static interface OnRefreshListening{
		public void onRefresh();
	}
	

	public LoadingStateView(Context context) {
		this(context,null);
	}

	public LoadingStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		initView();
	}
	
	private void initView(){
		inflateLayout();
		noDataLy = (LinearLayout)findViewById(R.id.nodata);
		netError = (LinearLayout)findViewById(R.id.neterror_layout);
		loadingLy = (LinearLayout)findViewById(R.id.loading_layout);
		
		
		initNoData();
		initNetError();
	}
	public void inflateLayout(){
		inflate(getContext(), R.layout.theme_shop_v6_loadingview, this);
	}
	
	private void initNoData(){
		nodate_img = (ImageView)findViewById(R.id.nodate_img);
		nodate_desc = (TextView)findViewById(R.id.nodate_desc);
		progress_small_title=(ProgressBar) findViewById(R.id.progress_small_title);
		setNoDataInfo(R.drawable.theme_shop_v6_theme_nodata, getContext().getString(R.string.theme_shop_v2_theme_nodata_desc_feedback));
		progress_small_title.setBackgroundResource(CommonLauncherControl.getThemeLoadingBackgroad());
	}
	
	private void initNetError(){
		netSettingBtn = (Button) findViewById(R.id.framework_viewfactory_err_btn); 
		netSettingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getContext().startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(getContext(), R.string.frame_viewfacotry_show_netsetting_err, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		refreshView = (View)findViewById(R.id.framework_viewfactory_refresh_btn);
		refreshView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mOnRefresh != null){
					mOnRefresh.onRefresh();
				}
				
			}
		});
	}
	
	public void setNoDataInfo(int resImgId, String resDesc){
		try {
			nodate_img.setImageResource(resImgId);
			nodate_desc.setText(resDesc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setState(LoadingState state){
		switch(state){
		case None:
			noDataLy.setVisibility(View.GONE);
			netError.setVisibility(View.GONE);
			loadingLy.setVisibility(View.GONE);
			setVisibility(View.GONE);
			break;
		case Loading:
			noDataLy.setVisibility(View.GONE);
			netError.setVisibility(View.GONE);
			loadingLy.setVisibility(View.VISIBLE);
			setVisibility(View.VISIBLE);
			break;
		case NoData:
			noDataLy.setVisibility(View.VISIBLE);
			netError.setVisibility(View.GONE);
			loadingLy.setVisibility(View.GONE);
			setVisibility(View.VISIBLE);
			break;
		case NetError:
			noDataLy.setVisibility(View.GONE);
			netError.setVisibility(View.VISIBLE);
			loadingLy.setVisibility(View.GONE);
			setVisibility(View.VISIBLE);
			break;
		default:break;
		}
	}
	
	public void setOnRefreshListening(OnRefreshListening onRefresh){
		mOnRefresh = onRefresh;
	}
	

}
