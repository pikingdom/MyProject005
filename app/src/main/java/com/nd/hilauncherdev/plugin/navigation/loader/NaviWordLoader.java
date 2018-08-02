package com.nd.hilauncherdev.plugin.navigation.loader;

import android.content.Context;
import android.text.TextUtils;

import com.nd.hilauncherdev.plugin.navigation.CommonLauncherControl;
import com.nd.hilauncherdev.plugin.navigation.R;
import com.nd.hilauncherdev.plugin.navigation.bean.HotwordItemInfo;
import com.nd.hilauncherdev.plugin.navigation.util.CommonGlobal;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherBranchController;
import com.nd.hilauncherdev.plugin.navigation.util.LauncherCaller;

import java.util.ArrayList;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 热词数据加载<br/>
 *
 * @author chenzhihong_9101910
 *
 */
public class NaviWordLoader {

	public static int ONE_BATCH_SIZE = 6;

	public static String HOTWORD_ATTR_NAME = "name_";
	public static String HOTWORD_ATTR_DETAILURL = "detailUrl_";
	public static String HOTWORD_ATTR_COLOR = "color_";
	public static String HOTWORD_ATTR_RESID = "resId_";


	private static HotwordItemInfo[] BUILDIN_KEYWRODS;

	private static void initKw(Context context) {
		if (context.getPackageName().equals(CommonGlobal.BAIDU_LAUNCHER_PKG_NAME) || CommonLauncherControl.AZ_PKG || CommonLauncherControl.BD_PKG) {
			BUILDIN_KEYWRODS = new HotwordItemInfo[] {
					// 此处HotwordItemInfo.detailUrl 保存的是功能点的intent uri
					new HotwordItemInfo(
							context.getString(R.string.hw_default_launcher), // 设置默认桌面
							"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.myphone.common.PluginBridgeServicei;i.action=" + LauncherCaller.ACTION_DEFAULT_LAUNCHER + ";end",
							HotwordItemInfo.TYPE_LAUNCHER_FUNCTION), new HotwordItemInfo(context.getString(R.string.hw_search), // 本机搜索
							"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.launcher.navigation.SearchActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
					new HotwordItemInfo(context.getString(R.string.hw_personal), // 个性化
							"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.shop.shop3.ThemeShopV2MainActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
					new HotwordItemInfo(context.getString(R.string.hw_setting), // 系统设置
							"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.menu.SystemSettingsActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION)

			};
		} else {
			if (context.getPackageName().equals(CommonGlobal.DIANXIN_LAUNCHER_PKG_NAME)) {
				BUILDIN_KEYWRODS = new HotwordItemInfo[]{
						// 此处HotwordItemInfo.detailUrl 保存的是功能点的intent uri
						//应用管理 本地主题 本地壁纸 本地铃声
						new HotwordItemInfo(context.getString(R.string.hw_app), 		// 应用管理
								"#Intent;component=com.dianxinos.dxhome/com.nd.hilauncherdev.myphone.appmanager.AppUninstallActivity;end",
								HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_local_theme), 		// 本地主题
								"#Intent;component=com.dianxinos.dxhome/com.nd.android.pandahome2.manage.shop.ThemeShopMainActivity;B.isLocalTheme=true;end",
								HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_local_wallpaper), 		//  本地壁纸
								"#Intent;component=com.dianxinos.dxhome/com.baidu.dx.personalize.PersonalizeActivityGroup;i.PERSONALIZE_TAB=1;i.SECOND_TAB=3;end",
								HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_local_ring), 		// 本地铃声
								"#Intent;component=com.dianxinos.dxhome/com.baidu.dx.personalize.PersonalizeActivityGroup;i.PERSONALIZE_TAB=2;i.SECOND_TAB=3;end",
								HotwordItemInfo.TYPE_LAUNCHER_FUNCTION)
					};
			}else if(LauncherBranchController.isNavigationForCustomLauncher()){
                BUILDIN_KEYWRODS = new HotwordItemInfo[] {
                        new HotwordItemInfo(context.getString(R.string.hw_search), // 本机搜索
                                "#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.launcher.navigation.SearchActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
                        new HotwordItemInfo(context.getString(R.string.hw_personal), // 个性化
                                "#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.shop.shop3.ThemeShopV2MainActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
                        new HotwordItemInfo(context.getString(R.string.hw_setting), // 系统设置
                                "#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.menu.SystemSettingsActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION)

                };

            }else{

				BUILDIN_KEYWRODS = new HotwordItemInfo[] {
						// 此处HotwordItemInfo.detailUrl 保存的是功能点的intent uri
						new HotwordItemInfo(
								context.getString(R.string.hw_default_launcher), // 设置默认桌面
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.myphone.common.PluginBridgeServicei;i.action=" + LauncherCaller.ACTION_DEFAULT_LAUNCHER + ";end",
								HotwordItemInfo.TYPE_LAUNCHER_FUNCTION), new HotwordItemInfo(context.getString(R.string.hw_search), // 本机搜索
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.launcher.navigation.SearchActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_help), // 帮助手册
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.myphone.faq.FAQActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_personal), // 个性化
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.shop.shop3.ThemeShopV2MainActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_personal_center), // 个人中心
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.menu.personal.PersonalCenterActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION),
						new HotwordItemInfo(context.getString(R.string.hw_setting), // 系统设置
								"#Intent;component=" + CommonLauncherControl.PKG_NAME + "/com.nd.hilauncherdev.menu.SystemSettingsActivity;end", HotwordItemInfo.TYPE_LAUNCHER_FUNCTION)

				};
			}
		}
	}

