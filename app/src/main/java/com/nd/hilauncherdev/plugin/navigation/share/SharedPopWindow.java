package com.nd.hilauncherdev.plugin.navigation.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.CommonLightbar;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.CommonSlidingView.OnCommonSlidingViewClickListener;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoader;
import com.nd.hilauncherdev.plugin.navigation.loader.CardDataLoaderFactory;
import com.nd.hilauncherdev.plugin.navigation.loader.LoaderClassMap;
import com.nd.hilauncherdev.plugin.navigation.loader.NaviCardLoader;
import com.nd.hilauncherdev.plugin.navigation.util.FileUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;
import com.nd.hilauncherdev.plugin.navigation.util.SystemUtil;
import com.nd.hilauncherdev.plugin.navigation.util.TelephoneUtil;
import com.nd.hilauncherdev.plugin.navigation.util.ThreadUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 分享弹出框<br/>
 *
 * @author chenzhihong_9101910
 */
public class SharedPopWindow {

	public static final String SHARE_IMG_NAME = "navi_share.png";
	public static final String SHARE_IMG_PATH = NaviCardLoader.NAV_DIR + SHARE_IMG_NAME;
	public static final String SHARE_IMG_NAME_DX = "navi_share_dx.png";
	public static final String SHARE_IMG_PATH_DX = NaviCardLoader.NAV_DIR + SHARE_IMG_NAME_DX;


	
	private Activity activity;
	private Context context;
	private LayoutInflater inflater;
	private PopupWindow mPopupWindow;
	private Window mWindow;
	private String mContent = "";
	private String mSubject = "";
	private Uri mUri;
	private boolean isSpecialWX = false;
	private String url;
	private int thumbLen;

