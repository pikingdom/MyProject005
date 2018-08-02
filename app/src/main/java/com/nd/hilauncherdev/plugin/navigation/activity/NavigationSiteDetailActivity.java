package com.nd.hilauncherdev.plugin.navigation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.widget.HeaderView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationDetailSiteView;
import com.nd.hilauncherdev.plugin.navigation.widget.NavigationFavoriteSiteView;

/**
 * 网址大全<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class NavigationSiteDetailActivity extends BaseActivity {

	private Context mContext;
	private NavigationDetailSiteView detailSiteView;
	private boolean needJump = true;
	/** 推荐ICON每行显示多少个 */
	private int iconInRow = 4;
	private String poUrlPath = Environment.getExternalStorageDirectory()+CommonGlobal.BASE_DIR_NAME+"/po/tagUrl";

	public static final String INTENT_TAG_COUNT = "icon_count";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent from = getIntent();
		String FROM  = from.getStringExtra("from");
		if(!TextUtils.isEmpty(FROM) && FROM.equals("tiezhi")){
			needJump = false;
		}
		iconInRow = from.getIntExtra(INTENT_TAG_COUNT,4);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigation_site_detail);
		HeaderView headView = (HeaderView) findViewById(R.id.head_view);
		headView.setTitle("网址大全");
		headView.setGoBackListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					if(FileUtil.isFileExits(poUrlPath))
						FileUtil.delFile(poUrlPath);
					finish();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

		initControls();
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == KeyEvent.KEYCODE_BACK){
			try{
				if(FileUtil.isFileExits(poUrlPath))
					FileUtil.delFile(poUrlPath);
				finish();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return  true;
	}

	private void initControls() {
		detailSiteView = (NavigationDetailSiteView) findViewById(R.id.navi_site_detail_view);
		detailSiteView.context = mContext;
		detailSiteView.initView(iconInRow,iconInRow*2);
		detailSiteView.initCallBack(new onNaviClickCallBack() {
			@Override
			public void onNaviSiteClicked(boolean isClicked,String url) {
				try{
				if(FileUtil.isFileExits(poUrlPath))
					FileUtil.delFile(poUrlPath);
				FileUtil.writeFile(poUrlPath,url,false);
				finish();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		},needJump);
		NavigationFavoriteSiteView rec = (NavigationFavoriteSiteView) findViewById(R.id.navigationFavoriteWebView);
		if(rec != null)
			rec.setFromSiteActivity(true);
	}



	public interface onNaviClickCallBack{
		public void onNaviSiteClicked(boolean isClicked,String url);
	}

}