	public static ArrayList<HotwordItemInfo> loadDefaultWordS(Context context) {
		if (BUILDIN_KEYWRODS == null) {
			initKw(context);
		}

		ArrayList<HotwordItemInfo> itemList = new ArrayList<>();
		for (HotwordItemInfo word : BUILDIN_KEYWRODS) {
			itemList.add(word);
		}

		return itemList;
	}


	/**
	 * 是否需要更新热词界面
	 * 当前无热词 或 热词不是默认数据时更新
	 *
	 * @return
	 */
	public static boolean isAllBuildInWord(List<HotwordItemInfo> items) {

		boolean allBuildInHotword = true;
		for (HotwordItemInfo itemInfo : items) {
			if (TextUtils.isEmpty(itemInfo.detailUrl)) {
				continue;
			}
			if (itemInfo.detailUrl.startsWith("http:") || itemInfo.detailUrl.startsWith("https:")) {
				allBuildInHotword = false;
				break;
			}
		}
		return allBuildInHotword;
	}



	public static ArrayList<HotwordItemInfo> convertHotwordList(List<Object> list) {

		ArrayList<HotwordItemInfo> itemList = convertHotwordListWithoutCheckSize(list);
        ArrayList<HotwordItemInfo> resultList = new ArrayList<HotwordItemInfo>();

        if(itemList == null || itemList.size() ==0){
			return itemList;
		}
        int total = itemList.size();
		for (int i = 0; i < total && (i % NaviWordLoader.ONE_BATCH_SIZE) + (total - i) >= NaviWordLoader.ONE_BATCH_SIZE; i++) {
            HotwordItemInfo hotwordItemInfo = itemList.get(i);
            if (!TextUtils.isEmpty(hotwordItemInfo.name) && !TextUtils.isEmpty(hotwordItemInfo.detailUrl)) {
                resultList.add(hotwordItemInfo);
            }
		}
        return resultList;
	}


	public static ArrayList<HotwordItemInfo> convertHotwordListWithoutCheckSize(List<Object> list) {

		ArrayList<HotwordItemInfo> itemList = new ArrayList<>();

		if(list == null){
			return itemList;
		}
		int total = list.size();
		for (int i = 0; i < total; i++) {
			Object obj = list.get(i);
			try {
				HotwordItemInfo hotwordItemInfo = new HotwordItemInfo();
				Field[] fieldS = obj.getClass().getDeclaredFields();
				for (Field field : fieldS) {
					Class fieldC = field.getType();
					if (fieldC != String.class) {
						continue;
					}
					String value = (String) field.get(obj);
					if (TextUtils.isEmpty(value))
						continue;
					if (value.startsWith(HOTWORD_ATTR_NAME)) {
						hotwordItemInfo.name = value.replace(HOTWORD_ATTR_NAME, "");
					} else if (value.startsWith(HOTWORD_ATTR_DETAILURL)) {
						hotwordItemInfo.detailUrl = value.replace(HOTWORD_ATTR_DETAILURL, "");
					} else if (value.startsWith(HOTWORD_ATTR_COLOR)) {
						int color = 0xff333333;
						try {
							color = Integer.parseInt(value.replace(HOTWORD_ATTR_COLOR, ""));
						} catch (Exception e) {
							e.printStackTrace();
						}
						hotwordItemInfo.color = color;
					} else if (value.startsWith(HOTWORD_ATTR_RESID)) {
						int resId = 0;
						try {
							resId = Integer.parseInt(value.replace(HOTWORD_ATTR_RESID, ""));
						} catch (Exception e) {
							e.printStackTrace();
						}
						hotwordItemInfo.resId = resId;
					}

				}

				if (!TextUtils.isEmpty(hotwordItemInfo.name) && !TextUtils.isEmpty(hotwordItemInfo.detailUrl)) {
					itemList.add(hotwordItemInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return itemList;

	}

}
