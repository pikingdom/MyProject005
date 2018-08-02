package com.nd.hilauncherdev.plugin.navigation.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.CommonSlidingView;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.CommonSlidingViewData;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.ICommonData;
import com.nd.hilauncherdev.plugin.navigation.commonsliding.datamodel.ICommonDataItem;
import com.nd.hilauncherdev.plugin.navigation.util.ScreenUtil;

/**
 * 分享选择界面<br/>
 * 
 * @author chenzhihong_9101910
 * 
 */
public class SharedView extends CommonSlidingView {

	private static final String COM_ANDROID_EMAIL_CLASS = "com.android.email.activity.MessageCompose";
	private static final String COM_ANDROID_EMAIL = "com.android.email";
	// private static final String COM_ANDROID_MMS_CLASS =
	// "com.android.mms.ui.ComposeMessageActivity";
	// private static final String COM_ANDROID_MMS = "com.android.mms";
	private static final String COM_TENCENT_MM_CLASS2 = "com.tencent.mm.ui.tools.ShareToTimeLineUI";
	private static final String COM_TENCENT_MM_CLASS = "com.tencent.mm.ui.tools.ShareImgUI";
	private static final String COM_TENCENT_MM = "com.tencent.mm";
	private static final String COM_QZONE_CLASS = "com.qzone.ui.operation.QZonePublishMoodActivity";
	private static final String COM_QZONE_CLASS1 = "com.qzonex.module.operation.ui.QZonePublishMoodActivity";
	private static final String COM_QZONE = "com.qzone";
	private static final String COM_QZONE1 = "com.qzone";
	private static final String COM_TENCENT_MOBILEQQ_CLASS = "com.tencent.mobileqq.activity.JumpActivity";
	private static final String COM_TENCENT_MOBILEQQ2 = "com.tencent.qqlite";
	private static final String COM_TENCENT_MOBILEQQ = "com.tencent.mobileqq";
	private static final String COM_SINA_WEIBO_CLASS3 = "com.sina.weibo.composerinde.ComposerDispatchActivity";
	private static final String COM_SINA_WEIBO_CLASS2 = "com.sina.weibog3.EditActivity";
	private static final String COM_SINA_WEIBO_CLASS = "com.sina.weibo.ComposerDispatchActivity";
	private static final String COM_SINA_WEIBO3 = "com.sina.weibo";
	private static final String COM_SINA_WEIBO2 = "com.sina.weibog3";
	private static final String COM_SINA_WEIBO = "com.sina.weibo";
	private Context mContext;
	private PackageManager mPackageManager;
	private CommonSlidingViewData data;
	private List<ICommonDataItem> commonDataItems = new ArrayList<ICommonDataItem>();
	private LayoutInflater layoutInflater;
	private List<SharedItem> mSharedDataList = new ArrayList<SharedItem>();
	private Map<String, SharedItem> mSharedDefaultMap = new HashMap<String, SharedItem>();
	private int iconSize;
	private static final int launcher_edit_cell_col = 3;
	private static final int launcher_edit_cell_row = 2;
	private LinearLayout.LayoutParams mLayoutParams;
	private static int SHARED_APP_ICON_SIZE = 60;

	public SharedView(Context context) {
		super(context);
		init(context);
	}