	public SharedPopWindow(Activity activity, Context context) {
		this.activity = activity;
		this.context = context;
		if (CommonLauncherControl.DX_PKG) {
			if (!new File(SHARE_IMG_PATH_DX).exists()) {
				FileUtil.copyPluginAssetsFile(context, SHARE_IMG_NAME_DX, NaviCardLoader.NAV_DIR, SHARE_IMG_NAME_DX);
			}
		}else {
			if (!new File(SHARE_IMG_PATH).exists()) {
				FileUtil.copyPluginAssetsFile(context, SHARE_IMG_NAME, NaviCardLoader.NAV_DIR, SHARE_IMG_NAME);
			}
		}

		inflater = LayoutInflater.from(context);
		mWindow = ((Activity) this.activity).getWindow();
		mPopupWindow = getSharedPopWindowRealy();
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setBgAlpha(1f);
			}
		});
	}

	public void showAtLocation(View parent, int gravity, int x, int y) {
		setBgAlpha(0.7f);
		mPopupWindow.showAtLocation(parent, gravity, x, y);
	}

	public void showAsDropDown(View anchor, int xoff, int yoff) {
		setBgAlpha(0.7f);
		mPopupWindow.showAsDropDown(anchor, xoff, yoff);
	}

	private void setBgAlpha(float alpha) {
		android.view.WindowManager.LayoutParams lp = mWindow.getAttributes();
		lp.alpha = alpha;
		mWindow.setAttributes(lp);
	}

	public void showAsDropDown(View anchor) {
		this.showAsDropDown(anchor, 0, 0);
	}

	/**
	 * 获取分享pop之前，必须调用一次setSharedContent，否则分享内容为空，
	 *
	 * @return
	 */
	public PopupWindow getSharedPopWindow() {
		if (null == mPopupWindow) {
			mPopupWindow = getSharedPopWindowRealy();
		}
		CommonSlidingView csv = getSharedView();
		csv.snapToScreen(0);
		return mPopupWindow;
	}

	public SharedPopWindow setSharedContent(String subject, String content) {
		return this.setSharedContent(subject, content, null);
	}

	public SharedPopWindow setSharedContent(String subject, String content, Uri picUri) {
		this.mSubject = subject;
		this.mContent = content;
		this.mUri = picUri;
		getSharedView().setOnItemClickListener(createListener());
		return this;
	}


	/**
	 * 分享 微信特殊处理
	 * @param subject
	 * @param content
	 * @param url
	 * @param mUri
	 * @param thumbLen 微信SDK分享，缩略图边长
	 * @return
	 */
	public SharedPopWindow setSharedContentSpecialWX(String subject, String content, String url ,Uri mUri , int thumbLen){
		this.isSpecialWX = true;
		this.mSubject = subject;
		this.mContent = content;
		this.url = url;
		this.mUri = mUri;
		this.thumbLen = thumbLen;
		getSharedView().setOnItemClickListener(createListener());
		return this;
	}

	private CommonSlidingView getSharedView() {
		RelativeLayout contentView = (RelativeLayout) mPopupWindow.getContentView();
		ViewGroup vg = (ViewGroup) contentView.findViewById(R.id.shared_container);
		CommonSlidingView csv = (CommonSlidingView) vg.getChildAt(0);
		return csv;
	}

	private PopupWindow getSharedPopWindowRealy() {
		View popView = inflater.inflate(R.layout.navi_shared_pop_main, null);

		ViewGroup container = (ViewGroup) popView.findViewById(R.id.shared_container);

		CommonLightbar lightbar = (CommonLightbar) popView.findViewById(R.id.shared_lightbar);
		lightbar.setNormalLighter(context.getResources().getDrawable(R.drawable.screen_choose_app_lightbar_normal));
		lightbar.setSelectedLighter(context.getResources().getDrawable(R.drawable.screen_choose_app_lightbar_selected));
		lightbar.setGap(ScreenUtil.dip2px(context, 3));

		CommonSlidingView sharedView = new SharedView(context);
		sharedView.setEndlessScrolling(false);
		sharedView.setSplitCommonLightbar(lightbar);

		// View mainView = popView.findViewById(R.id.shared_view);
		// mainView.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (null != mPopupWindow && mPopupWindow.isShowing()) {
		// mPopupWindow.dismiss();
		// }
		// }
		// });
		Button cancel = (Button) popView.findViewById(R.id.shared_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mPopupWindow && mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}
			}
		});
		sharedView.setOnItemClickListener(createListener());
		container.addView(sharedView);
		// container.setLayoutParams(new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
		// 420));

		PopupWindow popupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(context, 280));
		popupWindow.setBackgroundDrawable(new ColorDrawable(0x66000000));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(R.style.SharedAnimation);
		return popupWindow;
	}

	private OnCommonSlidingViewClickListener createListener() {
		return new OnCommonSlidingViewClickListener() {
			@Override
			public void onItemClick(View arg0, int positionInData, int arg2, int arg3, ICommonData data) {
				if(isSpecialWX){
					shareForEx(data,positionInData);
				}else {
					if (data == null || data.getDataList() == null || positionInData > data.getDataList().size() - 1 || data.getDataList().size() == 0) {
						return;
					}
					final SharedItem sharedItem = (SharedItem) data.getDataList().get(positionInData);

					if (sharedItem.isInstalled()) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
						intent.putExtra(Intent.EXTRA_TEXT, mContent);
						intent.putExtra("sms_body", mContent); // 适配短信
						intent.putExtra("Kdescription", mContent);// 适配微信
						if (null != mUri) {
							intent.setType("image/*");
							// Uri uri = Uri.parse("file://" +
							// Environment.getExternalStorageDirectory() +
							// "/PandaHome2/caches/91space/20141105_170734_681.jpg");
							intent.putExtra(Intent.EXTRA_STREAM, mUri);
						}
						intent.setComponent(new ComponentName(sharedItem.getPkg(), sharedItem.getClassName()));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					} else {
						// if ("".equals(sharedItem.getPkg())) {
						// ClipboardManager cm = (ClipboardManager)
						// context.getSystemService(Context.CLIPBOARD_SERVICE);
						// cm.setText(mContent);
						// Toast.makeText(context,
						// R.string.settings_home_apps_shared_copy_tip,
						// Toast.LENGTH_SHORT).show();
						// } else {
						Toast.makeText(context, String.format(context.getString(R.string.settings_home_apps_shared_not_install), sharedItem.getInstallName()), Toast.LENGTH_SHORT).show();
						// }
					}
				}

			}
		};
	}


	/**
	 * 拓展的分享策略（暂不开放-2015.11.15）
	 * @param data
	 * @param positionInData
	 */
	private void shareForEx(ICommonData data , int positionInData){
		if (data == null || data.getDataList() == null || positionInData > data.getDataList().size() - 1 || data.getDataList().size() == 0) {
			return;
		}
		//文件不存在时，使用桌面logo
		if (mUri == null || !FileUtil.isFileExits(mUri.getPath())) {
			mUri = Uri.parse("file://" + SharedPopWindow.SHARE_IMG_PATH);
		}

		final SharedItem sharedItem = (SharedItem) data.getDataList().get(positionInData);
		if (sharedItem.isInstalled()) {
			if (!doWXShare(sharedItem)) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, mSubject);
				intent.putExtra(Intent.EXTRA_TEXT, mContent + url);
				intent.putExtra("sms_body", mContent + url); // 适配短信
				intent.putExtra("Kdescription", mContent + url);// 适配微信
				//当为QQ时，用系统接口的方法分享图文时，会导致文字消失，固在此屏蔽图片
				if (null != mUri && !needScreenImage(sharedItem)) {
					intent.setType("image/*");
					// Uri uri = Uri.parse("file://" +
					// Environment.getExternalStorageDirectory() +
					// "/PandaHome2/caches/91space/20141105_170734_681.jpg");
					intent.putExtra(Intent.EXTRA_STREAM, mUri);
				}
				intent.setComponent(new ComponentName(sharedItem.getPkg(), sharedItem.getClassName()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} else {
			Toast.makeText(context, String.format(context.getString(R.string.settings_home_apps_shared_not_install), sharedItem.getInstallName()), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 是否需要屏蔽图片
	 * @param shareItem
	 * @return
	 */
	private boolean needScreenImage(SharedItem shareItem){
		String pkg = shareItem.getPkg();
		//QQ、微信好友、短信分享做图片资源屏蔽
		if("com.tencent.mobileqq".equals(pkg)){
			return true;
		}
		if("com.tencent.mm1".equals(shareItem.getId())  ){
			return true;
		}
		if("com.android.mms".equals(pkg)){
			return true;
		}
		return false;
	}


	/**
	 * 微信分享
	 * @param sharedItem
	 * @return
	 */
	private boolean doWXShare(SharedItem sharedItem){
		if (isSpecialWX && "com.tencent.mm".equals(sharedItem.getPkg())) {//微信分享
			Intent intent = new Intent("com.nd.android.pandahome2.wxapi.SendToWXActivity");
			intent.putExtra("content", mContent);
			if ("com.tencent.mm2".equals(sharedItem.getId())) { //朋友圈
				intent.putExtra("type", "2");
				intent.putExtra("title",mSubject);//分享到朋友圈，不显示Content，所以把内容放在标题
			} else if ("com.tencent.mm1".equals(sharedItem.getId())) {//好友
				intent.putExtra("type", "1");
				intent.putExtra("title", mSubject);
			}
			int len = thumbLen < 1 ? 110 : thumbLen;
			intent.putExtra("scaleH" , len);
			intent.putExtra("scaleW" , len);
			intent.putExtra("url", url);
			intent.putExtra("imagePath", mUri.getPath());
			SystemUtil.startActivitySafely(context, intent);
			return true;
		}
		return false;
	}


	public void dismiss() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
	}

	public boolean isShowing() {
		return null == mPopupWindow ? false : mPopupWindow.isShowing();
	}

	public static void loadSharedImg(final Context context) {
		ThreadUtil.executeMore(new Runnable() {
			@Override
			public void run() {

				HashMap<Integer, LoaderClassMap.LoaderClass> loaders = LoaderClassMap.getLoaderClassMap();
				if (loaders != null && loaders.entrySet() != null) {
					for (Map.Entry<Integer, LoaderClassMap.LoaderClass> entry : loaders.entrySet()) {
						try {
							CardDataLoader dataLoader = CardDataLoaderFactory.getInstance().getCardLoader(entry.getKey().intValue());
							if (dataLoader != null) {
								if (!FileUtil.isFileExits(dataLoader.getCardShareImagePath())) {
									FileUtil.downloadFileByURL(dataLoader.getCardShareImageUrl(), dataLoader.getCardShareImagePath());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		});
	}

	public void showcf(Activity activity, View view) {
		int y = 0;
		if (TelephoneUtil.checkDeviceHasNavigationBar(activity)) {
			y = TelephoneUtil.getNavigationBarHeight(activity);
		}

		showAtLocation(view, Gravity.BOTTOM, 0, y);
	}

}
