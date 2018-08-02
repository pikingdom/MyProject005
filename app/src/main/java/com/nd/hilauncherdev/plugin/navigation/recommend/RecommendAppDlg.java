//package com.nd.hilauncherdev.plugin.navigation.recommend;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.widget.TextView;
//
//import com.nd.hilauncherdev.plugin.navigation.R;
//import com.nd.hilauncherdev.plugin.navigation.http.DownloadHelper;
//import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
//import com.nd.hilauncherdev.plugin.navigation.util.AppAnalysisConstant;
//import com.nd.hilauncherdev.plugin.navigation.util.URLs;
//
//import static com.nd.hilauncherdev.plugin.navigation.R.layout.dlg_recommend_app;
//
//public class RecommendAppDlg extends Dialog {
//
//	public static final String APP_SAVE_NAME = "navi_app.apk";
//
//	private TextView downloadV;
//	private Context mContext;
//
//	public String pkgName = "";
//	public String appName = "今日头条";
//	//默认下载链接
//	public String appUrl = "http://s.toutiao.com/4P8/";
//	private Context baseContext;
//	private Context naviContext;
//	public RecommendAppDlg(Context base,Context context, Context navi,String pkgName) {
//		super(context);
//		mContext = context;
//		baseContext = base;
//		this.pkgName = pkgName;
//		naviContext = navi;
//		String url = URLs.getDownloadUrlFromPackageName(context, this.pkgName, null, AppAnalysisConstant.SP_NAVIGATION_NEWS_APP_DOWNLOAD);
//		if(!TextUtils.isEmpty(url))
//			appUrl = url;
//
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		initView();
//	}
//
//	private void initView() {
//		View v = LayoutInflater.from(baseContext).inflate(R.layout.common_dialog_layout_ex, null);
//		setContentView(v);
//		downloadV = (TextView) v.findViewById(R.id.app_download);
//
//		downloadV.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String id = DownloadHelper.DOWNLOAD_KEY_PRE + pkgName;
//				Intent intent = new Intent(mContext.getPackageName() + ".FORWARD_SERVICE");
//				intent.putExtra("identification", id);
//				intent.putExtra("fileType", 0);
//				intent.putExtra("downloadUrl", appUrl);
//				intent.putExtra("title", appName);
//				intent.putExtra("savedDir", NaviCardLoader.NAV_DIR);
//				intent.putExtra("savedName", APP_SAVE_NAME);
//				intent.putExtra("sp",AppAnalysisConstant.SP_NAVIGATION_NEWS_APP_DOWNLOAD);
//				intent.putExtra("disId",pkgName);
//				naviContext.startService(intent);
//				RecommendAppDlg.this.dismiss();
//			}
//		});
//		findViewById(R.id.dlg_close).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				RecommendAppDlg.this.dismiss();
//			}
//		});
//	}
//}