	public SharedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SharedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		mPackageManager = context.getPackageManager();
		SHARED_APP_ICON_SIZE = ScreenUtil.dip2px(mContext, 48);
		mLayoutParams = new LinearLayout.LayoutParams(SHARED_APP_ICON_SIZE, SHARED_APP_ICON_SIZE);
		mLayoutParams.gravity = Gravity.CENTER;
		initDefaultView();
		showShareDataView();
	}

	private void initDefaultView() {
		List<ICommonData> list = new ArrayList<ICommonData>();
		this.setList(list);
		iconSize = getResources().getDimensionPixelSize(R.dimen.app_icon_size);
		data = new CommonSlidingViewData(iconSize, iconSize, launcher_edit_cell_col, launcher_edit_cell_row, commonDataItems);
	}

	private ICommonData assembleSharedData() {
		data.setChildViewHeight((int) (2 * iconSize));
		data.setChildViewWidth((int) (2 * iconSize));
		data.setColumnNum(launcher_edit_cell_col);
		data.setRowNum(launcher_edit_cell_row);
		data.getDataList().clear();

		initEightLegends();
		initOtherApp();

		data.getDataList().addAll(mSharedDataList);
		return data;
	}

	private void initOtherApp() {
		// 新浪微博com.sina.weibo/com.sina.weibo.ComposerDispatchActivity
		// qq好友com.tencent.mobileqq/com.tencent.mobileqq.activity.JumpActivity--/com.tencent.mobileqq.activity.qfileJumpActivity
		// qq空间com.qzone/com.qzone.ui.operation.QZonePublishMoodActivity
		// 微信com.tencent.mm/com.tencent.mm.ui.tools.ShareImgUI--/com.tencent.mm.ui.tools.ShareToTimeLineUI
		// 、复制链接、邮件分享、
		// 短信com.android.mms/com.android.mms.ui.ComposeMessageActivity

		SharedItem si = null;
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.setType("image/*");

		List<ResolveInfo> a = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo resolveInfo : a) {

			String packageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;
			String key = packageName + "/" + className;
			if (!mSharedDefaultMap.containsKey(key)) {
				si = new SharedItem();
				si.setId(packageName);
				si.setPkg(packageName);
				si.setDesc(resolveInfo.loadLabel(mPackageManager).toString());
				si.setIcon(resolveInfo.activityInfo.loadIcon(mPackageManager));
				si.setClassName(className);
				si.setInstalled(true);
				mSharedDataList.add(si);
			} else {
				/**
				 * 适配一个应用多个包名的情况 找到系统安装的包名替换默认放的包名
				 */
				String id = mSharedDefaultMap.get(key).getId();
				int pos = -1;
				SharedItem tmp = null;
				for (int i = 0; i < mSharedDataList.size(); i++) {
					tmp = mSharedDataList.get(i);
					if (tmp.getId().equals(id)) {
						pos = i;
						break;
					}
				}
				if (pos != -1) {
					tmp = mSharedDataList.remove(pos);
					tmp.setPkg(packageName);
					tmp.setClassName(className);
					tmp.setDesc(resolveInfo.loadLabel(mPackageManager).toString());
					// si.setIcon(resolveInfo.activityInfo.loadIcon(mPackageManager));
					tmp.setInstalled(true);
					mSharedDataList.add(pos, tmp);
				}
			}
		}
	}

	private void initEightLegends() {
		initSharedApp(COM_TENCENT_MM + "1", COM_TENCENT_MM, COM_TENCENT_MM_CLASS, R.string.settings_home_apps_shared_wx_name, R.string.settings_home_apps_shared_wx_friend,
				R.drawable.shared_icon_wechat, true);
		initSharedApp(COM_SINA_WEIBO, COM_SINA_WEIBO, COM_SINA_WEIBO_CLASS, R.string.settings_home_apps_shared_sina_weibo, R.string.settings_home_apps_shared_sina_weibo, R.drawable.shared_icon_weibo,
				true);
		initSharedApp(COM_SINA_WEIBO, COM_SINA_WEIBO2, COM_SINA_WEIBO_CLASS2, R.string.settings_home_apps_shared_sina_weibo, R.string.settings_home_apps_shared_sina_weibo,
				R.drawable.shared_icon_weibo, false);
		initSharedApp(COM_SINA_WEIBO, COM_SINA_WEIBO3, COM_SINA_WEIBO_CLASS3, R.string.settings_home_apps_shared_sina_weibo, R.string.settings_home_apps_shared_sina_weibo,
				R.drawable.shared_icon_weibo, false);
		initSharedApp(COM_TENCENT_MOBILEQQ, COM_TENCENT_MOBILEQQ, COM_TENCENT_MOBILEQQ_CLASS, R.string.settings_home_apps_shared_mobile_qq_name, R.string.settings_home_apps_shared_mobile_qq,
				R.drawable.shared_icon_qq, true);
		initSharedApp(COM_TENCENT_MOBILEQQ, COM_TENCENT_MOBILEQQ2, COM_TENCENT_MOBILEQQ_CLASS, R.string.settings_home_apps_shared_mobile_qq_name, R.string.settings_home_apps_shared_mobile_qq,
				R.drawable.shared_icon_qq, false);
		initSharedApp(COM_QZONE, COM_QZONE, COM_QZONE_CLASS, R.string.settings_home_apps_shared_qzone, R.string.settings_home_apps_shared_qzone, R.drawable.shared_icon_qzone, true);
		initSharedApp(COM_QZONE, COM_QZONE1, COM_QZONE_CLASS1, R.string.settings_home_apps_shared_qzone, R.string.settings_home_apps_shared_qzone, R.drawable.shared_icon_qzone, false);

		initSharedApp(COM_TENCENT_MM + "2", COM_TENCENT_MM, COM_TENCENT_MM_CLASS2, R.string.settings_home_apps_shared_wx_name, R.string.settings_home_apps_shared_wx_friends,
				R.drawable.shared_icon_networking, true);
		// initSharedApp("copy", "", "Shared_Copy_Url",
		// R.string.settings_home_apps_shared_copy,
		// R.string.settings_home_apps_shared_copy,
		// R.drawable.shared_icon_copylink, true);
		// initSharedApp(COM_ANDROID_MMS, COM_ANDROID_MMS_CLASS,
		// R.string.settings_home_apps_shared_sms,
		// R.string.settings_home_apps_shared_sms, R.drawable.shared_icon_sms);
		initSharedApp(COM_ANDROID_EMAIL, COM_ANDROID_EMAIL, COM_ANDROID_EMAIL_CLASS, R.string.settings_home_apps_shared_email, R.string.settings_home_apps_shared_email, R.drawable.shared_icon_email,
				true);
	}

	private void initSharedApp(String id, String pkg, String clazz, int installNameId, int descId, int iconId, boolean addToDefault) {
		SharedItem sharedItem = new SharedItem();
		sharedItem.setId(id);
		sharedItem.setPkg(pkg);
		sharedItem.setDesc(mContext.getString(descId));
		sharedItem.setInstallName(mContext.getString(installNameId));
		sharedItem.setClassName(clazz);
		sharedItem.setIcon(mContext.getResources().getDrawable(iconId));
		mSharedDefaultMap.put(pkg + "/" + clazz, sharedItem);
		if (addToDefault) {
			mSharedDataList.add(sharedItem);
		}
	}

	private void showShareDataView() {
		this.getList().clear();
		ICommonData data = assembleSharedData();
		this.getList().add(data);
	}

	@Override
	protected void initSelf(Context context) {
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View onGetItemView(ICommonData data, int position) {
		View v = layoutInflater.inflate(R.layout.navi_shared_pop_item, null);
		final SharedItem sharedItem = (SharedItem) data.getDataList().get(position);
		TextView itemTxt = (TextView) v.findViewById(R.id.shared_item_txt);
		ImageView itemIcon = (ImageView) v.findViewById(R.id.shared_item_icon);
		itemIcon.setLayoutParams(mLayoutParams);
		itemTxt.setText(sharedItem.getDesc());
		itemIcon.setImageDrawable(sharedItem.getIcon());
		return v;
	}
}